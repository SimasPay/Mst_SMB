package com.mfino.stk.validations;

import com.mfino.result.XMLResult;

public class ValidationResult {

	private XMLResult xmlResult;
	private boolean result;
	public boolean isValid() {
	    return result;
    }
	public XMLResult getXmlResult() {
	    return xmlResult;
    }
	public void setXmlResult(XMLResult xmlResult) {
	    this.xmlResult = xmlResult;
    }
	public void setResult(boolean result) {
	    this.result = result;
    }
	
}