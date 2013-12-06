/**
 * 
 */
package com.mfino.service;

import com.mfino.mailer.NotificationWrapper;

/**
 * @author Sreenath
 *
 */
public interface NotificationMessageParserService {
	
    public String buildMessage(NotificationWrapper notificationWrapper,boolean appendNotificationCode);

	public String buildMessage();
    
}
