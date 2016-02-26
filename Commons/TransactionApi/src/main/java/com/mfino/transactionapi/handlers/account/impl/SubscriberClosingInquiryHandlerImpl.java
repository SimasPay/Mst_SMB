/**
 * 
 */
package com.mfino.transactionapi.handlers.account.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.SMSValues;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberClosingInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.SubscriberClosingInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.XMLError;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

/**
 * @author Sunil
 *
 */
@Service("SubscriberClosingInquiryHandlerImpl")
public class SubscriberClosingInquiryHandlerImpl  extends FIXMessageHandler implements SubscriberClosingInquiryHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	/* (non-Javadoc)
	 * @see com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidation#handle(com.mfino.transactionapi.vo.TransactionDetails)
	 */
	@Override
	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("Handling subscriber services Registration webapi request");
		SubscriberAccountClosingXMLResult result = new SubscriberAccountClosingXMLResult();
		
		TransactionsLog transactionsLog = null;
		ServiceChargeTransactionLog sctl = null;
		
		ChannelCode channelCode = transactionDetails.getCc();
		
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		
		if(!mfaService.isMFATransaction(ServiceAndTransactionConstants.SERVICE_AGENT, ServiceAndTransactionConstants.TRANSACTION_CLOSE_ACCOUNT, channelCode.getID()) == true) {
			
			XMLResult xmlResult = null;
			
			xmlResult = new XMLError();
			xmlResult.setLanguage(language);
			xmlResult.setTransactionTime(new Timestamp());
			xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_TransactionNotAvailable);
			
			return xmlResult;
			
		}
		
		CMJSSubscriberClosingInquiry subscriberClosing = new CMJSSubscriberClosingInquiry();
		subscriberClosing.setAgentMDN(transactionDetails.getSourceMDN());
		subscriberClosing.setDestMDN(transactionDetails.getDestMDN());
		
		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSSubscriberClosingInquiry,subscriberClosing.DumpFields());
		
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		SubscriberMDN agentMDN = null;
		SubscriberMDN subMDN = subscriberMdnService.getByMDN(subscriberClosing.getDestMDN());
		
		if(!transactionDetails.isSystemIntiatedTransaction()) {
		
			agentMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
			result.setCompany(agentMDN.getSubscriber().getCompany());
			
			Integer validationResult = transactionApiValidationService.validateAgentMDN(agentMDN);
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
				result.setNotificationCode(validationResult);
				return result;
			}
			
			validationResult = transactionApiValidationService.validatePin(agentMDN, transactionDetails.getSourcePIN());
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
				result.setNotificationCode(validationResult);
				result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT)-agentMDN.getWrongPINCount());
				return result;
			}
		} else {
			
			addCompanyANDLanguageToResult(subMDN, result);
			
		}
		
		if (subMDN != null) {
			
			Pocket destPocket = null;			
			destPocket = pocketService.getDefaultPocket(subMDN, String.valueOf(CmFinoFIX.PocketType_LakuPandai));
			
			if(null != destPocket) {
			
				if(destPocket.getCurrentBalance().compareTo(BigDecimal.valueOf(systemParametersService.getInteger(SystemParameterKeys.MAXIMUM_SUBSCRIBER_CLOSING_AMOUNT))) == -1) {
					
					if(CmFinoFIX.SubscriberStatus_Active.equals(subMDN.getSubscriber().getStatus())) {
					
						ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
						query.setSourceMdn(subMDN.getMDN());
						query.setStatus(CmFinoFIX.SCTLStatus_Pending);
						
						List<ServiceChargeTransactionLog> sctlData = sctlService.getSubscriberPendingTransactions(query);
						
						if(null == sctlData || (null != sctlData && sctlData.size() == 0)) {
						
							result.setName(subMDN.getSubscriber().getFirstName());
							result.setCode(String.valueOf(CmFinoFIX.NotificationCode_SubscriberClosingInquirySuccess));
							result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingInquirySuccess);
							result.setMessage("Subscriber Closing Inquiry successfull");
							result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
							
							Transaction transaction = null;
							ServiceCharge serviceCharge = new ServiceCharge();
							
							if(null != agentMDN) {
							
								serviceCharge.setSourceMDN(agentMDN.getMDN());
							}
							
							serviceCharge.setDestMDN(subMDN.getMDN());
							serviceCharge.setChannelCodeId(channelCode.getID());
							serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
							serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CLOSE_ACCOUNT);
							serviceCharge.setTransactionAmount(BigDecimal.ZERO);
							serviceCharge.setTransactionLogId(transactionsLog.getID());
							serviceCharge.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

							try{
								
								transaction = transactionChargingService.getCharge(serviceCharge);

							}catch (InvalidServiceException e) {
								log.error("Exception occured in getting charges",e);
								result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
				 				return result;
							
							} catch (InvalidChargeDefinitionException e) {
								log.error(e.getMessage());
								result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
				 				return result;
							}
							
							sctl = transaction.getServiceChargeTransactionLog();
							
							result.setSctlID(sctl.getID());
							result.setMfaMode("OTP");
							
							if(!transactionDetails.isSystemIntiatedTransaction()) {
							
								mfaService.handleMFATransaction(sctl.getID(), agentMDN.getMDN());
							}
							
							sendOTPSMS(subMDN);
							
							log.debug("SMS for OTP has been sent....");
							
						} else {
							
							result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
							result.setCode(String.valueOf(CmFinoFIX.NotificationCode_SubscriberClosingInquiryFailed));
							result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberClosingInquiryFailed);
							
							log.debug("Subscriber has pending transactions....");
						}
						
					} else {
						
						result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
						result.setCode(String.valueOf(CmFinoFIX.NotificationCode_MDNIsNotActive));
						result.setNotificationCode(CmFinoFIX.NotificationCode_MDNIsNotActive);
						
						log.debug("Subscriber is not active....");
					}
					
				} else {
					
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
					result.setCode(String.valueOf(CmFinoFIX.NotificationCode_SubscriberHasAccountBalance));
					result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberHasAccountBalance);
					
					log.debug("Subscriber balance is > 100....");
				}
			}			
		} else {
		
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			
			log.debug("Subscriber not found....");
		}		
		
		return result;
	}
	
	private void sendOTPSMS (SubscriberMDN subscriberMDN ) {
		
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		Subscriber subscriber = subscriberMDN.getSubscriber();
		
		Integer OTPLength = systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), oneTimePin);
		subscriberMDN.setOTP(digestPin1);
		subscriberMDN.setOTPExpirationTime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
		subscriberMDNDAO.save(subscriberMDN);
		
		log.info("oneTimePin:" + oneTimePin);
		
		NotificationWrapper smsNotificationWrapper = subscriberServiceExtended.generateOTPMessage(oneTimePin, CmFinoFIX.NotificationMethod_SMS);
		smsNotificationWrapper.setDestMDN(subscriberMDN.getMDN());
		smsNotificationWrapper.setLanguage(subscriber.getLanguage());
		smsNotificationWrapper.setFirstName(subscriber.getFirstName());
    	smsNotificationWrapper.setLastName(subscriber.getLastName());
		
    	String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper,true);
		String mdn2 = subscriberMDN.getMDN();
		
		SMSValues smsValues= new SMSValues();
		smsValues.setDestinationMDN(mdn2);
		smsValues.setMessage(smsMessage);
		smsValues.setNotificationCode(smsNotificationWrapper.getCode());
		
		smsService.asyncSendSMS(smsValues);
	}
}