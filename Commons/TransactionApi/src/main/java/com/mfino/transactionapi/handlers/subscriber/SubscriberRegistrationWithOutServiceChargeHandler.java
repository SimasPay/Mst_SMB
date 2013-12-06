/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Shashank
 *
 */
public interface SubscriberRegistrationWithOutServiceChargeHandler {
	public Result handle(TransactionDetails transactionDetails);
}
