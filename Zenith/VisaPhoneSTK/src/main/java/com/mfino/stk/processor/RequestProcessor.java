/**
 * 
 */
package com.mfino.stk.processor;

import com.mfino.result.XMLResult;
import com.mfino.stk.vo.STKRequest;

/**
 * This interface provides structure for Different request processors for the SMS received,
 * such as EncryptedRequestProcessor for messages which are encrypted, PlainRequestProcessor
 * for plain text messages.
 * 
 * @author Chaitanya
 *
 */
public abstract class RequestProcessor {
	

	/**
	 * Returns <code>XMLResult</code> after processing the transaction request received as SMS.
	 * 
	 * XMLResult contains elements required to construct response message for the request.
	 * 
	 * @param request
	 * 
	 * @return XMLResult, not null
	 */
	public abstract XMLResult processRequest(STKRequest request);
}
