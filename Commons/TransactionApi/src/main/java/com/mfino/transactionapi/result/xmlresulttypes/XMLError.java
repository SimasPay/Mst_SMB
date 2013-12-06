package com.mfino.transactionapi.result.xmlresulttypes;

import com.mfino.result.XMLResult;

public class XMLError extends XMLResult {
	public XMLError() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		super.render();

		writeEndOfDocument();
	}

}
