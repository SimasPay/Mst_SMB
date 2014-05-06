package com.mfino.transactionapi.handlers.account;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Amar
 *
 */
public interface GetPromoImageHandler {

	Result handle(TransactionDetails transactionDetails);
	
}
