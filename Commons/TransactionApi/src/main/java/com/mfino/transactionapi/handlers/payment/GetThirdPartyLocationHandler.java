/**
 * 
 */
package com.mfino.transactionapi.handlers.payment;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Srinivaas
 *
 */
public interface GetThirdPartyLocationHandler {
	public Result handle(TransactionDetails transactionDetails);
}
