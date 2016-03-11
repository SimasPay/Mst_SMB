/**
 * 
 */
package com.mfino.transactionapi.handlers.agent.impl;

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
import com.mfino.fix.CmFinoFIX.CMJSAgentClosingInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
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
import com.mfino.transactionapi.handlers.agent.AgentClosingInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

/**
 * @author Sunil
 *
 */
@Service("AgentClosingInquiryHandlerImpl")
public class AgentClosingInquiryHandlerImpl  extends FIXMessageHandler implements AgentClosingInquiryHandler {

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
		
		log.info("Handling Agent Closing webapi request");
		SubscriberAccountClosingXMLResult result = new SubscriberAccountClosingXMLResult();
		
		TransactionsLog transactionsLog = null;
		ServiceChargeTransactionLog sctl = null;
		
		ChannelCode channelCode = transactionDetails.getCc();
		CMJSAgentClosingInquiry agentClosing = new CMJSAgentClosingInquiry();
		agentClosing.setDestMDN(transactionDetails.getDestMDN());
		
		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSAgentClosingInquiry,agentClosing.DumpFields());
		
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		SubscriberMDN subMDN = subscriberMdnService.getByMDN(agentClosing.getDestMDN());
		
		Integer validationResult = transactionApiValidationService.validateSubscriberAsDestination(subMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			return result;
		}
		
		if (subMDN != null) {
			
			Pocket destPocket = null;			
			destPocket = pocketService.getDefaultPocket(subMDN, String.valueOf(CmFinoFIX.PocketType_SVA));
			
			if(null != destPocket) {
			
				if(destPocket.getCurrentBalance().compareTo(BigDecimal.valueOf(systemParametersService.getInteger(SystemParameterKeys.MAXIMUM_SUBSCRIBER_CLOSING_AMOUNT))) == -1) {
					
					if(CmFinoFIX.SubscriberStatus_Active.equals(subMDN.getSubscriber().getStatus())) {
					
						ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
						query.setSourceMdn(subMDN.getMDN());
						query.setStatus(CmFinoFIX.SCTLStatus_Pending);
						
						List<ServiceChargeTransactionLog> sctlData = sctlService.getSubscriberPendingTransactions(query);
						
						if(null == sctlData || (null != sctlData && sctlData.size() == 0)) {
						
							result.setName(subMDN.getSubscriber().getFirstName());
							result.setCode(String.valueOf(CmFinoFIX.NotificationCode_AgentClosingInquirySuccess));
							result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingInquirySuccess);
							result.setMessage("Agent Closing Inquiry successfull");
							result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
							
							Transaction transaction = null;
							
							ServiceCharge sc=new ServiceCharge();
							sc.setChannelCodeId(channelCode.getID());
							sc.setDestMDN(subMDN.getMDN());
							sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
							sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CLOSE_ACCOUNT);							
							sc.setTransactionAmount(BigDecimal.ZERO);
							sc.setTransactionLogId(transactionsLog.getID());
							sc.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

							try{
								
								transaction = transactionChargingService.getCharge(sc);

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
							result.setMfaMode("None");
							
							sendOTPSMS(subMDN);
							
							log.debug("SMS for OTP has been sent....");
							
						} else {
							
							result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
							result.setCode(String.valueOf(CmFinoFIX.NotificationCode_AgentClosingInquiryFailed));
							result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingInquiryFailed);
							
							log.debug("Agent has pending transactions....");
						}
						
					} else {
						
						result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
						result.setCode(String.valueOf(CmFinoFIX.NotificationCode_MDNIsNotActive));
						result.setNotificationCode(CmFinoFIX.NotificationCode_MDNIsNotActive);
						
						log.debug("Agent is not active....");
					}
					
				} else {
					
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
					result.setCode(String.valueOf(CmFinoFIX.NotificationCode_SubscriberHasAccountBalance));
					result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberHasAccountBalance);
					
					log.debug("Agent balance is > 100....");
				}
			}			
		} else {
		
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			
			log.debug("Agent not found....");
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