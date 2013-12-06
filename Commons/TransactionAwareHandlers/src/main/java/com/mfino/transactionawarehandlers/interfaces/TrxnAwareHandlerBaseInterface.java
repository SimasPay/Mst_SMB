package com.mfino.transactionawarehandlers.interfaces;

import com.mfino.fix.CFIXMsg;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;

/**
 * This is a base interface that must be implemted by all transaction aware handlers.
 * Usage way is to first call preprocess then communicate followed by postprocess methods
 * @author Sreenath
 *
 */
public interface TrxnAwareHandlerBaseInterface {
	
	/**
	 * Validates the data and creates the sctl for the transaction
	 * @param ichHandler
	 * @return
	 */
	public XMLResult preprocess(FIXMessageHandler ichHandler);
	
	/**
	 * prepares the message to be sent to backend and returns the response after backend processing
	 * @param ichHandler
	 * @return
	 */
	public CFIXMsg communicate(FIXMessageHandler ichHandler);
	
	/**
	 * continues the processing of transaction based on the backend response and returns the final result
	 * @param ichHandler
	 * @param response
	 * @param result
	 * @return
	 */
	public XMLResult postprocess(FIXMessageHandler ichHandler,CFIXMsg response,XMLResult result);


}
