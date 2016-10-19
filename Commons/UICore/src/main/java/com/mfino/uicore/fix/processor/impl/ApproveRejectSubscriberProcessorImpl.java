/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SMSValues;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSApproveRejectSubscriber;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.AuthorizationService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.PocketTemplateService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.ApproveRejectSubscriberProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.SubscriberSyncErrors;

/**
 * 
 * @author Raju
 */
@Service("ApproveRejectSubscriberProcessorImpl")
public class ApproveRejectSubscriberProcessorImpl extends BaseFixProcessor implements ApproveRejectSubscriberProcessor{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
	private static SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
	private static PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	private static KYCLevelDAO kycLevelDao = DAOFactory.getInstance().getKycLevelDAO();
	private  boolean isEMoneyPocketRequired;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;
	
	@Autowired
	@Qualifier("PocketTemplateServiceImpl")
	private PocketTemplateService pocketTemplateService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		if (!authorizationService.isAuthorized(CmFinoFIX.Permission_Subscriber_Approve)) {
			log.info("You are not authorized to perform this operation");
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}

		CMJSApproveRejectSubscriber realMsg = (CMJSApproveRejectSubscriber) msg;
		log.info("Procesing the Request of" + realMsg.getSubscriberMDNID());
		if (realMsg.getSubscriberMDNID() == null) {
			log.info("subscriberMDNid is null");
			errorMsg.setErrorDescription(MessageText._("Invalid mdn status"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}

		SubscriberMdn subscriberMDN = subscriberMdnDao.getById(realMsg.getSubscriberMDNID());

		if (null == subscriberMDN) {
			log.info("Invalid subscriberMDN" + realMsg.getSubscriberMDNID());
			errorMsg.setErrorDescription(MessageText._("Invalid MDNID"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}

		Subscriber subscriber = subscriberMDN.getSubscriber();
		if ((!CmFinoFIX.UpgradeState_Upgradable.equals(subscriber.getUpgradestate())
				&&(!CmFinoFIX.UpgradeState_Rejected.equals(subscriber.getUpgradestate())))
				|| subscriber.getKycLevel() == null) {
			log.info("subscriber is not upgradable"	+ realMsg.getSubscriberMDNID());
			errorMsg.setErrorDescription(MessageText._("subscriber is not upgradable"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		if (userService.getCurrentUser().getUsername().equals(subscriber.getAppliedby())) {
			log.info("You are not authorized to perform this operation");
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		log.info("Admin action -- " + realMsg.getAdminAction() + " for mdnid"+ realMsg.getSubscriberMDNID());
		Pocket bankPocket = null;
		Pocket emoneyPocket = null;
		Long groupID = null;
		SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
		List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(new BigDecimal(subscriber.getId()));
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroupid().longValue();
		}
		emoneyPocket = subscriberService.getEmoneyPocket(subscriberMDN.getId().longValue(), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
       
		KycLevel kyclevel = null;
		if (subscriber.getKycLevel() != null && subscriber.getKycLevel().getId() != null) {
			kyclevel = kycLevelDao.getByKycLevel(subscriber.getKycLevel().getId().longValue());
		}

		if (CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())) {
			
			if (ConfigurationUtil.getBulkUploadSubscriberKYClevel().equals(subscriber.getKycLevel())
					&&(bankPocket== null||
		         			!((bankPocket.getStatus()).equals(CmFinoFIX.PocketStatus_Initialized)||
		         					(bankPocket.getStatus()).equals(CmFinoFIX.PocketStatus_Active))
		         					||bankPocket.getCardpan()==null)) {
				log.info("valid bank pocket not found"+ realMsg.getSubscriberMDNID());
				errorMsg.setErrorDescription(MessageText._("valid bank pocket not found"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			
			if (kyclevel == null) {
				log.info("Invalid kyclevel to upgrade"+ realMsg.getSubscriberMDNID());
				errorMsg.setErrorDescription(MessageText._("Invalid Kyclevel to upgrade"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			
			PocketTemplate upgradeTemplate = null;
			isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
			if(isEMoneyPocketRequired == true){
				if(emoneyPocket== null||
	        			!((emoneyPocket.getStatus()).equals(CmFinoFIX.PocketStatus_Initialized)||
	        			(emoneyPocket.getStatus()).equals(CmFinoFIX.PocketStatus_Active))){
					log.info("valid emaoney pocket not found"+ realMsg.getSubscriberMDNID());
					errorMsg.setErrorDescription(MessageText._("valid Emoney pocket not found"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					return errorMsg;
				}
				
				upgradeTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kyclevel.getKyclevel().longValue(), true, 
						CmFinoFIX.PocketType_SVA, subscriber.getType(), null, groupID);
	
				if (upgradeTemplate == null) {
					log.info("Invalid pocketTemplate for kyclevel to upgrade"+ realMsg.getSubscriberMDNID());
					errorMsg.setErrorDescription(MessageText._("Invalid pocketTemplate for kyclevel to upgrade"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					return errorMsg;
				}
			}
			subscriber.setKycLevel(kyclevel);
			subscriber.setUpgradablekyclevel(null);
			subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Approved);
			subscriber.setApproveorrejectcomment(realMsg.getAdminComment());
			subscriber.setApprovedorrejectedby(userService.getCurrentUser().getUsername());
			subscriber.setApproveorrejecttime(new Timestamp());
			subscriberDao.save(subscriber);
			// if required generate otp

			Integer response = updatePockets(subscriber, emoneyPocket,bankPocket, realMsg.getAdminAction(), upgradeTemplate);
			if (!SubscriberSyncErrors.Success.equals(response)) {
				log.info(SubscriberSyncErrors.errorCodesMap.get(response)+ realMsg.getSubscriberMDNID());
				errorMsg.setErrorDescription(MessageText._(SubscriberSyncErrors.errorCodesMap.get(response)));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			errorMsg.setErrorDescription(MessageText._("Successfully approved the subscriber"));

		} else if (CmFinoFIX.AdminAction_RequestForCorrection.equals(realMsg.getAdminAction())) { 
			
			subscriber.setApprovedorrejectedby(userService.getCurrentUser().getUsername());
			subscriber.setApproveorrejecttime(new Timestamp());
			subscriber.setApproveorrejectcomment(realMsg.getAdminComment());
			subscriber.setUpgradestate(CmFinoFIX.UpgradeState_RequestForCorrection);
			subscriberDao.save(subscriber);
			
			errorMsg.setErrorDescription(MessageText._("Request For Correction has been sent for Subscriber"));
			
		} else if (CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())) {
			// update subscriber donot update pockets
			subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Rejected);
			subscriber.setApprovedorrejectedby(userService.getCurrentUser().getUsername());
			subscriber.setApproveorrejecttime(new Timestamp());
			subscriber.setApproveorrejectcomment(realMsg.getAdminComment());
			if (bankPocket != null){
				bankPocket.setRestrictions(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
				pocketDao.save(bankPocket);
			}
			subscriberDao.save(subscriber);
			errorMsg.setErrorDescription(MessageText._("Successfully rejected the subscriber"));
		} else {
			log.info("Invalid Mdn" + realMsg.getSubscriberMDNID());
			errorMsg.setErrorDescription(MessageText._("Invalid MDN"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		//send sms use async
		log.info("Sending...");
		log.info("Sending SMS to the subscriber:" + subscriberMDN.getMdn());
		String smsMsg = "approve or reject notification";
		String emailMsg = "approve or reject notification";
		NotificationWrapper notificationWrapper = new NotificationWrapper();
		try {
			 notificationWrapper.setLanguage(subscriber.getLanguage());
			 notificationWrapper.setCompany(subscriber.getCompany());
			 notificationWrapper.setKycLevel(kyclevel.getKyclevelname());
			 if(CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())){
			 
				 notificationWrapper.setCode(CmFinoFIX.NotificationCode_UpgradeSuccess);
				 
			 }else if(CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction()) || CmFinoFIX.AdminAction_RequestForCorrection.equals(realMsg.getAdminAction())){
			 
				 notificationWrapper.setCode(CmFinoFIX.NotificationCode_UpgradeFail);
			 }
			 
			 notificationWrapper.setDestMDN(subscriberMDN.getMdn());
			 
			 if(subscriberMDN != null){
				 
				 notificationWrapper.setFirstName(subscriberMDN.getSubscriber().getFirstname());
				 notificationWrapper.setLastName(subscriberMDN.getSubscriber().getLastname());					
			 }
			 
			 notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			 smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); //use thread pool to send message
			 notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			 emailMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); 
			 
			 if(!ConfigurationUtil.getSendOTPBeforeApproval()){
				Integer OTPLength = systemParametersService.getOTPLength();
				String oneTimePin = MfinoUtil.generateOTP(OTPLength);
				String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMdn(), oneTimePin);
				subscriberMDN.setOtp(digestPin1);
				subscriberMDN.setOtpexpirationtime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
				subscriberMdnDao.save(subscriberMDN);
				
				if(CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())){
					log.info("new OTP set for " + subscriberMDN.getId() + " by user " + getLoggedUserNameWithIP() + " oneTimePin:" + oneTimePin);
					NotificationWrapper smsNotificationWrapper=subscriberServiceExtended.generateOTPMessage(oneTimePin, CmFinoFIX.NotificationMethod_SMS);
					smsNotificationWrapper.setDestMDN(subscriberMDN.getMdn());
					smsNotificationWrapper.setLanguage(subscriber.getLanguage());
					smsNotificationWrapper.setFirstName(subscriber.getFirstname());
		            smsNotificationWrapper.setLastName(subscriber.getLastname());
					String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper,true);
					String mdn2 = subscriberMDN.getMdn();
					SMSValues smsValues= new SMSValues();
					smsValues.setDestinationMDN(mdn2);
					smsValues.setMessage(smsMessage);
					smsValues.setNotificationCode(smsNotificationWrapper.getCode());
						
					smsService.asyncSendSMS(smsValues);
					
					if(((subscriber.getNotificationmethod() & CmFinoFIX.NotificationMethod_Email) > 0) && subscriber.getEmail() != null){
						NotificationWrapper emailNotificationWrapper=subscriberServiceExtended.generateOTPMessage(oneTimePin, CmFinoFIX.NotificationMethod_Email);
						emailNotificationWrapper.setDestMDN(subscriberMDN.getMdn());
						emailNotificationWrapper.setLanguage(subscriber.getLanguage());
						emailNotificationWrapper.setFirstName(subscriber.getFirstname());
						emailNotificationWrapper.setLastName(subscriber.getLastname());
						String emailMessage = notificationMessageParserService.buildMessage(emailNotificationWrapper,true);
						String to=subscriber.getEmail();
						String name=subscriber.getFirstname();
						String sub = ConfigurationUtil.getOTPMailSubsject();
						mailService.asyncSendEmail(to, name, sub, emailMessage);
					}
				}
			 }			 
		}catch (Exception excp) {
			log.error("failed to generate message:",excp);
		}
			
		if(CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())){
			
			String mdn = subscriberMDN.getMdn();
			//smsService.setDestinationMDN(mdn);
			// service.setSourceMDN(notificationWrapper.getSMSNotificationCode());
			//smsService.setMessage(smsMsg);
			SMSValues smsValues1= new SMSValues();
			smsValues1.setDestinationMDN(mdn);
			smsValues1.setMessage(smsMsg);
			smsValues1.setNotificationCode(notificationWrapper.getCode());
			
			smsService.asyncSendSMS(smsValues1);
		}
		
		if( ((subscriber.getNotificationmethod() & CmFinoFIX.NotificationMethod_Email) > 0) && subscriberServiceExtended.isSubscriberEmailVerified(subscriber)){
			String to=subscriber.getEmail();
			String name= subscriber.getFirstname();
			mailService.asyncSendEmail(to,name, "UpgradeNotification", emailMsg);
		}
		
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		
		return errorMsg;
	}

	private int updatePockets(Subscriber subscriber, Pocket emoneyPocket,
			Pocket bankPocket, Integer adminAction,
			PocketTemplate upgradetemplate) {

		isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
		if(isEMoneyPocketRequired == true){
		boolean isCompatible = pocketTemplateService.areCompatible(emoneyPocket.getPocketTemplateByPockettemplateid(), upgradetemplate);
		if (!isCompatible) {
			return SubscriberSyncErrors.PocketTemplatesIncompatible;
		}
		if (emoneyPocket.getPocketTemplateByPockettemplateid().equals(upgradetemplate)) {
			// return SubscriberSyncErrors.PocketAlreadyUpgraded;
			log.info("Pocket already upgraded");
		}
		emoneyPocket.setPocketTemplateByOldpockettemplateid(emoneyPocket.getPocketTemplateByPockettemplateid());
		emoneyPocket.setPocketTemplateByPockettemplateid(upgradetemplate);
//		emoneyPocket.setStatus(subscriber.getStatus());
		emoneyPocket.setPockettemplatechangedby(userService.getCurrentUser().getUsername());
		emoneyPocket.setPockettemplatechangetime(new Timestamp());
		pocketDao.save(emoneyPocket);
		}
		if (bankPocket != null&&subscriber.getKycLevel().getKyclevel().equals(ConfigurationUtil.getBulkUploadSubscriberKYClevel())) {
			if (subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Active)) {
				bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
				bankPocket.setActivationtime(new Timestamp());
				
			} else {
				bankPocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
			}
			bankPocket.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
			pocketDao.save(bankPocket);
		}
		return SubscriberSyncErrors.Success;

	}
}
