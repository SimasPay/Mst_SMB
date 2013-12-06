/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.fix.processor;

import java.util.List;
import java.util.Set;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.MerchantQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Merchant;
import com.mfino.domain.Subscriber;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSMerchantNameCheck;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

/**
 * 
 * @author Raju
 */
public class MerchantUsernameCheckProcessor extends BaseFixProcessor{

  public CFIXMsg process(CFIXMsg msg) {

    CMJSMerchantNameCheck realMsg = (CMJSMerchantNameCheck) msg;
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    UserQuery query = new UserQuery();
    query.setUserName(realMsg.getUsername());

    List<User> results = userDAO.get(query);
    List<Merchant> finalResults = null;
    CMJSError err = new CMJSError();
    if (results.size() > 0) {
      User user = results.get(0);
      Set<Subscriber> subresults = user.getSubscriberFromUserID();
      if (subresults.size() > 0) {
        Subscriber sub = (Subscriber) subresults.toArray()[0];
        MerchantDAO merchantDao = DAOFactory.getInstance().getMerchantDAO();
        MerchantQuery merQuery = new MerchantQuery();
        merQuery.setId(sub.getID());
        finalResults = merchantDao.get(merQuery);
      }
      if (finalResults != null && finalResults.size() > 0) {
        if (realMsg.getCheckIfExists()) {
          err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
          err.setErrorDescription(MessageText._("Merchant Already Exists"));
        } else {
          err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
          err.setErrorDescription(MessageText._("Merchant exists"));
        }
      } else {
        if (realMsg.getCheckIfExists()) {
          err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
          err.setErrorDescription(MessageText._("Merchant Name Available"));
        } else {
          err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
          err.setErrorDescription(MessageText._("Merchant doesn't exists"));
        }
      }
    } else {
      if (realMsg.getCheckIfExists()) {
        err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
        err.setErrorDescription(MessageText._("Merchant Name Available"));
      } else {
        err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        err.setErrorDescription(MessageText._("User doesn't exists"));
      }
    }
    return err;
  }
}
