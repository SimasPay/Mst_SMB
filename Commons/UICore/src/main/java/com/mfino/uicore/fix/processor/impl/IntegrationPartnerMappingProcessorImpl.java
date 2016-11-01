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
import com.mfino.domain.IpMapping;
import com.mfino.domain.IntegrationPartnerMap;
import com.mfino.domain.MfsBiller;
import com.mfino.domain.MfsbillerPartnerMap;
import com.mfino.domain.Partner;
import com.mfino.domain.SubscriberMdn;
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
					IntegrationPartnerMap integrationPartnerMapping = integrationPartnerMappingDao.getById(e.getID());

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
			List<IntegrationPartnerMap> results = integrationPartnerMappingDao.get(query);
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				IntegrationPartnerMap integrationPartnerMapping = results.get(i);
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
					IntegrationPartnerMap integrationPartnerMapping = new IntegrationPartnerMap();
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


	private void validate(IntegrationPartnerMap integrationPartnerMapping) throws Exception 
	{
		if(integrationPartnerMapping.getInstitutionid() == null)
		{
			throw new Exception("Institution ID can't be null.");
		}
		if(integrationPartnerMapping.getIntegrationname() == null)
		{
			throw new Exception("Integration Name can't be null.");
		}
		if(integrationPartnerMapping.getPartner() == null && integrationPartnerMapping.getMfsBiller() == null )
		{
			throw new Exception("Both Partner and MfsBiller can't be null. One of them should exist");
		}
		if(integrationPartnerMapping.getPartner() != null && integrationPartnerMapping.getMfsBiller() != null )
		{
			throw new Exception("Either Partner or MfsBiller can be associated to an Integration. Both of them can't be given simultaneously");
		}
		List<IntegrationPartnerMap> entries = DAOFactory.getInstance().getIntegrationPartnerMappingDAO().getAll();
		Iterator<IntegrationPartnerMap> it = entries.iterator();
		while(it.hasNext())
		{
			IntegrationPartnerMap existingIntegration = it.next();
			
			if(integrationPartnerMapping.getIntegrationname().equals(existingIntegration.getIntegrationname())
					&& !(existingIntegration.getId().equals(integrationPartnerMapping.getId())))
			{				
				throw new Exception("Integration Name already exists");
			}
			if(integrationPartnerMapping.getInstitutionid().equals(existingIntegration.getInstitutionid())
					&& !(existingIntegration.getId().equals(integrationPartnerMapping.getId())))
			{				
				throw new Exception("Institution ID already exists");
			}
		}
	}

	private void updateEntity(IntegrationPartnerMap integrationPartnerMapping, CMJSIntegrationPartnerMapping.CGEntries e) {
		if (StringUtils.isNotBlank(e.getInstitutionID())) {
			integrationPartnerMapping.setInstitutionid(e.getInstitutionID());
		}
		if (StringUtils.isNotBlank(e.getIntegrationName())) {
			integrationPartnerMapping.setIntegrationname(e.getIntegrationName());
		}
		
		if(e.getPartnerID() != null)
		{
			Partner partner = DAOFactory.getInstance().getPartnerDAO().getById(e.getPartnerID());
			integrationPartnerMapping.setPartner(partner);
		}
		/*
		 * Sometimes, partner associated to an integration can be reset and can be associated a Biller and vice-versa.
		 * In this case, if there exists an older partner( or MfsBiller) associated to the integration it should be reset.
		 * Hence setting the partner(or MfsBiller) value to null.
		 */
		else if(e.isRemoteModifiedPartnerID())
		{
			integrationPartnerMapping.setPartner(null);
		}
		
		if (e.getMFSBillerId() != null) {
			MfsBiller mfsBiller = DAOFactory.getInstance().getMFSBillerDAO().getById(e.getMFSBillerId());
			integrationPartnerMapping.setMfsBiller(mfsBiller);
		}
		else if(e.isRemoteModifiedMFSBillerId())
		{
			integrationPartnerMapping.setMfsBiller(null);
		}
		
		if (StringUtils.isNotBlank(e.getAuthenticationKey())) {
			integrationPartnerMapping.setAuthenticationkey(e.getAuthenticationKey());
		}
		if (e.getIsAuthenticationKeyEnabled() != null) {
			integrationPartnerMapping.setIsauthenticationkeyenabled((short) (e.getIsAuthenticationKeyEnabled() ? 1:0) );
		}
		if (e.getIsLoginEnabled() != null) {
			integrationPartnerMapping.setIsloginenabled((short) (e.getIsLoginEnabled() ? 1:0));
		}
		if (e.getIsAppTypeCheckEnabled() != null) {
			integrationPartnerMapping.setIsapptypecheckenabled((short) (e.getIsAppTypeCheckEnabled() ? 1:0));
		}
		if(e.getIsAuthenticationKeyEnabled() != null && e.getIsAuthenticationKeyEnabled())
		{
			if(StringUtils.isBlank(integrationPartnerMapping.getAuthenticationkey()))
			{
				String authenticationkey = integrationPartnerMappingService.generateAuthenticationKey();
				String institutionID = integrationPartnerMapping.getInstitutionid();
				String digestedCode = MfinoUtil.calculateDigestPin(institutionID, authenticationkey);
				integrationPartnerMapping.setAuthenticationkey(digestedCode);
				isAuthenticationKeyChanged = true;
			}
		}		
		
	}

	private void updateMessage(IntegrationPartnerMap integrationPartnerMapping, CMJSIntegrationPartnerMapping.CGEntries e) {
		e.setID(integrationPartnerMapping.getId().longValue());
		e.setInstitutionID(integrationPartnerMapping.getInstitutionid());
		e.setIntegrationName(integrationPartnerMapping.getIntegrationname());
		if(integrationPartnerMapping.getPartner() != null)
		{
			e.setPartnerID(integrationPartnerMapping.getPartner().getId().longValue());
			e.setPartnerCode(integrationPartnerMapping.getPartner().getPartnercode());
		}
		if(integrationPartnerMapping.getMfsBiller() != null)
		{
			e.setMFSBillerId(integrationPartnerMapping.getMfsBiller().getId().longValue());
			e.setMFSBillerCode(integrationPartnerMapping.getMfsBiller().getMfsbillercode());
		}
		e.setAuthenticationKey(integrationPartnerMapping.getAuthenticationkey());
		e.setIsAuthenticationKeyEnabled(integrationPartnerMapping.getIsauthenticationkeyenabled() != 0);
		e.setIsLoginEnabled(integrationPartnerMapping.getIsloginenabled() != 0);
		e.setIsAppTypeCheckEnabled(integrationPartnerMapping.getIsapptypecheckenabled() != 0);
		e.setRecordVersion(integrationPartnerMapping.getVersion());
		e.setCreatedBy(integrationPartnerMapping.getCreatedby());
		e.setCreateTime(integrationPartnerMapping.getCreatetime());
		e.setUpdatedBy(integrationPartnerMapping.getUpdatedby());
		e.setLastUpdateTime(integrationPartnerMapping.getLastupdatetime());
		
		String ListOfIPAddressesForIntegration = "";
		Set<IpMapping> ipMappingList =  integrationPartnerMapping.getIpMappings();	
		if(ipMappingList != null)
		{
			Iterator<IpMapping> it = ipMappingList.iterator();
			while(it.hasNext())
			{
				IpMapping ipMapping = it.next();
				ListOfIPAddressesForIntegration = ListOfIPAddressesForIntegration + "<" + ipMapping.getIpaddress() + ">, ";
			}
		}
		
		if(ipMappingList.size() > 0) 
		{
			ListOfIPAddressesForIntegration = ListOfIPAddressesForIntegration.substring(0, ListOfIPAddressesForIntegration.length()-2);
		}
		e.setListOfIPsForIntegration(ListOfIPAddressesForIntegration);
	}
	
	private void generateAndSendAuthenticationKey(IntegrationPartnerMap integrationPartnerMapping) {
		
		String authenticationKey = integrationPartnerMappingService.generateAuthenticationKey();
		String institutionID = integrationPartnerMapping.getInstitutionid();
		String digestedCode = MfinoUtil.calculateDigestPin(institutionID, authenticationKey);
		integrationPartnerMapping.setAuthenticationkey(digestedCode);
		
		NotificationWrapper notificationWrapper = new NotificationWrapper();
		notificationWrapper.setCode(CmFinoFIX.NotificationCode_AuthenticationKeyForIntegration);
		notificationWrapper.setAuthenticationKey(authenticationKey);
		notificationWrapper.setIntegrationName(integrationPartnerMapping.getIntegrationname());
		
		Partner partner = null;
		if(integrationPartnerMapping.getPartner() != null){
			partner = integrationPartnerMapping.getPartner();			
		}
		else if(integrationPartnerMapping.getMfsBiller() != null)
		{
			Set<MfsbillerPartnerMap>  mfsBillerpartners = integrationPartnerMapping.getMfsBiller().getMfsbillerPartnerMaps();
			Iterator<MfsbillerPartnerMap> it  = mfsBillerpartners.iterator();
			if(it.hasNext())
			{
				MfsbillerPartnerMap mfsBillerPartner =  it.next();
				partner = mfsBillerPartner.getPartner();
			}
		}
		if(partner != null)
		{
			SubscriberMdn smdn = partner.getSubscriber().getSubscriberMdns().iterator().next();
			String destMDN = smdn.getMdn();
			Integer language = smdn.getSubscriber().getLanguage();
			notificationWrapper.setFirstName(smdn.getSubscriber().getFirstname());
			notificationWrapper.setLastName(smdn.getSubscriber().getLastname());
			notificationWrapper.setLanguage(language);
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			String smsMessage = notificationMessageParserService.buildMessage(notificationWrapper,true);

			smsService.setDestinationMDN(destMDN);
			smsService.setMessage(smsMessage);
			smsService.setNotificationCode(notificationWrapper.getCode());
			smsService.asyncSendSMS();
			
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			String emailMessage = notificationMessageParserService.buildMessage(notificationWrapper,true);			
			String to = partner.getAuthorizedemail();
			String name=partner.getTradename();
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
