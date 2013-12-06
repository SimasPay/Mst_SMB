package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSUsernameCheck;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.UsernameCheckProcessor;

@Service("UsernameCheckProcessorImpl")
public class UsernameCheckProcessorImpl extends BaseFixProcessor implements UsernameCheckProcessor{
	  
	  @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	  public CFIXMsg process(CFIXMsg msg) {

		    CMJSUsernameCheck realMsg = (CMJSUsernameCheck) msg;
		    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
		    UserQuery query = new UserQuery();
		    query.setUserName(realMsg.getUsername());

		    List<User> results = userDAO.get(query);

		    // TODO : Send possible username that would be available in the DB

		    CMJSError err = new CMJSError();

		    if (results.size() > 0) {
		      if (realMsg.getCheckIfExists()) {
		        err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		        err.setErrorDescription(MessageText._("User Name Already Exists in DB, Please Ener a Different UserName"));
		      } else {
		        err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		        err.setErrorDescription(MessageText._("User exists"));
		      }
		    } else {
		      if (realMsg.getCheckIfExists()) {
		        err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		        err.setErrorDescription(MessageText._("User Name Available"));
		      } else {
		        err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		        err.setErrorDescription(MessageText._("User doesn't exists"));
		      }
		    }

		    return err;
		  }
}
