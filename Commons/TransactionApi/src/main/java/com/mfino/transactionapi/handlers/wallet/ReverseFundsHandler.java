/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet;

import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.result.Result;

/**
 * @author Sreenath
 *
 */
public interface ReverseFundsHandler {

	public Result handle(UnRegisteredTxnInfo unRegisteredTxnInfo);

}
