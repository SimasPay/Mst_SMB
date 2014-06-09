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
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMChangeOtherMDN;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.XMLResult;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.impl.TransactionLogServiceImpl;
import com.mfino.transactionapi.handlers.subscriber.ChangeOtherMDNHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Bala Sunku
 * 
 */
@Service("ChangeOtherMDNHandlerImpl")
public class ChangeOtherMDNHandlerImpl extends FIXMessageHandler implements ChangeOtherMDNHandler {
	private static Logger	log	= LoggerFactory.getLogger(ChangeOtherMDNHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService tcs;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;


	public XMLResult handle(TransactionDetails transactionDetails) {
		ChannelCode cc = transactionDetails.getCc();	 
		 
		CMChangeOtherMDN changeOtherMDN = new CMChangeOtherMDN();
		changeOtherMDN.setPin(transactionDetails.getSourcePIN());
		changeOtherMDN.setSourceMDN(transactionDetails.getSourceMDN());
		changeOtherMDN.setOtherMDN(transactionDetails.getOtherMdn());	
		changeOtherMDN.setSourceApplication(cc.getChannelSourceApplication());
		changeOtherMDN.setChannelCode(cc.getChannelCode());
		changeOtherMDN.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());


		log.info("Handling Change Other MDN webapi request");
		XMLResult result = new ChangeEmailXMLResult();
		TransactionLogServiceImpl transactionLogService = new TransactionLogServiceImpl();
		TransactionsLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ChangeOtherMDN, changeOtherMDN.DumpFields());
		changeOtherMDN.setTransactionID(transactionLog.getID());

		result.setSourceMessage(changeOtherMDN);
		result.setTransactionTime(transactionLog.getTransactionTime());
		result.setTransactionID(transactionLog.getID());

		SubscriberMDN subscriberMDN = subscriberMdnService.getByMDN(changeOtherMDN.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(subscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+changeOtherMDN.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		validationResult = transactionApiValidationService.validatePin(subscriberMDN, changeOtherMDN.getPin());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: " + changeOtherMDN.getSourceMDN());
			result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - subscriberMDN.getWrongPINCount());
			result.setNotificationCode(validationResult);
			return result;
		}

 		addCompanyANDLanguageToResult(subscriberMDN, result);

		Transaction transaction = null;
		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(changeOtherMDN.getSourceMDN());
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(StringUtils.isNotBlank(changeOtherMDN.getChannelCode()) ? Long.valueOf(changeOtherMDN.getChannelCode()) : null);
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CHANGEOTHERMDN);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(changeOtherMDN.getTransactionID());
		serviceCharge.setTransactionIdentifier(changeOtherMDN.getTransactionIdentifier());

		try{
			transaction =tcs.getCharge(serviceCharge);
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
		try {	
			subscriberMDN.setOtherMDN(changeOtherMDN.getOtherMDN());
			subscriberMdnService.saveSubscriberMDN(subscriberMDN);
		}
		catch (Exception ex) {
			log.error("Exception occured while changing the Other MDN", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
			if (sctl != null) {
				tcs.failTheTransaction(sctl, MessageText._("Change Other MDN transaction Falied"));
			}
 			return result;
		}
		if (sctl != null) {
			tcs.completeTheTransaction(sctl);
		}
		result.setNotificationCode(CmFinoFIX.NotificationCode_ChangeOtherMDNCompleted);
 		return result;

	}
}