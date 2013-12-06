/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMEmptySVAPocket;
import com.mfino.fix.CmFinoFIX.CMJSEmptySVAPocket;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.uicore.fix.processor.EmptySVAPocketProcessor;

/**
 *
 * @author sunil
 */
@Service("EmptySVAPocketProcessorImpl")
public class EmptySVAPocketProcessorImpl extends MultixCommunicationHandler implements EmptySVAPocketProcessor
{

    /**
     *  If the commodity is Airtime then it is a Merchant Empty SVA Airtime
     *  If the commodity is Money then it is a subscriber Empty SVA Money     *         
     */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {

        CMJSEmptySVAPocket realMsg = (CMJSEmptySVAPocket) msg;
        CMEmptySVAPocket newMsg = new CMEmptySVAPocket();
        updateMessage(newMsg, realMsg);
        return handleRequestResponse(newMsg);
    }

    public void updateMessage(CMEmptySVAPocket newMsg, CMJSEmptySVAPocket realMsg) {
        if (realMsg.getCommodity() != null) {
            newMsg.setCommodity(realMsg.getCommodity());
        }
        if (CmFinoFIX.Commodity_Airtime.equals(realMsg.getCommodity())) {
            newMsg.setServletPath(CmFinoFIX.ServletPath_WebAppFEForMerchants);
        } else if (CmFinoFIX.Commodity_Money.equals(realMsg.getCommodity())) {
            newMsg.setServletPath(CmFinoFIX.ServletPath_WebAppFEForSubscribers);
        }

        if (realMsg.getServiceName() != null) {
            newMsg.setServiceName(realMsg.getServiceName());
        }

        if (realMsg.getMSPID() != null) {
            newMsg.setMSPID(realMsg.getMSPID());
        }
        if (realMsg.getSourceMDN() != null) {
            newMsg.setSourceMDN(realMsg.getSourceMDN());
        }
    }
}
