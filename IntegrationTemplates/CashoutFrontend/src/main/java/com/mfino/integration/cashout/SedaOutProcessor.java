package com.mfino.integration.cashout;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.eclipse.jetty.util.log.Log;

public class SedaOutProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
	
		Log.info("enterd the seda processor for cashout");
		
		String sync_id = (String) exchange.getIn().getHeader("synchronous_request_id");

		CamelContext camelContext = exchange.getContext();
		ProducerTemplate template = camelContext.createProducerTemplate();
		template.start();
		template.send("seda:" + sync_id, exchange);
		template.stop();

	}

}
