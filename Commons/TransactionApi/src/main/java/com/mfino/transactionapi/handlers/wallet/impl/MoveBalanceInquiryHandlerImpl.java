package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.MoveBalanceInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

@Service("MoveBalanceInquiryHandlerImpl")
public class MoveBalanceInquiryHandlerImpl extends FIXMessageHandler implements MoveBalanceInquiryHandler{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;


	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in MoveBalanceInquiryHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		String sourceMessage = transactionDetails.getSourceMessage();
		ChannelCode channelCode = transactionDetails.getCc();
		CMBankAccountToBankAccount bankAccountToBankAccount = new CMBankAccountToBankAccount();
		bankAccountToBankAccount.setSourceMDN(transactionDetails.getSourceMDN());
		bankAccountToBankAccount.setDestMDN(transactionDetails.getDestMDN());
		bankAccountToBankAccount.setAmount(transactionDetails.getAmount());
		bankAccountToBankAccount.setIsSystemIntiatedTransaction(transactionDetails.isSystemIntiatedTransaction());
		bankAccountToBankAccount.setPin(transactionDetails.getSourcePIN());
		bankAccountToBankAccount.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		bankAccountToBankAccount.setSourceMessage(StringUtils.isNotBlank(sourceMessage) ? sourceMessage : ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
		bankAccountToBankAccount.setSourceApplication((int)channelCode.getChannelsourceapplication());
		bankAccountToBankAccount.setChannelCode(channelCode.getChannelcode());
		bankAccountToBankAccount.setServiceName(transactionDetails.getServiceName());
		bankAccountToBankAccount.setDestinationBankAccountNo(transactionDetails.getDestAccountNumber());
		
		log.info("MoveBalanceInquiryHandler::Handling Transfer Inquiry request for moving money from Retired Subscriber from --> " + transactionDetails.getSourceMDN() + " To " + 
				transactionDetails.getDestMDN() + " For Amount = " + transactionDetails.getAmount());
		XMLResult result = new TransferInquiryXMLResult();
		Transaction transaction = null;
		result.setDestinationMDN(transactionDetails.getDestMDN());
		result.setSourceMessage(bankAccountToBankAccount);
		
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(bankAccountToBankAccount.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN, bankAccountToBankAccount.getIsSystemIntiatedTransaction());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+bankAccountToBankAccount.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket srcSubscriberPocket = null;
		if(transactionDetails.getSrcPocketId()!=null){
			srcSubscriberPocket = pocketService.getById(transactionDetails.getSrcPocketId());
		}
		else 
		{
			srcSubscriberPocket = pocketService.getDefaultPocket(srcSubscriberMDN, transactionDetails.getSourcePocketCode());
		}
		validationResult = transactionApiValidationService.validateSourcePocket(srcSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		bankAccountToBankAccount.setSourcePocketID(srcSubscriberPocket.getId().longValue());

		SubscriberMdn destSubscriberMDN = subscriberMdnService.getByMDN(bankAccountToBankAccount.getDestMDN());
		validationResult = transactionApiValidationService.validateSubscriberAsDestination(destSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Destination subscriber with mdn : "+bankAccountToBankAccount.getDestMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		addCompanyANDLanguageToResult(srcSubscriberMDN, result);
		
		log.info("MoveBalanceInquiryHandler destMdn="+destSubscriberMDN+", destPocketCode="+transactionDetails.getDestPocketCode());
		
		Pocket destSubscriberPocket = null;
		if(transactionDetails.getDestinationPocketId()!=null){
			destSubscriberPocket = pocketService.getById(transactionDetails.getDestinationPocketId());
		}
		else 
		{
			destSubscriberPocket = pocketService.getDefaultPocket(destSubscriberMDN, transactionDetails.getDestPocketCode());
		}
		validationResult = transactionApiValidationService.validateDestinationPocket(destSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destSubscriberPocket!=null? destSubscriberPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		bankAccountToBankAccount.setDestPocketID(destSubscriberPocket.getId().longValue());
		bankAccountToBankAccount.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		bankAccountToBankAccount.setIsSystemIntiatedTransaction(true);
		bankAccountToBankAccount.setPin(transactionDetails.getSourcePIN());
		

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccount, bankAccountToBankAccount.DumpFields());
		bankAccountToBankAccount.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setDestinationMDN(bankAccountToBankAccount.getDestMDN());
		

		log.info("creating the serviceCharge object....");
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(bankAccountToBankAccount.getSourceMDN());
		sc.setDestMDN(bankAccountToBankAccount.getDestMDN());
		sc.setChannelCodeId(channelCode.getId().longValue());
		sc.setServiceName(bankAccountToBankAccount.getServiceName());
		if(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY_INQUIRY.equals(transactionDetails.getTransactionName())) {
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY);			
		}else if(ServiceAndTransactionConstants.TRANSACTION_REFUND_INQUIRY.equals(transactionDetails.getTransactionName())) {
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_REFUND);			
		}else if(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_SYSTEM_INQUIRY.equals(transactionDetails.getTransactionName())) {
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_SYSTEM);			
		}
		sc.setTransactionAmount(bankAccountToBankAccount.getAmount());
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		
		if(transactionDetails.getDestinationBankAccountNo() != null)
		{
			sc.setOnBeHalfOfMDN(transactionDetails.getDestinationBankAccountNo());
			bankAccountToBankAccount.setDestinationBankAccountNo(transactionDetails.getDestinationBankAccountNo());
		}
		
		try{
			transaction =transactionChargingService.getCharge(sc);
			bankAccountToBankAccount.setAmount(transaction.getAmountToCredit());
			bankAccountToBankAccount.setCharges(transaction.getAmountTowardsCharges());
		} catch (InvalidServiceException ise) {
			log.error("Exception occured in getting charges",ise);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);//change to service not found
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		bankAccountToBankAccount.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		log.info("sending the bankAccountToBankAccount request to backend for processing");
		CFIXMsg response = super.process(bankAccountToBankAccount);
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionid(BigDecimal.valueOf(transactionResponse.getTransactionId()));
			bankAccountToBankAccount.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}
		
		transactionChargingService.updateTransactionStatus(transactionResponse, sctl);
		
		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setSctlID(sctl.getId().longValue());
		return result;		
		
	}
}
