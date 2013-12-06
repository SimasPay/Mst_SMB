/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMChargeDistribution;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.transactionapi.handlers.money.ChargeDistributionHandler;

/**
 * @author Bala Sunku
 *
 */
@Service("ChargeDistributionHandlerImpl")
public class ChargeDistributionHandlerImpl extends FIXMessageHandler implements ChargeDistributionHandler{

	private static Logger log = LoggerFactory.getLogger(ChargeDistributionHandlerImpl.class);

	public CFIXMsg process(CMChargeDistribution chargeDistribution) {
		log.info("Charge Distribution for MDN " + chargeDistribution.getSourceMDN() + " from pocket id --> " + chargeDistribution.getSourcePocketID());
		CFIXMsg response = super.process(chargeDistribution);
		return response;
	}

}
