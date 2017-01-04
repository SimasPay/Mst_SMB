/**
 * 
 */
package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

/**
 * @author Sreenath
 *
 */
public class PublicKeyXMLResult extends XMLResult {

	public PublicKeyXMLResult(){
		super();
	}
	
	public void render() throws Exception {

		writeStartOfDocument();
		
		if(getPublicKeyModulus()!=null && getPublicKeyExponent()!=null){
			getXmlWriter().writeStartElement("PublicKeyModulus");
			getXmlWriter().writeCharacters(getPublicKeyModulus(), false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("PublicKeyExponent");
			getXmlWriter().writeCharacters(getPublicKeyExponent(), false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("Success");
			getXmlWriter().writeCharacters("true", false);
			getXmlWriter().writeEndElement();
		}
		else{
			getXmlWriter().writeStartElement("Success");
			getXmlWriter().writeCharacters("false", false);
			getXmlWriter().writeEndElement();
		}
		
		super.render();
		
		getXmlWriter().writeStartElement("AppURL");
		getXmlWriter().writeCharacters(getAdditionalInfo(), false);
		getXmlWriter().writeEndElement();
		
		writeEndOfDocument();

	}

}
