package com.mfino.transactionapi.result.xmlresulttypes.mobileshopping;

import com.mfino.fix.CmFinoFIX.CMMobileShoppingPayment;
import com.mfino.result.XMLResult;

public class MobileShoppingPaymentXMLResult extends XMLResult
{
	private CMMobileShoppingPayment msp;
    public MobileShoppingPaymentXMLResult(CMMobileShoppingPayment msp) {
    	super();
    	this.msp = msp;
    }

    public void render() throws Exception {
		writeStartOfDocument();
		
		buildMessage();
		String message = getXMlelements().get("message");
		String code = getXMlelements().get("code");
			
		getXmlWriter().writeStartElement("message");
		getXmlWriter().writeAttribute("code", code,false);
		getXmlWriter().writeCharacters(message,false);
		getXmlWriter().writeEndElement();

		getXmlWriter().writeStartElement("transactionTime");
		getXmlWriter().writeCharacters(formatDate(getTransactionTime()),false);
		getXmlWriter().writeEndElement();
		
		writeEndOfDocument();
    }
}