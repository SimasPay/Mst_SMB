/**
 * 
 */
package com.mfino.transactionapi.handlers.account;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.fix.processor.MultixCommunicationHandler;

/**
 * @author Shashank
 *
 */
public interface AgentActivationHandler {

	Result handle(TransactionDetails transactionDetails);

}
