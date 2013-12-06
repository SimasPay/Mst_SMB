package com.mfino.mce.core.conf;

public class HTTPLinkConf extends TCPLinkConf 
{
	public HTTPLinkConf(MCELinkType linkType, String desc) 
	{
		super(linkType, desc);
	}
	public HTTPLinkConf(String linkType)
	{
		super(linkType);
	}
}
