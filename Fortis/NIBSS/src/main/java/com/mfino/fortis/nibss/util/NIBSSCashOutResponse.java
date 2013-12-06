package com.mfino.fortis.nibss.util;

/**
 * 
 * @author Sasi
 *
 */
public class NIBSSCashOutResponse {
	
	private String benName;
	//private String sessionId;
	//private String message;
	private String responseCode;
	//private String error;
	private String paymentReference;
	private String benAccountNumber;
	private String destCode;
	private String transactionNumber;
	private String originatorName;
	private String narration;
	private String amount;
	public String getBenName() {
		return benName;
	}
	public void setBenName(String benName) {
		this.benName = benName;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getPaymentReference() {
		return paymentReference;
	}
	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}
	public String getBenAccountNumber() {
		return benAccountNumber;
	}
	public void setBenAccountNumber(String benAccountNumber) {
		this.benAccountNumber = benAccountNumber;
	}
	public String getDestCode() {
		return destCode;
	}
	public void setDestCode(String destCode) {
		this.destCode = destCode;
	}
	public String getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public String getOriginatorName() {
		return originatorName;
	}
	public void setOriginatorName(String originatorName) {
		this.originatorName = originatorName;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
}
