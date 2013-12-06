/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.fix.processor;
import com.mfino.domain.Merchant;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBulkLOPDistribute;
import com.mfino.i18n.MessageText;
import com.mfino.service.MerchantService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;

/**
 *
 * @author admin
 */
public class BulkLOPDistributeProcessor extends BaseFixProcessor {

    public CFIXMsg process(CFIXMsg msg) {

        CMJSBulkLOPDistribute realMsg = (CMJSBulkLOPDistribute) msg;

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

      // call java distribute logic
        return realMsg;
    }
}
