package com.mfino.monitor.model;

import java.math.BigDecimal;

/**
 * @author Satya
 * 
 */
public class FailedTransactionsResult {
	private String mobileNumber;
	private String refID;
	private String amount;
	private String transactionType;
	private String channelName;
	private String reason;
	private String rcCode;
	private String txnDateTime;

	public FailedTransactionsResult() {
		super();
	}

	
	
	public String getRefID() {
		return refID;
	}



	public void setRefID(String refID) {
		this.refID = refID;
	}



	public String getAmount() {
		return amount;
	}



	public void setAmount(String amount) {
		this.amount = amount;
	}



	public String getRcCode() {
		return rcCode;
	}

	public void setRcCode(String rcCode) {
		this.rcCode = rcCode;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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
