package com.mfino.uicore.fix.processor.impl;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.Partner;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCheckMDN;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.service.SubscriberService;
import com.mfino.service.UserService;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CheckMDNProcessor;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Bala Sunku
 */
@Service("CheckMDNProcessorImpl")
public class CheckMDNProcessorImpl extends BaseFixProcessor implements CheckMDNProcessor{
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

  @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
  public CFIXMsg process(CFIXMsg msg) {

    CMJSCheckMDN realMsg = (CMJSCheckMDN) msg;
    CMJSError err = new CMJSError();
    
    User loggedInUser = userService.getCurrentUser();
    
    if (loggedInUser != null && StringUtils.isNotBlank(realMsg.getMDN())) {
    	Set<Partner> partners = loggedInUser.getPartners();
    	if (CollectionUtils.isNotEmpty(partners)) {
    		Partner partner = partners.iterator().next();
    		Set<SubscriberMdn> subscriberMDNs = partner.getSubscriber().getSubscriberMdns();
    		SubscriberMdn subscriberMDN = subscriberMDNs.iterator().next();
    		if (subscriberMDN.getMdn().equals(subscriberService.normalizeMDN(realMsg.getMDN()))) {
    	        err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
    	        err.setErrorDescription(MessageText._(""));
    		} else {
    			err = generateError();
    	    }
    	} else {
    		err = generateError();
    	}
    } else {
    	err = generateError();
    }
    return err;
  }
  
  private CMJSError generateError() {
	  CMJSError err = new CMJSError();
      err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
      err.setErrorDescription(MessageText._("Please enter your Account MDN"));
	  return err;
  }
}
