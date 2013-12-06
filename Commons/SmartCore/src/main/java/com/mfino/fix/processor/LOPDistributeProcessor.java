/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import com.mfino.domain.Merchant;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMH2HLOPDistribute;
import com.mfino.fix.CmFinoFIX.CMJSLOPDistribute;
import com.mfino.i18n.MessageText;
import com.mfino.service.MerchantService;
import com.mfino.uicore.security.Authorization;

/**
 *
 * @author sunil
 */
public class LOPDistributeProcessor extends MultixCommunicationHandler {

    public CFIXMsg process(CFIXMsg msg) {

        CMJSLOPDistribute realMsg = (CMJSLOPDistribute) msg;
        CMH2HLOPDistribute newMsg = new CMH2HLOPDistribute();

        //Authorization check
        if (!Authorization.isAuthorized(CmFinoFIX.Permission_LOP_Distribute)) {
            CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
            errorMsg.setErrorDescription(MessageText._("You are not authorized to Distribute LOP"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }

        //If the destination merchant, the one who receives the airtime is not Active reject the distribute.
        String MDN = realMsg.getDestMDN();
        Merchant m = MerchantService.getMerchantFromMDN(MDN);
        if (!CmFinoFIX.SubscriberStatus_Active.equals(m.getStatus())) {
            CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
            error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            error.setErrorDescription(MessageText._("Merchant is not active"));
            return error;
        }

        updateMessage(newMsg, realMsg);
        return handleRequestResponse(newMsg);
    }

    public void updateMessage(CMH2HLOPDistribute newMsg, CMJSLOPDistribute realMsg) {
        newMsg.setServletPath(CmFinoFIX.ServletPath_WebAppFEForMerchants);

        if (realMsg.getLOPID() != null) {
            newMsg.setLOPID(realMsg.getLOPID());
        }
        //if(realMsg.getPin()!=null){
        newMsg.setPin("");//realMsg.getPin());
        //}
        if (realMsg.getDestMDN() != null) {
            newMsg.setDestMDN(realMsg.getDestMDN());
        }
        if (realMsg.getServiceName() != null) {
            newMsg.setServiceName(realMsg.getServiceName());
        }
        if (realMsg.getMSPID() != null) {
            newMsg.setMSPID(realMsg.getMSPID());
        }
        newMsg.setSourceMDN("123"); //IMPORTANT: this is a dummy number. MultiX is going to ignore it, but we have to put a number here.
        if (realMsg.getAmount() != null) {
            newMsg.setAmount(realMsg.getAmount());
        }
    }
}
