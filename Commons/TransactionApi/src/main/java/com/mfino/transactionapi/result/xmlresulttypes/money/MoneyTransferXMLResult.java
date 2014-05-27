package com.mfino.transactionapi.result.xmlresulttypes.money;

import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

import com.mfino.constants.XMLResultConstants;
import com.mfino.result.XMLResult;
import com.mfino.util.MfinoUtil;

public class MoneyTransferXMLResult extends XMLResult
{
    public MoneyTransferXMLResult()
    {
	super();
    }
    
    public void render() throws Exception
    {
	writeStartOfDocument();
	
	super.render();
	if (StringUtils.isNotBlank(getReceiverAccountName())) {
		getXmlWriter().writeStartElement(XMLResultConstants.RECEIVER_ACCOUNT_NAME);
		getXmlWriter().writeCharacters(String.valueOf(getReceiverAccountName()),true);
		getXmlWriter().writeEndElement();
	}
	
	NumberFormat numberFormat = MfinoUtil.getNumberFormat();
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
	
	if(getDetailsOfPresentTransaction()!=null)
	{
	    getXmlWriter().writeStartElement("refID");
	    getXmlWriter().writeCharacters(String.valueOf(getDetailsOfPresentTransaction().getID()),true);
	    getXmlWriter().writeEndElement();
	}
	
	if(getAdditionalInfo()!=null)
	{
	    getXmlWriter().writeStartElement("AdditionalInfo");
	    getXmlWriter().writeCharacters(getAdditionalInfo(),false);
	    getXmlWriter().writeEndElement();
	}
	
	if(getResponseMessage()!=null)
	{
	    getXmlWriter().writeStartElement("ResponseMessage");
	    getXmlWriter().writeCharacters(getResponseMessage(),false);
	    getXmlWriter().writeEndElement();
	}
	
	if (getNominalAmount() != null) {
		getXmlWriter().writeStartElement(XMLResultConstants.NOMINAL_AMOUNT);
		getXmlWriter().writeCharacters(numberFormat.format(getNominalAmount()),true);
		getXmlWriter().writeEndElement();
	}
	
	writeEndOfDocument();
    }

}
