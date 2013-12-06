package com.mfino.nfc.iso8583;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.mce.core.util.MCEUtil;


public class NFCSimulator implements Processor
{
	Log	log	= LogFactory.getLog(NFCSimulator.class);
	
	private Endpoint replyEndpoint;
	private long timeout;
	public NFCSimulator(Endpoint replyEndpoint, long timeout)
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
			/*
			 * setting the transactionIdentifier in the camel breadcrumbId header and sending
			 * both the header and body to next endpoint.The transactionIdentifer is used for identifying the transaction 
			 * in logs
			 */
			/*Map<String, Object> headersMap = MCEUtil.generateMandatoryHeaders(exchange.getIn().getHeaders());
			exchange.getContext().createProducerTemplate().sendBodyAndHeaders(replyEndpoint,headersMap);*/
			
			exchange.getContext().createProducerTemplate().sendBody(replyEndpoint, mesg);
		}
		else
		{
			log.error("This request should not come to Bank Simulator would be ignored for now");
		}
	}

}
