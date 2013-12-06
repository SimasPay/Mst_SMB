/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber;

import com.mfino.result.XMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Shashank
 *
 */
public interface GenerateOTPHandler {
	
	public XMLResult handle(TransactionDetails transactionDetails);

}
