package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class ResetPinByOTPXMLResult extends XMLResult
{
    public ResetPinByOTPXMLResult()
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
