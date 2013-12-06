package com.mfino.sterling.bank.util;


/**
 * 
 * @author Amar
 *
 */
public class SterlingBankWebServiceRequest {
	
	
	private String sessionID;
	private String referenceID;
	private String requestType;
	private String fromAccount;
	private String toAccount;
	private String account;
	private String amount;
	private String destinationBankCode;
	private String neResponse;
	private String benefiName;
	private String paymentReference;
	private String records;
	
	public String getSessionID() {
		return sessionID;
	}
	
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(String referenceID) {
		this.referenceID = referenceID;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}

	public String getToAccount() {
		return toAccount;
	}

	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDestinationBankCode() {
		return destinationBankCode;
	}

	public void setDestinationBankCode(String destinationBankCode) {
		this.destinationBankCode = destinationBankCode;
	}

	public String getNeResponse() {
		return neResponse;
	}

	public void setNeResponse(String neResponse) {
		this.neResponse = neResponse;
	}

	public String getBenefiName() {
		return benefiName;
	}

	public void setBenefiName(String benefiName) {
		this.benefiName = benefiName;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	public String getRecords() {
		return records;
	}

	public void setRecords(String records) {
		this.records = records;
	}

}
