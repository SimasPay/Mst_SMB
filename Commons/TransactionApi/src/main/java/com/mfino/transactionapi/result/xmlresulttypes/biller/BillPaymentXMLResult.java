package com.mfino.transactionapi.result.xmlresulttypes.biller;

import com.mfino.result.XMLResult;

public class BillPaymentXMLResult extends XMLResult
{
	public BillPaymentXMLResult()
	{
		super();
	}

	public void render() throws Exception
	{
		writeStartOfDocument();

		buildMessage();
		String message = getXMlelements().get("ResponseString");
		String refID = null;
		String code = getXMlelements().get("code");
		Long transferID = null;
		try{
			transferID = Long.valueOf(getXMlelements().get("transferID"));
			refID = getXMlelements().get("refID");
		}catch (Exception e) {
			log.error("Error in getting transferID: "+e.getMessage(), e);
		}

		String billDetails = getXMlelements().get("billDetails");

		getXmlWriter().writeStartElement("message");
		getXmlWriter().writeAttribute("code", code,false);
		getXmlWriter().writeCharacters(message,false);
		getXmlWriter().writeEndElement();

		String success = getXMlelements().get("success");
		if (success.equals("0")&& refID!=null && transferID!=null)
		{
			getXmlWriter().writeStartElement("refID");
			getXmlWriter().writeCharacters(refID,false);
			getXmlWriter().writeEndElement();

			getXmlWriter().writeStartElement("transferID");
			getXmlWriter().writeCharacters(transferID.toString(),false);
			getXmlWriter().writeEndElement();

			getXmlWriter().writeStartElement("billDetails");
			getXmlWriter().writeCharacters(billDetails,false);
			getXmlWriter().writeEndElement();
		}

		getXmlWriter().writeStartElement("transactionTime");
		getXmlWriter().writeCharacters(formatDate(getTransactionTime()),false);
		getXmlWriter().writeEndElement();
		
		writeEndOfDocument();

	}
}