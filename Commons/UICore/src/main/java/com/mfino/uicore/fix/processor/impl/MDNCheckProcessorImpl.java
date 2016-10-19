/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.fix.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSMDNCheck;
import com.mfino.i18n.MessageText;
import com.mfino.service.SubscriberService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.MDNCheckProcessor;


/**
 *
 * @author sunil
 */
@Service("MDNCheckProcessorImpl")
public class MDNCheckProcessorImpl extends BaseFixProcessor implements MDNCheckProcessor{
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {

        CMJSMDNCheck realMsg = (CMJSMDNCheck) msg;
        SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
        SubscriberMdn subMdn = mdnDAO.getByMDN(subscriberService.normalizeMDN(realMsg.getMDN()));

        //TODO : Send possible username that would be available in the DB

        CMJSError err=new CMJSError();

        if(subMdn != null){
        	if(realMsg.getAgentCheck()!=null&&realMsg.getAgentCheck()){
        		if( (subMdn.getSubscriber().getType()).equals(CmFinoFIX.SubscriberType_Partner)){
        			 err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        	            err.setErrorDescription(MessageText._("Partner already Exists with this MDN"));	
        		}else if( (subMdn.getStatus()).equals(CmFinoFIX.MDNStatus_PendingRetirement)
        				|| (subMdn.getStatus()).equals(CmFinoFIX.MDNStatus_Retired)
        				|| (subMdn.getSubscriber().getStatus()).equals(CmFinoFIX.SubscriberStatus_PendingRetirement)
        				|| (subMdn.getSubscriber().getStatus()).equals(CmFinoFIX.SubscriberStatus_Retired)
        				){
        			err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
    	            err.setErrorDescription(MessageText._("Invalid MDN Status "));	
        		}
        		 
        	}else{
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("MDN already Exists in DB"));
        	}
        }else {
            err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            err.setErrorDescription(MessageText._("MDN Available"));
        }

        return err;
    }
}

