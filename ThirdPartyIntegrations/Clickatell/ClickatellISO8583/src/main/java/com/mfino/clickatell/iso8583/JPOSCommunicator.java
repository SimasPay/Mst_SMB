package com.mfino.clickatell.iso8583;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;
import com.mfino.mce.iso.jpos.comm.JPOSProcessor;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;

public class JPOSCommunicator {

	Log	                  log	= LogFactory.getLog(JPOSCommunicator.class);

	private JPOSProcessor	jposProcessor;
	private Processor	  bankLinkDownProcessor;
	private String muxName;

	public JPOSCommunicator(JPOSProcessor prc,Processor linkDown,String muxName) {
		this.jposProcessor = prc;
		this.bankLinkDownProcessor = linkDown;
		this.muxName = muxName;
	}

	@Handler
	public void processMessage(Exchange exchange) throws Exception {
		ISOMsg isoMsg = exchange.getIn().getBody(ISOMsg.class);
		if (NMStatus.Successful.equals(StatusRegistrar.getSignonStatus(this.muxName))) {
	        jposProcessor.process(exchange);
		}else{
			log.info("isoMsg with rrn="+isoMsg.getString(37)+" is failed as echo status is linkdown");
			bankLinkDownProcessor.process(exchange);
		}
	}
}