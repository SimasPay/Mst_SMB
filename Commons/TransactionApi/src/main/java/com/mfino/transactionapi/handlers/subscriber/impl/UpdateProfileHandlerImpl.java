package com.mfino.transactionapi.handlers.subscriber.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMChangeEmail;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.XMLResult;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.UpdateProfileHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.Base64;
import com.mfino.util.ConfigurationUtil;

@Service("UpdateProfileHandlerImpl")
public class UpdateProfileHandlerImpl extends FIXMessageHandler implements UpdateProfileHandler{
	private static Logger	log	= LoggerFactory.getLogger(UpdateProfileHandlerImpl.class);
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService tcs;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	public XMLResult handle(TransactionDetails transactionDetails) {
		log.info("Handling Update Profile webapi request");
		XMLResult result = new ChangeEmailXMLResult();
		ChannelCode cc = transactionDetails.getCc();
		CMChangeEmail updateProfile= new CMChangeEmail();
		updateProfile.setPin(transactionDetails.getSourcePIN());
		updateProfile.setSourceMDN(transactionDetails.getSourceMDN());
		updateProfile.setSourceApplication(cc.getChannelsourceapplication());
		updateProfile.setChannelCode(cc.getChannelcode());
		updateProfile.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		TransactionLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ChangeEmail, updateProfile.DumpFields());
		updateProfile.setTransactionID(transactionLog.getId());

		result.setSourceMessage(updateProfile);
		result.setTransactionTime(transactionLog.getTransactiontime());
		result.setTransactionID(transactionLog.getId());

		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(updateProfile.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(subscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+updateProfile.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		validationResult = transactionApiValidationService.validatePin(subscriberMDN, updateProfile.getPin());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: " + updateProfile.getSourceMDN());
			result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - subscriberMDN.getWrongpincount());
			result.setNotificationCode(validationResult);
			return result;
		}

 		addCompanyANDLanguageToResult(subscriberMDN, result);
		
		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(updateProfile.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(StringUtils.isNotBlank(updateProfile.getChannelCode()) ? Long.valueOf(updateProfile.getChannelCode()) : null);
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_UPDATE_PROFILE);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(updateProfile.getTransactionID());
		sc.setTransactionIdentifier(updateProfile.getTransactionIdentifier());

		try{
			transaction =tcs.getCharge(sc);
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
 			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
 			return result;
		}
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		
		result.setSctlID(sctl.getId());
		try {			
			if(!StringUtils.isBlank(transactionDetails.getProfileImageString())){
			String profileImageString = transactionDetails.getProfileImageString(); 
			  
			  byte[] profileImageByteArray= Base64.decode(profileImageString);
			 
			  FileOutputStream fileOuputStream = new FileOutputStream(ConfigurationUtil.getSubscriberProfileImageFilePath() + File.separator + 
					  updateProfile.getSourceMDN() + ".jpg");
			     fileOuputStream.write(profileImageByteArray);
			     fileOuputStream.close();
			     
			String profileImagePath = updateProfile.getSourceMDN() + ".jpg";
			subscriberMDN.setProfileImagePath(profileImagePath);
			}
			subscriberMdnService.saveSubscriberMDN(subscriberMDN);
		}
		catch (Exception ex) {
			log.error("Exception occured while updating profile", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_UpdateProfileFailed);
			if (sctl != null) {
				tcs.failTheTransaction(sctl, MessageText._("Update Profile transaction Falied"));
			}
 			return result;
		}	
		if (sctl != null) {
			sctl.setCalculatedcharge(BigDecimal.ZERO);
			tcs.completeTheTransaction(sctl);
		}
		result.setNotificationCode(CmFinoFIX.NotificationCode_UpdateProfileCompleted);
 		return result;

	}
}