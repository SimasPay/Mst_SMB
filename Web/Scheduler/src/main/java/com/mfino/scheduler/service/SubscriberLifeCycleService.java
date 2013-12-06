/**
 * 
 */
package com.mfino.scheduler.service;


/**
 * @author Bala Sunku
 *
 */
public interface SubscriberLifeCycleService extends BaseService{

	public void updateSubscriberStatus();
	
	public void forceGrave();
}
