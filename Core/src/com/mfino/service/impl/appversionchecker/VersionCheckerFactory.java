package com.mfino.service.impl.appversionchecker;

import com.mfino.constants.GeneralConstants;
import com.mfino.fix.CmFinoFIX.CMWebApiLoginRequest;
import com.mfino.service.definition.MobileappVersionCheckerService;

public class VersionCheckerFactory {

	
	public static final String VERSION_DB="db";
	
	private VersionCheckerFactory(){
		
	}
	
	public static VersionCheckerFactory getInstance(){
		return new VersionCheckerFactory();
	}
	
	public MobileappVersionCheckerService getService(String type){
		if(VERSION_DB.equals(type))
			return new VersionCheckerFromDB();
		return new MobileappVersionCheckerService() {
			
			@Override
			public String checkForNewVersionOfMobileApp(CMWebApiLoginRequest request) {
				return GeneralConstants.NEWER_VERSION_APP_NOT_AVAILABLE;
			}
			
			@Override
			public boolean isValidVersion(CMWebApiLoginRequest request) {
				return true;
			}
		};
	}
	
	
}
