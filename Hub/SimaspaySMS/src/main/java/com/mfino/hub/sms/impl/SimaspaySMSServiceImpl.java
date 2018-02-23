/**
 * 
 */
package com.mfino.hub.sms.impl;

import java.io.UnsupportedEncodingException;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
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
import com.mfino.hub.sms.utils.RSClientPostHttp;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.notification.SMSNotification;
import com.mfino.mce.notification.SMSNotificationService;


public class SimaspaySMSServiceImpl implements SMSNotificationService  
{	
	public static final String KEY_SYSTEM_ID ="systemId";
	public static final String KEY_MSG_ID ="msgId";
	public static final String KEY_RECEPIENT_NO ="recepientNo";
	public static final String KEY_MESSAGE = "message";
	public static final String RESPONSE_CODE_SUCCESS = "00";
	public static final String RESPONSE_CODE = "responseCode";
	public static final String ERROR_MESSAGE = "errorMessage";
	
	private String priorityUrl;
	private String url;
	private String systemId;
	private String msgId;
	private String message;
	private String nohp;
	private String timeout;
	
	/**
	 * @return the timeout
	 */
	public String getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the nohp
	 */
	public String getNohp() {
		return nohp;
	}

	/**
	 * @param nohp the nohp to set
	 */
	public void setNohp(String nohp) {
		this.nohp = nohp;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the systemId
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * @param systemId the systemId to set
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	/**
	 * @return the msgId
	 */
	public String getMsgId() {
		return msgId;
	}

	/**
	 * @param msgId the msgId to set
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	private Log log = LogFactory.getLog(SimaspaySMSServiceImpl.class);

	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,rollbackFor=Throwable.class) 
	public void process(Exchange httpExchange) {

		log.info("SimaspaySMSServiceImpl :: process() BEGIN");
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
		String errorMessage = null;;

		try {
			
			String reqParams = getJSONString();
			log.info("SimaspaySMSServiceImpl :: process() url: "+getUrl()+", Request Params: "+reqParams);
			
			RSClientPostHttp client = new RSClientPostHttp();
			JSONObject responseObj = null;
			if(StringUtils.startsWithIgnoreCase(getMessage(), "Kode OTP Simaspay anda") ||
					StringUtils.startsWithIgnoreCase(getMessage(), "Your Simaspay code is")) {
				responseObj = client.callHttpPostService(getJSON(), getPriorityUrl(), Integer.parseInt(getTimeout()));
			}else {
				responseObj = client.callHttpPostService(getJSON(), getUrl(), Integer.parseInt(getTimeout()));
			}
			
			if(null != responseObj && !responseObj.get("status").equals("CommunicationFailure")) {
				
				if(responseObj.has(RESPONSE_CODE)) {
				
					responseCode = responseObj.getString(RESPONSE_CODE);
				}
				
				if(responseObj.has(ERROR_MESSAGE)) {
					
					errorMessage = responseObj.getString(ERROR_MESSAGE);
				}
				
			} else {
				
				log.info("SimaspaySMSServiceImpl :: process() there is a communication or timeout failure.");
			}
			
			log.info("SimaspaySMSServiceImpl :: process() responseCode = " + responseCode + ", error message =  " + errorMessage);
			
		} catch (Exception e) {
			
			log.error("SimaspaySMSServiceImpl catch block, Error communicating with SMS Service. Error message:" + e.getMessage());
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
		
		log.info("SimaspaySMSServiceImpl :: process() END");	
	}
	
	private String getJSONString() throws UnsupportedEncodingException, JSONException {
		
		JSONObject requestParams = new JSONObject();
		
		requestParams.put(KEY_SYSTEM_ID, getSystemId());
		requestParams.put(KEY_MSG_ID, getMsgId());
		requestParams.put(KEY_RECEPIENT_NO, getNohp());
		requestParams.put(KEY_MESSAGE, getMessage());
		
		return requestParams.toString();
	}
	
	private JSONObject getJSON() throws UnsupportedEncodingException, JSONException {
		
		JSONObject requestParams = new JSONObject();
		
		requestParams.put(KEY_SYSTEM_ID, getSystemId());
		requestParams.put(KEY_MSG_ID, getMsgId());
		requestParams.put(KEY_RECEPIENT_NO, getNohp());
		requestParams.put(KEY_MESSAGE, getMessage());
		
		return requestParams;
	}

	public String getPriorityUrl() {
		return priorityUrl;
	}

	public void setPriorityUrl(String priorityUrl) {
		this.priorityUrl = priorityUrl;
	}
}