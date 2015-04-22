package com.mfino.monitor.model;

import com.mfino.domain.Service;

/**
 * @author Satya
 * 
 */
public class PerTransactionResults {
	private String txType;	
	private int successful;
	private int pending;
	private int failed;
	private int processing;
	private int txnTypeId;
	private int count;

	public PerTransactionResults() {
		super();
	}

	public PerTransactionResults(String txType, int successful, int failed, int pending, int processing, int txnTypeId, int count) {
		super();
		this.txType = txType;		
		this.successful = successful;
		this.pending = pending;
		this.failed = failed;
		this.processing = processing;
		this.txnTypeId = txnTypeId;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTxnTypeId() {
		return txnTypeId;
	}

	public void setTxnTypeId(int txnTypeId) {
		this.txnTypeId = txnTypeId;
	}

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public int getSuccessful() {
		return successful;
	}

	public void setSuccessful(int successful) {
		this.successful = successful;
	}

	public int getPending() {
		return pending;
	}

	public void setPending(int pending) {
		this.pending = pending;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getProcessing() {
		return processing;
	}

	public void setProcessing(int processing) {
		this.processing = processing;
	}

}
