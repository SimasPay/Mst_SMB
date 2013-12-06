package com.mfino.transactionapi.handlers.subscriber;


import com.mfino.result.XMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Amar
 *
 */
public interface ValidateOTPHandler {
	
	public XMLResult handle(TransactionDetails transactionDetails);

}
