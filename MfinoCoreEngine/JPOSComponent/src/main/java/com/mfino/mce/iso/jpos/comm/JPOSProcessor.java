package com.mfino.mce.iso.jpos.comm;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.q2.iso.QMUX;
import org.jpos.util.NameRegistrar.NotFoundException;


import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.iso.jpos.NoISOResponseException;

public class JPOSProcessor implements Processor {
	Log	             log	= LogFactory.getLog(JPOSProcessor.class);
	
	private String muxName;
	private Endpoint	replyEndpoint;
	private long	 timeout;

	public JPOSProcessor(String muxName, Endpoint replyEndpoint, long timeout) {
		this.muxName =  muxName;
		this.replyEndpoint = replyEndpoint;
		this.timeout = timeout;
		log.info("JPOSProcessor instance created for muxName="+muxName);
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		log.info("JPOSProcessor == process muxName="+muxName);
		
		try 
		{
			QMUX mux = (QMUX) QMUX.getMUX(muxName);
			ISOMsg msg = exchange.getIn().getBody(ISOMsg.class);
			
//			ISOPackager packager = msg.getPackager();
//			log.info("JPOSProcessor "+muxName+" Sending ISO message-->" + new String(packager.pack(msg)));
//			log.info("JPOSProcessor "+muxName+" Sending ISO message byes -->" + msg.getBytes());
			log.info("JPOSProcessor "+muxName+" About to send message " + msg);
			
			
			ISOMsg replyMsg = mux.request(msg, timeout);
			
			
//			ISOMsg replyMsg  = new ISOMsg();
//			replyMsg.setMTI("0210");
//			replyMsg.set(2, msg.getString(2));
//			replyMsg.set(3, msg.getString(3));
//			replyMsg.set(7, msg.getString(7));
//			replyMsg.set(11, msg.getString(11));
//			replyMsg.set(12, msg.getString(12));
//			replyMsg.set(13, msg.getString(13));
//			replyMsg.set(27, msg.getString(27));
//			replyMsg.set(32, msg.getString(32));
//			replyMsg.set(33, msg.getString(33));
//			replyMsg.set(37, msg.getString(37));
//			replyMsg.set(42, msg.getString(42));
//			replyMsg.set(43, msg.getString(43));
//			replyMsg.set(48, msg.getString(48));
//			
//			replyMsg.set(38, "654321");
//			replyMsg.set(39, "00");
//			//String str54 = "1002360C000000000034260000";
//			String str54 = "01360000000010000";
//			
//			//replyMsg.set(54, ((str54.length() > 9) ? str54.length() : "0" + str54.length()) + str54);
//			//replyMsg.set(54, "0" + str54.length() + str54);
//			replyMsg.set(54, str54);
//			//replyMsg.setValue(38, "654321", IsoType.ALPHA, 6);
//			//replyMsg.setValue(39, "00", IsoType.ALPHA, 2);
//			//replyMsg.setValue(54, "1002360C000000000034260000", IsoType.LLLVAR, 0);
//			String str61 = 	"100309213943DIDR00000000100000026790691340  Merchant                 " +
//							"100309203943DIDR00000000200000126790691341  Merchant                 " +
//							"100309193943DIDR00000000300000226790691342  Merchant                 " +
//							"100309183943DIDR00000000400000326790691343  Merchant                 " +
//							"100309173943DIDR00000000500000426790691344  Merchant                 " +
//							"100309163943DIDR00000000600000526790691345  Merchant                 " +
//							"100309153943DIDR00000000700000626790691346  Merchant                 " +
//							"100309143943DIDR00000000800000726790691347  Merchant                 " +
//							"100309133943DIDR00000000900000826790691348  Merchant                 " +
//							"100309123943DIDR00000001000000926790691349  Merchant                 ";
//			replyMsg.set(
//			//replyMsg.setValue(
//					61,
//					/*str61.length() + */str61
//					//,IsoType.LLLVAR, 0
//					);			
			
			
			if (replyMsg == null) {
				log.info(muxName+" NO reply for message " + replyMsg);
				// throwing exception here causes retrials for reversals from
				// onException exception policy
				//Condition added to send repeated requests in case of there is no response for payment advice from flashiz
				if (msg.getMTI().equals("0420") || (msg.getMTI().equals("0220") && msg.getString("3").startsWith("50"))) {
					log.info("Message MTI is "+ msg.getMTI() + " , throwing NoISOResponseException so that onException takes over "+muxName);
//					log.info("Reversal retrial count = "+exchange.);
					throw new NoISOResponseException(muxName+" no response for isomsg with RRN=" + msg.getString(37));
				}
			}
			else {
				log.info(muxName+" Got reply for message " + replyMsg);
				//If there is 68 in de-39 field for Flashiz , Have to send repeated requests 
//				if(replyMsg.getMTI().equals("0230") && replyMsg.getString("3").startsWith("50") && replyMsg.getString("39").equals("68")){
//					log.info("Got de-39 "+replyMsg.getString("39")+" Message MTI is "+ replyMsg.getMTI() + " , throwing NoISOResponseException so that onException takes over "+muxName);
//					throw new NoISOResponseException(muxName+" no response for isomsg ");
//				}
				ProducerTemplate template = exchange.getContext().createProducerTemplate();
				template.start();
				Map<String,Object> headersMap = MCEUtil.generateMandatoryHeaders(exchange.getIn().getHeaders());
				template.sendBodyAndHeaders(replyEndpoint, replyMsg,headersMap);
				template.stop();
			}
		}
		catch (NotFoundException e) {
			log.error(muxName+" Could not find the MUX to send message", e);
		}
		catch (ISOException e) {
			log.error(muxName+" Exception in sending ISO message ", e);
		}
	}

	public String getMuxName() {
		return muxName;
	}

	public void setMuxName(String muxName) {
		this.muxName = muxName;
	}

	public Endpoint getReplyEndpoint() {
		return replyEndpoint;
	}

	public void setReplyEndpoint(Endpoint replyEndpoint) {
		this.replyEndpoint = replyEndpoint;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
}
