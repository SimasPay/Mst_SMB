package com.mfino.uicore.fix.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.domain.Groups;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SMSValues;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSApproveRejectAddBankPocketToEmoneySubscriber;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.uicore.fix.processor.ApproveRejectAddBankPocketToEmoneySubscriberProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

@Service("ApproveRejectAddBankPocketToEmoneySubscriberProcessorImpl")
public class ApproveRejectAddBankPocketToEmoneySubscriberProcessorImpl extends BaseFixProcessor implements
ApproveRejectAddBankPocketToEmoneySubscriberProcessor {
	
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	SubscriberMDNDAO subscriberMdnDao =DAOFactory.getInstance().getSubscriberMdnDAO();
	SubscriberDAO subscriberDao=DAOFactory.getInstance().getSubscriberDAO();
	KYCLevelDAO kycLevelDao=DAOFactory.getInstance().getKycLevelDAO();
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("In ApproveRejectAddBankPocketToEmoneySubscriberProcessorImpl Process method");
		Integer notificationCode =null;
		CMJSApproveRejectAddBankPocketToEmoneySubscriber realMsg = (CMJSApproveRejectAddBankPocketToEmoneySubscriber) msg;
		SubscriberUpgradeData subscriberUpgradeData=  subscriberUpgradeDataDAO.getUpgradeDataByMdnId(realMsg.getMDNID());
		
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		
		
		
		if(CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())){
			
			SubscriberMdn srcSubscriberMDN = subscriberMdnService.getSubscriberMDNById(subscriberUpgradeData.getMdnId());
			subscriberUpgradeData.setSubActivity(CmFinoFIX.SubscriberActivity_Enable_MBanking_For_Emoney_Subscriber);
			subscriberUpgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
			subscriberUpgradeData.setSubsActivityApprovedBY(getLoggedUserName());
			subscriberUpgradeData.setSubsActivityAprvTime(new Timestamp());
			subscriberUpgradeData.setSubsActivityComments(realMsg.getAdminComment());
			
			if (CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())) {
				
				Long groupID = null;
				
				Subscriber subscriber = srcSubscriberMDN.getSubscriber();
				GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
				Groups defaultGroup = groupDao.getSystemGroup();
				groupID = defaultGroup.getId();
				
				PocketTemplate bankPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.SubscriberKYCLevel_FullyBanked.longValue(), true, CmFinoFIX.PocketType_BankAccount, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
				Pocket bankPocket = null;
				bankPocket = pocketService.createPocket(bankPocketTemplate,srcSubscriberMDN, CmFinoFIX.PocketStatus_Active, true, null);
				if(bankPocket!=null){
					bankPocket.setCardpan(subscriberUpgradeData.getBankAccountNumber());
					pocketDao.save(bankPocket);
					srcSubscriberMDN.setApplicationid(subscriberUpgradeData.getApplicationId());
					subscriberMdnDao.save(srcSubscriberMDN);
					KycLevel kyclevel=kycLevelDao.getByKycLevel(CmFinoFIX.SubscriberKYCLevel_FullyBanked.longValue());
					subscriber.setKycLevel(kyclevel);
					subscriberDao.save(subscriber);
					Pocket emoneyPocket = subscriberService.getDefaultPocket(srcSubscriberMDN.getId().longValue(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
					PocketTemplate emoneyPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.SubscriberKYCLevel_FullyBanked.longValue(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
					emoneyPocket.setPocketTemplateByPockettemplateid(emoneyPocketTemplate);
					pocketDao.save(emoneyPocket);
				}
				
				
				
				
				subscriberUpgradeData.setAdminAction(CmFinoFIX.AdminAction_Approve);
				notificationCode = CmFinoFIX.NotificationCode_ApproveMBankingServicesToEmoneySubScriber;
				realMsg.setsuccess(Boolean.TRUE);
		        return realMsg;
				
				
			}else if(CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())){
				
				
				subscriberUpgradeData.setAdminAction(CmFinoFIX.AdminAction_Reject);
			    notificationCode = CmFinoFIX.NotificationCode_RejectMBankingServicesToEmoneySubScriber;
				
				errorMsg.setErrorDescription(MessageText._("MBanking Services rejected"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
				
				
			}
			subscriberUpgradeDataDAO.save(subscriberUpgradeData);
			sendSMS(srcSubscriberMDN,notificationCode);
			
			log.info("In ApproveRejectAddBankPocketToEmoneySubscriberProcessorImpl Process method"+subscriberUpgradeData.getAdminAction()+" action completed");
		}
        
		if(CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())){
          if(subscriberUpgradeData==null){
    	    errorMsg.setErrorDescription(MessageText._("MBanking Service Request not enabled"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
           }
    	  if(!(subscriberUpgradeData.getSubsActivityStatus().equals(CmFinoFIX.SubscriberActivityStatus_Initialized))){
    		  errorMsg.setErrorDescription(MessageText._("Request Status Should be Initialized State Only!"));
    		  errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
    		  return errorMsg;
    	  }
    	  realMsg.setApplicationID(subscriberUpgradeData.getApplicationId());
    	  realMsg.setAccountNumber(subscriberUpgradeData.getBankAccountNumber());
    	  realMsg.setsuccess(Boolean.TRUE);
          return realMsg;
       
	
		}
		return errorMsg; 
	}

	private void sendSMS (SubscriberMdn subscriberMDN , Integer notificationCode) {
		try{
			Subscriber subscriber = subscriberMDN.getSubscriber();
			String mdn2 = subscriberMDN.getMdn();
			
			NotificationWrapper smsNotificationWrapper = new NotificationWrapper();
			smsNotificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			smsNotificationWrapper.setCode(notificationCode);
			smsNotificationWrapper.setDestMDN(mdn2);
			smsNotificationWrapper.setLanguage(subscriber.getLanguage());
			smsNotificationWrapper.setFirstName(subscriber.getFirstname());
			
	    	String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper, true);
			SMSValues smsValues= new SMSValues();
			smsValues.setDestinationMDN(mdn2);
			smsValues.setMessage(smsMessage);
			smsValues.setNotificationCode(smsNotificationWrapper.getCode());
			
			smsService.asyncSendSMS(smsValues);
		}catch(Exception e){
			e.printStackTrace();
			log.error("Error in Sending SMS "+e.getMessage(),e);
		}
	}
}
