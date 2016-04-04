package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class ActivationXMLResult extends XMLResult
{
    
	String name;
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public ActivationXMLResult()
    {
	super();
    }
    
    public void render() throws Exception
    {
	writeStartOfDocument();
	
	super.render();
	
	if(getName()!=null) {
		
	    getXmlWriter().writeStartElement("name");
	    getXmlWriter().writeCharacters(getName(),true);
	    getXmlWriter().writeEndElement();
	}
	
	writeEndOfDocument();
	
    }
}
