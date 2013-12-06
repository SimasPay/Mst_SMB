package com.mfino.fidelity.iso8583;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.mce.core.util.MCEUtil;

public class BankLinkDownProcessor implements Processor {
	Log	             log	= LogFactory.getLog(BankLinkDownProcessor.class);

	private Endpoint	replyEndpoint;

	public BankLinkDownProcessor(Endpoint replyEndpoint) {
		this.replyEndpoint = replyEndpoint;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		log.info("Got message in BankLindDownProcessor" + exchange.getIn().getBody());
		ISOMsg mesg = exchange.getIn().getBody(ISOMsg.class);
		log.info("Message in Bank Simulator " + mesg);

		if (mesg.getMTI().equals("1200")) {
			mesg.setResponseMTI();
			mesg.set(39, "06");
			exchange.getOut().setBody(mesg);
			Map<String,Object> headersMap = MCEUtil.generateMandatoryHeaders(exchange.getIn().getHeaders());
			exchange.getContext().createProducerTemplate().sendBodyAndHeaders(replyEndpoint, mesg,headersMap);
		}
		else {
			log.error("This request should not come to BankLinkDownProcessor would be ignored for now");
		}
	}

}
