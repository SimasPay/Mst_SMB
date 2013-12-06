package com.mfino.zenith.interbank.impl;

import com.mfino.fix.CmFinoFIX.CMBase;

/**
 * @author Sasi
 *
 */
public class IBTWSData extends CMBase{
	
	private String sessionId;
	private String destinationBankCode;
	private String channelCode;
	private String accountName;
	private String accountNumber;
	private String originatorName;
	private String narration;
	private String paymentReference;
	private String amount;
	private String responseCode;
	private String requestMethod;
	
	/*
	 * WS method/operation to invoke
	 */
	private String wsMethodName;
	private boolean isRequest; /*To indicate if we are using this transfer object in ws request or response.*/
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getDestinationBankCode() {
		return destinationBankCode;
	}
	
	public void setDestinationBankCode(String destinationBankCode) {
		this.destinationBankCode = destinationBankCode;
	}
	
	public String getChannelCode() {
		return channelCode;
	}
	
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	
	public String getAccountName() {
		return accountName;
	}
	
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
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
	
	public String getPaymentReference() {
		return paymentReference;
	}
	
	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}
	
	public String getAmount() {
		return amount;
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getFundTransferRequestXml(){
		return "&lt;FTSingleCreditRequest&gt;&lt;SessionID&gt;SessionID_PlaceHolder&lt;/SessionID&gt;&lt;DestinationBankCode&gt;011&lt;/DestinationBankCode&gt;&lt;ChannelCode&gt;3&lt;/ChannelCode&gt;&lt;AccountName&gt;AccountName_PlaceHolder&lt;/AccountName&gt;&lt;AccountNumber&gt;0013538419&lt;/AccountNumber&gt;&lt;OriginatorName&gt;1020105333&lt;/OriginatorName&gt;&lt;Narration&gt;EazyMoney NIP Tfr&lt;/Narration&gt;&lt;PaymentReference&gt;999&lt;/PaymentReference&gt;&lt;Amount&gt;25000.0&lt;/Amount&gt;&lt;/FTSingleCreditRequest&gt;";
	}
	
	public String getTransactionStatusRequestXml(){
		return null;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getWsMethodName() {
		return wsMethodName;
	}

	public void setWsMethodName(String wsMethodName) {
		this.wsMethodName = wsMethodName;
	}

	public boolean isRequest() {
		return isRequest;
	}

	public void setRequest(boolean isRequest) {
		this.isRequest = isRequest;
	}
	
	public boolean checkRequiredFields() {
		return true;
	}
}
