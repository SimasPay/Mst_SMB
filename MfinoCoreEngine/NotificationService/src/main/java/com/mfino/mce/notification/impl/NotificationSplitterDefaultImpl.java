package com.mfino.mce.notification.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.notification.Notification;
import com.mfino.mce.notification.NotificationSplitterService;
import com.mfino.mce.notification.NotificationWrapper;

/**
 * 
 * @author POCHADRI
 * This service would take the notificationwrapper message containing all the notifications that 
 * need to be sent and sent them to appropriate notification sending services.
 * 
 * MAP with the class name of the notification and the destination queue to which the 
 * message need to be sent need to be populated.
 * This is done in the notification context spring file.
 */
public class NotificationSplitterDefaultImpl implements NotificationSplitterService
{
	private Map<String, String> classQueueMapping;
	private String webResponseQueue;
	private Log log = LogFactory.getLog(NotificationSplitterDefaultImpl.class);

	public NotificationSplitterDefaultImpl(Map<String,String> classQueueMapping)
	{
		this.classQueueMapping = classQueueMapping;
	}
	
	@Override
	public void process(Exchange exchange) throws Exception 
	{
		log.info("NotificationSplitterDefaultImpl:: process");
		NotificationWrapper notificationWrapper = exchange.getIn().getBody(NotificationWrapper.class);
		List<Notification> notificationList = notificationWrapper.getNotifications();
		for(Notification notification:notificationList)
		{
			String className = notification.getClass().getName();
			Class c = Class.forName(className);
			String queueName = classQueueMapping.get(className);
			log.info("Sending the Notification to the queue --> " + queueName);
			if(c.isInstance(notification))
			{
				ProducerTemplate template = exchange.getContext().createProducerTemplate();
				template.start();
				Map<String,Object> headersMap = MCEUtil.generateMandatoryHeaders(exchange.getIn().getHeaders());
				exchange.getContext().createProducerTemplate().sendBodyAndHeaders(queueName, notification,headersMap);
				template.stop();
			}
		}
		
		/**
		 * send the webreponse to the web response queue
		 */
		
		exchange.getIn().setBody(notificationWrapper.getWebResponse());
		exchange.setOut(exchange.getIn());
		//exchange.getContext().createProducerTemplate().sendBody(getWebResponseQueue(), notificationWrapper.getWebResponse());
	}

	public String getWebResponseQueue() {
		return webResponseQueue;
	}

	public void setWebResponseQueue(String webResponseQueue) {
		this.webResponseQueue = webResponseQueue;
	}
}
