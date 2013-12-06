package com.mfino.mce.iso.jpos;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;



public class Echo extends RepeatedMessageSender
{
	//TODO: lack of time not creating a perfect implementation using the base clas
	// also make ChannelObserver implement from the same base class
	Log log = LogFactory.getLog(Echo.class);
	
	public Echo()
	{
		super();
	}

	@Override
	protected ISOMsg getISOMsgToSend() throws ISOException {
		return isoUtil.getEchoMessage();
	}

	@Override
	protected String getNoReplyMessage() {
		return null;
	}
	
	protected String getReplyReceivedMessage()
	{
		return null;
	}

}
