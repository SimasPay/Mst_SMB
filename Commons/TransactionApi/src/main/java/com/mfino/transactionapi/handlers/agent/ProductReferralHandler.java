package com.mfino.transactionapi.handlers.agent;

/**
 * 
 */
//package com.mfino.transactionapi.handlers.account;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Harihara
 *
 */
public interface ProductReferralHandler {
	public Result handle(TransactionDetails transactionDetails);

}
