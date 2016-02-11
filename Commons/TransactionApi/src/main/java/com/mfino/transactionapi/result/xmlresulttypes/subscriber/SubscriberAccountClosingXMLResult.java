package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;

public class SubscriberAccountClosingXMLResult extends XMLResult {

	private String	name;
	
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

		writeEndOfDocument();
	}
}