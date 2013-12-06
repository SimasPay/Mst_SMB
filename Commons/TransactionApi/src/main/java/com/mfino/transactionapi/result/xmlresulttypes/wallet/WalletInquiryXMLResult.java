package com.mfino.transactionapi.result.xmlresulttypes.wallet;

import java.text.NumberFormat;

import com.mfino.constants.XMLResultConstants;
import com.mfino.result.XMLResult;
import com.mfino.util.MfinoUtil;

public class WalletInquiryXMLResult extends XMLResult {
	public WalletInquiryXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		buildMessage();
		String message ="";
		String success ="0";
		if (getMultixResponse() != null) {
			message = getXMlelements().get("ResponseString");
			success = getXMlelements().get("success");
		}else{
			message = getXMlelements().get("message");
		}
		Long transferID = getTransactionID();
		Long parentTransactionID = getParentTransactionID();
		if(transferID==null){
			try{
				transferID = Long.valueOf(getXMlelements().get("transferID"));
			}catch (Exception error) {
				log.error("No transation id in message", error);
			}
		}
		String ptID = getXMlelements().get("parentTxnID");
		String code = getXMlelements().get("code");
		NumberFormat numberFormat = MfinoUtil.getNumberFormat();

		getXmlWriter().writeStartElement("message");
		getXmlWriter().writeAttribute("code", code,false);
		getXmlWriter().writeCharacters(message,false);
		getXmlWriter().writeEndElement();

		getXmlWriter().writeStartElement("transactionTime");
		getXmlWriter().writeCharacters(formatDate(getTransactionTime()),false);
		getXmlWriter().writeEndElement();

		if (getDebitAmount() != null) {
			getXmlWriter().writeStartElement(XMLResultConstants.DEBIT_AMOUNT);
			getXmlWriter().writeCharacters(numberFormat.format(getDebitAmount()),true);
			getXmlWriter().writeEndElement();
		}

		if (getCreditAmount() != null) {
			getXmlWriter().writeStartElement(XMLResultConstants.CREDIT_AMOUNT);
			getXmlWriter().writeCharacters(numberFormat.format(getCreditAmount()),true);
			getXmlWriter().writeEndElement();
		}
		
		if (getServiceCharge() != null) {
			getXmlWriter().writeStartElement(XMLResultConstants.CHARGES);
			getXmlWriter().writeCharacters(numberFormat.format(getServiceCharge()),true);
			getXmlWriter().writeEndElement();
		}
		
		if(parentTransactionID!=null){
			getXmlWriter().writeStartElement(XMLResultConstants.PARENTTRXNID);
			getXmlWriter().writeCharacters(parentTransactionID.toString(),true);
			getXmlWriter().writeEndElement();
		}
		if (transferID!=null) {
			getXmlWriter().writeStartElement(XMLResultConstants.TRANSFERID);
			getXmlWriter().writeCharacters(transferID.toString(),true);
			getXmlWriter().writeEndElement();
		}

		//		}
		//		else
		//		{
		//			super.render();
		//		}
		writeEndOfDocument();
	}
}
