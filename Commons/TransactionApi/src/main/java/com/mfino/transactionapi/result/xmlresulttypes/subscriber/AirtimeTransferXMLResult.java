package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class AirtimeTransferXMLResult extends XMLResult
{
    public AirtimeTransferXMLResult()
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

	    getXmlWriter().writeStartElement("lastBalanceAfterTransaction");
	    getXmlWriter().writeCharacters(String.valueOf(getDetailsOfPresentTransaction().getSourcePocketBalance()),true);
	    getXmlWriter().writeEndElement();
	}
	writeEndOfDocument();
    }
}