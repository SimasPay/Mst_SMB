package com.mfino.mce.iso.jpos.util;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.util.NameRegistrar.NotFoundException;

/**
 * 
 * @author Sasi
 *
 */
public interface ISOUtil {
	
	public ISOMsg getSignOnMessage() throws ISOException;
	
	public ISOMsg getEchoMessage() throws ISOException;
	
	public ISOMsg getSignOffMessage();
	
	public ISOMsg sendAndReceive(ISOMsg isoMsg,long timeout, String muxName) throws ISOException, NotFoundException;

	public void  sleep(long sleepTime);

	public String getSignOnSuccessResponseCode();
	
	public String getEchoSuccessResponseCode();
	
	public String getSuccessResponseCode();
}
