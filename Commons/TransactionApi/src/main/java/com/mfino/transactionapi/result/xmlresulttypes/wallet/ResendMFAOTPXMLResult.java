package com.mfino.transactionapi.result.xmlresulttypes.wallet;

import com.mfino.result.XMLResult;

public class ResendMFAOTPXMLResult extends XMLResult
{
	public ResendMFAOTPXMLResult() {
		super();
    }
    
    public void render() throws Exception {
    	
		writeStartOfDocument();
		
		super.render();
		
		writeEndOfDocument();
	
    }
}