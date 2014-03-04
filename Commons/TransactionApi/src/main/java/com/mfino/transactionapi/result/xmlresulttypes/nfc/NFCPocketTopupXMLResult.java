package com.mfino.transactionapi.result.xmlresulttypes.nfc;

import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

import com.mfino.constants.XMLResultConstants;
import com.mfino.result.XMLResult;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Amar
 *
 */
@org.springframework.stereotype.Service("NFCTransactionHistoryXMLResult")
public class NFCPocketTopupXMLResult extends XMLResult {

		
	public NFCPocketTopupXMLResult() {
		super();
	}

	public void render() throws Exception {
		
		writeStartOfDocument();
		NumberFormat numberFormat = MfinoUtil.getNumberFormat();
		super.render();
		
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
		if (StringUtils.isNotBlank(getTransID())) 
		{
    		getXmlWriter().writeStartElement("transID");
    		getXmlWriter().writeCharacters(getTransID(),true);
    		getXmlWriter().writeEndElement();
    	} 
		if(StringUtils.isNotBlank(getCardPan()))
		{
			getXmlWriter().writeStartElement("CardPAN");
			getXmlWriter().writeCharacters(getCardPan(),false);
			getXmlWriter().writeEndElement();
		}
		if(StringUtils.isNotBlank(getCardAlias()))
		{
			getXmlWriter().writeStartElement("CardAlias");
			getXmlWriter().writeCharacters(getCardAlias(),false);
			getXmlWriter().writeEndElement();
		}
		if(StringUtils.isNotBlank(getNickName()))
		{
			getXmlWriter().writeStartElement("NickName");
			getXmlWriter().writeCharacters(getNickName(),false);
			getXmlWriter().writeEndElement();
		}
		
		writeEndOfDocument();

	}
	
}

