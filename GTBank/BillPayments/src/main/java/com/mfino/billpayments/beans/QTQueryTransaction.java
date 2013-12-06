package com.mfino.billpayments.beans;

/**
 * @author Sasi
 *
 */
public class QTQueryTransaction {
	
	private String requestReference;

	public String getRequestReference() {
		return requestReference;
	}

	public void setRequestReference(String requestReference) {
		this.requestReference = requestReference;
	}
	
	public String toXML(){
		StringBuffer xmlStringBuffer = new StringBuffer();
		
		xmlStringBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		xmlStringBuffer.append("<RequestDetails>");
		
		xmlStringBuffer.append("<RequestReference>");
		xmlStringBuffer.append(getRequestReference());
		xmlStringBuffer.append("</RequestReference>");
		
		xmlStringBuffer.append("</RequestDetails>");
		
		return xmlStringBuffer.toString();
	}
}
