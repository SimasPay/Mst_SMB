package com.mfino.bayar.service;


/**
 * 
 * @author Amar
 *
 */
public class BayarWebServiceResponse {
	
	
	private Integer status;
	
	private String message;
	
	private String billNo;
	
	private String billName;
	
	private String billReference;
	
	private Integer totalAmount;
	
	private Integer fee;
	
	private Integer lateFee;
	
	private Integer grandTotal;
	
	private Integer balanceDeducted;
	
	private Integer currentBalance;
	
	private Integer remainingCreditLimit;
	
	private Integer transactionId;
	
	private String referenceId;
	
	private String paymentCode;
	
	private String productCode;
	
	private String voucherToken;
	
	private String voucherNo;
	
	private Integer voucherDenomination;
	
	private String dataMesssage;
	
	private String billInfo;
	
	private Integer totalBill;
	
	
	public String getBillReference() {
		return billReference;
	}

	public void setBillReference(String billReference) {
		this.billReference = billReference;
	}

	public Integer getBalanceDeducted() {
		return balanceDeducted;
	}

	public void setBalanceDeducted(Integer balanceDeducted) {
		this.balanceDeducted = balanceDeducted;
	}

	public Integer getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(Integer currentBalance) {
		this.currentBalance = currentBalance;
	}

	public Integer getRemainingCreditLimit() {
		return remainingCreditLimit;
	}

	public void setRemainingCreditLimit(Integer remainingCreditLimit) {
		this.remainingCreditLimit = remainingCreditLimit;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public String getVoucherToken() {
		return voucherToken;
	}

	public void setVoucherToken(String voucherToken) {
		this.voucherToken = voucherToken;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getBillName() {
		return billName;
	}

	public void setBillName(String billName) {
		this.billName = billName;
	}

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getFee() {
		return fee;
	}

	public void setFee(Integer fee) {
		this.fee = fee;
	}

	public Integer getLateFee() {
		return lateFee;
	}

	public void setLateFee(Integer lateFee) {
		this.lateFee = lateFee;
	}

	public Integer getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Integer grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getPaymentCode() {
		return paymentCode;
	}

	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDataMesssage() {
		return dataMesssage;
	}

	public void setDataMesssage(String dataMesssage) {
		this.dataMesssage = dataMesssage;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public Integer getVoucherDenomination() {
		return voucherDenomination;
	}

	public void setVoucherDenomination(Integer voucherDenomination) {
		this.voucherDenomination = voucherDenomination;
	}

	public String getBillInfo() {
		return billInfo;
	}

	public void setBillInfo(String billInfo) {
		this.billInfo = billInfo;
	}

	public Integer getTotalBill() {
		return totalBill;
	}

	public void setTotalBill(Integer totalBill) {
		this.totalBill = totalBill;
	}
	
}
