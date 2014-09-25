package com.mfino.bsim.iso8583.nm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.packager.XMLPackager;

import com.mfino.bsim.iso8583.nm.exceptions.InvalidWorkingKeyException;
import com.mfino.bsim.iso8583.nm.exceptions.KcvValidationFailedException;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;


public class NMRequestHandler implements Runnable {

	private static Log	log	= LogFactory.getLog(NMRequestHandler.class);

	private ISOMsg	   msg;
	private ISOSource	source;
	private String	   muxName;

	public NMRequestHandler(ISOMsg msg, ISOSource source) {
		this.msg = msg;
		this.source = source;
		this.muxName = "bsmmux";
	}

	@Override
	public void run() {
		try {
			log.info("network mangement iso msg received " + msg);

			XMLPackager packager = new XMLPackager();
			log.info("received isomsg-->" + new String(packager.pack(msg)));

			// sigon
			String code = msg.getValue(70).toString();
			if ("001".equals(code)) {
				log.info("request is signon");
				msg.set(39, "00");
				Thread.sleep(10000);
				log.info("setting signon status to true");
				StatusRegistrar.setSignonStatus(muxName, NMStatus.Successful);
			}// signoff
			else if ("002".equals(code)) {
				log.info("request is signoff");
				msg.set(39, "00");
				log.info("setting signon status to false");
				StatusRegistrar.setSignonStatus(muxName, NMStatus.Successful);
			}// echo
			else if ("301".equals(code)) {
				log.info("request is echo");
				msg.set(39, "00");
				StatusRegistrar.setEchoStatus(muxName, NMStatus.Successful);
			}// key exchange
			else if ("101".equals(code)) {
				log.info("request is key exchange");
				NMStatus status = StatusRegistrar.getSignonStatus(muxName);
				StatusRegistrar.setKeyExchangeStatus(muxName, NMStatus.Failed);
				if (status.equals(NMStatus.Successful)) {
					log.info("Sign-on has been completed, processing key exchange");
					try {
						KeyExchangeHandler.getInstance().handle(msg);
						msg.set(39, "00");
					}
					catch (KcvValidationFailedException ex) {
						log.error("kcv validation failed.Sending M1 as error code", ex);
						msg.set(39, "M1");
					}
					catch (InvalidWorkingKeyException ex) {
						log.error("working key calculation failed.sending M2 as error code", ex);
						msg.set(39, "M2");
					}
					catch (Exception ex) {
						log.error("Exception occured while handling the key exchange.", ex);
						msg.set(39, "06");
					}
					StatusRegistrar.setKeyExchangeStatus(muxName, NMStatus.Successful);
				}
				else {
					log.error("key exchange failed because sigon was not done successfully");
					msg.set(39, "57");
				}
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
				log.error("couldn't even set field 39", ex1);
			}
		}
		finally {
			try {
				msg.setResponseMTI();
				XMLPackager packager = new XMLPackager();
				log.info("response isomsg-->" + new String(packager.pack(msg)));

				log.info("sending isomsg");
				source.send(msg);
			}
			catch (Exception ex) {
				log.error("Exception occured while sending iso response." + ex.getLocalizedMessage());
			}
		}
	}
}
