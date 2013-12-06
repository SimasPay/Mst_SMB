/**
 * 
 */
package com.mfino.transactionapi.handlers.money;

import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMChargeDistribution;

/**
 * @author Shashank
 *
 */
public interface ChargeDistributionHandler {

	CFIXMsg process(CMChargeDistribution chargeDistribution);

	TransactionResponse checkBackEndResponse(CFIXMsg response);

}
