/**
 * 
 */
package com.mfino.alertz.sms.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
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
 * @author Satya Sekhar
 * 
 */
public class AlertzSMSServiceImpl implements SMSNotificationService {	
	
	private Log log = LogFactory.getLog(AlertzSMSServiceImpl.class);
	private String webserviceOperationName;
	private String senderID;
	private String webServiceEndpointBean;

	public String getWebserviceOperationName() {
		return webserviceOperationName;
	}

	public void setWebserviceOperationName(String webserviceOperationName) {
		this.webserviceOperationName = webserviceOperationName;
	}

	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}

	public String getWebServiceEndpointBean() {
		return webServiceEndpointBean;
	}

	public void setWebServiceEndpointBean(String webServiceEndpointBean) {
		this.webServiceEndpointBean = webServiceEndpointBean;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void process(Exchange exchange) {
		log.info("AlertzSMSServiceImpl::process");
		SMSNotification smsNotification = exchange.getIn().getBody(
				SMSNotification.class);
		List<Object> params = new ArrayList<Object>();
		List<Object> responseFromWS = null;
		
		Long notificationDetailsLogID = smsNotification.getNotificationLogDetailsID();
		NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
		NotificationLogDetails notificationLogDetails = null;
		if(notificationDetailsLogID != null)
		{
			notificationLogDetails = notificationLogDetailsDao.getById(notificationDetailsLogID);
		}	

		if (smsNotification != null) {
			params.add("0"); // reference
			params.add(smsNotification.getMdn()); // destinationAddress
			params.add(smsNotification.getContent()); // message
			params.add(""); // account_no
			params.add(senderID); //sender
			
			log.info("SMS to  --> " + smsNotification.getMdn() + ":: " + smsNotification.getContent());
			try
			{	
				ProducerTemplate template = exchange.getContext().createProducerTemplate();
				template.start();
				responseFromWS = (List<Object>)template
								.requestBodyAndHeader("cxf:bean:"+webServiceEndpointBean,params,
										"operationName",webserviceOperationName);
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
}
