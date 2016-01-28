/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Date;
import java.util.Set;

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
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSApproveRejectPartner;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.AuthorizationService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.ApproveRejectPartnerProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.SubscriberSyncErrors;


/**
 * 
 * @author Raju
 */
@Service("ApproveRejectPartnerProcessorImpl")
public class ApproveRejectPartnerProcessorImpl extends BaseFixProcessor implements ApproveRejectPartnerProcessor{
   
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static SubscriberDAO  subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
    private static SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
    private static PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
    private static KYCLevelDAO kycLevelDao = DAOFactory.getInstance().getKycLevelDAO();
    private static PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
    private  boolean sendOTPOnIntialized;
    
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	 
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {
    	CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        if (!authorizationService.isAuthorized(CmFinoFIX.Permission_Partner_Approve)) {
            log.info("You are not authorized to perform this operation");
            errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }

        CMJSApproveRejectPartner realMsg = (CMJSApproveRejectPartner) msg;
        log.info("Procesing the Request of" + realMsg.getPartnerID());
        if (realMsg.getPartnerID() == null ) {
            log.info("PartnerID is null");
            errorMsg.setErrorDescription(MessageText._("Invalid Partner status"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }

        
       Partner partner = partnerDAO.getById(realMsg.getPartnerID()); 
         
        if (null == partner) {
            log.info("Invalid Partner" + realMsg.getPartnerID());
            errorMsg.setErrorDescription(MessageText._("Invalid Partner"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
       
        Subscriber subscriber = partner.getSubscriber();
        SubscriberMDN subscriberMDN =subscriber.getSubscriberMDNFromSubscriberID().iterator().next();
        if((!CmFinoFIX.UpgradeState_Upgradable.equals(subscriber.getUpgradeState()))
        		&&(!CmFinoFIX.UpgradeState_Rejected.equals(subscriber.getUpgradeState()))
        	&&(!CmFinoFIX.UpgradeState_RequestForCorrection.equals(subscriber.getUpgradeState()))){
        	log.info("Invalid partner status" + realMsg.getPartnerID());
            errorMsg.setErrorDescription(MessageText._("Invalid partner upgradestatus"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
        if(!(partner.getPartnerStatus().equals(CmFinoFIX.SubscriberStatus_Initialized)
        		&&subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Initialized))){
        	log.info("Invalid partner status" + realMsg.getPartnerID());
            errorMsg.setErrorDescription(MessageText._("Invalid partner status"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
        
        if (userService.getCurrentUser().getUsername().equals(subscriber.getAppliedBy())) {
			log.info("You are not authorized to perform this operation");
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
        Set<Pocket> pockets = subscriberMDN.getPocketFromMDNID();
		Pocket bankPocket = null;
		Pocket emoneyPocket = null;
		KYCLevel kyclevel=kycLevelDao.getByKycLevel(ConfigurationUtil.getBulkUploadSubscriberKYClevel());
		
		Long groupID = null;
		Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroup().getID();
		}
		PocketTemplate emoneyPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(subscriber.getKYCLevelByKYCLevel().getKYCLevel(), true, CmFinoFIX.PocketType_SVA, subscriber.getType(), partner.getBusinessPartnerType(), groupID);	
		if(emoneyPocketTemplate == null) {
         	log.info("Valid emoneyPocketTemplate not found" + realMsg.getPartnerID());
            errorMsg.setErrorDescription(MessageText._("Valid emoneyPocketTemplate not found"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
         }
		emoneyPocket = subscriberService.getDefaultPocket(subscriberMDN.getID(),emoneyPocketTemplate.getID());
        bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getID(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
       
        log.info("Admin action -- " + realMsg.getAdminAction()+" for PartnerID"+realMsg.getPartnerID());
        
        if (CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())) {
        	if(emoneyPocket== null||
        			!(emoneyPocket.getStatus().equals(CmFinoFIX.PocketStatus_Initialized)||
        			emoneyPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active))){
             	log.info("valid emoney pocket not found" + realMsg.getPartnerID());
                 errorMsg.setErrorDescription(MessageText._("valid emoney pocket not found"));
                 errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                 return errorMsg;
             }
             if(bankPocket== null||
         			!(bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_Initialized)||
         					bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active))
         					||bankPocket.getCardPAN()==null){
             	log.info("valid bank pocket not found" + realMsg.getPartnerID());
                 errorMsg.setErrorDescription(MessageText._("valid bank pocket not found"));
                 errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                 return errorMsg;
             }
             
        	subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Approved);
        	subscriber.setApprovedOrRejectedBy(userService.getCurrentUser().getUsername());
        	subscriber.setApproveOrRejectComment(realMsg.getAdminComment());
        	subscriber.setApproveOrRejectTime(new Timestamp());
        	subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
        	subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
            User u =partner.getUser();
    		Integer OTPLength = systemParametersService.getOTPLength();
            String oneTimePin = MfinoUtil.generateOTP(OTPLength);
    		String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), oneTimePin);
    		subscriberMDN.setOTP(digestPin1);
			subscriberMDN.setOTPExpirationTime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));

//    		userDao.save(u);
    		subscriberDao.save(subscriber);
    		subscriberMdnDao.save(subscriberMDN);
    		partnerDAO.save(partner);
    		Integer response = updatePockets(subscriber, emoneyPocket,bankPocket, realMsg.getAdminAction());
			if (!SubscriberSyncErrors.Success.equals(response)) {
				log.info(SubscriberSyncErrors.errorCodesMap.get(response)+ realMsg.getPartnerID());
				errorMsg.setErrorDescription(MessageText._(SubscriberSyncErrors.errorCodesMap.get(response)));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			errorMsg.setErrorDescription(MessageText._("Successfully approved the Partner"));
			
			sendOTPOnIntialized = ConfigurationUtil.getSendOTPOnIntialized();
    		if(!sendOTPOnIntialized){
    			String responsetext=partnerService.processActivation(subscriberMDN.getMDN(), oneTimePin) ;
    			log.info(realMsg.getPartnerID() +": " + responsetext);
    		}else{
    		
    			String email=u.getEmail();
    			String to=u.getFirstName() + " " + u.getLastName();

    			String mdn=subscriberMDN.getMDN();
    			//           String message =SubscriberServiceExtended.generateOTPMessage(oneTimePin);
    			NotificationWrapper smsWrapper = partnerService.genratePartnerOTPMessage(partner, oneTimePin,subscriberMDN.getMDN(), CmFinoFIX.NotificationMethod_SMS);
    			NotificationWrapper emailWrapper = partnerService.genratePartnerOTPMessage(partner, oneTimePin,subscriberMDN.getMDN(), CmFinoFIX.NotificationMethod_Email);
    			SubscriberMDN smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(mdn);
    			if(smdn != null)
    			{
    				smsWrapper.setFirstName(smdn.getSubscriber().getFirstName());
    				smsWrapper.setLastName(smdn.getSubscriber().getLastName());	
    				emailWrapper.setFirstName(smdn.getSubscriber().getFirstName());
    				emailWrapper.setLastName(smdn.getSubscriber().getLastName());
    			}
    			smsWrapper.setDestMDN(subscriberMDN.getMDN());
    			String smsMessage = notificationMessageParserService.buildMessage(smsWrapper,true);

    			smsService.setDestinationMDN(mdn);
    			smsService.setMessage(smsMessage);
    			smsService.setNotificationCode(smsWrapper.getCode());
    			smsService.asyncSendSMS();
    			
    			emailWrapper.setDestMDN(subscriberMDN.getMDN());
    			String emailMessage = notificationMessageParserService.buildMessage(emailWrapper,true);
    			String sub= ConfigurationUtil.getOTPMailSubsject();
    			mailService.asyncSendEmail(email, to, sub, emailMessage);
    		}
		} else if (CmFinoFIX.AdminAction_RequestForCorrection.equals(realMsg.getAdminAction())) { 
			
        	subscriber.setApprovedOrRejectedBy(userService.getCurrentUser().getUsername());
        	subscriber.setApproveOrRejectComment(realMsg.getAdminComment());
        	subscriber.setApproveOrRejectTime(new Timestamp());
        	subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
        	subscriber.setStatusTime(new Timestamp());
        	subscriber.setUpgradeState(CmFinoFIX.UpgradeState_RequestForCorrection);
        	subscriberDao.save(subscriber);
			errorMsg.setErrorDescription(MessageText._("Requested For Correction of Data for Agent"));
        } else if (CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())) {
            //update subscriber  update pockets
        	partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_Initialized);
        	subscriber.setApprovedOrRejectedBy(userService.getCurrentUser().getUsername());
        	subscriber.setApproveOrRejectComment(realMsg.getAdminComment());
        	subscriber.setApproveOrRejectTime(new Timestamp());
        	subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
        	subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
        	subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
        	subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
        	subscriber.setStatusTime(new Timestamp());
        	subscriberMDN.setStatusTime(new Timestamp());
        	subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Rejected);
        	subscriberDao.save(subscriber);
        	subscriberMdnDao.save(subscriberMDN);
            partnerDAO.save(partner);
            errorMsg.setErrorDescription(MessageText._("Successfully rejected the Partner"));
        } else {
            log.info("Invalid Mdn" + realMsg.getPartnerID());
            errorMsg.setErrorDescription(MessageText._("Invalid MDN"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
        return errorMsg;
    }


	private Integer updatePockets(Subscriber subscriber, Pocket emoneyPocket,
			Pocket bankPocket, Integer adminAction) {
		if(emoneyPocket!=null){
		emoneyPocket.setStatus(subscriber.getStatus());
		pocketDao.save(emoneyPocket);
		}
		if (bankPocket != null) {			
			bankPocket.setStatus(subscriber.getStatus());
			pocketDao.save(bankPocket);
		}
		return SubscriberSyncErrors.Success;

	}


}
