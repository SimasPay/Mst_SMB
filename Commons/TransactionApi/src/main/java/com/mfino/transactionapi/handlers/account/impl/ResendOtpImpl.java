package com.mfino.transactionapi.handlers.account.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMResendOtp;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.XMLResult;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PartnerService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.ResendOtp;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ResendOtpXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

@Service("ResendOtpImpl")
public class ResendOtpImpl extends FIXMessageHandler implements ResendOtp{
	
	private static Logger log = LoggerFactory.getLogger(ResendOtpImpl.class);

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
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
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	public XMLResult handle(TransactionDetails transactionDetails) {
		log.info("Handling resend OTP webapi request::From"+transactionDetails.getSourceMDN());
		
		ChannelCode channelCode = transactionDetails.getCc();
		XMLResult result = new ResendOtpXMLResult();
		
		CMResendOtp resendOtp = new CMResendOtp();
		resendOtp.setSourceMDN(transactionDetails.getSourceMDN());
		resendOtp.setChannelCode(channelCode.getChannelcode());
		resendOtp.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());


		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ResendOtp,resendOtp.DumpFields());
		
		resendOtp.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(resendOtp);
		result.setTransactionTime(transactionsLog.getTransactiontime());

		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(resendOtp.getSourceMDN());
        if(sourceMDN==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			log.error("Invalid MDN.MDN not found: "+resendOtp.getSourceMDN());
			return result;
        }
	
        Subscriber subscriber = sourceMDN.getSubscriber();
        String mdn = sourceMDN.getMdn();
        if(!(subscriber.getStatus() == (CmFinoFIX.SubscriberStatus_Initialized.longValue())
        		||subscriber.getStatus() == (CmFinoFIX.SubscriberStatus_Registered.longValue()))
        		||sourceMDN.getOtp()==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_OtpGenerationNotAllowed);
			result.setDestinationMDN(mdn);
			log.error("OneTimePin generation not allowed for MDN: "+mdn+"MDN status is: "+subscriber.getStatus());
 			return result;
        }
		Integer OTPLength = systemParametersService.getOTPLength();
        String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(sourceMDN.getMdn(), oneTimePin);
		
		sourceMDN.setOtp(digestPin1);
		sourceMDN.setOtpexpirationtime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
	
		subscriberMdnService.saveSubscriberMDN(sourceMDN);

		NotificationWrapper wrapper = new NotificationWrapper();
		wrapper.setOneTimePin(oneTimePin);
		wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		wrapper.setCode(CmFinoFIX.NotificationCode_New_OTP_Success);
		wrapper.setSourceMDN(mdn);
		wrapper.setLanguage(new Integer(String.valueOf(sourceMDN.getSubscriber().getLanguage())));
		wrapper.setFirstName(sourceMDN.getSubscriber().getFirstname());
		wrapper.setLastName(sourceMDN.getSubscriber().getLastname());					
		
		String message = notificationMessageParserService.buildMessage(wrapper,true);
		smsService.setDestinationMDN(mdn);
		smsService.setMessage(message);
		smsService.setNotificationCode(wrapper.getCode());
		smsService.asyncSendSMS();
		
		String email=subscriber.getEmail();
		String to = subscriber.getFirstname();
		String mailMessage;
		if(subscriberServiceExtended.isSubscriberEmailVerified(subscriber)) {
		if(subscriber.getType() == (CmFinoFIX.SubscriberType_Partner.longValue())&&subscriber.getPartners().iterator().next().getAuthorizedemail()!=null){
			Partner partner =subscriber.getPartners().iterator().next();
			NotificationWrapper notificationWrapper = partnerService.genratePartnerOTPMessage(partner, oneTimePin, mdn, CmFinoFIX.NotificationMethod_Email);
			notificationWrapper.setDestMDN(mdn);
			if(sourceMDN != null)
            {
            	notificationWrapper.setFirstName(sourceMDN.getSubscriber().getFirstname());
            	notificationWrapper.setLastName(sourceMDN.getSubscriber().getLastname());					
            }
			mailMessage = notificationMessageParserService.buildMessage(notificationWrapper,true);
			mailService.asyncSendEmail(email, to, "Activation", mailMessage);
		}else if(((subscriber.getNotificationmethod() & CmFinoFIX.NotificationMethod_Email) > 0) && email != null){
			wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			mailMessage = notificationMessageParserService.buildMessage(wrapper,true);			
			mailService.asyncSendEmail(email, to, "New OTP", message);
		}
		} else {
			log.info("Email is not sent since it is not verified for subscriber with ID ->" + subscriber.getId());
		}
		result.setOneTimePin(oneTimePin);
		result.setMessage(message);
		result.setNotificationCode(CmFinoFIX.NotificationCode_New_OTP_Success);
 		return result;
		
	}

}
