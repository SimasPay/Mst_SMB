package com.mfino.transactionapi.result.xmlresulttypes.biller;

import com.mfino.fix.CmFinoFIX.CMBillPaymentInquiry;
import com.mfino.result.XMLResult;

public class BillPaymentInquiryXMLResult extends XMLResult
{
	
	private CMBillPaymentInquiry billPaymentInquiry;
    public BillPaymentInquiryXMLResult(CMBillPaymentInquiry billPaymentInquiry)
    {
    	super();
    	this.billPaymentInquiry = billPaymentInquiry;
	
    }


	public void render() throws Exception
    {
	writeStartOfDocument();
	
	buildMessage();
	String message = getXMlelements().get("ResponseString");
	String ptID = getXMlelements().get("parentTxnID");
	String refID = getXMlelements().get("refID");
	String billDetails = getXMlelements().get("billDetails");
	String code = getXMlelements().get("code");
	String amount = getXMlelements().get("amount");

	getXmlWriter().writeStartElement("message");
	getXmlWriter().writeAttribute("code", code,false);
	getXmlWriter().writeCharacters(message,false);
	getXmlWriter().writeEndElement();

	getXmlWriter().writeStartElement("transactionTime");
	getXmlWriter().writeCharacters(formatDate(getTransactionTime()),false);
	getXmlWriter().writeEndElement();

	String success = getXMlelements().get("success");
	if (success.equals("0"))
	{
//	    getXmlWriter().writeStartElement("transactionID");
//	    getXmlWriter().writeCharacters(transferID);
//	    getXmlWriter().writeEndElement();
		getXmlWriter().writeStartElement("customerID");
		getXmlWriter().writeCharacters(billPaymentInquiry.getCustomerID().toString(),false);
		getXmlWriter().writeEndElement();
 
	    getXmlWriter().writeStartElement("parentTxnID");
	    getXmlWriter().writeCharacters(ptID,false);
	    getXmlWriter().writeEndElement();
	    
	    getXmlWriter().writeStartElement("amount");
	 	getXmlWriter().writeCharacters(amount,false);
	    getXmlWriter().writeEndElement();
	    
	    getXmlWriter().writeStartElement("refID");
	    getXmlWriter().writeCharacters(refID,false);
	    getXmlWriter().writeEndElement();
 
	    getXmlWriter().writeStartElement("billDetails");
	    getXmlWriter().writeCharacters(billDetails,false);
	    getXmlWriter().writeEndElement();
	}
	writeEndOfDocument();
	
    }
}