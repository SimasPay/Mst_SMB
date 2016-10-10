package com.mfino.service;

import com.mfino.domain.SubscriberGroups;
import com.mfino.exceptions.MfinoRuntimeException;

public interface SubscriberGroupService {
	public void save(SubscriberGroups subscriberGroup);
	public SubscriberGroups getBySubscriberID(Long subscriberID) throws MfinoRuntimeException;
	
}
