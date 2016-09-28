package com.mfino.transactionapi.result.xmlresulttypes.biller;

import java.util.List;

import com.mfino.domain.Denomination;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPaymentTopUpInquiry;
import com.mfino.result.XMLResult;

public class BillPaymentTopupInquiryXMLResult extends XMLResult
{
	private CMBillPaymentTopUpInquiry billPaymentTopUpInquiry;
    public BillPaymentTopupInquiryXMLResult(CMBillPaymentTopUpInquiry billPaymentTopupInquiry)
    {
    	super();
    	this.billPaymentTopUpInquiry = billPaymentTopupInquiry;
	
    }

    public void render() throws Exception
    {
	writeStartOfDocument();
	
	buildMessage();
	String message = getXMlelements().get("message");
	String code = getXMlelements().get("code");
		
	getXmlWriter().writeStartElement("message");
	getXmlWriter().writeAttribute("code", code,false);
	getXmlWriter().writeCharacters(message,false);
	getXmlWriter().writeEndElement();
	
	if(!CmFinoFIX.NotificationCode_InvalidBillerName.equals(new Integer(code.trim()))){
	getXmlWriter().writeStartElement("billerName");
	getXmlWriter().writeCharacters(billPaymentTopUpInquiry.getBillerName(),false);
	getXmlWriter().writeEndElement();
	
	getXmlWriter().writeStartElement("customerID");
	getXmlWriter().writeCharacters(billPaymentTopUpInquiry.getCustomerID().toString(),false);
	getXmlWriter().writeEndElement();
	
 if(code.equals(CmFinoFIX.NotificationCode_BillPaymentTopupInquiryDetails.toString())){
	    	    
	    getXmlWriter().writeStartElement("amount");
	 	getXmlWriter().writeCharacters(billPaymentTopUpInquiry.getAmount().toString(),false);
	    getXmlWriter().writeEndElement();
	    
	    getXmlWriter().writeStartElement("parentTxnID");
	 	getXmlWriter().writeCharacters(billPaymentTopUpInquiry.getTransactionID().toString(),false);
	    getXmlWriter().writeEndElement();
 }
 else if(code.equals(CmFinoFIX.NotificationCode_BillPaymentTopupInvalidDenomination.toString())){
	 
	 getXmlWriter().writeStartElement("amount");
	 	getXmlWriter().writeCharacters(billPaymentTopUpInquiry.getAmount().toString(),false);
	    getXmlWriter().writeEndElement();
	 
	 List<Denomination> denominations = getDenominations();
	 getXmlWriter().writeStartElement("denominations");
		if(denominations != null && denominations.size()>0){
			for(Denomination denomination:denominations ){
				getXmlWriter().writeStartElement("denominationAmount");
				getXmlWriter().writeCharacters(String.valueOf(denomination.getDenominationamount()),false);
				getXmlWriter().writeEndElement();
				}
			}
		else {
			getXmlWriter().writeCharacters("No Denominations found",false);
		}
		getXmlWriter().writeEndElement();
 }
	}
	getXmlWriter().writeStartElement("transactionTime");
	getXmlWriter().writeCharacters(formatDate(getTransactionTime()),false);
	getXmlWriter().writeEndElement();
	
	writeEndOfDocument();
	
    }

	}