/**
 * 
 */
package com.mfino.transactionapi.handlers.nfc;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sreenath
 *
 */
public interface NFCPocketTopupInquiryHandler {

	public Result handle(TransactionDetails transactionDetails);

}
