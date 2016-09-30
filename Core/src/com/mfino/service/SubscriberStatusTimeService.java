package com.mfino.service;


public interface SubscriberStatusTimeService {
	
	public long getTimeToNextStatus(Integer currentStatus);
}
