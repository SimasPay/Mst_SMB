/**
 * 
 */
package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;

/**
 * @author Sreenath
 *
 */
public class KYCUpgradeInquiryXMLResult extends XMLResult {
	
	public KYCUpgradeInquiryXMLResult() {
		super();
	}
	
	public void render() throws Exception {
		writeStartOfDocument();

		super.render();
		
		if (getResponseStatus() != null) {
			getXmlWriter().writeStartElement("responseCode");
			getXmlWriter().writeCharacters(getResponseStatus(),true);
			getXmlWriter().writeEndElement();
		}
		writeEndOfDocument();
	}

}
