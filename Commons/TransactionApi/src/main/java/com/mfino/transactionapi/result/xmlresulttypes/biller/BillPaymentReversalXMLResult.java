package com.mfino.transactionapi.result.xmlresulttypes.biller;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;

public class BillPaymentReversalXMLResult extends XMLResult
{
    public BillPaymentReversalXMLResult()
    {
	super();
    }

    public void render() throws Exception
    {
    	writeStartOfDocument();
    	
    	buildMessage();
    	TransferInquiryXMLResult transferInquiryXMLResult =new TransferInquiryXMLResult();
    	String message = getXMlelements().get("ResponseString");
    	String transferID = transferInquiryXMLResult.getTransferIDValue(message);
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