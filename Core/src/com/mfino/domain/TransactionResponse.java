/**
 * 
 */
package com.mfino.domain;

import java.math.BigDecimal;

/**
 * @author Bala Sunku
 *
 */
public class TransactionResponse {
	
	private boolean result;
	private Long transactionId ;
	private Long transferId ;
	private String message;
	private String code;
	private String paymentInquiryDetails;
	private String SourceCardPAN;
	private String DestinationType;
	private String bankName;
	private String AdditionalInfo;
	private String DestinationUserName;
	private BigDecimal amount;
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	public Long getTransferId() {
		return transferId;
	}
	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPaymentInquiryDetails() {
		return paymentInquiryDetails;
	}
	public void setPaymentInquiryDetails(String paymentInquiryDetails) {
		this.paymentInquiryDetails = paymentInquiryDetails;
	}
	
	public String getSourceCardPAN() {
		return SourceCardPAN;
	}
	public void setSourceCardPAN(String SourceCardPAN) {
		this.SourceCardPAN = SourceCardPAN;
	}
	
	public String getDestinationType() {
		return DestinationType;
	}
	public void setDestinationType(String DestinationType) {
		this.DestinationType = DestinationType;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAdditionalInfo() {
		return AdditionalInfo;
	}
	public void setAdditionalInfo(String AdditionalInfo) {
		this.AdditionalInfo = AdditionalInfo;
	}
	public String getDestinationUserName() {
		return DestinationUserName;
	}
	public void setDestinationUserName(String DestinationUserName) {
		this.DestinationUserName = DestinationUserName;
	}
}
