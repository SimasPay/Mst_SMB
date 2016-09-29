package com.mfino.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.crypto.CryptographyService;
import com.mfino.dao.AddressDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SubscribersAdditionalFieldsDAO;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.Address;
import com.mfino.domain.Group;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.SubscriberSyncRecord;
import com.mfino.domain.SubscribersAdditionalFields;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.exceptions.EmptyStringException;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivation;
import com.mfino.fix.CmFinoFIX.CMSubscriberActivation;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistration;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationThroughWeb;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.MailService;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.NotificationLogDetailsService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.SubscriberSyncErrors;

@Service("SubscriberServiceExtendedImpl")
public class SubscriberServiceExtendedImpl implements SubscriberServiceExtended{
	private static Logger log = LoggerFactory.getLogger(SubscriberServiceExtendedImpl.class);
	private SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
	private SubscribersAdditionalFieldsDAO subscribersAdditionalFieldsDao = DAOFactory.getInstance().getSubscribersAdditionalFieldsDAO();
	private AddressDAO addressDao = DAOFactory.getInstance().getAddressDAO();
	private SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
	private KYCLevelDAO kycLevelDAO = DAOFactory.getInstance().getKycLevelDAO();
	private PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	private SubscribersAdditionalFieldsDAO subscriberAddFieldsDAO = DAOFactory.getInstance().getSubscribersAdditionalFieldsDAO();
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("MfinoUtilServiceImpl")
	private MfinoUtilService mfinoUtilService;

	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Autowired
	@Qualifier("NotificationLogDetailsServiceImpl")
	private NotificationLogDetailsService notificationLogDetailsService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer registerSubscriber(Subscriber subscriber,
			SubscriberMDN subscriberMDN,
			CMSubscriberRegistration subscriberRegistration, Pocket epocket,
			String oneTimePin, Partner registeringPartner) {
		SubscriberMDN existingSubscriberMDN = subscriberMdnDao
				.getByMDN(subscriberRegistration.getMDN());
		boolean isUnRegistered = isRegistrationForUnRegistered(existingSubscriberMDN);
		if (existingSubscriberMDN == null || isUnRegistered) {
			if (isUnRegistered) {
				Long regPartnerID = subscriber.getRegisteringPartnerID();
				subscriberMDN = existingSubscriberMDN;
				subscriber = subscriberMDN.getSubscriber();
				subscriber.setRegisteringPartnerID(regPartnerID);
			}

			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);
			String createdByName = null;
			if (registeringPartner != null) {
				createdByName = registeringPartner.getTradeName();
			} else {
				createdByName = subscriberRegistration.getFirstName();
			}
			subscriber.setFirstName(subscriberRegistration.getFirstName());
			subscriber.setLastName(subscriberRegistration.getLastName());
			subscriber.setDateOfBirth(subscriberRegistration.getDateOfBirth());
			String mothersMaidenName = "MothersMaidenName";
			subscriber.setSecurityQuestion(mothersMaidenName);
			if (subscriberRegistration.getMothersMaidenName() != null) {
				subscriber.setSecurityAnswer(subscriberRegistration
						.getMothersMaidenName());
			}
			subscriber.setDetailsRequired(CmFinoFIX.Boolean_True);
			if (registeringPartner != null) {
				subscriber
						.setRegistrationMedium(CmFinoFIX.RegistrationMedium_Agent);
			} else {
				subscriber
						.setRegistrationMedium(CmFinoFIX.RegistrationMedium_Self);
			}

			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriber.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			subscriber.setTimezone(CmFinoFIX.Timezone_UTC);
			subscriber.setStatusTime(new Timestamp());
			subscriber.setCreatedBy(createdByName);
			subscriber.setUpdatedBy(createdByName);
			subscriber.setCreateTime(new Timestamp());
			subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
			KYCLevel kycLevel = kycLevelDAO.getByKycLevel(ConfigurationUtil
					.getIntialKyclevel());
			if (kycLevel == null ) {
				return CmFinoFIX.NotificationCode_InvalidKYCLevel;
			}
			subscriber.setKYCLevelByKYCLevel(kycLevel);
			Long groupID = null;
			Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
			if(subscriberGroups != null && !subscriberGroups.isEmpty())
			{
				SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
				groupID = subscriberGroup.getGroup().getID();
			}
			Long kycLevelNo = null;
			if(null != subscriber.getUpgradableKYCLevel())
			{
				kycLevelNo = subscriber.getUpgradableKYCLevel();
			}
			else
			{
				kycLevelNo = subscriber.getKYCLevelByKYCLevel().getKYCLevel();
			}
			PocketTemplate emoneyTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			if (emoneyTemplate == null) {
				return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
			}
			
			if (!kycLevel.getKYCLevel().equals(
					subscriberRegistration.getKYCLevel())) {
				kycLevel = kycLevelDAO.getByKycLevel(subscriberRegistration.getKYCLevel());
				if (kycLevel == null) {
					return CmFinoFIX.NotificationCode_InvalidKYCLevel;
				}
				subscriber.setUpgradableKYCLevel(subscriberRegistration
						.getKYCLevel());
				subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Upgradable);
			} else {
				subscriber.setUpgradeState(CmFinoFIX.UpgradeState_none);
			}
			int pocketStatus = CmFinoFIX.PocketStatus_Initialized;
			if (CmFinoFIX.SubscriberStatus_NotRegistered == subscriberRegistration
					.getSubscriberStatus()) {
				Long templateID = systemParametersService
						.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED);
				if (templateID > 0) {
					PocketTemplateDAO templateDAO = DAOFactory.getInstance()
							.getPocketTemplateDao();
					PocketTemplate unRegisteredTemplate = templateDAO
							.getById(templateID);
					if (unRegisteredTemplate != null) {
						emoneyTemplate = unRegisteredTemplate;
						pocketStatus = CmFinoFIX.PocketStatus_OneTimeActive;
					} else {
						log.error("ERROR: Pocket Template for Emoney Unregistered system is not found");
						return CmFinoFIX.NotificationCode_UnRegisteredPocketTemplateNotFound;
					}
				}
			}
			subscriber.setAppliedBy(createdByName);
			subscriber.setAppliedTime(new Timestamp());
			subscriber.setDetailsRequired(true);
			subscriberDao.save(subscriber);
			if(subscriber.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
				mailService.generateEmailVerificationMail(subscriber, subscriber.getEmail());				
			}
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setMDN(subscriberRegistration.getMDN());
			subscriberMDN.setApplicationID(subscriberRegistration
					.getApplicationID());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriberMDN.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriberMDN.setStatusTime(new Timestamp());
			subscriberMDN.setCreatedBy(createdByName);
			subscriberMDN.setCreateTime(new Timestamp());
			subscriberMDN.setUpdatedBy(createdByName);
			setOTPToSubscriber(subscriberMDN, oneTimePin);
			subscriberMdnDao.save(subscriberMDN);
			//handling adding default group if the group doesnot exist here
			if(groupID==null){
			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
			Group defaultGroup =groupDao.getSystemGroup();
			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
			SubscriberGroup sg = new SubscriberGroup();
			sg.setSubscriber(subscriber);
			sg.setGroup(defaultGroup);
			subscriber.getSubscriberGroupFromSubscriberID().add(sg);
			if(subscriber.getID() != null){
				subscriberGroupDao.save(sg);
			}
			}
			if (isUnRegistered) {
				Set<Pocket> pockets = subscriberMDN.getPocketFromMDNID();
				// ideally there should be only one pocket
				if (pockets.size() == 1) {
					Pocket pocket = null;
					Iterator<Pocket> pocketIterator = pockets.iterator();
					while (pocketIterator.hasNext()) {
						pocket = pocketIterator.next();
						if (pocket
								.getPocketTemplate()
								.getID()
								.equals(systemParametersService
										.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED))) {
							break;
						}
					}
					if (pocket != null) {
						pocket.setPocketTemplate(emoneyTemplate);
						pocket.setStatus(pocketStatus);
						pocketDao.save(pocket);
						return CmFinoFIX.ResponseCode_Success;
					}
				}
				return CmFinoFIX.NotificationCode_MoneySVAPocketNotFound;
			}
			boolean isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
			if(isEMoneyPocketRequired == true){
			String cardPan = null;
			try {
				cardPan = pocketService
						.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMDN());
			} catch (Exception e) {
				log.error("Cardpan creation failed", e);
			}
			epocket.setID(pocketService.createPocket(emoneyTemplate,
					subscriberMDN, pocketStatus, true, cardPan).getID());
			}
			return CmFinoFIX.ResponseCode_Success;
		}
		return CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Source;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer registerSubscriberByAgent(Subscriber subscriber, SubscriberMDN subscriberMDN, CMSubscriberRegistration subscriberRegistration, 
			Pocket lakuPandiaPocket, Partner registeringPartner, Address ktpAddress, Address domesticAddress,SubscribersAdditionalFields subscriberAddiFields) {
		
		SubscriberMDN existingSubscriberMDN = subscriberMdnDao.getByMDN(subscriberRegistration.getMDN());
		
		boolean isUnRegistered = isRegistrationForUnRegistered(existingSubscriberMDN);
		if (existingSubscriberMDN == null || isUnRegistered) {
			
			if (isUnRegistered) {
				Long regPartnerID = subscriber.getRegisteringPartnerID();
				subscriberMDN = existingSubscriberMDN;
				subscriber = subscriberMDN.getSubscriber();
				subscriber.setRegisteringPartnerID(regPartnerID);
			}

			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);
			String createdByName = null;
			
			if (registeringPartner != null) {
				
				createdByName = registeringPartner.getTradeName();
			
			} else {
				
				createdByName = subscriberRegistration.getFirstName();
			}
			
			subscriber.setFirstName(subscriberRegistration.getFirstName());
			subscriber.setLastName(subscriberRegistration.getLastName());
			subscriber.setDateOfBirth(subscriberRegistration.getDateOfBirth());
			
			String mothersMaidenName = "MothersMaidenName";
			
			subscriber.setSecurityQuestion(mothersMaidenName);
			
			if (subscriberRegistration.getMothersMaidenName() != null) {
				subscriber.setSecurityAnswer(subscriberRegistration.getMothersMaidenName());
			}
			
			subscriber.setDetailsRequired(CmFinoFIX.Boolean_True);
			
			if (registeringPartner != null) {
				subscriber.setRegistrationMedium(CmFinoFIX.RegistrationMedium_Agent);
				
			} else {
				subscriber.setRegistrationMedium(CmFinoFIX.RegistrationMedium_Self);
			}

			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				
				subscriber.setStatus(subscriberRegistration.getSubscriberStatus());
			}
			
			if(StringUtils.isNotBlank(subscriber.getEmail())) {
			
				subscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS|CmFinoFIX.NotificationMethod_Email);
				
			} else {
				
				subscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			}
			
			subscriber.setTimezone(CmFinoFIX.Timezone_UTC);
			subscriber.setStatusTime(new Timestamp());
			subscriber.setCreatedBy(createdByName);
			subscriber.setUpdatedBy(createdByName);
			subscriber.setCreateTime(new Timestamp());
			subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
			
			KYCLevel kycLevel = kycLevelDAO.getByKycLevel(ConfigurationUtil.getIntialKyclevel());
			
			if (kycLevel == null ) {
				return CmFinoFIX.NotificationCode_InvalidKYCLevel;
			}
			
			subscriber.setKYCLevelByKYCLevel(kycLevel);
			Long groupID = null;
			Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
			
			if(subscriberGroups != null && !subscriberGroups.isEmpty()){
				
				SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
				groupID = subscriberGroup.getGroup().getID();
			}
			
			Long kycLevelNo = null;
			if(null != subscriber.getUpgradableKYCLevel()){
				
				kycLevelNo = subscriber.getUpgradableKYCLevel();
			}
			else {
				
				kycLevelNo = kycLevel.getKYCLevel();
			}
			
			PocketTemplate lakuPandaiTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_LakuPandai, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			
			if (lakuPandaiTemplate == null) {
				
				return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
			}
			
			addressDao.save(ktpAddress);
			subscriber.setAddressBySubscriberAddressKTPID(ktpAddress);
			
			if(subscriberMDN.getDomAddrIdentity().equals(CmFinoFIX.DomAddrIdentity_According_to_Identity)) {
				
				subscriber.setAddressBySubscriberAddressID(ktpAddress);
				
			} else {
			
				addressDao.save(domesticAddress);
				subscriber.setAddressBySubscriberAddressID(domesticAddress);
			}
			
			subscriber.setUpgradableKYCLevel(subscriberRegistration.getKYCLevel());
			subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Upgradable);
			
			int pocketStatus = CmFinoFIX.PocketStatus_Initialized;
			if (CmFinoFIX.SubscriberStatus_NotRegistered == subscriberRegistration.getSubscriberStatus()) {
				
				Long templateID = systemParametersService.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED);
				
				if (templateID > 0) {
					PocketTemplateDAO templateDAO = DAOFactory.getInstance().getPocketTemplateDao();
					PocketTemplate unRegisteredTemplate = templateDAO.getById(templateID);
					if (unRegisteredTemplate != null) {
						lakuPandaiTemplate = unRegisteredTemplate;
						pocketStatus = CmFinoFIX.PocketStatus_OneTimeActive;
					} else {
						log.error("ERROR: Pocket Template for Emoney Unregistered system is not found");
						return CmFinoFIX.NotificationCode_UnRegisteredPocketTemplateNotFound;
					}
				}
			}
			
			subscriber.setAppliedBy(createdByName);
			subscriber.setAppliedTime(new Timestamp());
			subscriber.setDetailsRequired(true);
			subscriberDao.save(subscriber);
			
			subscriberAddiFields.setSubscriber(subscriber);
			subscriberAddFieldsDAO.save(subscriberAddiFields);
			
			if(subscriber.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
				
				mailService.generateEmailVerificationMail(subscriber, subscriber.getEmail());				
			}
			
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setMDN(subscriberRegistration.getMDN());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriberMDN.setStatus(subscriberRegistration.getSubscriberStatus());
			}
			
			subscriberMDN.setStatusTime(new Timestamp());
			subscriberMDN.setCreatedBy(createdByName);
			subscriberMDN.setCreateTime(new Timestamp());
			subscriberMDN.setUpdatedBy(createdByName);
			subscriberMdnDao.save(subscriberMDN);
			
			Long subid = subscriberMDN.getID();
            
            int cifnoLength = systemParametersService.getInteger(SystemParameterKeys.LAKUPANDIA_SUBSCRIBER_CIFNO_LENGTH);
    		String cifnoPrefix = systemParametersService.getString(SystemParameterKeys.LAKUPANDIA_SUBSCRIBER_PREFIX_CIFNO);
    		
    		if((cifnoPrefix.length() + String.valueOf(subid).length()) >= cifnoLength) {
    			
    			log.info("CIF No number length is invalid.....");
    			return CmFinoFIX.NotificationCode_SubscriberRegistrationfailed;
       		}
    		
    		String cifno = cifnoPrefix + StringUtils.leftPad(String.valueOf(subid),(cifnoLength - cifnoPrefix.length()),"0");
    		
    		subscriberMDN.setApplicationID(cifno);
    		
    		subscriberMdnDao.save(subscriberMDN);
			
			subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
			
			//handling adding default group if the group doesnot exist here
			if(groupID==null){
				
				GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
				Group defaultGroup =groupDao.getSystemGroup();
				SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
				SubscriberGroup sg = new SubscriberGroup();
				sg.setSubscriber(subscriber);
				sg.setGroup(defaultGroup);
				subscriber.getSubscriberGroupFromSubscriberID().add(sg);
				if(subscriber.getID() != null){
					subscriberGroupDao.save(sg);
				}
			}
			
			Pocket lakuPocket = null;
			lakuPocket = pocketService.createPocket(lakuPandaiTemplate,subscriberMDN, pocketStatus, true, null);
			
			if(null != lakuPocket) {
				
				lakuPandiaPocket.setID(lakuPocket.getID());
				
				String cardPan = null;
				
				try {
					
					cardPan = pocketService.generateLakupandia16DigitCardPAN(subscriberMDN.getMDN());
					
				} catch (Exception e) {
					
					log.error("Cardpan creation failed", e);
				}
				
				lakuPocket.setCardPAN(cardPan);
				pocketDao.save(lakuPocket);
			}
			
			return CmFinoFIX.ResponseCode_Success;
		}
		return CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Source;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer registerWithActivationSubscriber(
			CMSubscriberRegistrationThroughWeb subscriberRegistration) {
		SubscriberMDN existingSubscriberMDN = subscriberMdnDao
				.getByMDN(subscriberRegistration.getSourceMDN());
		
		boolean isUnRegistered = isRegistrationForUnRegistered(existingSubscriberMDN);
		
		if (existingSubscriberMDN == null || existingSubscriberMDN.getStatus().equals(CmFinoFIX.MDNStatus_NotRegistered)) {
			Subscriber subscriber = new Subscriber();
			SubscriberMDN subscriberMDN = new SubscriberMDN();
			
			if(existingSubscriberMDN == null)
			{
				subscriberMDN.setCreateTime(new Timestamp());
				subscriber.setCreateTime(new Timestamp());
			}
			else 
			{
				subscriberMDN = existingSubscriberMDN;
				subscriber = existingSubscriberMDN.getSubscriber();
			}			
			
			Address address = new Address();	
			//Set city to "Jakarta", ApplicationId to "881", Currency to "IDR" as this flow is specifically to Smart
			address.setCity("Jakarta");
			addressDao.save(address);
			subscriberMDN.setApplicationID("881");
			subscriber.setCurrency("IDR");

			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);
			String createdByName = subscriberRegistration.getFirstName();
			subscriber.setFirstName(subscriberRegistration.getFirstName());
			subscriber.setLastName(subscriberRegistration.getLastName());
			subscriber.setNickname(subscriberRegistration.getNickname());
			subscriber.setDateOfBirth(subscriberRegistration.getDateOfBirth());
			subscriber.setDetailsRequired(CmFinoFIX.Boolean_True);
			subscriber.setRegistrationMedium(CmFinoFIX.RegistrationMedium_Self);
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			subscriber.setTimezone(CmFinoFIX.Timezone_UTC);
            subscriber.setStatusTime(new Timestamp());
            if(createdByName != null)
            {
            	subscriber.setCreatedBy(createdByName);
            	subscriber.setUpdatedBy(createdByName);
            	subscriberMDN.setCreatedBy(createdByName);
            	subscriberMDN.setUpdatedBy(createdByName);    			
            }
			subscriber.setAddressBySubscriberAddressID(address);
			KYCLevel kycLevel = kycLevelDAO.getByKycLevel(subscriberRegistration.getKYCLevel());
			if (kycLevel == null ) {
				return CmFinoFIX.NotificationCode_InvalidKYCLevel;
			}
			subscriber.setKYCLevelByKYCLevel(kycLevel);
			subscriber.setUpgradeState(CmFinoFIX.UpgradeState_none);
			subscriber.setDetailsRequired(true);
			subscriber.setActivationTime(new Timestamp());
			subscriber.setStatusTime(new Timestamp());
			if (subscriberRegistration.getDateOfBirth() != null) {
				subscriber.setDateOfBirth(subscriberRegistration
						.getDateOfBirth());
			}
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setMDN(subscriberRegistration.getSourceMDN());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriberMDN.setStatusTime(new Timestamp());
			subscriberMDN.setIDType(subscriberRegistration.getIDType());
			subscriberMDN.setIDNumber(subscriberRegistration.getIDNumber());
			subscriberMDN.setOtherMDN(subscriberService.normalizeMDN(subscriberRegistration.getOtherMDN()));
			/*String digestpin = MfinoUtil.calculateDigestPin(
					subscriberMDN.getMDN(), subscriberRegistration.getPin());
			subscriberMDN.setDigestedPIN(digestpin);*/
			String newpin = null;
	 		try{
	 			newpin = subscriberRegistration.getPin();
	 			subscriberRegistration.setPin(newpin);
	 		}
	 		catch(Exception e){
	 			log.error("Exception occured while decrypting pin ");
	 			e.printStackTrace();
	 			return CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing;
	 		}
			String calcPIN = null;
			try
			{
				calcPIN = mfinoUtilService.modifyPINForStoring(subscriberMDN.getMDN(), subscriberRegistration.getPin());
			}
			catch(Exception e)
			{
				log.error("Error during PIN conversion "+e);
				return CmFinoFIX.NotificationCode_Failure;
			}
			subscriberMDN.setDigestedPIN(calcPIN);
			String authToken = MfinoUtil.calculateAuthorizationToken(subscriberMDN.getMDN(), subscriberRegistration.getPin());
			subscriberMDN.setAuthorizationToken(authToken);
			
			subscriberMDN.setActivationTime(new Timestamp());
			subscriberDao.save(subscriber);
			subscriberMdnDao.save(subscriberMDN);
			subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
						
			String cardPan = null;
			try {
				cardPan = pocketService
						.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMDN());
			} catch (Exception e) {
				log.error("Cardpan creation failed", e);
			}
			Long groupID = null;
			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
			Group defaultGroup =groupDao.getSystemGroup();
			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
			SubscriberGroup sg = new SubscriberGroup();
			sg.setSubscriber(subscriber);
			sg.setGroup(defaultGroup);
			subscriber.getSubscriberGroupFromSubscriberID().add(sg);
			if(subscriber.getID() != null){
				subscriberGroupDao.save(sg);
			}
			groupID = sg.getID();
			Long kycLevelNo = null;
			if(null != subscriber.getUpgradableKYCLevel())
			{
				kycLevelNo = subscriber.getUpgradableKYCLevel();
			}
			else
			{
				kycLevelNo = subscriber.getKYCLevelByKYCLevel().getKYCLevel();
			}
			
			PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			
			if (isUnRegistered) {
				log.info("Updating the Unregistered transfers status as Subscriver Active and changing the pocket status to Active");
				updateUnRegisteredTxnInfoToActivated(subscriberMDN);
				Set<Pocket> pockets = subscriberMDN.getPocketFromMDNID();
				// ideally there should be only one pocket
				if (pockets.size() == 1) {
					Pocket pocket = pockets.iterator().next();
					if (pocket != null) {
						pocket.setPocketTemplate(svaPocketTemplate);
						pocket.setStatus(CmFinoFIX.PocketStatus_Active);
						pocketDao.save(pocket);
						return CmFinoFIX.ResponseCode_Success;
					}
				}
				return CmFinoFIX.NotificationCode_MoneySVAPocketNotFound;
			}
			else
			{
				pocketService.createPocket(svaPocketTemplate, subscriberMDN,
						CmFinoFIX.PocketStatus_Active, true, cardPan);
			}

			return CmFinoFIX.ResponseCode_Success;
		}
		return CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Source;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer registerSubscriberThroughWeb(
			CMSubscriberRegistrationThroughWeb subscriberRegistration,
			String oneTimePin) {
		Subscriber subscriber = new Subscriber();
		SubscriberMDN subscriberMDN = new SubscriberMDN();
		SubscribersAdditionalFields subscribersAdditionalFields = new SubscribersAdditionalFields();
		Address address = new Address();
		Pocket epocket = new Pocket();
		SubscriberMDN existingSubscriberMDN = subscriberMdnDao
				.getByMDN(subscriberRegistration.getMDN());
		boolean isUnRegistered = isRegistrationForUnRegistered(existingSubscriberMDN);
		if (existingSubscriberMDN == null || isUnRegistered) {
			if (isUnRegistered) {
				Long regPartnerID = subscriber.getRegisteringPartnerID();
				subscriberMDN = existingSubscriberMDN;
				subscriber = subscriberMDN.getSubscriber();
				subscriber.setRegisteringPartnerID(regPartnerID);
			}

			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);

			address.setLine1(subscriberRegistration.getPlotNo());
			address.setLine2(subscriberRegistration.getStreetAddress());
			address.setCity(subscriberRegistration.getCity());
			address.setRegionName(subscriberRegistration.getRegionName());
			address.setCountry(subscriberRegistration.getCountry());
			addressDao.save(address);

			subscriber.setAddressBySubscriberAddressID(address);
			subscriber.setFirstName(subscriberRegistration.getFirstName());
			subscriber.setLastName(subscriberRegistration.getLastName());
			subscriber.setDateOfBirth(subscriberRegistration.getDateOfBirth());
			subscriber.setBirthPlace(subscriberRegistration.getBirthPlace());
			subscriber.setEmail(subscriberRegistration.getEmail());
			subscriber.setIsEmailVerified(false);
			subscriber.setIDExiparetionTime(subscriberRegistration
					.getIDExpiryDate());
			subscriber.setDetailsRequired(CmFinoFIX.Boolean_True);
			subscriber
					.setRegistrationMedium(CmFinoFIX.RegistrationMedium_Web);
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriber.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriber.setUpgradableKYCLevel(subscriberRegistration.getUpgradableKYCLevel());
			subscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			subscriber.setTimezone(CmFinoFIX.Timezone_UTC);
			subscriber.setStatusTime(new Timestamp());
			subscriber.setCreatedBy("Web Registration");
			subscriber.setUpdatedBy("Web Registration");
			subscriber.setCreateTime(new Timestamp());
			KYCLevel kycLevel = kycLevelDAO.getByKycLevel(ConfigurationUtil
					.getIntialKyclevel());
			if (kycLevel == null ) {
				return CmFinoFIX.NotificationCode_InvalidKYCLevel;
			}
			subscriber.setKYCLevelByKYCLevel(kycLevel);
			Long groupID = null;
			Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
			if(subscriberGroups != null && !subscriberGroups.isEmpty())
			{
				SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
				groupID = subscriberGroup.getGroup().getID();
			}
			Long kycLevelNo = null;
			if(null != subscriber.getUpgradableKYCLevel())
			{
				kycLevelNo = subscriber.getUpgradableKYCLevel();
			}
			else
			{
				kycLevelNo = subscriber.getKYCLevelByKYCLevel().getKYCLevel();
			}
			PocketTemplate emoneyTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			if (emoneyTemplate == null) {
				return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
			}

			if (!kycLevel.getKYCLevel().equals(
					subscriberRegistration.getKYCLevel())) {
				kycLevel = kycLevelDAO.getByKycLevel(subscriberRegistration
						.getKYCLevel());
				if (kycLevel == null) {
					return CmFinoFIX.NotificationCode_InvalidKYCLevel;
				}
				subscriber.setUpgradableKYCLevel(subscriberRegistration
						.getKYCLevel());
				subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Upgradable);
			} else {
				subscriber.setUpgradeState(CmFinoFIX.UpgradeState_none);
			}
			int pocketStatus = CmFinoFIX.PocketStatus_Initialized;
			if (CmFinoFIX.SubscriberStatus_NotRegistered == subscriberRegistration
					.getSubscriberStatus()) {
				Long templateID = systemParametersService
						.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED);
				if (templateID > 0) {
					PocketTemplateDAO templateDAO = DAOFactory.getInstance()
							.getPocketTemplateDao();
					PocketTemplate unRegisteredTemplate = templateDAO
							.getById(templateID);
					if (unRegisteredTemplate != null) {
						emoneyTemplate = unRegisteredTemplate;
						pocketStatus = CmFinoFIX.PocketStatus_OneTimeActive;
					} else {
						log.error("ERROR: Pocket Template for Emoney Unregistered system is not found");
						return CmFinoFIX.NotificationCode_UnRegisteredPocketTemplateNotFound;
					}
				}
			}
			subscriber.setAppliedBy("self");
			subscriber.setAppliedTime(new Timestamp());
			subscriber.setDetailsRequired(true);
			subscriberDao.save(subscriber);
			if(subscriberRegistration.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
				mailService.generateEmailVerificationMail(subscriber, subscriberRegistration.getEmail());
			}
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setMDN(subscriberRegistration.getMDN());
			subscriberMDN.setApplicationID(subscriberRegistration
					.getApplicationID());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			subscriberMDN.setIDType(subscriberRegistration.getIDType());
			subscriberMDN.setIDNumber(subscriberRegistration.getIDNumber());
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriberMDN.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriberMDN.setStatusTime(new Timestamp());
			subscriberMDN.setCreatedBy("Web Registration");
			subscriberMDN.setCreateTime(new Timestamp());
			subscriberMDN.setUpdatedBy("Web Registration");
			setOTPToSubscriber(subscriberMDN, oneTimePin);
			subscriberMdnDao.save(subscriberMDN);

			subscribersAdditionalFields.setSubscriber(subscriber);
			subscribersAdditionalFields
					.setProofofAddress(subscriberRegistration
							.getProofofAddress());
			subscribersAdditionalFields
					.setSubsCompanyName(subscriberRegistration
							.getSubsCompanyName());
			subscribersAdditionalFields
					.setCertofIncorporation(subscriberRegistration
							.getCertofIncorporation());
			subscribersAdditionalFields
					.setSubscriberMobileCompany(subscriberRegistration
							.getSubscriberMobileCompany());
			subscribersAdditionalFields.setNationality(subscriberRegistration
					.getNationality());
			subscribersAdditionalFields.setKinName(subscriberRegistration
					.getKinName());
			subscribersAdditionalFields.setKinMDN(subscriberRegistration
					.getKinMDN());
			subscribersAdditionalFieldsDao.save(subscribersAdditionalFields);
			//handling adding default group if the group doesnot exist here
			if(groupID==null){
			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
			Group defaultGroup =groupDao.getSystemGroup();
			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
			SubscriberGroup sg = new SubscriberGroup();
			sg.setSubscriber(subscriber);
			sg.setGroup(defaultGroup);
			subscriber.getSubscriberGroupFromSubscriberID().add(sg);
			if(subscriber.getID() != null){
				subscriberGroupDao.save(sg);
			}
			}
			if (isUnRegistered) {
				Set<Pocket> pockets = subscriberMDN.getPocketFromMDNID();
				// ideally there should be only one pocket
				if (pockets.size() == 1) {
					Pocket pocket = null;
					Iterator<Pocket> pocketIterator = pockets.iterator();
					while (pocketIterator.hasNext()) {
						pocket = pocketIterator.next();
						if (pocket
								.getPocketTemplate()
								.getID()
								.equals(systemParametersService
										.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED))) {
							break;
						}
					}
					if (pocket != null) {
						pocket.setPocketTemplate(emoneyTemplate);
						pocket.setStatus(pocketStatus);
						pocketDao.save(pocket);
						return CmFinoFIX.ResponseCode_Success;
					}
				}
				return CmFinoFIX.NotificationCode_MoneySVAPocketNotFound;
			}
			String cardPan = null;
			try {
				cardPan = pocketService
						.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMDN());
			} catch (Exception e) {
				log.error("Cardpan creation failed", e);
			}
			epocket.setID(pocketService.createPocket(emoneyTemplate,
					subscriberMDN, pocketStatus, true, cardPan).getID());
			return CmFinoFIX.ResponseCode_Success;
		}
		return CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Destination;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void setOTPToSubscriber(SubscriberMDN subscriberMDN,
			String oneTimePin) {
		Integer OTPTimeoutDuration = systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION);
		if (subscriberMDN != null && oneTimePin != null) {
			String digestPin1 = MfinoUtil.calculateDigestPin(
					subscriberMDN.getMDN(), oneTimePin);
			subscriberMDN.setOTP(digestPin1);
			subscriberMDN.setOTPExpirationTime(new Timestamp(DateUtil.addHours(
					new Date(), OTPTimeoutDuration)));
		}
	}

	private void fillSubscriberMDNMandatoryFields(
			SubscriberMDN subscriberMDN) {
		if (subscriberMDN.getAuthenticationPhrase() == null) {
			subscriberMDN.setAuthenticationPhrase("mFino");
		}
		if (subscriberMDN.getRestrictions() == null) {
			subscriberMDN
					.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		}
		if (subscriberMDN.getWrongPINCount() == null) {
			subscriberMDN.setWrongPINCount(0);
		}
	}

	private void fillSubscriberMandatoryFields(Subscriber subscriber) {
		if (subscriber.getmFinoServiceProviderByMSPID() == null) {
			MfinoServiceProviderDAO mfinoServiceProviderDAO = DAOFactory
					.getInstance().getMfinoServiceProviderDAO();
			subscriber.setmFinoServiceProviderByMSPID(mfinoServiceProviderDAO
					.getById(1));
		}
		subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
		if (subscriber.getCurrency() == null) {
			subscriber.setCurrency(systemParametersService
					.getString(SystemParameterKeys.DEFAULT_CURRENCY_CODE));
		}
		if (subscriber.getRestrictions() == null) {
			subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		}
		if (subscriber.getType() == null) {
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
		}
		if (subscriber.getCompany() == null) {
			CompanyDAO companyDAO = DAOFactory.getInstance().getCompanyDAO();
			subscriber.setCompany(companyDAO.getById(1));
		}

	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public int createNewSubscriber(SubscriberSyncRecord syncRecord,
			Subscriber subscriber, SubscriberMDN subscriberMDN,
			String uploadedBy) {
		try {
			Integer OTPTimeoutDuration = systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION);
			if (syncRecord.getMdn() == null) {
				return CmFinoFIX.SynchError_Failed_Invalid_MDN;
			}
			if (syncRecord.getId() != null
					&& (!subscriberMDN.getStatus().equals(
							CmFinoFIX.MDNStatus_NotRegistered))) {
				log.info("Create New subscriber failed. Subscriber MDN already exists in DB - "
						+ syncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Subscriber_already_exists;
			}
			if (syncRecord.getId() == null) {
				subscriberMDN.setMDN(syncRecord.getMdn());
			}
			// create subscriber mdn and subscriber
			KYCLevelDAO kyclevelDao = DAOFactory.getInstance().getKycLevelDAO();
			subscriber.setFirstName(syncRecord.getFirstName());
			subscriber.setLastName(syncRecord.getLastName());
			subscriber.setEmail(syncRecord.getEmail());
			subscriber.setIsEmailVerified(false);
			// subscriber.setLanguage(syncRecord.getLanguage());
			subscriber
					.setDateOfBirth(new Timestamp(syncRecord.getDateOfBirth()));
			subscriber.setKYCLevelByKYCLevel(kyclevelDao
					.getByKycLevel(ConfigurationUtil.getIntialKyclevel()));
			// subscriber.setBirthPlace(syncRecord.getPlaceOfBirth());
			// subscriber.setAliasName(syncRecord.getAliasName());
			subscriber.setType(syncRecord.getServiceType());
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			subscriber.setStatusTime(new Timestamp());
			// subscriber.setReferenceAccount(syncRecord.getReferenceACNumber());
			subscriber.setUpdatedBy(uploadedBy);
			if (syncRecord.getIdExpireDate() != null) {
				subscriber.setIDExiparetionTime(new Timestamp(syncRecord
						.getIdExpireDate()));
			}
			if (StringUtils.isNotBlank(subscriber.getEmail())) {
				subscriber
						.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS
								| CmFinoFIX.NotificationMethod_Email);
			} else {
				subscriber
						.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			}
			if(ConfigurationUtil.getLocalTimeZone()!=null){
				subscriber.setTimezone(ConfigurationUtil.getLocalTimeZone().getDisplayName());
			}
			else{
				subscriber.setTimezone(systemParametersService.getString(SystemParameterKeys.TIME_ZONE));
			}			
			subscriberMDN.setIDType(syncRecord.getIdType());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			subscriberMDN.setStatusTime(new Timestamp());
			subscriberMDN.setApplicationID(syncRecord.getApplicationId());

			Address address = subscriber.getAddressBySubscriberAddressID();
			if (address == null) {
				address = new Address();
			}
			AddressDAO addressDAO = DAOFactory.getInstance().getAddressDAO();
			address.setLine1(syncRecord.getAddress());
			address.setLine2(syncRecord.getAddressline2());
			address.setCity(syncRecord.getCity());
			address.setRegionName(syncRecord.getRegion());
			address.setCountry(syncRecord.getCountry());
			// mandatory in db remove it
			if (address.getCountry() == null) {
				address.setCountry("");
			}
			addressDAO.save(address);
			subscriber.setAddressBySubscriberAddressID(address);

			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);
			Integer OTPLength = systemParametersService.getOTPLength();
			String oneTimePin = MfinoUtil.generateOTP(OTPLength);
			String digestPin = MfinoUtil.calculateDigestPin(
					subscriberMDN.getMDN(), oneTimePin);
			syncRecord.setOneTimePin(oneTimePin);
			subscriberMDN.setOTP(digestPin);
			subscriberMDN.setOTPExpirationTime(new Timestamp(DateUtil.addHours(
					new Date(), OTPTimeoutDuration)));
		} catch (Exception e) {
			log.error("Exception while creating new subscriber", e);
			return SubscriberSyncErrors.Failure;
		}
		return SubscriberSyncErrors.Success;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer activeSubscriber(
			CMSubscriberActivation subscriberActivation, boolean isHttps) {
		
		return activeSubscriber(subscriberActivation, isHttps,false);
	}
	
	public  Integer activeSubscriber(CMSubscriberActivation subscriberActivation, boolean isHttps, boolean isHashedPin) {
		String mdn = subscriberActivation.getSourceMDN();

		SubscriberMDN subscriberMDN = subscriberMdnDao.getByMDN(subscriberService.normalizeMDN(mdn));
		
		int int_code=validateOTP(subscriberActivation, isHttps, isHashedPin);
		
		if(int_code!=CmFinoFIX.NotificationCode_OTPValidationSuccessful){
			return CmFinoFIX.NotificationCode_OTPInvalid;
		}

		String newpin = null;
 		try{
 			newpin = subscriberActivation.getPin();
 			subscriberActivation.setPin(newpin);
 		}
 		catch(Exception e){
 			log.error("Exception occured while decrypting pin ");
 			e.printStackTrace();
 			return CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing;
 		}
		if(!isHashedPin)
		{
			if (systemParametersService.getPinLength() != newpin.length()) {
				log.error("PIN length does not match the system pin's length");
				return CmFinoFIX.NotificationCode_ChangeEPINFailedInvalidPINLength;
			}
			log.info("Checking for the strength of pin XXXX for subscribermdn"
					+ mdn);
			if (!MfinoUtil.isPinStrongEnough(newpin)) {
				log.info("The pin is not strong enough for subscriber " + mdn);
				return CmFinoFIX.NotificationCode_PinNotStrongEnough;
			}
			log.info("Pin passed strength conditions for subscribermdn" + mdn);
			}
		else
		{
			log.info("Since hashed pin is enabled, pin length and pin strength checks are not performed");
		}
		Subscriber subscriber = subscriberMDN.getSubscriber();
		Set<Pocket> pockets = subscriberMDN.getPocketFromMDNID();
		Pocket bankPocket = null;
		Pocket emoneyPocket = null;
		Pocket lakupandaiPocket = null;
		boolean bankPocketFound = false;
		boolean emoneyPocketFound = false;
		boolean lakupandaiPocketFound = false;
		Long groupID = null;
		Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroup().getID();
		}
		Long kycLevelNo = null;
		if(null != subscriber.getUpgradableKYCLevel())
		{
			kycLevelNo = subscriber.getUpgradableKYCLevel();
		}
		else
		{
			kycLevelNo = subscriber.getKYCLevelByKYCLevel().getKYCLevel();
		}
		PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
		
		for (Pocket pocket : pockets) {
			if (!bankPocketFound
					&& pocket.getPocketTemplate().getType()
							.equals(CmFinoFIX.PocketType_BankAccount)
					&& pocket.getCardPAN() != null
					&& (pocket.getStatus()
							.equals(CmFinoFIX.PocketStatus_Active) || pocket
							.getStatus().equals(
									CmFinoFIX.PocketStatus_Initialized))) {
				bankPocketFound = true;
				bankPocket = pocket;
				continue;
			}
			if (!lakupandaiPocketFound
					&& pocket.getPocketTemplate().getType()
							.equals(CmFinoFIX.PocketType_LakuPandai)
					&& pocket.getCardPAN() != null
					&& (pocket.getStatus()
							.equals(CmFinoFIX.PocketStatus_Active) || pocket
							.getStatus().equals(
									CmFinoFIX.PocketStatus_Initialized))) {
				lakupandaiPocketFound = true;
				lakupandaiPocket = pocket;
				continue;
			}			
			if (!emoneyPocketFound
					&& pocket.getPocketTemplate().getType()
							.equals(CmFinoFIX.PocketType_SVA)
					&& pocket.getPocketTemplate().getCommodity()
							.equals(CmFinoFIX.Commodity_Money)
					&& (pocket.getStatus().equals(
							CmFinoFIX.PocketStatus_Initialized) || pocket
							.getStatus().equals(CmFinoFIX.PocketStatus_Active))
					&& pocket
							.getPocketTemplate()
							.getID()
							.equals(svaPocketTemplate.getID())) {
				emoneyPocketFound = true;
				emoneyPocket = pocket;
				continue;
			}
			if (emoneyPocketFound && bankPocketFound && lakupandaiPocketFound) {
				break;
			}
		}
		String subscriberName = subscriber.getFirstName();
		if (emoneyPocket != null) {
			emoneyPocket.setActivationTime(new Timestamp());
			emoneyPocket.setIsDefault(true);
			emoneyPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			emoneyPocket.setStatusTime(new Timestamp());
			emoneyPocket.setUpdatedBy(subscriberName);
		}

		if(bankPocketFound)
		{
			if (subscriber.getUpgradeState().equals(
					CmFinoFIX.UpgradeState_Approved)) {
				bankPocket.setActivationTime(new Timestamp());
				bankPocket.setIsDefault(true);
				bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
				bankPocket.setStatusTime(new Timestamp());
				bankPocket.setUpdatedBy(subscriberName);
				pocketDao.save(bankPocket);
				log.info("SubscriberActivation : bankPocket activation id:"
						+ bankPocket.getID() + " subscriberid"
						+ subscriber.getID());
			}
		}
		
		if (lakupandaiPocketFound && lakupandaiPocket != null) {
			lakupandaiPocket.setActivationTime(new Timestamp());
			lakupandaiPocket.setIsDefault(true);
			lakupandaiPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			lakupandaiPocket.setStatusTime(new Timestamp());
			lakupandaiPocket.setUpdatedBy(subscriberName);
		}

		if (CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN
				.getStatus())) {
			PocketQuery pq = new PocketQuery();
			pq.setMdnIDSearch(subscriberMDN.getID());
			List<Pocket> pocketList = pocketDao.get(pq);
			if (pocketList.size() > 0) {
				emoneyPocket = pocketList.get(0);
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
				emoneyPocket.setPocketTemplate(svaPocketTemplate);
				emoneyPocket.setStatus(CmFinoFIX.PocketStatus_Active);
				emoneyPocket.setActivationTime(new Timestamp());
				emoneyPocket.setIsDefault(true);
				emoneyPocket.setUpdatedBy(subscriberName);
			}
		}

/*		String digestpin = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(),
				newpin);
		subscriberMDN.setDigestedPIN(digestpin);
*/		
		String calcPIN = null;
		try	{
			calcPIN = mfinoUtilService.modifyPINForStoring(subscriberMDN.getMDN(), newpin);
		}
		catch(Exception e){
			log.error("Error during PIN conversion "+e);
			return CmFinoFIX.NotificationCode_Failure;
		}
		subscriberMDN.setDigestedPIN(calcPIN);
		String authToken = MfinoUtil.calculateAuthorizationToken(subscriberMDN.getMDN(), newpin);
		subscriberMDN.setAuthorizationToken(authToken);
		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriberMDN.setStatusTime(new Timestamp());
		subscriberMDN.setActivationTime(new Timestamp());
		subscriberMDN.setUpdatedBy(subscriberName);
		subscriber.setActivationTime(new Timestamp());
		subscriber.setUpdatedBy(subscriberName);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriber.setStatusTime(new Timestamp());
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
		if (subscriberActivation.getDateOfBirth() != null) {
			subscriber.setDateOfBirth(subscriberActivation.getDateOfBirth());
		}
		subscriberMDN.setOTP(null);
		subscriberMDN.setOTPExpirationTime(null);
		if (emoneyPocket != null) {
			pocketDao.save(emoneyPocket);
			log.info("SubscriberActivation : emoneyPocket with id:"
					+ emoneyPocket.getID() + " subscriberid:"
					+ subscriber.getID());
		} else {
			boolean isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
			if(isEMoneyPocketRequired == true){
			log.info("SubscriberActivation: creating emaoneyPocket for subscriberID:"
					+ subscriber.getID());
			String cardPan = "";
			try {
				cardPan = pocketService
						.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMDN());
			} catch (InvalidMDNException e) {
				log.error("", e);
			} catch (EmptyStringException e) {
				log.error("", e);
			}
			emoneyPocket = pocketService
					.createPocket(svaPocketTemplate, subscriberMDN,
							CmFinoFIX.PocketStatus_Active, true, cardPan);
			}
		}
		if (lakupandaiPocketFound && lakupandaiPocket != null) {
			pocketDao.save(lakupandaiPocket);
		}
		subscriberDao.save(subscriber);
		subscriberMdnDao.save(subscriberMDN);

		updateUnRegisteredTxnInfoToActivated(subscriberMDN);

		String smsMsg = "activation notification";
		String emailMsg = "activation notification";
		try {
			// add notifications
			NotificationWrapper notificationWrapper = new NotificationWrapper();
			notificationWrapper.setLanguage(subscriber.getLanguage());
			notificationWrapper.setFirstName(subscriber.getFirstName());
			notificationWrapper.setLastName(subscriber.getLastName());				
			notificationWrapper.setCompany(subscriber.getCompany());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_BOBPocketActivationCompleted);
			notificationWrapper.setSctlID(subscriberActivation.getServiceChargeTransactionLogID());
			notificationWrapper.setTransactionId(subscriberActivation.getTransactionID());
			smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); // use thread pool to send message
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			emailMsg = notificationMessageParserService.buildMessage(notificationWrapper,true);
		} catch (Exception e) {
			log.error("failed to generate message:", e);
		}
		String mdn1 = subscriberMDN.getMDN();
		smsService.setDestinationMDN(mdn1);
		// service.setSourceMDN(notificationWrapper.getSMSNotificationCode());
		smsService.setSctlId(subscriberActivation.getServiceChargeTransactionLogID());
		smsService.setMessage(smsMsg);
		smsService.asyncSendSMS();
		
		if ( ((subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0)
			    && subscriber.getEmail() != null && isSubscriberEmailVerified(subscriber)) {
			String email = subscriber.getEmail();
			String firstName = subscriber.getFirstName();
			Long notificationLogDetailsID = notificationLogDetailsService.persistNotification(email, "Activation", emailMsg, subscriberActivation.getServiceChargeTransactionLogID(), 
					CmFinoFIX.NotificationCode_BOBPocketActivationCompleted, CmFinoFIX.NotificationMethod_Email, CmFinoFIX.NotificationReceiverType_Source);
			mailService.asyncSendEmail(email, firstName, "Activation", emailMsg, notificationLogDetailsID);
		}
		return CmFinoFIX.NotificationCode_BOBPocketActivationCompleted;
	}


	public  Integer validateOTP(CMSubscriberActivation subscriberActivation, boolean isHttps, boolean isHashedPin) {

		String mdn = subscriberActivation.getSourceMDN();

		SubscriberMDN subscriberMDN = subscriberMdnDao.getByMDN(subscriberService.normalizeMDN(mdn));
		if (subscriberMDN == null) {
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		int int_subscriberType=subscriberMDN.getSubscriber().getType();

		if (!(CmFinoFIX.SubscriberType_Subscriber.equals(int_subscriberType)
				||CmFinoFIX.SubscriberType_Partner.equals(int_subscriberType))
				) {
			return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
		}
		if (!(CmFinoFIX.SubscriberStatus_Registered.equals(subscriberMDN
				.getStatus()) || CmFinoFIX.SubscriberStatus_Initialized
				.equals(subscriberMDN.getStatus()))) {
			if (!(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN
					.getStatus()))) {
				return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
			}
		}
		if (!(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN
				.getStatus()))
				&& subscriberMDN.getOTPExpirationTime().before(new Date())) {
			return CmFinoFIX.NotificationCode_OTPExpired;
		}

		String originalOTP =subscriberMDN.getOTP();

		
		if (!isHttps) {
			try {
				String authStr = subscriberActivation.getOTP();
				byte[] authBytes = CryptographyService.hexToBin(authStr
						.toCharArray());
				byte[] salt = { 0, 0, 0, 0, 0, 0, 0, 0 };
				byte[] decStr = CryptographyService.decryptWithPBE(authBytes,
						originalOTP.toCharArray(), salt, 20);
				String str = new String(decStr, GeneralConstants.UTF_8);
				if (!GeneralConstants.ZEROES_STRING.equals(str))
					return CmFinoFIX.NotificationCode_OTPInvalid;
				/**
				 * TODO: This needs to be fixed for hashed pin
				 */
			} catch (Exception ex) {
				log.info("OTP Check failed for the subscriber "
						+ subscriberMDN.getMDN());
				return CmFinoFIX.NotificationCode_OTPInvalid;
			}
		} else {
			String receivedOTP = subscriberActivation.getOTP();
			if (CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN
					.getStatus())) {
				boolean isValidFac = false;
				UnRegisteredTxnInfoDAO unRegisteredTxnInfoDAO = DAOFactory
						.getInstance().getUnRegisteredTxnInfoDAO();
				Integer[] status = new Integer[2];
				status[0] = CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED;
				status[1] = CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED;

				UnRegisteredTxnInfoQuery txnInfoQuery = new UnRegisteredTxnInfoQuery();
				txnInfoQuery.setSubscriberMDNID(subscriberMDN.getID());
				txnInfoQuery.setMultiStatus(status);
				List<UnregisteredTxnInfo> txnInfoList = unRegisteredTxnInfoDAO
						.get(txnInfoQuery);
				String prefix = systemParametersService
						.getString(SystemParameterKeys.FAC_PREFIX_VALUE);
				prefix = (prefix == null) ? StringUtils.EMPTY : prefix;
				int otpLength = Integer.parseInt(systemParametersService
						.getString(SystemParameterKeys.OTP_LENGTH));
				String receivedFAC = receivedOTP;
				if (receivedOTP.length() < (otpLength + prefix.length()))
					receivedFAC = prefix + receivedOTP;

				String receivedFACDigest = MfinoUtil.calculateDigestPin(
						subscriberMDN.getMDN(), receivedFAC);
				for (UnregisteredTxnInfo txnInfo : txnInfoList) {
					if (txnInfo.getDigestedPIN().equals(receivedFACDigest)) {
						isValidFac = true;
						break;
					}
				}

				if (isValidFac == true) {
					for (UnregisteredTxnInfo txnInfo : txnInfoList) {
						if (StringUtils.isBlank(txnInfo.getTransactionName())) { 
							txnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_SUBSCRIBER_ACTIVE);
						}
					}
					unRegisteredTxnInfoDAO.save(txnInfoList);
				}

				if (isValidFac == false) {
					log.info("OTP Check failed for the subscriber "
							+ subscriberMDN.getMDN());
					return CmFinoFIX.NotificationCode_OTPInvalid;
				}
			} else {
				receivedOTP = new String(
						CryptographyService
								.generateSHA256Hash(mdn, receivedOTP));
				if (!originalOTP.equals(receivedOTP)) {
					log.info("OTP Check failed for the subscriber "
							+ subscriberMDN.getMDN());
					return CmFinoFIX.NotificationCode_OTPInvalid;
				}
			}
			/**
			 * Depending on whethere hashed pin is enabled or not we get either hashed pin or normal pin
			 */
		}
	
		return CmFinoFIX.NotificationCode_OTPValidationSuccessful;
	}
	
	public NotificationWrapper generateOTPMessage(String oneTimePin, Integer notificationMethod) {
		NotificationWrapper notificationWrapper = new NotificationWrapper();
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		notificationWrapper.setLanguage(language);
		notificationWrapper.setNotificationMethod(notificationMethod);
		notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberRegistrationSuccessfulToSubscriber);
		notificationWrapper.setOneTimePin(oneTimePin);
		return notificationWrapper;
	}
	
	public NotificationWrapper generateOTPMessage(String oneTimePin, Integer notificationMethod, Integer notificationCode) {
		
		NotificationWrapper notificationWrapper = new NotificationWrapper();
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		notificationWrapper.setLanguage(language);
		notificationWrapper.setNotificationMethod(notificationMethod);
		notificationWrapper.setCode(notificationCode);
		notificationWrapper.setOneTimePin(oneTimePin);
		
		return notificationWrapper;
	}
	
	private boolean isRegistrationForUnRegistered(
			SubscriberMDN subscriberMDN) {
		boolean isUnRegistered = false;
		if (subscriberMDN != null) {
			if (CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN
					.getSubscriber().getStatus())
					&& CmFinoFIX.SubscriberStatus_NotRegistered
							.equals(subscriberMDN.getStatus())) {
				return true;
			}
		}
		return isUnRegistered;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public boolean updateUnRegisteredTxnInfoToActivated(
			SubscriberMDN subscriberMDN) {
		UnRegisteredTxnInfoDAO unRegisteredTxnInfoDAO = DAOFactory
				.getInstance().getUnRegisteredTxnInfoDAO();
		UnRegisteredTxnInfoQuery txnInfoQuery = new UnRegisteredTxnInfoQuery();
		txnInfoQuery.setSubscriberMDNID(subscriberMDN.getID());
		Integer[] status = new Integer[2];
		status[0] = CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED;
		status[1] = CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED;
		txnInfoQuery.setMultiStatus(status);
		List<UnregisteredTxnInfo> txnInfoList = unRegisteredTxnInfoDAO
				.get(txnInfoQuery);
		for (UnregisteredTxnInfo txnInfo : txnInfoList) {
			if (StringUtils.isBlank(txnInfo.getTransactionName())) {
				txnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_SUBSCRIBER_ACTIVE);
			}
		}
		unRegisteredTxnInfoDAO.save(txnInfoList);
		return true;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer ReactivateSubscriber(
			CMExistingSubscriberReactivation subscriberReactivation,
			boolean isHttps) {
		String mdn = subscriberReactivation.getSourceMDN();

		SubscriberMDN subscriberMDN = subscriberMdnDao.getByMDN(subscriberService
				.normalizeMDN(mdn));
		if (subscriberMDN == null) {
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		if (!CmFinoFIX.SubscriberType_Subscriber.equals(subscriberMDN
				.getSubscriber().getType())) {
			return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
		}
		if (!(CmFinoFIX.SubscriberStatus_Registered.equals(subscriberMDN
				.getStatus()) || CmFinoFIX.SubscriberStatus_Initialized
				.equals(subscriberMDN.getStatus()))) {
					return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
		}
		
		String newpin = null;
 		try{
 			newpin = subscriberReactivation.getNewPin();
 			subscriberReactivation.setNewPin(newpin);
 			subscriberReactivation.setConfirmPin(newpin);
 		}
 		catch(Exception e){
 			log.error("Exception occured while decrypting pin ");
 			e.printStackTrace();
 			return CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing;
 		}

		Subscriber subscriber = subscriberMDN.getSubscriber();
		Set<Pocket> pockets = subscriberMDN.getPocketFromMDNID();
		Pocket bankPocket = null;
		boolean bankPocketFound = false;
		Long groupID = null;
		Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getID();
		}
		Long kycLevelNo = null;
		if(null != subscriber.getUpgradableKYCLevel())
		{
			kycLevelNo = subscriber.getUpgradableKYCLevel();
		}
		else
		{
			kycLevelNo = subscriber.getKYCLevelByKYCLevel().getKYCLevel();
		}
		
		for (Pocket pocket : pockets) {
			if (!bankPocketFound
					&& pocket.getPocketTemplate().getType()
							.equals(CmFinoFIX.PocketType_BankAccount)
					&& pocket.getCardPAN() != null
					&& (pocket.getStatus()
							.equals(CmFinoFIX.PocketStatus_Active) || pocket
							.getStatus().equals(
									CmFinoFIX.PocketStatus_Initialized))) {
				bankPocketFound = true;
				bankPocket = pocket;
				continue;
			}
			if (bankPocketFound) {
				break;
			}
		}
		
		String subscriberName = subscriber.getFirstName();
		if(bankPocketFound)
		{
			bankPocket.setActivationTime(new Timestamp());
			bankPocket.setIsDefault(true);
			bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			bankPocket.setStatusTime(new Timestamp());
			bankPocket.setUpdatedBy(subscriberName);
			bankPocket.setCardPAN(subscriberReactivation.getSourceCardPAN());
			pocketDao.save(bankPocket);
			log.info("SubscriberActivation : bankPocket activation id:"
					+ bankPocket.getID() + " subscriberid"
					+ subscriber.getID());
		}
		String calcPIN = null;
		try
		{
			calcPIN = mfinoUtilService.modifyPINForStoring(subscriberMDN.getMDN(), newpin);
		}
		catch(Exception e)
		{
			log.error("Error during PIN conversion "+e);
			return CmFinoFIX.NotificationCode_Failure;
		}
		subscriberMDN.setDigestedPIN(calcPIN);
		
		/*String digestpin = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(),
		newpin);
		subscriberMDN.setDigestedPIN(digestpin);*/

		String authToken = MfinoUtil.calculateAuthorizationToken(subscriberMDN.getMDN(), newpin);
		subscriberMDN.setAuthorizationToken(authToken);
		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriberMDN.setStatusTime(new Timestamp());
		subscriberMDN.setActivationTime(new Timestamp());
		subscriberMDN.setUpdatedBy(subscriberName);
		subscriber.setActivationTime(new Timestamp());
		subscriber.setUpdatedBy(subscriberName);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriber.setStatusTime(new Timestamp());
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
		subscriberMDN.setOTP(null);
		subscriberMDN.setOTPExpirationTime(null);
		subscriber.setUpgradableKYCLevel(null);
		subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Approved);
		subscriber.setApproveOrRejectComment("Approved for No Emoney");
		subscriber.setApprovedOrRejectedBy("System");
		subscriber.setApproveOrRejectTime(new Timestamp());
		subscriberDao.save(subscriber);
		subscriberMdnDao.save(subscriberMDN);
		String smsMsg = "reactivation notification";
		String emailMsg = "reactivation notification";
		try {
			// add notifications
			NotificationWrapper notificationWrapper = new NotificationWrapper();
			notificationWrapper.setLanguage(subscriber.getLanguage());
			notificationWrapper.setFirstName(subscriber.getFirstName());
			notificationWrapper.setLastName(subscriber.getLastName());				
			notificationWrapper.setCompany(subscriber.getCompany());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_NewSubscriberActivation);
			notificationWrapper.setSctlID(subscriberReactivation.getServiceChargeTransactionLogID());
			notificationWrapper.setTransactionId(subscriberReactivation.getTransactionID());
			smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); // use thread pool to send message
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			emailMsg = notificationMessageParserService.buildMessage(notificationWrapper,true);
		} catch (Exception e) {
			log.error("failed to generate message:", e);
		}
		String mdn1 = subscriberMDN.getMDN();
		SMSServiceImpl service = new SMSServiceImpl();
		service.setDestinationMDN(mdn1);
		service.setMessage(smsMsg);
		service.setSctlId(subscriberReactivation.getServiceChargeTransactionLogID());
		service.asyncSendSMS();
		if (((subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0) && isSubscriberEmailVerified(subscriber)) {
			String email = subscriber.getEmail();
			String firstName = subscriber.getFirstName();
			mailService.asyncSendEmail(email, firstName, "Reactivation", emailMsg);
			Long notificationLogDetailsID = notificationLogDetailsService.persistNotification(email, "Activation", emailMsg, subscriberReactivation.getServiceChargeTransactionLogID(), 
					CmFinoFIX.NotificationCode_BOBPocketActivationCompleted, CmFinoFIX.NotificationMethod_Email, CmFinoFIX.NotificationReceiverType_Source);
			mailService.asyncSendEmail(email, firstName, "Activation", emailMsg, notificationLogDetailsID);
		}
		return CmFinoFIX.NotificationCode_BOBPocketActivationCompleted;
	}
	
	public boolean isSubscriberEmailVerified(Subscriber sub) {		
		if(systemParametersService.getIsEmailVerificationNeeded() && StringUtils.isNotBlank(sub.getEmail())) {
			return sub.getIsEmailVerified();
		}		
		return true;
	}

}
