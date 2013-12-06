/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import java.util.Collection;

/**
 *
 * @author Maruthi
 */
public class BillPaymentsQuery extends BaseQuery  {
	private Long sctlID;
	private String billerCode;
	private String integrationTxnRefId;
	private Collection<Integer> billPayStatuses;
	private String integrationCode;
	
	public void setSctlID(Long sctlID) {
		this.sctlID = sctlID;
	}

	public Long getSctlID() {
		return sctlID;
	}

	public void setBillerCode(String billerCode) {
		this.billerCode = billerCode;
	}

	public String getBillerCode() {
		return billerCode;
	}

	public String getIntegrationTxnRefId() {
		return integrationTxnRefId;
	}

	public void setIntegrationTxnRefId(String integrationTxnRefId) {
		this.integrationTxnRefId = integrationTxnRefId;
	}
	

	public Collection<Integer> getBillPayStatuses() {
		return billPayStatuses;
	}

	public void setBillPayStatuses(Collection<Integer> billPayStatuses) {
		this.billPayStatuses = billPayStatuses;
	}

	public String getIntegrationCode() {
		return integrationCode;
	}

	public void setIntegrationCode(String integrationCode) {
		this.integrationCode = integrationCode;
	}
}
