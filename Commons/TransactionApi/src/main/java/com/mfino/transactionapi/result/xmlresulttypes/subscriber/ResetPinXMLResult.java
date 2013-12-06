package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class ResetPinXMLResult extends XMLResult
{
    public ResetPinXMLResult()
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
