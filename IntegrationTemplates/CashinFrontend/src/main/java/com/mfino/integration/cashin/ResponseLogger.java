package com.mfino.integration.cashin;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseLogger implements Processor{

	
	Logger logger = LoggerFactory.getLogger(ResponseLogger.class);
	
	@Override
    public void process(Exchange exchange) throws Exception {
		
		String str = exchange.getOut().getBody(String.class);
		logger.info("received in ResponseLogger response-->" +str);
	    
    }

}
