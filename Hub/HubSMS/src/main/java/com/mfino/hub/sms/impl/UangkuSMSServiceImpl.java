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
import org.apache.commons.lang.StringUtils;
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


public class UangkuSMSServiceImpl implements SMSNotificationService  
{	
	public static final String RESPONSE_CODE_SUCCESS = "200";
	public static final String KEY_API_TOKEN = "apitoken";
	public static final String KEY_SHORT_CODE = "shortcode";
	public static final String KEY_TO_ADDRESS = "to";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_WALLETID = "walletid";

	private Log log = LogFactory.getLog(UangkuSMSServiceImpl.class);

	private String url;
	private String shortcode;
	private String apitoken;
	private String toAddress;
	private String message;
	private String intPrefixCode;
	private String walletID;

	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,rollbackFor=Throwable.class) 
	public void process(Exchange httpExchange) {

		log.info("UangkuSMSServiceImpl :: process() BEGIN");
		SMSNotification smsNotification = httpExchange.getIn().getBody(SMSNotification.class);
		message = smsNotification.getContent();
		if (smsNotification.getMdn().startsWith("62") || smsNotification.getMdn().startsWith("0")) {
			toAddress = smsNotification.getMdn();
		} else {
			toAddress = StringUtils.isNotBlank(getIntPrefixCode()) ? getIntPrefixCode() +  smsNotification.getMdn() : smsNotification.getMdn();
		}

		Long notificationLogDetailsID = smsNotification.getNotificationLogDetailsID();
		NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
		NlogDetails notificationLogDetails = null;
		if(notificationLogDetailsID != null)
		{
			notificationLogDetails = notificationLogDetailsDao.getById(notificationLogDetailsID);
		}		

		String responseCode = null;
		String response = null;;

		try{
			String urlParams = getQueryString();
			log.info("UangkuSMSServiceImpl :: process() url: "+getUrl()+", URL Params: "+urlParams);
			URL uri = new URL(getUrl());
			HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
			wr.writeBytes (urlParams);
			wr.flush ();
			wr.close ();
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
			
			log.info("UangkuSMSServiceImpl :: process() response="+response+", response Code "+responseCode);
		}
		catch (Exception e) {
			log.error("UangkuSMSServiceImpl catch block, Error communicating with SMS Service. Error message:" + e.getMessage());
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
		log.info("UangkuSMSServiceImpl :: process() END");	
	}
	
	private String getQueryString() throws UnsupportedEncodingException {
		String queryString = "";
		queryString = queryString + KEY_TO_ADDRESS + "=" + toAddress;
		queryString = queryString + "&" + KEY_MESSAGE + "=" + URLEncoder.encode( message, "UTF-8" );
		queryString = queryString + "&" + KEY_API_TOKEN + "=" + getApitoken();
		queryString = queryString + "&" + KEY_SHORT_CODE + "=" + getShortcode();
		queryString = queryString + "&" + KEY_WALLETID + "=" + getWalletID();
		return queryString;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

	public String getApitoken() {
		return apitoken;
	}

	public void setApitoken(String apitoken) {
		this.apitoken = apitoken;
	}

	public String getIntPrefixCode() {
		return intPrefixCode;
	}

	public void setIntPrefixCode(String intPrefixCode) {
		this.intPrefixCode = intPrefixCode;
	}

	public String getWalletID() {
		return walletID;
	}

	public void setWalletID(String walletID) {
		this.walletID = walletID;
	}
}
