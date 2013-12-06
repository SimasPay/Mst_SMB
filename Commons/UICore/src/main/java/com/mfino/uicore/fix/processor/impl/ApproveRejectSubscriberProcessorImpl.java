/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMDN;
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
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.ApproveRejectSubscriberProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.util.ConfigurationUtil;
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

		SubscriberMDN subscriberMDN = subscriberMdnDao.getById(realMsg.getSubscriberMDNID());

		if (null == subscriberMDN) {
			log.info("Invalid subscriberMDN" + realMsg.getSubscriberMDNID());
			errorMsg.setErrorDescription(MessageText._("Invalid MDNID"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}

		Subscriber subscriber = subscriberMDN.getSubscriber();
		if ((!CmFinoFIX.UpgradeState_Upgradable.equals(subscriber.getUpgradeState())
				&&(!CmFinoFIX.UpgradeState_Rejected.equals(subscriber.getUpgradeState())))
				|| subscriber.getUpgradableKYCLevel() == null) {
			log.info("subscriber is not upgradable"	+ realMsg.getSubscriberMDNID());
			errorMsg.setErrorDescription(MessageText._("subscriber is not upgradable"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		if (userService.getCurrentUser().getUsername().equals(subscriber.getAppliedBy())) {
			log.info("You are not authorized to perform this operation");
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		log.info("Admin action -- " + realMsg.getAdminAction() + " for mdnid"+ realMsg.getSubscriberMDNID());
		Pocket bankPocket = null;
		Pocket emoneyPocket = null;
		Long groupID = null;
		Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroup().getID();
		}
		emoneyPocket = subscriberService.getEmoneyPocket(subscriberMDN.getID(), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getID(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
       
		KYCLevel kyclevel = null;
		if (subscriber.getUpgradableKYCLevel() != null) {
			kyclevel = kycLevelDao.getByKycLevel(subscriber.getUpgradableKYCLevel());
		}

		if (CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())) {
			

			if (ConfigurationUtil.getBulkUploadSubscriberKYClevel().equals(subscriber.getUpgradableKYCLevel())
					&&(bankPocket== null||
		         			!(bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_Initialized)||
		         					bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active))
		         					||bankPocket.getCardPAN()==null)) {
				log.info("valid bank pocket not found"+ realMsg.getSubscriberMDNID());
				errorMsg.setErrorDescription(MessageText._("valid bank pocket not found"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			
			isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
			if(isEMoneyPocketRequired == true){
			if(emoneyPocket== null||
        			!(emoneyPocket.getStatus().equals(CmFinoFIX.PocketStatus_Initialized)||
        			emoneyPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active))){
				log.info("valid emaoney pocket not found"+ realMsg.getSubscriberMDNID());
				errorMsg.setErrorDescription(MessageText._("valid Emoney pocket not found"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			}

			if (kyclevel == null) {
				log.info("Invalid kyclevel to upgrade"+ realMsg.getSubscriberMDNID());
				errorMsg.setErrorDescription(MessageText._("Invalid Kyclevel to upgrade"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			PocketTemplate upgradeTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kyclevel.getKYCLevel(), true, CmFinoFIX.PocketType_SVA, subscriber.getType(), null, groupID);

			if (upgradeTemplate == null) {
				log.info("Invalid pocketTemplate for kyclevel to upgrade"+ realMsg.getSubscriberMDNID());
				errorMsg.setErrorDescription(MessageText._("Invalid pocketTemplate for kyclevel to upgrade"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}

			subscriber.setKYCLevelByKYCLevel(kyclevel);
			subscriber.setUpgradableKYCLevel(null);
			subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Approved);
			subscriber.setApproveOrRejectComment(realMsg.getAdminComment());
			subscriber.setApprovedOrRejectedBy(userService.getCurrentUser().getUsername());
			subscriber.setApproveOrRejectTime(new Timestamp());
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

		} else if (CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())) {
			// update subscriber donot update pockets
			subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Rejected);
			subscriber.setApprovedOrRejectedBy(userService.getCurrentUser().getUsername());
			subscriber.setApproveOrRejectTime(new Timestamp());
			subscriber.setApproveOrRejectComment(realMsg.getAdminComment());
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
		log.info("Sending SMS to the subscriber:" + subscriberMDN.getMDN());
		String smsMsg = "approve or reject notification";
		String emailMsg = "approve or reject notification";
		try {
			 //add notifications
			 NotificationWrapper notificationWrapper = new NotificationWrapper();
			 notificationWrapper.setLanguage(subscriber.getLanguage());
			 notificationWrapper.setCompany(subscriber.getCompany());
			 notificationWrapper.setKycLevel(kyclevel.getKYCLevelName());
			 if(CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())){
			 notificationWrapper.setCode(CmFinoFIX.NotificationCode_UpgradeSuccess);
			 }else
			 if(CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())){
			 notificationWrapper.setCode(CmFinoFIX.NotificationCode_UpgradeFail);
			 }
			 notificationWrapper.setDestMDN(subscriberMDN.getMDN());
			 if(subscriberMDN != null)
			 {
				 notificationWrapper.setFirstName(subscriberMDN.getSubscriber().getFirstName());
				 notificationWrapper.setLastName(subscriberMDN.getSubscriber().getLastName());					
			 }
			 notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			 smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); //use thread pool to send message
			 notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			 emailMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); 
		}catch (Exception excp) {
			log.error("failed to generate message:",excp);
		}
			String mdn = subscriberMDN.getMDN();
			smsService.setDestinationMDN(mdn);
			// service.setSourceMDN(notificationWrapper.getSMSNotificationCode());
			smsService.setMessage(smsMsg);
			smsService.asyncSendSMS();
		
		if( ((subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0) && subscriberServiceExtended.isSubscriberEmailVerified(subscriber)){
			String to=subscriber.getEmail();
			String name= subscriber.getFirstName();
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
		boolean isCompatible = pocketTemplateService.areCompatible(emoneyPocket.getPocketTemplate(), upgradetemplate);
		if (!isCompatible) {
			return SubscriberSyncErrors.PocketTemplatesIncompatible;
		}
		if (emoneyPocket.getPocketTemplate().equals(upgradetemplate)) {
			// return SubscriberSyncErrors.PocketAlreadyUpgraded;
			log.info("Pocket already upgraded");
		}
		emoneyPocket.setPocketTemplateByOldPocketTemplateID(emoneyPocket.getPocketTemplate());
		emoneyPocket.setPocketTemplate(upgradetemplate);
//		emoneyPocket.setStatus(subscriber.getStatus());
		emoneyPocket.setPocketTemplateChangedBy(userService.getCurrentUser().getUsername());
		emoneyPocket.setPocketTemplateChangeTime(new Timestamp());
		pocketDao.save(emoneyPocket);
		}
		if (bankPocket != null&&subscriber.getKYCLevelByKYCLevel().getKYCLevel().equals(ConfigurationUtil.getBulkUploadSubscriberKYClevel())) {
			if (subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Active)) {
				bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
				bankPocket.setActivationTime(new Timestamp());
				
			} else {
				bankPocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
			}
			bankPocket.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
			pocketDao.save(bankPocket);
		}
		return SubscriberSyncErrors.Success;

	}
}
