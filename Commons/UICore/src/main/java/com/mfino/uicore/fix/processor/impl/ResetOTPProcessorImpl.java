/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Partner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSResetOTP;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PartnerService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ResetOTPProcessor;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

/**
 *
 * @author Maruthi
 */
@Service("ResetOTPProcessorImpl")
public class ResetOTPProcessorImpl extends BaseFixProcessor implements ResetOTPProcessor{

    private static SubscriberMDNDAO subMdndao=DAOFactory.getInstance().getSubscriberMdnDAO();
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {

        CMJSResetOTP realMsg = (CMJSResetOTP) msg;
        CMJSError error = new CMJSError();
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        SubscriberMDN subscriberMDN = subMdndao.getById(realMsg.getMDNID());
        if(subscriberMDN==null){
        	error.setErrorDescription(MessageText._("Invalid MDN ID"));
        	return error;
        }
        Subscriber subscriber = subscriberMDN.getSubscriber();
        if(!(subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Initialized)
        		||subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Registered))||subscriberMDN.getOTP()==null){
        	error.setErrorDescription(MessageText._("OneTimePin generation not allowed"));
        	return error;
        }
		Integer OTPLength = systemParametersService.getOTPLength();
        String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), oneTimePin);
		subscriberMDN.setOTP(digestPin1);
		subscriberMDN.setOTPExpirationTime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
		subscriberMDN.setActivationWrongOTPCount(0);
		subscriberMDN.setOtpRetryCount(0);
		subMdndao.save(subscriberMDN);
		String mdn = subscriberMDN.getMDN();
		
		NotificationWrapper wrapper = new NotificationWrapper();
		wrapper.setOneTimePin(oneTimePin);
		wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		wrapper.setCode(CmFinoFIX.NotificationCode_New_OTP_Success);
		wrapper.setDestMDN(mdn);
		wrapper.setLanguage(subscriberMDN.getSubscriber().getLanguage());
		wrapper.setFirstName(subscriberMDN.getSubscriber().getFirstName());
		wrapper.setLastName(subscriberMDN.getSubscriber().getLastName());					
		
		String message = notificationMessageParserService.buildMessage(wrapper,true);
		smsService.setDestinationMDN(mdn);
		smsService.setMessage(message);
		smsService.setNotificationCode(wrapper.getCode());
		smsService.asyncSendSMS();
		
		String email=subscriber.getEmail();
		String to = subscriber.getFirstName();
		String mailMessage;
			if(subscriberServiceExtended.isSubscriberEmailVerified(subscriber)) {
			if(subscriber.getType().equals(CmFinoFIX.SubscriberType_Partner)&&subscriber.getPartnerFromSubscriberID().iterator().next().getAuthorizedEmail()!=null){
				Partner partner =subscriber.getPartnerFromSubscriberID().iterator().next();
				NotificationWrapper notificationWrapper = partnerService.genratePartnerOTPMessage(partner, oneTimePin, mdn, CmFinoFIX.NotificationMethod_Email);
				notificationWrapper.setDestMDN(mdn);
				if(subscriberMDN != null)
	            {
	            	notificationWrapper.setFirstName(subscriberMDN.getSubscriber().getFirstName());
	            	notificationWrapper.setLastName(subscriberMDN.getSubscriber().getLastName());					
	            }
				mailMessage = notificationMessageParserService.buildMessage(notificationWrapper,true);
				mailService.asyncSendEmail(email, to, "Activation", mailMessage);
			}else if(((subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0) && email != null){
				wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
				mailMessage = notificationMessageParserService.buildMessage(wrapper,true);			
				mailService.asyncSendEmail(email, to, "New OTP", message);
				} else {
				log.info("Email is not sent since it is not verified for subscriber with ID ->" + subscriber.getID());
			}
		}
		
		error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		error.setErrorDescription(MessageText._("New OneTimePin sent successfully"));
        
		return error;
    }

}
