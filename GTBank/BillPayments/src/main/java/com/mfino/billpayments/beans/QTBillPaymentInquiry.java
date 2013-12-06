package com.mfino.billpayments.beans;


/**
 * @author Sasi
 *
 */
public class QTBillPaymentInquiry {
	
	private String paymentCode;
	private String customerMobile;
	private String customerEmail;
	private String customerId;
	private String cardPan;
	private String terminalId;
	
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
	
	public String getCardPan() {
		return cardPan;
	}
	
	public void setCardPan(String cardPan) {
		this.cardPan = cardPan;
	}
	
	public String getTerminalId() {
		return terminalId;
	}
	
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	
	public String toXML(){
		StringBuffer xmlStringBuffer = new StringBuffer();
		
		xmlStringBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		xmlStringBuffer.append("<BillPaymentInquiry>");
		
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
		
/*		xmlStringBuffer.append("<CardPan>");
		xmlStringBuffer.append(getCardPan());
		xmlStringBuffer.append("</CardPan>");*/
		
		xmlStringBuffer.append("<TerminalId>");
		xmlStringBuffer.append(getTerminalId());
		xmlStringBuffer.append("</TerminalId>");
		
		xmlStringBuffer.append("</BillPaymentInquiry>");
		
		return xmlStringBuffer.toString();
	}
	
	@Override
	public String toString() {
		return toXML();
	}
}

