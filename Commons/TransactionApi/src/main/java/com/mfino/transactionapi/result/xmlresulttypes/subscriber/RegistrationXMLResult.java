package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class RegistrationXMLResult extends XMLResult
{
    public RegistrationXMLResult()
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
