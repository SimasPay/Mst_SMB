/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.domain.MfinoUser;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMErrorNotification;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSResetPassword;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.i18n.MessageText;
import com.mfino.service.UserService;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.fix.processor.ResetPasswordProcessor;

/**
 * 
 * @author Siddhartha Chinthapally
 */
@Service("ResetPasswordProcessorImpl")
public class ResetPasswordProcessorImpl extends MultixCommunicationHandler implements ResetPasswordProcessor{


	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

  @Override
  @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
  public CFIXMsg process(CFIXMsg msg) {
    CMJSResetPassword realMsg = (CMJSResetPassword) msg;
    Long userID = realMsg.getUserID();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    MfinoUser user = userDAO.getById(userID);

    
    CMJSError errorMsg = new CMJSError();
    
    Long tempStatusL = user.getStatus();
    Integer tempStatusLI = tempStatusL.intValue();
    
    if (user == null) {
    	errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
    	errorMsg.setErrorDescription("User does not Exist");
    	log.warn("Attempt to change Password by " + getLoggedUserNameWithIP() + " " +userID + " does not exist");    	
    	return errorMsg;
    }else if(!tempStatusLI.equals(CmFinoFIX.UserStatus_Active)){
    	errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
    	errorMsg.setErrorDescription("User is Inactive not allowed to reset Password");
    	log.warn(getLoggedUserNameWithIP() + " not allowed to change password as the " + user.getId() + " is inactive");
    	return errorMsg;
    }

    CMErrorNotification error = userService.resetPassword(user);
    errorMsg.setErrorCode(error.getErrorCode());
    if (errorMsg.getErrorCode() == CmFinoFIX.ErrorCode_NoError) {
      errorMsg.setErrorDescription(MessageText._("Password reset successfully"));
      log.info("Pass word reset successfully by " + getLoggedUserNameWithIP() + " for userId" + user.getId());
    }else{
    	errorMsg.setErrorDescription(MessageText._("Password reset failed try again"));
    	log.warn("Password reset atempt by " + getLoggedUserNameWithIP() + " failed for userId " + user.getId());
    }
    return errorMsg;
  }
  
//  @Override
//  public CFIXMsg handleResponse(CFIXMsg pMsg) {
//	return pMsg;
//  }
}
