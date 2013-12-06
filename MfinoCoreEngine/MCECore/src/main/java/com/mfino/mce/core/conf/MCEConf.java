package com.mfino.mce.core.conf;

import java.util.List;

public class MCEConf 
{
	protected List<MCELinkConf> linkList;
	
	public void setLinkList(List<MCELinkConf> linkList)
	{
		this.linkList = linkList;
	}
	
	public List<MCELinkConf> getLinkList()
	{
		return linkList;
	}
}
