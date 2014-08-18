package com.mfino.bsm.uangku.iso8583;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.mce.core.util.MCEUtil;


public class BankSimulator implements Processor
{
	Log	log	= LogFactory.getLog(BankSimulator.class);
	
	private Endpoint replyEndpoint;
	private long timeout;
	public BankSimulator(Endpoint replyEndpoint, long timeout)
	{
		this.replyEndpoint = replyEndpoint;
		this.timeout = timeout;
	}
	
	@Override
	public void process(Exchange exchange) throws Exception 
	{
		log.info("Got message in BS" +exchange.getIn().getBody());
		ISOMsg mesg = exchange.getIn().getBody(ISOMsg.class);
		log.info("Message in Bank Simulator " + mesg);
		
		if (mesg.getMTI().equals("0200"))
		{
			//TODO: fill the response
			mesg.setResponseMTI();
			mesg.set(39,"00");
			mesg.set(38,"123456");
			exchange.getOut().setBody(mesg);
			ProducerTemplate template = exchange.getContext().createProducerTemplate();
			template.start();
			Map<String,Object> headersMap = MCEUtil.generateMandatoryHeaders(exchange.getIn().getHeaders());
			template.sendBodyAndHeaders(replyEndpoint, mesg,headersMap);
			template.stop();
		}
		else
		{
			log.error("This request should not come to Bank Simulator would be ignored for now");
		}
	}

}
