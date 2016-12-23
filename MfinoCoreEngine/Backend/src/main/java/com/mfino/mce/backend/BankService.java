package com.mfino.mce.backend;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAgentToAgentTransfer;
import com.mfino.fix.CmFinoFIX.CMAgentToAgentTransferInquiry;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankAccountBalanceInquiry;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMBulkDistribution;
import com.mfino.fix.CmFinoFIX.CMCashIn;
import com.mfino.fix.CmFinoFIX.CMCashInFromATM;
import com.mfino.fix.CmFinoFIX.CMCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOut;
import com.mfino.fix.CmFinoFIX.CMCashOutAtATM;
import com.mfino.fix.CmFinoFIX.CMCashOutAtATMInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOutForNonRegistered;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiryForNonRegistered;
import com.mfino.fix.CmFinoFIX.CMChargeDistribution;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivation;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivationFromBank;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivationToBank;
import com.mfino.fix.CmFinoFIX.CMGetBankAccountTransactions;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivation;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationFromBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationToBank;
import com.mfino.fix.CmFinoFIX.CMPinLessInquiryLessTransfer;
import com.mfino.fix.CmFinoFIX.CMPurchase;
import com.mfino.fix.CmFinoFIX.CMPurchaseInquiry;
import com.mfino.fix.CmFinoFIX.CMReverseTransaction;
import com.mfino.fix.CmFinoFIX.CMReverseTransactionInquiry;
import com.mfino.fix.CmFinoFIX.CMSettlementOfCharge;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationCashIn;
import com.mfino.fix.CmFinoFIX.CMTransactionReversal;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToNonRegistered;
import com.mfino.fix.CmFinoFIX.CMTransferToNonRegistered;
import com.mfino.fix.CmFinoFIX.CMWithdrawFromATM;
import com.mfino.fix.CmFinoFIX.CMWithdrawFromATMInquiry;

/**
 * @author sasidhar
 *
 */
public interface BankService {
	
	public CFIXMsg onBalanceInquiry(CMBankAccountBalanceInquiry requestFix);
	
	public CFIXMsg onBalanceInquiryFromBank(CMBalanceInquiryToBank toBank, CMBalanceInquiryFromBank fromBank);
	
	public CFIXMsg onTransferInquiryToBank(CMBankAccountToBankAccount requestFix) throws Exception;
	
	public CFIXMsg onTransferInquiryFromBank(CMTransferInquiryToBank toBank, CMTransferInquiryFromBank fromBank) throws Exception;
	
	public CFIXMsg onTransferConfirmationToBank(CMBankAccountToBankAccountConfirmation confirmationToBank) throws Exception;
	
	public CFIXMsg onTransferConfirmationFromBank(CMMoneyTransferToBank toBank, CMMoneyTransferFromBank fromBank) throws Exception;	
	
	public CFIXMsg onTransferInquiryForUnRegistered(CMTransferInquiryToNonRegistered requestFix);
	
	public CFIXMsg onTransferConfirmationForUnRegistered(CMTransferToNonRegistered confirmationToBank);
	
	/**
	 * This method should be called for one shot transfer of funds from Agents E-Money Pocket to Subscribers E-Money pocket during
	 * subscriber registration.
	 * @param subscriberRegistrationCashIn
	 * @return
	 */
	public CFIXMsg onSubscriberRegistrationCashIn(CMSubscriberRegistrationCashIn subscriberRegistrationCashIn) throws Exception;
	
	public CFIXMsg onBulkDistribution(CMBulkDistribution bulkDistribution);
	
	/**
	 * For distribution of charges to a partner.
	 * Moves funds from charges pocket to partner collector pockets.
	 * Source is always an E-Money charge pocket defined in platform.
	 * Destination pocket is always partners configured E-Money pocket.
	 * No SMS/Email notifications would be sent for this.
	 * @param chargeDistribution
	 * @return
	 */
	public CFIXMsg onChargeDistribution(CMChargeDistribution chargeDistribution) throws Exception;
	
	/**
	 * Does settlement from partners collector pocket to settlement pocket.
	 * @param chargeDistribution
	 * @return
	 */
	public CFIXMsg onSettlementOfCharge(CMSettlementOfCharge settlementOfCharge) throws Exception;
	
	/**
	 * For cash in inquiry.
	 * @param cashInInquiry
	 * @return
	 */
	public CFIXMsg onCashInInquiry(CMCashInInquiry cashInInquiry);
	
	/**
	 * Cash in confirmation
	 * @param cashIn
	 * @return
	 */
	public CFIXMsg onCashIn(CMCashIn cashIn);
	
	/**
	 * 
	 * @param cashOutInquiry
	 * @return
	 */
	public CFIXMsg onCashOutInquiry(CMCashOutInquiry cashOutInquiry);
	
	/**
	 * 
	 * @param cashOut
	 * @return
	 */
	public CFIXMsg onCashOut(CMCashOut cashOut);
	
	/**
	 * 
	 * @param cashOutInquiry
	 * @return
	 */
	public CFIXMsg onCashOutInquiryForUnRegistered(CMCashOutInquiryForNonRegistered cashOutInquiry);
	
	/**
	 * 
	 * @param cashOut
	 * @return
	 */
	public CFIXMsg onCashOutForUnRegistered(CMCashOutForNonRegistered cashOut);
	
	/**
	 * Handles processing of purchase inquiry request.
	 * 
	 * Based on the request validation are done for the source and destination accounts 
	 * whether the transaction is possible or not.
	 * 
	 * @param purchaseInquiry
	 * @return
	 */
	public CFIXMsg onPurchaseInquiry(CMPurchaseInquiry purchaseInquiry);
	
	/**
	 * Handles purchase request and transfer of funds from subscriber to merchant.
	 * 
	 * @param purchase
	 * @return
	 */
	public CFIXMsg onPurchase(CMPurchase purchase);
	
	/**
	 * Handles processing of Reverse Transaction inquiry request.
	 * 
	 * Based on the request validation are done for the source and destination accounts 
	 * whether the transaction is possible or not.
	 * 
	 * @param purchaseInquiry
	 * @return
	 */
	public CFIXMsg onReverseTransactionInquiry(CMReverseTransactionInquiry reverseTransactionInquiry);
	
	/**
	 * Handles Reverse Transaction request and transfer of funds from subscriber to merchant.
	 * 
	 * @param purchase
	 * @return
	 */
	public CFIXMsg onReverseTransaction(CMReverseTransaction reverseTransaction);
	
	/**
	 * Reverts changes done for TransferInquiry and moves the PendingCommodityTransfer to CommodityTransfer
	 * and marks it as Complete and Failed.
	 * 
	 * @param pendingTransfer
	 * @return
	 */
	public CFIXMsg onRevertOfTransferInquiry(PendingCommodityTransfer pendingTransfer, boolean updatePCT);
	
	/**
	 * Reverts changes done for TransferConfirmation and moves the PendingCommodityTransfer to CommodityTransfer
	 * and marks it as Complete and Failed.
	 * 
	 * @param pendingTranfer
	 * @return
	 */
	public CFIXMsg onRevertOfTransferConfirmation(PendingCommodityTransfer pendingTranfer, boolean updatePCT);
	
	/**
	 * Completes changes done for TransferConfirmation and moves the PendingCommodityTransfer to CommodityTransfer
	 * and marks it as Complete and Success.
	 * 
	 * @param pendingTransfer
	 * @return
	 */
	public CFIXMsg onResolveCompleteOfTransfer(PendingCommodityTransfer pendingTransfer);

	
	/**
	 * Handles History of a bank account
	 * 
	 * @param getBankAccountTransations
	 * @return
	 */
	public CFIXMsg onGetBankHistoryToBank(CMGetBankAccountTransactions getBankAccountTransations);

	public CFIXMsg onGetBankHistoryFromBank(CMGetLastTransactionsToBank toBank,CMGetLastTransactionsFromBank fromBank);

	public CFIXMsg onTransferReversalFromBank(CMMoneyTransferToBank requestFix,
			CMMoneyTransferReversalFromBank responseFix);

	public CFIXMsg onTransferReversalToBank(CMBankAccountToBankAccountConfirmation requestFix,
			CMMoneyTransferReversalToBank responseFix);
	
	public CFIXMsg onTransferReversalToBank(CMMoneyTransferToBank requestFix,
			NoISOResponseMsg responseFix);
	
	/**
	 * Handles inquiry for transfer between Agents
	 * This is the first step for transfer
	 * 
	 * @param agentToAgentTransferInquiry
	 * @return
	 */
	public CFIXMsg onAgentToAgentTransferInquiry(CMAgentToAgentTransferInquiry agentToAgentTransferInquiry);
	
	/**
	 * Handles transfer between Agents.
	 * This is the second step for transfer, first step is {@link onAgentToAgentTransferInquiry}  
	 * 
	 * @param agentToAgentTransfer
	 * @return
	 */
	public CFIXMsg onAgentToAgentTransfer(CMAgentToAgentTransfer agentToAgentTransfer);
	
	
	public CFIXMsg onTransactionReversal(CMTransactionReversal transactionReversal);
	
	public CFIXMsg onCashOutAtATMInquiry(CMCashOutAtATMInquiry cashOutInquiry);
	
	public CFIXMsg onCashOutAtATM(CMCashOutAtATM cashOut);
	
	public CFIXMsg onwithdrawFromATMInquiry(CMWithdrawFromATMInquiry withdrawInquiry);
	
	public CFIXMsg onwithdrawFromATM(CMWithdrawFromATM withdraw);
	
	public CFIXMsg onPinLessInquiryLessTransfer(CMPinLessInquiryLessTransfer transfer);
	
	public CFIXMsg onNewSubscriberActivation(CMNewSubscriberActivation newSubscriberActivation);
	
	public CFIXMsg onExistingSubscriberReactivation(CMExistingSubscriberReactivation existingSubscriberReactivation);
	
	public CFIXMsg onNewSubscriberActivationFromBank(CMNewSubscriberActivationToBank toBank,CMNewSubscriberActivationFromBank fromBank); 
	
	public CFIXMsg onExistingSubscriberReactivationFromBank(CMExistingSubscriberReactivationToBank toBank,CMExistingSubscriberReactivationFromBank fromBank);
	
	public CFIXMsg onGetSubscriberDetailsFromBank(CMGetSubscriberDetailsToBank toBank, CMGetSubscriberDetailsFromBank fromBank);
	
	public CFIXMsg onCashInFromATM(CMCashInFromATM cashinfromAtm);
	
	public CFIXMsg onCashOutInquiryToBank(CmFinoFIX.CMCashOutAtATMInquiry requestFix);
	
	public CFIXMsg onCashOutAtATMConfirmationToBank(CmFinoFIX.CMCashOutAtATM confirmationToBank);
}
