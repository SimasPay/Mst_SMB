package com.mfino.service.impl.appversionchecker;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.fix.CmFinoFIX.CMWebApiLoginRequest;
import com.mfino.service.SystemParametersService;
import com.mfino.service.definition.MobileappVersionCheckerService;
@Service("VersionCheckerFromDB")
public class VersionCheckerFromDB implements MobileappVersionCheckerService {

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	@Override
	public String checkForNewVersionOfMobileApp(CMWebApiLoginRequest request) {

		String url = GeneralConstants.NEWER_VERSION_APP_NOT_AVAILABLE;
//		String[] schemes = { "https" };
//		UrlValidator urlValidator = new UrlValidator(schemes);
		String parameter = "";
		parameter = request.getAppOS() + "." + request.getAppType() + "." + "version";
		parameter = parameter.toLowerCase();

		try {
			String newVersion = systemParametersService.getString(parameter);
			if (newVersion.compareToIgnoreCase(request.getAppVersion()) > 0) {
				if("subapp".equals(request.getAppType()))
					url = systemParametersService.getString(SystemParameterKeys.SUBAPP_URL_KEY);
				else if("agentapp".equals(request.getAppType()))
					url = systemParametersService.getString(SystemParameterKeys.AGENTAPP_URL_KEY);
				if (!StringUtils.isBlank(url))
					return url;
				else
					return GeneralConstants.NEWER_VERSION_APP_NOT_AVAILABLE;
			}
		}
		catch (Exception ex) {
			return GeneralConstants.NEWER_VERSION_APP_NOT_AVAILABLE;
		}
		return url;
	}
	@Override
	public boolean isValidVersion(CMWebApiLoginRequest request) {
		String parameter = "";
		parameter = request.getAppOS() + "." + request.getAppType() + "." + "minvalidversion";
		parameter = parameter.toLowerCase();
		try {
			String lowerValidVersion = systemParametersService.getString(parameter);
			if(StringUtils.isBlank(request.getAppVersion()))
				return false;
			
			if (StringUtils.isNotBlank(lowerValidVersion) && 
					versionCompare(lowerValidVersion, request.getAppVersion()) > 0) {
				return false;
			}
		}
		catch (Exception ex) {
			return true;
		}
		return true;
	}
	public static void main(String[] args) {

		String str = null;
		String abcd = "8.0";
		String abcde = "1.2";

		System.out.println(versionCompare(abcd, abcde));
	}
	public static int versionCompare(String str1, String str2) {
	    String[] vals1 = str1.split("\\.");
	    String[] vals2 = str2.split("\\.");
	    int i = 0;
	    // set index to first non-equal ordinal or length of shortest version string
	    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
	      i++;
	    }
	    // compare first non-equal ordinal number
	    if (i < vals1.length && i < vals2.length) {
	        int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
	        return Integer.signum(diff);
	    }
	    // the strings are equal or one string is a substring of the other
	    // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
	    return Integer.signum(vals1.length - vals2.length);
	}
}
