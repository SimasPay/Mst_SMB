/**
 * 
 */
package com.mfino.transactionapi.handlers.money;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala sunku
 *
 */
public interface BulkDistributionHandler {

	Result handle(TransactionDetails transactionDetails);

}
