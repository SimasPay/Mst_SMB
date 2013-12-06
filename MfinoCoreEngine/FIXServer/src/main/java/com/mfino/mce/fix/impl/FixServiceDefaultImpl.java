package com.mfino.mce.fix.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.fix.FixService;


public class FixServiceDefaultImpl implements FixService 
{
	Log log = LogFactory.getLog(FixServiceDefaultImpl.class);

	@Override
	public MCEMessage processMessage(MCEMessage mesg) 
	{
		log.info("Message in Fix Service"+mesg.getRequest().DumpFields());
		return mesg;
	}

}
