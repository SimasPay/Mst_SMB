package com.mfino.transactionapi.result.xmlresulttypes.money;

import com.mfino.result.XMLResult;

public class BankTopupXMLResult extends XMLResult {
	public BankTopupXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		super.render();

		if (getDetailsOfPresentTransaction() != null) {
			getXmlWriter().writeStartElement("refID");
			getXmlWriter().writeCharacters(String.valueOf(getDetailsOfPresentTransaction().getId()),true);
			getXmlWriter().writeEndElement();
		}

		writeEndOfDocument();
	}
}
