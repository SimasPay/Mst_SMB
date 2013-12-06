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
public class SettlementTransactionSCTLMapQuery extends BaseQuery {

	
	private Long stlID;
	private Long sctlID;
	private Integer status;
	public Long getStlID() {
		return stlID;
	}
	public void setStlID(Long stlID) {
		this.stlID = stlID;
	}
	public Long getSCTLID() {
		return sctlID;
	}
	public void setSCTLID(Long sctlID) {
		this.sctlID = sctlID;
	}
	public Integer getSettlementStatus() {
		return status;
	}
	public void setSettlementStatus(Integer status) {
		this.status = status;
	}

   
}
