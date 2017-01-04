package com.mfino.transactionapi.handlers.account.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CmFinoFIX;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.handlers.account.GetPublicKeyHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.PublicKeyXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author Sigit
 *
 */
@Service("GetPublicKeyHandlerImpl") 
public class GetPublicKeyHandlerImpl extends FIXMessageHandler implements GetPublicKeyHandler  {

	private static Logger log = LoggerFactory.getLogger(GetPublicKeyHandlerImpl.class);
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	public XMLResult handle(TransactionDetails transactionDetails) {
		PublicKeyXMLResult result = new PublicKeyXMLResult();
		
		try{
			log.info("Getting the public to send to the mobile app");
			String[] publicKeyParams = CryptographyService.getPubKeyStrings();
			log.info("Public key Modulus: "+publicKeyParams[0]+"\n Public key expo: "+publicKeyParams[1]);
			result.setPublicKeyModulus(publicKeyParams[0]);
			result.setPublicKeyExponent(publicKeyParams[1]);
			
			String systemMinimumVersion = null;
			String appMinimumVersion = transactionDetails.getAppVersion();
			
			String appUrl = null;
			
			if (transactionDetails.getAppOS().equals("1")) {
				//for iOs
				systemMinimumVersion = systemParametersService.getString(SystemParameterKeys.APP_VERSION_MIN_IOS);
				appUrl = systemParametersService.getString(SystemParameterKeys.APP_URL_UPGRADE_IOS);
			} else if (transactionDetails.getAppOS().equals("2")) {
				//for Android
				systemMinimumVersion = systemParametersService.getString(SystemParameterKeys.APP_VERSION_MIN_ANDROID);
				appUrl = systemParametersService.getString(SystemParameterKeys.APP_URL_UPGRADE_ANDROID);
			}
			
			if (systemMinimumVersion != null) {
				if (systemMinimumVersion == appMinimumVersion) {
					result.setNotificationCode(CmFinoFIX.NotificationCode_NotRequiredForceUpgradeApp);
				} else {
					//compare the version
					int resultCompare = compareAppVersion(systemMinimumVersion, appMinimumVersion);
					if (resultCompare==1) {
						result.setNotificationCode(CmFinoFIX.NotificationCode_NotRequiredForceUpgradeApp);
					} else {
						//failed, need upgrade
						result.setNotificationCode(CmFinoFIX.NotificationCode_RequiredForceUpgradeApp);
						result.setAdditionalInfo(appUrl);
					}
				}
			} else {
				result.setNotificationCode(CmFinoFIX.NotificationCode_NotRequiredForceUpgradeApp);
			}
			
		}catch(Exception e){
			log.error("Exception occured while sending the public key parameters");
			e.printStackTrace();
		}	
		
		return result;
	
	}
	
	private int compareAppVersion(String systemMinimumVersion, String appMinimumVersion){
		
		String[] tempSMV = systemMinimumVersion.split("\\.");
		int smv_start = Integer.parseInt(tempSMV[0]);
		int smv_middle = Integer.parseInt(tempSMV[1]);
		int smv_end = Integer.parseInt(tempSMV[2]);
		
		String[] tempAMV = appMinimumVersion.split("\\.");
		int amv_start = Integer.parseInt(tempAMV[0]);
		int amv_middle = Integer.parseInt(tempAMV[1]);
		int amv_end = Integer.parseInt(tempAMV[2]);
		
		if (amv_start > smv_start) {
			return 1;
		} else if(amv_start == smv_start) {
			if (amv_middle > smv_middle) {
				return 1;
			} else if (amv_middle == smv_middle) {
				if (amv_end >= smv_end) {
					return 1;
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

}
