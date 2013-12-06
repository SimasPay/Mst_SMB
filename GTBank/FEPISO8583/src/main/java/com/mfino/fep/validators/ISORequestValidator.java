package com.mfino.fep.validators;

import org.jpos.iso.ISOMsg;

public abstract class ISORequestValidator {
	
	public abstract boolean isValid(ISOMsg iso);
	
	public String getCharacters(String xmlString, String startTag){		
		String endTag = startTag.replace("<", "</");
		return xmlString.substring(xmlString.indexOf(startTag)+startTag.length(), xmlString.indexOf(endTag));
	}
	
}
