package com.mfino.mce.notification;

import com.mfino.mce.core.MCEMessage;

/**
 * @author sasidhar
 * Notification service notifies a subscriber based on his notification preferences.
 */
public interface NotificationService {

	/**
	 * Returns a Notification object, it can be Email, SMS, etc.
	 * @param mesg
	 * @returnCFIXMsg
	 */
	public NotificationWrapper processMessage(MCEMessage mesg);
}
