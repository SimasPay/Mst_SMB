package com.mfino.mce.notification;

import java.util.ArrayList;
import java.util.List;

import com.mfino.fix.CFIXMsg;

/**
 * @author sasidhar
 * Has several notifications including Web Response, Email, SMS etc
 */
public class NotificationWrapper {

	private List<Notification> notifications = new ArrayList<Notification>();
	private CFIXMsg webResponse;
	
	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public CFIXMsg getWebResponse() {
		return webResponse;
	}

	public void setWebResponse(CFIXMsg webResponse) {
		this.webResponse = webResponse;
	}
}
