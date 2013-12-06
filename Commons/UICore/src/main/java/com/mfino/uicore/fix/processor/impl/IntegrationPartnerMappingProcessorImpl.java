package com.mfino.uicore.fix.processor.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationPartnerMappingDAO;
import com.mfino.dao.query.IntegrationPartnerMappingQuery;
import com.mfino.domain.IPMapping;
import com.mfino.domain.IntegrationPartnerMapping;
import com.mfino.domain.MFSBiller;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.Partner;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSIntegrationPartnerMapping;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.IntegrationPartnerMappingService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.IntegrationPartnerMappingProcessor;
import com.mfino.uicore.web.WebContextError;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

/**
 * @author Amar
 *
 */
@Service("IntegrationPartnerMappingProcessorImpl")
public class IntegrationPartnerMappingProcessorImpl extends BaseFixProcessor implements IntegrationPartnerMappingProcessor{

	private boolean isAuthenticationKeyChanged = false;
	
	@Autowired
	@Qualifier("IntegrationPartnerMappingServiceImpl")
	private IntegrationPartnerMappingService integrationPartnerMappingService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {

		CMJSIntegrationPartnerMapping realMsg = (CMJSIntegrationPartnerMapping) msg;
		IntegrationPartnerMappingDAO integrationPartnerMappingDao = DAOFactory.getInstance().getIntegrationPartnerMappingDAO();
		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			CMJSIntegrationPartnerMapping.CGEntries[] entries = realMsg.getEntries();

			for (CMJSIntegrationPartnerMapping.CGEntries e : entries) {
				if(e != null)
				{
					IntegrationPartnerMapping integrationPartnerMapping = integrationPartnerMappingDao.getById(e.getID());

					// Check for Stale Data
					if (!e.getRecordVersion().equals(integrationPartnerMapping.getVersion())) {
						handleStaleDataException();
					}
					updateEntity(integrationPartnerMapping, e);
					try {
						validate(integrationPartnerMapping);
						/*
						 * Generate Authentication key only if it's enabled and not set previously.
						 */
						if(e.getIsAuthenticationKeyEnabled() != null && e.getIsAuthenticationKeyEnabled() && isAuthenticationKeyChanged )
						{
							generateAndSendAuthenticationKey(integrationPartnerMapping);
							isAuthenticationKeyChanged = false;
						}
						integrationPartnerMappingDao.save(integrationPartnerMapping);
					} catch (Exception ex) {
						handleException(ex);
					}
					updateMessage(integrationPartnerMapping, e);
				}
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			IntegrationPartnerMappingQuery query = new IntegrationPartnerMappingQuery();

			if (StringUtils.isNotBlank(realMsg.getInstitutionIDSearch())) {
				query.setInstitutionID(realMsg.getInstitutionIDSearch());
			}
			if (StringUtils.isNotBlank(realMsg.getIntegrationNameSearch())) {
				query.setIntegrationName(realMsg.getIntegrationNameSearch());
			}
			if (StringUtils.isNotBlank(realMsg.getPartnerSearch())) {
				query.setPartnerID(Long.parseLong(realMsg.getPartnerSearch()));
			}
			if (StringUtils.isNotBlank(realMsg.getMFSBillerSearch())) {
				query.setMfsBillerId(Long.parseLong(realMsg.getMFSBillerSearch()));
			}
			if(realMsg.getstart() != null){
				query.setStart(realMsg.getstart());
			}
			if(realMsg.getlimit() != null)
			{
				query.setLimit(realMsg.getlimit());
			}
			List<IntegrationPartnerMapping> results = integrationPartnerMappingDao.get(query);
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				IntegrationPartnerMapping integrationPartnerMapping = results.get(i);
				CMJSIntegrationPartnerMapping.CGEntries entry = new CMJSIntegrationPartnerMapping.CGEntries();
				updateMessage(integrationPartnerMapping, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			return realMsg;
		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			CMJSIntegrationPartnerMapping.CGEntries[] entries = realMsg.getEntries();

			for (CMJSIntegrationPartnerMapping.CGEntries e : entries) {
				if(e != null)
				{
					IntegrationPartnerMapping integrationPartnerMapping = new IntegrationPartnerMapping();
					updateEntity(integrationPartnerMapping, e);
					try {
						validate(integrationPartnerMapping);
						if(e.getIsAuthenticationKeyEnabled() != null && e.getIsAuthenticationKeyEnabled())
						{
							generateAndSendAuthenticationKey(integrationPartnerMapping);
						}	
						integrationPartnerMappingDao.save(integrationPartnerMapping);
					} catch (Exception ex) {
						handleException(ex);
					}
					updateMessage(integrationPartnerMapping, e);
				}
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
			CMJSIntegrationPartnerMapping.CGEntries[] entries = realMsg.getEntries();			
			for (CMJSIntegrationPartnerMapping.CGEntries e : entries) {
				integrationPartnerMappingDao.deleteById(e.getID());
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		}
		return realMsg;

	}


	private void validate(IntegrationPartnerMapping integrationPartnerMapping) throws Exception 
	{
		if(integrationPartnerMapping.getInstitutionID() == null)
		{
			throw new Exception("Institution ID can't be null.");
		}
		if(integrationPartnerMapping.getIntegrationName() == null)
		{
			throw new Exception("Integration Name can't be null.");
		}
		if(integrationPartnerMapping.getPartner() == null && integrationPartnerMapping.getMFSBiller() == null )
		{
			throw new Exception("Both Partner and MFSBiller can't be null. One of them should exist");
		}
		if(integrationPartnerMapping.getPartner() != null && integrationPartnerMapping.getMFSBiller() != null )
		{
			throw new Exception("Either Partner or MFSBiller can be associated to an Integration. Both of them can't be given simultaneously");
		}
		List<IntegrationPartnerMapping> entries = DAOFactory.getInstance().getIntegrationPartnerMappingDAO().getAll();
		Iterator<IntegrationPartnerMapping> it = entries.iterator();
		while(it.hasNext())
		{
			IntegrationPartnerMapping existingIntegration = it.next();
			
			if(integrationPartnerMapping.getIntegrationName().equals(existingIntegration.getIntegrationName())
					&& !(existingIntegration.getID().equals(integrationPartnerMapping.getID())))
			{				
				throw new Exception("Integration Name already exists");
			}
			if(integrationPartnerMapping.getInstitutionID().equals(existingIntegration.getInstitutionID())
					&& !(existingIntegration.getID().equals(integrationPartnerMapping.getID())))
			{				
				throw new Exception("Institution ID already exists");
			}
		}
	}

	private void updateEntity(IntegrationPartnerMapping integrationPartnerMapping, CMJSIntegrationPartnerMapping.CGEntries e) {
		if (StringUtils.isNotBlank(e.getInstitutionID())) {
			integrationPartnerMapping.setInstitutionID(e.getInstitutionID());
		}
		if (StringUtils.isNotBlank(e.getIntegrationName())) {
			integrationPartnerMapping.setIntegrationName(e.getIntegrationName());
		}
		
		if(e.getPartnerID() != null)
		{
			Partner partner = DAOFactory.getInstance().getPartnerDAO().getById(e.getPartnerID());
			integrationPartnerMapping.setPartner(partner);
		}
		/*
		 * Sometimes, partner associated to an integration can be reset and can be associated a Biller and vice-versa.
		 * In this case, if there exists an older partner( or MFSBiller) associated to the integration it should be reset.
		 * Hence setting the partner(or MFSBiller) value to null.
		 */
		else if(e.isRemoteModifiedPartnerID())
		{
			integrationPartnerMapping.setPartner(null);
		}
		
		if (e.getMFSBillerId() != null) {
			MFSBiller mfsBiller = DAOFactory.getInstance().getMFSBillerDAO().getById(e.getMFSBillerId());
			integrationPartnerMapping.setMFSBiller(mfsBiller);
		}
		else if(e.isRemoteModifiedMFSBillerId())
		{
			integrationPartnerMapping.setMFSBiller(null);
		}
		
		if (StringUtils.isNotBlank(e.getAuthenticationKey())) {
			integrationPartnerMapping.setAuthenticationKey(e.getAuthenticationKey());
		}
		if (e.getIsAuthenticationKeyEnabled() != null) {
			integrationPartnerMapping.setIsAuthenticationKeyEnabled(e.getIsAuthenticationKeyEnabled());
		}
		if (e.getIsLoginEnabled() != null) {
			integrationPartnerMapping.setIsLoginEnabled(e.getIsLoginEnabled());
		}
		if (e.getIsAppTypeCheckEnabled() != null) {
			integrationPartnerMapping.setIsAppTypeCheckEnabled(e.getIsAppTypeCheckEnabled());
		}
		if(e.getIsAuthenticationKeyEnabled() != null && e.getIsAuthenticationKeyEnabled())
		{
			if(StringUtils.isBlank(integrationPartnerMapping.getAuthenticationKey()))
			{
				String authenticationkey = integrationPartnerMappingService.generateAuthenticationKey();
				String institutionID = integrationPartnerMapping.getInstitutionID();
				String digestedCode = MfinoUtil.calculateDigestPin(institutionID, authenticationkey);
				integrationPartnerMapping.setAuthenticationKey(digestedCode);
				isAuthenticationKeyChanged = true;
			}
		}		
		
	}

	private void updateMessage(IntegrationPartnerMapping integrationPartnerMapping, CMJSIntegrationPartnerMapping.CGEntries e) {
		e.setID(integrationPartnerMapping.getID());
		e.setInstitutionID(integrationPartnerMapping.getInstitutionID());
		e.setIntegrationName(integrationPartnerMapping.getIntegrationName());
		if(integrationPartnerMapping.getPartner() != null)
		{
			e.setPartnerID(integrationPartnerMapping.getPartner().getID());
			e.setPartnerCode(integrationPartnerMapping.getPartner().getPartnerCode());
		}
		if(integrationPartnerMapping.getMFSBiller() != null)
		{
			e.setMFSBillerId(integrationPartnerMapping.getMFSBiller().getID());
			e.setMFSBillerCode(integrationPartnerMapping.getMFSBiller().getMFSBillerCode());
		}
		e.setAuthenticationKey(integrationPartnerMapping.getAuthenticationKey());
		e.setIsAuthenticationKeyEnabled(integrationPartnerMapping.getIsAuthenticationKeyEnabled());
		e.setIsLoginEnabled(integrationPartnerMapping.getIsLoginEnabled());
		e.setIsAppTypeCheckEnabled(integrationPartnerMapping.getIsAppTypeCheckEnabled());
		e.setRecordVersion(integrationPartnerMapping.getVersion());
		e.setCreatedBy(integrationPartnerMapping.getCreatedBy());
		e.setCreateTime(integrationPartnerMapping.getCreateTime());
		e.setUpdatedBy(integrationPartnerMapping.getUpdatedBy());
		e.setLastUpdateTime(integrationPartnerMapping.getLastUpdateTime());
		
		String ListOfIPAddressesForIntegration = "";
		Set<IPMapping> ipMappingList =  integrationPartnerMapping.getIPMappingFromIntegrationID();	
		if(ipMappingList != null)
		{
			Iterator<IPMapping> it = ipMappingList.iterator();
			while(it.hasNext())
			{
				IPMapping ipMapping = it.next();
				ListOfIPAddressesForIntegration = ListOfIPAddressesForIntegration + "<" + ipMapping.getIPAddress() + ">, ";
			}
		}
		
		if(ipMappingList.size() > 0) 
		{
			ListOfIPAddressesForIntegration = ListOfIPAddressesForIntegration.substring(0, ListOfIPAddressesForIntegration.length()-2);
		}
		e.setListOfIPsForIntegration(ListOfIPAddressesForIntegration);
	}
	
	private void generateAndSendAuthenticationKey(IntegrationPartnerMapping integrationPartnerMapping) {
		
		String authenticationKey = integrationPartnerMappingService.generateAuthenticationKey();
		String institutionID = integrationPartnerMapping.getInstitutionID();
		String digestedCode = MfinoUtil.calculateDigestPin(institutionID, authenticationKey);
		integrationPartnerMapping.setAuthenticationKey(digestedCode);
		
		NotificationWrapper notificationWrapper = new NotificationWrapper();
		notificationWrapper.setCode(CmFinoFIX.NotificationCode_AuthenticationKeyForIntegration);
		notificationWrapper.setAuthenticationKey(authenticationKey);
		notificationWrapper.setIntegrationName(integrationPartnerMapping.getIntegrationName());
		
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
			String destMDN = smdn.getMDN();
			Integer language = smdn.getSubscriber().getLanguage();
			notificationWrapper.setFirstName(smdn.getSubscriber().getFirstName());
			notificationWrapper.setLastName(smdn.getSubscriber().getLastName());
			notificationWrapper.setLanguage(language);
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			String smsMessage = notificationMessageParserService.buildMessage(notificationWrapper,true);

			smsService.setDestinationMDN(destMDN);
			smsService.setMessage(smsMessage);
			smsService.setNotificationCode(notificationWrapper.getCode());
			smsService.asyncSendSMS();
			
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			String emailMessage = notificationMessageParserService.buildMessage(notificationWrapper,true);			
			String to = partner.getAuthorizedEmail();
			String name=partner.getTradeName();
			String subject = ConfigurationUtil.getAuthenticationKeyMailSubject();
			mailService.asyncSendEmail(to, name, subject, emailMessage);
		}
	}
	
	private void handleException(Exception e) throws Exception {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		errorMsg.setErrorDescription(e.getMessage());
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		newEntries[0].setErrorDescription(e.getMessage());
		log.warn(e.getMessage());
		WebContextError.addError(errorMsg);
		//return errorMsg;
		throw e;
	}
}
