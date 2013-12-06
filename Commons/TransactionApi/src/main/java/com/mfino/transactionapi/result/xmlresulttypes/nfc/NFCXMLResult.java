package com.mfino.transactionapi.result.xmlresulttypes.nfc;

import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Amar
 *
 */
@org.springframework.stereotype.Service("NFCTransactionHistoryXMLResult")
public class NFCXMLResult extends XMLResult {

	private NumberFormat numberFormat = MfinoUtil.getNumberFormat();
			
	public NFCXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		super.render();

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
		if(getAmount() != null ) {
			getXmlWriter().writeStartElement("Amount");
			getXmlWriter().writeCharacters(numberFormat.format(getAmount()),true);
			getXmlWriter().writeEndElement();
		}
		
		writeEndOfDocument();

	}
	
}

