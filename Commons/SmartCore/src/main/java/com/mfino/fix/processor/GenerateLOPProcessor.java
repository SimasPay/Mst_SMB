/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMH2HGenerateLOP;
import com.mfino.fix.CmFinoFIX.CMJSGenerateLOP;

/**
 *
 * @author Raju
 */
public class GenerateLOPProcessor extends MultixCommunicationHandler {

  
    public CFIXMsg process(CFIXMsg msg) {
        CMJSGenerateLOP realMsg = (CMJSGenerateLOP) msg;
        CMH2HGenerateLOP newMsg = new CMH2HGenerateLOP();
        updateMessage(newMsg, realMsg);
        return handleRequestResponse(newMsg);
    }

    public void updateMessage(CMH2HGenerateLOP newMsg, CMJSGenerateLOP realMsg) {

        newMsg.setLOPActualAmountPaid(realMsg.getLOPActualAmountPaid());
        newMsg.setLOPGiroRefID(realMsg.getLOPGiroRefID());
        newMsg.setPin("");//realMsg.getPin());
        newMsg.setLOPTransferDate(realMsg.getLOPTransferDate());
        newMsg.setMSPID(realMsg.getMSPID());
        newMsg.setSourceMDN(realMsg.getSourceMDN());

        newMsg.setServletPath(CmFinoFIX.ServletPath_WebAppFEForMerchants);

        if(realMsg.getComment()!=null)
        {
            newMsg.setComment(realMsg.getComment());
        }
    }   
}
