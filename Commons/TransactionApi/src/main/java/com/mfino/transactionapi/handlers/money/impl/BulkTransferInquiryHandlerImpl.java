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
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
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
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.BulkTransferInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 *
 */
@Service("BulkTransferInquiryHandlerImpl")
public class BulkTransferInquiryHandlerImpl extends FIXMessageHandler implements BulkTransferInquiryHandler{

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
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
		bankAccountToBankAccount.setIsSystemIntiatedTransaction(BOOL_TRUE);
		
		if (ServiceAndTransactionConstants.MESSAGE_BULK_TRANSFER.equals(transactionDetails.getSourceMessage())) {
			bankAccountToBankAccount.setUICategory(CmFinoFIX.TransactionUICategory_Bulk_Transfer);
		} 
		else if (ServiceAndTransactionConstants.MESSAGE_SETTLE_BULK_TRANSFER.equals(transactionDetails.getSourceMessage())) {
			bankAccountToBankAccount.setUICategory(CmFinoFIX.TransactionUICategory_Settle_Bulk_Transfer);
		} 

		log.info("Handling Bulk Transfer Inquiry request of " + bankAccountToBankAccount.getSourceMDN() + " For Amount = " + 
				bankAccountToBankAccount.getAmount());
		
		Transaction transaction = null;
		XMLResult result = new TransferInquiryXMLResult();
		
		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccount, 
				bankAccountToBankAccount.DumpFields());
		bankAccountToBankAccount.setTransactionID(transactionsLog.getID());
		
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(bankAccountToBankAccount);
		result.setTransactionID(transactionsLog.getID());
		result.setDestinationMDN(bankAccountToBankAccount.getDestMDN());
		
		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(bankAccountToBankAccount.getSourceMDN());
		serviceCharge.setDestMDN(bankAccountToBankAccount.getDestMDN());
		serviceCharge.setChannelCodeId(channelCode.getID());
		serviceCharge.setServiceName(bankAccountToBankAccount.getServiceName());
		serviceCharge.setTransactionTypeName(transactionDetails.getTransactionName());
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
		if (ServiceAndTransactionConstants.MESSAGE_SETTLE_BULK_TRANSFER.equals(transactionDetails.getSourceMessage()) && 
				transactionDetails.getSctlId() != null) {
			sctl.setParentSCTLID(transactionDetails.getSctlId());
		}		
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
