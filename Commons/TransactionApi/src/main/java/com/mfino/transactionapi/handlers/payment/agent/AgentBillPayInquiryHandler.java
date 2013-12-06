/**
 * 
 */
package com.mfino.transactionapi.handlers.payment.agent;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Shashank
 *
 */
public interface AgentBillPayInquiryHandler {
	public Result handle(TransactionDetails transactionDetails);
}
