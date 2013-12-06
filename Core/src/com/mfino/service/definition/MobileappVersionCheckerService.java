package com.mfino.service.definition;

import com.mfino.fix.CmFinoFIX.CMWebApiLoginRequest;

public interface MobileappVersionCheckerService {
	
	/**
	 * 
	 * Checks if a new version of an app is available.
	 * Returns the url of the new app from database.
	 * 
	 * @param request
	 * @return
	 */
	public String checkForNewVersionOfMobileApp(CMWebApiLoginRequest request);

	public boolean isValidVersion(CMWebApiLoginRequest request);
}
