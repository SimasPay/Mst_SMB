package com.mfino.mce.iso.jpos.nm;

public enum NMStatus {

	Illegal("Illegal"),	
	Successful("Successful"), 
	Failed("Failed"),
	Disconnected("Disconnected");
	
	private String status;

	public String getIdentifier() {
		return status;
	}

	private NMStatus(String status) {
		this.status = status;
	}

}
