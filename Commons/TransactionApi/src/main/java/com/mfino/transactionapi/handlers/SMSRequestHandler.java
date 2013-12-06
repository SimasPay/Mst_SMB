/**
 * 
 */
package com.mfino.transactionapi.handlers;

import com.mfino.result.XMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Shashank
 *
 */
public interface SMSRequestHandler {

	XMLResult process(TransactionDetails transactionDetails);

}
