/**
 * 
 */
package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

/**
 * 
 * @author Amar
 *
 */
public class PromoImageXMLResult extends XMLResult {

	public PromoImageXMLResult()
	{
		super();
	}

	private String promoImageURL;

	public String getPromoImageURL() {
		return promoImageURL;
	}

	public void setPromoImageURL(String promoImageURL) {
		this.promoImageURL = promoImageURL;
	}

	public void render() throws Exception {

		writeStartOfDocument();

		super.render();

		if(getPromoImageURL() != null)
		{
			getXmlWriter().writeStartElement("promoImageURL");
			getXmlWriter().writeCharacters(getPromoImageURL(),false);
			getXmlWriter().writeEndElement();
		}

		writeEndOfDocument();

	}

}
