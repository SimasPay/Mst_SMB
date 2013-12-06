package com.mfino.integration.cashin;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.interceptor.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationCashinRouter extends RouteBuilder {
	
	private static Logger	log	= LoggerFactory.getLogger(IntegrationCashinRouter.class);
	private String	fronetendOutQ;
	public void setFronetendOutQ(String outQueue) {
		this.fronetendOutQ = outQueue;
	}

	private Endpoint	jettyEndpoint;
	public void setJettyEndpoint(Endpoint jettyEndpoint) {
		this.jettyEndpoint = jettyEndpoint;
	}

	private IntegrationCashinProcessor processor;
	public void setProcessor(IntegrationCashinProcessor processor) {
		this.processor = processor;
	}
	
	@Override
	public void configure() throws Exception {

		log.info("adding GTCashinRouter");

		getContext().addInterceptStrategy(new Tracer());

		log.info("got a jetty end point");

		this.processor.setFronetendOutQ(fronetendOutQ);
		from(jettyEndpoint).process(this.processor);

		from(fronetendOutQ).process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				String sync_id = (String) exchange.getIn().getHeader("synchronous_request_id");

				CamelContext camelContext = exchange.getContext();
				ProducerTemplate template = camelContext.createProducerTemplate();
				template.start();
				template.send("seda:" + sync_id, exchange);
				template.stop();
			}
		});
	}

}
