/**
 * 
 */
package com.mfino.scheduler.service;


/**
 * @author Bala Sunku
 *
 */
public interface TransactionAmountDistributionService extends BaseService{

	public void distributeTransactionAmount();
	
	public void updateCollectorPockets();
	
	public void retryCollectorPockets();
	
	public void retryFailedCollectorPockets();
}
