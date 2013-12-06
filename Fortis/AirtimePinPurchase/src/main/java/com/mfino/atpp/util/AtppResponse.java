package com.mfino.atpp.util;

/**
 * 
 * @author Satya
 *
 */
public class AtppResponse {
	private String responseCode;
	private String requestReference;
	private String transactionReference;
	private String rechargePin;
	private String pinValue;
	private String amountCharged;
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getRequestReference() {
		return requestReference;
	}
	public void setRequestReference(String requestReference) {
		this.requestReference = requestReference;
	}
	public String getTransactionReference() {
		return transactionReference;
	}
	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}
	public String getRechargePin() {
		return rechargePin;
	}
	public void setRechargePin(String rechargePin) {
		this.rechargePin = rechargePin;
	}
	public String getPinValue() {
		return pinValue;
	}
	public void setPinValue(String pinValue) {
		this.pinValue = pinValue;
	}
	public String getAmountCharged() {
		return amountCharged;
	}
	public void setAmountCharged(String amountCharged) {
		this.amountCharged = amountCharged;
	}	
}
