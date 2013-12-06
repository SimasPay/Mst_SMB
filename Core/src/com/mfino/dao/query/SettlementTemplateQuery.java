/**
 * 
 */
package com.mfino.dao.query;

/**
 * @author sunil
 *
 */
public class SettlementTemplateQuery extends BaseQuery {
	
	private String settlementName;
	private Long settlementPocketId;
	private Integer settlementType;
	private Long partnerId;
	private String exactSettlementName;
	
	public String getSettlementName() {
		return settlementName;
	}
	public void setSettlementName(String settlementName) {
		this.settlementName = settlementName;
	}
	public Long getSettlementPocketId() {
		return settlementPocketId;
	}
	public void setSettlementPocketId(Long settlementPocketId) {
		this.settlementPocketId = settlementPocketId;
	}
	public Integer getSettlementType() {
		return settlementType;
	}
	public void setSettlementType(Integer settlementType) {
		this.settlementType = settlementType;
	}
	public Long getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(Long partnerId) {
		this.partnerId = partnerId;
	}
	public String getExactSettlementName() {
		return exactSettlementName;
	}
	public void setExactSettlementName(String exactSettlementName) {
		this.exactSettlementName = exactSettlementName;
	}
	

}
