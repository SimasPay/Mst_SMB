package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.AddressDAO;
import com.mfino.dao.BranchCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.PocketTemplateQuery;
import com.mfino.domain.BranchCodes;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MfinoUser;
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
import com.mfino.fix.CmFinoFIX.CMJSSubscriberUpgradeKyc;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SubscriberUpgradeKycProcessor;

@Service("SubscriberUpgradeKycProcessorImpl")
public class SubscriberUpgradeKycProcessorImpl extends BaseFixProcessor implements
		SubscriberUpgradeKycProcessor {
	
	private static SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
	private static PocketTemplateDAO pocketTemplateDAO = DAOFactory.getInstance().getPocketTemplateDao();
	private static PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
	private static SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
	private static AddressDAO addressDao = DAOFactory.getInstance().getAddressDAO();
	private static BranchCodeDAO branchCodeDao = DAOFactory.getInstance().getBranchCodeDAO(); 
	
	private static final String DEFAULT_BRANCH = "000";
	
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("In SubscriberUpgradeKycProcessImpl Process method");
		
		CMJSSubscriberUpgradeKyc realMsg = (CMJSSubscriberUpgradeKyc) msg;
		CMJSError error = new CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		TransactionLog transactionsLog = null;
		ServiceChargeTxnLog sctl = null;
		
		SubscriberMdn subscriberMDN = subMdndao.getById(realMsg.getID());
        
		if(subscriberMDN == null){
        	error.setErrorDescription(MessageText._("Invalid MDN ID"));
        	return error;
        }
        
        if(subscriberMDN.getUpgradeacctstatus() != null && 
			subscriberMDN.getUpgradeacctstatus() == CmFinoFIX.SubscriberUpgradeStatus_Approve &&
			subscriberMDN.getUpgradeacctstatus() == CmFinoFIX.SubscriberUpgradeStatus_Reject ){
        	
    		error.setErrorDescription(MessageText._("Subscriber Upgrade Not Allowed "));
        	return error;
    	}
        
        Subscriber subscriber = subscriberMDN.getSubscriber();
        Integer subscriberStatus = subscriber.getStatus();
        
        if( !subscriberStatus.equals(CmFinoFIX.SubscriberStatus_Active) ){
        	
        	error.setErrorDescription(MessageText._("Subscriber Should be Active! "));
        	return error;
        }
        
		PocketTemplateQuery pocketTemplateQuery = new PocketTemplateQuery();
        pocketTemplateQuery.setPocketType(CmFinoFIX.PocketType_SVA);
        pocketTemplateQuery.setDescriptionSearch("Emoney-UnBanked");
        
        List<PocketTemplate> eMoneyNonKycTemplateList = pocketTemplateDAO.get(pocketTemplateQuery);
        
		String actionString = realMsg.getaction();
		log.info("Action is : "+actionString);
		
		if(subscriberMDN.getUpgradeacctstatus() == CmFinoFIX.SubscriberUpgradeStatus_Initialized){
			
			String makerUsername = subscriberMDN.getUpgradeacctrequestby();
			MfinoUser makerUser = userService.getByUserName(makerUsername);
			
			Long checkerUserBranchId = userService.getCurrentUser().getBranchcodeid();
			BranchCodes branchCodes = branchCodeDao.getById(checkerUserBranchId);
			
			if((makerUser != null && makerUser.getBranchcodeid() == checkerUserBranchId) || 
					(branchCodes != null && StringUtils.equals(branchCodes.getBranchcode(), DEFAULT_BRANCH))){
				
				pocketTemplateQuery.setDescriptionSearch("Emoney-SemiBanked");
            	List<PocketTemplate> eMoneyKycTemplateList = pocketTemplateDAO.get(pocketTemplateQuery);
            	
            	if (eMoneyNonKycTemplateList != null && eMoneyNonKycTemplateList.size() > 0) {
            		Pocket nonKycPocket = getNonKycPocket(subscriberMDN, eMoneyNonKycTemplateList);
            		
            		if (nonKycPocket == null){
            			error.setErrorDescription(MessageText._("Subscriber Not Have Emoney-UnBanked Pocket."));
                    	return error;
            		} 
            		
            		if (eMoneyKycTemplateList == null || eMoneyKycTemplateList.size() == 0){
        				error.setErrorDescription(MessageText._("Emoney-SemiBanked Not Available."));
                    	return error;
        			}
        			
        			Integer notificationCode = updateStatus(realMsg, error,
							subscriberMDN, subscriber, eMoneyKycTemplateList,
							nonKycPocket);
            		
            		error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            		sendSMS(subscriberMDN,notificationCode);
            		
            	} else{
                	error.setErrorDescription(MessageText._("Emoney-UnBanked Not Available."));
                	return error;
                }
			} else{
				error.setErrorDescription(MessageText._("Admin Branch is not available or different"));
            	return error;
			}
			
		} else{
    		error.setErrorDescription(MessageText._("Subscriber Upgrade Not Allowed "));
        	return error;
    	}
		
		
		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSSubscriberUpgradeKyc, realMsg.DumpFields());
		
		Transaction transaction = null;
		ChannelCode channelCode   =	channelCodeService.getChannelCodeByChannelCode("2");

		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(null);
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(channelCode.getId());
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.SUBSCRIBER_UPGRADE_KYC);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(transactionsLog.getId());

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

	private Integer updateStatus(CMJSSubscriberUpgradeKyc realMsg,
			CMJSError error, SubscriberMdn subscriberMDN,
			Subscriber subscriber, List<PocketTemplate> eMoneyKycTemplateList,
			Pocket nonKycPocket) {
		
		Integer upgradeStatus = realMsg.getSubscriberUpgradeStatus();
		subscriberMDN.setUpgradeacctstatus(upgradeStatus);
		subscriberMDN.setUpgradeacctcomments(realMsg.getUpgradeAcctComments());
		subscriberMDN.setUpgradeacctapprovedby(userService.getCurrentUser().getUsername());
		subscriberMDN.setUpgradeaccttime(new Timestamp());
		
		Integer notificationCode = null;
		if (upgradeStatus == CmFinoFIX.SubscriberUpgradeStatus_Approve){			
			nonKycPocket.setPocketTemplateByPockettemplateid(eMoneyKycTemplateList.get(0));
			pocketDAO.save(nonKycPocket);
			error.setErrorDescription(MessageText._("Request for Subscriber Upgraded is Approved successfully"));
			notificationCode = CmFinoFIX.NotificationCode_SubscriberUpgradeRequestApproved;
			log.info("Request for Subscriber Upgraded Approved successfully");
			
		} else if(upgradeStatus == CmFinoFIX.SubscriberUpgradeKycStatus_Revision){
			error.setErrorDescription(MessageText._("Request for Subscriber Need Revision"));
			notificationCode = CmFinoFIX.NotificationCode_SubscriberUpgradeRequestRevision;
			log.info("Request for Subscriber Need Revision");
			
		} else{
			subscriberMDN.setUpgradeacctstatus(null);
			subscriberMDN.setKtpdocumentpath(null);
			subscriberMDN.setIdtype(null);
			subscriberMDN.setIdnumber(null);
			
			addressDao.delete(subscriber.getAddressBySubscriberaddressid());
			
			subscriber.setBirthplace(null);
			subscriber.setMothersmaidenname(null);
			subscriber.setAddressBySubscriberaddressid(null);
			subscriber.setDateofbirth(null);
			subscriberDao.save(subscriber);
			
			error.setErrorDescription(MessageText._("Request for Subscriber Upgraded is Rejected successfully"));
			notificationCode = CmFinoFIX.NotificationCode_SubscriberUpgradeRequestRejected;
			log.info("Request for Subscriber Upgraded Rejected successfully");
		}

		subMdndao.save(subscriberMDN);
		return notificationCode;
	}

	private Pocket getNonKycPocket(SubscriberMdn subscriberMDN,
			List<PocketTemplate> eMoneyNonKycTemplateList) {
		PocketQuery pocketQuery= new PocketQuery();
		pocketQuery.setPocketTemplateID(eMoneyNonKycTemplateList.get(0).getId());
		pocketQuery.setMdnIDSearch(subscriberMDN.getId());
		List<Pocket> pocketList = pocketDAO.get(pocketQuery);
		if(pocketList != null && pocketList.size() > 0)
			return pocketList.get(0);
		else
			return null;
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
	    	smsNotificationWrapper.setLastName(subscriber.getLastname());
			
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
