package com.mfino.transactionapi.handlers.subscriber.impl;


import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationThroughWeb;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.KYCLevelService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.SubscriberRegistrationThroughWebHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.RegistrationXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.SubscriberSyncErrors;
@Service("SubscriberRegistrationThroughWebHandlerImpl")
public class SubscriberRegistrationThroughWebHandlerImpl extends FIXMessageHandler implements SubscriberRegistrationThroughWebHandler{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private CMSubscriberRegistrationThroughWeb subscriberRegistration;

	@Autowired
	@Qualifier("KYCLevelServiceImpl")
	private KYCLevelService kycLevelService;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	public Result handle(TransactionDetails txnDetails) {
		subscriberRegistration = new CMSubscriberRegistrationThroughWeb();		
		ChannelCode cc = txnDetails.getCc();
		subscriberRegistration.setMDN(subscriberService.normalizeMDN(txnDetails.getDestMDN()));
		subscriberRegistration.setFirstName(txnDetails.getFirstName());
		subscriberRegistration.setLastName(txnDetails.getLastName());
		subscriberRegistration.setApplicationID(txnDetails.getApplicationId());
		subscriberRegistration.setDateOfBirth(new Timestamp(txnDetails.getDateOfBirth()));
		subscriberRegistration.setCity(txnDetails.getCity());
		subscriberRegistration.setKinMDN(txnDetails.getNextOfKinNo());
		subscriberRegistration.setKinName(txnDetails.getNextOfKin());
		subscriberRegistration.setEmail(txnDetails.getEmail());
		subscriberRegistration.setPlotNo(txnDetails.getPlotNo());
		subscriberRegistration.setStreetAddress(txnDetails.getStreetAddress());
		subscriberRegistration.setRegionName(txnDetails.getRegionName());
		subscriberRegistration.setCountry(txnDetails.getCountry());
		subscriberRegistration.setIDType(txnDetails.getIdType());
		subscriberRegistration.setIDNumber(txnDetails.getIdNumber());
		subscriberRegistration.setKYCLevel(txnDetails.getKycType() == null ? -1L : Long.parseLong(txnDetails.getKycType()));
		try {
			if(txnDetails.getDateOfExpiry() != null){
			subscriberRegistration.setIDExpiryDate(new Timestamp(transactionApiValidationService.getDate(txnDetails.getDateOfExpiry())));
			}
		} catch (InvalidDataException e1) {
			log.error("Exception occured in parsing DateOfExpiry",e1);
			e1.printStackTrace();
		}
		subscriberRegistration.setProofofAddress(txnDetails.getAddressProof());
		subscriberRegistration.setBirthPlace(txnDetails.getBirthPlace());
		subscriberRegistration.setNationality(txnDetails.getNationality());
		subscriberRegistration.setSubsCompanyName(txnDetails.getCompanyName());
		subscriberRegistration.setSubscriberMobileCompany(txnDetails.getSubscriberMobileCompany());
		subscriberRegistration.setCertofIncorporation(txnDetails.getCertOfIncorp());
		subscriberRegistration.setChannelCode(cc.getChannelcode());
		subscriberRegistration.setSourceApplication((int)cc.getChannelsourceapplication());
		subscriberRegistration.setApprovalRequired(txnDetails.getApprovalRequired());
		subscriberRegistration.setCardPAN(txnDetails.getCardPAN());
		if(StringUtils.isNotBlank(txnDetails.getBankAccountType())){
			subscriberRegistration.setBankAccountType(Integer.parseInt(txnDetails.getBankAccountType()));
		}
		subscriberRegistration.setAuthorizingFirstName(txnDetails.getAuthorizingFirstName());
		subscriberRegistration.setAuthorizingLastName(txnDetails.getAuthorizingLastName());
		subscriberRegistration.setAuthorizingIdNumber(txnDetails.getAuthorizingIdNumber());
		subscriberRegistration.setApprovalComments(txnDetails.getApprovalComments());
		subscriberRegistration.setUpgradableKYCLevel(Long.parseLong(txnDetails.getKycType()));
		subscriberRegistration.setTransactionIdentifier(txnDetails.getTransactionIdentifier());
		
		TransactionsLog transactionsLog = null;
		log.info("Handling subscriber services Registration webapi request");
		XMLResult result = new RegistrationXMLResult();

		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberRegistration,subscriberRegistration.DumpFields());
		result.setSourceMessage(subscriberRegistration);
		result.setDestinationMDN(subscriberRegistration.getMDN());
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());
		subscriberRegistration.setTransactionID(transactionsLog.getID());

		result.setActivityStatus(false);
		

		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(subscriberRegistration.getMDN());
		sc.setDestMDN(subscriberRegistration.getMDN());
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBER_REGISTRATION_THROUGH_WEB);
		sc.setTransactionAmount(ZERO);
		sc.setTransactionLogId(transactionsLog.getID());
		sc.setTransactionIdentifier(subscriberRegistration.getTransactionIdentifier());

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
		subscriberRegistration.setServiceChargeTransactionLogID(sctl.getID());
		Integer OTPLength = systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		Integer regResponse = subscriberServiceExtended.registerSubscriberThroughWeb(subscriberRegistration, oneTimePin);
		if (!regResponse.equals(CmFinoFIX.ResponseCode_Success)) {
			Notification notification = notificationService.getByNoticationCode(regResponse);
			String notificationName = null;
			if(notification != null){
				notificationName = notification.getCodename();
			}else{
				log.error("Could not find the failure notification code: "+regResponse);
			}
			transactionChargingService.failTheTransaction(sctl, MessageText._("Subscriber Registration failed. Notification Code: "+regResponse+" NotificationName: "+notificationName));
			result.setActivityStatus(false);
			result.setNotificationCode(regResponse);
			sendSMS(subscriberRegistration, oneTimePin, false);
		}else{
			SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(subscriberRegistration.getMDN());
			if(!(CmFinoFIX.RecordType_SubscriberUnBanked.toString().equals(subscriberRegistration.getUpgradableKYCLevel().toString()))){
				if(!(subscriberRegistration.getApprovalRequired())){  					

					log.info("the field value is:"+subscriberRegistration.getApprovalRequired());
					log.info("entered the code.approvalRequired false in handler");
					autoApproval();
				}else{
					Pocket bankPocket = getBankPocket(subscriberMDN);
					if(bankPocket==null){
						log.info("bank pocket is null");
					}

				}
			}
			result.setActivityStatus(true);
			result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberRegistrationThroughWebSuccessfulToSubscriber);
			sendSMS(subscriberRegistration, oneTimePin, true);			
		}
		return result;

	}

	private Pocket getBankPocket(SubscriberMdn subscriberMDN){
		Long groupID = null;
		Pocket bankPocket = null;
		Subscriber subscriber = subscriberMDN.getSubscriber();
		Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroup().getID();
		}
		PocketTemplate bankPocketTemplate = pocketService.getBankPocketTemplateFromPocketTemplateConfig(subscriberRegistration.getBankAccountType(), true, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
		if(bankPocketTemplate == null)
		{
			log.info("No valid Bank Pocket Template configured for this subscriber type");		        	 
		}
		else{
			if(subscriber.getUpgradablekyclevel() !=null && subscriber.getUpgradablekyclevel().intValue() > CmFinoFIX.RecordType_SubscriberUnBanked){
				bankPocket = pocketService.createPocket(bankPocketTemplate, subscriberMDN, CmFinoFIX.PocketStatus_Initialized, true, subscriberRegistration.getCardPAN());
			}
		}		
		return bankPocket;
	}
	
	private int updatePockets(Subscriber subscriber, Pocket emoneyPocket,
			Pocket bankPocket, Integer adminAction,
			PocketTemplate upgradetemplate) {
		if (emoneyPocket.getPocketTemplate().equals(upgradetemplate)) {
			log.info("Pocket already upgraded");
		}
		log.info("EMONEYpOCKET id="+emoneyPocket.getPocketTemplate()+upgradetemplate);
		emoneyPocket.setPocketTemplateByOldpockettemplateid(emoneyPocket.getPocketTemplate());
		emoneyPocket.setPocketTemplate(upgradetemplate);
		emoneyPocket.setPockettemplatechangetime(new Timestamp());
		pocketService.save(emoneyPocket);
		log.info("in update pocket");
		return SubscriberSyncErrors.Success;

	}
	public void autoApproval(){
		// function if approval required is false		
		
		log.info("entered autoApproval");
		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(subscriberRegistration.getMDN());
		Subscriber subscriber = subscriberMDN.getSubscriber();
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();

		
		Long groupID = null;
		Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroup().getID();
		}
		Long kycLevelNo = null;
		kycLevelNo = subscriber.getKycLevel().getKyclevel().longValue();
		PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
		Pocket emoneyPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(),svaPocketTemplate.getId().longValue());
        subscriber.setUpgradablekyclevel(new BigDecimal(subscriberRegistration.getKYCLevel()));// empty
        
        log.info("KYC LVL="+subscriberRegistration.getKYCLevel());
         PocketTemplate bankPocketTemplate = pocketService.getBankPocketTemplateFromPocketTemplateConfig(subscriberRegistration.getBankAccountType(), true, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
         if(bankPocketTemplate == null)
         {
        	 log.error("No valid Bank Pocket Template configured for this subscriber type");
        	 return;
         }
         Pocket bankPocket = pocketService.createPocket(bankPocketTemplate, subscriberMDN, CmFinoFIX.PocketStatus_Active, true, subscriberRegistration.getCardPAN());
        
        try{
		KYCLevel kyclevel = null;
		if (subscriber.getUpgradablekyclevel() != null) {
			log.info("kyc if loop");
			kyclevel = kycLevelService.getByKycLevel(subscriber.getUpgradablekyclevel().longValue());
		}
		log.info("KYCLEVEL="+kyclevel.toString());
		PocketTemplate upgradeTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kyclevel.getKyclevel().longValue(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
				
		log.info("UPGRADE TEMPLATE="+upgradeTemplate.toString());
		if(upgradeTemplate.equals(null)){
			log.info("upgrade temp is NULL");
		}
			subscriber.setKycLevel(kyclevel);
			subscriber.setUpgradablekyclevel(null);
			subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Approved.longValue());
			subscriber.setApproveorrejectcomment(subscriberRegistration.getApprovalComments());
			subscriber.setApprovedorrejectedby(subscriberRegistration.getAuthorizingFirstName()+ " " +subscriberRegistration.getAuthorizingLastName());
			subscriber.setApprovalidnumber(Long.valueOf(subscriberRegistration.getAuthorizingIdNumber()));
			subscriber.setApproveorrejecttime(new Timestamp());
			
			subscriberService.saveSubscriber(subscriber);
			if(emoneyPocket==null){
				log.info("emoney null");
			}
			if(bankPocket==null){
				log.info("bank null");
			}
			
			Integer response = updatePockets(subscriber, emoneyPocket,bankPocket,3, upgradeTemplate);
			if (!SubscriberSyncErrors.Success.equals(response)) {
				log.info(SubscriberSyncErrors.errorCodesMap.get(response)+"CODE REMOVED");
				errorMsg.setErrorDescription(MessageText._(SubscriberSyncErrors.errorCodesMap.get(response)));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				//return errorMsg;
			}
        }catch(NullPointerException e){
        	log.info("NUll value obtained:" + e.getStackTrace());
        }
			errorMsg.setErrorDescription(MessageText._("Successfully approved the subscriber"));
			
		}
	
	private void sendSMS(CMSubscriberRegistrationThroughWeb subscriberRegistration, String oneTimePin, boolean registrationStatus) {

		smsService.setSctlId(subscriberRegistration.getServiceChargeTransactionLogID());
		NotificationWrapper notificationWrapper=new NotificationWrapper();
		SubscriberMdn smdn = subscriberMdnService.getByMDN(subscriberRegistration.getMDN());
		if(smdn != null)
		{
			notificationWrapper.setFirstName(smdn.getSubscriber().getFirstname());
			notificationWrapper.setLastName(smdn.getSubscriber().getLastname());
		}
		if(registrationStatus){
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberRegistrationSuccessfulToSubscriber);
			notificationWrapper.setOneTimePin(oneTimePin);
			notificationWrapper.setDestMDN(subscriberRegistration.getMDN());
			smsService.setDestinationMDN(subscriberRegistration.getMDN());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			smsService.setMessage(notificationMessageParserService.buildMessage(notificationWrapper,true));
			smsService.send();
		}else{
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Destination);
			notificationWrapper.setDestMDN(subscriberRegistration.getMDN());
			notificationWrapper.setCustomerServiceShortCode(ConfigurationUtil.getCustomerServiceShortCode());
			smsService.setDestinationMDN(subscriberRegistration.getSourceMDN());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			smsService.setMessage(notificationMessageParserService.buildMessage(notificationWrapper,true));
			smsService.send();
		}

	}
}
