package com.mfino.integration.cashin;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLogger implements Processor{

	
	Logger logger = LoggerFactory.getLogger(RequestLogger.class);
	
	@Override
    public void process(Exchange exchange) throws Exception {
		
		String str = exchange.getIn().getBody(String.class);
		logger.info("received in RequestLogger request -->" +str);
		
		
		
	    
    }

}
