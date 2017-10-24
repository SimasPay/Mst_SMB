package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class MigrateTokenXMLResult extends XMLResult {
	public MigrateTokenXMLResult() {
		super();
	}

	private String migrateToken;

	public void render() throws Exception {

		writeStartOfDocument();

		super.render();

		getXmlWriter().writeStartElement("migrateToken");
		getXmlWriter().writeCharacters(migrateToken, false);
		getXmlWriter().writeEndElement();

		writeEndOfDocument();

	}

	public String getMigrateToken() {
		return migrateToken;
	}

	public void setMigrateToken(String migrateToken) {
		this.migrateToken = migrateToken;
	}
}
