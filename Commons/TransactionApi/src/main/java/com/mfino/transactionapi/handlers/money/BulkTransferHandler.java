/**
 * 
 */
package com.mfino.transactionapi.handlers.money;

import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Shashank
 *
 */
public interface BulkTransferHandler {

	Result handle(TransactionDetails td);

}
