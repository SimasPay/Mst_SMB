package com.mfino.bsm.iso8583;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.hibernate.Timestamp;
import com.mfino.mce.iso.jpos.comm.JPOSProcessor;
import com.mfino.mce.iso.jpos.nm.TimestampRegistrar;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;

public class JPOSCommunicator {

	private static Logger	log	= LoggerFactory.getLogger(JPOSCommunicator.class);

	private JPOSProcessor	jposProcessor;
	private BankSimulator	simulator;
	private Processor	  failureProcessor;
	private String	      muxName;
	private String	      statusIdentifier;
	private long	      keyExchangeValidDurationInMillis;

	public JPOSCommunicator(JPOSProcessor prc, BankSimulator sim, Processor failureProcessor, String muxName, long keDuration) {
		this.jposProcessor = prc;
		this.simulator = sim;
		this.failureProcessor = failureProcessor;
		this.muxName = muxName;
		this.keyExchangeValidDurationInMillis = keDuration * 60 * 1000;
	}

	@Handler
	public void processMessage(Exchange exchange) throws Exception {

		ISOMsg isoMsg = exchange.getIn().getBody(ISOMsg.class);

		NMStatus signonStatus = StatusRegistrar.getSignonStatus(muxName);
		NMStatus keyexStatus = StatusRegistrar.getKeyExchangeStatus(muxName);
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
		if (!NMStatus.Successful.equals(keyexStatus)) {
			log.info("key exchange status is " + signonStatus + ". Have to fail the transaction");
			fail = true;
		}

		Timestamp presentTime = new Timestamp();
		Timestamp lastKETime = TimestampRegistrar.getLastKeyExchangeTime(muxName);
		if (lastKETime == null || presentTime.getTime() - lastKETime.getTime() > this.keyExchangeValidDurationInMillis) {
			log.info("last key exchange was done at " + lastKETime + ".and it has been more than " + this.keyExchangeValidDurationInMillis
			        + " milliseconds");
			fail = true;
		}

		if (!fail) {

			if (isoMsg.getMTI().equals("0200") && isoMsg.getString(3).substring(0, 2).equals("37")) {
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