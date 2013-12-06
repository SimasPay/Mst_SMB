/**
 * 
 */
package com.mfino.transactionapi.handlers.mobileshopping;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Shashank
 *
 */
public interface PurchaseConfirmHandler {

	public Result handle(TransactionDetails transactionDetails);

}
