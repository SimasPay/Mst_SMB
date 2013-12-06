package com.mfino.transactionapi.result.xmlresulttypes.money;

import com.mfino.result.XMLResult;

/**
 * @author Sasi
 * 
 */
public class AirtimePurchaseXMLResult extends XMLResult {
	
	@Override
	public void render() throws Exception {
		writeStartOfDocument();
		
		super.render();
		
/*		getXmlWriter().writeStartElement(XMLResultConstants.DEBIT_AMOUNT);
		getXmlWriter().writeCharacters(String.valueOf(getDebitAmount()),true);
		getXmlWriter().writeEndElement();

		getXmlWriter().writeStartElement(XMLResultConstants.CREDIT_AMOUNT);
		getXmlWriter().writeCharacters(String.valueOf(getCreditAmount()),true);
		getXmlWriter().writeEndElement();
		
		getXmlWriter().writeStartElement(XMLResultConstants.CHARGES);
		getXmlWriter().writeCharacters(String.valueOf(getServiceCharge()),true);
		getXmlWriter().writeEndElement();*/
		
		if(getDetailsOfPresentTransaction()!=null)
		{
		    getXmlWriter().writeStartElement("refID");
		    getXmlWriter().writeCharacters(String.valueOf(getDetailsOfPresentTransaction().getID()),true);
		    getXmlWriter().writeEndElement();
		}
		
		writeEndOfDocument();
	    }
	
	@Override
	protected String getTransferIDValue(String message) {
		return super.getTransferIDValue(message);
	}
}
