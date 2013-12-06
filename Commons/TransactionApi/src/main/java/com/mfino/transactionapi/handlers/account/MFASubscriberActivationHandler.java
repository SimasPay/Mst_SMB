/**
 * 
 */
package com.mfino.transactionapi.handlers.account;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Shashank
 *
 */
public interface MFASubscriberActivationHandler {

	Result handle(TransactionDetails transactionDetails);

}
