/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.fix.processor;

import java.security.InvalidKeyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SMSPartnerDAO;
import com.mfino.domain.SMSPartner;
import com.mfino.domain.User;
import com.mfino.domain.common.KeyTokenPair;
import com.mfino.exceptions.CoreException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSResetAPItoken;
import com.mfino.i18n.MessageText;
import com.mfino.service.TokenService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MailUtil;

/**
 * 
 * @author Maruthi
 */
public class ResetAPItokenProcessor extends MultixCommunicationHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CFIXMsg process(CFIXMsg msg) {
        CMJSResetAPItoken realMsg = (CMJSResetAPItoken) msg;
        Long partnerID = realMsg.getPartnerID();
        log.info("Partner ID : " + partnerID);
        SMSPartnerDAO smspdao = DAOFactory.getInstance().getSMSPartnerDAO();
        SMSPartner smsp = smspdao.getById(partnerID);
        User user = smsp.getUser();
        if (user == null) {
            log.info("User does not exist");
            realMsg.seterrorCodes("User does not exist");
            realMsg.setsuccess(Boolean.FALSE);
        }

        TokenService service = TokenService.getInstance();
        KeyTokenPair ktp;
        String token = "";
        try {
            ktp = service.generateToken(user.getUsername());
            if (ktp != null) {
                smsp.setAPIKey(ktp.getKey());
                token = ktp.getToken();
                smspdao.save(smsp);
            } else {
                log.info("KeyToken Pair is null for username : " + user.getUsername());
            }
        } catch (CoreException ex) {
            log.error("Reset API Toket error fetching new key", ex);
        } catch (InvalidKeyException ex) {
        	log.error("Reset API Toket error fetching new key", ex);
        }

        String emailMsg =
                String.format(
                "Dear %s,\n\tYour username is %s \n\tYour token has been reset to: %s\n\tYour PartnerID is %s" + ".\n" + ConfigurationUtil.getAdditionalMsg() + "\n" + ConfigurationUtil.getEmailSignature(), smsp.getPartnerName(),
                user.getUsername(), token, partnerID);

        CFIXMsg pMsg =
                MailUtil.sendMailMultiX(user.getEmail(), user.getFirstName() + " " + user.getLastName(), ConfigurationUtil.getResetPasswordSubject(),
                emailMsg);
        CMJSError errorMsg = (CMJSError) handleResponse(pMsg);
        if (errorMsg.getErrorCode() == CmFinoFIX.ErrorCode_NoError) {
            errorMsg.setErrorDescription(MessageText._("API Token reset successfully"));
        }
        return errorMsg;
    }
}
