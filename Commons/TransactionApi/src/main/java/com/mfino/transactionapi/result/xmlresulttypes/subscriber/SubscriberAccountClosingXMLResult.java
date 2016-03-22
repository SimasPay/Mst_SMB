package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;

public class SubscriberAccountClosingXMLResult extends XMLResult {

	private String	name;
	private String destinationMDN;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the destinationMDN
	 */
	public String getDestinationMDN() {
		return destinationMDN;
	}

	/**
	 * @param destinationMDN the destinationMDN to set
	 */
	public void setDestinationMDN(String destinationMDN) {
		this.destinationMDN = destinationMDN;
	}

	public SubscriberAccountClosingXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();
		super.render();
		
		if(StringUtils.isNotBlank(getName())) {
		
			getXmlWriter().writeStartElement("name");
			getXmlWriter().writeCharacters(getName(),false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getDestinationMDN())) {
			
			getXmlWriter().writeStartElement("destinationMDN");
			getXmlWriter().writeCharacters(getDestinationMDN(),false);
			getXmlWriter().writeEndElement();
		}

		writeEndOfDocument();
	}
}