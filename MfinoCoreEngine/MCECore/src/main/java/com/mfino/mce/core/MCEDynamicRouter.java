package com.mfino.mce.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CmFinoFIX.CMBase;

public class MCEDynamicRouter 
{
	Log log = LogFactory.getLog(MCEDynamicRouter.class);
	public String findDestQueue(Object mesg)
	{
		if(mesg instanceof CMBase)
		{
			CMBase fixMesg = (CMBase)mesg;
			int mesgId = fixMesg.m_pHeader.getMsgType();
			log.info("Finding route for message id "+mesgId);
			//int processId = conf.getTargetProcessId(mesgId);
			String destQueue = "test";//processMap.get(processId).getInQueueName();
			log.info("Next Destination queue "+destQueue);
			return "jms:queue:"+destQueue;
		}
		
		return null;
	}
}
