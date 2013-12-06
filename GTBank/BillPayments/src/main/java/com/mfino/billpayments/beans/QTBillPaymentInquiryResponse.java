package com.mfino.billpayments.beans;

import java.math.BigDecimal;

/**
 * @author Sasi
 *
 */
public class QTBillPaymentInquiryResponse {
	
	private String responseCode;
	private String transactionReference;
	private String biller;
	private String customerName;
	private BigDecimal amount;
	private String collectionsAccountNumber;
	private String collectionsAccountType;
	
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
	
	public String getBiller() {
		return biller;
	}
	
	public void setBiller(String biller) {
		this.biller = biller;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getCollectionsAccountNumber() {
		return collectionsAccountNumber;
	}
	
	public void setCollectionsAccountNumber(String collectionsAccountNumber) {
		this.collectionsAccountNumber = collectionsAccountNumber;
	}
	
	public String getCollectionsAccountType() {
		return collectionsAccountType;
	}
	
	public void setCollectionsAccountType(String collectionsAccountType) {
		this.collectionsAccountType = collectionsAccountType;
	}
}
