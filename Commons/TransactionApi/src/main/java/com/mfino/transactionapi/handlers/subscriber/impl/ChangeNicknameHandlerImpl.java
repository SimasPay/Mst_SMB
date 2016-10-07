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
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMChangeNickname;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.XMLResult;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.impl.TransactionLogServiceImpl;
import com.mfino.transactionapi.handlers.subscriber.ChangeNicknameHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ChangeEmailXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Srikanth
 * 
 */
@Service("ChangeNicknameHandlerImpl")
public class ChangeNicknameHandlerImpl extends FIXMessageHandler implements ChangeNicknameHandler{
	private static Logger	log	= LoggerFactory.getLogger(ChangeNicknameHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService tcs;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;


	public XMLResult handle(TransactionDetails transactionDetails) {
		 ChannelCode cc = transactionDetails.getCc();	 
		 
		 CMChangeNickname changeNickname = new CMChangeNickname();
		changeNickname.setPin(transactionDetails.getSourcePIN());
		changeNickname.setSourceMDN(transactionDetails.getSourceMDN());
		changeNickname.setNickname(transactionDetails.getNickname());		
		changeNickname.setSourceApplication((int)cc.getChannelsourceapplication());
		changeNickname.setChannelCode(cc.getChannelcode());
		changeNickname.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());


		log.info("Handling Change Nickname webapi request");
		XMLResult result = new ChangeEmailXMLResult();
		TransactionLogServiceImpl transactionLogService = new TransactionLogServiceImpl();
		TransactionLog transactionLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ChangeNickname, changeNickname.DumpFields());
		changeNickname.setTransactionID(transactionLog.getId().longValue());

		result.setSourceMessage(changeNickname);
		result.setTransactionTime(transactionLog.getTransactiontime());
		result.setTransactionID(transactionLog.getId().longValue());

		SubscriberMdn subscriberMDN = subscriberMdnService.getByMDN(changeNickname.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(subscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+changeNickname.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		validationResult = transactionApiValidationService.validatePin(subscriberMDN, changeNickname.getPin());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: " + changeNickname.getSourceMDN());
			result.setNumberOfTriesLeft((int)(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - subscriberMDN.getWrongpincount()));
			result.setNotificationCode(validationResult);
			return result;
		}

 		addCompanyANDLanguageToResult(subscriberMDN, result);

		Transaction transaction = null;
		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(changeNickname.getSourceMDN());
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(StringUtils.isNotBlank(changeNickname.getChannelCode()) ? Long.valueOf(changeNickname.getChannelCode()) : null);
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CHANGENICKNAME);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(changeNickname.getTransactionID());
		serviceCharge.setTransactionIdentifier(changeNickname.getTransactionIdentifier());

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
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		result.setSctlID(sctl.getId().longValue());
		try {	
			Subscriber subscriber = subscriberMDN.getSubscriber();
			subscriber.setNickname(changeNickname.getNickname());

			subscriberService.saveSubscriber(subscriber);
		}
		catch (Exception ex) {
			log.error("Exception occured while changing the Nickname", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
			if (sctl != null) {
				tcs.failTheTransaction(sctl, MessageText._("Change Nickname transaction Falied"));
			}
 			return result;
		}
		if (sctl != null) {
			sctl.setCalculatedcharge(BigDecimal.ZERO);
			tcs.completeTheTransaction(sctl);
		}
		result.setNotificationCode(CmFinoFIX.NotificationCode_ChangeNicknameCompleted);
 		return result;

	}
}