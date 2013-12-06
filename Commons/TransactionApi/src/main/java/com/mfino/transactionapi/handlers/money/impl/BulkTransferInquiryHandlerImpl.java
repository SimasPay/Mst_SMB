/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
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
import com.mfino.transactionapi.handlers.money.BulkTransferInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 *
 */
@Service("BulkTransferInquiryHandlerImpl")
public class BulkTransferInquiryHandlerImpl extends FIXMessageHandler implements BulkTransferInquiryHandler{

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
		ChannelCode channelCode = transactionDetails.getCc();

		CMBankAccountToBankAccount bankAccountToBankAccount = new CMBankAccountToBankAccount();
		bankAccountToBankAccount.setSourceMDN(transactionDetails.getSourceMDN());
		bankAccountToBankAccount.setDestMDN(transactionDetails.getDestMDN());
		bankAccountToBankAccount.setAmount(transactionDetails.getAmount());
		bankAccountToBankAccount.setPin(transactionDetails.getSourcePIN());
		bankAccountToBankAccount.setServletPath(transactionDetails.getServletPath());
		bankAccountToBankAccount.setSourceMessage(transactionDetails.getSourceMessage());
		bankAccountToBankAccount.setSourceApplication(channelCode.getChannelSourceApplication());
		bankAccountToBankAccount.setChannelCode(channelCode.getChannelCode());
		bankAccountToBankAccount.setSourcePocketID(transactionDetails.getSrcPocketId());
		bankAccountToBankAccount.setDestPocketID(transactionDetails.getDestinationPocketId());
		bankAccountToBankAccount.setServiceName(transactionDetails.getServiceName());

		log.info("Handling Bulk Transfer Inquiry request of " + bankAccountToBankAccount.getSourceMDN() + " For Amount = " + bankAccountToBankAccount.getAmount());
		
		Transaction transaction = null;
		XMLResult result = new TransferInquiryXMLResult();
		

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccount, bankAccountToBankAccount.DumpFields());
		bankAccountToBankAccount.setTransactionID(transactionsLog.getID());
		
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(bankAccountToBankAccount);
		result.setTransactionID(transactionsLog.getID());
		result.setDestinationMDN(bankAccountToBankAccount.getDestMDN());
		
		SubscriberMDN sourceMDN = subscriberMdnService.getByMDN(bankAccountToBankAccount.getSourceMDN());


		Integer validationResult=transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+sourceMDN.getMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		Pocket srcPocket = pocketService.getById(bankAccountToBankAccount.getSourcePocketID());
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		
			
		addCompanyANDLanguageToResult(sourceMDN, result);

		// Check the Destination pocket status
		Pocket destPocket = pocketService.getById(bankAccountToBankAccount.getDestPocketID());
		validationResult = transactionApiValidationService.validateSourcePocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(destPocket!=null? destPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		bankAccountToBankAccount.setDestPocketID(destPocket.getID());


		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(bankAccountToBankAccount.getSourceMDN());
		serviceCharge.setDestMDN(bankAccountToBankAccount.getDestMDN());
		serviceCharge.setChannelCodeId(channelCode.getID());
		serviceCharge.setServiceName(bankAccountToBankAccount.getServiceName());
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_BULK_TRANSFER);
		if (ServiceAndTransactionConstants.MESSAGE_SETTLE_BULK_TRANSFER.equals(bankAccountToBankAccount.getSourceMessage())) {
			serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SETTLE_BULK_TRANSFER);
		}
		serviceCharge.setTransactionAmount(bankAccountToBankAccount.getAmount());
		serviceCharge.setTransactionLogId(transactionsLog.getID());

		try{
			transaction =transactionChargingService.getCharge(serviceCharge);
			bankAccountToBankAccount.setAmount(transaction.getAmountToCredit());
			bankAccountToBankAccount.setCharges(transaction.getAmountTowardsCharges());
		} catch (InvalidServiceException ise) {
			log.error("Exception occured in getting charges",ise);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		bankAccountToBankAccount.setServiceChargeTransactionLogID(sctl.getID());
		log.info("Sending the Bulk transfer inquiry request to Backend");
		
		CFIXMsg response = super.process(bankAccountToBankAccount);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (transactionResponse.getTransactionId()!=null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			bankAccountToBankAccount.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}

		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setSctlID(sctl.getID());
		return result;
	}
}
