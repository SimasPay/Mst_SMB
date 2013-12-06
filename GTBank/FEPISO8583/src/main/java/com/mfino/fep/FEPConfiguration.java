package com.mfino.fep;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.ConfigurationException;

public class FEPConfiguration {
	
	private static Log	    log	= LogFactory.getLog(FEPConfiguration.class);
	private static Map<String, String> notificationToISOResponse;
	private static String frontEndID ;
	
	public static String getFrontEndID() throws ConfigurationException {
		if(StringUtils.isBlank(frontEndID))
			throw new ConfigurationException("frontEndID is not configured");
		return frontEndID;
	}
	
	public static void setNotificationToISOResponse(Map<String, String> notificationToISOResponse) throws ConfigurationException {
		if(notificationToISOResponse == null ||notificationToISOResponse.isEmpty())
			throw new ConfigurationException("notificationToISOResponse not configured");	
		FEPConfiguration.notificationToISOResponse = notificationToISOResponse;
	}

	public static void setFrontEndID(String frontEndID) throws ConfigurationException {
		if(StringUtils.isBlank(frontEndID))
			throw new ConfigurationException("frontEndID not configured");
		FEPConfiguration.frontEndID = frontEndID;
	}
	
	public static String getISOResponseCode(String notificationCode) throws ConfigurationException{
		if(notificationToISOResponse == null ||notificationToISOResponse.isEmpty())
			throw new ConfigurationException("notificationToISOResponse not configured");	
		String code = notificationToISOResponse.get(notificationCode);
		if(StringUtils.isBlank(code)){
			log.info("ISO ResponseCode not set for NotificationCode:"+notificationCode);
			code = notificationToISOResponse.get(FEPConstants.ISORESPONSE_SYSTEM_MALFUNCTION);
		}
		return code;
	}
}
