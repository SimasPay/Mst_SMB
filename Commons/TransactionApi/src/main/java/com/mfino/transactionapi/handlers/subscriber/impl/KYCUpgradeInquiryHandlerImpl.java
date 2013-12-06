/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMKYCUpgradeInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.KYCUpgradeInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.KYCUpgradeInquiryXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sreenath
 *
 */
@Service("KYCUpgradeInquiryHandlerImpl")
public class KYCUpgradeInquiryHandlerImpl extends FIXMessageHandler	implements KYCUpgradeInquiryHandler {
	
	private static Logger log	= LoggerFactory.getLogger(KYCUpgradeInquiryHandlerImpl.class);
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService ;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Override
	public Result handle(TransactionDetails transactionDetails) {
		ChannelCode cc = transactionDetails.getCc();

		CMKYCUpgradeInquiry kycUpgradeInquiry = new CMKYCUpgradeInquiry();
		kycUpgradeInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		kycUpgradeInquiry.setSourceApplication(cc.getChannelSourceApplication());
		kycUpgradeInquiry.setChannelCode(cc.getChannelCode());
		kycUpgradeInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());				
		log.info("Handling validate MDN For Upgrade webapi request");
		XMLResult result = new KYCUpgradeInquiryXMLResult();
		TransactionsLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_KYCUpgradeInquiry, kycUpgradeInquiry.DumpFields());
		kycUpgradeInquiry.setTransactionID(transactionLog.getID());

		result.setSourceMessage(kycUpgradeInquiry);
		result.setTransactionTime(transactionLog.getTransactionTime());
		result.setTransactionID(transactionLog.getID());
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		SubscriberMDN subscriberMDN = subscriberMdnService.getByMDN(kycUpgradeInquiry.getSourceMDN());
		if(subscriberMDN==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			return result;
		}
		result.setSourceMDN(kycUpgradeInquiry.getSourceMDN());
		if(subscriberMDN.getSubscriber()!=null && !(CmFinoFIX.SubscriberStatus_Active.equals(subscriberMDN.getSubscriber().getStatus()))){
    		String status = enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, CmFinoFIX.Language_English, subscriberMDN.getSubscriber().getStatus());
			result.setStatus(status);
			result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberStatusNotValidForKYCUpgrade);
			return result;
		}
		if(subscriberMDN.getSubscriber().getKYCLevelByKYCLevel()!=null && !(CmFinoFIX.SubscriberKYCLevel_NoKyc.equals(subscriberMDN.getSubscriber().getKYCLevelByKYCLevel().getKYCLevel().intValue()))){
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidKYCLevel);
			return result;
		}
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		result.setNotificationCode(CmFinoFIX.NotificationCode_KycUpgradeInquirySuccessful);
		return result;
	}

}
