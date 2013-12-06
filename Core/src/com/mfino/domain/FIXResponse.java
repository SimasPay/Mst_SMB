package com.mfino.domain;

/**
 * Object which will encapsulate FIXResponse, after CFIXMsg is parsed.
 * @author sasidhar
 *
 */
public abstract class FIXResponse {
	
	private String status;
	private String notificationCode;
	private String description;
	private String refId;
	
	public abstract FIXResponseType getResponseType();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotificationCode() {
		return notificationCode;
	}

	public void setNotificationCode(String notificationCode) {
		this.notificationCode = notificationCode;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "notificationCode="+notificationCode + ", description="+description + ", refId="+refId;
	}
}
