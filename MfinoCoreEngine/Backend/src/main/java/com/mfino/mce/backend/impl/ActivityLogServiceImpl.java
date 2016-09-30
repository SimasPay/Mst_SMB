package com.mfino.mce.backend.impl;

import com.mfino.domain.ActivitiesLog;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.backend.ActivityLogService;
import com.mfino.mce.core.CoreDataWrapper;

/**
 * @author sasidhar
 *
 */
public class ActivityLogServiceImpl implements ActivityLogService {
	
	private CoreDataWrapper coreDataWrapper;
	
	public void saveActivityLog(ActivitiesLog aLog) {
		coreDataWrapper.save(aLog);	
	}
	
	public ActivitiesLog createActivityLog(CMBase baseMsg){
		ActivitiesLog activitiesLog = new ActivitiesLog();
		if(baseMsg!=null){
			
			//Define category based on different messages
			activitiesLog.setActivitycategory(null);
			
		}		
		return activitiesLog;
	}

	public CoreDataWrapper getCoreDataWrapper() {
		return coreDataWrapper;
	}

	public void setCoreDataWrapper(CoreDataWrapper coreDataWrapper) {
		this.coreDataWrapper = coreDataWrapper;
	}
}
