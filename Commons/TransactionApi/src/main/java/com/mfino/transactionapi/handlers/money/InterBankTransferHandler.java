/**
 * 
 */
package com.mfino.transactionapi.handlers.money;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Shashank
 *
 */
public interface InterBankTransferHandler {

	Result handle(TransactionDetails transactionDetails);

}
