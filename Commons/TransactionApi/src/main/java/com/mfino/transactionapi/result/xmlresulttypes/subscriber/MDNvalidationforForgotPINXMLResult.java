package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

public class MDNvalidationforForgotPINXMLResult extends XMLResult{
	
String message;
	
	
	/**
	 * @return the name
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param name the name to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public MDNvalidationforForgotPINXMLResult()
    {
	super();
    }
    
    public void render() throws Exception
    {
	writeStartOfDocument();
	
	super.render();
	
	if(getMessage()!=null) {
		
	    getXmlWriter().writeStartElement("message");
	    getXmlWriter().writeCharacters(getMessage(),true);
	    getXmlWriter().writeEndElement();
	}
	
	writeEndOfDocument();
	
    }

}
