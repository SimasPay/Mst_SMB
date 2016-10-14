package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * NlogDetails generated by hbm2java
 */
@Entity
@Table(name = "NLOG_DETAILS")
public class NlogDetails extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_NotificationLog = "notificationLog";
	public static final String FieldName_SendNotificationStatus = "status";
	private NotificationLog notificationLog;
	private long status;

	public NlogDetails() {
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NOTIFICATIONLOGID", nullable = false)
	public NotificationLog getNotificationLog() {
		return this.notificationLog;
	}

	public void setNotificationLog(NotificationLog notificationLog) {
		this.notificationLog = notificationLog;
	}

	
	@Column(name = "STATUS", nullable = false, precision = 10, scale = 0)
	public long getStatus() {
		return this.status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

}
