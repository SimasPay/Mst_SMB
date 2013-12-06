package com.mfino.mce.backend;

import com.mfino.domain.ActivitiesLog;
import com.mfino.fix.CmFinoFIX.CMBase;

/**
 * @author sasidhar
 *
 */
public interface ActivityLogService {
	
	/**
	 * Saves activities Log
	 * 
	 * @param aLog
	 */
	public void saveActivityLog(ActivitiesLog aLog);
	
	/**
	 * Creates Activities Log and sets the attributes based on the CFIXMsg.
	 * 
	 * @param baseMsg
	 * @return
	 */
	public ActivitiesLog createActivityLog(CMBase baseMsg);
}
