package com.mfino.transactionapi.result.xmlresulttypes.nfc;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;

/**
 * 
 * @author Amar
 *
 */
@org.springframework.stereotype.Service("NFCTransactionHistoryXMLResult")
public class ModifyNFCCardAliasXMLResult extends XMLResult {

		
	public ModifyNFCCardAliasXMLResult() {
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
		if(StringUtils.isNotBlank(getOldCardAlias()))
		{
			getXmlWriter().writeStartElement("OldCardAlias");
			getXmlWriter().writeCharacters(getOldCardAlias(),false);
			getXmlWriter().writeEndElement();
		}
		
		writeEndOfDocument();

	}
	
}

