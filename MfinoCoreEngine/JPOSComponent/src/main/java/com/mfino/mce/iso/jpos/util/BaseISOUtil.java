package com.mfino.mce.iso.jpos.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.q2.iso.QMUX;
import org.jpos.util.NameRegistrar.NotFoundException;

/**
 * @author Sasi
 *
 */
public abstract class BaseISOUtil {
	
	public Log log = LogFactory.getLog(this.getClass());
	
	public ISOMsg sendAndReceive(ISOMsg isoMsg,long timeout, String muxName) throws ISOException, NotFoundException 
	{
		log.info("sending msg with mti="+isoMsg.getMTI()+",STAN="+isoMsg.getValue(11)+" muxName="+muxName+", isoMsg="+isoMsg);
		QMUX mux = (QMUX) QMUX.getMUX(muxName);
		ISOMsg replyMsg = mux.request(isoMsg,timeout);

		log.info("response received for msg with mti="+isoMsg.getMTI()+",STAN="+isoMsg.getValue(11)+" muxName="+muxName+", replyMsg="+replyMsg);
		return replyMsg;
	}
	
	public void sleep(long sleepTime)
	{

		try 
		{
			Thread.sleep(sleepTime);
		} 
		catch (InterruptedException e) 
		{
			return;
		}
	}
	
	public String getSignOnSuccessResponseCode() {
		return "00";
	}
	
	public String getEchoSuccessResponseCode() {
		return "00";
	}
	
	public String getSuccessResponseCode() {
		return "00";
	}
}
