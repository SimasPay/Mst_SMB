package com.mfino.transactionapi.handlers.account;

import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author HemanthKumar
 *
 */
public interface GetUserAPIKeyHandler {

	Result handle(TransactionDetails transactionDetails);
	
}
