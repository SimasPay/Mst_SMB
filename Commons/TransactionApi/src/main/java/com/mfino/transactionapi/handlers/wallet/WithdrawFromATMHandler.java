/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet;

import com.mfino.fix.CmFinoFIX.CMThirdPartyCashOut;
import com.mfino.result.Result;

/**
 * @author Sreenath
 *
 */
public interface WithdrawFromATMHandler {

	public Result handle(CMThirdPartyCashOut thirdPartyCashOut);

}
