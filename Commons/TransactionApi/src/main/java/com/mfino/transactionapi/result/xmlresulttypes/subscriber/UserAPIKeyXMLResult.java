/**
 * 
 */
package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import com.mfino.result.XMLResult;

/**
 * 
 * @author HemanthKumar
 *
 */
public class UserAPIKeyXMLResult extends XMLResult {
	
	 public UserAPIKeyXMLResult()
	    {
		super();
	    }

private String userAPIKey;
	
	public String isUserAPIKey() {
		return userAPIKey;
	}
	public void setUserAPIKey(String userAPIKey) {
		this.userAPIKey = userAPIKey;
	}
	
	public void render() throws Exception {

		writeStartOfDocument();
		
		super.render();
		
		getXmlWriter().writeStartElement("userAPIKey");
		getXmlWriter().writeCharacters(userAPIKey,false);
		getXmlWriter().writeEndElement();

		writeEndOfDocument();

	}

}
