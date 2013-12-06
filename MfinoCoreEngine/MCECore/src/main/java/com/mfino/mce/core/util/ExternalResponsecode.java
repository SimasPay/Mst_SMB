package com.mfino.mce.core.util;

public class ExternalResponsecode {
	String code;
	String description;
	String notificationText;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotificationText() {
		return notificationText;
	}

	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

	@Override
	public String toString() {
		return "Details:code->" + this.code + ";desc->" + this.description
				+ ";notificationText->" + this.notificationText;
	}
}
