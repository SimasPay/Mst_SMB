package com.mfino.transactionapi.result.xmlresulttypes.biller;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;

public class BillPaymentTopupReversalXMLResult extends XMLResult
{
    public BillPaymentTopupReversalXMLResult()
    {
	super();
    }

    public void render() throws Exception
    {
    	writeStartOfDocument();
    	
    	buildMessage();
    	// *FindbugsChange*
    	// Previous -- Commented out the line "TransferInquiryXMLResult transferInquiryXMLResult =new TransferInquiryXMLResult();"
    	//TransferInquiryXMLResult transferInquiryXMLResult =new TransferInquiryXMLResult();
    	String message = getXMlelements().get("ResponseString");
    	String transferID = getXMlelements().get("transferID");
    	String code = getXMlelements().get("code");

    	getXmlWriter().writeStartElement("message");
    	getXmlWriter().writeAttribute("code", code,false);
    	getXmlWriter().writeCharacters(message,false);
    	getXmlWriter().writeEndElement();

    	
    	String success = getXMlelements().get("success");
    	if (!StringUtils.isBlank(transferID)&&success.equals("0"))
    	{
//    	    getXmlWriter().writeStartElement("refID");
//    	    getXmlWriter().writeCharacters(refID);
//    	    getXmlWriter().writeEndElement();
     
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