/**
 * 
 */
package com.mfino.transactionapi.handlers.payment;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author HemanthKumar
 *
 */
public interface QRPaymentConfirmHandler {
	public Result handle(TransactionDetails transactionDetails);
}
