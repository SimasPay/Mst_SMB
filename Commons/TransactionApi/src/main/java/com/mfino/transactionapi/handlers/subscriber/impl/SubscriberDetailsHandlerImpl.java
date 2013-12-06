package com.mfino.transactionapi.handlers.subscriber.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.ZTEDataPush;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationThroughWeb;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.ZTEDataPushService;
import com.mfino.service.impl.ZTEDataPushServiceImpl;
import com.mfino.transactionapi.handlers.subscriber.SubscriberDetailsHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberDetailsXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/*
 *
 * @author Srikanth
 */

@Service("SubscriberDetailsHandlerImpl")
public class SubscriberDetailsHandlerImpl extends FIXMessageHandler implements SubscriberDetailsHandler{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("ZTEDataPushServiceImpl")
	private ZTEDataPushService zteDataPushService;
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Result handle(TransactionDetails txnDetails) {
		CMSubscriberRegistrationThroughWeb subscriberRegistration = new CMSubscriberRegistrationThroughWeb();
		subscriberRegistration.setSourceMDN(txnDetails.getSourceMDN());
		ChannelCode cc = txnDetails.getCc();
		subscriberRegistration.setChannelCode(cc.getChannelCode());
		
		log.info("Handling SubscriberDetails webapi request for MDN:"+subscriberRegistration.getSourceMDN());
		ZTEDataPush zteDataPush = zteDataPushService.getByMDN(subscriberRegistration.getSourceMDN());
		XMLResult result = new SubscriberDetailsXMLResult();
		if(zteDataPush != null){
			log.info(String.format("Handling SubscriberDetails : Successfully retrieved zteDataPushRecord(ID:%d) for MDN:%s",zteDataPush.getID(),subscriberRegistration.getSourceMDN()));
			result.setZteDataPush(zteDataPush);
			result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberDetailsSuccessMessage);
		}else{
			log.info(String.format("Handling SubscriberDetails : Failed to retrieve zteDataPushRecord for MDN:%s",subscriberRegistration.getSourceMDN()));
			result.setZteDataPush(null);
			result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberDetailsFailMessage);
		}
		return result;
	}

	
}
