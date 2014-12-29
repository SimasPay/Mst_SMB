package com.mfino.scheduler.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.SubscriberQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.scheduler.service.ActivationMsgService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
@Service("ActivationMsgServiceImpl")
public class ActivationMsgServiceImpl  implements ActivationMsgService{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static long MILLI_SECONDS_PER_DAY = 24*60*60*1000;
	private static long TIME_TO_SEND_MESSAGE = 2;
	 
	private ChannelCode channelCode;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	public  void getBulkUploadSubscriberList() {
		
		log.info("BEGIN Sending message to intialized bulk upload subscribers");
			
			channelCode = channelCodeService.getChannelCodeByChannelCode(CmFinoFIX.SourceApplication_BackEnd.toString());
	 
			SubscriberQuery query = new SubscriberQuery();
			long days = -1;
			days = systemParametersService.getLong(SystemParameterKeys.ACTIVATION_SMS_INTERVAL_BULKUPLOAD);
			if (days != -1) {
				TIME_TO_SEND_MESSAGE = days;
			}
			TIME_TO_SEND_MESSAGE = TIME_TO_SEND_MESSAGE * MILLI_SECONDS_PER_DAY;
			
			 Integer status  = CmFinoFIX.SubscriberStatus_Initialized;
			 Integer registrationMedium = CmFinoFIX.RegistrationMedium_BulkUpload;
 			 
			query.setStatus(status);
			query.setRegistrationMedium(registrationMedium);
			
			List<Subscriber> lst = subscriberService.getByQuery(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				for (Subscriber sm : lst) {
					checkTimeAndSendMessage(sm);
				} 
			}
		log.info("END send message");
	 }
	
	private void checkTimeAndSendMessage(Subscriber subscriberObject) {
 
		Timestamp now = new Timestamp();
		
	 	Set<SubscriberMDN> MDNList=subscriberObject.getSubscriberMDNFromSubscriberID();
	 	
	 	SubscriberMDN smdn = MDNList.toArray(new SubscriberMDN[0])[0];
	  	boolean smsRequired =false;
        
		if (subscriberObject != null) {
 			log.info("SMS service check: subscriber ID --> " + subscriberObject.getID() + " , status is --> " + subscriberObject.getStatus()+ " and registratoin medium --> " +subscriberObject.getRegistrationMedium());
		 	  
			if(subscriberObject.getLastNotificationTime()==null){
				smsRequired=true; 
			}
		   	else if ((now.getTime() - subscriberObject.getLastNotificationTime().getTime()) > TIME_TO_SEND_MESSAGE){
		   		smsRequired=true;
		   	}	 
   		}
		if(smsRequired==true){
			subscriberObject.setLastNotificationTime(now);
			sendSms(smdn);
			subscriberService.saveSubscriber(subscriberObject);
		}
 	}
	
	private void sendSms(SubscriberMDN destMDN){
		
		NotificationWrapper notification = new NotificationWrapper();
		Integer language = 0;
 		if(destMDN != null){
  			notification.setFirstName(destMDN.getSubscriber().getFirstName());
		 	notification.setLastName(destMDN.getSubscriber().getLastName());
		 	if (destMDN.getSubscriber().getLanguage() != null) {
		 		language = destMDN.getSubscriber().getLanguage();
		 	} else {
		 		language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		 	}
  		}
		notification.setLanguage(language);
		notification.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		notification.setCode(CmFinoFIX.NotificationCode_ActivationSMSTypeBulkUpload);

		String message1 = notificationMessageParserService.buildMessage(notification,true);
		smsService.setDestinationMDN(destMDN.getMDN());
		smsService.setMessage(message1);
		smsService.setNotificationCode(notification.getCode());
		smsService.asyncSendSMS();
	}
}
