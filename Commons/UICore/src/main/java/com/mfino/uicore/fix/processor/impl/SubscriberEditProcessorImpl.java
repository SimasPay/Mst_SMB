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
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.dao.query.EnumTextQuery;
import com.mfino.domain.Address;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.EnumText;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberEdit;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SubscriberEditProcessor;

@Service("SubscriberEditProcessorImpl")
public class SubscriberEditProcessorImpl extends BaseFixProcessor implements SubscriberEditProcessor {

	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	private SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
	private PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	
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
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("In SubscriberEditProcessorImpl Process method");
		CMJSSubscriberEdit realMsg = (CMJSSubscriberEdit) msg;
		CMJSError error = new CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		
		SubscriberMdn subscriberMDN = subMdndao.getById(realMsg.getMDNID());
		if(subscriberMDN == null){
        	error.setErrorDescription(MessageText._("Invalid MDN ID"));
        	return error;
        }
    	Subscriber subscriber = subscriberMDN.getSubscriber();
		String actionString = realMsg.getaction();
		log.info("Action is :"+actionString);
		SubscriberUpgradeData subscriberUpgradeData = subscriberUpgradeDataDAO.getSubmitedRequestData(
				subscriberMDN.getId(), CmFinoFIX.SubscriberActivity_Edit_Subscriber_Details);
		
        if (StringUtils.equals(actionString, "default")){
    		realMsg.settotal(0);
    		CMJSSubscriberEdit.CGEntries entry = new CMJSSubscriberEdit.CGEntries();
    		
    		if(subscriberUpgradeData != null){
        		realMsg.allocateEntries(2);
        		displaySubmitedRequestData(subscriberMDN, subscriberUpgradeData, entry);
        		
        		CMJSSubscriberEdit.CGEntries entryOldValue = new CMJSSubscriberEdit.CGEntries();
        		displayExistingSubscriberData(subscriberMDN, subscriber, entryOldValue);
        		
        		realMsg.getEntries()[0] = entry;
        		realMsg.getEntries()[1] = entryOldValue;
        		realMsg.settotal(2);
    		} else{
        		realMsg.allocateEntries(1);
        		displayExistingSubscriberData(subscriberMDN, subscriber, entry);
        		realMsg.getEntries()[0] = entry;
        		realMsg.settotal(1);
    		}
    		
        	realMsg.setsuccess(Boolean.TRUE);
        	return realMsg;
        	
        } else{
        	if(subscriberUpgradeData == null){
        		error.setErrorDescription(MessageText._("Subscriber Edit data is not available"));
        		return error;
        	}
        		
        	if(realMsg.getSubscriberUpgradeStatus() == CmFinoFIX.SubscriberUpgradeStatus_Approve){
	        	approveSubscriberEdit(realMsg, subscriberMDN, subscriber, subscriberUpgradeData);
	        	
				error.setErrorDescription(MessageText._("Request for Subscriber Upgrade is Approved successfully"));
				error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				log.info("Request for Subscriber Upgraded Approved successfully");
        	} else {
				error.setErrorDescription(MessageText._("Request for Subscriber Upgrade is Rejected successfully"));
				error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				log.info("Request for Subscriber Upgraded Rejected successfully");
			}

    		error.setsuccess(true);
        	subscriberUpgradeData.setSubsActivityComments(realMsg.getUpgradeAcctComments());
			subscriberUpgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
			subscriberUpgradeData.setSubsActivityApprovedBY(userService.getCurrentUser().getUsername());
			subscriberUpgradeData.setSubsActivityAprvTime(new Timestamp());
			subscriberUpgradeDataDAO.save(subscriberUpgradeData);
        }
        TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSSubscriberEdit, realMsg.DumpFields());
		
		ChannelCode channelCode   =	channelCodeService.getChannelCodeByChannelCode("2");

		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(null);
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(channelCode.getId());
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.SUBSCRIBER_EDIT);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(transactionsLog.getId());

		try{
			transactionChargingService.getCharge(serviceCharge);
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
		
		return error;
	}

	private void approveSubscriberEdit(CMJSSubscriberEdit realMsg,
			SubscriberMdn subscriberMDN, Subscriber subscriber,
			SubscriberUpgradeData subscriberUpgradeData) {
		
		if (!(subscriberUpgradeData.getSubscriberStatus().equals(subscriberMDN.getStatus()))) {
			if (!CmFinoFIX.MDNStatus_Retired.equals(subscriberUpgradeData.getSubscriberStatus())) {
				log.info("Subscriber:"+subscriber.getId()+" Force Close Requested field is updated to " + 
						userService.getCurrentUser().getUsername() + " by user:"+getLoggedUserNameWithIP());
				subscriberMDN.setIsforcecloserequested(true);
			}
			subscriberMDN.setStatus(subscriberUpgradeData.getSubscriberStatus());
			subscriberMDN.setStatustime(new Timestamp());
			subscriberMDN.getSubscriber().setStatus(subscriberUpgradeData.getSubscriberStatus());
			subscriberMDN.getSubscriber().setStatustime(new Timestamp());
			subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriberMDN.getSubscriber(), true);
		} else{
			subscriber.setLanguage(subscriberUpgradeData.getLanguage());
			subscriber.setFirstname(subscriberUpgradeData.getFullName());
			subscriber.setEmail(subscriberUpgradeData.getEmail());
			
			subscriber.setAddressBySubscriberaddressid(subscriberUpgradeData.getAddress());
			subscriber.setNotificationmethod(subscriberUpgradeData.getNotificationMethod());
			subscriber.setRestrictions(subscriberUpgradeData.getSubscriberRestriction());
			
			subscriberMDN.setIdtype(subscriberUpgradeData.getIdType());
			subscriberMDN.setIdnumber(subscriberUpgradeData.getIdNumber());
			subscriberMDN.setKtpdocumentpath(subscriberUpgradeData.getIdCardScanPath());
			subscriberMDN.setRestrictions(subscriberUpgradeData.getSubscriberRestriction());
			
			if(StringUtils.isNotBlank(subscriberUpgradeData.getBankAccountNumber())){
				Pocket existingBankPocket = subscriberService.getDefaultPocket(subscriberMDN.getMdn(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
				if(existingBankPocket != null) {
					log.info("Bank pocket already exists for subscriber with mdn " + subscriberMDN.getMdn());
					String existingAccountNo = existingBankPocket.getCardpan();
					if(!existingAccountNo.equals(subscriberUpgradeData.getBankAccountNumber())) {
						log.info("Updating the old bank a/c no " + existingAccountNo + " with the new a/c no "+realMsg.getAccountNumber() + "for subscriber with mdn "+ subscriberMDN.getMdn());
						existingBankPocket.setCardpan(subscriberUpgradeData.getBankAccountNumber());
						pocketDao.save(existingBankPocket);         
					}
				}
			}
		}
		
		subMdndao.save(subscriberMDN);
		subscriberService.save(subscriber);
	}

	private void displayExistingSubscriberData(SubscriberMdn subscriberMDN,
			Subscriber subscriber, CMJSSubscriberEdit.CGEntries entry) {
		String idTypeValue = getIdTypeCode(subscriberMDN.getIdtype());
		entry.setEmail(subscriber.getEmail());
		entry.setFirstName(subscriber.getFirstname());
		entry.setIDType(idTypeValue);
		entry.setIDNumber(subscriberMDN.getIdnumber());
		entry.setKTPDocumentPath(subscriberMDN.getKtpdocumentpath());
		entry.setMDNID(subscriberMDN.getId());
		entry.setMDN(subscriberMDN.getMdn());
		Pocket existingBankPocket = subscriberService.getDefaultPocket(subscriberMDN.getMdn(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		if(existingBankPocket != null){
			entry.setAccountNumber(existingBankPocket.getCardpan());
		}else{
			entry.setAccountNumber("#");
		}
		Address address = subscriber.getAddressBySubscriberaddressid();
		if(address != null){
			entry.setCity(address.getCity());
			entry.setRegionName(address.getRegionname());
			entry.setState(address.getState());
			entry.setSubState(address.getSubstate());
			entry.setStreetAddress(address.getLine1());
		}
		entry.setNotificationMethod(subscriber.getNotificationmethod());
		entry.setMDNRestrictions(subscriberMDN.getRestrictions());
		entry.setLanguage(subscriber.getLanguage());
		entry.setSubscriberStatus(subscriber.getStatus());
	}

	private void displaySubmitedRequestData(SubscriberMdn subscriberMDN,
			SubscriberUpgradeData subscriberUpgradeData,
			CMJSSubscriberEdit.CGEntries entry) {
		String idTypeValue = getIdTypeCode(subscriberUpgradeData.getIdType());
		entry.setEmail(subscriberUpgradeData.getEmail());
		entry.setFirstName(subscriberUpgradeData.getFullName());
		entry.setIDType(idTypeValue);
		entry.setIDNumber(subscriberUpgradeData.getIdNumber());
		entry.setKTPDocumentPath(subscriberUpgradeData.getIdCardScanPath());
		entry.setMDNID(subscriberUpgradeData.getId());
		entry.setMDN(subscriberMDN.getMdn());
		Pocket existingBankPocket = subscriberService.getDefaultPocket(subscriberMDN.getMdn(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		if(existingBankPocket != null){
			entry.setAccountNumber(subscriberUpgradeData.getBankAccountNumber());
		}else{
			entry.setAccountNumber("#");
		}
		Address address = subscriberUpgradeData.getAddress();
		if(address != null){
			entry.setCity(address.getCity());
			entry.setRegionName(address.getRegionname());
			entry.setState(address.getState());
			entry.setSubState(address.getSubstate());
			entry.setStreetAddress(address.getLine1());
		}
		entry.setNotificationMethod(subscriberUpgradeData.getNotificationMethod());
		entry.setMDNRestrictions(subscriberUpgradeData.getSubscriberRestriction());
		entry.setLanguage(subscriberUpgradeData.getLanguage());
		entry.setSubscriberStatus(subscriberUpgradeData.getSubscriberStatus());
	}

	private String getIdTypeCode(String idTypeValue) {
		if(!StringUtils.isNumeric(idTypeValue)){
			EnumTextQuery enumTextQuery = new EnumTextQuery();
			enumTextQuery.setTagId(CmFinoFIX.TagID_IDTypeForKycUpgrade);
			enumTextQuery.setDisplayText(idTypeValue);
			List<EnumText> enumTexts = enumTextService.getEnumText(enumTextQuery);
			if(enumTexts != null){
				EnumText enumText = enumTexts.get(0);
				idTypeValue = enumText.getEnumcode();
			}
		}
		return idTypeValue;
	}

}
