package com.mfino.mce.core.util;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.jetty.JettyHttpComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.mce.core.conf.HTTPLinkConf;
import com.mfino.mce.core.conf.MCELinkConf;


public class LinkUtil 
{
	private static Log log = LogFactory.getLog(LinkUtil.class);
	
	public static Endpoint getEndPoint(MCELinkConf conf,CamelContext context, Integer minThreads, Integer maxThreads)
	{
		if(conf instanceof HTTPLinkConf)
		{
			HTTPLinkConf httpLinkConf = (HTTPLinkConf) conf;
			JettyHttpComponent jetty = context.getComponent("jetty", JettyHttpComponent.class);
			//if value is -1 then use default values
			if(minThreads!=null && minThreads != -1)
			   jetty.setMinThreads(minThreads);
			
			//if value is -1 use default values
			if(maxThreads!=null && minThreads != -1)
			   jetty.setMaxThreads(maxThreads);
			String host = httpLinkConf.getHost();
			String port = httpLinkConf.getPort();
			try {
				return jetty.createEndpoint("jetty:http://"+host+":"+port+"/"+"?"+"enableJmx=false");
			} catch (Exception error) {
				log.error("Error creating end point: ",error );
				return null;
			}
		}
		return null;
		}
		
	
}
