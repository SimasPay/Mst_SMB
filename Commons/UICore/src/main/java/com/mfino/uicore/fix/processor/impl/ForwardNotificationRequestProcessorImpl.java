/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMForwardNotificationRequest;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSForwardNotificationRequest;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.uicore.fix.processor.ForwardNotificationRequestProcessor;

/**
 *
 * @author sunil
 */
@Service("ForwardNotificationRequestProcessorImpl")
public class ForwardNotificationRequestProcessorImpl extends MultixCommunicationHandler implements ForwardNotificationRequestProcessor {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {
        CMJSForwardNotificationRequest realMsg = (CMJSForwardNotificationRequest) msg;
           
        CMForwardNotificationRequest newMsg = new CMForwardNotificationRequest();
        update(newMsg, realMsg);
        CMJSError responseMsg = new CMJSError();
        try {
            responseMsg = (CMJSError) handleRequestResponse(newMsg);
        } catch (Exception exp) {
            log.error("Exception in Forwarding the Request, Please Check your Mutlix Communication", exp);
            responseMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            responseMsg.setErrorDescription(CmFinoFIX.MultixError_MultiXCommunicationError);
        }
        return responseMsg;
    }

    void update(CMForwardNotificationRequest newMsg, CMJSForwardNotificationRequest realMsg) {
        newMsg.setDestMDN(realMsg.getDestMDN());
        newMsg.setCode(realMsg.getCode());
        newMsg.setEmailSubject(realMsg.getEmailSubject());
        newMsg.setFormatOnly(realMsg.getFormatOnly());
        if (realMsg.getMSPID() != null) {
            newMsg.setMSPID(realMsg.getMSPID());
        }
        newMsg.setSourceMDN(realMsg.getSourceMDN());
        //This is done with purpose, Please talk to Sunil or Moshe before making any changes.
        //newMsg.setServletPath();
    }
}
