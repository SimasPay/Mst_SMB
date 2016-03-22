package com.mfino.service.impl;

import java.util.Date;
import java.util.Set;

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
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
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
		
		SubscriberMDN subscriberMDN = subscriberMdnDao.getByMDN(mdn);
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
		if (!CmFinoFIX.UpgradeState_Approved.equals(subscriberMDN.getSubscriber().getUpgradeState())) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation);
			return notificationWrapper;
		}
		Partner agent = subscriberMDN.getSubscriber().getPartnerFromSubscriberID().iterator().next();
		if (!partnerService.isAgentType(agent.getBusinessPartnerType())) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner);
			return notificationWrapper;
		}
		if (!(CmFinoFIX.SubscriberStatus_Registered.equals(agent.getPartnerStatus()) || CmFinoFIX.SubscriberStatus_Initialized.equals(agent.getPartnerStatus()))) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation);
			return notificationWrapper;
		}
		if (subscriberMDN.getOTPExpirationTime().before(new Date())) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_OTPExpired);
			return notificationWrapper;
		}

		String originalOTP = subscriberMDN.getOTP();
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
				log.info("OTP Check failed for the subscriber "+subscriberMDN.getMDN());
				notificationWrapper.setCode(CmFinoFIX.NotificationCode_OTPInvalid);
				return notificationWrapper;
			}
		}
		else {
			String receivedOTP = subscriberActivation.getOTP();
			receivedOTP = new String(CryptographyService.generateSHA256Hash(mdn, receivedOTP));
			if(!originalOTP.equals(receivedOTP)) {
				log.info("OTP Check failed for the subscriber "+subscriberMDN.getMDN());
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
		Pocket bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getID(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		Long groupID = null;
		Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroup().getID();
		}
		PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(subscriber.getKYCLevelByKYCLevel().getKYCLevel(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, agent.getBusinessPartnerType(), groupID);
		Pocket emoneyPocket = subscriberService.getDefaultPocket(subscriberMDN.getID(), svaPocketTemplate.getID());

		if (emoneyPocket == null) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_MoneySVAPocketNotFound);
			return notificationWrapper;
		}
		if (bankPocket == null) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_DefaultBankAccountPocketNotFound);
			return notificationWrapper;
		}
		if (emoneyPocket.getStatus().equals(CmFinoFIX.PocketStatus_PendingRetirement) || emoneyPocket.getStatus().equals(CmFinoFIX.PocketStatus_Retired) || bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_PendingRetirement)
		        || bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_Retired)) {
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_PocketStatusDoesNotEnableActivation);
			return notificationWrapper;
		}
		String tradeName=agent.getTradeName();
		if (!emoneyPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)) {
			emoneyPocket.setActivationTime(new Timestamp());
			emoneyPocket.setIsDefault(true);
			emoneyPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			emoneyPocket.setStatusTime(new Timestamp());
			emoneyPocket.setUpdatedBy(tradeName);
		}
		if (!bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)) {
			bankPocket.setActivationTime(new Timestamp());
			bankPocket.setIsDefault(true);
			bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			bankPocket.setStatusTime(new Timestamp());
			bankPocket.setUpdatedBy(tradeName);
			pocketDao.save(bankPocket);
			log.info("AgentActivation : bankPocket activation id:" + bankPocket.getID() + " agentid" + agent.getID());
		}

		String calcPIN = null;
		
		try	{
			
			calcPIN = mfinoUtilService.modifyPINForStoring(subscriberMDN.getMDN(), newpin);
		}
		catch(Exception e){
			log.error("Error during PIN conversion "+e);
		}
		subscriberMDN.setDigestedPIN(calcPIN);
		
		String authToken = MfinoUtil.calculateAuthorizationToken(subscriberMDN.getMDN(), newpin);
		subscriberMDN.setAuthorizationToken(authToken);
		
		/*String digestpin = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), newpin);
		subscriberMDN.setDigestedPIN(digestpin);*/
		
		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriberMDN.setStatusTime(new Timestamp());
		subscriberMDN.setActivationTime(new Timestamp());
		subscriberMDN.setUpdatedBy(tradeName);
		subscriber.setUpdatedBy(tradeName);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriber.setStatusTime(new Timestamp());
		subscriber.setActivationTime(new Timestamp());
		subscriberMDN.setOTP(null);
		subscriberMDN.setOTPExpirationTime(null);
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
		if (emoneyPocket != null) {
			pocketDao.save(emoneyPocket);
			log.info("AgentActivation : emoneyPocket with id:" + emoneyPocket.getID() + " agent:" + agent.getID());
		}

		agent.setPartnerStatus(CmFinoFIX.SubscriberStatus_Active);
		User user = agent.getUser();
		user.setStatus(CmFinoFIX.UserStatus_Active);
		user.setStatusTime(new Timestamp());
		user.setFailedLoginCount(0);
		user.setFirstTimeLogin(true);
		user.setUserActivationTime(new Timestamp());
		user.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		String password = PasswordGenUtil.generate();
		PasswordEncoder encoder = new ShaPasswordEncoder(1);
		String encPassword = encoder.encodePassword(password, user.getUsername());
		user.setPassword(encPassword);
		String emailSubject = "Partner Registration";
		String emailMsg = partnerService.genratePartnerRegistrationMail(agent, user, agent.getBusinessPartnerType(), password);
		String email = agent.getAuthorizedEmail();
		String to = agent.getTradeName();
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
		
			notificationWrapper.setLanguage(subscriber.getLanguage());
			notificationWrapper.setCompany(subscriber.getCompany());
			notificationWrapper.setFirstName(subscriber.getFirstName());
			notificationWrapper.setLastName(subscriber.getLastName());
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			notificationWrapper.setPartnerCode(agent.getPartnerCode());
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_PartnerActivationCompleted);//partneractivation
			smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); //use thread pool to send message
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			emailMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); 
		}
		catch (Exception e) {
			log.error("failed to generate message:", e);
		}
		String mdn1 = subscriberMDN.getMDN();
		smsService.setDestinationMDN(mdn1);
		// service.setSourceMDN(notificationWrapper.getSMSNotificationCode());
		smsService.setMessage(smsMsg);
		smsService.setSctlId(subscriberActivation.getServiceChargeTransactionLogID());
		smsService.asyncSendSMS();
		if (agent.getAuthorizedEmail() != null) {
			String email2 = agent.getAuthorizedEmail();
			String firstName = subscriber.getFirstName();
			Long notificationLogDetailsID = notificationLogDetailsService.persistNotification(email2, "Activation", emailMsg, subscriberActivation.getServiceChargeTransactionLogID(),
					CmFinoFIX.NotificationCode_BOBPocketActivationCompleted, CmFinoFIX.NotificationMethod_Email, CmFinoFIX.NotificationReceiverType_Source);
			mailService.asyncSendEmail(email2, firstName, "Activation", emailMsg, notificationLogDetailsID);
		}
		
		notificationWrapper.setCode(CmFinoFIX.NotificationCode_PartnerActivationCompleted);
		return notificationWrapper;
	}

}
