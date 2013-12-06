package com.mfino.mce.fix.conf;

import org.springframework.beans.factory.annotation.Required;

public class FixLinkConf 
{
	private String host;
	private String port;
	
	//Required is to signify that value is mandatory to be set during configuration
	@Required
	public void setHost(String host) {
		this.host = host;
	}
	
	@Required
	public String getHost() {
		return host;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	public String getPort() {
		return port;
	}
}
