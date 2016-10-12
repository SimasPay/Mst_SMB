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
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Partner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
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
	            List<SubscriberMdn> results = subscriberMdnService.getStatusForMdns(query);
	            if (CollectionUtils.isNotEmpty(results)) {
					for (SubscriberMdn sm : results) {
						checkSubscriberOTP(sm);
					}
	            }
	            firstResult += results.size();           
	            results.clear();   
	            session.clear();

	        }
			
		log.info("END updateSubscriberOTPStatus");
	}

	
	private void checkSubscriberOTP(SubscriberMdn subscriberMDN){
		Subscriber subscriber = null;
		Timestamp now = new Timestamp();

		if (subscriberMDN != null) {
			subscriber = subscriberMDN.getSubscriber();
			log.info("Checking the Subscriber with id --> " + subscriber.getId() + " And status is --> " + subscriber.getStatus());
			if (CmFinoFIX.SubscriberStatus_Initialized.intValue() == subscriberMDN.getStatus()) {
				if (subscriber != null && Long.valueOf(subscriber.getType() )!= null && 
						(CmFinoFIX.SubscriberType_Subscriber.intValue() == subscriber.getType() || CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())
						&& subscriber.getActivationtime() == null) {
					if ( ((now.getTime() - subscriber.getCreatetime().getTime()) > TIME_TO_SUSPEND_OF_NO_ACTIVATION) &&
							((now.getTime() - subscriber.getStatustime().getTime()) > TIME_TO_SUSPEND_OF_NO_ACTIVATION)) {
						suspendSubscriber(subscriber, subscriberMDN,now);
						if (Long.valueOf(subscriber.getType() )!= null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
							Partner partner = getPartnerForSubscriber(subscriber);
							if (partner != null) {
								partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_Suspend);
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

	private void suspendSubscriber(Subscriber subscriber, SubscriberMdn subscriberMDN,Timestamp now){

		subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
		subscriberMDN.setStatustime(now);
		subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
		subscriber.setStatustime(now);
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber, true);
		subscriberMdnService.saveSubscriberMDN(subscriberMDN);
		subscriberService.saveSubscriber(subscriber);
		log.info("Suspended the Subscriber because of no activation within the required time--> " + subscriber.getId());
	}



	private boolean isItTimeToSendOTP(SubscriberMdn subscriberMDN,Timestamp now){
		 Subscriber subscriber = subscriberMDN.getSubscriber();
		if(Long.valueOf(subscriber.getStatus()).equals(CmFinoFIX.SubscriberStatus_Initialized) && (subscriberMDN.getOtp()!=null)){
			if(now.getTime() > subscriberMDN.getOtpexpirationtime().getTime()){
				return true;
			}
        }
		return false;
	}


	private void sendOTP(SubscriberMdn subscriberMDN){
		Subscriber subscriber = subscriberMDN.getSubscriber();
		log.info("Sending Automatic OTP to subscriber with Id--> "+subscriber.getId());

		Integer OTPLength = systemParametersService.getOTPLength();
	    String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMdn(), oneTimePin);
		subscriberMDN.setOtp(digestPin1);
		subscriberMDN.setOtpexpirationtime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
		subscriberMdnService.saveSubscriberMDN(subscriberMDN);
		String mdn = subscriberMDN.getMdn();

		NotificationWrapper wrapper = new NotificationWrapper();
		wrapper.setOneTimePin(oneTimePin);
		wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		wrapper.setCode(CmFinoFIX.NotificationCode_New_OTP_Success);
		wrapper.setDestMDN(mdn);
		wrapper.setLanguage((int)subscriberMDN.getSubscriber().getLanguage());
		wrapper.setFirstName(subscriberMDN.getSubscriber().getFirstname());
		wrapper.setLastName(subscriberMDN.getSubscriber().getLastname());

		String smsMessage = notificationMessageParserService.buildMessage(wrapper,true);
		smsService.setDestinationMDN(mdn);
		smsService.setMessage(smsMessage);
		smsService.setNotificationCode(wrapper.getCode());
		smsService.asyncSendSMS();
		if(subscriberServiceExtended.isSubscriberEmailVerified(subscriber)) {
		if(Long.valueOf(subscriber.getType()).equals(CmFinoFIX.SubscriberType_Partner)&&subscriber.getPartners().iterator().next().getAuthorizedemail()!=null){
			String email=subscriber.getEmail();
			String to = subscriber.getFirstname();
			Partner partner =subscriber.getPartners().iterator().next();
			NotificationWrapper notification = partnerService.genratePartnerOTPMessage(partner, oneTimePin, mdn, CmFinoFIX.NotificationMethod_Email);
			notification.setDestMDN(mdn);
			if(subscriberMDN != null){
	           	notification.setFirstName(subscriberMDN.getSubscriber().getFirstname());
	           	notification.setLastName(subscriberMDN.getSubscriber().getLastname());
	        }
			String mailMessage = notificationMessageParserService.buildMessage(notification,true);
			mailService.asyncSendEmail(email, to, "Activation", mailMessage);
			}else if(((subscriber.getNotificationmethod() & CmFinoFIX.NotificationMethod_Email) > 0) && subscriber.getEmail() != null){
				wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
				String emailMessage = notificationMessageParserService.buildMessage(wrapper,true);
				String email=subscriber.getEmail();
				String to = subscriber.getFirstname();
				mailService.asyncSendEmail(email, to, "New OTP", emailMessage);
		} 
		}else {
			log.info("Email not sent since it is not verified for subscriber with Id ->" + subscriber.getId());
		}
		log.info("Sent Automatic OTP to subscriber with Id--> "+subscriber.getId());
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
			Set<Partner> partners = subscriber.getPartners();
			if ((partners != null) && (partners.size()!=0)) { 
				partner = partners.iterator().next();
			}
		}
		return partner;
	}



}
