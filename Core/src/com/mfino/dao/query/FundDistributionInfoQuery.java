package com.mfino.dao.query;

import java.math.BigDecimal;

public class FundDistributionInfoQuery extends BaseQuery {
	private Long fundAllocationId;
	private BigDecimal distributedAmount;
	private Integer distributionStatus;
	private String failureReason;
	private Integer failureReasonCode;
	private Long transferSCTLId;
	private Long transferCTId;
	private Integer distributionType;
	public Long getFundAllocationId() {
		return fundAllocationId;
	}
	public void setFundAllocationId(Long fundAllocationId) {
		this.fundAllocationId = fundAllocationId;
	}
	public BigDecimal getDistributedAmount() {
		return distributedAmount;
	}
	public void setDistributedAmount(BigDecimal distributedAmount) {
		this.distributedAmount = distributedAmount;
	}
	public Integer getDistributionStatus() {
		return distributionStatus;
	}
	public void setDistributionStatus(Integer distributionStatus) {
		this.distributionStatus = distributionStatus;
	}
	public String getFailureReason() {
		return failureReason;
	}
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	public Long getTransferSCTLId() {
		return transferSCTLId;
	}
	public void setTransferSCTLId(Long transferSCTLId) {
		this.transferSCTLId = transferSCTLId;
	}
	public Long getTransferCTId() {
		return transferCTId;
	}
	public void setTransferCTId(Long transferCTId) {
		this.transferCTId = transferCTId;
	}
	public Integer getFailureReasonCode() {
		return failureReasonCode;
	}
	public void setFailureReasonCode(Integer failureReasonCode) {
		this.failureReasonCode = failureReasonCode;
	}
	public Integer getDistributionType() {
		return distributionType;
	}
	public void setDistributionType(Integer distributionType) {
		this.distributionType = distributionType;
	}
	
	
}
