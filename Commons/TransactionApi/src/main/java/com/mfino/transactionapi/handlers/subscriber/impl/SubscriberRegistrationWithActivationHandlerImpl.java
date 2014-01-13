package com.mfino.transactionapi.handlers.subscriber.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Notification;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationThroughWeb;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.SubscriberRegistrationWithActivationHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.RegistrationXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;

/*
 *
 * @author Srikanth
 */
@Service("SubscriberRegistrationWithActivationHandlerImpl")
public class SubscriberRegistrationWithActivationHandlerImpl extends FIXMessageHandler implements SubscriberRegistrationWithActivationHandler{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Result handle(TransactionDetails txnDetails) {
		CMSubscriberRegistrationThroughWeb subscriberRegistration = new CMSubscriberRegistrationThroughWeb();
		
		ChannelCode cc = txnDetails.getCc();
		// Only source MDN exists in this case, no dest MDN - so didnt set MDN
		subscriberRegistration.setSourceMDN(txnDetails.getSourceMDN());
		subscriberRegistration.setFirstName(txnDetails.getFirstName());
		subscriberRegistration.setLastName(txnDetails.getLastName());
		subscriberRegistration.setNickname(txnDetails.getNickname());
		if(txnDetails.getDateOfBirth() != null)
		{
			subscriberRegistration.setDateOfBirth(new Timestamp(txnDetails.getDateOfBirth()));
		}
		if(ServiceAndTransactionConstants.TRANSACTION_REGISTRATION_WITH_ACTIVATION_HUB.equals(txnDetails.getTransactionName())){
			subscriberRegistration.setKYCLevel(CmFinoFIX.SubscriberKYCLevel_NoKyc.longValue());
		}
		else{
			subscriberRegistration.setKYCLevel(ConfigurationUtil.getIntialKyclevel());
		}
		subscriberRegistration.setChannelCode(cc.getChannelCode());
		subscriberRegistration.setSourceApplication(cc.getChannelSourceApplication());
		subscriberRegistration.setIDType(txnDetails.getIdType());
		subscriberRegistration.setIDNumber(txnDetails.getIdNumber());
		subscriberRegistration.setPin(txnDetails.getNewPIN());
		subscriberRegistration.setTransactionIdentifier(txnDetails.getTransactionIdentifier());
		subscriberRegistration.setOtherMDN(txnDetails.getOtherMdn());
		TransactionsLog transactionsLog = null;
		log.info("Handling subscriber services Registration with activation webapi request");
		XMLResult result = new RegistrationXMLResult();

		//change messg type below
		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberRegistration,subscriberRegistration.DumpFields());
		result.setSourceMessage(subscriberRegistration);
		result.setDestinationMDN(subscriberRegistration.getSourceMDN());
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());
		subscriberRegistration.setTransactionID(transactionsLog.getID());

		result.setActivityStatus(false);


		Transaction transactionDetails = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(subscriberRegistration.getSourceMDN());
		//sc.setDestMDN(subscriberRegistration.getMDN());
		sc.setChannelCodeId(cc.getID());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_REGISTRATION_WITH_ACTIVATION);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionsLog.getID());
		sc.setTransactionIdentifier(subscriberRegistration.getTransactionIdentifier());

		try{
			transactionDetails =transactionChargingService.getCharge(sc);
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTransactionLog sctl = transactionDetails.getServiceChargeTransactionLog();
		subscriberRegistration.setServiceChargeTransactionLogID(sctl.getID());
		result.setSctlID(sctl.getID());

		Integer regResponse = subscriberServiceExtended.registerWithActivationSubscriber(subscriberRegistration);

		if (!regResponse.equals(CmFinoFIX.ResponseCode_Success)) {
			Notification notification = notificationService.getByNoticationCode(regResponse);
			String notificationName = null;
			if(notification != null){
				notificationName = notification.getCodeName();
			}else{
				log.error("Could not find the failure notification code: "+regResponse);
			}
			result.setActivityStatus(false);
			result.setNotificationCode(regResponse);
			transactionChargingService.failTheTransaction(sctl, MessageText._("Subscriber Registration failed. Notification Code: "+regResponse+" NotificationName: "+notificationName));
			sendSMS(false,subscriberRegistration);
		}else{
			result.setActivityStatus(true);
			result.setNotificationCode(CmFinoFIX.NotificationCode_BOBPocketActivationCompleted);
			if (sctl != null) {
				// Calculate the Commission and generates the logs for the same
				sc.setSctlId(sctl.getID());
				try{
					transactionDetails =transactionChargingService.getCharge(sc);
				} catch (InvalidChargeDefinitionException e) {
					log.error(e.getMessage());
				} catch (Exception e) {
					log.error("Exception occured in getting charges for Registration",e);
				}
				transactionChargingService.confirmTheTransaction(sctl);
			}
			sendSMS(true,subscriberRegistration);
		}

		return result;

	}

	private void sendSMS(boolean registartionStatus,CMSubscriberRegistrationThroughWeb subscriberRegistration) {

		//Skip SMS to Sender in case of STK Request (Channel code is 6)
		if(CmFinoFIX.SourceApplication_STK.equals(subscriberRegistration.getSourceApplication()))
		{
			return;
		}

		NotificationWrapper notificationWrapper=new NotificationWrapper();
		SubscriberMDN smdn = subscriberMdnService.getByMDN(subscriberRegistration.getMDN());
		if(smdn != null)
		{
			notificationWrapper.setFirstName(smdn.getSubscriber().getFirstName());
			notificationWrapper.setLastName(smdn.getSubscriber().getLastName());
		}
		if(registartionStatus){
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_BOBPocketActivationCompleted);
			notificationWrapper.setDestMDN(subscriberRegistration.getSourceMDN());

		}else{
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberRegistrationfailed);
			notificationWrapper.setDestMDN(subscriberRegistration.getSourceMDN());
		}
		if(notificationMessageParserService!=null)
		{
			smsService.setDestinationMDN(subscriberRegistration.getSourceMDN());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			smsService.setMessage(notificationMessageParserService.buildMessage(notificationWrapper,true));
			smsService.setSctlId(subscriberRegistration.getServiceChargeTransactionLogID());
			smsService.send();
		}
	}
}
