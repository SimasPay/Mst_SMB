package com.mfino.transactionapi.handlers.subscriber.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.crypto.CryptographyService;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMResetPinByOTP;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.ForgotPinHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ResetPinByOTPXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Hemanth
 * 
 */
@Service("ForgotPinHandlerImpl")
public class ForgotPinHandlerImpl extends FIXMessageHandler implements ForgotPinHandler {
	private static Logger	log	= LoggerFactory.getLogger(ForgotPinHandlerImpl.class);
	private boolean isHttps;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;	

	@Autowired
	@Qualifier("MfinoUtilServiceImpl")
	private MfinoUtilService mfinoUtilService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;

	public Result handle(TransactionDetails transDetails) {
		
		CMResetPinByOTP resetPin = new CMResetPinByOTP();
		ChannelCode cc = transDetails.getCc();
		
		resetPin.setNewPin(transDetails.getNewPIN());
		resetPin.setConfirmPin(transDetails.getConfirmPIN());
		resetPin.setSourceMDN(transDetails.getSourceMDN());
		resetPin.setOTP(transDetails.getActivationOTP());
		resetPin.setSourceApplication(cc.getChannelSourceApplication());
		resetPin.setChannelCode(cc.getChannelCode());
		resetPin.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		isHttps = transDetails.isHttps();
		
		log.info("Handling Subscriber Forgot PIN webapi request");
		XMLResult result = new ResetPinByOTPXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		TransactionsLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ResetPinByOTP, resetPin.DumpFields());
		resetPin.setTransactionID(transactionLog.getID());

		result.setSourceMessage(resetPin);
		result.setTransactionTime(transactionLog.getTransactionTime());
		result.setTransactionID(transactionLog.getID());
		
 		try{
 			String clearPin = CryptographyService.decryptWithPrivateKey(transDetails.getNewPIN());
 			resetPin.setNewPin(clearPin);
 			resetPin.setConfirmPin(clearPin);
 		}
 		catch(Exception e){
 			log.error("Exception occured while decrypting pin ");
 			e.printStackTrace();
 			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing);
 			return result;
 		}
		
		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(resetPin.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberForResetPinRequest(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+resetPin.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;

		}
		
		Subscriber subscriber = srcSubscriberMDN.getSubscriber();
		addCompanyANDLanguageToResult(srcSubscriberMDN, result);

		/**
		 * no need of these validations when using HSM hashed pin since pin would be 
		 * generated from the hash and it bound be correct length
		 */ 
		if(!ConfigurationUtil.getuseHashedPIN())
		{
			if (resetPin.getNewPin().length() != systemParametersService.getPinLength()) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_ChangeEPINFailedInvalidPINLength);
				return result;
			}
			if (!StringUtils.isNumeric(resetPin.getNewPin())) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_OnlyNumericPinAllowed);
				return result;
			}
			
			log.info("checking for new pin strength for subscribermdn "+resetPin.getSourceMDN() );
			if(!MfinoUtil.isPinStrongEnough(resetPin.getNewPin())){
			   log.info("The pin is not strong enough for subscribermdn "+resetPin.getSourceMDN() );
			   result.setNotificationCode(CmFinoFIX.NotificationCode_PinNotStrongEnough);
				return result;
			}
			log.info("Pin passed strength conditions");
		}
		else
		{
			log.info("Since hashed pin is enabled, pin length and pin strength checks are not performed");
		}

		String originalOTP = srcSubscriberMDN.getOTP();

		//Validate OTP 
		if(originalOTP!=null && isOtpExpired(srcSubscriberMDN)){
			log.error("The otp entered has expired for mdn:"+ resetPin.getSourceMDN());
			result.setNotificationCode(CmFinoFIX.NotificationCode_OTPExpired);
			return result;
		}
		
		String receivedOTP=MfinoUtil.calculateDigestPin(resetPin.getSourceMDN(), resetPin.getOTP());
		if(!(receivedOTP.equals(originalOTP)))
		{
			srcSubscriberMDN.setOtpRetryCount(srcSubscriberMDN.getOtpRetryCount()+1);
			log.error("The otp entered is wrong for subscribermdn "+ resetPin.getSourceMDN());
			result.setNotificationCode(CmFinoFIX.NotificationCode_OTPInvalid);
			if(isMaxRetryExceededForOtp(srcSubscriberMDN)) {
				absoluteLockSubscriber(subscriber, srcSubscriberMDN);
				result.setNotificationCode(CmFinoFIX.NotificationCode_MDNIsRestricted);
				log.error("Account locked for mdn: "+ resetPin.getSourceMDN());
			}
			subscriberMdnService.saveSubscriberMDN(srcSubscriberMDN);
			return result;
		}
		srcSubscriberMDN.setOTP(null); // reseting the OTP to null as the OTP is used for new pin generation.
		srcSubscriberMDN.setOtpRetryCount(0);
		srcSubscriberMDN.setOTPExpirationTime(null);


		log.info("OTP validation Successfull");

		ServiceChargeTransactionLog sctl = sctlService.getBySCTLID(transDetails.getSctlId());
		result.setSctlID(sctl.getID());
		try {
			String calcPIN = mfinoUtilService.modifyPINForStoring(resetPin.getSourceMDN(), resetPin.getNewPin());
			srcSubscriberMDN.setDigestedPIN(calcPIN);
			String authToken = MfinoUtil.calculateAuthorizationToken(resetPin.getSourceMDN(), resetPin.getNewPin());
			srcSubscriberMDN.setAuthorizationToken(authToken);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
			srcSubscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriberService.saveSubscriber(subscriber);
			subscriberMdnService.saveSubscriberMDN(srcSubscriberMDN);
		}
		catch (Exception ex) {
			log.error("Exception occured while updating the new pin", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
			if (sctl != null) {
				transactionChargingService.failTheTransaction(sctl, MessageText._("Reset / Forgot Pin transaction Falied"));
			}
			return result;
		}
		if (sctl != null) {
			transactionChargingService.completeTheTransaction(sctl);
		}
		result.setNotificationCode(CmFinoFIX.NotificationCode_ResetPINCompleted);
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		return result;

	}

	private void absoluteLockSubscriber(Subscriber subscriber, SubscriberMDN srcSubscriberMDN) {
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
		subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
		srcSubscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_InActive);
		srcSubscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
		srcSubscriberMDN.setDigestedPIN(null);
		subscriberService.saveSubscriber(subscriber);
	}

	private boolean isMaxRetryExceededForOtp(SubscriberMDN srcSubscriberMDN) {
		int maxOtpTrials = systemParametersService.getInteger(SystemParameterKeys.MAX_OTP_TRAILS);
		int currentOtpTrials = srcSubscriberMDN.getOtpRetryCount();
		if( currentOtpTrials <= maxOtpTrials) {
			return false;
		}
		return true;		
	}

	private boolean isOtpExpired(SubscriberMDN srcSubscriberMDN) {
		if(srcSubscriberMDN.getOTPExpirationTime().after(new Date())) {
			return false;
		}
		return true;
	}
}