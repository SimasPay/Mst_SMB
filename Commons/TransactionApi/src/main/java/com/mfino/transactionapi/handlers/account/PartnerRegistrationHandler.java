/**
 * 
 */
package com.mfino.transactionapi.handlers.account;

import com.mfino.result.Result;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Maruthi
 *
 */
public interface PartnerRegistrationHandler {

	public Result handle(TransactionDetails transactionDetails);
}
