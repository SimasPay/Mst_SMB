/**
 * 
 */
package com.mfino.dao.query;

import java.math.BigDecimal;

import com.mfino.hibernate.Timestamp;

/**
 * @author Chaitanya
 *
 */
public class UnRegisteredTxnInfoQuery extends BaseQuery {

	private Integer status;
	
	private Long transferCTId;
	
	private Long TransferSctlId;
	
	private Long subscriberMDNID;
	
	private boolean restrictionIsEquals = true;
	
	private Long cashoutSCTLId;
	
	private String fundAccessCode;
	
	private BigDecimal amount;
	
	private Integer[] multiStatus;
	
	private Timestamp expiryTime;
	
	private String reversalReason;
	
	private Long fundDefinitionID;
	
	private BigDecimal availableAmount;
	
	private String withdrawalMDN;
	
	private Integer withdrawalFailureAttempt;
	
	private String partnerCode;
	
	private String[] multiPartnerCode;
	
	private String transactionName;
	
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the commodityTransferID
	 */
	public Long getTransferCTId() {
		return transferCTId;
	}

	/**
	 * @param commodityTransferID the commodityTransferID to set
	 */
	public void setTransferCTId(Long transferCTId) {
		this.transferCTId = transferCTId;
	}

	/**
	 * @return the subscriberMDNID
	 */
	public Long getSubscriberMDNID() {
		return subscriberMDNID;
	}

	/**
	 * @param subscriberMDNID the subscriberMDNID to set
	 */
	public void setSubscriberMDNID(Long subscriberMDNID) {
		this.subscriberMDNID = subscriberMDNID;
	}

	/**
	 * @return the restrictionIsEquals
	 */
	public boolean isRestrictionIsEquals() {
		return restrictionIsEquals;
	}

	/**
	 * @param restrictionIsEquals the restrictionIsEquals to set
	 */
	public void setRestrictionIsEquals(boolean restrictionIsEquals) {
		this.restrictionIsEquals = restrictionIsEquals;
	}

	public Long getCashoutSCTLId() {
		return cashoutSCTLId;
	}

	public void setCashoutSCTLId(Long cashoutSCTLId) {
		this.cashoutSCTLId = cashoutSCTLId;
	}

	public Long getTransferSctlId() {
		return TransferSctlId;
	}

	public void setTransferSctlId(Long transferSctlId) {
		TransferSctlId = transferSctlId;
	}


	public Timestamp getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Timestamp expiryTime) {
		this.expiryTime = expiryTime;
	}

	public String getReversalReason() {
		return reversalReason;
	}

	public void setReversalReason(String reversalReason) {
		this.reversalReason = reversalReason;
	}

	public Long getFundDefinitionID() {
		return fundDefinitionID;
	}

	public void setFundDefinitionID(Long fundDefinitionID) {
		this.fundDefinitionID = fundDefinitionID;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	public String getWithdrawalMDN() {
		return withdrawalMDN;
	}

	public void setWithdrawalMDN(String withdrawalMDN) {
		this.withdrawalMDN = withdrawalMDN;
	}

	public Integer getWithdrawalFailureAttempt() {
		return withdrawalFailureAttempt;
	}

	public void setWithdrawalFailureAttempt(Integer withdrawalFailureAttempt) {
		this.withdrawalFailureAttempt = withdrawalFailureAttempt;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public String getFundAccessCode() {
		return fundAccessCode;
	}

	public void setFundAccessCode(String fundAccessCode) {
		this.fundAccessCode = fundAccessCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer[] getMultiStatus() {
		return multiStatus;
	}

	public void setMultiStatus(Integer[] multiStatus) {
		this.multiStatus = multiStatus;
	}

	public String[] getMultiPartnerCode() {
		return multiPartnerCode;
	}

	public void setMultiPartnerCode(String[] multiPartnerCode) {
		this.multiPartnerCode = multiPartnerCode;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
	
	
	
	
}
