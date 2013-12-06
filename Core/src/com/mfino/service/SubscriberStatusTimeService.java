package com.mfino.service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.SystemParametersUtil;

public interface SubscriberStatusTimeService {
	
	public long getTimeToNextStatus(Integer currentStatus);
}
