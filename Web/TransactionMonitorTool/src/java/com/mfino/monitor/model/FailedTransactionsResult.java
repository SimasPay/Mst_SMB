package com.mfino.monitor.model;

import java.math.BigDecimal;

/**
 * @author Satya
 * 
 */
public class FailedTransactionsResult {
	private String mobileNumber;
	private Long refID;
	private BigDecimal amount;
	private String transactionType;
	private String channelName;
	private String reason;
	private String txnDateTime;

	public FailedTransactionsResult() {
		super();
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getRefID() {
		return refID;
	}

	public void setRefID(Long refID) {
		this.refID = refID;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getTxnDateTime() {
		return txnDateTime;
	}

	public void setTxnDateTime(String txnDateTime) {
		this.txnDateTime = txnDateTime;
	}

}
