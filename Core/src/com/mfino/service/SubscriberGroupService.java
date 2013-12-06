package com.mfino.service;

import com.mfino.domain.SubscriberGroup;
import com.mfino.exceptions.MfinoRuntimeException;

public interface SubscriberGroupService {
	public void save(SubscriberGroup subscriberGroup);
	public SubscriberGroup getBySubscriberID(Long subscriberID) throws MfinoRuntimeException;
	
}
