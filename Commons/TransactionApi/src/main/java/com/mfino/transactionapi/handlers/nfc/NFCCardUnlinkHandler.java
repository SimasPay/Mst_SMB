/**
 * 
 */
package com.mfino.transactionapi.handlers.nfc;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Srikanth
 *
 */
public interface NFCCardUnlinkHandler {

	public Result handle(TransactionDetails transactionDetails);

}
