package com.mfino.mce.notification.impl;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDetailsDAO;
import com.mfino.domain.NlogDetails;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.notification.SMSNotification;
import com.mfino.mce.notification.SMSNotificationService;
import com.mfino.mce.notification.conf.SMSConf;

/**
 * 
 * @author POCHADRI
 * 
 * SMS Notification service. this service would take the sms notification message and create the 
 * sms notification message to be sent using the camel smsc integration.
 *
 */

public class SMSNotificationServiceDefaultImpl implements SMSNotificationService
{
	SMSConf smsConf;
	private Map<String,String> configMap;
	
	private Log log = LogFactory.getLog(SMSNotificationServiceDefaultImpl.class);

	public SMSConf getSmsConf() {
		return smsConf;
	}
	public void setSmsConf(SMSConf smsConf) {
		this.smsConf = smsConf;
	}

	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,isolation=Isolation.SERIALIZABLE) 
	public void process(Exchange exchange) throws Exception 
	{

		// TODO Auto-generated method stub
		SMSNotification smsNotification = exchange.getIn().getBody(SMSNotification.class);
		Long notificationDetailsLogID = smsNotification.getNotificationLogDetailsID();
		NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
		NlogDetails notificationLogDetails = null;
		if(notificationDetailsLogID != null)
		{
			notificationLogDetails = notificationLogDetailsDao.getById(notificationDetailsLogID);
		}
		
		try{
			//set all the values in the header which camel-smpp component would read 
			// and send the reply
			exchange.getOut().setHeader("CamelSmppSourceAddr",smsConf.getFrom());
			exchange.getOut().setHeader("CamelSmppDestAddr",smsNotification.getMdn());

			//actual message should be sent as string
			//set configured parameteres
			if(configMap!=null&&!configMap.isEmpty()){
				for(String key:configMap.keySet()){
					exchange.getOut().setHeader(key,configMap.get(key));
				}
			}

			exchange.getOut().setBody(smsNotification.getContent());

		}catch(Exception e)
		{
			log.error(e.getMessage());
			if(notificationLogDetails != null)
			{
				notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Failed);
				notificationLogDetailsDao.save(notificationLogDetails);
				log.info("Failed to send sms with notificationLogDetailsID " + notificationDetailsLogID);
			}
			throw new Exception(e);
		}
		if(notificationLogDetails != null)
		{
			notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Success);
			notificationLogDetailsDao.save(notificationLogDetails);
			log.info("SMS with NotificationLogDetailsID " + notificationDetailsLogID + " was successfully sent");
		}
	}
	public Map<String,String> getConfigMap() {
		return configMap;
	}
	public void setConfigMap(Map<String,String> configMap) {
		this.configMap = configMap;
	}

}
