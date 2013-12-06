package com.mfino.billpayments.beans;

/**
 * @author Sasi
 *
 */
public class QTQueryTransactionResponse {
	
	private String responseCode;
	private String transactionSet;
	private String transactionResponseCode;
	private String status;
	
	public String getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getTransactionSet() {
		return transactionSet;
	}
	
	public void setTransactionSet(String transactionSet) {
		this.transactionSet = transactionSet;
	}
	
	public String getTransactionResponseCode() {
		return transactionResponseCode;
	}
	
	public void setTransactionResponseCode(String transactionResponseCode) {
		this.transactionResponseCode = transactionResponseCode;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
