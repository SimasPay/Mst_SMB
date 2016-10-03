/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSChangePin;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ChangePinProcessor;
import com.mfino.util.MfinoUtil;
  	
/**
 *
 * @author Maruthi
 */
@Service("ChangePinProcessorImpl")
public class ChangePinProcessorImpl extends BaseFixProcessor implements ChangePinProcessor{

    private SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
    private SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
    
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {

        CMJSChangePin realMsg = (CMJSChangePin) msg;
        CMJSError error = new CMJSError();
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
//        CMResetPin newMsg = new CMResetPin();
        if(StringUtils.isBlank(realMsg.getMDN())){
        	error.setErrorDescription("Invalid MDN");
        	log.warn("Invalid MDN selected by " + getLoggedUserNameWithIP());
        	return error;
        }
        if(StringUtils.isBlank(realMsg.getOldPin())
        		||StringUtils.isBlank(realMsg.getNewPin())
        		||StringUtils.isBlank(realMsg.getConfirmPin())){
        	error.setErrorDescription("Invalid Request");
        	log.warn("Invalid Request for mdn "+realMsg.getMDN() + getLoggedUserNameWithIP());
        	return error;
        }
        if(realMsg.getOldPin().equals(realMsg.getNewPin())){
        	error.setErrorDescription("New pin must not match oldpin.");
        	log.warn("Entered newPin and oldPin are same " + getLoggedUserNameWithIP());
        	return error;
        }
        if(!MfinoUtil.isPinStrongEnough(realMsg.getNewPin())){
        	error.setErrorDescription("New pin is not Strong enough choose another pin.");
        	log.warn("New pin is not Strong enough" + getLoggedUserNameWithIP());
        	return error;
        }
        if(!realMsg.getNewPin().equals(realMsg.getConfirmPin())){
        	error.setErrorDescription("Confirmation does not match your new pin.");
        	log.warn("Confirmation does not match with new pin" + getLoggedUserNameWithIP());
        	return error;
        }
        SubscriberMdn mdn=subMdndao.getByMDN(realMsg.getMDN());
        if(StringUtils.isBlank(mdn.getDigestedpin())){
        	error.setErrorDescription("Pin not set for You please try resetPin.");
        	log.warn("Pin not set " + getLoggedUserNameWithIP());
        	return error;
        }
        String calcPIN = MfinoUtil.calculateDigestPin(mdn.getMdn(), realMsg.getOldPin());
		if (!calcPIN.equals(mdn.getDigestedpin())) {
			error.setErrorDescription("Wrong OldPin.");
        	log.warn("Wrong OldPin " + getLoggedUserNameWithIP());
        	return error;
		}
		calcPIN = MfinoUtil.calculateDigestPin(mdn.getMdn(), realMsg.getNewPin());
		Subscriber subscriber = mdn.getSubscriber();
		mdn.setDigestedpin(calcPIN);
		mdn.setWrongpincount(0);
        mdn.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
        subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
        log.info("Subscriber MDN: " +mdn.getId() + " :new pin updated for subscriber: " + subscriber.getId() + "selected by user: " + getLoggedUserNameWithIP());
        subscriberDAO.save(subscriber);
        subMdndao.save(mdn);
        realMsg.setsuccess(true);
        return realMsg;
    }
}
    
    
    
    
