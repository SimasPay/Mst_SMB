/**
 * 
 */
package com.mfino.dao.query;

import java.util.List;

/**
 * @author Bala Sunku
 *
 */
public class MFSLedgerQuery extends BaseQuery {
	
	private Long pocketId;
	private Long mdnId;
	private List<Long> transferIDs;
	
	public Long getPocketId() {
		return pocketId;
	}
	public void setPocketId(Long pocketId) {
		this.pocketId = pocketId;
	}
	public Long getMdnId() {
		return mdnId;
	}
	public void setMdnId(Long mdnId) {
		this.mdnId = mdnId;
	}
	public List<Long> getTransferIDs() {
		return transferIDs;
	}
	public void setTransferIDs(List<Long> transferIDs) {
		this.transferIDs = transferIDs;
	}

}
