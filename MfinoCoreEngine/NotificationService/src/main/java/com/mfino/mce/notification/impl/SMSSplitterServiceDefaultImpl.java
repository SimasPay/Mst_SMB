/**
 * 
 */
package com.mfino.mce.notification.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.notification.SMSNotification;
import com.mfino.mce.notification.SMSSplitterService;

/**
 * @author Bala Sunku
 *
 */
public class SMSSplitterServiceDefaultImpl implements SMSSplitterService {
	
	private Log log = LogFactory.getLog(SMSSplitterServiceDefaultImpl.class);
	private int maxSMSLength;
	private String smsQueue;

	public int getMaxSMSLength() {
		return maxSMSLength;
	}

	public void setMaxSMSLength(int maxSMSLength) {
		this.maxSMSLength = maxSMSLength;
	}

	public String getSmsQueue() {
		return smsQueue;
	}

	public void setSmsQueue(String smsQueue) {
		this.smsQueue = smsQueue;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		log.info("SMSSplitterServiceDefaultImpl::process BEGIN");
		List<SMSNotification> lstNotifications = null;
		SMSNotification smsNotification = exchange.getIn().getBody(SMSNotification.class);
		String actualSMS = smsNotification.getContent();
		
		if (actualSMS.length() > maxSMSLength) {
			List<String> lstSMS = splitEqually(actualSMS, maxSMSLength);
			lstNotifications = new ArrayList<SMSNotification>(lstSMS.size());
			
			for (String sms: lstSMS) {
				SMSNotification notification = new SMSNotification();
				notification.setMdn(smsNotification.getMdn());
				notification.setContent(sms);
				notification.setNotificationLogDetailsID(smsNotification.getNotificationLogDetailsID());
				lstNotifications.add(notification);
			}
		}
		else {
			lstNotifications = new ArrayList<SMSNotification>(1);
			lstNotifications.add(smsNotification);
		}
		
		for (SMSNotification smsnotify: lstNotifications) {
			ProducerTemplate template = exchange.getContext().createProducerTemplate();
			template.start();
			Map<String,Object> headersMap = MCEUtil.generateMandatoryHeaders(exchange.getIn().getHeaders());
			template.sendBodyAndHeaders(getSmsQueue(), smsnotify,headersMap);
			template.stop();
			exchange.getIn().setBody(smsnotify);
			exchange.setOut(exchange.getIn());
		}
		log.info("SMSSplitterServiceDefaultImpl::process END");
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
