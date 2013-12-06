package com.mfino.fidelity.iso8583;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.mce.core.util.MCEUtil;


public class BankSimulator implements Processor
{
	Log	log	= LogFactory.getLog(BankSimulator.class);
	
	private Endpoint replyEndpoint;
	public BankSimulator(Endpoint replyEndpoint)
	{
		this.replyEndpoint = replyEndpoint;
	}
	
	@Override
	public void process(Exchange exchange) throws Exception 
	{
		log.info("Got message in BS" +exchange.getIn().getBody());
		ISOMsg mesg = exchange.getIn().getBody(ISOMsg.class);
		log.info("Message in Bank Simulator " + mesg);
		
		if (mesg.getMTI().equals("1200"))
		{
			//TODO: fill the response
			mesg.setResponseMTI();
			mesg.set(39,"00");
			mesg.set(38,"123456");
//			setRequiredFields(mesg);
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

	private void setRequiredFields(ISOMsg mesg) throws ISOException {
		int pc = Integer.parseInt(mesg.getString(3));
		switch (pc) {
		case 310000:
			mesg.set(48, "+0000000000612196+0000000000612196+0000000000000000+0000000000000000+0000000000612196NGN");
			break;
		case 381000:
			mesg.set(125,"TERM_ID|DATE_TIME|ACC_ID1|TRAN_TYPE|TRAN_AMOUNT|FROM_ACC|CURR_CODE~00000000|20110413000000|0000000000000000000000000072|91|4000000||566~00000000|20110413000000|0000000000000000000000000072|91|1355500||566~00000000|20110408000000|0000000000000000000000000072|91|350000||566~00000000|20110408000000|0000000000000000000000000072|91|950000||566~00000000|20110408000000|0000000000000000000000000072|91|1000000||566~00000000|20110408000000|0000000000000000000000000072|91|2500000||566~00000000|20110406000000|0000000000000000000000000072|91|1400000||566~00000000|20110405000000|0000000000000000000000000072|91|2000000||566~00000000|20110331000000|0000000000000000000000000072|91|3000000||566~00000000|20110330000000|0000000000000000000000000072|91|800000||566~");
			break;
		case 401010:
			mesg.set(39,"000");
			break;
		default:
			break;
		}
		
	}

}
