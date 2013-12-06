package com.mfino.mce.notification;

import java.io.Serializable;

/**
 * @author sasidhar
 * Abstract Notification which should be subclassed by all Notifications.
 */
public abstract class Notification implements Serializable
{
	
	private Integer notificationCode;
	
	private Long notificationLogDetailsID;

	public Integer getNotificationCode() {
		return notificationCode;
	}

	public void setNotificationCode(Integer notificationCode) {
		this.notificationCode = notificationCode;
	}

	public Long getNotificationLogDetailsID() {
		return notificationLogDetailsID;
	}

	public void setNotificationLogDetailsID(Long notificationLogDetailsID) {
		this.notificationLogDetailsID = notificationLogDetailsID;
	}

	
}
