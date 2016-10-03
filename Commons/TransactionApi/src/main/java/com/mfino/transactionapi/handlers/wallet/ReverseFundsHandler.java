/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet;

import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.result.Result;

/**
 * @author Sreenath
 *
 */
public interface ReverseFundsHandler {

	public Result handle(UnregisteredTxnInfo unRegisteredTxnInfo);

}
