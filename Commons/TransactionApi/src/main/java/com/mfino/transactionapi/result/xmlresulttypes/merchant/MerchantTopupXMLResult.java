package com.mfino.transactionapi.result.xmlresulttypes.merchant;

import com.mfino.result.XMLResult;

public class MerchantTopupXMLResult extends XMLResult
{
    public MerchantTopupXMLResult()
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
	    getXmlWriter().writeCharacters(String.valueOf(getDetailsOfPresentTransaction().getId()),false);
	    getXmlWriter().writeEndElement();

	    getXmlWriter().writeStartElement("lastBalanceAfterTransaction");
	    getXmlWriter().writeCharacters(String.valueOf(getDetailsOfPresentTransaction().getSourcepocketbalance()),false);
	    getXmlWriter().writeEndElement();

	    getXmlWriter().writeStartElement("sourceRefID");
	    getXmlWriter().writeCharacters(String.valueOf(getDetailsOfPresentTransaction().getSourcereferenceid()),false);
	    getXmlWriter().writeEndElement();
	}
	writeEndOfDocument();

    }
}
