package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class ShareLoadXMLResult extends XMLResult
{
    public ShareLoadXMLResult()
    {
	super();
    }

    public void render() throws Exception
    {
	writeStartOfDocument();

	super.render();

	if (getDetailsOfPresentTransaction() != null)
	{
	    getXmlWriter().writeStartElement("refID");
	    getXmlWriter().writeCharacters(String.valueOf(getDetailsOfPresentTransaction().getID()),true);
	    getXmlWriter().writeEndElement();
	}
	
	writeEndOfDocument();
    }
}
