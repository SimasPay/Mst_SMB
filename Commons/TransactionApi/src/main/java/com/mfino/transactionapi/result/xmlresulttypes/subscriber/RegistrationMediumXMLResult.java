package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;

public class RegistrationMediumXMLResult extends XMLResult {
	public RegistrationMediumXMLResult() {
		super();
	}

	public void render() throws Exception {

		writeStartOfDocument();
		if(StringUtils.isNotBlank(getRegistrationMedium())){
			getXmlWriter().writeStartElement("RegistrationMedium");
			getXmlWriter().writeCharacters(getRegistrationMedium(), false);
			getXmlWriter().writeEndElement();
		
			if (getDetailsOfPresentTransaction() != null) {
				getXmlWriter().writeStartElement("transactionTime");
				getXmlWriter().writeCharacters(formatDate(getDetailsOfPresentTransaction().getStartTime()), false);
				getXmlWriter().writeEndElement();
			}
			else {
				getXmlWriter().writeStartElement("transactionTime");
				getXmlWriter().writeCharacters(formatDate(getTransactionTime()), false);
				getXmlWriter().writeEndElement();
			}
			
			if(getMessage()!=null){
				getXmlWriter().writeStartElement("message");
				getXmlWriter().writeCharacters(getMessage(), false);
				getXmlWriter().writeEndElement();
			}
			
			if(isResetPinRequested()!=null)
			{
			getXmlWriter().writeStartElement("ResetPinRequested");
			getXmlWriter().writeCharacters(isResetPinRequested().toString(), false);
			getXmlWriter().writeEndElement();
			}
			if(getStatus()!=null)
			{
			getXmlWriter().writeStartElement("Status");
			getXmlWriter().writeCharacters(getStatus(), false);
			getXmlWriter().writeEndElement();
			}
			if(getIsAlreadyActivated()!=null){
				getXmlWriter().writeStartElement("IsAlreadyActivated");
				getXmlWriter().writeCharacters(getIsAlreadyActivated().toString(), false);
				getXmlWriter().writeEndElement();
			}
		}
		else{
			super.render();
		}
		writeEndOfDocument();

	}
}
