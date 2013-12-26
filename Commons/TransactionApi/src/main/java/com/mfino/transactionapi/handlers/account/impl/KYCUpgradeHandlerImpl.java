package com.mfino.transactionapi.handlers.account.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.Address;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Group;
import com.mfino.domain.KYCLevel;
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
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.EnumTextService;
import com.mfino.service.KYCLevelService;
import com.mfino.service.PocketService;
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


	public Result handle(TransactionDetails transactionDetails) {
		String sourceMdn = transactionDetails.getSourceMDN();
		String firstName = transactionDetails.getFirstName();
		String lastName = transactionDetails.getLastName();
		String kycType = transactionDetails.getKycType();
		String city = transactionDetails.getCity();
		String transID = transactionDetails.getTransID();
		Date dob = transactionDetails.getDateOfBirth();
		
		log.info(String.format("Handling UpgradeKyc request for MDN:%s, KycType:%s, FirstName:%s, LastName:%s, City:%s, TransID:%s, dob:%s",sourceMdn, kycType, firstName, lastName, city, transID, dob.toString()));
		
		XMLResult result = new KYCUpgradeXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		CMKYCUpgrade kycUpgrade = new CMKYCUpgrade();
		ChannelCode	cc = transactionDetails.getCc();
		kycUpgrade.setFirstName(firstName);
		kycUpgrade.setLastName(lastName);
		kycUpgrade.setKYCLevel(new Long(kycType));
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
		result.setTransID(transID);
		result.setSourceMDN(sourceMdn);
		result.setKycLevel(kycType);
		
		KYCLevel kycLevel = kycLevelService.getByKycLevel(new Long(kycType));
		if(kycLevel == null){
			log.info("KYCUpgrade: Failed to get the KycLevel Domain Object Corresponding to KycType:"+kycType);
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidKYCLevel);
			return result;
		}
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
		address.setCity(city);
		srcSub.setKYCLevelByKYCLevel(kycLevel);
		srcSub.setFirstName(firstName);
		srcSub.setLastName(lastName);
		srcSub.setDateOfBirth(new Timestamp(dob));
		srcSub.setAddressBySubscriberAddressID(address);
		srcPocket.setPocketTemplate(pkTem);
		
		if (StringUtils.isNotBlank(transID)) {
			sctl.setIntegrationTransactionID(new Long(transID));
		}
		sctl.setCalculatedCharge(BigDecimal.ZERO);
		transactionChargingService.completeTheTransaction(sctl);
		subscriberMdnService.saveSubscriberMDN(srcMDN);
		subscriberService.saveSubscriber(srcSub);
		pocketService.save(srcPocket);
		result.setNotificationCode(CmFinoFIX.NotificationCode_KYCUpgradeSuccess);
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		log.info(String.format("KycUpgrade is success for MDN:%s with KycLevel:%s ", sourceMdn, kycType));
		return result;
	}
}
