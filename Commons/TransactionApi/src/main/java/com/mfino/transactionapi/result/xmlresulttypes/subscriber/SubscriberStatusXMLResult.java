package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class SubscriberStatusXMLResult extends XMLResult {

	private String	sourceMDN = "";

	private String status;
	
	public SubscriberStatusXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();
		//super.render();

		getXmlWriter().writeStartElement("subscriberDetails");

		getXmlWriter().writeStartElement("mdn");
		getXmlWriter().writeCharacters(sourceMDN,false);
		getXmlWriter().writeEndElement();

		getXmlWriter().writeStartElement("firstName");
		getXmlWriter().writeCharacters(getFirstName(),false);
		getXmlWriter().writeEndElement();

		getXmlWriter().writeStartElement("lastName");
		getXmlWriter().writeCharacters(getLastName(),false);
		getXmlWriter().writeEndElement();
		
		getXmlWriter().writeStartElement("status");
		getXmlWriter().writeCharacters(getStatus(),false);
		getXmlWriter().writeEndElement();
		
		getXmlWriter().writeEndElement();

		writeEndOfDocument();
	}

	public void setSourceMDN(String sourceMDN) {
		this.sourceMDN = sourceMDN;
	}

	public String getSourceMDN() {
		return sourceMDN;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	

}
