/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sreenath
 *
 */
public interface AgentCashInInquiryHandler {
	public Result handle(TransactionDetails transactionDetails);
}
