/**
 * 
 */
package com.mfino.transactionapi.handlers.agent;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sunil
 *
 */
public interface ApproveorRejectAgentClosingHandler {
	
	public Result handle(TransactionDetails transactionDetails);

}
