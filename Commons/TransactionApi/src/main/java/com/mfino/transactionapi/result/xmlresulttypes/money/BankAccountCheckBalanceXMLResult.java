package com.mfino.transactionapi.result.xmlresulttypes.money;

import java.text.NumberFormat;

import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.result.XMLResult;
import com.mfino.util.MfinoUtil;

public class BankAccountCheckBalanceXMLResult extends XMLResult {
	public BankAccountCheckBalanceXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();
		// super.render();
		// response from multix is of the following format
		// 0<htmlbody><span>(28) </span><table>(4) Your Smart Dompet Balance on
		// 24/05/11 17:25 is IDR 3,426.
		// </table></htmlbody>
		// this code may not work if the notificationtext contains something
		// other than
		// the valid notification code inside the ( ) paranthesis.
		if (getMultixResponse() == null) {
			super.render();
			writeEndOfDocument();
			return;
		}
		else {
			try {
				CMJSError response = (CMJSError) getMultixResponse();
				String message = response.getErrorDescription();
				String code = null;
				message = message.trim();

				getXMlelements().put("success", response.getErrorCode().toString());
				if(response.getCode() != null)
				{
					code = response.getCode().toString();
				}
				else 
				{
					code = "0";
				}
				
				getXMlelements().put("code", code);
				getXMlelements().put("message", message);
				
				getXmlWriter().writeStartElement("message");
				getXmlWriter().writeAttribute("code", code,false);
				getXmlWriter().writeCharacters(message,false);
				getXmlWriter().writeEndElement();
				getXmlWriter().writeStartElement("transactionTime");
				getXmlWriter().writeCharacters(formatDate(getTransactionTime()),false);
				getXmlWriter().writeEndElement();
				
				if(getSctlID()!=null){
					getXmlWriter().writeStartElement("sctlID");
					getXmlWriter().writeCharacters(getSctlID().toString(),true);
					getXmlWriter().writeEndElement();
				}
				if (getAmount() != null) {
					getXmlWriter().writeStartElement("amount");
					NumberFormat numberFormat = MfinoUtil.getNumberFormat();
					getXmlWriter().writeCharacters(numberFormat.format(getAmount()),true);
					getXmlWriter().writeEndElement();
				}
			}
			catch (Exception ex) {
				log.error("Error while rendering BankAccountCheckBalance result xml: ", ex);
				super.render();
			}
		}
		
		if (getTransID() != null) {
			getXmlWriter().writeStartElement("transID");
			getXmlWriter().writeCharacters(getTransID(),true);
			getXmlWriter().writeEndElement();
		}
		
		if (getResponseStatus() != null) {
			getXmlWriter().writeStartElement("responseCode");
			getXmlWriter().writeCharacters(getResponseStatus(),true);
			getXmlWriter().writeEndElement();
		}
		writeEndOfDocument();	
	}
}