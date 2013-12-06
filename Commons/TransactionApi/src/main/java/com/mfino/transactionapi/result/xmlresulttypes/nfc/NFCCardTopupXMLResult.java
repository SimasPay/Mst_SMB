package com.mfino.transactionapi.result.xmlresulttypes.nfc;

import java.text.NumberFormat;
import org.apache.commons.lang.StringUtils;
import com.mfino.result.XMLResult;
import com.mfino.util.MfinoUtil;

public class NFCCardTopupXMLResult extends XMLResult
{

	public NFCCardTopupXMLResult()
    {
	super();
    }
    
    public void render() throws Exception
    {
	writeStartOfDocument();
	
	super.render();
	
	if(getCreditAmount() != null){
		getXmlWriter().writeStartElement("amount");
		NumberFormat numberFormat = MfinoUtil.getNumberFormat();
		getXmlWriter().writeCharacters(numberFormat.format(getCreditAmount()),true);
		getXmlWriter().writeEndElement();
	}else{
		getXmlWriter().writeStartElement("amount");
		getXmlWriter().writeCharacters(String.valueOf("0"),true);
		getXmlWriter().writeEndElement();
	}
	
	if (getTransID() != null) {
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
	

	
	writeEndOfDocument();
    }

}
