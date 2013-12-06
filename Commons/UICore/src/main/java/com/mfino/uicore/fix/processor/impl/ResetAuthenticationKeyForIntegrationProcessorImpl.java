/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationPartnerMappingDAO;
import com.mfino.domain.IntegrationPartnerMapping;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.Partner;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSResetAuthenticationKeyForIntegration;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.IntegrationPartnerMappingService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ResetAuthenticationKeyForIntegrationProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

/**
 * @author Amar
 */
@Service("ResetAuthenticationKeyForIntegrationProcessorImpl")
public class ResetAuthenticationKeyForIntegrationProcessorImpl extends BaseFixProcessor implements ResetAuthenticationKeyForIntegrationProcessor{
	
	@Autowired
	@Qualifier("IntegrationPartnerMappingServiceImpl")
	private IntegrationPartnerMappingService integrationPartnerMappingService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {

		CMJSResetAuthenticationKeyForIntegration realMsg = (CMJSResetAuthenticationKeyForIntegration) msg;
		CMJSError error = new CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);

		IntegrationPartnerMappingDAO integrationPartnerMappingDao = DAOFactory.getInstance().getIntegrationPartnerMappingDAO();
		if(realMsg.getIntegrationID() != null)
		{
		IntegrationPartnerMapping integrationPartnerMapping= integrationPartnerMappingDao.getById(realMsg.getIntegrationID());
			if(integrationPartnerMapping != null)
			{				
				if(!integrationPartnerMapping.getIsAuthenticationKeyEnabled())
				{
					error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
					error.setErrorDescription(MessageText._("Authentication Key is not enabled for this Integration"));
					return error;
				}				
				String authenticationkey = integrationPartnerMappingService.generateAuthenticationKey();
				String institutionID = integrationPartnerMapping.getInstitutionID();
				String digestedCode = MfinoUtil.calculateDigestPin(institutionID, authenticationkey);
				integrationPartnerMapping.setAuthenticationKey(digestedCode);
				integrationPartnerMappingDao.save(integrationPartnerMapping);
				NotificationWrapper wrapper = new NotificationWrapper();
				wrapper.setAuthenticationKey(authenticationkey);
				wrapper.setIntegrationName(integrationPartnerMapping.getIntegrationName());
				wrapper.setCode(CmFinoFIX.NotificationCode_ResetAuthenticationKeyForIntegration);
				
				Partner partner = null;
				if(integrationPartnerMapping.getPartner() != null){
					partner = integrationPartnerMapping.getPartner();			
				}
				else if(integrationPartnerMapping.getMFSBiller() != null)
				{
					Set<MFSBillerPartner>  mfsBillerpartners = integrationPartnerMapping.getMFSBiller().getMFSBillerPartnerFromMFSBillerId();
					Iterator<MFSBillerPartner> it  = mfsBillerpartners.iterator();
					if(it.hasNext())
					{
						MFSBillerPartner mfsBillerPartner =  it.next();
						partner = mfsBillerPartner.getPartner();
					}
				}
				if(partner != null)
				{
					SubscriberMDN smdn = partner.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
					wrapper.setFirstName(smdn.getSubscriber().getFirstName());
					wrapper.setLastName(smdn.getSubscriber().getLastName());
					String destMDN = smdn.getMDN();
					Integer language = smdn.getSubscriber().getLanguage();
					wrapper.setLanguage(language);
					wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
					String smsMessage = notificationMessageParserService.buildMessage(wrapper,true);					
					
					smsService.setDestinationMDN(destMDN);
					smsService.setMessage(smsMessage);
					smsService.setNotificationCode(wrapper.getCode());
					smsService.asyncSendSMS();
					
					wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
					String emailMsg = notificationMessageParserService.buildMessage(wrapper,true);					
					String to = partner.getAuthorizedEmail();
					String name=partner.getTradeName();
					String subject = ConfigurationUtil.getAuthenticationKeyMailSubject();
					mailService.asyncSendEmail(to, name, subject, emailMsg);
				}

			}
			error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
			error.setErrorDescription(MessageText._("New Access Code sent successfully"));
		}

		return error;

	}
	
}
