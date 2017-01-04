package com.mfino.transactionapi.handlers.account;

import com.mfino.result.XMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Sigit
 *
 */
public interface GetPublicKeyHandler {

	XMLResult handle(TransactionDetails transactionDetails);
	
}
