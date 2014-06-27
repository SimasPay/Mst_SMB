package com.mfino.transactionapi.handlers.subscriber.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MdnOtp;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMForgotPinInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.ForgotPinInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Srikanth
 * 
 */
@Service("ForgotPinInquiryHandlerImpl")
public class ForgotPinInquiryHandlerImpl extends FIXMessageHandler implements ForgotPinInquiryHandler{
	private static Logger	log	= LoggerFactory.getLogger(ForgotPinInquiryHandlerImpl.class);


	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private  NotificationMessageParserService notificationMsgParser ;

	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService ;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	public Result handle(TransactionDetails transactionDetails) {
		ChannelCode cc = transactionDetails.getCc();
		
		CMForgotPinInquiry forgotPinInquiry= new CMForgotPinInquiry();		
		forgotPinInquiry.setSourceMDN(transactionDetails.getSourceMDN());			
		forgotPinInquiry.setSourceApplication(cc.getChannelSourceApplication());
		forgotPinInquiry.setChannelCode(cc.getChannelCode());
		forgotPinInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());				
		log.info("Handling Forgot pin inquiry webapi request for MDN: " +  forgotPinInquiry.getSourceMDN());
		XMLResult result = new ChangeEmailXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);

		TransactionsLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ForgotPinInquiry, forgotPinInquiry.DumpFields());
		forgotPinInquiry.setTransactionID(transactionLog.getID());

		result.setSourceMessage(forgotPinInquiry);
		result.setTransactionTime(transactionLog.getTransactionTime());
		result.setTransactionID(transactionLog.getID());
		
		SubscriberMDN subscriberMDN = subscriberMdnService.getByMDN(forgotPinInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberForResetPinInquiryRequest(subscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+forgotPinInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		Subscriber subscriber = subscriberMDN.getSubscriber();
		addCompanyANDLanguageToResult(subscriberMDN, result);

		boolean sendOtpToOtherMdn = false;
		String mdn = subscriberMDN.getMDN();
		String value = systemParametersService.getString(SystemParameterKeys.SEND_OTP_TO_OTHER_MDN);
		log.info("SEND_OTP_TO_OTHER_MDN param value is :" + value);
		if(value != null) {
			sendOtpToOtherMdn = Boolean.parseBoolean(value);
		}
		if(sendOtpToOtherMdn) { //if sendOtpToOtherMdn is true send otp to other mdn else send to mdn
			mdn = subscriberMDN.getOtherMDN();
			log.info("Subscriber's other MDN is :" + mdn);
			if(StringUtils.isBlank(mdn)) {
				log.info("Forgot Pin Inquiry failed because subscriber's OtherMDN field is blank");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ForgotPinInquiryFailed);
				return result;
			}			
		}		
		
		if(!isNewOtpGenRequired(subscriberMDN, result)) {
			return result;
		}
			
		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(forgotPinInquiry.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(StringUtils.isNotBlank(forgotPinInquiry.getChannelCode()) ? Long.valueOf(forgotPinInquiry.getChannelCode()) : null);
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_FORGOTPIN);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(forgotPinInquiry.getTransactionID());
		sc.setTransactionIdentifier(forgotPinInquiry.getTransactionIdentifier());

		try{
			transaction =transactionChargingService.getCharge(sc);
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		result.setSctlID(sctl.getID());
        
		int otpLength = systemParametersService.getOTPLength();
 		String otp = MfinoUtil.generateOTP(otpLength);
 		String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), otp);
 		subscriberMDN.setOTP(digestPin1);
 		subscriberMDN.setOTPExpirationTime(new Timestamp(DateUtil.addMinutes(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION_MINUTES))));
 		subscriberMDN.setOtpRetryCount(0);
 		
 		subscriberMDN.setDigestedPIN(null);
 		subscriberMDN.setAuthorizationToken(null);
 		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
 		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
 		subscriberMDN.setStatusTime(new Timestamp());
		subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
 		subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
 		subscriberService.saveSubscriber(subscriber);
 		subscriberMdnService.saveSubscriberMDN(subscriberMDN);
 		//Building notification message
    	NotificationWrapper notificationWrapper = new NotificationWrapper();
    	notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
    	notificationWrapper.setCode(CmFinoFIX.NotificationCode_ForgotPinOTPSent);
    	notificationWrapper.setOneTimePin(otp);
    	notificationWrapper.setLanguage(subscriber.getLanguage());
    	
        String smsMessage = notificationMsgParser.buildMessage(notificationWrapper, false);
        smsService.setDestinationMDN(mdn); 
        smsService.setMessage(smsMessage);
        smsService.setNotificationCode(notificationWrapper.getCode());
        smsService.asyncSendSMS();
        log.info("Successfully generated OTP for "+ forgotPinInquiry.getSourceMDN() + " and sent sms to MDN:" + mdn);
        result.setOtherMDN(mdn);
		result.setNotificationCode(CmFinoFIX.NotificationCode_ForgotPinInquiryCompleted);
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		return result;
	}
	
	
	private boolean isNewOtpGenRequired(SubscriberMDN subscriberMDN, XMLResult result) {
		if(subscriberMDN.getOTP()==null){
			return true;
		}
		
		if(isOtpExpired(subscriberMDN)){
			Timestamp blockTimeEnd = new Timestamp(DateUtil.addMinutes(subscriberMDN.getStatusTime(), systemParametersService.getInteger(SystemParameterKeys.RESEND_OTP_BLOCK_DURATION_MINUTES)));
			Long remainingTime = getRemainingMinutesToUnblockOtp(blockTimeEnd); 
			if(remainingTime <= 0) {
				return true;
			}
			else{
				result.setRemainingBlockTime(remainingTime.toString());
				result.setNotificationCode(CmFinoFIX.NotificationCode_OtpGenerationBlocked);
				return false;
			}
		}
		else if(hasExceededMaxTrials(subscriberMDN)) {
			Timestamp blockTimeEnd = new Timestamp(DateUtil.addHours(subscriberMDN.getStatusTime(), systemParametersService.getInteger(SystemParameterKeys.ABSOLUTE_LOCK_DURATION_HOURS)));
			Long remainingTime = getRemainingMinutesToUnblockOtp(blockTimeEnd); 
			if(remainingTime <= 0 ) {
				return true;
			}
			else{
				remainingTime = remainingTime/60 + (remainingTime%60==0?0L:1L);
				result.setRemainingBlockTime(remainingTime.toString());
				result.setNotificationCode(CmFinoFIX.NotificationCode_OtpGenBlockedForLockedAccount);
				return false;
			}
		}
		return true;
	}
	
	private Long getRemainingMinutesToUnblockOtp(Timestamp blockTimeEnd) {
		Long remainingTime = (blockTimeEnd.getTime() - new Date().getTime()) / (1000*60);
		return remainingTime;
	}

	private boolean hasExceededMaxTrials(SubscriberMDN subscriberMDN) {
		if(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(subscriberMDN.getRestrictions())){
			return true;
		}
		return false;
	}

	private boolean isOtpExpired(SubscriberMDN subscriberMDN) {
		if(subscriberMDN.getOTPExpirationTime().after(new Date())) {
			return false;
		}
		return true;
	}
}