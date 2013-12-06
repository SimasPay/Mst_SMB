package com.mfino.transactionapi.result.xmlresulttypes.wallet;

import com.mfino.result.XMLResult;

public class WalletConfirmXMLResult extends XMLResult {
	public WalletConfirmXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		buildMessage();
		String message = "";
		String success = "0";
		if (getMultixResponse() != null) {
			message = getXMlelements().get("ResponseString");
			success = getXMlelements().get("success");
		}
		else {
			message = getXMlelements().get("message");
		}
		Long transferID = getTransactionID();
		if (transferID == null) {
			if (transferID == null) {
				try {
					transferID = Long.valueOf(getXMlelements().get("transferID"));
				}
				catch (Exception error) {
					log.error("No transation id in message", error);
				}
			}
		}
//		String ptID = getXMlelements().get("parentTxnID");
		String code = getXMlelements().get("code");

		getXmlWriter().writeStartElement("message");
		getXmlWriter().writeAttribute("code", code, false);
		getXmlWriter().writeCharacters(message, false);
		getXmlWriter().writeEndElement();

		getXmlWriter().writeStartElement("transactionTime");
		getXmlWriter().writeCharacters(formatDate(getTransactionTime()), false);
		getXmlWriter().writeEndElement();

		if (transferID != null) {
			getXmlWriter().writeStartElement("transferID");
			getXmlWriter().writeCharacters(transferID.toString(), true);
			getXmlWriter().writeEndElement();
		}
		
		if(getSctlID()!=null){
			getXmlWriter().writeStartElement("sctlID");
			getXmlWriter().writeCharacters(getSctlID().toString(),true);
			getXmlWriter().writeEndElement();
		}
		writeEndOfDocument();
	}
	
}
