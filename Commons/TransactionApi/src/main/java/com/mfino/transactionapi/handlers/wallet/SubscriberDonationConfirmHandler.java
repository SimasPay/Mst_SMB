/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 *
 */
public interface SubscriberDonationConfirmHandler {

	public Result handle(TransactionDetails transactionDetails);

}
