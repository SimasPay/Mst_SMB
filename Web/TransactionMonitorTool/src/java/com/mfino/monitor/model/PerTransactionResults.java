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

	public PerTransactionResults() {
		super();
	}

	public PerTransactionResults(String txType, int successful, int failed, int pending, int processing) {
		super();
		this.txType = txType;		
		this.successful = successful;
		this.pending = pending;
		this.failed = failed;
		this.processing = processing;
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
