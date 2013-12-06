package com.mfino.gt.nibss.util;

/**
 * 
 * @author Sasi
 *
 */
public class NIBSSCashOutResponse {
	
	private String benName;
	private String sessionId;
	private String message;
	private String responseCode;
	private String error;
	
	public String getBenName() {
		return benName;
	}
	
	public void setBenName(String benName) {
		this.benName = benName;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
}
