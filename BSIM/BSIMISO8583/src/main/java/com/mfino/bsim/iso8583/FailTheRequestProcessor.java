package com.mfino.bsim.iso8583;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.jpos.iso.ISOMsg;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.mfino.mce.core.util.MCEUtil;

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
		
		if (mesg.getMTI().equals("0200"))
		{
			mesg.setResponseMTI();
			mesg.set(39,"06");
			exchange.getOut().setBody(mesg);
			ProducerTemplate template = exchange.getContext().createProducerTemplate();
			template.start();
			/*
			 * setting the transactionIdentifier in the camel breadcrumbId header and sending
			 * both the header and body to next endpoint.The transactionIdentifer is used for identifying the transaction 
			 * in logs
			 */
			Map<String, Object> headersMap = MCEUtil.generateMandatoryHeaders(exchange.getIn().getHeaders());
			template.sendBodyAndHeaders(replyEndpoint, mesg,headersMap);
			template.stop();
		}
		else
		{
			log.error("This request should not come to Bank Simulator would be ignored for now");
		}

		
    }

}
