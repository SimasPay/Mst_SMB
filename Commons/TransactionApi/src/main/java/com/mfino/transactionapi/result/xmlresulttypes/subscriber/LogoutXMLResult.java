package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class LogoutXMLResult extends XMLResult {

	public LogoutXMLResult() {
		super();    	
    }

	public void render() throws Exception {
		writeStartOfDocument();

		super.render();

		writeEndOfDocument();

	}
}
