package com.mfino.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.crypto.CryptographyService;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.MfinoUser;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberActivation;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.AgentService;
import com.mfino.service.MailService;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.NotificationLogDetailsService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.PasswordGenUtil;

/**
 * @author Maruthi .
 */
@Service("AgentServiceImpl")
public class AgentServiceImpl implements AgentService {
	private static Logger	        log	             = LoggerFactory.getLogger(AgentServiceImpl.class);

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;	
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("NotificationLogDetailsServiceImpl")
	private NotificationLogDetailsService notificationLogDetailsService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	@Autowired
	@Qualifier("MfinoUtilServiceImpl")
	private MfinoUtilService mfinoUtilService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public NotificationWrapper activeAgent(CMSubscriberActivation subscriberActivation,boolean isHttps) 	{
		return activeAgent(subscriberActivation, isHttps, false);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public NotificationWrapper activeAgent(CMSubscriberActivation subscriberActivation,boolean isHttps, boolean isHashedPin) {
		SubscriberMDNDAO	subscriberMdnDao	= DAOFactory.getInstance().getSubscriberMdnDAO();
		PocketDAO	    pocketDao	     = DAOFactory.getInstance().getPocketDAO();
		PartnerDAO	    partnerDao	     = DAOFactory.getInstance().getPartnerDAO();
		UserDAO	        userDao	         = DAOFactory.getInstance().getUserDAO();
		SubscriberDAO	subscriberDAO	 = DAOFactory.getInstance().getSubscriberDAO();
		
		String mdn = subscriberActivation.getSourceMDN();
		
		NotificationWrapper notificationWrapper = new NotificationWrapper();
		
		SubscriberMdn subscriberMDN = subscriberMdnDao.getByMDN(mdn);
		if (subscriberMDN == null) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_MDNNotFound);
			return notificationWrapper;
		}

		if (!CmFinoFIX.SubscriberType_Partner.equals(subscriberMDN.getSubscriber().getType())) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_MDNIsNotActive);
			return notificationWrapper;
		}

		if (!(CmFinoFIX.SubscriberStatus_Registered.equals(subscriberMDN.getStatus()) || CmFinoFIX.SubscriberStatus_Initialized.equals(subscriberMDN.getStatus()))) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation);
			return notificationWrapper;
		}
		if (!CmFinoFIX.UpgradeState_Approved.equals(subscriberMDN.getSubscriber().getUpgradestate())) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation);
			return notificationWrapper;
		}
		Partner agent = subscriberMDN.getSubscriber().getPartners().iterator().next();
		if (!partnerService.isAgentType(agent.getBusinesspartnertype().intValue())) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner);
			return notificationWrapper;
		}
		if (!(CmFinoFIX.SubscriberStatus_Registered.equals(agent.getPartnerstatus()) || CmFinoFIX.SubscriberStatus_Initialized.equals(agent.getPartnerstatus()))) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation);
			return notificationWrapper;
		}
		if (subscriberMDN.getOtpexpirationtime().before(new Date())) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_OTPExpired);
			return notificationWrapper;
		}

		String originalOTP = subscriberMDN.getOtp();
		String newpin = null;
		if(!isHttps) {
			String authStr = subscriberActivation.getOTP();
			byte[] authBytes = CryptographyService.hexToBin(authStr.toCharArray());
			byte[] salt = { 0, 0, 0, 0, 0, 0, 0, 0 };
			try {
				byte[] decStr = CryptographyService.decryptWithPBE(authBytes, originalOTP.toCharArray(), salt, 20);
				String str = new String(decStr, GeneralConstants.UTF_8);
				if (!GeneralConstants.ZEROES_STRING.equals(str)) {
				notificationWrapper.setCode(CmFinoFIX.NotificationCode_OTPInvalid);
				return notificationWrapper;
				}

				byte[] hexNewPin = CryptographyService.hexToBin(subscriberActivation.getPin().toCharArray());
				byte[] decNewPin = CryptographyService.decryptWithPBE(hexNewPin, originalOTP.toCharArray(), salt, 20);
				newpin = new String(decNewPin,GeneralConstants.UTF_8);
			}
			catch (Exception ex) {
				log.info("OTP Check failed for the subscriber "+subscriberMDN.getMdn());
				notificationWrapper.setCode(CmFinoFIX.NotificationCode_OTPInvalid);
				return notificationWrapper;
			}
		}
		else {
			String receivedOTP = subscriberActivation.getOTP();
			receivedOTP = new String(CryptographyService.generateSHA256Hash(mdn, receivedOTP));
			if(!originalOTP.equals(receivedOTP)) {
				log.info("OTP Check failed for the subscriber "+subscriberMDN.getMdn());
				notificationWrapper.setCode(CmFinoFIX.NotificationCode_OTPInvalid);
				return notificationWrapper;
			}
			newpin = subscriberActivation.getPin();
		}
		if(!isHashedPin)
		{
			if (systemParametersService.getPinLength() != newpin.length()) {
				log.error("PIN length does not match the system pin's length");
				notificationWrapper.setCode(CmFinoFIX.NotificationCode_ChangeEPINFailedInvalidPINLength);
				return notificationWrapper;
			}
		
			log.info("Checking for the strength of pin XXXX for subscribermdn "+mdn);
			if(!MfinoUtil.isPinStrongEnough(newpin)) {
				log.info("The pin is not strong enough for subscribermdn "+mdn);
				notificationWrapper.setCode(CmFinoFIX.NotificationCode_PinNotStrongEnough);
				return notificationWrapper;
			}
			log.info("Pin passed strength conditions for subscribermdn "+mdn);
		}
		else
		{
			log.info("Since hashed pin is enabled, pin length and pin strength checks are not performed");
		}
		Subscriber subscriber = subscriberMDN.getSubscriber();
		Pocket bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		Long groupID = null;
		SubscriberGroupDao sgDao = DAOFactory.getInstance().getSubscriberGroupDao();
		SubscriberGroups subscriberGroup = sgDao.getBySubscriberID(subscriber.getId().longValue());
		if(subscriberGroup != null)
		{
			groupID = subscriberGroup.getGroupid();
		}
		
		String tradeName=agent.getTradename();
		
		boolean isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
		Pocket emoneyPocket = null;
		
		if(isEMoneyPocketRequired == true){
		
			Long tempKycLevelL = subscriber.getKycLevel().getKyclevel().longValue();
			Boolean tempTrue = true;
			
			PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(tempKycLevelL, tempTrue, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, agent.getBusinesspartnertype().intValue(), groupID);
			emoneyPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), svaPocketTemplate.getId().longValue());

			if (emoneyPocket == null) {
				notificationWrapper.setCode(CmFinoFIX.NotificationCode_MoneySVAPocketNotFound);
				return notificationWrapper;
			}
			
			Long emoneyPocketStatusL = emoneyPocket.getStatus();
			Integer emoneyPocketStatusLI = emoneyPocketStatusL.intValue();
			
			Long bankPocketStatusL = bankPocket.getStatus();
			Integer bankPocketStatusLI = bankPocketStatusL.intValue();
			
			if (emoneyPocketStatusLI.equals(CmFinoFIX.PocketStatus_PendingRetirement) || emoneyPocketStatusLI.equals(CmFinoFIX.PocketStatus_Retired) || bankPocketStatusLI.equals(CmFinoFIX.PocketStatus_PendingRetirement)
			        || bankPocketStatusLI.equals(CmFinoFIX.PocketStatus_Retired)) {
				notificationWrapper.setCode(CmFinoFIX.NotificationCode_PocketStatusDoesNotEnableActivation);
				return notificationWrapper;
			}
			
			if (!emoneyPocketStatusLI.equals(CmFinoFIX.PocketStatus_Active)) {
				emoneyPocket.setActivationtime(new Timestamp());
				emoneyPocket.setIsdefault((short)Boolean.compare(true, false));
				emoneyPocket.setStatus(CmFinoFIX.PocketStatus_Active);
				emoneyPocket.setStatustime(new Timestamp());
				emoneyPocket.setUpdatedby(tradeName);
			}
		}
		
		if (bankPocket == null) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_DefaultBankAccountPocketNotFound);
			return notificationWrapper;
		}
		
		Long bankPocketStatusL = bankPocket.getStatus();
		Integer bankPocketStatusLI = bankPocketStatusL.intValue();
		
		if (!bankPocketStatusLI.equals(CmFinoFIX.PocketStatus_Active)) {
			bankPocket.setActivationtime(new Timestamp());
			bankPocket.setIsdefault((short)Boolean.compare(true, false));
			bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			bankPocket.setStatustime(new Timestamp());
			bankPocket.setUpdatedby(tradeName);
			pocketDao.save(bankPocket);
			log.info("AgentActivation : bankPocket activation id:" + bankPocket.getId() + " agentid" + agent.getId());
		}
		
		Pocket lakuPocket = null;
		
		Long tempKycLevelL = subscriber.getKycLevel().getKyclevel().longValue();
		Boolean tempTrue = true;
		
		PocketTemplate lakuPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(tempKycLevelL, tempTrue, CmFinoFIX.PocketType_LakuPandai, CmFinoFIX.SubscriberType_Partner, agent.getBusinesspartnertype().intValue(), groupID);
		lakuPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), lakuPocketTemplate.getId().longValue());

		if (lakuPocket == null) {
			
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_MoneySVAPocketNotFound);
			return notificationWrapper;
		}
		
		Long lakuPocketStatusL = emoneyPocket.getStatus();
		Integer lakuPocketStatusLI = lakuPocketStatusL.intValue();
		
		if (lakuPocketStatusLI.equals(CmFinoFIX.PocketStatus_PendingRetirement) || lakuPocketStatusLI.equals(CmFinoFIX.PocketStatus_Retired) || bankPocketStatusLI.equals(CmFinoFIX.PocketStatus_PendingRetirement)
		        || bankPocketStatusLI.equals(CmFinoFIX.PocketStatus_Retired)) {
			
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_PocketStatusDoesNotEnableActivation);
			return notificationWrapper;
		}
		
		if (!lakuPocketStatusLI.equals(CmFinoFIX.PocketStatus_Active)) {
			
			lakuPocket.setActivationtime(new Timestamp());
			lakuPocket.setIsdefault((short)Boolean.compare(true, false));
			lakuPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			lakuPocket.setStatustime(new Timestamp());
			lakuPocket.setUpdatedby(tradeName);
			pocketDao.save(lakuPocket);
			log.info("AgentActivation : lakuPocket activation id:" + lakuPocket.getId() + " agentid" + agent.getId());
		}

		String calcPIN = null;
		
		try	{
			
			calcPIN = mfinoUtilService.modifyPINForStoring(subscriberMDN.getMdn(), newpin);
		}
		catch(Exception e){
			log.error("Error during PIN conversion "+e);
		}
		subscriberMDN.setDigestedpin(calcPIN);
		
		String authToken = MfinoUtil.calculateAuthorizationToken(subscriberMDN.getMdn(), newpin);
		subscriberMDN.setAuthorizationtoken(authToken);
		
		/*String digestpin = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), newpin);
		subscriberMDN.setDigestedPIN(digestpin);*/
		
		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriberMDN.setStatustime(new Timestamp());
		subscriberMDN.setActivationtime(new Timestamp());
		subscriberMDN.setUpdatedby(tradeName);
		subscriber.setUpdatedby(tradeName);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriber.setStatustime(new Timestamp());
		subscriber.setActivationtime(new Timestamp());
		subscriberMDN.setOtp(null);
		subscriberMDN.setOtpexpirationtime(null);
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
		if (emoneyPocket != null) {
			pocketDao.save(emoneyPocket);
			log.info("AgentActivation : emoneyPocket with id:" + emoneyPocket.getId() + " agent:" + agent.getId());
		}

		agent.setPartnerstatus(CmFinoFIX.SubscriberStatus_Active);
		MfinoUser user = agent.getMfinoUser();
		user.setStatus(CmFinoFIX.UserStatus_Active);
		user.setStatustime(new Timestamp());
		user.setFailedlogincount(0);
		user.setFirsttimelogin((short) 1);
		user.setUseractivationtime(new Timestamp());
		user.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		String password = PasswordGenUtil.generate();
		PasswordEncoder encoder = new ShaPasswordEncoder(1);
		String encPassword = encoder.encodePassword(password, user.getUsername());
		user.setPassword(encPassword);
		String emailSubject = "Partner Registration";
		String emailMsg = partnerService.genratePartnerRegistrationMail(agent, user, agent.getBusinesspartnertype().intValue(), password);
		String email = agent.getAuthorizedemail();
		String to = agent.getTradename();
		partnerService.activateServices(agent);
		partnerService.activateNonTransactionable(subscriberMDN);
		userDao.save(user);
		partnerDao.save(agent);
		subscriberDAO.save(subscriber);
		subscriberMdnDao.save(subscriberMDN);
		subscriberServiceExtended.updateUnRegisteredTxnInfoToActivated(subscriberMDN);
//		mailService.asyncSendEmail(email, to, emailSubject, emailMsg);//commented for SMP-93
		String smsMsg = "activation notification";
		try {
			//add notifications
			Long languageL = subscriber.getLanguage();
			Integer languageLI = languageL.intValue();
		
			notificationWrapper.setLanguage(languageLI);
			notificationWrapper.setCompany(subscriber.getCompany());
			notificationWrapper.setFirstName(subscriber.getFirstname());
			notificationWrapper.setLastName(subscriber.getLastname());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			notificationWrapper.setPartnerCode(agent.getPartnercode());
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_PartnerActivationCompleted);//partneractivation
			smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); //use thread pool to send message
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			emailMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); 
		}
		catch (Exception e) {
			log.error("failed to generate message:", e);
		}
		String mdn1 = subscriberMDN.getMdn();
		smsService.setDestinationMDN(mdn1);
		// service.setSourceMDN(notificationWrapper.getSMSNotificationCode());
		smsService.setMessage(smsMsg);
		smsService.setSctlId(subscriberActivation.getServiceChargeTransactionLogID());
		smsService.asyncSendSMS();
		if (agent.getAuthorizedemail() != null) {
			String email2 = agent.getAuthorizedemail();
			String firstName = subscriber.getFirstname();
			Long notificationLogDetailsID = notificationLogDetailsService.persistNotification(email2, "Activation", emailMsg, subscriberActivation.getServiceChargeTransactionLogID(),
					CmFinoFIX.NotificationCode_BOBPocketActivationCompleted, CmFinoFIX.NotificationMethod_Email, CmFinoFIX.NotificationReceiverType_Source);
			mailService.asyncSendEmail(email2, firstName, "Activation", emailMsg, notificationLogDetailsID);
		}
		
		notificationWrapper.setCode(CmFinoFIX.NotificationCode_PartnerActivationCompleted);
		return notificationWrapper;
	}

}
