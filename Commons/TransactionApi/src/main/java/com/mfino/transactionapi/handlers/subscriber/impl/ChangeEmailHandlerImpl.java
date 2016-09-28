package com.mfino.transactionapi.handlers.subscriber.impl;

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
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMChangeEmail;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.XMLResult;
import com.mfino.service.MailService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.ChangeEmailHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Srikanth
 * 
 */
@Service("ChangeEmailHandlerImpl")
public class ChangeEmailHandlerImpl extends FIXMessageHandler implements ChangeEmailHandler{
	private static Logger	log	= LoggerFactory.getLogger(ChangeEmailHandlerImpl.class);

	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService tcs;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
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
		log.info("Handling Change Email webapi request");
		XMLResult result = new ChangeEmailXMLResult();
		ChannelCode cc = transactionDetails.getCc();
		CMChangeEmail changeEmail= new CMChangeEmail();
		changeEmail.setPin(transactionDetails.getSourcePIN());
		changeEmail.setSourceMDN(transactionDetails.getSourceMDN());
		changeEmail.setNewEmail(transactionDetails.getNewEmail());
		changeEmail.setConfirmEmail(transactionDetails.getConfirmEmail());
		changeEmail.setSourceApplication((int)cc.getChannelsourceapplication());
		changeEmail.setChannelCode(cc.getChannelcode());
		changeEmail.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		TransactionsLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ChangeEmail, changeEmail.DumpFields());
		changeEmail.setTransactionID(transactionLog.getID());

		result.setSourceMessage(changeEmail);
		result.setTransactionTime(transactionLog.getTransactionTime());
		result.setTransactionID(transactionLog.getID());

		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(changeEmail.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(subscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+changeEmail.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		validationResult = transactionApiValidationService.validatePin(subscriberMDN, changeEmail.getPin());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: " + changeEmail.getSourceMDN());
			result.setNumberOfTriesLeft((int)(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - subscriberMDN.getWrongpincount()));
			result.setNotificationCode(validationResult);
			return result;
		}

 		addCompanyANDLanguageToResult(subscriberMDN, result);
		
		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(changeEmail.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(StringUtils.isNotBlank(changeEmail.getChannelCode()) ? Long.valueOf(changeEmail.getChannelCode()) : null);
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CHANGEEMAIL);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(changeEmail.getTransactionID());
		sc.setTransactionIdentifier(changeEmail.getTransactionIdentifier());

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
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		result.setSctlID(sctl.getID());
		Subscriber subscriber = subscriberMDN.getSubscriber();
		try {			
			subscriber.setEmail(changeEmail.getNewEmail());
			subscriber.setIsemailverified(BOOL_FALSE);
 			subscriberService.saveSubscriber(subscriber);
		}
		catch (Exception ex) {
			log.error("Exception occured while updating the new Email", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
			if (sctl != null) {
				tcs.failTheTransaction(sctl, MessageText._("Change Email transaction Falied"));
			}
 			return result;
		}
		if(systemParametersService.getIsEmailVerificationNeeded()) {
			mailService.generateEmailVerificationMail(subscriber, changeEmail.getNewEmail()); //send Email verification mail
		}		
		if (sctl != null) {
			sctl.setCalculatedCharge(BigDecimal.ZERO);
			tcs.completeTheTransaction(sctl);
		}
		result.setNotificationCode(CmFinoFIX.NotificationCode_ChangeEmailCompleted);
 		return result;

	}
}