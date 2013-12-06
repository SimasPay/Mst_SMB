/**
 * 
 */
package com.mfino.transactionapi.handlers.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.fix.CmFinoFIX;
import com.mfino.result.XMLResult;
import com.mfino.transactionapi.handlers.SMSRequestHandler;
import com.mfino.transactionapi.handlers.TransactionRequestHandler;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 *
 */
@Service("SMSRequestHandlerImpl")
public class SMSRequestHandlerImpl implements SMSRequestHandler{
	
	@Autowired
	@Qualifier("TransactionRequestHandlerImpl")
	private TransactionRequestHandler transactionRequestHandler;
	
	public XMLResult process(TransactionDetails transactionDetails) {
	
		XMLResult xmlResult = null;
 
		
		if (ServiceAndTransactionConstants.TRANSACTION_TRANSFER.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_INQUIRY);
			// Inquiry Call
			
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(xmlResult.getCode()) ||
						CmFinoFIX.NotificationCode_TransferToUnRegisteredConfirmationPrompt.toString().equals(xmlResult.getCode()) ) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
			
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_CASHOUT.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_INQUIRY);
			// Inquiry Call
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(xmlResult.getCode())) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
			
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM_INQUIRY);
			// Inquiry Call
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(xmlResult.getCode())||CmFinoFIX.NotificationCode_CashOutAtATMConfirmationPrompt.toString().equals(xmlResult.getCode()) ) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
			
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_AGENT_TO_AGENT_TRANSFER.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AGENT_AGENT_TRANSFER_INQUIRY);
			// Inquiry Call
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(xmlResult.getCode())) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AGENT_TO_AGENT_TRANSFER);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_CASHIN.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHIN_INQUIRY);
			// Inquiry Call
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(xmlResult.getCode())) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHIN);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
			
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_PURCHASE.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_PURCHASE_INQUIRY);
			// Inquiry Call
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(xmlResult.getCode())) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_PURCHASE);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY);
			// Inquiry Call
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(xmlResult.getCode())) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_BILL_PAY.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY_INQUIRY);
			// Inquiry Call
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(xmlResult.getCode())) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER_INQUIRY);
			// Inquiry Call
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_IBTInquiry.toString().equals(xmlResult.getCode())) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_AGENT_BILL_PAY.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AGENT_BILL_PAY_INQUIRY);
			// Inquiry Call
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(xmlResult.getCode())) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AGENT_BILL_PAY	);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED.equals(transactionDetails.getTransactionName())) {
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED_INQUIRY);
			// Inquiry Call
			//Setting some pin to bypass null check
			transactionDetails.setSourcePIN("1503");
			xmlResult = transactionRequestHandler.process(transactionDetails);
			if (xmlResult != null) {
				if (CmFinoFIX.NotificationCode_CashOutToUnRegisteredConfirmationPrompt.toString().equals(xmlResult.getCode())) {
					transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED);
					transactionDetails.setTransferId(xmlResult.getTransferID());
					transactionDetails.setParentTxnId(xmlResult.getParentTransactionID());
					transactionDetails.setConfirmString("true");
					//Confirm Call
					xmlResult = transactionRequestHandler.process(transactionDetails);
				} 
			}
		}
		else {
			xmlResult = transactionRequestHandler.process(transactionDetails);
		}

		return xmlResult;
	}
}
