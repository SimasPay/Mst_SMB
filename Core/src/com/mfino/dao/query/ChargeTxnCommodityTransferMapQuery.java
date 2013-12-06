/**
 * 
 */
package com.mfino.dao.query;

/**
 * @author Chaitanya
 *
 */
public class ChargeTxnCommodityTransferMapQuery extends BaseQuery {

	private Long sctlID;
	
	private Long commodityTransferID;

	/**
	 * @param sctlID the sctlID to set
	 */
	public void setSctlID(Long sctlID) {
		this.sctlID = sctlID;
	}

	/**
	 * @return the sctlID
	 */
	public Long getSctlID() {
		return sctlID;
	}

	/**
	 * @param commodityTransferID the commodityTransferID to set
	 */
	public void setCommodityTransferID(Long commodityTransferID) {
		this.commodityTransferID = commodityTransferID;
	}

	/**
	 * @return the commodityTransferID
	 */
	public Long getCommodityTransferID() {
		return commodityTransferID;
	}
	
	
}
