package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class KYCUpgradeXMLResult extends XMLResult
{

	public KYCUpgradeXMLResult()
    {
	super();
    }
    
    public void render() throws Exception
    {
	writeStartOfDocument();
	
	super.render();
	
	
	
	if (getTransID() != null) {
		getXmlWriter().writeStartElement("transID");
		getXmlWriter().writeCharacters(getTransID(),true);
		getXmlWriter().writeEndElement();
	}
	

	
	writeEndOfDocument();
    }

}
