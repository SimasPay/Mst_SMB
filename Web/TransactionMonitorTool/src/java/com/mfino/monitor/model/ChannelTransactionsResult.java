package com.mfino.monitor.model;

import com.mfino.domain.ChannelCode;

/**
 * @author Satya
 * 
 */
public class ChannelTransactionsResult {
	private ChannelCode channelCode;
	private int successful;
	private int failed;
	private int pending;
	private int processing;
	private int reversals;
	private int intermediate;

	public ChannelTransactionsResult() {
		super();		
	}

	public ChannelTransactionsResult(ChannelCode channelCode, 
			int successful, int failed, int pending,
			int processing, int reversals, int intermediate) {
		super();
		this.channelCode = channelCode;
		this.successful = successful;
		this.failed = failed;
		this.pending = pending;
		this.processing = processing;
		this.reversals = reversals;
		this.intermediate = intermediate;
	}

	public ChannelCode getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(ChannelCode channelCode) {
		this.channelCode = channelCode;
	}

	public int getSuccessful() {
		return successful;
	}

	public void setSuccessful(int successful) {
		this.successful = successful;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getPending() {
		return pending;
	}

	public void setPending(int pending) {
		this.pending = pending;
	}

	public int getProcessing() {
		return processing;
	}

	public void setProcessing(int processing) {
		this.processing = processing;
	}

	public int getReversals() {
		return reversals;
	}

	public void setReversals(int reversals) {
		this.reversals = reversals;
	}

	public int getIntermediate() {
		return intermediate;
	}

	public void setIntermediate(int intermediate) {
		this.intermediate = intermediate;
	}

}
