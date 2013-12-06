package com.mfino.cashin;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelBasedRooter implements Processor{

	Logger log = LoggerFactory.getLogger(ChannelBasedRooter.class);
	
	private Properties	props	= new Properties();
	
	String	           defaultQueue="jms:DeadChannelQueue?disableReplyTo=true";

	public ChannelBasedRooter(String propFile) throws Exception{
		log.info("channelToQueueMappingFile=" + propFile);
		props.load(new FileInputStream(new File(propFile)));
	}
	
	public String getDefaultQueue() {
		return defaultQueue;
	}

	public void setDefaultQueue(String defaultQueue) {
		this.defaultQueue = defaultQueue;
	}

	@Override
    public void process(Exchange exchange) throws Exception {

		CamelContext context = exchange.getContext();
		
		String syncId = exchange.getIn().getHeader("synchronous_request_id").toString();
		String channel = exchange.getIn().getHeader("FrontendID").toString();

		log.info("finding the next queue ::");

		if (props == null) {
			log.info("can not load properties file so returning default queue");
			context.createProducerTemplate().send(defaultQueue, exchange);
		}

		String nextQueue = props.getProperty(channel);
		if (nextQueue == null) {
			log.info("bankcode=" + channel + " doesn't have a valid queue associated with it.");
			context.createProducerTemplate().send(defaultQueue, exchange);
		}
		log.info("returning nextqueue=" + nextQueue);
		
		context.createProducerTemplate().send(nextQueue, exchange);
		
    }

}
