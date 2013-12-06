package com.mfino.mce.notification;


/**
 * @author sasidhar
 * Bean holds necessary information for sending SMS notification. 
 */
public class SMSNotification extends Notification {
	
	private String mdn;
	private String content;
	
	public String getMdn() {
		return mdn;
	}
	
	public void setMdn(String mdn) {
		this.mdn = mdn;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
}
