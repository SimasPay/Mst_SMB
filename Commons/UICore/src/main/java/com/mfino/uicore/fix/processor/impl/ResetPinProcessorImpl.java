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
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSResetPin;
import com.mfino.fix.CmFinoFIX.CMResetPin;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.MailService;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.NotificationMessageParserService;
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
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("MfinoUtilServiceImpl")
	private MfinoUtilService mfinoUtilService;

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
        SubscriberMdn mdn=subMdndao.getByMDN(realMsg.getSourceMDN());
        Subscriber subscriber=mdn.getSubscriber();
        
        Long tempStatusL = mdn.getStatus();
        Integer tempStatusLI = tempStatusL.intValue();
        
        if(mdn==null||!tempStatusLI.equals(CmFinoFIX.SubscriberStatus_Active)){
        	error.setErrorDescription("Invalid MDN or MDN Status");
        	log.warn("Invalid MDN entered or MDN Status is not active for user:" + getLoggedUserNameWithIP());
        	return error;
        }
       
        String resetPinMode = systemParametersService.getString(SystemParameterKeys.RESET_PIN_MODE);
        mdn.setWrongpincount(0);
        mdn.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
        subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
        String name=subscriber.getFirstname();
        if(name==null){
        	Set<Partner> partner=subscriber.getPartners();
        	name = partner!=null&&(!partner.isEmpty())?partner.iterator().next().getTradename():" ";
        }
        if(StringUtils.isNotBlank(resetPinMode) && GeneralConstants.RESET_PIN_MODE_OTP.equals(resetPinMode)){
    		Integer OTPLength = systemParametersService.getOTPLength();
        	String otp = MfinoUtil.generateOTP(OTPLength);
     		String digestPin1 = MfinoUtil.calculateDigestPin(mdn.getMdn(), otp);
     		mdn.setOtp(digestPin1);
        	mdn.setDigestedpin(null);
        	mdn.setAuthorizationtoken(null);
        	subMdndao.save(mdn);
        	subdao.save(subscriber);
        	
        	NotificationWrapper notificationWrapper = new NotificationWrapper();
        	
        	Long tempLanguageL = subscriber.getLanguage().longValue();
        	Integer tempLanguageLI = tempLanguageL.intValue();
        	
			notificationWrapper.setLanguage(tempLanguageLI);
			notificationWrapper.setCompany(subscriber.getCompany());
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_ForgotPinOTPSent);
			notificationWrapper.setDestMDN(mdn.getMdn());
			notificationWrapper.setFirstName(subscriber.getFirstname());
			notificationWrapper.setLastName(subscriber.getLastname());		
			notificationWrapper.setOneTimePin(otp);
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			String smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true);
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			String emailMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); 
        	
        	log.info("Subscriber MDN: " + mdn.getId() + " : new OTP generated for subscriber:" + subscriber.getId() + "selected by user: " + getLoggedUserNameWithIP());
            if((CmFinoFIX.NotificationMethod_Email&subscriber.getNotificationmethod())>0 && subscriberServiceExtended.isSubscriberEmailVerified(subscriber)){
            	String to=subscriber.getEmail();
            	String subject="OTP";
            	
            	mailService.asyncSendEmail(to, name, subject, emailMsg);
            }
            smsService.setDestinationMDN(mdn.getMdn());
            smsService.setMessage(smsMsg);
            smsService.asyncSendSMS();
            error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            error.setErrorDescription("Successfully generated OTP for "+realMsg.getSourceMDN());
        }
        else{
	        String newpin;
			try {
				newpin = systemParametersService.generatePIN();
				String calcPIN = mfinoUtilService.modifyPINForStoring(mdn.getMdn(), newpin);
				mdn.setDigestedpin(calcPIN);
				String authToken = MfinoUtil.calculateAuthorizationToken(mdn.getMdn(), newpin);
				mdn.setAuthorizationtoken(authToken);
			} catch (Exception e) {
				log.error("Exception occured while updating the new pin", e);
	        	error.setErrorDescription("Exception occured while updating the new pin");
	        	return error;
			}
	        
	        log.info("Subscriber MDN: " +mdn.getId() + " :new pin updated for subscriber: " + subscriber.getId() + "selected by user: " + getLoggedUserNameWithIP());
	        subdao.save(subscriber);
	        subMdndao.save(mdn);
	        
	        Long tempLanguageL = subscriber.getLanguage().longValue();
        	Integer tempLanguageLI = tempLanguageL.intValue();
	        
	    	NotificationWrapper notificationWrapper = new NotificationWrapper();
			notificationWrapper.setLanguage(tempLanguageLI);
			notificationWrapper.setCompany(subscriber.getCompany());
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_ResetPin_New_MPIN_ToSubscriber);
			notificationWrapper.setDestMDN(mdn.getMdn());
			notificationWrapper.setFirstName(subscriber.getFirstname());
			notificationWrapper.setLastName(subscriber.getLastname());		
			notificationWrapper.setSubscriberPin(newpin);
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			String smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true);
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			String emailMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); 
	
	        if((CmFinoFIX.NotificationMethod_Email&subscriber.getNotificationmethod())>0 && subscriberServiceExtended.isSubscriberEmailVerified(subscriber)){
	        	String to=subscriber.getEmail();
	        	String subject="Reset Pin";
	        	
	        	mailService.asyncSendEmail(to, name, subject, emailMsg);
	        }
	        smsService.setDestinationMDN(mdn.getMdn());
	        smsService.setMessage(smsMsg);
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
