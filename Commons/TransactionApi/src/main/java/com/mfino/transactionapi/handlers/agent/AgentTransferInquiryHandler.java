/**
 * 
 */
package com.mfino.transactionapi.handlers.agent;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author srinivaas
 *
 */
public interface AgentTransferInquiryHandler {

	Result handle(TransactionDetails transactionDetails);

}
