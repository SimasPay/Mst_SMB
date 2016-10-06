package com.mfino.transactionapi.handlers.account.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKey;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyFromBank;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.GetUserAPIKeyHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.UserAPIKeyXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

@Service("GetUserAPIKeyHandlerImpl") 
public class GetUserAPIKeyHandlerImpl extends FIXMessageHandler implements GetUserAPIKeyHandler  {

	private static Logger log = LoggerFactory.getLogger(GetUserAPIKeyHandlerImpl.class);
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	

	public Result handle(TransactionDetails transactionDetails) {
		CMGetUserAPIKey getUserAPIKey = new CMGetUserAPIKey();
		getUserAPIKey.setSourceMDN(transactionDetails.getSourceMDN());
		getUserAPIKey.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		getUserAPIKey.setChannelCode(transactionDetails.getChannelCode());
		UserAPIKeyXMLResult result = new UserAPIKeyXMLResult();
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_GetUserAPIKey,getUserAPIKey.DumpFields());
				getUserAPIKey.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(getUserAPIKey);
		result.setTransactionTime(transactionsLog.getTransactiontime());
		
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(getUserAPIKey.getSourceMDN());
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
		if (StringUtils.isNotBlank(sourceMDN.getUserapikey())) {
			log.info("Returning the user api key from local db ...");
			result.setUserAPIKey(sourceMDN.getUserapikey());
			result.setNotificationCode(CmFinoFIX.NotificationCode_GetUserAPIKeySuccess);
			return result;
		}		
		CFIXMsg response = super.process(getUserAPIKey);
		if(response instanceof CMGetUserAPIKeyFromBank){
			CMGetUserAPIKeyFromBank bankResponse = (CMGetUserAPIKeyFromBank) response;
				if(StringUtils.isNotBlank(bankResponse.getUserAPIKey()))
				{
					sourceMDN.setUserapikey(bankResponse.getUserAPIKey());
					subscriberMdnService.saveSubscriberMDN(sourceMDN);
					result.setUserAPIKey(bankResponse.getUserAPIKey());
					result.setNotificationCode(CmFinoFIX.NotificationCode_GetUserAPIKeySuccess);
					return result;
				}
				else{
					result.setNotificationCode(CmFinoFIX.NotificationCode_GetUserAPIKeyFailed);
					return result;
				}			
		}else{
			result.setNotificationCode(CmFinoFIX.NotificationCode_GetUserAPIKeyFailed);
			return result;
		}
		
	
	}

}
