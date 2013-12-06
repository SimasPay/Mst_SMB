/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.fix.processor;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMH2HTopup;
import com.mfino.fix.CmFinoFIX.CMJSMerchantRecharge;
import com.mfino.i18n.MessageText;
import com.mfino.service.MerchantService;
import com.mfino.uicore.service.UserService;
import com.mfino.util.MfinoUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class MerchantRechargeProcessor extends MultixCommunicationHandler {

   private Logger log = LoggerFactory.getLogger(this.getClass());
  
    @Override
    public CFIXMsg process(CFIXMsg msg) {
        CMJSMerchantRecharge receivedMsg = (CMJSMerchantRecharge) msg;
        CMH2HTopup toBeSentMsg = new CmFinoFIX.CMH2HTopup();
        
        //Merchant can transact only for himself. Can not transact on behalf of others.
        if(UserService.isMerchant() && !MerchantService.isSelf(receivedMsg.getSourceMDN())) {
            CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
            errorMsg.setErrorDescription(MessageText._("You are not authorized to recharge on behalf of other merchants"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }

        updateMessage(toBeSentMsg, receivedMsg);
        return handleRequestResponse(toBeSentMsg);
    }

    private void updateMessage(CMH2HTopup toSend, CMJSMerchantRecharge received) {
        String bucketType = received.getBucketType_Recharge();
        String sourceMDN = received.getSourceMDN();
        String pin ="";// received.getPin();
        BigDecimal amount = received.getRechargeAmount();
        String destMDN = received.getDestMDN();
        destMDN = MfinoUtil.normalizeMDN(destMDN);
        toSend.setMSPID(received.getMSPID());
        toSend.setServletPath(CmFinoFIX.ServletPath_WebAppFEForMerchants);

        log.info(String.format("TOPUP: %d from %s to %s -- %tc", amount, sourceMDN, destMDN, new Date()));
        
        if (sourceMDN != null && sourceMDN.trim().length() > 0) {
            toSend.setSourceMDN(sourceMDN);
        }

        if (destMDN != null && destMDN.trim().length() > 0) {
            toSend.setDestMDN(destMDN);
        }

        if (bucketType != null && bucketType.trim().length() > 0) {
            toSend.setBucketType(bucketType);
        }

        toSend.setPin(pin);

        if (amount != null) {
            toSend.setAmount(amount);
        }
    }

    

}
