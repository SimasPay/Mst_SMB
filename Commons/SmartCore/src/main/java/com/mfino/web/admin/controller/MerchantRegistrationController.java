/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.web.admin.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.mfino.domain.Merchant;
import com.mfino.exceptions.InvalidPasswordException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMValidateMerchant;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.i18n.MessageText;
import com.mfino.service.MerchantService;
import com.mfino.uicore.service.UserService;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author Raju
 */
@Controller
public class MerchantRegistrationController extends MultixCommunicationHandler {
        private Logger log = LoggerFactory.getLogger(this.getClass());
    @RequestMapping("/merchant_registration.htm")
    public View doAuthenticate(
            HttpServletRequest request,
            HttpServletResponse response) {

        View returnView = null;
        Transaction transaction = null;
        try {
            transaction = HibernateUtil.getCurrentSession().beginTransaction();
            String type = request.getParameter("type");

            if (type.equals("merchantmdnandpin")) {
                returnView = (checkmdnandpin(request));
            } else if (type.equals("merchantpasswordsave")) {
                returnView = (setpassword(request));
            } else {
                HashMap map = new HashMap();
                map.put("success", false);
                map.put("Error", String.format(MessageText._("Sorry, Invalid MDN or PIN")));
                returnView = new JSONView(map);
            }
            transaction.commit();
        } catch (Exception exp) {
        	log.error("Error in merchant registration", exp);
        	if(transaction!=null && transaction.isActive()){
            	transaction.rollback();
            }
        } finally{
        	if(transaction!=null && transaction.isActive()){
            	transaction.rollback();
            }
        }
        return returnView;
    }

    View setpassword(HttpServletRequest request) {
        HashMap map = new HashMap();
        String password = request.getParameter("merchantnewpassword");
        String username = request.getParameter("username");
        if (username == null || password == null) {
            map.put("success", false);
            map.put("Error", String.format(MessageText._("Sorry, Username or Password cannot be Null")));
            return new JSONView(map);
        }

        //This needs to be changed to username,oldPassword, newPassword
        //Once oldpassword needs to confirmed
        try {
			UserService.changePassword(username, password, password, Boolean.TRUE,false);
			 map.put("username", username);
		     map.put("success", true);
		} catch (InvalidPasswordException e) {
			 map.put("Error", String.format(MessageText._(" Invalid Old Password")));
		     map.put("success", false);
		}
       
        return new JSONView(map);
    }

    View checkmdnandpin(HttpServletRequest request) {
        HashMap map = new HashMap();
        String mdn = request.getParameter("merchantmdn1");
        String pin = request.getParameter("merchantpin1");

        CMValidateMerchant toBeSentMsg = new CmFinoFIX.CMValidateMerchant();
        toBeSentMsg.setMerchantPIN(pin);
        toBeSentMsg.setSourceMDN(mdn);
        toBeSentMsg.setServletPath(CmFinoFIX.ServletPath_WebAppFEForMerchants);
        toBeSentMsg.setSourceApplication(CmFinoFIX.SourceApplication_Web);
        toBeSentMsg.setMSPID(1L);//TODO: Define a constat or find one if already exists for Smart
        CmFinoFIX.CMJSError errorMsg = (CMJSError) handleRequestResponse(toBeSentMsg);
        if (errorMsg.getErrorCode() == CmFinoFIX.ErrorCode_NoError) {
            try {
                String userName = "";
                Merchant merchant = MerchantService.getMerchantFromMDN(mdn);
                if (MerchantService.checkMDNRestrictions(merchant)) {
                    userName = merchant.getSubscriber().getUser().getUsername();
                    if (userName.equals("")) {
                        map.put("success", false);
                        map.put("Error", String.format(MessageText._("Sorry,  Your MDN has no username associated, Please talk to customer support to generate one")));
                    } else {
                        map.put("success", true);
                        map.put("username", userName);
                    }
                } else {
                    map.put("success", false);
                    map.put("Error", String.format(MessageText._("Sorry,  Your MDN has restrictions or status is not active, Please talk to customer support")));
                }
            } catch (Throwable throwable) {
                log.error("Failed in merchant registration", throwable);
                map.put("success", false);
                map.put("Error", String.format(MessageText._("Sorry, Invalid MDN or PIN")));
            }
        } else {
            map.put("success", false);
            map.put("Error", String.format(MessageText._(errorMsg.getErrorDescription())));
        }
        return new JSONView(map);
    }

    public CFIXMsg process(CFIXMsg msg) {
        //We dont do anything
        return msg;
    }
}
