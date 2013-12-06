/**
 * 
 */
package com.mfino.transactionapi.handlers;

import com.mfino.domain.ServiceChargeTransactionLog;

/**
 * @author Sreenath
 *
 */
public interface ReverseTransactionHandler {
	
	/**
	 * 
	 * @param sctl
	 * @param parentSCTL
	 */
	public void processReverseRequest(ServiceChargeTransactionLog sctl, ServiceChargeTransactionLog parentSCTL);

}
