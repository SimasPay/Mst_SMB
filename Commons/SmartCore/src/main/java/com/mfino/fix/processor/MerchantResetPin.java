/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.fix.processor;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMH2HResetPin;
import com.mfino.fix.CmFinoFIX.CMJSMerchantResetPin;
import com.mfino.i18n.MessageText;
import com.mfino.service.MerchantService;
import com.mfino.uicore.security.Authorization;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.PasswordGenUtil;

/**
 * 
 * @author sunil
 */
public class MerchantResetPin extends MultixCommunicationHandler {

  private static final String PIN_SOURCE = "0123456789";

  public CFIXMsg process(CFIXMsg msg) {

    CMJSMerchantResetPin realMsg = (CMJSMerchantResetPin) msg;
    CMH2HResetPin newMsg = new CMH2HResetPin();

    Integer permission = CmFinoFIX.Permission_Merchant_Reset_Pin_Others;
    if (MerchantService.isSelf(realMsg.getSourceMDN())) {
      permission = CmFinoFIX.Permission_Merchant_Reset_Pin_Self;
    }

    // boolean isAuth = Authorization.isAuthorized(ItemType.FixMessage,
    // realMsg.getClass().getName(), fieldID, "default");
    boolean isAuth = Authorization.isAuthorized(permission);

    if (isAuth) {
      updateMessage(newMsg, realMsg);
      return handleRequestResponse(newMsg);
    } else {
      CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
      errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
      errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation."));
      return errorMsg;
    }
  }

  public void updateMessage(CMH2HResetPin newMsg, CMJSMerchantResetPin realMsg) {
    if (realMsg.getAuthenticationPhrase() != null) {
      newMsg.setAuthenticationPhrase(realMsg.getAuthenticationPhrase());
    }
    int pinLength = 0;

    String pinStr = ConfigurationUtil.getPINLength();
    if (pinStr != null) {
      pinLength = Integer.parseInt(pinStr);
    }
    String genPin = PasswordGenUtil.generate(PIN_SOURCE, pinLength);

    newMsg.setNewPin(genPin);
    newMsg.setConfPin(genPin);
    newMsg.setServletPath(CmFinoFIX.ServletPath_WebAppFEForMerchants);

    if (realMsg.getServiceName() != null) {
      newMsg.setServiceName(realMsg.getServiceName());
    }

    if (realMsg.getMSPID() != null) {
      newMsg.setMSPID(realMsg.getMSPID());
    }
    if (realMsg.getSourceMDN() != null) {
      newMsg.setSourceMDN(realMsg.getSourceMDN());
    }

  }
}
