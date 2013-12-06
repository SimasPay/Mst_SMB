package com.mfino.iframework.de;

public enum ResponseCode {

	Success("00"), GeneralError("01"), InvalidMessage("09"), NotAllowed("0A"), InvalidAmount("0B"), BillerIDNotFound("0C"),
	CustomerNotFound("0D"),CustomerBlocked("0E"),CustomerAccountExpired("0F");

	private String	mappedCode;

	public String getMappedCode() {
		return mappedCode;
	}

	private ResponseCode(String value) {
		this.mappedCode = value;
	}

}