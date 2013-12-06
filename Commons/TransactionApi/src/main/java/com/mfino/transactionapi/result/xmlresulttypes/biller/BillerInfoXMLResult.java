package com.mfino.transactionapi.result.xmlresulttypes.biller;

import java.util.List;

import com.mfino.domain.Biller;
import com.mfino.domain.Denomination;
import com.mfino.fix.CmFinoFIX;
import com.mfino.result.XMLResult;

public class BillerInfoXMLResult extends XMLResult
{
    public BillerInfoXMLResult()
    {
	super();
    }

    public void render() throws Exception
    {
	writeStartOfDocument();
	List<Biller> biller = getBillerList();
	
		if(biller != null && biller.size()>0){
				getXmlWriter().writeStartElement("biller");
				getXmlWriter().writeStartElement("billerCode");
				getXmlWriter().writeCharacters(String.valueOf(biller.get(0).getBillerCode()),false);
				getXmlWriter().writeEndElement();
				
				getXmlWriter().writeStartElement("billerName");
				getXmlWriter().writeCharacters(biller.get(0).getBillerName(),false);
				getXmlWriter().writeEndElement();
				
				getXmlWriter().writeStartElement("billerType");
				getXmlWriter().writeCharacters(biller.get(0).getBillerType(),false);
				getXmlWriter().writeEndElement();
								
				if(biller.get(0).getBillerType().equals(CmFinoFIX.BillerType_Topup_Denomination)){
					getXmlWriter().writeStartElement("denominations");
					if(getDenominations()!=null && getDenominations().size()>0){
						for(Denomination denom : getDenominations()){
							getXmlWriter().writeStartElement("denominationAmount");
							getXmlWriter().writeCharacters(String.valueOf(denom.getDenominationAmount()),false);
							getXmlWriter().writeEndElement();
						}
						
					}else{
						getXmlWriter().writeCharacters("No Denominations found",false);
					}
					getXmlWriter().writeEndElement();
				}
				getXmlWriter().writeEndElement();
		}
		else{
			buildMessage();
			getXmlWriter().writeStartElement("message");
			getXmlWriter().writeAttribute("code", getXMlelements().get("code"),false);
			getXmlWriter().writeCharacters(getXMlelements().get("message"),false);
			getXmlWriter().writeEndElement();
		}
		
	writeEndOfDocument();
    }
}