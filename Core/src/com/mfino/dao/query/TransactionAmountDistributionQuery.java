/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import com.mfino.domain.Partner;

/**
 *
 * @author Maruthi
 */
public class TransactionAmountDistributionQuery extends BaseQuery{

    private Partner partner;
    private Integer status;
    private Long serviceChargeTransactionLogID;
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getStatus() {
		return status;
	}
	public void setServiceChargeTransactionLogID(
			Long serviceChargeTransactionLogID) {
		this.serviceChargeTransactionLogID = serviceChargeTransactionLogID;
	}
	public Long getServiceChargeTransactionLogID() {
		return serviceChargeTransactionLogID;
	}
	public void setPartner(Partner partner) {
		this.partner = partner;
	}
	public Partner getPartner() {
		return partner;
	}
}
