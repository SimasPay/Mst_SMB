/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.TxnPendingSummary;
import com.mfino.fix.CmFinoFIX.CMBillPayPendingRequest;
import com.mfino.fix.CmFinoFIX.CMInterBankPendingCommodityTransferRequest;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;

/**
 * @author Shashank
 *
 */
public interface TransactionPendingSummaryService {

	public TxnPendingSummary saveTransactionPendingSummary( CMPendingCommodityTransferRequest newMsg);

	public TxnPendingSummary saveTransactionPendingSummary(CMBillPayPendingRequest newMsg);

	public TxnPendingSummary saveTransactionPendingSummary(CMInterBankPendingCommodityTransferRequest newMsg);

}
