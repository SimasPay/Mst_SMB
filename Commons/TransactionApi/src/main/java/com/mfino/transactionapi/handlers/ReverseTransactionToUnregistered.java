/**
 * 
 */
package com.mfino.transactionapi.handlers;

import com.mfino.domain.ServiceChargeTxnLog;


/**
 * @author Sreenath
 *
 */
public interface ReverseTransactionToUnregistered {
	
	/**
	 * 
	 * @param sctl
	 * @param parentSCTL
	 */
	public void processReverseRequest(ServiceChargeTxnLog sctl, ServiceChargeTxnLog parentSCTL);

}
