package com.mfino.mce.backend;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.mce.core.MCEMessage;

public class BackendDynamicRouter 
{	
	Log log = LogFactory.getLog(BackendDynamicRouter.class);
	Map<String, String> classToQueueMapping;
	

	BackendDynamicRouter(Map<String,String> classToQueueMapping )
	{
		this.classToQueueMapping = classToQueueMapping;
	}
	
	public String findDestination(MCEMessage body,
			@Header(Exchange.SLIP_ENDPOINT) String previous,
			@Header("backendRoutingStart") String backendRoutingStart) 
	{
			return findNextDestination(body, previous);
	}
	
	public String findNextDestination(MCEMessage mesg,String previous)
	{
		if(previous!=null)
			return null;
		log.info(" trying to root message");
		CFIXMsg fixMesg = mesg.getResponse();
		String className = fixMesg.getClass().getName();
//		System.out.println("got the classname "+className);
		log.info("got the route: "+ classToQueueMapping.get(className));
		return classToQueueMapping.get(className);
	}
}
