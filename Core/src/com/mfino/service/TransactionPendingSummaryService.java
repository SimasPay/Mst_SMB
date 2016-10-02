/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.TransactionPendingSummary;
import com.mfino.fix.CmFinoFIX.CMBillPayPendingRequest;
import com.mfino.fix.CmFinoFIX.CMInterBankPendingCommodityTransferRequest;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;

/**
 * @author Shashank
 *
 */
public interface TransactionPendingSummaryService {

	public TransactionPendingSummary saveTransactionPendingSummary( CMPendingCommodityTransferRequest newMsg);

	public TransactionPendingSummary saveTransactionPendingSummary(CMBillPayPendingRequest newMsg);

	public TransactionPendingSummary saveTransactionPendingSummary(CMInterBankPendingCommodityTransferRequest newMsg);

}
