/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.fix.processor;

import java.util.HashMap;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSParentGroupIdCheck;
import com.mfino.i18n.MessageText;
import com.mfino.service.MerchantService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.service.DistributionChainLevelService;

/**
 * 
 * @author Sunil
 */
public class ParentGroupIDCheckProcessor extends BaseFixProcessor {

  public CFIXMsg process(CFIXMsg msg) {

    CMJSParentGroupIdCheck receivedMsg = (CMJSParentGroupIdCheck) msg;
    boolean isAllowedGenerateLOP = false;

    long[] dctIDAndLevel = MerchantService.getDCTIDAndLevel(receivedMsg.getID());
    long DCTID = dctIDAndLevel[0];
    int level = ((int) dctIDAndLevel[1] == -1) ? -1 : (int) dctIDAndLevel[1] + 1;

    DistributionChainLevelService dcls = new DistributionChainLevelService();

    if (DCTID != -1 && level != -1) {
      HashMap hm = dcls.getCommissionandLOP(level, DCTID);
      isAllowedGenerateLOP = ((Boolean) (hm.get("allowedforlop"))).booleanValue();
    }
    receivedMsg.setAllowedForLOP(isAllowedGenerateLOP);

    if (!isAllowedGenerateLOP) {
      receivedMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
      receivedMsg.setErrorDescription(MessageText._("Is not authorized to generate LOP"));
      receivedMsg.setsuccess(CmFinoFIX.Boolean_False);
      return receivedMsg;
    }
    receivedMsg.setsuccess(CmFinoFIX.Boolean_True);
    return receivedMsg;
  }
}
