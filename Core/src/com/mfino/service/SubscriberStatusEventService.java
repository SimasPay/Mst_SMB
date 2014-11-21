package com.mfino.service;

import java.util.List;

import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberStatusEvent;
import com.mfino.hibernate.Timestamp;

public interface SubscriberStatusEventService {

	public List<SubscriberStatusEvent> getSubscriberStatusEvent(boolean includeParnterInSLC, Integer[] statuses);
	
	public void save(SubscriberStatusEvent subscriberStatusEvent);
	
	public void upsertNextPickupDateForStatusChange(Subscriber subscriber,boolean isOnline);
}
