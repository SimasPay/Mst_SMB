package com.mfino.zenith.interbank.impl;

import com.mfino.mce.core.util.BackendResponse;

/**
 * @author Sasi
 *
 */
public class IBTBackendResponse extends BackendResponse {

	private Long sctlId;
	private String webServiceResponse;
	boolean isProcessed;
	
	private String destinationBankCode;
	private String channelCode;
	private String paymentReference;
	private String sessionId;
	private String responseCode;
	
	private NIBSSResponseCode nibssResponseCode;
	
	public Long getSctlId() {
		return sctlId;
	}
	
	public void setSctlId(Long sctlId) {
		this.sctlId = sctlId;
	}
	
	public String getWebServiceResponse() {
		return webServiceResponse;
	}
	
	public void setWebServiceResponse(String webServiceResponse) {
		this.webServiceResponse = webServiceResponse;
	}
	
	public boolean isProcessed() {
		return isProcessed;
	}
	
	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
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

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public NIBSSResponseCode getNibssResponseCode() {
		return nibssResponseCode;
	}

	public void setNibssResponseCode(NIBSSResponseCode nibssResponseCode) {
		this.nibssResponseCode = nibssResponseCode;
	}
}
