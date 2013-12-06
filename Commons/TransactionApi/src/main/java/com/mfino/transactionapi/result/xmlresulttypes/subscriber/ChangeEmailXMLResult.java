package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class ChangeEmailXMLResult extends XMLResult {
	public ChangeEmailXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		super.render();

		writeEndOfDocument();
	}
}
