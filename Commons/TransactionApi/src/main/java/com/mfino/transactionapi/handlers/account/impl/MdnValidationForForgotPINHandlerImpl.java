package com.mfino.transactionapi.handlers.account.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMForgotPinInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.MdnValidationForForgotPINHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.MDNvalidationforForgotPINXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

@Service("MdnValidationForForgotPINHandlerImpl")
public class MdnValidationForForgotPINHandlerImpl extends FIXMessageHandler implements MdnValidationForForgotPINHandler{

	private static Logger	log	= LoggerFactory.getLogger(MdnValidationForForgotPINHandlerImpl.class);
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public XMLResult handle(TransactionDetails transactionDetails) {
		log.info("MdnValidationForForgotPINHandlerImpl :: Handling MDN for ForgotPIN webapi request for "+transactionDetails.getSourceMDN());
		
		XMLResult result = new MDNvalidationforForgotPINXMLResult();
		
		CMForgotPinInquiry forgotPin=new CMForgotPinInquiry();
		forgotPin.setSourceMDN(transactionDetails.getSourceMDN());
		
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ForgotPinInquiry, forgotPin.DumpFields());
		result.setSourceMDN(transactionDetails.getSourceMDN());
		result.setTransactionTime(transactionsLog.getTransactiontime());
		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
		
		if(subscriberMDN!=null){
            Pocket bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), CmFinoFIX.PocketType_BankAccount, 
            		CmFinoFIX.Commodity_Money);
            if(bankPocket != null && bankPocket.getStatus().intValue() == CmFinoFIX.PocketStatus_Active){
				result.setNotificationCode(CmFinoFIX.NotificationCode_BankNotificationForForgotPin);
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
     			 return result;
			}
            
            Pocket emoneyPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), CmFinoFIX.PocketType_SVA, 
            		CmFinoFIX.Commodity_Money);
			if(emoneyPocket != null && emoneyPocket.getStatus().intValue() == CmFinoFIX.PocketStatus_Active){
				result.setNotificationCode(CmFinoFIX.NotificationCode_EmoneyNotificationForForgotPin);
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
      			  return result;
			}
			return result;
		  }
		else {
			log.info("MDN not found");
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			return result;
		}
	}
}
