package com.mfino.gt.iso8583;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;

public class JPOSCommunicator {

	Log	                  log	= LogFactory.getLog(JPOSCommunicator.class);

	private Processor	  jposProcessor;
	private Processor	  simulator;
	private Processor	  bankLinkDownProcessor;
	private String muxName;

	public JPOSCommunicator(Processor prc, Processor sim, Processor linkDown,String muxName) {
		this.jposProcessor = prc;
		this.simulator = sim;
		this.bankLinkDownProcessor = linkDown;
		this.muxName = muxName;
	}

	@Handler
	public void processMessage(Exchange exchange) throws Exception {

		ISOMsg isoMsg = exchange.getIn().getBody(ISOMsg.class);

		if (NMStatus.Successful.equals(StatusRegistrar.getEchoStatus(this.muxName))) {

			if (isoMsg.getMTI().equals("0200") && isoMsg.getString(3).substring(0, 2).equals("30")) {
				log.info("isoMsg with rrn=" + isoMsg.getString(37) + " going for simulation");
				simulator.process(exchange);
			}
			else {
				log.info("isoMsg with rrn =" + isoMsg.getString(37) + " going for bank ");
				jposProcessor.process(exchange);
			}
		}
		else{
			log.info("isoMsg with rrn="+isoMsg.getString(37)+" is failed as echo status is linkdown");
			bankLinkDownProcessor.process(exchange);
		}
	}

}