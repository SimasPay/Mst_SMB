/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Date;
import java.util.List;
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
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.MfinoUser;
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
        SubscriberMdn subscriberMDN =subscriber.getSubscriberMdns().iterator().next();
        if((!CmFinoFIX.UpgradeState_Upgradable.equals(subscriber.getUpgradestate()))
        		&&(!CmFinoFIX.UpgradeState_Rejected.equals(subscriber.getUpgradestate()))
        	&&(!CmFinoFIX.UpgradeState_RequestForCorrection.equals(subscriber.getUpgradestate()))){
        	log.info("Invalid partner status" + realMsg.getPartnerID());
            errorMsg.setErrorDescription(MessageText._("Invalid partner upgradestatus"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
        if(!(((Long)partner.getPartnerstatus()).equals(CmFinoFIX.SubscriberStatus_Initialized)
        		&& ((Long)subscriber.getStatus()).equals(CmFinoFIX.SubscriberStatus_Initialized))){
        	log.info("Invalid partner status" + realMsg.getPartnerID());
            errorMsg.setErrorDescription(MessageText._("Invalid partner status"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
        
        if (userService.getCurrentUser().getUsername().equals(subscriber.getAppliedby())) {
			log.info("You are not authorized to perform this operation");
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
        Set<Pocket> pockets = subscriberMDN.getPockets();
		Pocket bankPocket = null;
		Pocket emoneyPocket = null;
		Pocket lakuPocket = null;
		KycLevel kyclevel=kycLevelDao.getByKycLevel(ConfigurationUtil.getBulkUploadSubscriberKYClevel());
		
		Long groupID = null;
		SubscriberGroupDao sgDao = DAOFactory.getInstance().getSubscriberGroupDao();
		List<SubscriberGroups> subscriberGroups = sgDao.getAllBySubscriberID(subscriber.getId());
		if(subscriberGroups != null && !subscriberGroups.isEmpty()) {
			
			SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroupid();
		}
		
		boolean isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
		
		PocketTemplate emoneyPocketTemplate = null;
		PocketTemplate lakuPocketTemplate = null;
		
		if(isEMoneyPocketRequired == true){
		
			emoneyPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(subscriber.getKycLevel().getKyclevel().longValue(), true, 
					CmFinoFIX.PocketType_SVA, ((Long)subscriber.getType()).intValue(), partner.getBusinesspartnertype().intValue(), groupID);
			
			if(emoneyPocketTemplate == null) {
	         	log.info("Valid emoneyPocketTemplate not found" + realMsg.getPartnerID());
	            errorMsg.setErrorDescription(MessageText._("Valid emoneyPocketTemplate not found"));
	            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
	            return errorMsg;
	         }
			
			emoneyPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), emoneyPocketTemplate.getId().longValue());
		}
		
        bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
        
        lakuPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(subscriber.getKycLevel().getKyclevel().longValue(), true, 
        		CmFinoFIX.PocketType_LakuPandai, ((Long)subscriber.getType()).intValue(), partner.getBusinesspartnertype().intValue(), groupID);
		
		if(lakuPocketTemplate == null) {
         	log.info("Valid lakuPocketTemplate not found" + realMsg.getPartnerID());
            errorMsg.setErrorDescription(MessageText._("Valid lakuPocketTemplate not found"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
         }
       
        log.info("Admin action -- " + realMsg.getAdminAction()+" for PartnerID"+realMsg.getPartnerID());
        
        lakuPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), CmFinoFIX.PocketType_LakuPandai, CmFinoFIX.Commodity_Money);
        
        if (CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())) {
        	
    		if(isEMoneyPocketRequired == true){
        	
    			if(emoneyPocket== null||
	        			!(((Long)emoneyPocket.getStatus()).equals(CmFinoFIX.PocketStatus_Initialized)||
	        			((Long)emoneyPocket.getStatus()).equals(CmFinoFIX.PocketStatus_Active))){
	             	log.info("valid emoney pocket not found" + realMsg.getPartnerID());
	                 errorMsg.setErrorDescription(MessageText._("valid emoney pocket not found"));
	                 errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
	                 return errorMsg;
	             }
    		}
    		
    		if(lakuPocket == null||
        			!(((Long)lakuPocket.getStatus()).equals(CmFinoFIX.PocketStatus_Initialized)||
        			((Long)lakuPocket.getStatus()).equals(CmFinoFIX.PocketStatus_Active))){
             	log.info("valid laku pocket not found" + realMsg.getPartnerID());
                 errorMsg.setErrorDescription(MessageText._("valid laku pocket not found"));
                 errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                 return errorMsg;
             }
    		
            if(bankPocket== null||
         			!(((Long)bankPocket.getStatus()).equals(CmFinoFIX.PocketStatus_Initialized)||
         					((Long)bankPocket.getStatus()).equals(CmFinoFIX.PocketStatus_Active))
         					||bankPocket.getCardpan()==null){
             	log.info("valid bank pocket not found" + realMsg.getPartnerID());
                 errorMsg.setErrorDescription(MessageText._("valid bank pocket not found"));
                 errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                 return errorMsg;
             }
             
        	subscriber.setUpgradestate(((Integer)CmFinoFIX.UpgradeState_Approved).longValue());
        	subscriber.setApprovedorrejectedby(userService.getCurrentUser().getUsername());
        	subscriber.setApproveorrejectcomment(realMsg.getAdminComment());
        	subscriber.setApproveorrejecttime(new Timestamp());
        	subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
        	subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
            MfinoUser u =partner.getMfinoUser();
    		Integer OTPLength = systemParametersService.getOTPLength();
            String oneTimePin = MfinoUtil.generateOTP(OTPLength);
    		String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMdn(), oneTimePin);
    		subscriberMDN.setOtp(digestPin1);
			subscriberMDN.setOtpexpirationtime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));

//    		userDao.save(u);
    		subscriberDao.save(subscriber);
    		subscriberMdnDao.save(subscriberMDN);
    		partnerDAO.save(partner);
    		Integer response = updatePockets(subscriber, emoneyPocket,bankPocket, realMsg.getAdminAction(), lakuPocket);
			if (!SubscriberSyncErrors.Success.equals(response)) {
				log.info(SubscriberSyncErrors.errorCodesMap.get(response)+ realMsg.getPartnerID());
				errorMsg.setErrorDescription(MessageText._(SubscriberSyncErrors.errorCodesMap.get(response)));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			errorMsg.setErrorDescription(MessageText._("Successfully approved the Partner"));
			
			sendOTPOnIntialized = ConfigurationUtil.getSendOTPOnIntialized();
    		if(!sendOTPOnIntialized){
    			String responsetext=partnerService.processActivation(subscriberMDN.getMdn(), oneTimePin) ;
    			log.info(realMsg.getPartnerID() +": " + responsetext);
    		}else{
    		
    			String email=u.getEmail();
    			String to=u.getFirstname() + " " + u.getLastname();

    			String mdn=subscriberMDN.getMdn();
    			//           String message =SubscriberServiceExtended.generateOTPMessage(oneTimePin);
    			NotificationWrapper smsWrapper = partnerService.genratePartnerOTPMessage(partner, oneTimePin,subscriberMDN.getMdn(), CmFinoFIX.NotificationMethod_SMS);
    			NotificationWrapper emailWrapper = partnerService.genratePartnerOTPMessage(partner, oneTimePin,subscriberMDN.getMdn(), CmFinoFIX.NotificationMethod_Email);
    			SubscriberMdn smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(mdn);
    			if(smdn != null)
    			{
    				smsWrapper.setFirstName(smdn.getSubscriber().getFirstname());
    				smsWrapper.setLastName(smdn.getSubscriber().getLastname());	
    				emailWrapper.setFirstName(smdn.getSubscriber().getFirstname());
    				emailWrapper.setLastName(smdn.getSubscriber().getLastname());
    			}
    			smsWrapper.setDestMDN(subscriberMDN.getMdn());
    			String smsMessage = notificationMessageParserService.buildMessage(smsWrapper,true);

    			smsService.setDestinationMDN(mdn);
    			smsService.setMessage(smsMessage);
    			smsService.setNotificationCode(smsWrapper.getCode());
    			smsService.asyncSendSMS();
    			
    			emailWrapper.setDestMDN(subscriberMDN.getMdn());
    			String emailMessage = notificationMessageParserService.buildMessage(emailWrapper,true);
    			String sub= ConfigurationUtil.getOTPMailSubsject();
    			mailService.asyncSendEmail(email, to, sub, emailMessage);
    		}
		} else if (CmFinoFIX.AdminAction_RequestForCorrection.equals(realMsg.getAdminAction())) { 
			
        	subscriber.setApprovedorrejectedby(userService.getCurrentUser().getUsername());
        	subscriber.setApproveorrejectcomment(realMsg.getAdminComment());
        	subscriber.setApproveorrejecttime(new Timestamp());
        	subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
        	subscriber.setStatustime(new Timestamp());
        	subscriber.setUpgradestate(((Integer)CmFinoFIX.UpgradeState_RequestForCorrection).longValue());
        	subscriberDao.save(subscriber);
			errorMsg.setErrorDescription(MessageText._("Requested For Correction of Data for Agent"));
        } else if (CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())) {
            //update subscriber  update pockets
        	partner.setPartnerstatus(((Integer)CmFinoFIX.SubscriberStatus_Initialized).longValue());
        	subscriber.setApprovedorrejectedby(userService.getCurrentUser().getUsername());
        	subscriber.setApproveorrejectcomment(realMsg.getAdminComment());
        	subscriber.setApproveorrejecttime(new Timestamp());
        	subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
        	subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
        	subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
        	subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
        	subscriber.setStatustime(new Timestamp());
        	subscriberMDN.setStatustime(new Timestamp());
        	subscriber.setUpgradestate(((Integer)CmFinoFIX.UpgradeState_Rejected).longValue());
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


	private Integer updatePockets(Subscriber subscriber, Pocket emoneyPocket, Pocket bankPocket, Integer adminAction, Pocket lakuPocket) {
		
		if(emoneyPocket!=null){
		
			emoneyPocket.setStatus(subscriber.getStatus());
			pocketDao.save(emoneyPocket);
		}
		
		if(lakuPocket!=null){
			
			lakuPocket.setStatus(subscriber.getStatus());
			pocketDao.save(lakuPocket);
		}
		
		if (bankPocket != null) {			
			bankPocket.setStatus(subscriber.getStatus());
			pocketDao.save(bankPocket);
		}
		return SubscriberSyncErrors.Success;
	}
}
