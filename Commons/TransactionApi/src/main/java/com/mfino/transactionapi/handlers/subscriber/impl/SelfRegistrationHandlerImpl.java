package com.mfino.transactionapi.handlers.subscriber.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistration;
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
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.SelfRegistrationHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.RegistrationXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

/*
 *
 * @author Bala Sunku
 */
@Service("SelfRegistrationHandlerImpl")
public class SelfRegistrationHandlerImpl extends FIXMessageHandler implements SelfRegistrationHandler{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private String email;

	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;	
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;	
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	public Result handle(TransactionDetails transDetails) {
		CMSubscriberRegistration subscriberRegistration = new CMSubscriberRegistration();
		ChannelCode cc = transDetails.getCc();
		
		subscriberRegistration.setSourceMDN(transDetails.getSourceMDN());
		subscriberRegistration.setMDN(subscriberService.normalizeMDN(transDetails.getSourceMDN()));
		subscriberRegistration.setFirstName(transDetails.getFirstName());
		subscriberRegistration.setLastName(transDetails.getLastName());
		subscriberRegistration.setPin(transDetails.getSourcePIN());
		subscriberRegistration.setMothersMaidenName(transDetails.getMothersMaidenName());
		subscriberRegistration.setDateOfBirth(new Timestamp(transDetails.getDateOfBirth()));
		subscriberRegistration.setKYCLevel(ConfigurationUtil.getIntialKyclevel());
		subscriberRegistration.setChannelCode(cc.getChannelCode());
		subscriberRegistration.setSourceApplication(cc.getChannelSourceApplication());
		subscriberRegistration.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		email = transDetails.getEmail();
		
		TransactionsLog transactionsLog = null;
		log.info("Handling subscriber services Registration webapi request");
		XMLResult result = new RegistrationXMLResult();
		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(transDetails.getSourceMDN());

		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberRegistration,subscriberRegistration.DumpFields());
		result.setSourceMessage(subscriberRegistration);
		result.setDestinationMDN(subscriberRegistration.getMDN());
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());
		subscriberRegistration.setTransactionID(transactionsLog.getID());
		addCompanyANDLanguageToResult(srcSubscriberMDN,result);
		result.setActivityStatus(false);

		// Check whether the agent has the Service or not.

		Transaction transactionDetails = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(subscriberRegistration.getSourceMDN());
		sc.setDestMDN(subscriberRegistration.getMDN());
		sc.setChannelCodeId(cc.getID());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION);
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

		Subscriber subscriber = new Subscriber();
		SubscriberMDN subscriberMDN = new SubscriberMDN();
		Pocket epocket = new Pocket();
		Integer OTPLength = systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		subscriber.setEmail(email);
		subscriber.setIsEmailVerified(BOOL_FALSE);
		Integer regResponse = subscriberServiceExtended.registerSubscriber(subscriber, subscriberMDN, subscriberRegistration,
				epocket,oneTimePin,null);
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
			sendSMS(subscriberRegistration, oneTimePin, false);
			result.setOneTimePin(oneTimePin);
		}else{
			result.setActivityStatus(true);
			result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberRegistrationSuccessfulToSubscriber);
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
//				sctl.setCalculatedCharge(BigDecimal.ZERO);
				transactionChargingService.confirmTheTransaction(sctl);
			}
			sendSMS(subscriberRegistration, oneTimePin, true);
			result.setOneTimePin(oneTimePin);
		}

		return result;

	}

	private void sendSMS(CMSubscriberRegistration subscriberRegistration, String oneTimePin, boolean registartionStatus) {

		//Skip SMS to Sender in case of STK Request (Channel code is 6)
		if(CmFinoFIX.SourceApplication_SMS.equals(subscriberRegistration.getSourceApplication()))
		{
			return;
		}

		NotificationWrapper notificationWrapper=new NotificationWrapper();
		if(registartionStatus){
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberRegistrationSuccessfulToSubscriber);
			notificationWrapper.setOneTimePin(oneTimePin);
			notificationWrapper.setDestMDN(subscriberRegistration.getMDN());
			SubscriberMDN smdn = subscriberMdnService.getByMDN(subscriberRegistration.getMDN());
			if(smdn != null)
			{
				notificationWrapper.setFirstName(smdn.getSubscriber().getFirstName());
				notificationWrapper.setLastName(smdn.getSubscriber().getLastName());
			}

		}else{
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberRegistrationfailed);
			notificationWrapper.setDestMDN(subscriberRegistration.getMDN());
		}
		if(notificationMessageParserService!=null)
		{

			smsService.setDestinationMDN(subscriberRegistration.getMDN());
			smsService.setSctlId(subscriberRegistration.getServiceChargeTransactionLogID());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			smsService.setMessage(notificationMessageParserService.buildMessage(notificationWrapper,true));
			smsService.send();
		}
	}
}
