package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.PocketTemplateQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SMSValues;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberUpgrade;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionIdentifierService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UserService;
import com.mfino.service.impl.ChannelCodeServiceImpl;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SubscriberUpgradeProcessor;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

@Service("SubscriberUpgradeProcessorImpl")
public class SubscriberUpgradeProcessorImpl extends BaseFixProcessor implements SubscriberUpgradeProcessor{

	private static SubscriberMDNDAO subMdndao=DAOFactory.getInstance().getSubscriberMdnDAO();
	private static PocketTemplateDAO pocketTemplateDAO=DAOFactory.getInstance().getPocketTemplateDao();
	private static PocketDAO pocketDAO= DAOFactory.getInstance().getPocketDAO();
	
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	

	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	

	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService ChannelCodeService;
	

 	@Autowired
 	@Qualifier("TransactionIdentifierServiceImpl")
 	private TransactionIdentifierService transactionIdentifierService;
	
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {

    	log.info("In SubscriberUpgradeProcessImpl Process method");
        CMJSSubscriberUpgrade realMsg = (CMJSSubscriberUpgrade) msg;
        CMJSError error = new CMJSError();
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        SubscriberMdn subscriberMDN = subMdndao.getById(realMsg.getMDNID());
        
        
        TransactionLog transactionsLog = null;
		ServiceChargeTxnLog sctl = null;
		

        
        
        String actionString=realMsg.getaction();
       log.info("Action is :"+actionString);
        if(subscriberMDN==null){
        	error.setErrorDescription(MessageText._("Invalid MDN ID"));
        	return error;
        }
        
        Subscriber subscriber = subscriberMDN.getSubscriber();
        Integer subMDNStatL = Integer.valueOf(Long.valueOf(subscriber.getStatus()).intValue());
        
        if(!(subMDNStatL.equals(CmFinoFIX.SubscriberStatus_Active)
        		|| subMDNStatL.equals(CmFinoFIX.SubscriberStatus_Active))){
        	error.setErrorDescription(MessageText._("Subscriber Should be Active! "));
        	return error;
        }
        
        PocketTemplateQuery pocketTemplateQuery = new PocketTemplateQuery();
        pocketTemplateQuery.setPocketType(CmFinoFIX.PocketType_LakuPandai);
        pocketTemplateQuery.setDescriptionSearch("LakuPandaiBasicTemplate");
        
        List<PocketTemplate> lakuPandaiBasicTemplateList= pocketTemplateDAO.get(pocketTemplateQuery);
        
        if(actionString!=null && actionString.equals("default")){
            
        	if(subscriberMDN.getUpgradeacctstatus()!=null){
        		error.setErrorDescription(MessageText._("Subscriber Upgrade Not Allowed "));
            	return error;
        	}
        	
            if(lakuPandaiBasicTemplateList!=null && lakuPandaiBasicTemplateList.size()>0){
            	
            	PocketQuery pocketQuery= new PocketQuery();
            	pocketQuery.setPocketTemplateID(lakuPandaiBasicTemplateList.get(0).getId().longValue());
            	pocketQuery.setMdnIDSearch(subscriberMDN.getId().longValue());
            	
            	List<Pocket> pocketList=pocketDAO.get(pocketQuery);
            	
            	if(pocketList==null || pocketList.size()==0 ){
            		error.setErrorDescription(MessageText._("Subscriber Not Have LakuPandaiBasicTemplate Pocket."));
                	return error;
            	}
            	
            	subscriberMDN.setUpgradeacctstatus(CmFinoFIX.SubscriberUpgradeStatus_Initialized);
        		subMdndao.save(subscriberMDN);
        		
        		error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
        		error.setErrorDescription(MessageText._("Request for Subscriber Upgraded Initialized successfully"));
        		log.info("Request for Subscriber Upgraded Initialized successfully");
            }else{
            	error.setErrorDescription(MessageText._("LakuPandaiBasicTemplate Not Available."));
            	return error;
            }
            
            
    		
    		
        	
        }else if(actionString!=null && actionString.equals("update")){
        	
        	if(subscriberMDN.getUpgradeacctstatus()!=null && (subscriberMDN.getUpgradeacctstatus().intValue() == CmFinoFIX.SubscriberUpgradeStatus_Initialized.intValue())){
        		
        		pocketTemplateQuery.setDescriptionSearch("LakuPandaiAdvancedTemplate");
            	List<PocketTemplate> lakuPandaiAdvancedTemplateList= pocketTemplateDAO.get(pocketTemplateQuery);
                
                
                
                
                if(lakuPandaiBasicTemplateList!=null && lakuPandaiBasicTemplateList.size()>0 ){
                	
                	PocketQuery pocketQuery= new PocketQuery();
                	pocketQuery.setPocketTemplateID(lakuPandaiBasicTemplateList.get(0).getId().longValue());
                	pocketQuery.setMdnIDSearch(subscriberMDN.getId().longValue());
                	
                	List<Pocket> pocketList=pocketDAO.get(pocketQuery);
                	if(pocketList!=null && pocketList.size()>0 ){
                		
                		if(lakuPandaiAdvancedTemplateList!=null && lakuPandaiAdvancedTemplateList.size()>0){
                		
                			Pocket pocket=pocketList.get(0);
                			
                			
                			
                			Integer upgradeStatus=realMsg.getUpgradeAcctStatus();
                			subscriberMDN.setUpgradeacctstatus(upgradeStatus);
                			subscriberMDN.setUpgradeacctcomments(realMsg.getUpgradeAcctComments());
                    		subscriberMDN.setUpgradeacctapprovedby(userService.getCurrentUser().getUsername());
                    		subscriberMDN.setUpgradeaccttime(new Timestamp());
                    		subMdndao.save(subscriberMDN);
                    		
                    		Integer notificationCode=null;
                    		if(upgradeStatus.intValue()== CmFinoFIX.SubscriberUpgradeStatus_Approve.intValue()){
                    			pocket.setPocketTemplateByPockettemplateid(lakuPandaiAdvancedTemplateList.get(0));
                    			pocketDAO.save(pocket);
                    			error.setErrorDescription(MessageText._("Request for Subscriber Upgraded is Approved successfully"));
                    			notificationCode=CmFinoFIX.NotificationCode_SubscriberUpgradeRequestApproved;
                    			log.info("Request for Subscriber Upgraded Approved successfully");
                    		}else{
                    			error.setErrorDescription(MessageText._("Request for Subscriber Upgraded is Rejected successfully"));
                    			notificationCode=CmFinoFIX.NotificationCode_SubscriberUpgradeRequestRejected;
                    			log.info("Request for Subscriber Upgraded Rejected successfully");
                    		}
                    		error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
                    		sendSMS(subscriberMDN,notificationCode);
                		}else{
                			error.setErrorDescription(MessageText._("LakuPandaiAdvanceTemplate Not Available."));
                        	return error;
                		}
                		
                		
                		
                	}else{
                		error.setErrorDescription(MessageText._("Subscriber Not Have LakuPandaiBasicTemplate Pocket."));
                    	return error;
                	}
                }else{
                	error.setErrorDescription(MessageText._("LakuPandaiBasicTemplate Not Available."));
                	return error;
                }
        		
        	}else{
        		error.setErrorDescription(MessageText._("Subscriber Upgrade Not Allowed "));
            	return error;
        	}
        	
        	
            
            
    		
        }
        
        
        
        
        
		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSSubscriberUpgrade,realMsg.DumpFields());
		
		
		Transaction transaction = null;
		ServiceCharge serviceCharge = new ServiceCharge();
		ChannelCode channelCode   =	ChannelCodeService.getChannelCodeByChannelCode("2");
//		String transactionIdentifier=transactionIdentifierService.generateTransactionIdentifier(subscriberMDN.getMDN());
		
		serviceCharge.setSourceMDN(null);
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(channelCode.getId().longValue());
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.SUBSCRIBER_UPGRADE);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(transactionsLog.getId().longValue());
//		serviceCharge.setTransactionIdentifier(transactionIdentifier);

		try{
			
			transaction = transactionChargingService.getCharge(serviceCharge);

		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			error.setCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			error.setErrorDescription(MessageText._("ServiceNotAvailable"));
        	return error;
		
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			error.setCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			error.setErrorDescription(MessageText._("ServiceNotAvailable"));
        	return error;
		}
		
		sctl = transaction.getServiceChargeTransactionLog();
		
		
        
		return error;
    }
	private void sendSMS (SubscriberMdn subscriberMDN , Integer notificationCode) {

		try{
			
			Subscriber subscriber = subscriberMDN.getSubscriber();
			String mdn2 = subscriberMDN.getMdn();
			
			NotificationWrapper smsNotificationWrapper = new NotificationWrapper();
			smsNotificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			smsNotificationWrapper.setCode(notificationCode);
			smsNotificationWrapper.setDestMDN(mdn2);
			smsNotificationWrapper.setLanguage(Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()));
			smsNotificationWrapper.setFirstName(subscriber.getFirstname());
	    	smsNotificationWrapper.setLastName(subscriber.getLastname());
			
	    	String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper,true);
			
			
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
