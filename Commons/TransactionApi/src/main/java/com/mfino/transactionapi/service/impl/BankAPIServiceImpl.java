/**
 * 
 */
package com.mfino.transactionapi.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.SubscriberService;
import com.mfino.transactionapi.handlers.money.BankMoneyTransferHandler;
import com.mfino.transactionapi.handlers.money.BankTransactionsHistoryHandler;
import com.mfino.transactionapi.handlers.money.InterBankTransferHandler;
import com.mfino.transactionapi.handlers.money.InterBankTransferInquiryHandler;
import com.mfino.transactionapi.handlers.money.TransferInquiryHandler;
import com.mfino.transactionapi.handlers.wallet.CheckBalanceHandler;
import com.mfino.transactionapi.service.BankAPIService;
import com.mfino.transactionapi.service.BaseAPIService;
import com.mfino.transactionapi.service.TransactionRequestValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * All Transactions where Source pocket id Bank, should be handled from this service
 * Bank Check Balance
 * Bank Transaction History
 * Bank Transfer
 * 
 * @author Bala sunku
 * 
 */
@Service("BankAPIServiceImpl")
public class BankAPIServiceImpl extends BaseAPIService implements BankAPIService{

	@Autowired
	@Qualifier("CheckBalanceHandlerImpl")
	private CheckBalanceHandler checkBalanceHandler;
	
	@Autowired
	@Qualifier("InterBankTransferHandlerImpl")
	private InterBankTransferHandler interBankTransferHandler;
	
	@Autowired
	@Qualifier("BankMoneyTransferHandlerImpl")
	private BankMoneyTransferHandler bankMoneyTransferHandler;
	
	@Autowired
	@Qualifier("BankTransactionsHistoryHandlerImpl")
	private BankTransactionsHistoryHandler bankTransactionsHistoryHandler;
	
	@Autowired
	@Qualifier("InterBankTransferInquiryHandlerImpl")
	private InterBankTransferInquiryHandler interBankTransferInquiryHandler;
	
	@Autowired
	@Qualifier("TransferInquiryHandlerImpl")
	private TransferInquiryHandler transferInquiryHandler;
	
    @Autowired
    @Qualifier("SubscriberServiceImpl")
    private SubscriberService subscriberService;	

	@Autowired
	@Qualifier("TransactionRequestValidationServiceImpl")
	private TransactionRequestValidationService transactionRequestValidationService;

	public XMLResult handleRequest(TransactionDetails transactionDetails) throws InvalidDataException {
		XMLResult xmlResult = null;
		String sourceMessage = transactionDetails.getSourceMessage();
		String transactionName = transactionDetails.getTransactionName();
		String destMDN = transactionDetails.getDestMDN();
		
		if (ServiceAndTransactionConstants.TRANSACTION_CHECKBALANCE.equals(transactionName)) {
			if (StringUtils.isBlank(sourceMessage)) {
				sourceMessage = ServiceAndTransactionConstants.TRANSACTION_CHECKBALANCE;
				transactionDetails.setSourceMessage(sourceMessage);
			}
			transactionRequestValidationService.validateCheckBalanceDetails(transactionDetails);
			transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
			transactionDetails.setTransactionName(transactionName);
			xmlResult = (XMLResult) checkBalanceHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_HISTORY.equalsIgnoreCase(transactionName)) {
			
			transactionRequestValidationService.validateTransactionHistoryDetails(transactionDetails);
			xmlResult = (XMLResult) bankTransactionsHistoryHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_TRANSFER_INQUIRY.equalsIgnoreCase(transactionName)) {
			
			transactionRequestValidationService.validateTransferInquiryDetails(transactionDetails);
			if (StringUtils.isBlank(sourceMessage)) {
				sourceMessage = ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER;
				transactionDetails.setSourceMessage(sourceMessage);
			}
			transactionDetails.setDestMDN( subscriberService.normalizeMDN(destMDN));
			xmlResult = (XMLResult) transferInquiryHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_TRANSFER.equalsIgnoreCase(transactionName)) {
			
			transactionRequestValidationService.validateTransferConfirmDetails(transactionDetails);
			xmlResult = (XMLResult) bankMoneyTransferHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER_INQUIRY.equalsIgnoreCase(transactionName)){

			transactionRequestValidationService.validateInterBankTransferInquiryDetails(transactionDetails);
			if (StringUtils.isBlank(sourceMessage)) {
				sourceMessage = ServiceAndTransactionConstants.MESSAGE_INTERBANK_TRANSFER;
				transactionDetails.setSourceMessage(sourceMessage);
			}
			xmlResult = (XMLResult) interBankTransferInquiryHandler.handle(transactionDetails);

		}
		else if (ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER.equalsIgnoreCase(transactionName)){

			transactionRequestValidationService.validateInterBankTransferConfirmDetails(transactionDetails);
			xmlResult = (XMLResult) interBankTransferHandler.handle(transactionDetails);			
		}
		else{
			
			xmlResult = new XMLResult();
			xmlResult.setLanguage(CmFinoFIX.Language_English);
			xmlResult.setTransactionTime(new Timestamp());
			xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_TransactionNotAvailable);
		}
		return xmlResult;
	}

}
