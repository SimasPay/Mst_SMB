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
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.SMSValues;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
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
		
		TransactionLog transactionsLog = null;
		ServiceChargeTxnLog sctl = null;
		
		ChannelCode channelCode = transactionDetails.getCc();
		CMJSAgentClosingInquiry agentClosing = new CMJSAgentClosingInquiry();
		agentClosing.setDestMDN(transactionDetails.getDestMDN());
		
		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSAgentClosingInquiry,agentClosing.DumpFields());
		
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		SubscriberMdn subMDN = subscriberMdnService.getByMDN(agentClosing.getDestMDN());
		
		Integer validationResult = transactionApiValidationService.validateSubscriberAsDestination(subMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			return result;
		}
		
		Partner partner = partnerService.getPartner(subMDN);
		
		if (subMDN != null) {
			
			Pocket destPocket = null;	
			destPocket = pocketService.getDefaultPocket(subMDN, String.valueOf(CmFinoFIX.PocketType_LakuPandai));
			
			if(null != destPocket) {
			
				if(!checkBalanceInAllPockets(subMDN.getId().longValue())) {
					
					if(CmFinoFIX.SubscriberStatus_Active.equals(subMDN.getSubscriber().getStatus())) {
					
						ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
						query.setSourceMdn(subMDN.getMdn());
						query.setStatus(CmFinoFIX.SCTLStatus_Pending);
						
						List<ServiceChargeTxnLog> sctlData = sctlService.getSubscriberPendingTransactions(query);
						
						if(null == sctlData || (null != sctlData && sctlData.size() == 0)) {
						
							result.setName(subMDN.getSubscriber().getFirstname());
							result.setCode(String.valueOf(CmFinoFIX.NotificationCode_AgentClosingInquirySuccess));
							result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingInquirySuccess);
							result.setMessage("Agent Closing Inquiry successfull");
							result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
							
							Transaction transaction = null;
							
							ServiceCharge sc=new ServiceCharge();
							sc.setChannelCodeId(channelCode.getId().longValue());
							sc.setDestMDN(subMDN.getMdn());
							sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
							sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CLOSE_ACCOUNT);							
							sc.setTransactionAmount(BigDecimal.ZERO);
							sc.setTransactionLogId(transactionsLog.getId().longValue());
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
							
							result.setSctlID(sctl.getId().longValue());
							result.setMfaMode("None");
							
							sendOTPSMS(subMDN,sctl.getId().longValue());
							
							partner.setCloseacctstatus(CmFinoFIX.CloseAcctStatus_Initialized);
							
							log.debug("SMS for OTP has been sent....");
							
						} else {
							
							result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
							result.setCode(String.valueOf(CmFinoFIX.NotificationCode_AgentClosingInquiryFailed));
							result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingInquiryFailed);
							
							partner.setCloseacctstatus(CmFinoFIX.CloseAcctStatus_InquiryFailed);
							
							log.debug("Agent has pending transactions....");
						}
						
					} else {
						
						result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
						result.setCode(String.valueOf(CmFinoFIX.NotificationCode_MDNIsNotActive));
						result.setNotificationCode(CmFinoFIX.NotificationCode_MDNIsNotActive);
						
						partner.setCloseacctstatus(CmFinoFIX.CloseAcctStatus_InquiryFailed);
						
						log.debug("Agent is not active....");
					}
					
				} else {
					
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
					result.setCode(String.valueOf(CmFinoFIX.NotificationCode_SubscriberHasAccountBalance));
					result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberHasAccountBalance);
					
					partner.setCloseacctstatus(CmFinoFIX.CloseAcctStatus_InquiryFailed);
					
					log.debug("Agent balance is > 100....");
				}
			}			
		} else {
		
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			
			partner.setCloseacctstatus(CmFinoFIX.CloseAcctStatus_InquiryFailed);
			
			log.debug("Agent not found....");
		}
		
		partnerService.savePartner(partner);
		
		return result;
	}
	
	private boolean checkBalanceInAllPockets(Long mdnId) {
        
		// Here we need to get the Records from pocket table for MdnId.        
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();			
        PocketQuery pocketQuery = new PocketQuery();
        pocketQuery.setMdnIDSearch(mdnId);
        boolean isBalanceAvailable = false;
        
        List<Pocket> resultantPockets = pocketDAO.get(pocketQuery);

        for (Pocket eachPocket : resultantPockets) {
        
        	if(eachPocket.getCurrentbalance().compareTo(BigDecimal.valueOf(systemParametersService.getInteger(SystemParameterKeys.MAXIMUM_AGENT_CLOSING_AMOUNT))) == 1) {
        		
        		isBalanceAvailable = true;
        		break;
        	}
        }
        
        return isBalanceAvailable;
	}
	
	private void sendOTPSMS (SubscriberMdn subscriberMDN , Long sctlID) {
		
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		Subscriber subscriber = subscriberMDN.getSubscriber();
		
		Integer OTPLength = systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMdn(), oneTimePin);
		subscriberMDN.setOtp(digestPin1);
		subscriberMDN.setOtpexpirationtime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
		subscriberMDNDAO.save(subscriberMDN);
		
		NotificationWrapper smsNotificationWrapper = subscriberServiceExtended.generateOTPMessage(oneTimePin, CmFinoFIX.NotificationMethod_SMS,CmFinoFIX.NotificationCode_AgentClosingInquirySuccess);
		smsNotificationWrapper.setDestMDN(subscriberMDN.getMdn());
		smsNotificationWrapper.setLanguage(new Integer(String.valueOf(subscriber.getLanguage())));
		smsNotificationWrapper.setFirstName(subscriber.getFirstname());
    	smsNotificationWrapper.setLastName(subscriber.getLastname());
		
    	String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper,true);
		String mdn2 = subscriberMDN.getMdn();
		
		log.info("smsMessage:" + smsMessage);
		
		SMSValues smsValues= new SMSValues();
		smsValues.setDestinationMDN(mdn2);
		smsValues.setMessage(smsMessage);
		smsValues.setNotificationCode(smsNotificationWrapper.getCode());
		smsService.asyncSendSMS(smsValues);
		
		log.info("sms sent successfully");
	}
}