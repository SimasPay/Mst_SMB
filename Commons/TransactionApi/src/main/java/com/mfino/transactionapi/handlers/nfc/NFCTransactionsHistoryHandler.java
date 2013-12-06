
package com.mfino.transactionapi.handlers.nfc;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Amar
 *
 */
public interface NFCTransactionsHistoryHandler {

	Result handle(TransactionDetails transactionDetails);

}
