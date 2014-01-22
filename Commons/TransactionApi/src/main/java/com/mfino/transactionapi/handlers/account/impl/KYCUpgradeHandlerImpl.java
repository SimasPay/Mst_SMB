package com.mfino.transactionapi.handlers.account.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.Address;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Group;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMKYCUpgrade;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.EnumTextService;
import com.mfino.service.KYCLevelService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberGroupService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.KYCUpgradeHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.KYCUpgradeXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;


@Service("KYCUpgradeHandlerImpl")
public class KYCUpgradeHandlerImpl extends FIXMessageHandler implements KYCUpgradeHandler{
	private static Logger log = LoggerFactory.getLogger(KYCUpgradeHandlerImpl.class);
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("KYCLevelServiceImpl")
	private KYCLevelService kycLevelService;
	
	@Autowired
	@Qualifier("SubscriberGroupServiceImpl")
	private SubscriberGroupService subscriberGroupService ;
	 
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	

	public Result handle(TransactionDetails transactionDetails) {
		String sourceMdn = transactionDetails.getSourceMDN();
		String firstName = transactionDetails.getFirstName();
		String lastName = transactionDetails.getLastName();
		String kycType = transactionDetails.getKycType();
		String city = transactionDetails.getCity();
		String idNumber = transactionDetails.getIdNumber();
		Date dob = transactionDetails.getDateOfBirth();
		
		log.info(String.format("Handling UpgradeKyc request for MDN:%s, KycType:%s, FirstName:%s, LastName:%s, AddressLine1:%s, City:%s, State:%S, " +
				"ZipCode:%s, IDType:%S, IDNumber:%s, dob:%s",
				sourceMdn, kycType, firstName, lastName, transactionDetails.getAddressLine1(),city, transactionDetails.getState(), 
				transactionDetails.getZipCode(), transactionDetails.getIdType(), idNumber, dob.toString()));
		
		XMLResult result = new KYCUpgradeXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		CMKYCUpgrade kycUpgrade = new CMKYCUpgrade();
		ChannelCode	cc = transactionDetails.getCc();
		kycUpgrade.setSourceMDN(sourceMdn);
		kycUpgrade.setFirstName(firstName);
		kycUpgrade.setLastName(lastName);
		kycUpgrade.setKYCLevel(new Long(kycType));
		kycUpgrade.setIDType(transactionDetails.getIdType());
		kycUpgrade.setIDNumber(idNumber);
		kycUpgrade.setCity(city);
		if(dob != null){
			kycUpgrade.setDateOfBirth(new Timestamp(dob));
		}
		log.info("Retrieving KYC level corresponding to kycType:"+kycType);
		
		TransactionsLog transactionsLog = null;
		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_KYCUpgrade, kycUpgrade.DumpFields());
		
		result.setSourceMessage(kycUpgrade);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());
		result.setIdNumber(idNumber);
		result.setSourceMDN(sourceMdn);
		
		KYCLevel kycLevel = kycLevelService.getByKycLevel(new Long(kycType));
		if(kycLevel == null){
			log.info("KYCUpgrade: Failed to get the KycLevel Domain Object Corresponding to KycType:"+kycType);
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidKYCLevel);
			return result;
		}
		result.setKycLevel(kycLevel.getKYCLevelName());
		SubscriberMDN srcMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
		
		if(srcMDN == null){
			log.info("KYCUpgrade: Failed to get the SourceMDN Domain Object Corresponding to MDN:"+sourceMdn);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			return result;
		}
		Subscriber srcSub = srcMDN.getSubscriber();
		
		KYCLevel subCurKycLevel = srcSub.getKYCLevelByKYCLevel();
		if(!subCurKycLevel.getKYCLevel().equals(new Long(CmFinoFIX.SubscriberKYCLevel_NoKyc))){
			log.info("KYCUpgrade: Failed as the current KycLevel is not NoKyc, Corresponding to MDN:"+sourceMdn);
			String status = enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, CmFinoFIX.Language_English, srcSub.getStatus());
			result.setStatus(status);
			result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberStatusNotValidForKYCUpgrade);
			return result;
		}
		
		Transaction transaction = null;
		
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(srcMDN.getMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(cc.getID());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_KYCUpgrade);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionsLog.getID());
		sc.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

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
		if (sctl == null) {
			log.info("Failed to create SCTL while doing KycUpgrade for MDN:"+sourceMdn);
			result.setNotificationCode(CmFinoFIX.NotificationCode_KYCUpgradeFail);
			return result;
		}
		
		kycUpgrade.setServiceChargeTransactionLogID(sctl.getID());
		result.setSctlID(sctl.getID());		
		
		SubscriberGroup subGroup = subscriberGroupService.getBySubscriberID(srcSub.getID());
		if(subGroup == null){
			log.info("Failed to get  SubscriberGroup while doing KycUpgrade for MDN:"+sourceMdn);
			transactionChargingService.failTheTransaction(sctl, MessageText._("KYC Upgrade Falied.Notification Code: "+CmFinoFIX.NotificationCode_KYCUpgradeFail));
			result.setNotificationCode(CmFinoFIX.NotificationCode_KYCUpgradeFail);
			return result;
		}
		Group group = subGroup.getGroup();
		
		PocketTemplate pkTem = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevel.getID(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, group.getID());
		
		if(pkTem == null){
			log.info("Failed to get  PocketTemplate while doing KycUpgrade for MDN:"+sourceMdn);
			transactionChargingService.failTheTransaction(sctl, MessageText._("KYC Upgrade Falied.Notification Code: "+CmFinoFIX.NotificationCode_KYCUpgradeFail));
			result.setNotificationCode(CmFinoFIX.NotificationCode_KYCUpgradeFail);
			return result;
		}
		
		Pocket srcPocket = pocketService.getDefaultPocket(srcMDN, CmFinoFIX.PocketType_SVA.toString());
		if(srcPocket == null){
			log.info("Failed to retrieve  Emoney Pocket Domian Object while doing KycUpgrade for MDN:"+sourceMdn);
			transactionChargingService.failTheTransaction(sctl, MessageText._("KYC Upgrade Falied.Notification Code: "+CmFinoFIX.NotificationCode_KYCUpgradeFail));
			result.setNotificationCode(CmFinoFIX.NotificationCode_KYCUpgradeFail);
			return result;
		}
		
		Address address = srcSub.getAddressBySubscriberAddressID();
		if(address == null){
			address = new Address();
		}
		address.setLine1(transactionDetails.getAddressLine1());
		address.setCity(city);
		address.setState(transactionDetails.getState());
		address.setZipCode(transactionDetails.getZipCode());
		srcSub.setKYCLevelByKYCLevel(kycLevel);
		srcSub.setFirstName(firstName);
		srcSub.setLastName(lastName);
		srcSub.setDateOfBirth(new Timestamp(dob));
		srcSub.setAddressBySubscriberAddressID(address);
		srcMDN.setIDType(transactionDetails.getIdType());
		srcMDN.setIDNumber(idNumber);
		srcPocket.setPocketTemplate(pkTem);
		
		sctl.setCalculatedCharge(BigDecimal.ZERO);
		transactionChargingService.completeTheTransaction(sctl);
		subscriberMdnService.saveSubscriberMDN(srcMDN);
		subscriberService.saveSubscriber(srcSub);
		pocketService.save(srcPocket);
		result.setNotificationCode(CmFinoFIX.NotificationCode_KYCUpgradeSuccess);
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		log.info(String.format("KycUpgrade is success for MDN:%s with KycLevel:%s ", sourceMdn, kycType));
		// Send SMS for KYC upgrade transaction
		sendSms(result, srcSub);
		return result;
	}
	
	/*
	 * Notifies the Subscriber after successful upgradtion by sending an SMS
	 */
	private void sendSms(XMLResult result, Subscriber subscriber) {
		NotificationQuery query = new NotificationQuery();
		query.setNotificationCode(result.getNotificationCode());
		query.setLanguage(subscriber.getLanguage());
		query.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		List<Notification> notifications = notificationService.getLanguageBasedNotificationsByQuery(query);

		if(CollectionUtils.isNotEmpty(notifications) && (!notifications.get(0).getIsActive()) ){
			log.info("SMS notification is not active, so not sending the SMS for the KYC upgrade transaction.") ;
		} 
		else {
			NotificationWrapper notificationWrapper = new NotificationWrapper(notifications.get(0));
			notificationWrapper.setCode(result.getNotificationCode());
			notificationWrapper.setFirstName(subscriber.getFirstName());
			notificationWrapper.setLastName(subscriber.getLastName());
			notificationWrapper.setLanguage(subscriber.getLanguage());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			notificationWrapper.setReceiverMDN(result.getSourceMDN());
			notificationWrapper.setSourceMDN(result.getSourceMDN());
			notificationWrapper.setKycLevel(result.getKycLevel());
			String msg = notificationMessageParserService.buildMessage(notificationWrapper,false);
			smsService.setDestinationMDN(result.getSourceMDN());
			smsService.setMessage(msg);
			smsService.setNotificationCode(notificationWrapper.getCode());
			smsService.setSctlId(result.getSctlID());
			smsService.asyncSendSMS();
		}
	}
}
