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
			if (lowerValidVersion.compareToIgnoreCase(request.getAppVersion())> 0) {
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
		String abcd = str + "." + str + "." + str;

		System.out.println(abcd);
	}

}
