/**
 * 
 */
package com.mfino.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mfino.dao.DAOFactory;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMSMSNotification;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.HibernateService;
import com.mfino.service.SMSService;
import com.mfino.util.ConfigurationUtil;


/**
 * @author Deva
 *
 */
@Service("SMSServiceImpl")
public class SMSServiceImpl extends MultixCommunicationHandler implements SMSService{
	
	private static ExecutorService threadPool = Executors.newCachedThreadPool();  

	private String destinationMDN;
	
	private String sourceMDN;
	
	private String message;
	
	private String senderName;
	
	//private static String sendSMSURL = null;
	
	private String accessCode;
	
	private String smsc;
	
	private Long transactionid;
	
	private int notificationCode;
	
	private Long sctlId;
	
	private Long notificationLogDetailsID;
	
	private boolean isDuplicateSMS = false;
	
	private String transactionIdentifier;
	// Kannel Send SMS related constants
	
	private static final String KANNEL_USERNAME_PARAMNAME = "username";
	
	private static final String KANNEL_PASSWORD_PARAMNAME = "password";
	
	private static final String KANNEL_FROM_PARAMNAME = "from";
	
	private static final String KANNEL_TO_PARAMNAME = "to";
	
	private static final String KANNEL_TEXT_PARAMNAME = "text";
	
	private static final String KANNEL_BINFO_PARAMNAME = "binfo";
	
	private static final String KANNEL_DLRMASK_PARAMNAME = "dlr-mask";
	
	private static final String KANNEL_DLRURL_PARAMNAME = "dlr-url";
	
	private static final String KANNEL_SMSC_PARAMNAME = "smsc";
	
	public static final String KANNEL_DLR_STATUS_QUERYNAME = "status";

	public static final String KANNEL_DLR_ID_QUERYNAME = "id";

	public static final String KANNEL_DLR_TIME_QUERYNAME = "time";

	public static final String KANNEL_DLR_FID_QUERYNAME = "fid";

	private static final String ENCODING = "UTF-8";
	
	public static final String	       DEFAULT_URL	= ConfigurationUtil.getBackendURL();

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public CFIXMsg process(CFIXMsg msg) {
		log.info("Request to Core Engine " + msg.DumpFields());
		CFIXMsg errorMsg = null;
		try {
			errorMsg = handleRequestResponse((CMBase) msg, DEFAULT_URL, ((CMBase) msg).getSourceApplication());
			if (errorMsg != null)
				log.info("Response from Core Engine " + errorMsg.DumpFields());
			return errorMsg;
		}
		catch (Exception error) {
			// Send SMS from here
			log.error("Unexpected Exception " + error.getMessage(), error);
			return null;
		}
	}
	
    public boolean send() {
        try {
        	CMSMSNotification sms = new CMSMSNotification();
        	sms.setTo(destinationMDN);
        	sms.setText(message);
        	sms.setCode(notificationCode);
        	sms.setServiceChargeTransactionLogID(sctlId);
        	sms.setNotificationLogDetailsID(notificationLogDetailsID);
        	sms.setIsDuplicateSMS(isDuplicateSMS());
    		//setting the transactionIdentifier to be use in fix to set the camel breadcrumbId header later
        	sms.setTransactionIdentifier(getTransactionIdentifier());
        	CFIXMsg resMsg =process(sms);
//            HttpResponse httpResponse = send(false);
//            if (httpResponse != null) {
//                if (httpResponse.getStatusLine().getStatusCode() == 202) {
//                    return true;
//                }
//            }
        	return true;
        } catch (Exception e) {
            log.error("failed to send sms",e);
        }
        return false;
    }

	/**
	 * Send SMS based on the properties set. If <code>isAlert</code> is <code>true</code>,
	 * the Kannel configurations corresponding to SMS Alerts are used, otherwise configurations
	 * to SMS notifications are used.
	 *
	 * @return HttpResponse
	 */
	public HttpResponse send(boolean isAlert){
		//remove this 
		log.info("message:"+ message);
		HttpResponse httpResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair(KANNEL_FROM_PARAMNAME, sourceMDN));
		qparams.add(new BasicNameValuePair(KANNEL_TO_PARAMNAME, destinationMDN));
		qparams.add(new BasicNameValuePair(KANNEL_TEXT_PARAMNAME, message));
		qparams.add(new BasicNameValuePair(KANNEL_BINFO_PARAMNAME, accessCode));
		if(smsc != null) {
			qparams.add(new BasicNameValuePair(KANNEL_SMSC_PARAMNAME, smsc));
		}
		qparams.add(new BasicNameValuePair(KANNEL_DLRMASK_PARAMNAME, "31"));

		List<NameValuePair> dlrparams = new ArrayList<NameValuePair>();
		dlrparams.add(new BasicNameValuePair(KANNEL_DLR_FID_QUERYNAME, "%F"));
		dlrparams.add(new BasicNameValuePair(KANNEL_DLR_STATUS_QUERYNAME, "%d"));
		dlrparams.add(new BasicNameValuePair(KANNEL_DLR_TIME_QUERYNAME, "%T"));
		dlrparams.add(new BasicNameValuePair(KANNEL_DLR_ID_QUERYNAME, String.valueOf(transactionid)));

		String dlrhost = ConfigurationUtil.getDLRHost();
		String userName = ConfigurationUtil.getSMSGatewayUser();
		String password = ConfigurationUtil.getSMSGatewayPassword();
		String url = ConfigurationUtil.getSMSGatewayURL();
		if(isAlert){
			dlrhost = ConfigurationUtil.getSMSAlertsDLRHost();
			userName = ConfigurationUtil.getSMSAlertsGatewayUser();
			url = ConfigurationUtil.getSMSAlertsGatewayURL();
			password = ConfigurationUtil.getSMSAlertsGatewayPassword();
		}
		
		StringBuffer paramBuffer = new StringBuffer();
		for(int index=0; index<dlrparams.size(); index++){
			if(index!=0){
				paramBuffer.append('&');
			}
			NameValuePair pair = dlrparams.get(index);
			paramBuffer.append(pair.getName());
			paramBuffer.append('=');
			paramBuffer.append(pair.getValue());
			
		}
		String dlrQuery = paramBuffer.toString();
		
		qparams.add(new BasicNameValuePair(KANNEL_USERNAME_PARAMNAME, userName));
		qparams.add(new BasicNameValuePair(KANNEL_PASSWORD_PARAMNAME, password));
		qparams.add(new BasicNameValuePair(KANNEL_DLRURL_PARAMNAME, dlrhost + "?" + dlrQuery));
		HttpGet httpget = new HttpGet(url + "?" + URLEncodedUtils.format(qparams, ENCODING));
		if(isAlert)
			log.info(httpget.getURI().toString());
		try {
			httpResponse = httpclient.execute(httpget);
			log.info("message sent from "+sourceMDN+" to "+destinationMDN);
		} catch (ClientProtocolException protocolEx) {
			log.error("Error sending http request for sms", protocolEx);
		} catch (IOException ioEx) {
			log.error("Error sending http request for sms", ioEx);
		}
		return httpResponse;
	}
	
	 public void asyncSendSMS(){ 	
	    	threadPool.execute(new Runnable() {			
				@Override
				public void run(){
					Session session = null;
					HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
					session = hibernateService.getSessionFactory().openSession();
					HibernateSessionHolder hibernateSessionHolder = hibernateService.getHibernateSessionHolder();
					hibernateSessionHolder.setSession(session);		
					DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);

					//log.info("ThreadPool ID: " + threadPool.toString());
					log.info("sending sms to "+destinationMDN);
					send();	
					session.close();
				}
			});
	    	  	
	    }

	/**
	 * @return the destinationMDN
	 */
	public String getDestinationMDN() {
		return destinationMDN;
	}

	/**
	 * @param destinationMDN the destinationMDN to set
	 */
	public void setDestinationMDN(String destinationMDN) {
		this.destinationMDN = destinationMDN;
	}

	/**
	 * @return the sourceMDN
	 */
	public String getSourceMDN() {
		return sourceMDN;
	}

	/**
	 * @param sourceMDN the sourceMDN to set
	 */
	public void setSourceMDN(String sourceMDN) {
		this.sourceMDN = sourceMDN;
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

	/**
	 * @return the senderName
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * @param senderName the senderName to set
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	/**
	 * @return the accessCode
	 */
	public String getAccessCode() {
		return accessCode;
	}

	/**
	 * @param accessCode the accessCode to set
	 */
	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	/**
	 * @return the smsc
	 */
	public String getSmsc() {
		return smsc;
	}

	/**
	 * @param smsc the smsc to set
	 */
	public void setSmsc(String smsc) {
		this.smsc = smsc;
	}

	/**
	 *
	 * @param transactionID
	 */
	public void setTransactionID(Long transactionID){
		this.transactionid = transactionID;
	}
	
	/**
	 *
	 * @return
	 */
	public Long getTransactionID(){
		return transactionid;
}

	/**
	 * @param notificationCode the notificationCode to set
	 */
	public void setNotificationCode(int notificationCode) {
		this.notificationCode = notificationCode;
	}

	/**
	 * @return the notificationCode
	 */
	public int getNotificationCode() {
		return notificationCode;
	}
	
	/**
	 * 
	 * @return the sctlId
	 */
	public Long getSctlId() {
		return sctlId;
	}

	/**
	 * 
	 * @param sctlId
	 */
	public void setSctlId(Long sctlId) {
		this.sctlId = sctlId;
	}

	public boolean isDuplicateSMS() {
		return isDuplicateSMS;
	}

	public void setDuplicateSMS(boolean isDuplicateSMS) {
		this.isDuplicateSMS = isDuplicateSMS;
	}

	public Long getNotificationLogDetailsID() {
		return notificationLogDetailsID;
	}

	public void setNotificationLogDetailsID(Long notificationLogDetailsID) {
		this.notificationLogDetailsID = notificationLogDetailsID;
	}

	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}

	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}

	
}
