package com.mfino.billpayments.beans;

/**
 * @author Sasi
 *
 */
public class QTBillPayAdviceResponse {
	
	private String responseCode;
	private String transactionReference;
	private String rechargePin;
	
	public String getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
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
}
