/**
 * 
 */
package com.mfino.transactionapi.handlers.money;

import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.result.Result;

/**
 * @author Shashank
 *
 */
public interface FundReimberseHandler {

	public Result handle(CMBase request);

}
