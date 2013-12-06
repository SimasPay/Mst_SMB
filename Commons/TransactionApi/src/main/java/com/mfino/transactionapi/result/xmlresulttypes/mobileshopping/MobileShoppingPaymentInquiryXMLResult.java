package com.mfino.transactionapi.result.xmlresulttypes.mobileshopping;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMobileShoppingPaymentInquiry;
import com.mfino.result.XMLResult;

public class MobileShoppingPaymentInquiryXMLResult extends XMLResult
{
	private CMMobileShoppingPaymentInquiry mspInquiry;
    public MobileShoppingPaymentInquiryXMLResult(CMMobileShoppingPaymentInquiry mspInquiry) {
    	super();
    	this.mspInquiry = mspInquiry;
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

		if(!(CmFinoFIX.NotificationCode_InvalidBillerCode.equals(new Integer(code.trim())) || 
				CmFinoFIX.NotificationCode_InvalidSourcePocketCode.equals(new Integer(code.trim())) ||
				CmFinoFIX.NotificationCode_MDNNotFound.equals(new Integer(code.trim())) ||
				CmFinoFIX.NotificationCode_PartnerNotFound.equals(new Integer(code.trim())) ||
				CmFinoFIX.NotificationCode_TransactionRuleNotFound.equals(new Integer(code.trim())))){

			getXmlWriter().writeStartElement("billerCode");
			getXmlWriter().writeCharacters(mspInquiry.getMFSBillerCode(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("amount");
			getXmlWriter().writeCharacters(mspInquiry.getTransactionAmount().toString(),false);
			getXmlWriter().writeEndElement();
			
			getXmlWriter().writeStartElement("serviceCharge");
			getXmlWriter().writeCharacters(getServiceCharge().toString(),false);
			getXmlWriter().writeEndElement();			

			getXmlWriter().writeStartElement("parentTxnID");
			getXmlWriter().writeCharacters(mspInquiry.getTransactionID().toString(),false);
			getXmlWriter().writeEndElement();
		}

		getXmlWriter().writeStartElement("transactionTime");
		getXmlWriter().writeCharacters(formatDate(getTransactionTime()),false);
		getXmlWriter().writeEndElement();
		
		writeEndOfDocument();
    }
}