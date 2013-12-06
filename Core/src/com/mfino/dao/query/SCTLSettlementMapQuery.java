/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 * 
 * @author Hemanth
 *
 */
public class SCTLSettlementMapQuery extends BaseQuery {

	
	private Long sctlID;
	private Long partnerID;
	private Integer settlementStatus;
	private Long serviceID;
	private Long stlID;
	public Long getSctlID() {
		return sctlID;
	}
	public void setSctlID(Long sctlID) {
		this.sctlID = sctlID;
	}
	public Integer getSettlementStatus() {
		return settlementStatus;
	}
	public void setSettlementStatus(Integer settlementStatus) {
		this.settlementStatus = settlementStatus;
	}
	public Long getPartnerID() {
		return partnerID;
	}
	public void setPartnerID(Long partnerID) {
		this.partnerID = partnerID;
	}
	public Long getServiceID() {
		return serviceID;
	}
	public void setServiceID(Long serviceID) {
		this.serviceID = serviceID;
	}
	
	public Long getStlID() {
		return stlID;
	}
	public void setStlID(Long stlID) {
		this.stlID = stlID;
	}
}

	
