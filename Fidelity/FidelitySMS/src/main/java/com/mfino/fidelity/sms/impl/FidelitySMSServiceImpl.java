/**
 * 
 */
package com.mfino.fidelity.sms.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDetailsDAO;
import com.mfino.domain.NotificationLogDetails;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.notification.SMSNotification;
import com.mfino.mce.notification.SMSNotificationService;

/**
 * @author shashank
 *
 */
public class FidelitySMSServiceImpl implements SMSNotificationService  {
	
	public static final String WEBSERVICE_OPERATION_NAME = "Send";
	private Log log = LogFactory.getLog(FidelitySMSServiceImpl.class);
	private int maxSMSLength;
 	private String webServiceEndpointBean;
 	
	public int getMaxSMSLength() {
		return maxSMSLength;
	}
	
	public void setMaxSMSLength(int maxSMSLength) {
		this.maxSMSLength = maxSMSLength;
	}
	
	public String getWebServiceEndpointBean() {
		return webServiceEndpointBean;
	}

	public void setWebServiceEndpointBean(String webServiceEndpointBean) {
		this.webServiceEndpointBean = webServiceEndpointBean;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void process(Exchange exchange) {
		log.info("FidelitySMSServiceImpl::process");
		SMSNotification smsNotification = exchange.getIn().getBody(SMSNotification.class);
		List<SMSNotification> notificationsList = null;
		List<Object> params = new ArrayList<Object>();
		List<Object> responseFromWS = null;
		
		Long notificationDetailsLogID = smsNotification.getNotificationLogDetailsID();
		NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
		NotificationLogDetails notificationLogDetails = null;
		if(notificationDetailsLogID != null)
		{
			notificationLogDetails = notificationLogDetailsDao.getById(notificationDetailsLogID);
		}		
		String actualSMS = smsNotification.getContent();
		
		if (actualSMS.length() > maxSMSLength) {
			List<String> lstSMS = splitEqually(actualSMS, maxSMSLength);
			notificationsList = new ArrayList<SMSNotification>(lstSMS.size());
			
			for (String sms: lstSMS) {
				SMSNotification notification = new SMSNotification();
				notification.setMdn(smsNotification.getMdn());
				notification.setContent(sms);
				notification.setNotificationLogDetailsID(smsNotification.getNotificationLogDetailsID());
				notificationsList.add(notification);
			}
		}
		else {
			notificationsList = new ArrayList<SMSNotification>(1);
			notificationsList.add(smsNotification);
		}
		
		for (SMSNotification smsnotify: notificationsList) {
			if (smsnotify != null) {
	 			params.add(smsnotify.getMdn()); // MobileNum
				params.add(smsnotify.getContent()); //Message
				params.add("");//foracid
				log.info("SMS to  --> " + smsnotify.getMdn() + ":: " + smsnotify.getContent());
				try
				{	
					ProducerTemplate template = exchange.getContext().createProducerTemplate();
					template.start();
					Map<String,Object> headersMap = new HashMap<String,Object>();
					headersMap.put("operationName",WEBSERVICE_OPERATION_NAME);
					MCEUtil.setMandatoryHeaders(exchange.getIn().getHeaders(), headersMap);
					responseFromWS = (List<Object>)template.requestBodyAndHeaders("cxf:bean:"+webServiceEndpointBean,params,headersMap);								
					template.stop();
				}
				catch(Exception e)
				{
					//any exception during call to web service need to be catched and it needs to be treated as failure
					log.info("Exception during call to web service for SMS",e);
					responseFromWS = new ArrayList<Object>();
					responseFromWS.add(MCEUtil.SERVICE_UNAVAILABLE); // TODO
					log.error(e.getMessage());
					if(notificationLogDetails != null )
					{
						notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Failed);
						notificationLogDetailsDao.save(notificationLogDetails);
						log.info("Failed to send sms with notificationLogDetailsID " + notificationDetailsLogID);
					}
					
				}				
			}
		}
		
		if((null != responseFromWS) && (responseFromWS.size() > 0)) {
			Object wsResponse = responseFromWS.get(0);
			log.info("Reponse from SMS WebService --> " + wsResponse);
			if(notificationLogDetails != null )
			{
				notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Success);
				notificationLogDetailsDao.save(notificationLogDetails);
				log.info("SMS with NotificationLogDetailsID " + notificationDetailsLogID + " was successfully sent");
			}
		}				
	}
	
	private List<String> splitEqually(String text, int size) {
		log.info("Splitting the SMS as length is greater than " + size);
	    List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

	    for (int start = 0; start < text.length(); start += size) {
	        ret.add(text.substring(start, Math.min(text.length(), start + size)));
	    }
	    return ret;
	}
}
