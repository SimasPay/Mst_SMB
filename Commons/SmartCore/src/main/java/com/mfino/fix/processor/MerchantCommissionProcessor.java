/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.math.BigDecimal;
import java.util.HashMap;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSMerchantCommission;
import com.mfino.i18n.MessageText;
import com.mfino.service.MerchantService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.service.DistributionChainLevelService;

/**
 *
 * @author Raju
 */
public class MerchantCommissionProcessor extends BaseFixProcessor {

    public CFIXMsg process(CFIXMsg msg) {

        CMJSMerchantCommission receivedMsg = (CMJSMerchantCommission) msg;
        BigDecimal commission = ZERO;
        BigDecimal maxWeeklyPurchaseAmount = ZERO;
        BigDecimal maxAmountPerTransaction = ZERO;
        boolean isAllowedGenerateLOP = false;

        if (!CmFinoFIX.SubscriberStatus_Active.equals(receivedMsg.getStatus())) {
            receivedMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            receivedMsg.setErrorDescription(MessageText._(" is not Active"));
            receivedMsg.setsuccess(CmFinoFIX.Boolean_False);
            receivedMsg.setAllowedForLOP(CmFinoFIX.Boolean_False);
            return receivedMsg;
        }
        long[] dctIDAndLevel = MerchantService.getDCTIDAndLevel(receivedMsg.getID());
        long DCTID = dctIDAndLevel[0];
        int level = (int) dctIDAndLevel[1];

        DistributionChainLevelService dcls = new DistributionChainLevelService();

        if (DCTID != -1 && level != -1) {
            HashMap hm = dcls.getCommissionandLOP(level, DCTID);
            isAllowedGenerateLOP = ((Boolean) (hm.get("allowedforlop"))).booleanValue();
            if (isAllowedGenerateLOP) {
//                commission = ((Double) (hm.get("commission"))).doubleValue();
//                maxWeeklyPurchaseAmount = ((Long) (hm.get("maxWeeklyPurchaseAmount"))).longValue();
//                maxAmountPerTransaction = ((Long) (hm.get("maxAmountPerTransaction"))).longValue();
                commission = (BigDecimal) (hm.get("commission"));
                maxWeeklyPurchaseAmount = (BigDecimal) (hm.get("maxWeeklyPurchaseAmount"));
                maxAmountPerTransaction = (BigDecimal) (hm.get("maxAmountPerTransaction"));
            }
        }
        receivedMsg.setMaxWeeklyPurchaseAmount(maxWeeklyPurchaseAmount);
        receivedMsg.setMaxAmountPerTransaction(maxAmountPerTransaction);
        receivedMsg.setAllowedForLOP(isAllowedGenerateLOP);
        receivedMsg.setCommission(commission);

        if (!isAllowedGenerateLOP) {
            receivedMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            receivedMsg.setErrorDescription(MessageText._("is not authorized to generate LOP"));
            receivedMsg.setsuccess(CmFinoFIX.Boolean_False);
            return receivedMsg;
        }
        receivedMsg.setsuccess(CmFinoFIX.Boolean_True);
        return receivedMsg;
    }
}
