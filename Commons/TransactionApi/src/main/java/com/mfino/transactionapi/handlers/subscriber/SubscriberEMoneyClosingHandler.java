/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sunil
 *
 */
public interface SubscriberEMoneyClosingHandler {
	
	public Result handle(TransactionDetails transactionDetails);

}
