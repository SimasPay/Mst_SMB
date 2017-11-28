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
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
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
import com.mfino.util.ConfigurationUtil;
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
		resendOtp.setChannelCode(channelCode.getChannelCode());
		resendOtp.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());


		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ResendOtp,resendOtp.DumpFields());
		
		resendOtp.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(resendOtp);
		result.setTransactionTime(transactionsLog.getTransactionTime());

		SubscriberMDN sourceMDN = subscriberMdnService.getByMDN(resendOtp.getSourceMDN());
        if(sourceMDN==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_New_OTP_Success);
			log.error("Invalid MDN.MDN not found: "+resendOtp.getSourceMDN());
			return result;
        }
        
        Integer currentOtpTrials = (sourceMDN.getOtpRetryCount()) == null ? 0 : sourceMDN.getOtpRetryCount();
        sourceMDN.setOtpRetryCount(currentOtpTrials+1);
        
        if(getNumberOfRemainingTrials(sourceMDN.getOtpRetryCount()) < 0){
        	result.setNotificationCode(CmFinoFIX.NotificationCode_ExceedMaxResendOTP);
			result.setDestinationMDN(sourceMDN.getMDN());
 			return result;
        }
        
        Subscriber subscriber = sourceMDN.getSubscriber();
        String mdn = sourceMDN.getMDN();
        if(!(subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Initialized) 
        		||subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Registered))
        		||sourceMDN.getOTP()==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_OtpGenerationNotAllowed);
			result.setDestinationMDN(mdn);
			log.error("OneTimePin generation not allowed for MDN: "+mdn+"MDN status is: "+subscriber.getStatus());
 			return result;
        }
        
        if(sourceMDN != null && sourceMDN.getActivationWrongOTPCount() >= ConfigurationUtil.getMaxOTPActivationWrong()){
			log.error("OneTimePin generation not allowed for MDN: "+mdn+"Number of generation otp: "+ConfigurationUtil.getMaxOTPActivationWrong());
			result.setNotificationCode(CmFinoFIX.NotificationCode_ActivationBlocked);
			return result;
		}
        
		Integer OTPLength = systemParametersService.getOTPLength();
        String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(sourceMDN.getMDN(), oneTimePin);
		
		sourceMDN.setOTP(digestPin1);
		sourceMDN.setOTPExpirationTime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
	
		subscriberMdnService.saveSubscriberMDN(sourceMDN);

		NotificationWrapper wrapper = new NotificationWrapper();
		wrapper.setOneTimePin(oneTimePin);
		wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		wrapper.setCode(CmFinoFIX.NotificationCode_New_OTP_Success);
		wrapper.setSourceMDN(mdn);
		wrapper.setLanguage(sourceMDN.getSubscriber().getLanguage());
		wrapper.setFirstName(sourceMDN.getSubscriber().getFirstName());
		wrapper.setLastName(sourceMDN.getSubscriber().getLastName());					
		
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
			if(sourceMDN != null)
            {
            	notificationWrapper.setFirstName(sourceMDN.getSubscriber().getFirstName());
            	notificationWrapper.setLastName(sourceMDN.getSubscriber().getLastName());					
            }
			mailMessage = notificationMessageParserService.buildMessage(notificationWrapper,true);
			mailService.asyncSendEmail(email, to, "Activation", mailMessage);
		}else if(((subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0) && email != null){
			wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			mailMessage = notificationMessageParserService.buildMessage(wrapper,true);			
			mailService.asyncSendEmail(email, to, "New OTP", message);
		}
		} else {
			log.info("Email is not sent since it is not verified for subscriber with ID ->" + subscriber.getID());
		}
		//result.setOneTimePin(oneTimePin);
		result.setMessage(message);
		result.setNotificationCode(CmFinoFIX.NotificationCode_New_OTP_Success);
 		return result;
		
	}
	private int getNumberOfRemainingTrials(int currentOtpTrials) {
		int maxOtpTrials = systemParametersService.getInteger(SystemParameterKeys.MAX_OTP_TRAILS);
		return (maxOtpTrials-currentOtpTrials);
	}
}
