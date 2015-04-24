/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import java.util.List;

/**
 *
 * @author Srinu
 */
public class ServiceChargeTransactionsLogQuery extends BaseQuery{

   

    private Long transationID;
    private Integer sourceChannelApplication;
    private Long transferID;
	private Long sourcePartnerID;
    private Long destPartnerID;
    private String billerCode;
    private String sourceMdn;
    private String destMdn;
    private Integer status;
    private List<Long> transactionTypeIds;
    private String sourceDestMdn;
    private Long sourceDestPartnerID;
    private Long serviceID;
    private Long transactionTypeID;
    private Long parentSCTLID;
    private Long integrationTxnID;
    private String info1;
    private Integer adjustmentStatus;
    private Long parentIntegrationTransID;
    private String customQuery;
    
	public String getCustomQuery() {
		return customQuery;
	}

	public void setCustomQuery(String customQuery) {
		this.customQuery = customQuery;
	}

	public void setTransationID(Long transationID) {
		this.transationID = transationID;
	}

	public Long getTransationID() {
		return transationID;
	}

	
	 public Long getSourcePartnerID() {
			return sourcePartnerID;
		}

		public void setSourcePartnerID(Long sourcePartnerID) {
			this.sourcePartnerID = sourcePartnerID;
		}

		public Long getDestPartnerID() {
			return destPartnerID;
		}

		public void setDestPartnerID(Long destPartnerID) {
			this.destPartnerID = destPartnerID;
		}

		public String getBillerCode() {
			return billerCode;
		}

		public void setBillerCode(String billerCode) {
			this.billerCode = billerCode;
		}

		private Integer [] statusList;
		public String getSourceMdn() {
			return sourceMdn;
		}

		public void setSourceMdn(String sourceMdn) {
			this.sourceMdn = sourceMdn;
		}

		public String getDestMdn() {
			return destMdn;
		}

		public void setDestMdn(String destMdn) {
			this.destMdn = destMdn;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public void setTransferID(Long transferID) {
			this.transferID = transferID;
		}

		public Long getTransferID() {
			return transferID;
		}

		public void setSourceChannelApplication(Integer sourceChannelApplication) {
			this.sourceChannelApplication = sourceChannelApplication;
		}

		public Integer getSourceChannelApplication() {
			return sourceChannelApplication;
		}

		public void setTransactionTypeIds(List<Long> transactionTypeIds) {
			this.transactionTypeIds = transactionTypeIds;
		}

		public List<Long> getTransactionTypeIds() {
			return transactionTypeIds;
		}

		public String getSourceDestMdn() {
			return sourceDestMdn;
		}

		public void setSourceDestMdn(String sourceDestMdn) {
			this.sourceDestMdn = sourceDestMdn;
		}

		public Long getServiceID() {
			return serviceID;
		}

		public void setServiceID(Long serviceID) {
			this.serviceID = serviceID;
		}

		public Long getTransactionTypeID() {
			return transactionTypeID;
		}

		public void setTransactionTypeID(Long transactionTypeID) {
			this.transactionTypeID = transactionTypeID;
		}

		public Long getSourceDestPartnerID() {
			return sourceDestPartnerID;
		}

		public void setSourceDestPartnerID(Long sourceDestPartnerID) {
			this.sourceDestPartnerID = sourceDestPartnerID;
		}

		public Long getParentSCTLID() {
			return parentSCTLID;
		}

		public void setParentSCTLID(Long parentSCTLID) {
			this.parentSCTLID = parentSCTLID;
		}

		public Long getIntegrationTxnID() {
			return integrationTxnID;
		}

		public void setIntegrationTxnID(Long integrationTxnID) {
			this.integrationTxnID = integrationTxnID;
		}

		public String getInfo1() {
			return info1;
		}

		public void setInfo1(String info1) {
			this.info1 = info1;
		}

		public Integer getAdjustmentStatus() {
			return adjustmentStatus;
		}

		public void setAdjustmentStatus(Integer adjustmentStatus) {
			this.adjustmentStatus = adjustmentStatus;
		}
		public Integer[] getStatusList() {
			return statusList;
		}

		public void setStatusList(Integer[] statusList) {
			this.statusList = statusList;
		}

		public Long getParentIntegrationTransID() {
			return parentIntegrationTransID;
		}

		public void setParentIntegrationTransID(Long parentIntegrationTransID) {
			this.parentIntegrationTransID = parentIntegrationTransID;
		}   
}
