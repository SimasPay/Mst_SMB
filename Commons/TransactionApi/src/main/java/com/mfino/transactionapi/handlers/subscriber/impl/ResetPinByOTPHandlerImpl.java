package com.mfino.transactionapi.handlers.subscriber.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.crypto.CryptographyService;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
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
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.ResetPinByOTPHandler;
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
@Service("ResetPinByOTPHandlerImpl")
public class ResetPinByOTPHandlerImpl extends FIXMessageHandler implements ResetPinByOTPHandler{
	private static Logger	log	= LoggerFactory.getLogger(ResetPinByOTPHandlerImpl.class);
	private boolean isHttps;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

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
		
		log.info("Handling Subscriber ResetPin By OTP webapi request");
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
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+resetPin.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;

		}
		
		addCompanyANDLanguageToResult(srcSubscriberMDN, result);

		if (!(resetPin.getNewPin().equals(resetPin.getConfirmPin()))) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_ChangeEPINFailedInvalidConfirmPIN);
			return result;
		}

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
		
		//Validate OTP 
		
		String receivedOTP=MfinoUtil.calculateDigestPin(resetPin.getSourceMDN(), resetPin.getOTP());
		String originalOTP = srcSubscriberMDN.getOTP();
		if(!(receivedOTP.equals(originalOTP)))
		{
			log.info("The otp entered is wrong for subscribermdn "+ resetPin.getSourceMDN());
			result.setNotificationCode(CmFinoFIX.NotificationCode_OTPInvalid);
			return result;
		}
		srcSubscriberMDN.setOTP(null); // reseting the OTP to null as the OTP is used for new pin generation.
		
		log.info("OTP validation Successfull");
		


		Transaction transactionDetails = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(resetPin.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(StringUtils.isNotBlank(resetPin.getChannelCode()) ? Long.valueOf(resetPin.getChannelCode()) : null);
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CHANGEPIN);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(resetPin.getTransactionID());
		sc.setTransactionIdentifier(resetPin.getTransactionIdentifier());

		try{
			transactionDetails =transactionChargingService.getCharge(sc);
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTransactionLog sctl = transactionDetails.getServiceChargeTransactionLog();
		result.setSctlID(sctl.getID());
		try {

			//String calcPIN = MfinoUtil.calculateDigestPin(resetPin.getSourceMDN(), resetPin.getNewPin());
			String calcPIN = mfinoUtilService.modifyPINForStoring(resetPin.getSourceMDN(), resetPin.getNewPin());
			srcSubscriberMDN.setDigestedPIN(calcPIN);
			String authToken = MfinoUtil.calculateAuthorizationToken(resetPin.getSourceMDN(), resetPin.getNewPin());
			srcSubscriberMDN.setAuthorizationToken(authToken);
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
			sctl.setCalculatedCharge(BigDecimal.ZERO);
			transactionChargingService.completeTheTransaction(sctl);
		}
		result.setNotificationCode(CmFinoFIX.NotificationCode_ChangePINCompleted);
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		return result;

	}
}