package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class ChangeSettingsXMLResult extends XMLResult {
	public ChangeSettingsXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		super.render();

		writeEndOfDocument();
	}

}
