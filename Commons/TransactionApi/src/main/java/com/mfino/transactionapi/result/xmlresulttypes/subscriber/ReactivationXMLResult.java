package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class ReactivationXMLResult extends XMLResult{
	
	public ReactivationXMLResult() {
		super();
	}
	
	public void render() throws Exception{
		writeStartOfDocument();

		super.render();

		writeEndOfDocument();
	
    }
}
