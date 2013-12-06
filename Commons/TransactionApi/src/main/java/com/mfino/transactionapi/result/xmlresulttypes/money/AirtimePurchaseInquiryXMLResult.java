package com.mfino.transactionapi.result.xmlresulttypes.money;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;

/**
 * @author Sasi
 *
 */
public class AirtimePurchaseInquiryXMLResult extends XMLResult {
	
	@Override
	public void render() throws Exception {
		writeStartOfDocument();

		buildMessage();

		if (getMultixResponse() != null) {
			String message = getXMlelements().get("ResponseString");
			String transferID = getXMlelements().get("transferID");
			String ptID = getXMlelements().get("parentTxnID");
			String code = getXMlelements().get("code");

			getXmlWriter().writeStartElement("message");
			getXmlWriter().writeAttribute("code", code,false);
			getXmlWriter().writeCharacters(message,false);
			getXmlWriter().writeEndElement();

			getXmlWriter().writeStartElement("transactionTime");
			getXmlWriter().writeCharacters(formatDate(getTransactionTime()),false);
			getXmlWriter().writeEndElement();

			String success = getXMlelements().get("success");
			if (!StringUtils.isBlank(transferID) && success.equals("0")) {
				getXmlWriter().writeStartElement("transferID");
				getXmlWriter().writeCharacters(transferID,true);
				getXmlWriter().writeEndElement();

				getXmlWriter().writeStartElement("parentTxnID");
				getXmlWriter().writeCharacters(ptID,true);
				getXmlWriter().writeEndElement();
			}
			
			if(getSctlID()!=null){
				getXmlWriter().writeStartElement("sctlID");
				getXmlWriter().writeCharacters(getSctlID().toString(),true);
				getXmlWriter().writeEndElement();
		}
		}
		else
		{
			super.render();
		}
		writeEndOfDocument();
	}
	
	@Override
	protected String getTransferIDValue(String message) {
		return super.getTransferIDValue(message);
	}
}
