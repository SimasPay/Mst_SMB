package com.mfino.service;

import com.mfino.domain.SubscriberUpgradeData;

public interface SubscriberUpgradeDataService {

	public void save(SubscriberUpgradeData subscriberUpgradeData);
	
	public SubscriberUpgradeData getByMdnId(Long mdnId);
}
