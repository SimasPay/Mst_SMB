package com.mfino.domain;

public enum NotificationCodes {
	
	BANK_TRANSFER_INQUIRY_SUCCESS("72"),
	BANK_TRANSFER_SUCCESS("307"),
	SETTLEMENT_SUCCESS("81");
	
	NotificationCodes(String successNotificationCode){
		this.successNotificationCode = successNotificationCode;
	}
	
	private String successNotificationCode;
	
	public String getSuccessNotificationCode() {
		return successNotificationCode;
	}

	public void setSuccessNotificationCode(String successNotificationCode) {
		this.successNotificationCode = successNotificationCode;
	}

	@Override
	public String toString() {
		return getSuccessNotificationCode();
	}
}
