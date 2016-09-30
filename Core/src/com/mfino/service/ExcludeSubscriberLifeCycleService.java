package com.mfino.service;

import com.mfino.domain.ExcludeSubscriberLifeCycle;
import com.mfino.domain.SubscriberMdn;

public interface ExcludeSubscriberLifeCycleService {
	public ExcludeSubscriberLifeCycle getBySubscriberMDN(SubscriberMdn subscriberMDN);

}
