package com.mfino.uicore.fix.processor.impl;

import org.apache.commons.lang.StringUtils;
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
import com.mfino.fix.CmFinoFIX.CMJSValidatePin;
import com.mfino.i18n.MessageText;
import com.mfino.service.SubscriberService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ValidatePinProcessor;
import com.mfino.util.MfinoUtil;

@Service("ValidatePinProcessorImpl")
public class ValidatePinProcessorImpl extends BaseFixProcessor implements ValidatePinProcessor{
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	  
	  @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	  public CFIXMsg process(CFIXMsg msg) {

			CMJSValidatePin realMsg = (CMJSValidatePin) msg;
		    CMJSError err = new CMJSError();
		    SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		    
		    if (StringUtils.isNotBlank(realMsg.getSourceMDN()) && StringUtils.isNotBlank(realMsg.getPin())) {
		    	String mdn = subscriberService.normalizeMDN(realMsg.getSourceMDN());
		    	String pin = realMsg.getPin();
		    	
		    	SubscriberMdn subscriberMDN = mdnDAO.getByMDN(mdn);
		    	
		    	String calcPin = MfinoUtil.calculateDigestPin(mdn, pin);
		    	
		    	if (!calcPin.equals(subscriberMDN.getDigestedpin())) {
		    	      err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		    	      err.setErrorDescription(MessageText._("Please enter correct Pin"));
		    	      log.info("Pin validation failed for the mdn : " + mdn);
		    	} 
		    	else {
			        err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
			        err.setErrorDescription(MessageText._(""));
		    	}
		    }
		    return err;
		  }
}
