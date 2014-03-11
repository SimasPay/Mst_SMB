package com.mfino.flashiz.iso8583.nm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.iso.packager.XMLPackager;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;


public class NMRequestHandler implements Runnable {

	public NMRequestHandler() {
		
	}

	private static Log	log	= LogFactory.getLog(NMRequestHandler.class);

	private ISOMsg	   msg;
	public ISOMsg getMsg() {
		return msg;
	}

	public void setMsg(ISOMsg msg) {
		this.msg = msg;
	}

	public ISOSource getSource() {
		return source;
	}

	public void setSource(ISOSource source) {
		this.source = source;
	}

	private ISOSource	source;
	private String	   muxName="flashizmux";
	private static NMRequestHandler nmRequestHandler;
	
	
	
	public static NMRequestHandler createInstance(){
		if(nmRequestHandler==null)
			{
			nmRequestHandler = new NMRequestHandler();
			}
		return nmRequestHandler;
	}
	
	public static NMRequestHandler getInstance(){
		if(nmRequestHandler==null){
			throw new RuntimeException("Instance is not already created");
		}
		return nmRequestHandler;
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
