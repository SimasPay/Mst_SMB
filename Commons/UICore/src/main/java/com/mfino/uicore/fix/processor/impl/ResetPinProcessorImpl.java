/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Partner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSResetPin;
import com.mfino.fix.CmFinoFIX.CMResetPin;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.service.MailService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.uicore.fix.processor.ResetPinProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.PasswordGenUtil;
  	
/**
 *
 * @author sunil
 */
@Service("ResetPinProcessorImpl")
public class ResetPinProcessorImpl extends MultixCommunicationHandler implements ResetPinProcessor{

    private static final String PIN_SOURCE = "0123456789";
    private static SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
    private static SubscriberDAO subdao = DAOFactory.getInstance().getSubscriberDAO();
    
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) {

        CMJSResetPin realMsg = (CMJSResetPin) msg;
        CMJSError error = new CMJSError();
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
//        CMResetPin newMsg = new CMResetPin();
        if(StringUtils.isBlank(realMsg.getSourceMDN())){
        	error.setErrorDescription("Invalid MDN");
        	log.warn("Invalid MDN selected by " + getLoggedUserNameWithIP());
        	return error;
        }
        SubscriberMDN mdn=subMdndao.getByMDN(realMsg.getSourceMDN());
        Subscriber subscriber=mdn.getSubscriber();
        if(mdn==null||!mdn.getStatus().equals(CmFinoFIX.SubscriberStatus_Active)){
        	error.setErrorDescription("Invalid MDN or MDN Status");
        	log.warn("Invalid MDN entered or MDN Status is not active for user:" + getLoggedUserNameWithIP());
        	return error;
        }
       
        String resetPinMode = systemParametersService.getString(SystemParameterKeys.RESET_PIN_MODE);
        mdn.setWrongPINCount(0);
        mdn.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
        subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
        String name=subscriber.getFirstName();
        if(name==null){
        	Set<Partner> partner=subscriber.getPartnerFromSubscriberID();
        	name = partner!=null&&(!partner.isEmpty())?partner.iterator().next().getTradeName():" ";
        }
        if(StringUtils.isNotBlank(resetPinMode) && GeneralConstants.RESET_PIN_MODE_OTP.equals(resetPinMode)){
    		Integer OTPLength = systemParametersService.getOTPLength();
        	String otp = MfinoUtil.generateOTP(OTPLength);
     		String digestPin1 = MfinoUtil.calculateDigestPin(mdn.getMDN(), otp);
     		mdn.setOTP(digestPin1);
        	mdn.setDigestedPIN(null);
        	mdn.setAuthorizationToken(null);
        	subMdndao.save(mdn);
        	subdao.save(subscriber);
        	log.info("Subscriber MDN: " + mdn.getID() + " : new OTP generated for subscriber:" + subscriber.getID() + "selected by user: " + getLoggedUserNameWithIP());
            String message= String.format(
                    "Dear %s , Please reset your Pin using new OTP.Your new OTP is : %s", name,otp);
            if((CmFinoFIX.NotificationMethod_Email&subscriber.getNotificationMethod())>0 && subscriberServiceExtended.isSubscriberEmailVerified(subscriber)){
            	String to=subscriber.getEmail();
            	String subject="OTP";
            	
            	mailService.asyncSendEmail(to, name, subject, message);
            }
            smsService.setDestinationMDN(mdn.getMDN());
            smsService.setMessage(message);
            smsService.asyncSendSMS();
            error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            error.setErrorDescription("Successfully generated OTP for "+realMsg.getSourceMDN());
        }else{
        String newpin = systemParametersService.generatePIN();
        String digestPin = MfinoUtil.calculateDigestPin(mdn.getMDN(), newpin);
        mdn.setDigestedPIN(digestPin);
        log.info("Subscriber MDN: " +mdn.getID() + " :new pin updated for subscriber: " + subscriber.getID() + "selected by user: " + getLoggedUserNameWithIP());
        subdao.save(subscriber);
        subMdndao.save(mdn);
       
        String message= String.format(
                "Dear %s ,\nYour Pin has been reset to: %s", name,newpin);
        if((CmFinoFIX.NotificationMethod_Email&subscriber.getNotificationMethod())>0 && subscriberServiceExtended.isSubscriberEmailVerified(subscriber)){
        	String to=subscriber.getEmail();
        	String subject="Reset Pin";
        	
        	mailService.asyncSendEmail(to, name, subject, message);
        }
        smsService.setDestinationMDN(mdn.getMDN());
        smsService.setMessage(message);
        smsService.asyncSendSMS();
        error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
        error.setErrorDescription("Successfully Reset Pin for "+realMsg.getSourceMDN());
        }
        return error;
//        updateMessage(newMsg, realMsg);
//        return handleRequestResponse(newMsg);
    }
    
    
    
    public void updateMessage(CMResetPin newMsg, CMJSResetPin realMsg) {
        if (realMsg.getAuthenticationPhrase() != null) {
            newMsg.setAuthenticationPhrase(realMsg.getAuthenticationPhrase());
        }
        int pinLength = 0;

        String pinStr = ConfigurationUtil.getPINLength();
        if (pinStr != null) {
            pinLength = Integer.parseInt(pinStr);
        }

        if (pinLength ==  CmFinoFIX.ResetPinLength_SixDigit || pinLength == CmFinoFIX.ResetPinLength_FourDigit) {
            newMsg.setNewPin(PasswordGenUtil.generate(PIN_SOURCE, pinLength));
        }

        if (realMsg.getServiceName() != null) {
            newMsg.setServiceName(realMsg.getServiceName());
        }

        if (realMsg.getMSPID() != null) {
            newMsg.setMSPID(realMsg.getMSPID());
        }
        if (realMsg.getSourceMDN() != null) {
            newMsg.setSourceMDN(realMsg.getSourceMDN());
        }

        if (realMsg.getServletPath() != null) {
            newMsg.setServletPath(realMsg.getServletPath());
        }
    }
}
