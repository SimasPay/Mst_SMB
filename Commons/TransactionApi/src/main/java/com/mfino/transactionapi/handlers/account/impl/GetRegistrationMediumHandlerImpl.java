package com.mfino.transactionapi.handlers.account.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.dao.query.EnumTextQuery;
import com.mfino.domain.EnumText;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetRegistrationMedium;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.GetRegistrationMediumHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.RegistrationMediumXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

@Service("GetRegistrationMediumHandlerImpl") 
public class GetRegistrationMediumHandlerImpl extends FIXMessageHandler implements GetRegistrationMediumHandler{
	
	private static Logger log = LoggerFactory.getLogger(GetRegistrationMediumHandlerImpl.class);
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	public XMLResult handle(TransactionDetails transactionDetails) {

        CMGetRegistrationMedium getRegistrationMedium = new CMGetRegistrationMedium();
    	getRegistrationMedium.setSourceMDN(transactionDetails.getSourceMDN());
		getRegistrationMedium.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		getRegistrationMedium.setChannelCode(transactionDetails.getChannelCode());
		
		log.info("Handling Get Subscriber registration medium webapi request::From"+getRegistrationMedium.getSourceMDN());
		XMLResult result = new RegistrationMediumXMLResult();
		

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_GetRegistrationMedium,getRegistrationMedium.DumpFields());
		
		getRegistrationMedium.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(getRegistrationMedium);
		result.setTransactionTime(transactionsLog.getTransactiontime());
	
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(getRegistrationMedium.getSourceMDN());
		if(sourceMDN==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			log.error("Entered MDN is not registered");
 			return result;
		}
		Subscriber subscriber = sourceMDN.getSubscriber();
		if(subscriber == null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			log.error("No Subscriber found with the given MDN in subscriber table");
 			return result;
		}

		EnumTextQuery enumTextQuery = new EnumTextQuery();
		enumTextQuery.setTagId(CmFinoFIX.TagID_RegistrationMedium);
		enumTextQuery.setEnumCode(subscriber.getRegistrationmedium().toString());
		enumTextQuery.setLanguage(CmFinoFIX.Language_English);

		List<EnumText> lstEnumTexts = enumTextService.getEnumText(enumTextQuery);

		String registrationMedium;
		if(CollectionUtils.isNotEmpty(lstEnumTexts)){
			registrationMedium = lstEnumTexts.get(0).getEnumvalue();
		}
		else{
			result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
			log.error("empty result obtained from enumText table for tagID:"+CmFinoFIX.TagID_RegistrationMedium+" and enumCode: "+subscriber.getRegistrationmedium().toString());
 			return result;
		}
		log.info("Successfully obtained registration medium:"+registrationMedium);
		result.setDestinationMDN(sourceMDN.getMdn());
		result.setRegistrationMedium(registrationMedium);
		
		String status = enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, CmFinoFIX.Language_English, subscriber.getStatus());
		result.setStatus(status);
		
		log.info("Successfully obtained subscriber status :" + status );
		if(sourceMDN.getDigestedpin()==null && sourceMDN.getOtp()!=null 
				&& subscriber.getStatus() == CmFinoFIX.SubscriberStatus_Active){
			result.setResetPinRequested(BOOL_TRUE);
		}else{
			result.setResetPinRequested(BOOL_FALSE);
		}
		if(subscriber.getActivationtime()!=null){
			result.setIsAlreadyActivated(BOOL_TRUE);
		}
		else{
			result.setIsAlreadyActivated(BOOL_FALSE);
		}
 		return result;
		
	}

}
