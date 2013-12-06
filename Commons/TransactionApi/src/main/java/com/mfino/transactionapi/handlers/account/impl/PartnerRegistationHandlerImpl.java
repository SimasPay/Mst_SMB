package com.mfino.transactionapi.handlers.account.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Notification;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.exceptions.PartnerRegistrationException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPartnerRegistrationThroughAPI;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PartnerService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.PartnerRegistrationHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.RegistrationXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;
@Service("PartnerRegistrationHandlerImpl")
public class PartnerRegistationHandlerImpl extends FIXMessageHandler implements PartnerRegistrationHandler{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private CMPartnerRegistrationThroughAPI partnerRegistration;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	public Result handle(TransactionDetails txnDetails) {
		partnerRegistration = new CMPartnerRegistrationThroughAPI();		
		ChannelCode cc = txnDetails.getCc();
		partnerRegistration.setMDN(subscriberService.normalizeMDN(txnDetails.getDestMDN()));
		partnerRegistration.setBusinessPartnerType(txnDetails.getPartnerType());
		partnerRegistration.setTradeName(txnDetails.getTradeName());
		partnerRegistration.setPartnerCode(txnDetails.getPartnerCode());
		partnerRegistration.setUsername(txnDetails.getUserName());
		
		partnerRegistration.setMerchantAddressLine1(txnDetails.getPlotNo());
		partnerRegistration.setMerchantAddressLine2(txnDetails.getStreetAddress());
		partnerRegistration.setMerchantAddressCity(txnDetails.getCity());
		partnerRegistration.setMerchantAddressState(txnDetails.getRegionName());
		partnerRegistration.setMerchantAddressZipcode(txnDetails.getPostalCode());
		partnerRegistration.setMerchantAddressCountry(txnDetails.getCountry());
		
		partnerRegistration.setClassification(txnDetails.getOutletClasification());
		partnerRegistration.setFranchisePhoneNumber(txnDetails.getFranchisePhoneNumber());
		partnerRegistration.setFaxNumber(txnDetails.getFaxNumber());
		partnerRegistration.setTypeOfOrganization(txnDetails.getTypeOfOrganization());
		partnerRegistration.setIndustryClassification(txnDetails.getIndustryClassification());
		partnerRegistration.setWebSite(txnDetails.getWebSite());
		partnerRegistration.setNumberOfOutlets(txnDetails.getNumberOfOutlets());	
		partnerRegistration.setYearEstablished(txnDetails.getYearEstablished());
		partnerRegistration.setOutletAddressLine1(txnDetails.getOutletAddressLine1());
		partnerRegistration.setOutletAddressLine2(txnDetails.getOutletAddressLine2());
		partnerRegistration.setOutletAddressCity(txnDetails.getOutletAddressCity());
		partnerRegistration.setOutletAddressState(txnDetails.getOutletAddressState());
		partnerRegistration.setOutletAddressZipcode(txnDetails.getOutletAddressZipcode());
		partnerRegistration.setOutletAddressCountry(txnDetails.getOutletAddressCountry());
		
		partnerRegistration.setAuthorizedRepresentative(txnDetails.getAuthorizedRepresentative());
		partnerRegistration.setRepresentativeName(txnDetails.getRepresentativeName());
		partnerRegistration.setDesignation(txnDetails.getDesignation());
		partnerRegistration.setAuthorizedFaxNumber(txnDetails.getAuthorizedFaxNumber());
		partnerRegistration.setAuthorizedEmail(txnDetails.getEmail());
		
		partnerRegistration.setApprovalRequired(txnDetails.getApprovalRequired());
		partnerRegistration.setAccountNumber(txnDetails.getCardPAN());
		
//		partnerRegistration.setLanguage(Integer.parseInt(txnDetails.getLanguage()));
		
		TransactionsLog transactionsLog = null;
		log.info("Handling Partner Registration webapi request");
		XMLResult result = new RegistrationXMLResult();

		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_PartnerRegistrationThroughAPI,partnerRegistration.DumpFields());
		result.setSourceMessage(partnerRegistration);
		result.setDestinationMDN(partnerRegistration.getMDN());
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());
		partnerRegistration.setTransactionID(transactionsLog.getID());

		result.setActivityStatus(false);
		

		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(partnerRegistration.getMDN());
		sc.setDestMDN(partnerRegistration.getMDN());
		sc.setChannelCodeId(cc.getID());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_PARTNER_REGISTRATION_THROUGH_API);
		sc.setTransactionAmount(ZERO);
		sc.setTransactionLogId(transactionsLog.getID());
		sc.setTransactionIdentifier(partnerRegistration.getTransactionIdentifier());

		try{
			transaction =transactionChargingService.getCharge(sc);
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
		partnerRegistration.setServiceChargeTransactionLogID(sctl.getID());
		Integer OTPLength = systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		try{
			Partner partner = partnerService.registerPartner(partnerRegistration);
			log.info("partner registration successfull");
			result.setActivityStatus(true);
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerRegistrationSuccessful);
			sendSMS(partnerRegistration, oneTimePin, true);		
		}catch (Exception e) {
			String errordesc = "Internal Exception check logs";
			if(e instanceof PartnerRegistrationException){
				log.info("Error registering partner :"+e.getMessage());
				errordesc = e.getMessage();
			}
			else
				log.info("Error registering partner :",e);
			Notification notification = notificationService.getByNoticationCode(CmFinoFIX.NotificationCode_PartnerRegistrationFailed);
			String notificationName = null;
			if(notification != null){
				notificationName = notification.getCodeName();
			}else{
				log.error("Could not find the failure notification code: "+CmFinoFIX.NotificationCode_PartnerRegistrationFailed);
			}
			transactionChargingService.failTheTransaction(sctl, MessageText._("Partner Registration failed. Notification Code: "+CmFinoFIX.NotificationCode_PartnerRegistrationFailed+" NotificationName: "+notificationName+" Error:"+errordesc));
			result.setActivityStatus(false);
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerRegistrationFailed);
			sendSMS(partnerRegistration, oneTimePin, false);
		} 
		return result;

	}
	
	private void sendSMS(CMPartnerRegistrationThroughAPI partnerRegistration2, String oneTimePin, boolean registrationStatus) {

		smsService.setSctlId(partnerRegistration2.getServiceChargeTransactionLogID());
		NotificationWrapper notificationWrapper=new NotificationWrapper();
		SubscriberMDN smdn = subscriberMdnService.getByMDN(partnerRegistration2.getMDN());
		if(smdn != null)
		{
			notificationWrapper.setFirstName(smdn.getSubscriber().getFirstName());
			notificationWrapper.setLastName(smdn.getSubscriber().getLastName());
		}
		if(registrationStatus){
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_PartnerRegistrationSuccessful);
			notificationWrapper.setOneTimePin(oneTimePin);
			notificationWrapper.setDestMDN(partnerRegistration2.getMDN());
			smsService.setDestinationMDN(partnerRegistration2.getMDN());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			smsService.setMessage(notificationMessageParserService.buildMessage(notificationWrapper,true));
			smsService.send();
		}else{
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_PartnerRegistrationFailed);
			notificationWrapper.setDestMDN(partnerRegistration2.getMDN());
			notificationWrapper.setCustomerServiceShortCode(ConfigurationUtil.getCustomerServiceShortCode());
			smsService.setDestinationMDN(partnerRegistration2.getSourceMDN());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			smsService.setMessage(notificationMessageParserService.buildMessage(notificationWrapper,true));
			smsService.send();
		}

	}
}
