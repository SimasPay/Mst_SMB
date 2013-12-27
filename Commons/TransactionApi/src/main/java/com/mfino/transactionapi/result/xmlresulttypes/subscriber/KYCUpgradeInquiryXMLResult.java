/**
 * 
 */
package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

/**
 * @author Sreenath
 *
 */
public class KYCUpgradeInquiryXMLResult extends XMLResult {
	
	public KYCUpgradeInquiryXMLResult() {
		super();
	}
	
	public void render() throws Exception {
		writeStartOfDocument();

		super.render();
		
		writeEndOfDocument();
	}

}
