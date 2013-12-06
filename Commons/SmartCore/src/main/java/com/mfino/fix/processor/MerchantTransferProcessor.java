/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.MerchantQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Merchant;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMH2HDistribute;
import com.mfino.fix.CmFinoFIX.CMJSMerchantTransfer;
import com.mfino.i18n.MessageText;
import com.mfino.service.MerchantService;
import com.mfino.uicore.service.UserService;
import com.mfino.util.MfinoUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class MerchantTransferProcessor extends MultixCommunicationHandler {

  private Logger log = LoggerFactory.getLogger(this.getClass());
  
    @Override
    public CFIXMsg process(CFIXMsg msg) {
        CMJSMerchantTransfer receivedMsg = (CMJSMerchantTransfer) msg;
        CMH2HDistribute toBeSentMsg = new CmFinoFIX.CMH2HDistribute();

        //Merchant can transact only for himself. Can not transact on behalf of others.
        if(UserService.isMerchant() && !MerchantService.isSelf(receivedMsg.getSourceMDN())) {
            CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
            errorMsg.setErrorDescription(MessageText._("You are not authorized to distribute on behalf of other merchants"));
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            return errorMsg;
        }
    
        String destMDN = receivedMsg.getDestMDN();
        Merchant mer = MerchantService.getMerchantFromMDN(destMDN);
        if(mer != null && !CmFinoFIX.SubscriberStatus_Active.equals(mer.getStatus())){        	
     		   CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
     		   errorMsg.setErrorDescription(MessageText._("Destination merchant is not active. Can not receive airtime"));
     		   errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);     	 
     		   return errorMsg;
        }

        updateMessage(toBeSentMsg, receivedMsg);
        return handleRequestResponse(toBeSentMsg);
    }

    private void updateMessage(CMH2HDistribute toSend, CMJSMerchantTransfer received)  {

        String sourceMDN = received.getSourceMDN();
        String destMDN = received.getDestMDN();
        destMDN = MfinoUtil.normalizeMDN(destMDN);
        String pin = "";
        BigDecimal amount = received.getTransferAmount();

        log.info(String.format("TRANSFER: %d from %s to %s -- %tc", amount, sourceMDN, destMDN, new Date()));
        
        toSend.setMSPID(received.getMSPID());
        toSend.setServletPath(CmFinoFIX.ServletPath_WebAppFEForMerchants);
        if (received.getUsername() != null && received.getUsername().trim().length() > 0) {
            UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
            UserQuery query = new UserQuery();
            query.setUserName(received.getUsername());
            List<User> results = userDAO.get(query);
            List<Merchant> finalResults = null;
            Subscriber sub = null;
            if (results.size() > 0) {
                User user = results.get(0);
                Set<Subscriber> subresults = user.getSubscriberFromUserID();
                if (subresults.size() > 0) {
                    sub = (Subscriber) subresults.toArray()[0];
                    MerchantDAO merchantDao = DAOFactory.getInstance().getMerchantDAO();
                    MerchantQuery merQuery = new MerchantQuery();
                    merQuery.setId(sub.getID());
                    finalResults = merchantDao.get(merQuery);
                }
                if (finalResults != null && finalResults.size() > 0) {
                    Set<SubscriberMDN> submdn = sub.getSubscriberMDNFromSubscriberID();
                    SubscriberMDN subcr = (SubscriberMDN) submdn.toArray()[0];
                    toSend.setDestMDN(subcr.getMDN());
                }
//                else {
//                    String error = "Merchant Doesn't Exist";
//                    handleMDNException(error);
//                }
            }
//            else {
//                String error = "User Doesn't Exist";
//                handleMDNException(error);
//            }
        }
        if (sourceMDN != null && sourceMDN.trim().length() > 0) {
            toSend.setSourceMDN(received.getSourceMDN());
        }
        if (destMDN != null && destMDN.trim().length() > 0) {
            toSend.setDestMDN(destMDN);
        }
        toSend.setPin(pin);

        if (amount != null) {
            toSend.setAmount(amount);
        }
    }
//    private void handleMDNException(String type) throws AddressLine1RequiredException {
//        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
//        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
//        error.setErrorDescription(MessageText._(type));
//        WebContextError.addError(error);
//        throw new AddressLine1RequiredException(MessageText._(" error "));
//    }
}
