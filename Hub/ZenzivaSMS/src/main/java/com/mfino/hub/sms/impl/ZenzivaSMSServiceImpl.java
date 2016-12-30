/**
 * 
 */
package com.mfino.hub.sms.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDetailsDAO;
import com.mfino.domain.NlogDetails;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.notification.SMSNotification;
import com.mfino.mce.notification.SMSNotificationService;


public class ZenzivaSMSServiceImpl implements SMSNotificationService  
{	
	public static final String RESPONSE_CODE_SUCCESS = "200";
	public static final String KEY_USER_KEY = "userkey";
	public static final String KEY_PASS_KEY = "passkey";
	public static final String KEY_NO_HP = "nohp";
	public static final String KEY_PESAN = "pesan";

	private String url;
	private String userkey;
	private String passkey;
	private String nohp;
	private String message;
	
	private Log log = LogFactory.getLog(ZenzivaSMSServiceImpl.class);

	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,rollbackFor=Throwable.class) 
	public void process(Exchange httpExchange) {

		log.info("ZenzivaSMSServiceImpl :: process() BEGIN");
		SMSNotification smsNotification = httpExchange.getIn().getBody(SMSNotification.class);
		message = smsNotification.getContent();
		
		if (smsNotification.getMdn().startsWith("62") || smsNotification.getMdn().startsWith("0")) {
			
			nohp = smsNotification.getMdn();
			
		} else {
			
			nohp = "62" + smsNotification.getMdn();
		}

		Long notificationLogDetailsID = smsNotification.getNotificationLogDetailsID();
		NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
		NlogDetails notificationLogDetails = null;
		
		if(notificationLogDetailsID != null){
			
			notificationLogDetails = notificationLogDetailsDao.getById(notificationLogDetailsID);
		}		

		String responseCode = null;
		String response = null;;

		try {
			
			String urlParams = getQueryString();
			log.info("ZenzivaSMSServiceImpl :: process() url: "+getUrl()+", URL Params: "+urlParams);
			
			URL uri = new URL(getUrl());
			
			HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			wr.writeBytes (urlParams);
			wr.flush();
			wr.close();
			
			//Get Response	
			responseCode = Integer.toString(connection.getResponseCode());
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuffer sb = new StringBuffer(); 
			
			while((line = rd.readLine()) != null) {
				
				sb.append(line);
				sb.append('\r');
			}
			rd.close();
			response = sb.toString();
			
			log.info("ZenzivaSMSServiceImpl :: process() response="+response+", response Code "+responseCode);
			
		} catch (Exception e) {
			log.error("ZenzivaSMSServiceImpl catch block, Error communicating with SMS Service. Error message:" + e.getMessage());
			responseCode = MCEUtil.SERVICE_UNAVAILABLE;
		}		

		if((null != responseCode) && responseCode.equals(RESPONSE_CODE_SUCCESS)) {
			
			if(notificationLogDetails != null ) {
				
				notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Success);
				notificationLogDetailsDao.save(notificationLogDetails);
				log.info("SMS with NotificationLogDetailsID " + notificationLogDetailsID + " was successfully sent");
			}
			
		} else {
			
			if(notificationLogDetails != null ) {
				
				notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Failed);
				notificationLogDetailsDao.save(notificationLogDetails);
				log.info("Failed to send sms with notificationLogDetailsID " + notificationLogDetailsID);
			}
		}
		
		log.info("ZenzivaSMSServiceImpl :: process() END");	
	}
	
	private String getQueryString() throws UnsupportedEncodingException {
		
		StringBuffer queryString = new StringBuffer();
		
		queryString.append(KEY_USER_KEY + "=" + getUserkey());
		queryString.append("&" + KEY_PESAN + "=" + URLEncoder.encode( message, "UTF-8" ));
		queryString.append("&" + KEY_PASS_KEY + "=" + getPasskey());
		queryString.append("&" + KEY_NO_HP + "=" + nohp);
		
		return queryString.toString();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserkey() {
		return userkey;
	}

	public void setUserkey(String userkey) {
		this.userkey = userkey;
	}

	public String getPasskey() {
		return passkey;
	}

	public void setPasskey(String passkey) {
		this.passkey = passkey;
	}
}