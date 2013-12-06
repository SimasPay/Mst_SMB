package com.mfino.transactionapi.result.xmlresulttypes.biller;

import com.mfino.result.XMLResult;

public class BillPaymentTopupXMLResult extends XMLResult
{
    public BillPaymentTopupXMLResult()
    {
	super();
    }

    public void render() throws Exception
    {
    	writeStartOfDocument();
    	
    	buildMessage();
    	String message ="";
    	String transferID=null;
    	if(getMultixResponse()!=null){
    	message = getXMlelements().get("ResponseString");
    	transferID = getXMlelements().get("transferID");
    	}
    	else{
    		message=getXMlelements().get("message");
    	}
    	String code = getXMlelements().get("code");
    	getXmlWriter().writeStartElement("message");
    	getXmlWriter().writeAttribute("code", code,false);
    	getXmlWriter().writeCharacters(message,false);
    	getXmlWriter().writeEndElement();
    	
    	String success = getXMlelements().get("success");
    	if (success!=null && success.equals("0"))
    	{     
    	    getXmlWriter().writeStartElement("transferID");
    	    getXmlWriter().writeCharacters(transferID,false);
    	    getXmlWriter().writeEndElement();
    	}
    	
    	getXmlWriter().writeStartElement("transactionTime");
    	getXmlWriter().writeCharacters(formatDate(getTransactionTime()),false);
    	getXmlWriter().writeEndElement();
    	writeEndOfDocument();
    	
        }
}