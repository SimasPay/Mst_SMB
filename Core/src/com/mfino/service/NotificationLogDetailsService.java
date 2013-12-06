/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.NotificationLogDetails;

/**
 * @author Sreenath
 *
 */
public interface NotificationLogDetailsService {

	public NotificationLogDetails getNotificationLogDetailsById(Long notificationLogDetailsID);
	
	public void saveNotificationLogDetails(NotificationLogDetails notificationLogDetails);
	
	public Long persistNotification(String toAddress, String emailSubject, String text, Long ServiceChargeTransactionLogID, Integer notificationCode, Integer notificationMethod, Integer notificationReceiverType); 

}
