/**
 *
 */
package com.mfino.scheduler.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Partner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.scheduler.service.SubscriberOTPLifeCycleService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PartnerService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

/**
 * @author Bala Sunku
 *
 */
@Service("SubscriberOTPLifeCycleServiceImpl")
public class SubscriberOTPLifeCycleServiceImpl  implements SubscriberOTPLifeCycleService {
	private static Logger log = LoggerFactory.getLogger(SubscriberOTPLifeCycleServiceImpl.class);

	private static long MILLI_SECONDS_PER_DAY = 24*60*60*1000;
	private static long TIME_TO_SUSPEND_OF_NO_ACTIVATION = 2;
	private static long MILLI_SECONDS_PER_HOUR = 60*60*1000;
	private static long OTP_TIMEOUT_DURATION = 24;
	private static long AUTOMATIC_RESEND_OTP = 0;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Override
	
	public void updateSubscriberOTPStatus() {
		
		    HibernateTransactionManager txManager = (HibernateTransactionManager) this.getTxManager();
		    SessionFactory sessionFactory = txManager.getSessionFactory();
		    SessionHolder sessionHolder = (SessionHolder)TransactionSynchronizationManager.getResource(sessionFactory);
		    Session session = sessionHolder.getSession();
		    log.info("BEGIN updateSubscriberOTPStatus");
			SubscriberMdnQuery query = new SubscriberMdnQuery();
			long days = -1l;
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_SUSPEND_OF_NO_ACTIVATION);
			if (days != -1) {
				TIME_TO_SUSPEND_OF_NO_ACTIVATION = days;
			}
			TIME_TO_SUSPEND_OF_NO_ACTIVATION = TIME_TO_SUSPEND_OF_NO_ACTIVATION * MILLI_SECONDS_PER_DAY;

			long automaticResendOTP = -1l;
			automaticResendOTP = systemParametersService.getLong(SystemParameterKeys.AUTOMATIC_RESEND_OTP);
			if (automaticResendOTP != -1) {
				AUTOMATIC_RESEND_OTP = automaticResendOTP;
			}


			long hours = -1l;
			hours = systemParametersService.getLong(SystemParameterKeys.OTP_TIMEOUT_DURATION);
			if (hours != -1) {
				OTP_TIMEOUT_DURATION = hours;
			}
			OTP_TIMEOUT_DURATION = OTP_TIMEOUT_DURATION * MILLI_SECONDS_PER_HOUR;

			Integer[] status = new Integer[1];
			status[0] = CmFinoFIX.SubscriberStatus_Initialized;

			query.setStatusIn(status);
			int  countofInitialized = subscriberMdnService.getCountForStatusForMdns(query);
			
			int firstResult = 0;
	        int batchSize = 1000;
	        while (firstResult < countofInitialized) {
	            query.setStart(firstResult);
	            query.setLimit(batchSize);
	            List<SubscriberMDN> results = subscriberMdnService.getStatusForMdns(query);
	            if (CollectionUtils.isNotEmpty(results)) {
					for (SubscriberMDN sm : results) {
						checkSubscriberOTP(sm);
					}
	            }
	            firstResult += results.size();           
	            results.clear();   
	            session.clear();

	        }
			
		log.info("END updateSubscriberOTPStatus");
	}

	
	private void checkSubscriberOTP(SubscriberMDN subscriberMDN){
		Subscriber subscriber = null;
		Timestamp now = new Timestamp();

		if (subscriberMDN != null) {
			subscriber = subscriberMDN.getSubscriber();
			log.info("Checking the Subscriber with id --> " + subscriber.getID() + " And status is --> " + subscriber.getStatus());
			if (CmFinoFIX.SubscriberStatus_Initialized.intValue() == subscriberMDN.getStatus()) {
				if (subscriber != null && subscriber.getType() != null && 
						(CmFinoFIX.SubscriberType_Subscriber.intValue() == subscriber.getType() || CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())
						&& subscriber.getActivationTime() == null) {
					if ( ((now.getTime() - subscriber.getCreateTime().getTime()) > TIME_TO_SUSPEND_OF_NO_ACTIVATION) &&
							((now.getTime() - subscriber.getStatusTime().getTime()) > TIME_TO_SUSPEND_OF_NO_ACTIVATION)) {
						suspendSubscriber(subscriber, subscriberMDN,now);
						if (subscriber.getType() != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
							Partner partner = getPartnerForSubscriber(subscriber);
							if (partner != null) {
								partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_Suspend);
								partnerService.savePartner(partner);
							}
						}
					}else if((AUTOMATIC_RESEND_OTP == 1) && isItTimeToSendOTP(subscriberMDN,now)){
						sendOTP(subscriberMDN);
					}
				}

			}
		}
	}

	private void suspendSubscriber(Subscriber subscriber, SubscriberMDN subscriberMDN,Timestamp now){

		subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
		subscriberMDN.setStatusTime(now);
		subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
		subscriber.setStatusTime(now);
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber, true);
		subscriberMdnService.saveSubscriberMDN(subscriberMDN);
		subscriberService.saveSubscriber(subscriber);
		log.info("Suspended the Subscriber because of no activation within the required time--> " + subscriber.getID());
	}



	private boolean isItTimeToSendOTP(SubscriberMDN subscriberMDN,Timestamp now){
		 Subscriber subscriber = subscriberMDN.getSubscriber();
		if(subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Initialized) && (subscriberMDN.getOTP()!=null)){
			if(now.getTime() > subscriberMDN.getOTPExpirationTime().getTime()){
				return true;
			}
        }
		return false;
	}


	private void sendOTP(SubscriberMDN subscriberMDN){
		Subscriber subscriber = subscriberMDN.getSubscriber();
		log.info("Sending Automatic OTP to subscriber with Id--> "+subscriber.getID());

		Integer OTPLength = systemParametersService.getOTPLength();
	    String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), oneTimePin);
		subscriberMDN.setOTP(digestPin1);
		subscriberMDN.setOTPExpirationTime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
		subscriberMdnService.saveSubscriberMDN(subscriberMDN);
		String mdn = subscriberMDN.getMDN();

		NotificationWrapper wrapper = new NotificationWrapper();
		wrapper.setOneTimePin(oneTimePin);
		wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		wrapper.setCode(CmFinoFIX.NotificationCode_New_OTP_Success);
		wrapper.setDestMDN(mdn);
		wrapper.setLanguage(subscriberMDN.getSubscriber().getLanguage());
		wrapper.setFirstName(subscriberMDN.getSubscriber().getFirstName());
		wrapper.setLastName(subscriberMDN.getSubscriber().getLastName());

		String smsMessage = notificationMessageParserService.buildMessage(wrapper,true);
		smsService.setDestinationMDN(mdn);
		smsService.setMessage(smsMessage);
		smsService.setNotificationCode(wrapper.getCode());
		smsService.asyncSendSMS();
		if(subscriberServiceExtended.isSubscriberEmailVerified(subscriber)) {
		if(subscriber.getType().equals(CmFinoFIX.SubscriberType_Partner)&&subscriber.getPartnerFromSubscriberID().iterator().next().getAuthorizedEmail()!=null){
			String email=subscriber.getEmail();
			String to = subscriber.getFirstName();
			Partner partner =subscriber.getPartnerFromSubscriberID().iterator().next();
			NotificationWrapper notification = partnerService.genratePartnerOTPMessage(partner, oneTimePin, mdn, CmFinoFIX.NotificationMethod_Email);
			notification.setDestMDN(mdn);
			if(subscriberMDN != null){
	           	notification.setFirstName(subscriberMDN.getSubscriber().getFirstName());
	           	notification.setLastName(subscriberMDN.getSubscriber().getLastName());
	        }
			String mailMessage = notificationMessageParserService.buildMessage(notification,true);
			mailService.asyncSendEmail(email, to, "Activation", mailMessage);
			}else if(((subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0) && subscriber.getEmail() != null){
				wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
				String emailMessage = notificationMessageParserService.buildMessage(wrapper,true);
				String email=subscriber.getEmail();
				String to = subscriber.getFirstName();
				mailService.asyncSendEmail(email, to, "New OTP", emailMessage);
		} 
		}else {
			log.info("Email not sent since it is not verified for subscriber with Id ->" + subscriber.getID());
		}
		log.info("Sent Automatic OTP to subscriber with Id--> "+subscriber.getID());
	}
	
	/**
	 * Returns the Partner for the given Subscriber
	 * 
	 * @param subscriber
	 * @return
	 */
	private Partner getPartnerForSubscriber(Subscriber subscriber) {
		Partner partner = null;
		if (subscriber != null) {
			Set<Partner> partners = subscriber.getPartnerFromSubscriberID();
			if ((partners != null) && (partners.size()!=0)) { 
				partner = partners.iterator().next();
			}
		}
		return partner;
	}



}
