package com.mfino.transactionapi.result.xmlresulttypes.biller;

import java.util.List;

import com.mfino.domain.Biller;
import com.mfino.result.XMLResult;

public class BillerXMLResult extends XMLResult
{
    public BillerXMLResult()
    {
	super();
    }

    public void render() throws Exception
    {
	writeStartOfDocument();
	List<Biller> billers = getBillerList();
	getXmlWriter().writeStartElement("billersList");
		if(billers != null && billers.size()>0){
			for(Biller biller: billers){
				getXmlWriter().writeStartElement("biller");
				getXmlWriter().writeStartElement("billerCode");
				getXmlWriter().writeCharacters(String.valueOf(biller.getBillercode()),false);
				getXmlWriter().writeEndElement();
				
				getXmlWriter().writeStartElement("billerName");
				getXmlWriter().writeCharacters(biller.getBillername(),false);
				getXmlWriter().writeEndElement();
				
				getXmlWriter().writeStartElement("billerType");
				getXmlWriter().writeCharacters(biller.getBillertype(),false);
				getXmlWriter().writeEndElement();
				getXmlWriter().writeEndElement();
				
				}
			}
			else{
				getXmlWriter().writeCharacters("No billers found",false);
			}
		getXmlWriter().writeEndElement();
		
	writeEndOfDocument();
    }
}