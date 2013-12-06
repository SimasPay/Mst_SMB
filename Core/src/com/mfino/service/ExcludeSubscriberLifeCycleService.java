package com.mfino.service;

import com.mfino.domain.ExcludeSubscriberLifeCycle;
import com.mfino.domain.SubscriberMDN;

public interface ExcludeSubscriberLifeCycleService {
	public ExcludeSubscriberLifeCycle getBySubscriberMDN(SubscriberMDN subscriberMDN);

}
