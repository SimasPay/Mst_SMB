package com.mfino.mce.core.routing;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.dialect.FrontBaseDialect;

import com.mfino.fix.CFIXMsg;
import com.mfino.mce.core.MCEMessage;

public class ClassBasedDynamicRouter 
{
	Log log = LogFactory.getLog(ClassBasedDynamicRouter.class);
	Map<String, String> classToQueueMapping;
	String defaultQueue;
	
	public String getDefaultQueue() {
		return defaultQueue;
	}

	public void setDefaultQueue(String defaultQueue) {
		this.defaultQueue = defaultQueue;
	}

	ClassBasedDynamicRouter(Map<String,String> classToQueueMapping)
	{
		this.classToQueueMapping = classToQueueMapping;
	}
	
	public String findDestination(MCEMessage body,
			@Header(Exchange.SLIP_ENDPOINT) String previous, @Header("delayedReply") String delayed) 
	{
			
			if((null != body) && (StringUtils.isNotEmpty(body.getDestinationQueue()))){
				return body.getDestinationQueue();
			}
			
			return findNextDestination(body.getResponse()!=null?body.getResponse():body.getRequest(), previous,delayed);
	}
	
	public String findDestination(CFIXMsg body,
			@Header(Exchange.SLIP_ENDPOINT) String previous, @Header("delayedReply") String delayed)
	{
			return findNextDestination(body, previous,delayed);
	}
	
	public String findNextDestination(CFIXMsg fixMesg,String previous, String delayed)
	{
		log.info("ClassBasedDynamicRouter :: findNextDestination :: fixMesg="+fixMesg + ", previous="+previous);
		if(previous!=null)
			return null;
		log.info("finding the next destination");
		String className = fixMesg.getClass().getName();
		log.info("got the classname "+className);
		log.info("got the route: "+ classToQueueMapping.get(className));
		String dest = classToQueueMapping.get(className);
		
		/**
		 * use the default value
		 */
		if(dest==null)
			return defaultQueue;
		return dest;
	}
	
	
}
