/**
 * 
 */
package com.mfino.bsim.sms.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDetailsDAO;
import com.mfino.domain.NlogDetails;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.notification.SMSNotification;
import com.mfino.mce.notification.SMSNotificationService;

/**
 * @author Amar
 *
 */
public class BSIMSMSServiceImpl implements SMSNotificationService  
{	
	public static final String KEY_SYSTEM_ID ="systemId";
	public static final String KEY_TOKEN_ID ="tokenId";
	public static final String KEY_MDN ="mdn";
	public static final String KEY_MESSAGE = "message";
	public static final String RESPONSE_CODE_SUCCESS = "00";
	
	
	
	private Log log = LogFactory.getLog(BSIMSMSServiceImpl.class);

	private String url;
	private String systemId;
	private String tokenId;
	private int timeOut;
//	private String mdn;
//	private String message;
	

	@Transactional(readOnly=false,propagation=Propagation.REQUIRED) 
	public void process(Exchange httpExchange) {
		
		log.info("BSIMSMSServiceImpl :: process() BEGIN");
		SMSNotification smsNotification = httpExchange.getIn().getBody(SMSNotification.class);
		String message = smsNotification.getContent();
		String mdn = smsNotification.getMdn();
		log.info("BSIMSMSServiceImpl :: process() message "+message+" mdn"+mdn);
		Long notificationLogDetailsID = smsNotification.getNotificationLogDetailsID();
		NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
		NlogDetails notificationLogDetails = null;
		if(notificationLogDetailsID != null)
		{
			notificationLogDetails = notificationLogDetailsDao.getById(notificationLogDetailsID);
		}		
		String responseCode;
		
		try{
			String url  = getActualUrl(message, mdn);
			log.info("BSIMSMSServiceImpl :: process() url="+url);
			HttpURLConnection connection = createHttpConnection(url);
			responseCode = Integer.toString(connection.getResponseCode());
			log.info("BSIMSMSServiceImpl Sending GET request to URL : " + url);
			log.info("BSIMSMSServiceImpl Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			log.info("BSIMSMSServiceImpl Response String : "+response.toString());
			responseCode = getResponseCode(response.toString());

		}
		catch (Exception e) {
			log.error("BSIMSMSServiceImpl catch block, Error communicating with BSIM SMS Service. Error message:" + e.getMessage());
			responseCode = MCEUtil.SERVICE_UNAVAILABLE;
		}		
		
		
		if((null != responseCode) && responseCode.equals(RESPONSE_CODE_SUCCESS))
		{
			if(notificationLogDetails != null )
			{
				notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Success);
				notificationLogDetailsDao.save(notificationLogDetails);
				log.info("SMS with NotificationLogDetailsID " + notificationLogDetailsID + " was successfully sent");
			}
		}
		else
		{
			if(notificationLogDetails != null )
			{
				notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Failed);
				notificationLogDetailsDao.save(notificationLogDetails);
				log.info("Failed to send sms with notificationLogDetailsID " + notificationLogDetailsID);
			}
		}
		log.debug("BSIMSMSServiceImpl :: process() END");	
	}

	private String getActualUrl(String msg, String mdn) throws UnsupportedEncodingException {
		String url = getUrl();
		url = url.replace(KEY_SYSTEM_ID, getSystemId());
		url = url.replace(KEY_TOKEN_ID, getTokenId());
		url = url.replace(KEY_MDN, mdn);
		url = url.replace(KEY_MESSAGE, URLEncoder.encode( msg, "UTF-8" )).replace("+", "%20");	
		return url;
	}
	
	private String getResponseCode(String response) {
    	JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(response);
			if(jsonResponse.has("responseCode")){
				return jsonResponse.getString("responseCode");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	private HttpURLConnection createHttpConnection(String url) throws IOException {
		URL uri = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(getTimeOut());
		connection.setReadTimeout(getTimeOut());
		return connection;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

//	public String getMdn() {
//		return mdn;
//	}
//
//	public void setMdn(String mdn) {
//		this.mdn = mdn;
//	}
//
//	public String getMessage() {
//		return message;
//	}
//
//	public void setMessage(String message) {
//		this.message = message;
//	}
	
}
