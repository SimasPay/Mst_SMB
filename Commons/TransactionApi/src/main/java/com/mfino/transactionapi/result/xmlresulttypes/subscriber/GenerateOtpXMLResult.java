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
public class GenerateOtpXMLResult extends XMLResult {
	
	public GenerateOtpXMLResult() {
		super();
	}
	
    public void render() throws Exception
    {
	writeStartOfDocument();
	
	super.render();
	if (StringUtils.isNotBlank(getIdNumber())) {
		getXmlWriter().writeStartElement("IDNumber");
		getXmlWriter().writeCharacters(getIdNumber(), false);
		getXmlWriter().writeEndElement();
	}
	
	if (StringUtils.isNotBlank(getFirstName())) {
		getXmlWriter().writeStartElement("FirstName");
		getXmlWriter().writeCharacters(getFirstName(), false);
		getXmlWriter().writeEndElement();
	}
	
	if (StringUtils.isNotBlank(getLastName())) {
		getXmlWriter().writeStartElement("LastName");
		getXmlWriter().writeCharacters(getLastName(), false);
		getXmlWriter().writeEndElement();
	}
	
	if (StringUtils.isNotBlank(getNickName())) {
		getXmlWriter().writeStartElement("NickName");
		getXmlWriter().writeCharacters(getNickName(), false);
		getXmlWriter().writeEndElement();
	}
	
	writeEndOfDocument();
	
    }


}
