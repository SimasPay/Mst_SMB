package com.mfino.mce.core.conf;

public class TCPLinkConf extends MCELinkConf
{
	private String host;
	private String port;
	public TCPLinkConf(MCELinkType linkType, String desc) 
	{
		super(linkType, desc);
	}
	public TCPLinkConf(String linkType)
	{
		super(linkType);
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
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
