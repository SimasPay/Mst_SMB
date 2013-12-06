/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.fix.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.exceptions.StaleDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.processor.IFixProcessor;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.web.WebContextError;

/**
 *
 * @author sandeepjs
 */
public abstract class BaseFixProcessor implements IFixProcessor {
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	private String loggedUserName;
	private String ipAddress;
	
   protected void handleStaleDataException() throws StaleDataException {
        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        error.setErrorDescription(MessageText._("You are working on stale Data"));
        WebContextError.addError(error);
        log.info("StaleData Exception");
        throw new StaleDataException(MessageText._(" StaleData "));
    }

   protected CmFinoFIX.CMJSError getErrorMessage(String errorDesc, int errorCode, String errorFieldName, String fieldErrorDesc) {
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        errorMsg.setErrorDescription(errorDesc);
        errorMsg.setErrorCode(errorCode);
        CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
        newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
        newEntries[0].setErrorName(errorFieldName);
        newEntries[0].setErrorDescription(fieldErrorDesc);
        return errorMsg;
   }

/**
 * @param loggedUserName the loggedUserName to set
 */
public void setLoggedUserName(String loggedUserName) {
	this.loggedUserName = loggedUserName;
}
/**
 * @return the loggedUserName
 */
public String getLoggedUserName() {
	return loggedUserName;
}
/**
 * @return the loggedUserName with IP
 */
public String getLoggedUserNameWithIP() {
	return loggedUserName+" [IP:"+ipAddress+"]";
}

public String getIpAddress() {
	return ipAddress;
}

public void setIpAddress(String ipAddress) {
	this.ipAddress = ipAddress;
}
   
   
}
