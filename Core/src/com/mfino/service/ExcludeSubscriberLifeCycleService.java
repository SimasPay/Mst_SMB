package com.mfino.service;

import com.mfino.domain.ExcludeSubscriberLc;
import com.mfino.domain.SubscriberMdn;

public interface ExcludeSubscriberLifeCycleService {
	public ExcludeSubscriberLc getBySubscriberMDN(SubscriberMdn subscriberMDN);

}
