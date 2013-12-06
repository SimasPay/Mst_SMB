/**
 * 
 */
package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

/**
 * @author Sreenath
 *
 */
public class ValidateOtpXMLResult extends XMLResult {
	
	public ValidateOtpXMLResult() {
		super();
	}
	
    public void render() throws Exception
    {
		writeStartOfDocument();
		
		super.render();
		getXmlWriter().writeStartElement("IsUnregistered");
		String isUnregistered = isUnRegistered()==true? "true" : "false";
		getXmlWriter().writeCharacters(isUnregistered, false);
		getXmlWriter().writeEndElement();
	
		writeEndOfDocument();
	
    }

}
