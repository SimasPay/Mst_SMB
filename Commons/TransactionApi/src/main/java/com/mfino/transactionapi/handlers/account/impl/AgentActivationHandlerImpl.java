package com.mfino.transactionapi.handlers.account.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Notification;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.AgentService;
import com.mfino.service.NotificationService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.AgentActivationHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ActivationXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;

/*
 * 
 * @author Maruthi
 */
@Service("AgentActivationHandlerImpl")
public class AgentActivationHandlerImpl extends FIXMessageHandler implements AgentActivationHandler{
	
	private static Logger log = LoggerFactory.getLogger(AgentActivationHandlerImpl.class);

	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("AgentServiceImpl")
	private AgentService agentService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
 	public Result handle(TransactionDetails transactionDetails) {
 		log.info("AgentActivationHandlerImpl :: Handling Agent activation webapi request for "+transactionDetails.getSourceMDN());
		
 		ChannelCode cc = transactionDetails.getCc();
		CMSubscriberActivation subscriberActivation = new CMSubscriberActivation();
		subscriberActivation.setPin(transactionDetails.getNewPIN());
		subscriberActivation.setSourceMDN(transactionDetails.getSourceMDN());
		subscriberActivation.setOTP(transactionDetails.getActivationOTP());
		subscriberActivation.setSourceApplication(cc.getChannelsourceapplication());
		subscriberActivation.setChannelCode(cc.getChannelcode());
		subscriberActivation.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

		boolean isHttps = transactionDetails.isHttps();
		
		XMLResult result = new ActivationXMLResult();
		Transaction transaction = null;


		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberActivation, subscriberActivation.DumpFields());

 		result.setSourceMessage(subscriberActivation);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getId());
		subscriberActivation.setTransactionID(transactionsLog.getId());
	
		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(subscriberActivation.getSourceMDN());

		addCompanyANDLanguageToResult(subscriberMDN,result);

		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(subscriberActivation.getSourceMDN());
		serviceCharge.setOnBeHalfOfMDN(subscriberActivation.getSourceMDN());
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(cc.getId().longValue());
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_AGENTACTIVATION);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(transactionsLog.getID());
		serviceCharge.setTransactionIdentifier(subscriberActivation.getTransactionIdentifier());

		try{
			transaction =transactionChargingService.getCharge(serviceCharge);
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
 			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
 			return result;
		}
		
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		subscriberActivation.setServiceChargeTransactionLogID(sctl.getId());

//		NotificationWrapper wrapper = agentService.activeAgent(subscriberActivation,isHttps);
		NotificationWrapper wrapper = agentService.activeAgent(subscriberActivation,isHttps, ConfigurationUtil.getuseHashedPIN());		
		Integer code= wrapper.getCode();
		if(code.equals(CmFinoFIX.NotificationCode_PartnerActivationCompleted)){
			log.info("AgentActivationHandlerImpl :: Agent Activation successfull for "+transactionDetails.getSourceMDN());
			result.setActivityStatus(BOOL_TRUE);
			if (sctl != null) {
				sctl.setCalculatedCharge(BigDecimal.ZERO);
				transactionChargingService.completeTheTransaction(sctl);
			}
		}else{
			Notification notification = notificationService.getByNoticationCode(code);
			String notificationName = null;
			log.info("AgentActivationHandlerImpl :: Agent Activation failed for "+transactionDetails.getSourceMDN());

			if(notification != null){
				notificationName = notification.getCodename();
			}else{
				log.error("Could not find the failure notification code: "+code);
			}
			result.setActivityStatus(BOOL_FALSE);
			transactionChargingService.failTheTransaction(sctl, MessageText._("Agent Activation Falied. Notification Code: "+code+" NotificationName: "+notificationName));
		}
		
		result.setPartnerCode(wrapper.getPartnerCode());
		result.setNotificationCode(code);
		result.setSctlID(sctl.getId());

		return result;
	}
 	
 	public void test(){
 		MultixCommunicationHandler handler = new MultixCommunicationHandler();
 	}
}
