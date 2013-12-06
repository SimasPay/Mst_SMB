package com.mfino.monitor.model;

/**
 * @author Satya
 * 
 */
public class TransactionSummaryResult {
	private String transactionStatus;
	private int count;

	public TransactionSummaryResult() {
		super();
	}

	public TransactionSummaryResult(String transactionStatus, int count) {
		this.transactionStatus = transactionStatus;
		this.count = count;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
