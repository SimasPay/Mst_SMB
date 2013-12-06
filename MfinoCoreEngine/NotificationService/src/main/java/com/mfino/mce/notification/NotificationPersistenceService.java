package com.mfino.mce.notification;

import java.util.Set;

import com.mfino.mce.core.MCEMessage;

/**
 * @author Amar
 * 
 */
public interface NotificationPersistenceService {

	public void persistNotification(Long ServiceChargeTransactionLogID, Notification notification, Integer notificationMethod, Integer notificationReceiverType);
	
	public void setSensitiveNotificationCodes(String notificationCodes);
}






