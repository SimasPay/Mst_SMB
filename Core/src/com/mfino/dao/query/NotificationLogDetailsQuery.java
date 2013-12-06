package com.mfino.dao.query;

import com.mfino.domain.NotificationLog;

/**
 * 
 * @author Amar
 *
 */
public class NotificationLogDetailsQuery extends BaseQuery{

	private NotificationLog notificationLog;
	private Integer sendNotificationtatus;
	
	public NotificationLog getNotificationLog() {
		return notificationLog;
	}
	public void setNotificationLog(NotificationLog notificationLog) {
		this.notificationLog = notificationLog;
	}
	public Integer getSendNotificationtatus() {
		return sendNotificationtatus;
	}
	public void setSendNotificationtatus(Integer sendNotificationtatus) {
		this.sendNotificationtatus = sendNotificationtatus;
	}
	
}
