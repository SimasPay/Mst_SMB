/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.Address;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.SubscriberSyncRecord;
import com.mfino.domain.SubscribersAdditionalFields;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivation;
import com.mfino.fix.CmFinoFIX.CMSubscriberActivation;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistration;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationThroughWeb;
import com.mfino.mailer.NotificationWrapper;

/**
 * @author Sreenath
 *
 */
public interface SubscriberServiceExtended {
	public Integer registerSubscriber(Subscriber subscriber,
			SubscriberMDN subscriberMDN,
			CMSubscriberRegistration subscriberRegistration, Pocket epocket,
			String oneTimePin, Partner registeringPartner);
	
	public Integer registerSubscriberByAgent(Subscriber subscriber,
			SubscriberMDN subscriberMDN,
			CMSubscriberRegistration subscriberRegistration, Pocket epocket,
			String oneTimePin, Partner registeringPartner, Address ktpAddress, Address dometicAddress, SubscribersAdditionalFields subscriberAddiFields);

	public Integer registerWithActivationSubscriber(
			CMSubscriberRegistrationThroughWeb subscriberRegistration);

	public Integer registerSubscriberThroughWeb(
			CMSubscriberRegistrationThroughWeb subscriberRegistration,
			String oneTimePin);

	public void setOTPToSubscriber(SubscriberMDN subscriberMDN,
			String oneTimePin);

	public int createNewSubscriber(SubscriberSyncRecord syncRecord,
			Subscriber subscriber, SubscriberMDN subscriberMDN,
			String uploadedBy);

	public Integer activeSubscriber(
			CMSubscriberActivation subscriberActivation, boolean isHttps);

	public NotificationWrapper generateOTPMessage(String oneTimePin, Integer notificationMethod);

	public boolean updateUnRegisteredTxnInfoToActivated(
			SubscriberMDN subscriberMDN);

	public Integer ReactivateSubscriber(
			CMExistingSubscriberReactivation subscriberReactivation,
			boolean isHttps);

	public Integer activeSubscriber(
			CMSubscriberActivation subscriberActivation, boolean isHttps,
			boolean isHashedPin);

	public boolean isSubscriberEmailVerified(Subscriber sub);

}
