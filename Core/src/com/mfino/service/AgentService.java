/**
 * 
 */
package com.mfino.service;

import com.mfino.fix.CmFinoFIX.CMSubscriberActivation;
import com.mfino.mailer.NotificationWrapper;

/**
 * @author Sreenath
 *
 */
public interface AgentService { 
	public NotificationWrapper activeAgent(CMSubscriberActivation subscriberActivation,boolean isHttps);

	public NotificationWrapper activeAgent(CMSubscriberActivation subscriberActivation,boolean isHttps, boolean isHashedPin);

}
