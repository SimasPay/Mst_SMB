/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

/**
 *
 * @author Srinu
 */
public class SubscriberMerchantDompetTransferXMLResult extends XMLResult{

     public SubscriberMerchantDompetTransferXMLResult()
    {
	super();
    }

    public void render() throws Exception
    {
	writeStartOfDocument();

	super.render();

	if (getDetailsOfPresentTransaction() != null)
	{
	    getXmlWriter().writeStartElement("refID");
	    getXmlWriter().writeCharacters(String.valueOf(getDetailsOfPresentTransaction().getId()),true);
	    getXmlWriter().writeEndElement();
	}

	writeEndOfDocument();
    }

}
