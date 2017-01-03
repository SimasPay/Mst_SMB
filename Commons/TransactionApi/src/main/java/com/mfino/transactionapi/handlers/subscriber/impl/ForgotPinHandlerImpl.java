package com.mfino.transactionapi.handlers.subscriber.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMResetPinByOTP;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
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
import com.mfino.util.DateUtil;
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
	 @Qualifier("MFAServiceImpl")
	 private MFAService mfaService;

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
		resetPin.setSourceApplication((int)cc.getChannelsourceapplication());
		resetPin.setChannelCode(cc.getChannelcode());
		resetPin.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		isHttps = transDetails.isHttps();
		
		log.info("Handling Subscriber Forgot PIN webapi request");
		XMLResult result = new ResetPinByOTPXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		TransactionLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_Forgotpin, resetPin.DumpFields());
		resetPin.setTransactionID(transactionLog.getId().longValue());

		result.setSourceMessage(resetPin);
		result.setTransactionTime(transactionLog.getTransactiontime());
		result.setTransactionID(transactionLog.getId().longValue());
		
 		try{
 			String clearPin = transDetails.getNewPIN();
 			resetPin.setNewPin(clearPin);
 			resetPin.setConfirmPin(clearPin);
 		}
 		catch(Exception e){
 			log.error("Exception occured while decrypting pin ");
 			e.printStackTrace();
 			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing);
 			return result;
 		}
		
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(resetPin.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberForResetPinRequest(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+resetPin.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			if(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(srcSubscriberMDN.getRestrictions())){
				Timestamp blockTimeEnd = new Timestamp(DateUtil.addHours(srcSubscriberMDN.getStatustime(), systemParametersService.getInteger(SystemParameterKeys.ABSOLUTE_LOCK_DURATION_HOURS)));
				Long remainingTime = (blockTimeEnd.getTime() - new Date().getTime()) / (1000*60);
				Long remainingTimeMin = remainingTime%60;
				Long remainingTimeHours = remainingTime/60;
				result.setRemainingBlockTimeMinutes(remainingTimeMin.toString());
				result.setRemainingBlockTimeHours(remainingTimeHours.toString());
				if(remainingTime > 0) {
					result.setNotificationCode(CmFinoFIX.NotificationCode_OtpGenBlockedForLockedAccount);
				}
			}
			return result;

		}
		
		Subscriber subscriber = srcSubscriberMDN.getSubscriber();
		addCompanyANDLanguageToResult(srcSubscriberMDN, result);

		/**
		 * no need of these validations when using HSM hashed pin since pin would be 
		 * generated from the hash and it bound be correct length
		 */ 
		if(!ConfigurationUtil.getuseHashedPIN()){
			
			if (resetPin.getNewPin().length() != systemParametersService.getPinLength()) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_ChangeEPINFailedInvalidPINLength);
				return result;
			}
			
			if (!StringUtils.isNumeric(resetPin.getNewPin())) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_OnlyNumericPinAllowed);
				return result;
			}
			
		} else {
			log.info("Since hashed pin is enabled, pin length and pin strength checks are not performed");
			
			log.info("checking for new pin strength for subscribermdn "+resetPin.getSourceMDN() );
			
			if(!MfinoUtil.isPinStrongEnough(resetPin.getNewPin())){
			   log.info("The pin is not strong enough for subscribermdn "+resetPin.getSourceMDN() );
			   result.setNotificationCode(CmFinoFIX.NotificationCode_PinNotStrongEnough);
				return result;
			}
			
			log.info("Pin passed strength conditions");
		}

		ServiceChargeTxnLog sctl = sctlService.getBySCTLID(transDetails.getSctlId());
		result.setSctlID(sctl.getId().longValue());

		//Validate OTP 
		if(mfaService.isMFATransaction(transDetails.getServiceName(), transDetails.getTransactionName(), cc.getId().longValue())){
			   
			   if(resetPin.getOTP() == null || !(mfaService.isValidOTP(resetPin.getOTP() ,sctl.getId().longValue(), srcSubscriberMDN.getMdn()))){
			    
			    result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidMFAOTP);
			    return result;
			   }
			  }
		log.info("OTP validation Successfull");

		try {

			//String calcPIN = MfinoUtil.calculateDigestPin(changePin.getSourceMDN(), changePin.getNewPin());
			String calcPIN = mfinoUtilService.modifyPINForStoring(resetPin.getSourceMDN(), resetPin.getNewPin());
			srcSubscriberMDN.setDigestedpin(calcPIN);
			String authToken = MfinoUtil.calculateAuthorizationToken(resetPin.getSourceMDN(), resetPin.getNewPin());
			srcSubscriberMDN.setAuthorizationtoken(authToken);
			srcSubscriberMDN.setLastapppinchange(new Timestamp());
			subscriberMdnService.saveSubscriberMDN(srcSubscriberMDN);
		}
		catch (Exception ex) {
			log.error("Exception occured while updating the new pin", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
			if (sctl != null) {
				transactionChargingService.failTheTransaction(sctl, MessageText._("Change Pin transaction Falied"));
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

	/*private void absoluteLockSubscriber(Subscriber subscriber, SubscriberMdn srcSubscriberMDN) {
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
		subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
		srcSubscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_InActive);
		srcSubscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
		srcSubscriberMDN.setDigestedpin(null);
		subscriberService.saveSubscriber(subscriber);
	
	}*/
	/*private int getNumberOfRemainingTrials(int currentOtpTrials) {
		int maxOtpTrials = systemParametersService.getInteger(SystemParameterKeys.MAX_OTP_TRAILS);
		return (maxOtpTrials-currentOtpTrials);
	}

	private boolean isOtpExpired(SubscriberMdn srcSubscriberMDN) {
		if(srcSubscriberMDN.getOtpexpirationtime().after(new Date())) {
			return false;
		}
		return true;
	}*/
}