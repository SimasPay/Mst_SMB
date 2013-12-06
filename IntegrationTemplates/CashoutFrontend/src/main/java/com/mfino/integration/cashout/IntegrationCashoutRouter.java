package com.mfino.integration.cashout;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.interceptor.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.integration.xml.RequestResponseTransformation;

public class IntegrationCashoutRouter extends RouteBuilder {

	private String	        channelNumber;

	public void setChannelNumber(String channelNumber) {
		this.channelNumber = channelNumber;
	}

	private String	backendResultOutQueue;

	public void setBackendResultOutQueue(String backendResultOutQueue) {
		this.backendResultOutQueue = backendResultOutQueue;
	}
	
	private String cashinInQueue ;
	public void setCashinInQueue(String cashinInQueue) {
    	this.cashinInQueue = cashinInQueue;
    }

	private RequestResponseTransformation	xsltTransformation;

	public RequestResponseTransformation getXsltTransformation() {
		return xsltTransformation;
	}

	public void setXsltTransformation(RequestResponseTransformation xsltTransformation) {
		this.xsltTransformation = xsltTransformation;
	}
	
	private Endpoint jettyEndpoint;
	public void setJettyEndpoint(Endpoint jettyEndpoint) {
    	this.jettyEndpoint = jettyEndpoint;
    }

	Logger	log	= LoggerFactory.getLogger(IntegrationCashoutRouter.class);

	@Override
	public void configure() throws Exception {

		log.info("adding GTCashinRouter");

		getContext().addInterceptStrategy(new Tracer());
//		Endpoint endPoint = jetty

		log.info("got a jetty end point");

		IntegrationCashoutProcessor pro = new IntegrationCashoutProcessor();
		// pro.setRequestXSLTfile(requestXSLTfile);
		// pro.setResponseXSLTfile(responseXSLTfile);
		pro.setChannelNumber(this.channelNumber);
		pro.setCashOutQueue(cashinInQueue);
		// pro.setJmsCashinInQueue(jmsCashinInQueue);
		from(jettyEndpoint).process(pro);

		// "jms:GOQ?disableReplyTo=true"
		from(backendResultOutQueue).process(new Processor() {
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
