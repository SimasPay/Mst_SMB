package com.mfino.transactionapi.result.xmlresulttypes.nfc;

import org.apache.commons.lang.StringUtils;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;

/**
 * 
 * @author Amar
 *
 */
@org.springframework.stereotype.Service("NFCTransactionHistoryXMLResult")
public class NFCPocketTopupInquiryXMLResult extends TransferInquiryXMLResult {

		
	public NFCPocketTopupInquiryXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		//super.render();
		
		super.appendXMLTags();

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

