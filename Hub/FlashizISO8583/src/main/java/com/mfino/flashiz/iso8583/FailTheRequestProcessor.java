package com.mfino.flashiz.iso8583;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.jpos.iso.ISOMsg;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class FailTheRequestProcessor implements Processor{

	private static Logger log = LoggerFactory.getLogger(FailTheRequestProcessor.class);
	
	private Endpoint replyEndpoint;
	private long timeout;

	public FailTheRequestProcessor(Endpoint replyEndpoint, long timeout)
	{
		this.replyEndpoint = replyEndpoint;
		this.timeout = timeout;
	}
	
	@Override
    public void process(Exchange exchange) throws Exception {

		log.info("Have to fail the request. " +exchange.getIn().getBody());
		ISOMsg mesg = exchange.getIn().getBody(ISOMsg.class);
		
		if (mesg.getMTI().equals("0200") || mesg.getMTI().equals("0220"))
		{
			mesg.setResponseMTI();
			mesg.set(39,"06");
			exchange.getOut().setBody(mesg);
			ProducerTemplate template = exchange.getContext().createProducerTemplate();
			template.start();
			template.sendBody(replyEndpoint, mesg);
			template.stop();
		}
		else
		{
			log.error("This request should not come to Bank Simulator would be ignored for now");
		}

		
    }

}
