/**
 * 
 */
package com.mfino.scheduler.service;

/**
 * 
 * Service to schedule partner configuration for settlement.
 * 
 * This service would pick records from ServiceSettlementConfig and either schedule
 * or reschedule PartnerSettlementJobs based on the status of the record.
 * 
 * @author Chaitanya
 *
 */
public interface PartnerSettlementService extends BaseService{

	/**
	 * This service would pick records from ServiceSettlementConfig and either schedule
	 * or reschedule PartnerSettlementJobs based on the status of the record.
	 * 
	 */
	public void processSettlementJobs();
	
}
