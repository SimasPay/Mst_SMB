package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;

public class LoginXMLResult extends XMLResult {

	private String	Key;
	private String	salt;
	private String	authentication;
	private String	newAppURL;
	private Integer	subscriberType;
	private boolean isValidVersion;
	
	public String getNewAppURL() {
    	return newAppURL;
    }
	public void setNewAppURL(String newAppURL) {
    	this.newAppURL = newAppURL;
    }
	public String getAuthentication() {
	    return authentication;
    }
	public void setAuthentication(String authentication) {
	    this.authentication = authentication;
    }
	public String getSalt() {
	    return salt;
    }
	public void setSalt(String salt) {
	    this.salt = salt;
    }
	public String getKey() {
	    return Key;
    }
	public void setKey(String key) {
	    Key = key;
    }
	public Integer getSubscriberType() {
		return subscriberType;
	}
	public void setSubscriberType(Integer subscriberType) {
		this.subscriberType = subscriberType;
	}
	public boolean isValidVersion() {
		return isValidVersion;
	}
	public void setValidVersion(boolean isValidVersion) {
		this.isValidVersion = isValidVersion;
	}
	public void render() throws Exception{
		writeStartOfDocument();
		
		super.render();
		
		if(!StringUtils.isBlank(Key))
		{
			getXmlWriter().writeStartElement("key");
			getXmlWriter().writeCharacters(Key,false);
			getXmlWriter().writeEndElement();
		}
		if(!StringUtils.isBlank(salt))
		{
			getXmlWriter().writeStartElement("salt");
			getXmlWriter().writeCharacters(salt,false);
			getXmlWriter().writeEndElement();
		}
		if(!StringUtils.isBlank(authentication))
		{
			getXmlWriter().writeStartElement("authentication");
			getXmlWriter().writeCharacters(authentication,false);
			getXmlWriter().writeEndElement();
		}
		if(!StringUtils.isBlank(newAppURL)){
			getXmlWriter().writeStartElement("url");
			getXmlWriter().writeCharacters(newAppURL,false);
			getXmlWriter().writeEndElement();
		}
		if(subscriberType!=null){
			getXmlWriter().writeStartElement("type");
			getXmlWriter().writeCharacters(subscriberType.toString(),false);
			getXmlWriter().writeEndElement();
		}
		writeEndOfDocument();
	}

}
