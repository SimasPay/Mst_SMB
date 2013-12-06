package com.mfino.dao.query;


/**
 * 
 * @author Sasi
 */
public class InterBankTransfersQuery extends BaseQuery{
	
	private Long sctlId;
	private Long transferId;
	
	public Long getSctlId() {
		return sctlId;
	}
	
	public void setSctlId(Long sctlId) {
		this.sctlId = sctlId;
	}
	
	public Long getTransferId() {
		return transferId;
	}
	
	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}
}
