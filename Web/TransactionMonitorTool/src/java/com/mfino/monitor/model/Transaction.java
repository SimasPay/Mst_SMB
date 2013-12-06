package com.mfino.monitor.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Srikanth
 * 
 */

public class Transaction {
	private Long ID;
	private Long referenceID;
	private Date createTimeGE;
	private Date createTimeLT;
	private Date updateTimeGE;
	private Date updateTimeLT;
	private Long sourcePartnerID;
	private Long destPartnerID;
	private String sourceMDN;
	private String destMDN;
	private Integer status;
	private Integer [] statusList;
	private Integer sourceChannelApplication;
	private List<Long> transactionTypeIds;
	private Long transactionTypeID;
	private String bankRRN;
	private BigDecimal calculatedCharge;
	private String accessMethodText;
	private Long commodityTransferID;
	private String destPartnerCode;
	private String failureReason;
	private String invoiceNumber;
	private String MFSBillerCode;
	private String onBeHalfOfMDN;
	private String transactionName;
	private Long serviceID;
	private String serviceName;
	private Long serviceProviderID;
	private String sourcePartnerCode;
	private String transferStatusText;
	private BigDecimal transactionAmount;
	private Long transactionID;
	private Long parentSCTLID;
	private Boolean isChargeDistributed;
	private Boolean isTransactionReversed;
	private Integer amtRevStatus;
	private Integer chrgRevStatus;
	private String amtRevStatusText;
	private String info1;
	private String chrgRevStatusText;
	private Long transactionRuleID;
	private String reversalReason;
	private Date transactionTime;
	private Integer start;
	private Integer limit;
	private Integer total;

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

	public Long getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(Long referenceID) {
		this.referenceID = referenceID;
	}

	public Date getCreateTimeGE() {
		return createTimeGE;
	}

	public void setCreateTimeGE(Date createTimeGE) {
		this.createTimeGE = createTimeGE;
	}

	public Date getCreateTimeLT() {
		return createTimeLT;
	}

	public void setCreateTimeLT(Date createTimeLT) {
		this.createTimeLT = createTimeLT;
	}

	public Date getUpdateTimeGE() {
		return updateTimeGE;
	}

	public void setUpdateTimeGE(Date updateTimeGE) {
		this.updateTimeGE = updateTimeGE;
	}

	public Date getUpdateTimeLT() {
		return updateTimeLT;
	}

	public void setUpdateTimeLT(Date updateTimeLT) {
		this.updateTimeLT = updateTimeLT;
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

	public String getSourceMDN() {
		return sourceMDN;
	}

	public void setSourceMDN(String sourceMDN) {
		this.sourceMDN = sourceMDN;
	}

	public String getDestMDN() {
		return destMDN;
	}

	public void setDestMDN(String destMDN) {
		this.destMDN = destMDN;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSourceChannelApplication() {
		return sourceChannelApplication;
	}

	public void setSourceChannelApplication(Integer sourceChannelApplication) {
		this.sourceChannelApplication = sourceChannelApplication;
	}

	public List<Long> getTransactionTypeIds() {
		return transactionTypeIds;
	}

	public void setTransactionTypeIds(List<Long> transactionTypeIds) {
		this.transactionTypeIds = transactionTypeIds;
	}

	public Long getTransactionTypeID() {
		return transactionTypeID;
	}

	public void setTransactionTypeID(Long transactionTypeID) {
		this.transactionTypeID = transactionTypeID;
	}

	public String getBankRRN() {
		return bankRRN;
	}

	public void setBankRRN(String bankRRN) {
		this.bankRRN = bankRRN;
	}

	public BigDecimal getCalculatedCharge() {
		return calculatedCharge;
	}

	public void setCalculatedCharge(BigDecimal calculatedCharge) {
		this.calculatedCharge = calculatedCharge;
	}

	public String getAccessMethodText() {
		return accessMethodText;
	}

	public void setAccessMethodText(String accessMethodText) {
		this.accessMethodText = accessMethodText;
	}

	public Long getCommodityTransferID() {
		return commodityTransferID;
	}

	public void setCommodityTransferID(Long commodityTransferID) {
		this.commodityTransferID = commodityTransferID;
	}

	public String getDestPartnerCode() {
		return destPartnerCode;
	}

	public void setDestPartnerCode(String destPartnerCode) {
		this.destPartnerCode = destPartnerCode;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getMFSBillerCode() {
		return MFSBillerCode;
	}

	public void setMFSBillerCode(String mFSBillerCode) {
		MFSBillerCode = mFSBillerCode;
	}

	public String getOnBeHalfOfMDN() {
		return onBeHalfOfMDN;
	}

	public void setOnBeHalfOfMDN(String onBeHalfOfMDN) {
		this.onBeHalfOfMDN = onBeHalfOfMDN;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}

	public Long getServiceID() {
		return serviceID;
	}

	public void setServiceID(Long serviceID) {
		this.serviceID = serviceID;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Long getServiceProviderID() {
		return serviceProviderID;
	}

	public void setServiceProviderID(Long serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}

	public String getSourcePartnerCode() {
		return sourcePartnerCode;
	}

	public void setSourcePartnerCode(String sourcePartnerCode) {
		this.sourcePartnerCode = sourcePartnerCode;
	}

	public String getTransferStatusText() {
		return transferStatusText;
	}

	public void setTransferStatusText(String transferStatusText) {
		this.transferStatusText = transferStatusText;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public Long getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(Long transactionID) {
		this.transactionID = transactionID;
	}

	public Long getParentSCTLID() {
		return parentSCTLID;
	}

	public void setParentSCTLID(Long parentSCTLID) {
		this.parentSCTLID = parentSCTLID;
	}

	public Boolean getIsChargeDistributed() {
		return isChargeDistributed;
	}

	public void setIsChargeDistributed(Boolean isChargeDistributed) {
		this.isChargeDistributed = isChargeDistributed;
	}

	public Boolean getIsTransactionReversed() {
		return isTransactionReversed;
	}

	public void setIsTransactionReversed(Boolean isTransactionReversed) {
		this.isTransactionReversed = isTransactionReversed;
	}

	public Integer getAmtRevStatus() {
		return amtRevStatus;
	}

	public void setAmtRevStatus(Integer amtRevStatus) {
		this.amtRevStatus = amtRevStatus;
	}

	public Integer getChrgRevStatus() {
		return chrgRevStatus;
	}

	public void setChrgRevStatus(Integer chrgRevStatus) {
		this.chrgRevStatus = chrgRevStatus;
	}

	public String getAmtRevStatusText() {
		return amtRevStatusText;
	}

	public void setAmtRevStatusText(String amtRevStatusText) {
		this.amtRevStatusText = amtRevStatusText;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getChrgRevStatusText() {
		return chrgRevStatusText;
	}

	public void setChrgRevStatusText(String chrgRevStatusText) {
		this.chrgRevStatusText = chrgRevStatusText;
	}

	public Long getTransactionRuleID() {
		return transactionRuleID;
	}

	public void setTransactionRuleID(Long transactionRuleID) {
		this.transactionRuleID = transactionRuleID;
	}

	public String getReversalReason() {
		return reversalReason;
	}

	public void setReversalReason(String reversalReason) {
		this.reversalReason = reversalReason;
	}

	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer[] getStatusList() {
		return statusList;
	}

	public void setStatusList(Integer[] statusList) {
		this.statusList = statusList;
	}
}
