package com.mfino.billpayments.beans;

import java.math.BigDecimal;

/**
 * @author Sasi
 *
 */
public class QTBillPaymentAdvice {
	
	//private String transactionRef;
	//private String responseCode;
	private BigDecimal amount;
	private String paymentCode;
	private String customerMobile;
	private String customerEmail;
	private String customerId;
	private String terminalId;
	private String requestReference;
	
	/*public String getTransactionRef() {
		return transactionRef;
	}
	
	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}
	*/
	/*public String getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}*/
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getPaymentCode() {
		return paymentCode;
	}
	
	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}
	
	public String getCustomerMobile() {
		return customerMobile;
	}
	
	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}
	
	public String getCustomerEmail() {
		return customerEmail;
	}
	
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	
	public String getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public String getTerminalId() {
		return terminalId;
	}
	
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	
	public String getRequestReference() {
		return requestReference;
	}
	
	public void setRequestReference(String requestReference) {
		this.requestReference = requestReference;
	}
	
	public String toXML(){
		StringBuffer xmlStringBuffer = new StringBuffer();
		
		xmlStringBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		xmlStringBuffer.append("<BillPaymentAdvice>");
		
	/*	xmlStringBuffer.append("<TransactionRef>");
		xmlStringBuffer.append(getTransactionRef());
		xmlStringBuffer.append("</TransactionRef>");
		
		xmlStringBuffer.append("<ResponseCode>");
		xmlStringBuffer.append(getResponseCode());
		xmlStringBuffer.append("</ResponseCode>");*/
		
		xmlStringBuffer.append("<Amount>");
		xmlStringBuffer.append(getAmount());
		xmlStringBuffer.append("</Amount>");
		
		xmlStringBuffer.append("<PaymentCode>");
		xmlStringBuffer.append(getPaymentCode());
		xmlStringBuffer.append("</PaymentCode>");
		
		xmlStringBuffer.append("<CustomerMobile>");
		xmlStringBuffer.append(getCustomerMobile());
		xmlStringBuffer.append("</CustomerMobile>");
		
		xmlStringBuffer.append("<CustomerEmail>");
		xmlStringBuffer.append(getCustomerEmail());
		xmlStringBuffer.append("</CustomerEmail>");
		
		xmlStringBuffer.append("<CustomerId>");
		xmlStringBuffer.append(getCustomerId());
		xmlStringBuffer.append("</CustomerId>");
		
		xmlStringBuffer.append("<TerminalId>");
		xmlStringBuffer.append(getTerminalId());
		xmlStringBuffer.append("</TerminalId>");
		
		xmlStringBuffer.append("<RequestReference>");
		xmlStringBuffer.append(getRequestReference());
		xmlStringBuffer.append("</RequestReference>");
		
		xmlStringBuffer.append("</BillPaymentAdvice>");

		
		return xmlStringBuffer.toString();
	}
}
