package com.mfino.mce.core.conf;

public abstract class MCELinkConf 
{
	public MCELinkConf(String linkType)
	{
		if(linkType==null)
		{
			linkType="";
		}
		else
		{
			linkType = linkType.toLowerCase();
		}
		
		/**
		 * by default client otherwise server
		 */
		if(linkType.equals("client"))
		{
			setLinkType(MCELinkType.CLIENT);
		}
		else
		{
			setLinkType(MCELinkType.SERVER);
		}
	}
	
	public MCELinkConf(MCELinkType linkType, String desc)
	{
		setLinkType(linkType);
		setDescription(desc);
	}
	
	protected String description; 
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected MCELinkType linkType;

	public MCELinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(MCELinkType linkType) {
		this.linkType = linkType;
	}
}
