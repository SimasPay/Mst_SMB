package com.mfino.transactionapi.result.xmlresulttypes.money;

import com.mfino.result.XMLResult;

public class BankPocketRetirementXMLResult extends XMLResult {
	public void render() throws Exception {
		writeStartOfDocument();
		super.render();
		writeEndOfDocument();
	}
}
