/**
 * 
 */
package com.mfino.dao.query;

import com.mfino.domain.Pocket;

/**
 * @author Bala Sunku
 *
 */
public class ServiceSettlementConfigQuery extends BaseQuery {
	
	private Long partnerServiceId;
	
	private int schedulerStatus = -1;

	private Pocket collectorPocket = null;
	
	public Long getPartnerServiceId() {
		return partnerServiceId;
	}

	public void setPartnerServiceId(Long partnerServiceId) {
		this.partnerServiceId = partnerServiceId;
	}

	public int getSchedulerStatus(){
		return schedulerStatus;
	}
	
	public void setSchedulerStatus(int status){
		this.schedulerStatus = status;
	}

	/**
	 * @param collectorPocket the collectorPocket to set
	 */
	public void setCollectorPocket(Pocket collectorPocket) {
		this.collectorPocket = collectorPocket;
	}

	/**
	 * @return the collectorPocket
	 */
	public Pocket getCollectorPocket() {
		return collectorPocket;
	}
}
