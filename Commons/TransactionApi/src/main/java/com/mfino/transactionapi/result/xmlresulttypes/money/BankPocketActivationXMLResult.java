package com.mfino.transactionapi.result.xmlresulttypes.money;

import com.mfino.result.XMLResult;

public class BankPocketActivationXMLResult extends XMLResult
{
    public BankPocketActivationXMLResult()
    {
	super();
    }
    public void render() throws Exception
    {
	writeStartOfDocument();
	super.render();
	writeEndOfDocument();
    }
}
