package com.mfino.monitor.model;

public class PerRcTransactionResults {
	private String rcCode;
	private int count;
	private String rcDescription;
	
	public String getRcDescription() {
		return rcDescription;
	}
	public void setRcDescription(String rcDescription) {
		this.rcDescription = rcDescription;
	}
	public String getRcCode() {
		return rcCode;
	}
	public void setRcCode(String rcCode) {
		this.rcCode = rcCode;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
