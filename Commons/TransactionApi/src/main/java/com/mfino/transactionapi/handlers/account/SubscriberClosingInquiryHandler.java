/**
 * 
 */
package com.mfino.transactionapi.handlers.account;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sunil
 *
 */
public interface SubscriberClosingInquiryHandler {
	
	public Result handle(TransactionDetails transactionDetails);

}
