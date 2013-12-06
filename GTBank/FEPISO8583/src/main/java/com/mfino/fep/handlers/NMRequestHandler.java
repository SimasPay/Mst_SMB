package com.mfino.fep.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;

public class NMRequestHandler implements Runnable {

	private static Log	log	= LogFactory.getLog(NMRequestHandler.class);

	private ISOMsg	   msg;
	private ISOSource	source;

	public NMRequestHandler(ISOMsg msg, ISOSource source) {
		this.msg = msg;
		this.source = source;
	}

	@Override
	public void run() {
		try {
			log.info("network mangement iso msg received " + msg);

			// sigon
			String code = msg.getValue(70).toString();
			/*no sign on for FEP
			 * if ("001".equals(code)) {
				log.info("request is signon");
				msg.set(39, "00");
				log.info("setting signon status to true");
				FEPEnum.Signon.setStatus(true);
			}// signoff
			else if ("002".equals(code)) {
				log.info("request is signoff");
				msg.set(39, "00");
				log.info("setting signon status to false");
				FEPEnum.Signon.setStatus(false);
			}// echo
			else */
			if ("301".equals(code)) {
				log.info("request is echo");
				msg.set(39, "00");
			}
			else {
				log.warn("unsupported network management request is received");
				msg.set(39, "40");
			}

		}
		catch (Exception ex) {
			log.error("Exception occured while handling vah network mgmt request", ex);
			try {
				msg.set(39, "06");
			}
			catch (ISOException ex1) {
				log.error("couldn't even set field 39",ex1);
			}
		}
		finally {
			try {
				msg.setResponseMTI();
				log.info("sending isomsg response:"+msg);
				source.send(msg);
			}
			catch (Exception ex) {
				log.error("Exception occured while sending iso response." + ex.getLocalizedMessage());
			}
		}
	}
}
