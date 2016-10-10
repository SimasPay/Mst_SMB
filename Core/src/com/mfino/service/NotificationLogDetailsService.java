/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.NlogDetails;

/**
 * @author Sreenath
 *
 */
public interface NotificationLogDetailsService {

	public NlogDetails getNotificationLogDetailsById(Long notificationLogDetailsID);
	
	public void saveNotificationLogDetails(NlogDetails notificationLogDetails);
	
	public Long persistNotification(String toAddress, String emailSubject, String text, Long ServiceChargeTransactionLogID, Integer notificationCode, Integer notificationMethod, Integer notificationReceiverType); 

}
