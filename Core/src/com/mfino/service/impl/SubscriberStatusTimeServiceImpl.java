package com.mfino.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.SubscriberStatusTimeService;
import com.mfino.service.SystemParametersService;

@Service("SubscriberStatusTimeServiceImpl")
public class SubscriberStatusTimeServiceImpl implements SubscriberStatusTimeService{
	private static long MILLI_SECONDS_PER_DAY = 24 * 60 * 60 * 1000;
	private static long TIME_TO_SUSPEND_OF_INACTIVE = 270;
	private static long TIME_TO_RETIRE_OF_SUSPENDED = 180;
	private static long TIME_TO_GRAVE_OF_RETIRED = 365;
	private static long DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT = 15;
	private static long DAYS_TO_NATIONAL_TREASURY_OF_GRAVED = 180;

	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public long getTimeToNextStatus(Integer currentStatus) {
		long days = -1l;
		long time=-1l;
		if (CmFinoFIX.SubscriberStatus_InActive.equals(currentStatus)) {
			days = systemParametersService
					.getLong(SystemParameterKeys.DAYS_TO_SUSPEND_OF_INACTIVE);
			if (days != -1) {
				TIME_TO_SUSPEND_OF_INACTIVE = days;
			}
			time=TIME_TO_SUSPEND_OF_INACTIVE = TIME_TO_SUSPEND_OF_INACTIVE
					* MILLI_SECONDS_PER_DAY;

		} else if (CmFinoFIX.SubscriberStatus_Active.equals(currentStatus)) {
			days = systemParametersService
					.getLong(SystemParameterKeys.DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT);
			if (days != -1) {
				DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT = days;
			}
			time=DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT = DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT
					* MILLI_SECONDS_PER_DAY;
		} else if (CmFinoFIX.SubscriberStatus_Suspend.equals(currentStatus)) {
			days = systemParametersService
					.getLong(SystemParameterKeys.DAYS_TO_RETIRE_OF_SUSPENDED);
			if (days != -1) {
				TIME_TO_RETIRE_OF_SUSPENDED = days;
			}
			time=TIME_TO_RETIRE_OF_SUSPENDED = TIME_TO_RETIRE_OF_SUSPENDED
					* MILLI_SECONDS_PER_DAY;

		} else if (CmFinoFIX.SubscriberStatus_PendingRetirement
				.equals(currentStatus)) {
			days = systemParametersService
					.getLong(SystemParameterKeys.DAYS_TO_GRAVE_OF_RETIRED);
			if (days != -1) {
				TIME_TO_GRAVE_OF_RETIRED = days;
			}
			time=TIME_TO_GRAVE_OF_RETIRED = TIME_TO_GRAVE_OF_RETIRED
					* MILLI_SECONDS_PER_DAY;

		} else if (CmFinoFIX.SubscriberStatus_Retired.equals(currentStatus)) {
			days = systemParametersService
					.getLong(SystemParameterKeys.DAYS_TO_NATIONAL_TREASURY_OF_GRAVED);
			if (days != -1) {
				DAYS_TO_NATIONAL_TREASURY_OF_GRAVED = days;
			}
			time=DAYS_TO_NATIONAL_TREASURY_OF_GRAVED = DAYS_TO_NATIONAL_TREASURY_OF_GRAVED
					* MILLI_SECONDS_PER_DAY;

		}
		return time;
	}

}
