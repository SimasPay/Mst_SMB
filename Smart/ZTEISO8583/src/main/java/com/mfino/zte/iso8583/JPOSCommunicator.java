package com.mfino.zte.iso8583;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;
import com.mfino.mce.iso.jpos.comm.JPOSProcessor;

public class JPOSCommunicator {

	Log	                  log	= LogFactory.getLog(JPOSCommunicator.class);

	private JPOSProcessor	jposProcessor;
	private ZTESimulator	simulator;

	public JPOSCommunicator(JPOSProcessor prc, ZTESimulator sim) {
		this.jposProcessor = prc;
		this.simulator = sim;
	}

	@Handler
	public void processMessage(Exchange exchange) throws Exception {

		ISOMsg isoMsg = exchange.getIn().getBody(ISOMsg.class);

		/*
		if (isoMsg.getMTI().equals("0200") && isoMsg.getString(3).substring(0, 2).equals("30")) {
			log.info("isoMsg with rrn=" + isoMsg.getString(37) + " going for simulation");
			simulator.process(exchange);
		}
		else {*/
			log.info("isoMsg with rrn =" + isoMsg.getString(37) + " going for ZTE");
			log.info(isoMsg.toString());
			jposProcessor.process(exchange);
//		}
	}
}