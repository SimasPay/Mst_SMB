package com.mfino.bsm.uangku.iso8583;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.mce.iso.jpos.comm.JPOSProcessor;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;

public class JPOSCommunicator {

	private static Logger	log	= LoggerFactory.getLogger(JPOSCommunicator.class);

	private JPOSProcessor	jposProcessor;
	private BankSimulator	simulator;
	private Processor	  failureProcessor;
	private String	      muxName;

	public JPOSCommunicator(JPOSProcessor prc, BankSimulator sim, Processor failureProcessor, String muxName) {
		this.jposProcessor = prc;
		this.simulator = sim;
		this.failureProcessor = failureProcessor;
		this.muxName = muxName;
	}

	@Handler
	public void processMessage(Exchange exchange) throws Exception {

		ISOMsg isoMsg = exchange.getIn().getBody(ISOMsg.class);

		NMStatus signonStatus = StatusRegistrar.getSignonStatus(muxName);
		NMStatus echoStatus = StatusRegistrar.getEchoStatus(muxName);

		boolean fail = false;

		if (!NMStatus.Successful.equals(signonStatus)) {
			log.info("signon status is " + signonStatus + ". Have to fail the transaction");
			fail = true;
		}
		if (!NMStatus.Successful.equals(echoStatus)) {
			log.info("echo status is " + signonStatus + ". Have to fail the transaction");
			fail = true;
		}

		if (!fail) {

			if (isoMsg.getMTI().equals("0200") && isoMsg.getString(3).substring(0, 2).equals("00")) {
				log.info("isoMsg with rrn=" + isoMsg.getString(37) + " going for simulation");
				this.simulator.process(exchange);
			}
			else {
				log.info("isoMsg with rrn =" + isoMsg.getString(37) + " going for bank ");
				this.jposProcessor.process(exchange);
			}
		}
		else {
			this.failureProcessor.process(exchange);
		}
	}
}