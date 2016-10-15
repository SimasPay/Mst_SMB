package com.mfino.service.impl;

import java.math.BigDecimal;
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
import com.mfino.domain.Groups;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberAddiInfo;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberSyncRecord;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.exceptions.EmptyStringException;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivation;
import com.mfino.fix.CmFinoFIX.CMSubscriberActivation;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistration;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationThroughWeb;
import com.mfino.hibernate.Timestamp;
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
	private SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
	
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
			SubscriberMdn subscriberMDN,
			CMSubscriberRegistration subscriberRegistration, Pocket epocket,
			String oneTimePin, Partner registeringPartner) {
		SubscriberMdn existingSubscriberMDN = subscriberMdnDao
				.getByMDN(subscriberRegistration.getMDN());
		boolean isUnRegistered = isRegistrationForUnRegistered(existingSubscriberMDN);
		if (existingSubscriberMDN == null || isUnRegistered) {
			if (isUnRegistered) {
				Long regPartnerID = subscriber.getRegisteringpartnerid().longValue();
				subscriberMDN = existingSubscriberMDN;
				subscriber = subscriberMDN.getSubscriber();
				subscriber.setRegisteringpartnerid(regPartnerID);
			}

			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);
			String createdByName = null;
			if (registeringPartner != null) {
				createdByName = registeringPartner.getTradename();
			} else {
				createdByName = subscriberRegistration.getFirstName();
			}
			subscriber.setFirstname(subscriberRegistration.getFirstName());
			subscriber.setLastname(subscriberRegistration.getLastName());
			subscriber.setDateofbirth(subscriberRegistration.getDateOfBirth());
			String mothersMaidenName = "MothersMaidenName";
			subscriber.setSecurityquestion(mothersMaidenName);
			if (subscriberRegistration.getMothersMaidenName() != null) {
				subscriber.setSecurityanswer(subscriberRegistration
						.getMothersMaidenName());
			}
			subscriber.setDetailsrequired((short) CmFinoFIX.Boolean_True.compareTo(true));
			if (registeringPartner != null) {
				subscriber
						.setRegistrationmedium(CmFinoFIX.RegistrationMedium_Agent.longValue());
			} else {
				subscriber
						.setRegistrationmedium(CmFinoFIX.RegistrationMedium_Self.longValue());
			}

			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriber.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS.longValue());
			subscriber.setTimezone(CmFinoFIX.Timezone_UTC);
			subscriber.setStatustime(new Timestamp());
			subscriber.setCreatedby(createdByName);
			subscriber.setUpdatedby(createdByName);
			subscriber.setCreatetime(new Timestamp());
			subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
			KycLevel kycLevel = kycLevelDAO.getByKycLevel(ConfigurationUtil
					.getIntialKyclevel());
			if (kycLevel == null ) {
				return CmFinoFIX.NotificationCode_InvalidKYCLevel;
			}
			subscriber.setKycLevel(kycLevel);
			Long groupID = null;
			List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(BigDecimal.valueOf(subscriber.getId()));
			if(subscriberGroups != null && !subscriberGroups.isEmpty())
			{
				SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
				groupID = subscriberGroup.getGroupid();
			}
			Long kycLevelNo = null;
			if(null != subscriber.getUpgradablekyclevel())
			{
				kycLevelNo = subscriber.getUpgradablekyclevel().longValue();
			}
			else
			{
				kycLevelNo = subscriber.getKycLevel().getKyclevel().longValue();
			}
			PocketTemplate emoneyTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			if (emoneyTemplate == null) {
				return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
			}
			
			if (!kycLevel.getKyclevel().equals(
					subscriberRegistration.getKYCLevel())) {
				kycLevel = kycLevelDAO.getByKycLevel(subscriberRegistration.getKYCLevel());
				if (kycLevel == null) {
					return CmFinoFIX.NotificationCode_InvalidKYCLevel;
				}
				subscriber.setUpgradablekyclevel(new BigDecimal(subscriberRegistration.getKYCLevel()));
				subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Upgradable.longValue());
			} else {
				subscriber.setUpgradestate(CmFinoFIX.UpgradeState_none.longValue());
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
			subscriber.setAppliedby(createdByName);
			subscriber.setAppliedtime(new Timestamp());
			subscriber.setDetailsrequired((short) Boolean.compare(true, false));
			subscriberDao.save(subscriber);
			if(subscriber.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
				mailService.generateEmailVerificationMail(subscriber, subscriber.getEmail());				
			}
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setMdn(subscriberRegistration.getMDN());
			subscriberMDN.setApplicationid(subscriberRegistration
					.getApplicationID());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriberMDN.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriberMDN.setStatustime(new Timestamp());
			subscriberMDN.setCreatedby(createdByName);
			subscriberMDN.setCreatetime(new Timestamp());
			subscriberMDN.setUpdatedby(createdByName);
			setOTPToSubscriber(subscriberMDN, oneTimePin);
			subscriberMdnDao.save(subscriberMDN);
			//handling adding default group if the group doesnot exist here
			if(groupID==null){
			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
			Groups defaultGroup =groupDao.getSystemGroup();
			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
			SubscriberGroups sg = new SubscriberGroups();
			sg.setSubscriberid(subscriber.getId().longValue());
			sg.setGroupid(defaultGroup.getId().longValue());
			if(subscriber.getId() != null){
				subscriberGroupDao.save(sg);
			}
			}
			if (isUnRegistered) {
				Set<Pocket> pockets = subscriberMDN.getPockets();
				// ideally there should be only one pocket
				if (pockets.size() == 1) {
					Pocket pocket = null;
					Iterator<Pocket> pocketIterator = pockets.iterator();
					while (pocketIterator.hasNext()) {
						pocket = pocketIterator.next();
						if (pocket
								.getPocketTemplateByPockettemplateid()
								.getId()
								.equals(systemParametersService
										.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED))) {
							break;
						}
					}
					if (pocket != null) {
						pocket.setPocketTemplateByPockettemplateid(emoneyTemplate);
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
						.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMdn());
			} catch (Exception e) {
				log.error("Cardpan creation failed", e);
			}
			epocket.setId(pocketService.createPocket(emoneyTemplate,
					subscriberMDN, pocketStatus, true, cardPan).getId());
			}
			return CmFinoFIX.ResponseCode_Success;
		}
		return CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Source;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer registerSubscriberByAgent(Subscriber subscriber, SubscriberMdn subscriberMDN, CMSubscriberRegistration subscriberRegistration, 
			Pocket lakuPandiaPocket, Partner registeringPartner, Address ktpAddress, Address domesticAddress,SubscriberAddiInfo subscriberAddiFields) {
		
		SubscriberMdn existingSubscriberMDN = subscriberMdnDao.getByMDN(subscriberRegistration.getMDN());
		
		boolean isUnRegistered = isRegistrationForUnRegistered(existingSubscriberMDN);
		if (existingSubscriberMDN == null || isUnRegistered) {
			
			if (isUnRegistered) {
				Long regPartnerID = subscriber.getRegisteringpartnerid().longValue();
				subscriberMDN = existingSubscriberMDN;
				subscriber = subscriberMDN.getSubscriber();
				subscriber.setRegisteringpartnerid(regPartnerID);
			}

			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);
			String createdByName = null;
			
			if (registeringPartner != null) {
				
				createdByName = registeringPartner.getTradename();
			
			} else {
				
				createdByName = subscriberRegistration.getFirstName();
			}
			
			subscriber.setFirstname(subscriberRegistration.getFirstName());
			subscriber.setLastname(subscriberRegistration.getLastName());
			subscriber.setDateofbirth(subscriberRegistration.getDateOfBirth());
			
			String mothersMaidenName = "MothersMaidenName";
			
			subscriber.setSecurityquestion(mothersMaidenName);
			
			if (subscriberRegistration.getMothersMaidenName() != null) {
				subscriber.setSecurityanswer(subscriberRegistration.getMothersMaidenName());
			}
			
			subscriber.setDetailsrequired((short) CmFinoFIX.Boolean_True.compareTo(true));
			
			if (registeringPartner != null) {
				subscriber.setRegistrationmedium(CmFinoFIX.RegistrationMedium_Agent.longValue());
				
			} else {
				subscriber.setRegistrationmedium(CmFinoFIX.RegistrationMedium_Self.longValue());
			}

			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				
				subscriber.setStatus(subscriberRegistration.getSubscriberStatus());
			}
			
			if(StringUtils.isNotBlank(subscriber.getEmail())) {
			
				subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS.longValue()|CmFinoFIX.NotificationMethod_Email.longValue());
				
			} else {
				
				subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS.longValue());
			}
			
			subscriber.setTimezone(CmFinoFIX.Timezone_UTC);
			subscriber.setStatustime(new Timestamp());
			subscriber.setCreatedby(createdByName);
			subscriber.setUpdatedby(createdByName);
			subscriber.setCreatetime(new Timestamp());
			subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
			
			KycLevel kycLevel = kycLevelDAO.getByKycLevel(ConfigurationUtil.getIntialKyclevel());
			
			if (kycLevel == null ) {
				return CmFinoFIX.NotificationCode_InvalidKYCLevel;
			}
			
			subscriber.setKycLevel(kycLevel);
			Long groupID = null;
			List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(BigDecimal.valueOf(subscriber.getId()));
			
			if(subscriberGroups != null && !subscriberGroups.isEmpty()){
				
				SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
				groupID = subscriberGroup.getGroupid();
			}
			
			Long kycLevelNo = null;
			if(null != subscriber.getUpgradablekyclevel()){
				
				kycLevelNo = subscriber.getUpgradablekyclevel().longValue();
			}
			else {
				
				kycLevelNo = kycLevel.getKyclevel().longValue();
			}
			
			PocketTemplate lakuPandaiTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_LakuPandai, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			
			if (lakuPandaiTemplate == null) {
				
				return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
			}
			
			addressDao.save(ktpAddress);
			subscriber.setAddressBySubscriberaddressktpid(ktpAddress);
			
			if(subscriberMDN.getDomaddridentity().equals(CmFinoFIX.DomAddrIdentity_According_to_Identity)) {
				
				subscriber.setAddressBySubscriberaddressid(ktpAddress);
				
			} else {
			
				addressDao.save(domesticAddress);
				subscriber.setAddressBySubscriberaddressid(domesticAddress);
			}
			
			subscriber.setUpgradablekyclevel(new BigDecimal(subscriberRegistration.getKYCLevel()));
			subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Upgradable.longValue());
			
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
			
			subscriber.setAppliedby(createdByName);
			subscriber.setAppliedtime(new Timestamp());
			subscriber.setDetailsrequired((short) Boolean.compare(true, false));
			subscriberDao.save(subscriber);
			
			subscriberAddiFields.setSubscriber(subscriber);
			subscriberAddFieldsDAO.save(subscriberAddiFields);
			
			if(subscriber.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
				
				mailService.generateEmailVerificationMail(subscriber, subscriber.getEmail());				
			}
			
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setMdn(subscriberRegistration.getMDN());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriberMDN.setStatus(subscriberRegistration.getSubscriberStatus());
			}
			
			subscriberMDN.setStatustime(new Timestamp());
			subscriberMDN.setCreatedby(createdByName);
			subscriberMDN.setCreatetime(new Timestamp());
			subscriberMDN.setUpdatedby(createdByName);
			subscriberMdnDao.save(subscriberMDN);
			
			Long subid = subscriberMDN.getId().longValue();
            
            int cifnoLength = systemParametersService.getInteger(SystemParameterKeys.LAKUPANDIA_SUBSCRIBER_CIFNO_LENGTH);
    		String cifnoPrefix = systemParametersService.getString(SystemParameterKeys.LAKUPANDIA_SUBSCRIBER_PREFIX_CIFNO);
    		
    		if((cifnoPrefix.length() + String.valueOf(subid).length()) >= cifnoLength) {
    			
    			log.info("CIF No number length is invalid.....");
    			return CmFinoFIX.NotificationCode_SubscriberRegistrationfailed;
       		}
    		
    		String cifno = cifnoPrefix + StringUtils.leftPad(String.valueOf(subid),(cifnoLength - cifnoPrefix.length()),"0");
    		
    		subscriberMDN.setApplicationid(cifno);
    		
    		subscriberMdnDao.save(subscriberMDN);
			
			subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
			
			//handling adding default group if the group doesnot exist here
			if(groupID==null){
				
				GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
				Groups defaultGroup =groupDao.getSystemGroup();
				SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
				SubscriberGroups sg = new SubscriberGroups();
				sg.setSubscriberid(subscriber.getId().longValue());
				sg.setGroupid(defaultGroup.getId().longValue());
				if(subscriber.getId() != null){
					subscriberGroupDao.save(sg);
				}
			}
			
			Pocket lakuPocket = null;
			lakuPocket = pocketService.createPocket(lakuPandaiTemplate,subscriberMDN, pocketStatus, true, null);
			
			if(null != lakuPocket) {
				
				lakuPandiaPocket.setId(lakuPocket.getId());
				
				String cardPan = null;
				
				try {
					
					cardPan = pocketService.generateLakupandia16DigitCardPAN(subscriberMDN.getMdn());
					
				} catch (Exception e) {
					
					log.error("Cardpan creation failed", e);
				}
				
				lakuPocket.setCardpan(cardPan);
				pocketDao.save(lakuPocket);
			}
			
			return CmFinoFIX.ResponseCode_Success;
		}
		return CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Source;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer registerWithActivationSubscriber(
			CMSubscriberRegistrationThroughWeb subscriberRegistration) {
		SubscriberMdn existingSubscriberMDN = subscriberMdnDao
				.getByMDN(subscriberRegistration.getSourceMDN());
		
		boolean isUnRegistered = isRegistrationForUnRegistered(existingSubscriberMDN);
		Long tempL = existingSubscriberMDN.getStatus();
		Integer tempLI = tempL.intValue();
		if (existingSubscriberMDN == null || tempLI.equals(CmFinoFIX.MDNStatus_NotRegistered)) {
			Subscriber subscriber = new Subscriber();
			SubscriberMdn subscriberMDN = new SubscriberMdn();
			
			if(existingSubscriberMDN == null)
			{
				subscriberMDN.setCreatetime(new Timestamp());
				subscriber.setCreatetime(new Timestamp());
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
			subscriberMDN.setApplicationid("881");
			subscriber.setCurrency("IDR");

			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);
			String createdByName = subscriberRegistration.getFirstName();
			subscriber.setFirstname(subscriberRegistration.getFirstName());
			subscriber.setLastname(subscriberRegistration.getLastName());
			subscriber.setNickname(subscriberRegistration.getNickname());
			subscriber.setDateofbirth(subscriberRegistration.getDateOfBirth());
			subscriber.setDetailsrequired((short) CmFinoFIX.Boolean_True.compareTo(true));
			subscriber.setRegistrationmedium(CmFinoFIX.RegistrationMedium_Self.longValue());
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS.longValue());
			subscriber.setTimezone(CmFinoFIX.Timezone_UTC);
            subscriber.setStatustime(new Timestamp());
            if(createdByName != null)
            {
            	subscriber.setCreatedby(createdByName);
            	subscriber.setUpdatedby(createdByName);
            	subscriberMDN.setCreatedby(createdByName);
            	subscriberMDN.setUpdatedby(createdByName);    			
            }
			subscriber.setAddressBySubscriberaddressid(address);
			KycLevel kycLevel = kycLevelDAO.getByKycLevel(subscriberRegistration.getKYCLevel());
			if (kycLevel == null ) {
				return CmFinoFIX.NotificationCode_InvalidKYCLevel;
			}
			subscriber.setKycLevel(kycLevel);
			subscriber.setUpgradestate(CmFinoFIX.UpgradeState_none.longValue());
			subscriber.setDetailsrequired((short) Boolean.compare(true, false));
			subscriber.setActivationtime(new Timestamp());
			subscriber.setStatustime(new Timestamp());
			if (subscriberRegistration.getDateOfBirth() != null) {
				subscriber.setDateofbirth(subscriberRegistration
						.getDateOfBirth());
			}
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setMdn(subscriberRegistration.getSourceMDN());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriberMDN.setStatustime(new Timestamp());
			subscriberMDN.setIdtype(subscriberRegistration.getIDType());
			subscriberMDN.setIdnumber(subscriberRegistration.getIDNumber());
			subscriberMDN.setOthermdn(subscriberService.normalizeMDN(subscriberRegistration.getOtherMDN()));
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
				calcPIN = mfinoUtilService.modifyPINForStoring(subscriberMDN.getMdn(), subscriberRegistration.getPin());
			}
			catch(Exception e)
			{
				log.error("Error during PIN conversion "+e);
				return CmFinoFIX.NotificationCode_Failure;
			}
			subscriberMDN.setDigestedpin(calcPIN);
			String authToken = MfinoUtil.calculateAuthorizationToken(subscriberMDN.getMdn(), subscriberRegistration.getPin());
			subscriberMDN.setAuthorizationtoken(authToken);
			
			subscriberMDN.setActivationtime(new Timestamp());
			subscriberDao.save(subscriber);
			subscriberMdnDao.save(subscriberMDN);
			subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
						
			String cardPan = null;
			try {
				cardPan = pocketService
						.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMdn());
			} catch (Exception e) {
				log.error("Cardpan creation failed", e);
			}
			Long groupID = null;
			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
			Groups defaultGroup =groupDao.getSystemGroup();
			SubscriberGroups sg = new SubscriberGroups();
			sg.setSubscriberid(subscriber.getId().longValue());
			sg.setGroupid(defaultGroup.getId().longValue());
			if(subscriber.getId() != null){
				subscriberGroupDao.save(sg);
			}
			groupID = sg.getId().longValue();
			Long kycLevelNo = null;
			if(null != subscriber.getUpgradablekyclevel())
			{
				kycLevelNo = subscriber.getUpgradablekyclevel().longValue();
			}
			else
			{
				kycLevelNo = subscriber.getKycLevel().getKyclevel().longValue();
			}
			
			PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			
			if (isUnRegistered) {
				log.info("Updating the Unregistered transfers status as Subscriver Active and changing the pocket status to Active");
				updateUnRegisteredTxnInfoToActivated(subscriberMDN);
				Set<Pocket> pockets = subscriberMDN.getPockets();
				// ideally there should be only one pocket
				if (pockets.size() == 1) {
					Pocket pocket = pockets.iterator().next();
					if (pocket != null) {
						pocket.setPocketTemplateByPockettemplateid(svaPocketTemplate);
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
		SubscriberMdn subscriberMDN = new SubscriberMdn();
		SubscriberAddiInfo subscribersAdditionalFields = new SubscriberAddiInfo();
		Address address = new Address();
		Pocket epocket = new Pocket();
		SubscriberMdn existingSubscriberMDN = subscriberMdnDao
				.getByMDN(subscriberRegistration.getMDN());
		boolean isUnRegistered = isRegistrationForUnRegistered(existingSubscriberMDN);
		if (existingSubscriberMDN == null || isUnRegistered) {
			if (isUnRegistered) {
				Long regPartnerID = subscriber.getRegisteringpartnerid().longValue();
				subscriberMDN = existingSubscriberMDN;
				subscriber = subscriberMDN.getSubscriber();
				subscriber.setRegisteringpartnerid(regPartnerID);
			}

			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);

			address.setLine1(subscriberRegistration.getPlotNo());
			address.setLine2(subscriberRegistration.getStreetAddress());
			address.setCity(subscriberRegistration.getCity());
			address.setRegionname(subscriberRegistration.getRegionName());
			address.setCountry(subscriberRegistration.getCountry());
			addressDao.save(address);

			subscriber.setAddressBySubscriberaddressid(address);
			subscriber.setFirstname(subscriberRegistration.getFirstName());
			subscriber.setLastname(subscriberRegistration.getLastName());
			subscriber.setDateofbirth(subscriberRegistration.getDateOfBirth());
			subscriber.setBirthplace(subscriberRegistration.getBirthPlace());
			subscriber.setEmail(subscriberRegistration.getEmail());
			subscriber.setIsemailverified((short) Boolean.compare(false, true));
			subscriber.setIdexiparetiontime(subscriberRegistration
					.getIDExpiryDate());
			subscriber.setDetailsrequired((short) CmFinoFIX.Boolean_True.compareTo(true));
			subscriber
					.setRegistrationmedium(CmFinoFIX.RegistrationMedium_Web.longValue());
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriber.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriber.setUpgradablekyclevel(new BigDecimal(subscriberRegistration.getUpgradableKYCLevel()));
			subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS.longValue());
			subscriber.setTimezone(CmFinoFIX.Timezone_UTC);
			subscriber.setStatustime(new Timestamp());
			subscriber.setCreatedby("Web Registration");
			subscriber.setUpdatedby("Web Registration");
			subscriber.setCreatetime(new Timestamp());
			KycLevel kycLevel = kycLevelDAO.getByKycLevel(ConfigurationUtil
					.getIntialKyclevel());
			if (kycLevel == null ) {
				return CmFinoFIX.NotificationCode_InvalidKYCLevel;
			}
			subscriber.setKycLevel(kycLevel);
			Long groupID = null;
			List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(BigDecimal.valueOf(subscriber.getId()));
			if(subscriberGroups != null && !subscriberGroups.isEmpty())
			{
				SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
				groupID = subscriberGroup.getGroupid();
			}
			Long kycLevelNo = null;
			if(null != subscriber.getUpgradablekyclevel())
			{
				kycLevelNo = subscriber.getUpgradablekyclevel().longValue();
			}
			else
			{
				kycLevelNo = subscriber.getKycLevel().getKyclevel().longValue();
			}
			PocketTemplate emoneyTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			if (emoneyTemplate == null) {
				return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
			}

			if (!kycLevel.getKyclevel().equals(
					subscriberRegistration.getKYCLevel())) {
				kycLevel = kycLevelDAO.getByKycLevel(subscriberRegistration
						.getKYCLevel());
				if (kycLevel == null) {
					return CmFinoFIX.NotificationCode_InvalidKYCLevel;
				}
				subscriber.setUpgradablekyclevel(new BigDecimal(subscriberRegistration
						.getKYCLevel()));
				subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Upgradable.longValue());
			} else {
				subscriber.setUpgradestate(CmFinoFIX.UpgradeState_none.longValue());
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
			subscriber.setAppliedby("self");
			subscriber.setAppliedtime(new Timestamp());
			subscriber.setDetailsrequired((short) Boolean.compare(true, false));
			subscriberDao.save(subscriber);
			if(subscriberRegistration.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
				mailService.generateEmailVerificationMail(subscriber, subscriberRegistration.getEmail());
			}
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setMdn(subscriberRegistration.getMDN());
			subscriberMDN.setApplicationid(subscriberRegistration
					.getApplicationID());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			subscriberMDN.setIdtype(subscriberRegistration.getIDType());
			subscriberMDN.setIdnumber(subscriberRegistration.getIDNumber());
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriberMDN.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriberMDN.setStatustime(new Timestamp());
			subscriberMDN.setCreatedby("Web Registration");
			subscriberMDN.setCreatetime(new Timestamp());
			subscriberMDN.setUpdatedby("Web Registration");
			setOTPToSubscriber(subscriberMDN, oneTimePin);
			subscriberMdnDao.save(subscriberMDN);

			subscribersAdditionalFields.setSubscriber(subscriber);
			subscribersAdditionalFields
					.setProofofaddress(subscriberRegistration
							.getProofofAddress());
			subscribersAdditionalFields
					.setSubscompanyname(subscriberRegistration
							.getSubsCompanyName());
			subscribersAdditionalFields
					.setCertofincorporation(subscriberRegistration
							.getCertofIncorporation());
			subscribersAdditionalFields
					.setSubscribermobilecompany(subscriberRegistration
							.getSubscriberMobileCompany());
			subscribersAdditionalFields.setNationality(subscriberRegistration
					.getNationality());
			subscribersAdditionalFields.setKinname(subscriberRegistration
					.getKinName());
			subscribersAdditionalFields.setKinmdn(subscriberRegistration
					.getKinMDN());
			subscribersAdditionalFieldsDao.save(subscribersAdditionalFields);
			//handling adding default group if the group doesnot exist here
			if(groupID==null){
			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
			Groups defaultGroup =groupDao.getSystemGroup();
			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
			SubscriberGroups sg = new SubscriberGroups();
			sg.setSubscriberid(subscriber.getId().longValue());
			sg.setGroupid(defaultGroup.getId().longValue());
			if(subscriber.getId() != null){
				subscriberGroupDao.save(sg);
			}
			}
			if (isUnRegistered) {
				Set<Pocket> pockets = subscriberMDN.getPockets();
				// ideally there should be only one pocket
				if (pockets.size() == 1) {
					Pocket pocket = null;
					Iterator<Pocket> pocketIterator = pockets.iterator();
					while (pocketIterator.hasNext()) {
						pocket = pocketIterator.next();
						if (pocket
								.getPocketTemplateByPockettemplateid()
								.getId()
								.equals(systemParametersService
										.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED))) {
							break;
						}
					}
					if (pocket != null) {
						pocket.setPocketTemplateByPockettemplateid(emoneyTemplate);
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
						.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMdn());
			} catch (Exception e) {
				log.error("Cardpan creation failed", e);
			}
			epocket.setId(pocketService.createPocket(emoneyTemplate,
					subscriberMDN, pocketStatus, true, cardPan).getId());
			return CmFinoFIX.ResponseCode_Success;
		}
		return CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Destination;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void setOTPToSubscriber(SubscriberMdn subscriberMDN,
			String oneTimePin) {
		Integer OTPTimeoutDuration = systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION);
		if (subscriberMDN != null && oneTimePin != null) {
			String digestPin1 = MfinoUtil.calculateDigestPin(
					subscriberMDN.getMdn(), oneTimePin);
			subscriberMDN.setOtp(digestPin1);
			subscriberMDN.setOtpexpirationtime(new Timestamp(DateUtil.addHours(
					new Date(), OTPTimeoutDuration)));
		}
	}

	private void fillSubscriberMDNMandatoryFields(
			SubscriberMdn subscriberMDN) {
		if (subscriberMDN.getAuthenticationphrase() == null) {
			subscriberMDN.setAuthenticationphrase("mFino");
		}
		Long tempL = subscriberMDN.getRestrictions();
		Long tempWrPinCtL = subscriberMDN.getWrongpincount();
		
		if ( tempL== null) {
			subscriberMDN
					.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		}
		if ( tempWrPinCtL== null) {
			subscriberMDN.setWrongpincount(0);
		}
	}

	private void fillSubscriberMandatoryFields(Subscriber subscriber) {
		if (subscriber.getMfinoServiceProvider() == null) {
			MfinoServiceProviderDAO mfinoServiceProviderDAO = DAOFactory
					.getInstance().getMfinoServiceProviderDAO();
			subscriber.setMfinoServiceProvider(mfinoServiceProviderDAO
					.getById(1));
		}
		subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
		if (subscriber.getCurrency() == null) {
			subscriber.setCurrency(systemParametersService
					.getString(SystemParameterKeys.DEFAULT_CURRENCY_CODE));
		}
		Long tempL = subscriber.getRestrictions();
		Long tempX = subscriber.getType();
		if (tempL == null) {
			subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		}
		if ( tempX== null) {
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
		}
		if (subscriber.getCompany() == null) {
			CompanyDAO companyDAO = DAOFactory.getInstance().getCompanyDAO();
			subscriber.setCompany(companyDAO.getById(1));
		}

	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public int createNewSubscriber(SubscriberSyncRecord syncRecord,
			Subscriber subscriber, SubscriberMdn subscriberMDN,
			String uploadedBy) {
		try {
			Integer OTPTimeoutDuration = systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION);
			if (syncRecord.getMdn() == null) {
				return CmFinoFIX.SynchError_Failed_Invalid_MDN;
			}
			Long subsMDNL = subscriberMDN.getStatus();
			Integer subsMDNLI = subsMDNL.intValue();
			
			if (syncRecord.getId() != null
					&& (!subsMDNLI.equals(
							CmFinoFIX.MDNStatus_NotRegistered))) {
				log.info("Create New subscriber failed. Subscriber MDN already exists in DB - "
						+ syncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Subscriber_already_exists;
			}
			if (syncRecord.getId() == null) {
				subscriberMDN.setMdn(syncRecord.getMdn());
			}
			// create subscriber mdn and subscriber
			KYCLevelDAO kyclevelDao = DAOFactory.getInstance().getKycLevelDAO();
			subscriber.setFirstname(syncRecord.getFirstName());
			subscriber.setLastname(syncRecord.getLastName());
			subscriber.setEmail(syncRecord.getEmail());
			subscriber.setIsemailverified((short) Boolean.compare(false, true));
			// subscriber.setLanguage(syncRecord.getLanguage());
			subscriber
					.setDateofbirth(new Timestamp(syncRecord.getDateOfBirth()));
			subscriber.setKycLevel(kyclevelDao
					.getByKycLevel(ConfigurationUtil.getIntialKyclevel()));
			// subscriber.setBirthPlace(syncRecord.getPlaceOfBirth());
			// subscriber.setAliasName(syncRecord.getAliasName());
			subscriber.setType(syncRecord.getServiceType());
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			subscriber.setStatustime(new Timestamp());
			// subscriber.setReferenceAccount(syncRecord.getReferenceACNumber());
			subscriber.setUpdatedby(uploadedBy);
			if (syncRecord.getIdExpireDate() != null) {
				subscriber.setIdexiparetiontime(new Timestamp(syncRecord
						.getIdExpireDate()));
			}
			if (StringUtils.isNotBlank(subscriber.getEmail())) {
				subscriber
						.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS.longValue()
								| CmFinoFIX.NotificationMethod_Email.longValue());
			} else {
				subscriber
						.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS.longValue());
			}
			if(ConfigurationUtil.getLocalTimeZone()!=null){
				subscriber.setTimezone(ConfigurationUtil.getLocalTimeZone().getDisplayName());
			}
			else{
				subscriber.setTimezone(systemParametersService.getString(SystemParameterKeys.TIME_ZONE));
			}			
			subscriberMDN.setIdtype(syncRecord.getIdType());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			subscriberMDN.setStatustime(new Timestamp());
			subscriberMDN.setApplicationid(syncRecord.getApplicationId());

			Address address = subscriber.getAddressBySubscriberaddressid();
			if (address == null) {
				address = new Address();
			}
			AddressDAO addressDAO = DAOFactory.getInstance().getAddressDAO();
			address.setLine1(syncRecord.getAddress());
			address.setLine2(syncRecord.getAddressline2());
			address.setCity(syncRecord.getCity());
			address.setRegionname(syncRecord.getRegion());
			address.setCountry(syncRecord.getCountry());
			// mandatory in db remove it
			if (address.getCountry() == null) {
				address.setCountry("");
			}
			addressDAO.save(address);
			subscriber.setAddressBySubscriberaddressid(address);

			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);
			Integer OTPLength = systemParametersService.getOTPLength();
			String oneTimePin = MfinoUtil.generateOTP(OTPLength);
			String digestPin = MfinoUtil.calculateDigestPin(
					subscriberMDN.getMdn(), oneTimePin);
			syncRecord.setOneTimePin(oneTimePin);
			subscriberMDN.setOtp(digestPin);
			subscriberMDN.setOtpexpirationtime(new Timestamp(DateUtil.addHours(
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

		SubscriberMdn subscriberMDN = subscriberMdnDao.getByMDN(subscriberService.normalizeMDN(mdn));
		
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
		Set<Pocket> pockets = subscriberMDN.getPockets();
		Pocket bankPocket = null;
		Pocket emoneyPocket = null;
		Pocket lakupandaiPocket = null;
		boolean bankPocketFound = false;
		boolean emoneyPocketFound = false;
		boolean lakupandaiPocketFound = false;
		Long groupID = null;
		List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(BigDecimal.valueOf(subscriber.getId()));
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroupid();
		}
		Long kycLevelNo = null;
		if(null != subscriber.getUpgradablekyclevel())
		{
			kycLevelNo = subscriber.getUpgradablekyclevel().longValue();
		}
		else
		{
			kycLevelNo = subscriber.getKycLevel().getKyclevel().longValue();
		}
		PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevelNo, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
		
		for (Pocket pocket : pockets) {
			Long tempPocketTemplateL = pocket.getPocketTemplateByPockettemplateid().getType();
			Integer tempPocketTemplateLI = tempPocketTemplateL.intValue();
			
			Long tempPocketSTatusL = pocket.getPocketTemplateByPockettemplateid().getType();
			Integer tempPocketSTatusLI = tempPocketSTatusL.intValue();
			
			if (!bankPocketFound
					&& tempPocketTemplateLI
							.equals(CmFinoFIX.PocketType_BankAccount)
					&& pocket.getCardpan() != null
					&& (tempPocketSTatusLI
							.equals(CmFinoFIX.PocketStatus_Active) || tempPocketSTatusLI.equals(
									CmFinoFIX.PocketStatus_Initialized))) {
				bankPocketFound = true;
				bankPocket = pocket;
				continue;
			}
			if (!lakupandaiPocketFound
					&& tempPocketTemplateLI
							.equals(CmFinoFIX.PocketType_LakuPandai)
					&& pocket.getCardpan() != null
					&& (tempPocketSTatusLI
							.equals(CmFinoFIX.PocketStatus_Active) || tempPocketSTatusLI.equals(
									CmFinoFIX.PocketStatus_Initialized))) {
				lakupandaiPocketFound = true;
				lakupandaiPocket = pocket;
				continue;
			}			
			
			Long tempPocketComodityL = pocket.getPocketTemplateByPockettemplateid().getCommodity();
			Integer tempPocketComodityLI = tempPocketComodityL.intValue();
			
			if (!emoneyPocketFound
					&& tempPocketTemplateLI
							.equals(CmFinoFIX.PocketType_SVA)
					&& tempPocketComodityLI
							.equals(CmFinoFIX.Commodity_Money)
					&& (tempPocketSTatusLI.equals(
							CmFinoFIX.PocketStatus_Initialized) || tempPocketSTatusLI.equals(CmFinoFIX.PocketStatus_Active))
					&& pocket
							.getPocketTemplateByPockettemplateid()
							.getId()
							.equals(svaPocketTemplate.getId())) {
				emoneyPocketFound = true;
				emoneyPocket = pocket;
				continue;
			}
			if (emoneyPocketFound && bankPocketFound && lakupandaiPocketFound) {
				break;
			}
		}
		String subscriberName = subscriber.getFirstname();
		if (emoneyPocket != null) {
			emoneyPocket.setActivationtime(new Timestamp());
			emoneyPocket.setIsdefault((short) Boolean.compare(true, false));
			emoneyPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			emoneyPocket.setStatustime(new Timestamp());
			emoneyPocket.setUpdatedby(subscriberName);
		}

		if(bankPocketFound)
		{
			if (subscriber.getUpgradestate().equals(
					CmFinoFIX.UpgradeState_Approved)) {
				bankPocket.setActivationtime(new Timestamp());
				bankPocket.setIsdefault((short) Boolean.compare(true, false));
				bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
				bankPocket.setStatustime(new Timestamp());
				bankPocket.setUpdatedby(subscriberName);
				pocketDao.save(bankPocket);
				log.info("SubscriberActivation : bankPocket activation id:"
						+ bankPocket.getId() + " subscriberid"
						+ subscriber.getId());
			}
		}
		
		if (lakupandaiPocketFound && lakupandaiPocket != null) {
			lakupandaiPocket.setActivationtime(new Timestamp());
			lakupandaiPocket.setIsdefault((short) Boolean.compare(true, false));
			lakupandaiPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			lakupandaiPocket.setStatustime(new Timestamp());
			lakupandaiPocket.setUpdatedby(subscriberName);
		}

		if (CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN
				.getStatus())) {
			PocketQuery pq = new PocketQuery();
			pq.setMdnIDSearch(subscriberMDN.getId().longValue());
			List<Pocket> pocketList = pocketDao.get(pq);
			if (pocketList.size() > 0) {
				emoneyPocket = pocketList.get(0);
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
				emoneyPocket.setPocketTemplateByPockettemplateid(svaPocketTemplate);
				emoneyPocket.setStatus(CmFinoFIX.PocketStatus_Active);
				emoneyPocket.setActivationtime(new Timestamp());
				emoneyPocket.setIsdefault((short) Boolean.compare(true, false));
				emoneyPocket.setUpdatedby(subscriberName);
			}
		}

/*		String digestpin = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(),
				newpin);
		subscriberMDN.setDigestedPIN(digestpin);
*/		
		String calcPIN = null;
		try	{
			calcPIN = mfinoUtilService.modifyPINForStoring(subscriberMDN.getMdn(), newpin);
		}
		catch(Exception e){
			log.error("Error during PIN conversion "+e);
			return CmFinoFIX.NotificationCode_Failure;
		}
		subscriberMDN.setDigestedpin(calcPIN);
		String authToken = MfinoUtil.calculateAuthorizationToken(subscriberMDN.getMdn(), newpin);
		subscriberMDN.setAuthorizationtoken(authToken);
		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriberMDN.setStatustime(new Timestamp());
		subscriberMDN.setActivationtime(new Timestamp());
		subscriberMDN.setUpdatedby(subscriberName);
		subscriber.setActivationtime(new Timestamp());
		subscriber.setUpdatedby(subscriberName);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriber.setStatustime(new Timestamp());
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
		if (subscriberActivation.getDateOfBirth() != null) {
			subscriber.setDateofbirth(subscriberActivation.getDateOfBirth());
		}
		subscriberMDN.setOtp(null);
		subscriberMDN.setOtpexpirationtime(null);
		if (emoneyPocket != null) {
			pocketDao.save(emoneyPocket);
			log.info("SubscriberActivation : emoneyPocket with id:"
					+ emoneyPocket.getId() + " subscriberid:"
					+ subscriber.getId());
		} else {
			boolean isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
			if(isEMoneyPocketRequired == true){
			log.info("SubscriberActivation: creating emaoneyPocket for subscriberID:"
					+ subscriber.getId());
			String cardPan = "";
			try {
				cardPan = pocketService
						.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMdn());
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
			
			Long tempLanguage = subscriber.getLanguage();
			Integer tempLanguageI = tempLanguage.intValue();
			
			notificationWrapper.setLanguage(tempLanguageI);
			notificationWrapper.setFirstName(subscriber.getFirstname());
			notificationWrapper.setLastName(subscriber.getLastname());				
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
		String mdn1 = subscriberMDN.getMdn();
		smsService.setDestinationMDN(mdn1);
		// service.setSourceMDN(notificationWrapper.getSMSNotificationCode());
		smsService.setSctlId(subscriberActivation.getServiceChargeTransactionLogID());
		smsService.setMessage(smsMsg);
		smsService.asyncSendSMS();
		
		if ( ((subscriber.getNotificationmethod() & CmFinoFIX.NotificationMethod_Email) > 0)
			    && subscriber.getEmail() != null && isSubscriberEmailVerified(subscriber)) {
			String email = subscriber.getEmail();
			String firstName = subscriber.getFirstname();
			Long notificationLogDetailsID = notificationLogDetailsService.persistNotification(email, "Activation", emailMsg, subscriberActivation.getServiceChargeTransactionLogID(), 
					CmFinoFIX.NotificationCode_BOBPocketActivationCompleted, CmFinoFIX.NotificationMethod_Email, CmFinoFIX.NotificationReceiverType_Source);
			mailService.asyncSendEmail(email, firstName, "Activation", emailMsg, notificationLogDetailsID);
		}
		return CmFinoFIX.NotificationCode_BOBPocketActivationCompleted;
	}


	public  Integer validateOTP(CMSubscriberActivation subscriberActivation, boolean isHttps, boolean isHashedPin) {

		String mdn = subscriberActivation.getSourceMDN();

		SubscriberMdn subscriberMDN = subscriberMdnDao.getByMDN(subscriberService.normalizeMDN(mdn));
		if (subscriberMDN == null) {
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		
		Long tempSubType = subscriberMDN.getSubscriber().getType();
		
		int int_subscriberType=tempSubType.intValue();

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
				&& subscriberMDN.getOtpexpirationtime().before(new Date())) {
			return CmFinoFIX.NotificationCode_OTPExpired;
		}

		String originalOTP =subscriberMDN.getOtp();

		
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
						+ subscriberMDN.getMdn());
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
				txnInfoQuery.setSubscriberMDNID(subscriberMDN.getId().longValue());
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
						subscriberMDN.getMdn(), receivedFAC);
				for (UnregisteredTxnInfo txnInfo : txnInfoList) {
					if (txnInfo.getDigestedpin().equals(receivedFACDigest)) {
						isValidFac = true;
						break;
					}
				}

				if (isValidFac == true) {
					for (UnregisteredTxnInfo txnInfo : txnInfoList) {
						if (StringUtils.isBlank(txnInfo.getTransactionname())) { 
							txnInfo.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_SUBSCRIBER_ACTIVE.longValue());
						}
					}
					unRegisteredTxnInfoDAO.save(txnInfoList);
				}

				if (isValidFac == false) {
					log.info("OTP Check failed for the subscriber "
							+ subscriberMDN.getMdn());
					return CmFinoFIX.NotificationCode_OTPInvalid;
				}
			} else {
				receivedOTP = new String(
						CryptographyService
								.generateSHA256Hash(mdn, receivedOTP));
				if (!originalOTP.equals(receivedOTP)) {
					log.info("OTP Check failed for the subscriber "
							+ subscriberMDN.getMdn());
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
			SubscriberMdn subscriberMDN) {
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
			SubscriberMdn subscriberMDN) {
		UnRegisteredTxnInfoDAO unRegisteredTxnInfoDAO = DAOFactory
				.getInstance().getUnRegisteredTxnInfoDAO();
		UnRegisteredTxnInfoQuery txnInfoQuery = new UnRegisteredTxnInfoQuery();
		txnInfoQuery.setSubscriberMDNID(subscriberMDN.getId().longValue());
		Integer[] status = new Integer[2];
		status[0] = CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED;
		status[1] = CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED;
		txnInfoQuery.setMultiStatus(status);
		List<UnregisteredTxnInfo> txnInfoList = unRegisteredTxnInfoDAO
				.get(txnInfoQuery);
		for (UnregisteredTxnInfo txnInfo : txnInfoList) {
			if (StringUtils.isBlank(txnInfo.getTransactionname())) {
				txnInfo.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_SUBSCRIBER_ACTIVE.longValue());
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

		SubscriberMdn subscriberMDN = subscriberMdnDao.getByMDN(subscriberService
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
		Set<Pocket> pockets = subscriberMDN.getPockets();
		Pocket bankPocket = null;
		boolean bankPocketFound = false;
		Long groupID = null;
		List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(BigDecimal.valueOf(subscriber.getId()));
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getId().longValue();
		}
		Long kycLevelNo = null;
		if(null != subscriber.getUpgradablekyclevel())
		{
			kycLevelNo = subscriber.getUpgradablekyclevel().longValue();
		}
		else
		{
			kycLevelNo = subscriber.getKycLevel().getKyclevel().longValue();
		}
		
		for (Pocket pocket : pockets) {
			Long pocketTypeL = pocket.getPocketTemplateByPockettemplateid().getType();
			Integer pocketTypeLI = pocketTypeL.intValue();
			
			Long pocketStatusL = pocket.getStatus();
			Integer pocketStatusLI = pocketStatusL.intValue();
			
			if (!bankPocketFound
					&& pocketTypeLI
							.equals(CmFinoFIX.PocketType_BankAccount)
					&& pocket.getCardpan() != null
					&& (pocketStatusLI
							.equals(CmFinoFIX.PocketStatus_Active) || pocketStatusLI.equals(
									CmFinoFIX.PocketStatus_Initialized))) {
				bankPocketFound = true;
				bankPocket = pocket;
				continue;
			}
			if (bankPocketFound) {
				break;
			}
		}
		
		String subscriberName = subscriber.getFirstname();
		if(bankPocketFound)
		{
			bankPocket.setActivationtime(new Timestamp());
			bankPocket.setIsdefault((short) Boolean.compare(true, false));
			bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			bankPocket.setStatustime(new Timestamp());
			bankPocket.setUpdatedby(subscriberName);
			bankPocket.setCardpan(subscriberReactivation.getSourceCardPAN());
			pocketDao.save(bankPocket);
			log.info("SubscriberActivation : bankPocket activation id:"
					+ bankPocket.getId() + " subscriberid"
					+ subscriber.getId());
		}
		String calcPIN = null;
		try
		{
			calcPIN = mfinoUtilService.modifyPINForStoring(subscriberMDN.getMdn(), newpin);
		}
		catch(Exception e)
		{
			log.error("Error during PIN conversion "+e);
			return CmFinoFIX.NotificationCode_Failure;
		}
		subscriberMDN.setDigestedpin(calcPIN);
		
		/*String digestpin = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(),
		newpin);
		subscriberMDN.setDigestedPIN(digestpin);*/

		String authToken = MfinoUtil.calculateAuthorizationToken(subscriberMDN.getMdn(), newpin);
		subscriberMDN.setAuthorizationtoken(authToken);
		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriberMDN.setStatustime(new Timestamp());
		subscriberMDN.setActivationtime(new Timestamp());
		subscriberMDN.setUpdatedby(subscriberName);
		subscriber.setActivationtime(new Timestamp());
		subscriber.setUpdatedby(subscriberName);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriber.setStatustime(new Timestamp());
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
		subscriberMDN.setOtp(null);
		subscriberMDN.setOtpexpirationtime(null);
		subscriber.setUpgradablekyclevel(null);
		subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Approved.longValue());
		subscriber.setApproveorrejectcomment("Approved for No Emoney");
		subscriber.setApprovedorrejectedby("System");
		subscriber.setApproveorrejecttime(new Timestamp());
		subscriberDao.save(subscriber);
		subscriberMdnDao.save(subscriberMDN);
		String smsMsg = "reactivation notification";
		String emailMsg = "reactivation notification";
		try {
			// add notifications
			Long tempLanguageL = subscriber.getLanguage();
			
			NotificationWrapper notificationWrapper = new NotificationWrapper();
			notificationWrapper.setLanguage(tempLanguageL.intValue());
			notificationWrapper.setFirstName(subscriber.getFirstname());
			notificationWrapper.setLastName(subscriber.getLastname());				
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
		String mdn1 = subscriberMDN.getMdn();
		SMSServiceImpl service = new SMSServiceImpl();
		service.setDestinationMDN(mdn1);
		service.setMessage(smsMsg);
		service.setSctlId(subscriberReactivation.getServiceChargeTransactionLogID());
		service.asyncSendSMS();
		if (((subscriber.getNotificationmethod() & CmFinoFIX.NotificationMethod_Email) > 0) && isSubscriberEmailVerified(subscriber)) {
			String email = subscriber.getEmail();
			String firstName = subscriber.getFirstname();
			mailService.asyncSendEmail(email, firstName, "Reactivation", emailMsg);
			Long notificationLogDetailsID = notificationLogDetailsService.persistNotification(email, "Activation", emailMsg, subscriberReactivation.getServiceChargeTransactionLogID(), 
					CmFinoFIX.NotificationCode_BOBPocketActivationCompleted, CmFinoFIX.NotificationMethod_Email, CmFinoFIX.NotificationReceiverType_Source);
			mailService.asyncSendEmail(email, firstName, "Activation", emailMsg, notificationLogDetailsID);
		}
		return CmFinoFIX.NotificationCode_BOBPocketActivationCompleted;
	}
	
	public boolean isSubscriberEmailVerified(Subscriber sub) {		
		if(systemParametersService.getIsEmailVerificationNeeded() && StringUtils.isNotBlank(sub.getEmail())) {
			return Boolean.valueOf(sub.getIsemailverified().toString());
		}		
		return true;
	}

}
