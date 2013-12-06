package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;

public class ResendOtpXMLResult extends XMLResult {

		public ResendOtpXMLResult() {
			super();
		}

		public void render() throws Exception {

			writeStartOfDocument();
			if(StringUtils.isNotBlank(getOneTimePin())){
				getXmlWriter().writeStartElement("OneTimePin");
				getXmlWriter().writeCharacters(getOneTimePin(), false);
				getXmlWriter().writeEndElement();
				
				if(getMessage()!=null){
					getXmlWriter().writeStartElement("message");
					getXmlWriter().writeCharacters(getMessage(), false);
					getXmlWriter().writeEndElement();
				}
			
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
				
			}
			else{
				super.render();
			}
			writeEndOfDocument();

		}


}
