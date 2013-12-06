package com.mfino.monitor.model;

import com.mfino.domain.Service;

/**
 * @author Satya
 * 
 */
public class ServiceTransactionsResult {
	private Service service;
	private int count;
	private int successful;
	private int pending;
	private int failed;
	private int processing;
	private int reversals;
	private int intermediate;

	public ServiceTransactionsResult() {
		super();
	}

	public ServiceTransactionsResult(Service service, int count,
			int successful, int failed, int pending, 
			int processing, int reversals, int intermediate) {
		super();
		this.service = service;
		this.count = count;
		this.successful = successful;
		this.pending = pending;
		this.failed = failed;
		this.processing = processing;
		this.reversals = reversals;
		this.intermediate = intermediate;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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
