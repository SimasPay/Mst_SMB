/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.HashMap;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSMerchantDCT;
import com.mfino.service.EnumTextService;
import com.mfino.service.MerchantService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.service.DistributionChainLevelService;
import com.mfino.uicore.service.DistributionChainTemplateService;

/**
 *
 * @author ADMIN
 */
public class MerchantDCTProcessor extends BaseFixProcessor {

    public CFIXMsg process(CFIXMsg msg) {
        DistributionChainLevelService dclService = new DistributionChainLevelService();
        EnumTextService enumTextService = new EnumTextService();

        CMJSMerchantDCT receivedMsg = (CMJSMerchantDCT) msg;

        long[] dctIDAndLevel = MerchantService.getDCTIDAndLevel(receivedMsg.getID());
        long templateID = dctIDAndLevel[0];
        int levelNumber = (int) dctIDAndLevel[1];

        HashMap hm = dclService.getCommissionandLOP(levelNumber, templateID);

        Integer permissions = ((Integer) (hm.get("permission")));

        if (templateID > 0) {
            receivedMsg.setMerchantDistributionChainName(DistributionChainTemplateService.getName(templateID));
        }
        if (levelNumber > 0) {
            receivedMsg.setDistributionLevel(levelNumber);
        }
        if (permissions.intValue() > 0) {
            receivedMsg.setDistributionPermissionsText(enumTextService.getLevelPermissionsText(permissions));
        }

        receivedMsg.setsuccess(CmFinoFIX.Boolean_True);
        return receivedMsg;
    }
}
