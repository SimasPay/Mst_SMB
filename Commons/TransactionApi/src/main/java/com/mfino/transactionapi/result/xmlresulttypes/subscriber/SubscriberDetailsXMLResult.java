package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class SubscriberDetailsXMLResult extends XMLResult {

	private String	SourceMDN;

	public SubscriberDetailsXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();
		super.render();
		if (getZteDataPush() != null) {
			getXmlWriter().writeStartElement("subscriberDetails");
			
			getXmlWriter().writeStartElement("mdn");
			getXmlWriter().writeCharacters(getZteDataPush().getMsisdn(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("firstName");
			getXmlWriter().writeCharacters(getZteDataPush().getFirstName(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("lastName");
			getXmlWriter().writeCharacters(getZteDataPush().getLastName(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("email");
			getXmlWriter().writeCharacters(getZteDataPush().getEmail(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("language");
			getXmlWriter().writeCharacters(getZteDataPush().getLanguage().toString(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("currency");
			getXmlWriter().writeCharacters(getZteDataPush().getCurrency(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("paidFlag");
			getXmlWriter().writeCharacters(getZteDataPush().getPaidFlag(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("birthDate");
			getXmlWriter().writeCharacters(getZteDataPush().getBirthDate().toString(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("idType");
			getXmlWriter().writeCharacters(getZteDataPush().getIDType(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("idNumber");
			getXmlWriter().writeCharacters(getZteDataPush().getIDNumber(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("gender");
			getXmlWriter().writeCharacters(getZteDataPush().getGender(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("address");
			getXmlWriter().writeCharacters(getZteDataPush().getAddress(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("city");
			getXmlWriter().writeCharacters(getZteDataPush().getCity(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("birthPlace");
			getXmlWriter().writeCharacters(getZteDataPush().getBirthPlace(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("imsi");
			getXmlWriter().writeCharacters(getZteDataPush().getIMSI(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("marketingCatg");
			getXmlWriter().writeCharacters(getZteDataPush().getMarketingCatg(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("product");
			getXmlWriter().writeCharacters(getZteDataPush().getProduct(),false);
			getXmlWriter().writeEndElement();

			getXmlWriter().writeEndElement();
		}
		writeEndOfDocument();
	}

	public void setSourceMDN(String sourceMDN) {
		SourceMDN = sourceMDN;
	}

	public String getSourceMDN() {
		return SourceMDN;
	}
}
