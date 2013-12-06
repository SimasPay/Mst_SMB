package com.mfino.zenith.dstv;

import com.mfino.mce.core.util.BackendResponse;

public class DSTVBackendResponse extends BackendResponse 
{
	String decoderCode;
	boolean isProcessed;
	String webServiceResponse;
	private Long sctlID;

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

	public String getDecoderCode() {
		return decoderCode;
	}

	public void setDecoderCode(String decoderCode) {
		this.decoderCode = decoderCode;
	}
	
	public void setSctlID(Long sctlID){
		this.sctlID = sctlID;
	}
	
	public Long getSctlID(){
		return sctlID;
	}
	
	@Override
	public boolean checkRequiredFields() {
		return true;
	}
}
