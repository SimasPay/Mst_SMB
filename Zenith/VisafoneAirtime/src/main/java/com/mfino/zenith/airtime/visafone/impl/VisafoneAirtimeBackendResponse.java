package com.mfino.zenith.airtime.visafone.impl;

import com.mfino.mce.core.util.BackendResponse;

/**
 * @author Sasi
 */
public class VisafoneAirtimeBackendResponse extends BackendResponse {
	
	private Long sctlId;
	private String webServiceResponse;
	private String INTxnId;
	private VisafoneAirtimeResponseCodes responseCode;
	private Integer accountType;
	boolean isProcessed;

	public String getWebServiceResponse() {
		return webServiceResponse;
	}
	
	public void setWebServiceResponse(String webServiceResponse) {
		this.webServiceResponse = webServiceResponse;
	}
	
	public Long getSctlID() {
		return sctlId;
	}
	
	public void setSctlID(Long sctlID) {
		this.sctlId = sctlID;
	}

	public VisafoneAirtimeResponseCodes getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(VisafoneAirtimeResponseCodes responseCode) {
		this.responseCode = responseCode;
	}

	public Long getSctlId() {
		return sctlId;
	}

	public void setSctlId(Long sctlId) {
		this.sctlId = sctlId;
	}

	public String getINTxnId() {
		return INTxnId;
	}

	public void setINTxnId(String iNTxnId) {
		INTxnId = iNTxnId;
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}
}
