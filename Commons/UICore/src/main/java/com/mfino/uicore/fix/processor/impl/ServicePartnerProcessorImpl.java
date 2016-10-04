package com.mfino.uicore.fix.processor.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.AddressDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.Address;
import com.mfino.domain.Company;
import com.mfino.domain.Groups;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.User;
import com.mfino.errorcodes.Codes;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSPartner;
import com.mfino.fix.CmFinoFIX.CMJSPartner.CGEntries;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.EnumTextService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ServicePartnerProcessor;
import com.mfino.uicore.web.WebContextError;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

@Service("ServicePartnerProcessorImpl")
public class ServicePartnerProcessorImpl extends BaseFixProcessor implements ServicePartnerProcessor{
	
	private PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
	private MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
	private SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
	private SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
	private AddressDAO addressDao = DAOFactory.getInstance().getAddressDAO();
	private UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
	private KYCLevelDAO kyclevelDao = DAOFactory.getInstance().getKycLevelDAO();
	private PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	private boolean isOTPEnabled;
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("ServicePartnerProcessor::process() method BEGIN");
		
		CMJSPartner realMsg = (CMJSPartner) msg;
		isOTPEnabled = ConfigurationUtil.getSendOTPOnIntialized();
		log.info("ServicePartnerProcessor::process() :: realMsg.getaction()="+realMsg.getaction());

		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {

			CMJSPartner.CGEntries[] entries = realMsg.getEntries();

            for (CMJSPartner.CGEntries entry : entries) {
            	Partner objPartner = partnerDao.getById(Long.valueOf(entry.getID()));
                //Partner objPartner = objServicePartner.getPartner();
                Subscriber objSubscriber = objPartner.getSubscriber();
                SubscriberMdn objSubscriberMdn = ((objSubscriber != null) && (objSubscriber.getSubscriberMdns().size() > 0))? objSubscriber.getSubscriberMdns().iterator().next() : null;
                
                // Check for Stale Data
                if (!entry.getRecordVersion().equals(objPartner.getVersion())) {
                    handleStaleDataException();
                }
                if(entry.isRemoteModifiedPartnerCode()&&(!objPartner.getPartnercode().equalsIgnoreCase(entry.getPartnerCode()))){
                	PartnerDAO dao = DAOFactory.getInstance().getPartnerDAO();
            		PartnerQuery query = new PartnerQuery();
            		query.setPartnerCode(entry.getPartnerCode());
            		List<Partner> results = dao.get(query);
            		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
            			 CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                         errorMsg.setErrorDescription(MessageText._("Partner Code already exists in DB, please enter different Partner Code."));
                         errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                         return errorMsg;
            		}
                }
                if(entry.isRemoteModifiedTradeName()&&(!objPartner.getTradename().equalsIgnoreCase(entry.getTradeName()))){
                	PartnerDAO dao = DAOFactory.getInstance().getPartnerDAO();
            		PartnerQuery query = new PartnerQuery();
            		query.setTradeName(entry.getTradeName());
            		List<Partner> results = dao.get(query);
            		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
            			 CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                         errorMsg.setErrorDescription(MessageText._(" TradeName already exists in DB, please enter different TradeName."));
                         errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                         return errorMsg;
            		}
                }

/*              TODO : Need to check about this. 
 * 				boolean isAuthorized = Authorization.isAuthorized(CmFinoFIX.Permission_Subscriber_Other_Fields_Edit);

                if (!isAuthorized) {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorDescription(MessageText._("Not Authorized to edit the Subscriber details"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;
                }*/
                //for mfs2.5
                if(CmFinoFIX.BusinessPartnerType_ServicePartner.equals(objPartner.getBusinesspartnertype())&&entry.getBusinessPartnerType()!=null){
                	CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorDescription(MessageText._("Service Partner cannot be changed"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;
                }
                try
                {
                	validateBusinessPartnerType(entry);
                }
                catch(Exception e)
                {
                	CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorDescription(e.getMessage());
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;
                }
                
                
                if(CmFinoFIX.SubscriberStatus_PendingRetirement.equals(entry.getPartnerStatus())||CmFinoFIX.SubscriberStatus_Retired.equals(entry.getPartnerStatus())){
                	entry.setPartnerStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
                    int code = subscriberService.retireSubscriber(objSubscriberMdn);
                    if (code == Codes.OPERATION_NOT_ALLOWED) {
                        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
                        error.setErrorDescription(MessageText._("Subscriber retirement failed. This subscriber is registered as an active merchant. MDN will be suspended to prevent merchant performing transactions."));
                        return error;
                    }
                    partnerService.retireServices(objPartner);

                }
                if(CmFinoFIX.SubscriberStatus_Active.equals(entry.getPartnerStatus())
                		&&!objPartner.getBusinesspartnertype().equals(CmFinoFIX.BusinessPartnerType_ServicePartner)
                		&&!CmFinoFIX.SubscriberStatus_Active.equals(objPartner.getPartnerstatus())){
                	CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
                    error.setErrorDescription(MessageText._("Partner Activation not allowed."));
                    return error;
                }
                if(CmFinoFIX.SubscriberStatus_InActive.equals(entry.getPartnerStatus())){
                	if(!CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(entry.getRestrictions())) 
					{
                	CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
                    error.setErrorDescription(MessageText._("Partner InActivation not allowed."));
                    return error;
					}
                }
				if(CmFinoFIX.SubscriberStatus_Initialized.equals(entry.getPartnerStatus()) && !(CmFinoFIX.SubscriberStatus_Initialized.equals(objPartner.getPartnerstatus()) ||
						CmFinoFIX.SubscriberStatus_Suspend.equals(objPartner.getPartnerstatus()) || CmFinoFIX.SubscriberStatus_InActive.equals(objPartner.getPartnerstatus())) ){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Intializing partner not allowed."));
					return error;
				}                
				if(CmFinoFIX.SubscriberStatus_Suspend.equals(entry.getPartnerStatus())&&!CmFinoFIX.SubscriberStatus_Suspend.equals(objPartner.getPartnerstatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Suspending of Partner / Agent not allowed."));
					return error;
				}
				if(entry.getPartnerStatus() != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(entry.getPartnerStatus()) &&
						CmFinoFIX.SubscriberStatus_Suspend.equals(objPartner.getPartnerstatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Suspended Partner / Agent can be moved to Intialized status only"));
					return error;
				}				
				if(entry.getPartnerStatus() != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(entry.getPartnerStatus()) && 
						CmFinoFIX.SubscriberStatus_InActive.equals(objPartner.getPartnerstatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Inactive Partner / Agent can be moved to Intialized status only"));
					return error;
				}
				Integer partnerRestrictions = Integer.valueOf(Long.valueOf(objSubscriberMdn.getRestrictions()).intValue());
                updateEntity(objPartner, objSubscriber,objSubscriberMdn, entry);
                
				//Generate OTP for the Partner / agent if the status is changed from Suspend to Initialise or Inactive to Initialise. 
				if(entry.getPartnerStatus() != null && CmFinoFIX.SubscriberStatus_Initialized.equals(entry.getPartnerStatus())
						&&CmFinoFIX.UpgradeState_Approved.equals(objSubscriber.getUpgradestate())){
					if(isOTPEnabled){
						generateAndSendOTP(objPartner, objSubscriberMdn, objSubscriber);
						objSubscriberMdn.setWrongpincount(0);
					}else{
						hanldleNoOTP(partnerRestrictions,objPartner,objSubscriber,objSubscriberMdn);
					}						
				}
				
                 if(entry.getAuthorizedEmail()!=null||entry.getTradeName()!=null||entry.getBusinessPartnerType()!=null){
                	User user = objPartner.getMfinoUser();
                	if(entry.getAuthorizedEmail()!=null){
                 	user.setEmail(entry.getAuthorizedEmail());
                	}
                	if(entry.getTradeName()!=null){
                     	user.setFirstname(entry.getTradeName());
                    }
                	if(entry.getBusinessPartnerType()!=null){
                		user.setRole(partnerService.getRole(entry.getBusinessPartnerType()).longValue());
                	}
                 	userDAO.save(user);
                 }
                subscriberMdnDao.save(objSubscriberMdn);
                subscriberDao.save(objSubscriber);
                partnerDao.save(objPartner);
                if(entry.getAuthorizedEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
                	mailService.generateEmailVerificationMail(objSubscriber, entry.getAuthorizedEmail());					
				}
                updateMessage(objPartner, objSubscriber, objSubscriberMdn, entry);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        
			
		}else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			
            CMJSPartner.CGEntries[] entries = realMsg.getEntries();

            for (CMJSPartner.CGEntries e : entries) {
            	
            	Partner partner = new Partner();
                Subscriber subscriber;
                SubscriberMdn subscriberMdn;
                SubscriberMdn existingSubscriberMDN = null;
                
                CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                if(StringUtils.isBlank(e.getTradeName())){
                	WebContextError.addError(errorMsg);
                	errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                	errorMsg.setErrorDescription(MessageText._("MDN already registered as Partner or Agent"));
        			throw new InvalidMDNException(MessageText._("MDN already registered as Partner or Agent"));  
                }
                if(e.getBusinessPartnerType()<0){
                	errorMsg.setErrorDescription(MessageText._("Set Type for Partner"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;   	
 
                }
                try
                {
                	validateBusinessPartnerType(e);
                }
                catch(Exception ex)
                {
                	errorMsg.setErrorDescription(ex.getMessage());
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;
                }
                
                if(StringUtils.isEmpty(e.getMerchantAddressLine1())
                		||StringUtils.isEmpty(e.getMerchantAddressCity())
                		||StringUtils.isEmpty(e.getMerchantAddressCountry())
                		||StringUtils.isEmpty(e.getMerchantAddressState())
                		||StringUtils.isEmpty(e.getMerchantAddressZipcode())){
                	errorMsg.setErrorDescription(MessageText._("Fill all required fields in Address under Contact Details"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;           	
                	
                }
                if(StringUtils.isEmpty(e.getOutletAddressLine1())
                		||StringUtils.isEmpty(e.getOutletAddressCity())
                		||StringUtils.isEmpty(e.getOutletAddressCountry())
                		||StringUtils.isEmpty(e.getOutletAddressState())
                		||StringUtils.isEmpty(e.getOutletAddressZipcode())){
                	errorMsg.setErrorDescription(MessageText._("Fill all required fields in Address under Outlet Details"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;           	
                	
                }
                if(StringUtils.isBlank(e.getUsername())){
                	errorMsg.setErrorDescription(MessageText._("User name Required"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg; 
                }
                if(StringUtils.isBlank(e.getTradeName())){
                	errorMsg.setErrorDescription(MessageText._("Trade name Required"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg; 
                }
                if(StringUtils.isBlank(e.getMDN())){
                	errorMsg.setErrorDescription(MessageText._("MDN Required"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg; 
                }
               
                	PartnerDAO dao = DAOFactory.getInstance().getPartnerDAO();
            		PartnerQuery query = new PartnerQuery();
            		query.setPartnerCode(e.getPartnerCode());
            		List<Partner> results = dao.get(query);
            		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
                         errorMsg.setErrorDescription(MessageText._("Partner Code already exists in DB, please enter different Partner Code."));
                         errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                         return errorMsg;
            		}
            		query.setPartnerCode(null);
            		query.setTradeName(e.getTradeName());
            		results = dao.get(query);
            		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
            			  errorMsg.setErrorDescription(MessageText._(" TradeName already exists in DB, please enter different TradeName."));
                         errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                         return errorMsg;
            		}
                String username = e.getUsername();
                log.info("User name = " + username);
                User user = userDAO.getByUserName(username);
                if (user != null) {
                    // username already in use. So skip adding and report failure
                    errorMsg.setErrorDescription(String.format("Username %s is not available", username));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    log.info("Username already exist in DB " + username);
                    return errorMsg;
                }
                existingSubscriberMDN=subscriberMdnDao.getByMDN(subscriberService.normalizeMDN(e.getMDN()));
                if(existingSubscriberMDN==null){
                	subscriberMdn=new SubscriberMdn();
                	subscriber=new Subscriber();
                    subscriber.setMfinoServiceProvider(mspDAO.getById(1));
                }else{
                	 if(Integer.valueOf(Long.valueOf(existingSubscriberMDN.getStatus()).intValue()).equals(CmFinoFIX.SubscriberStatus_PendingRetirement)
                			 ||Integer.valueOf(Long.valueOf(existingSubscriberMDN.getStatus()).intValue()).equals(CmFinoFIX.SubscriberStatus_Retired)){
                		 errorMsg.setErrorDescription(MessageText._("Invalid MDN status not allowed to register as partner"));
                         errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                         return errorMsg; 
                	 }
                	subscriberMdn=existingSubscriberMDN;
                	subscriber=existingSubscriberMDN.getSubscriber();
                }
              

                User u = new User();
                if (userService.getUserCompany() != null) {
                    Company company = userService.getUserCompany();
                    u.setCompany(company);
                    subscriber.setCompany(company);
                } else {
                    errorMsg.setErrorDescription(String.format("Company does not exist for the logged in user"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    log.info("Company does not exist for the logged in user");
                    return errorMsg;
                }

                u.setUsername(username);
                u.setEmail(e.getAuthorizedEmail());
                u.setFirstname(findFirstName(e));
                u.setLastname(findLastName(e));
                u.setLanguage(e.getLanguage());
                u.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
                u.setStatus(CmFinoFIX.UserStatus_Registered);
                u.setTimezone(e.getTimezone());
                u.setRole(partnerService.getRole(e.getBusinessPartnerType()).longValue());
                subscriber.setType(CmFinoFIX.SubscriberType_Partner);
                KYCLevel kycLevel = kyclevelDao.getByKycLevel(ConfigurationUtil.getBulkUploadSubscriberKYClevel());
                subscriber.setKycLevel(kycLevel);
                subscriber.setUpgradablekyclevel(null);
                subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Upgradable.longValue());
                subscriber.setRegistrationmedium(CmFinoFIX.RegistrationMedium_AdminApp.longValue());
                subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS.longValue()|CmFinoFIX.NotificationMethod_Email.longValue());
                subscriberMdn.setOtp(null);
                subscriberMdn.setDigestedpin(null);
                subscriberMdn.setAuthorizationtoken(null);
                updateEntity(partner, subscriber, subscriberMdn, e);
                subscriberMdn.setSubscriber(subscriber);
                partner.setSubscriber(subscriber);
                partner.setMfinoUser(u);
                subscriber.setLastname(findLastName(e));
                subscriber.setAppliedby(userService.getCurrentUser().getUsername());
                subscriber.setAppliedtime(new Timestamp());
                subscriber.setApprovedorrejectedby("");
                subscriber.setApproveorrejectcomment("");
                subscriber.setApproveorrejecttime(null);
                                
                userDAO.save(u);                                               
                subscriberDao.save(subscriber);
                subscriberMdnDao.save(subscriberMdn);
                partnerDao.save(partner);
                if(e.getAuthorizedEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
                	mailService.generateEmailVerificationMail(subscriber, e.getAuthorizedEmail());
				}
				if(subscriber.getSubscriberGroupFromSubscriberID().size() > 0){
					SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
					for(SubscriberGroup sg: subscriber.getSubscriberGroupFromSubscriberID()){
						subscriberGroupDao.save(sg);
					}
				}
				
				updateMessage(partner,subscriber,subscriberMdn, e);
				
                String cardPan=null;
                Pocket epocket=null;
                if(existingSubscriberMDN!=null){
                	epocket=subscriberService.getDefaultPocket(existingSubscriberMDN.getId().longValue(),CmFinoFIX.PocketType_SVA,CmFinoFIX.Commodity_Money);
                }
                try{
                	cardPan=pocketService.generateSVAEMoney16DigitCardPAN(subscriberMdn.getMdn());
                }catch (Exception ex) {
					log.error("Exception to create cardPan",ex);
				}
                Long groupID = null;
				if (StringUtils.isNotEmpty(e.getGroupID())) {
					groupID = Long.valueOf(e.getGroupID());
				}
				StringBuilder  errDescription = new StringBuilder();
				boolean isError = false;
                PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, e.getBusinessPartnerType(), groupID);
                if (svaPocketTemplate == null) {
					log.info("No Default SVA Pocket template set for " + subscriber.getId());
					isError = true;
					errDescription.append("No Default SVA pocket template set for this group and partner type<br/>");
				}
                else
                {
                	if(epocket!=null&&!(Integer.valueOf(Long.valueOf(epocket.getStatus()).intValue()).equals(CmFinoFIX.PocketStatus_PendingRetirement)||Integer.valueOf(Long.valueOf(epocket.getStatus()).intValue()).equals(CmFinoFIX.PocketStatus_Retired))){
                		epocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
                		epocket.setStatustime(new Timestamp());
                		epocket.setPocketTemplateByOldpockettemplateid(epocket.getPocketTemplate());
                		epocket.setPocketTemplate(svaPocketTemplate);
                		epocket.setPockettemplatechangedby(userService.getCurrentUser().getUsername());
                		epocket.setPockettemplatechangetime(new Timestamp());
                		pocketDao.save(epocket);

                	}else{
                		epocket = pocketService.createPocket(svaPocketTemplate, subscriberMdn, CmFinoFIX.PocketStatus_Initialized, true, cardPan);
                		log.info("Default emoney pocket successfully created for the partner -->"+partner.getId());
                	}
                }
                Pocket collectorPocket = null;
                PocketTemplate collectorPocketTemplate = null;
                try {
                	collectorPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, false, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, e.getBusinessPartnerType(), groupID);
                	if(collectorPocketTemplate == null)
					{
                		log.error("There is no Collector pocket configured for the Partner type and group");
						isError = true;
                		errDescription.append("There is no Collector pocket configured for this Partner type and group<br/>" );
					}
                	else
                	{
                		String collectorPocketCardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMdn.getMdn());
                		collectorPocket = pocketService.createPocket(collectorPocketTemplate, subscriberMdn, CmFinoFIX.PocketStatus_Initialized, true, collectorPocketCardPan);
                		log.info("Default collector pocket Id --> " + collectorPocket.getId());
                	}
				} catch (Exception e1) {
					log.info("Default Collector Pocket creation is failed for the Partner --> " + partner.getId());
					isError = true;
					errDescription.append("Default Collector Pocket creation failed for the Partner<br/>");
				}
				Pocket suspensePocket = null;
				PocketTemplate suspensePocketTemplate = null;
                try {
                	suspensePocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, true, false, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, e.getBusinessPartnerType(), groupID);
					if(suspensePocketTemplate == null)
					{
						log.error("There is no Suspense pocket configured for this Partner type and group");
						isError = true;
						errDescription.append("There is no Suspense pocket configured for this Partner type and group<br/>");
					}
					else
					{
						String suspensePocketCardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMdn.getMdn());
						suspensePocket = pocketService.createPocket(suspensePocketTemplate, subscriberMdn, CmFinoFIX.PocketStatus_Initialized, true, suspensePocketCardPan);
						log.info("Default suspense pocket Id --> " + suspensePocket.getId());
					}
				} catch (Exception e1) {
					log.info("Default Suspense Pocket creation failed for the Partner --> " + partner.getId());
					isError = true;
					errDescription.append("Default Suspense Pocket creation failed for the Partner<br/>");
				}
                if(isError)
                {
                	realMsg.setErrorDescription(errDescription.toString());
                	return realMsg;
                }	                
            }            

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
            
		}else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())){
			
			PartnerQuery partnerQuery = new PartnerQuery();
			Partner partner = userService.getPartner();
			if(partner!=null){
					partnerQuery.setId(partner.getId().longValue());
					if(StringUtils.isNotBlank(realMsg.getPartnerIDSearch())&&!partner.getId().equals(Long.valueOf(realMsg.getPartnerIDSearch()))){
						partnerQuery.setId(0L);
					}				
			}else if((null != realMsg.getPartnerIDSearch()) && !("".equals(realMsg.getPartnerIDSearch()))){
				partnerQuery.setId(Long.valueOf(realMsg.getPartnerIDSearch()));
			}
			
			if((null != realMsg.getIsHierarchyTabPartnerIDSearch()) && true == realMsg.getIsHierarchyTabPartnerIDSearch()){
				if((null != realMsg.getPartnerIDSearch()) && !("".equals(realMsg.getPartnerIDSearch()))){
					partnerQuery.setId(Long.valueOf(realMsg.getPartnerIDSearch()));
				}
			}

			if((null != realMsg.getTradeNameSearch()) && !("".equals(realMsg.getTradeNameSearch()))){
				partnerQuery.setTradeName(realMsg.getTradeNameSearch());
			}
			if((null != realMsg.getAuthorizedEmailSearch()) && !("".equals(realMsg.getAuthorizedEmailSearch()))){
				partnerQuery.setAuthorizedEmail(realMsg.getAuthorizedEmailSearch());
			}
			if((null != realMsg.getCardPAN()) && !("".equals(realMsg.getCardPAN()))){
				partnerQuery.setCardPAN(realMsg.getCardPAN());
			}
			if((null != realMsg.getPartnerCodeSearch()) && !("".equals(realMsg.getPartnerCodeSearch()))){
				partnerQuery.setPartnerCode(realMsg.getPartnerCodeSearch());
			}
			
			if((null != realMsg.getPartnerTypeSearch()) && !("".equals(realMsg.getPartnerTypeSearch()))){
				partnerQuery.setPartnerType(realMsg.getPartnerTypeSearch());
			}
			if((null != realMsg.getNotPartnerTypeSearch()) && !("".equals(realMsg.getNotPartnerTypeSearch()))){
				partnerQuery.setNotPartnerType(realMsg.getNotPartnerTypeSearch());
			}		
			if ((realMsg.getServiceIDSearch() != null) && !("".equals(realMsg.getServiceIDSearch()))) {
				partnerQuery.setServiceId(realMsg.getServiceIDSearch());
			}
			if ((realMsg.getServiceProviderIDSearch() != null) && !("".equals(realMsg.getServiceProviderIDSearch()))) {
				partnerQuery.setServiceProviderId(realMsg.getServiceProviderIDSearch());
			}
			if (StringUtils.isNotBlank(realMsg.getTransactionRuleSearch())) {
				partnerQuery.setTransactionRuleId(new Long(realMsg.getTransactionRuleSearch()));
			}
			if (StringUtils.isNotBlank(realMsg.getPartnerTypeSearchString())) {
				partnerQuery.setPartnerTypeSearchString(realMsg.getPartnerTypeSearchString());
			}
			if (realMsg.getUpgradeStateSearch()!=null&&!realMsg.getUpgradeStateSearch().equals(CmFinoFIX.UpgradeStateSearch_All)) {
				partnerQuery.setUpgradeStateSearch(realMsg.getUpgradeStateSearch());
			}
			 if (!CmFinoFIX.JSmFinoAction_Update.equals(realMsg.getmfinoaction())) {
				 partnerQuery.setStart(realMsg.getstart());
				 partnerQuery.setLimit(realMsg.getlimit());
	            }
			
            List<Partner> results = partnerDao.get(partnerQuery);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                Partner objPartner = results.get(i);
                Subscriber objSubscriber = objPartner.getSubscriber();
                SubscriberMdn objSubscriberMdn = ((objSubscriber != null) && (objSubscriber.getSubscriberMdns().size() > 0))? objSubscriber.getSubscriberMdns().iterator().next() : null;

                CMJSPartner.CGEntries entry = new CMJSPartner.CGEntries();

                updateMessage(objPartner, objSubscriber, objSubscriberMdn, entry);
                realMsg.getEntries()[i] = entry;
            }
            
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(partnerQuery.getTotal());			
		}
		
		log.info("ServicePartnerProcessor::process() method END");
		return realMsg;
	}
	
	private void hanldleNoOTP(Integer oldRestrictions, Partner partner,
			Subscriber subscriber, SubscriberMdn mdn) {
		Boolean isNewSecurityLocked = ((subscriber.getRestrictions() & CmFinoFIX.SubscriberRestrictions_SecurityLocked ) != 0);
		Boolean isNewAbsoluteLocked = ((subscriber.getRestrictions() & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) != 0 );
		Boolean isOldSecurityLocked = ((oldRestrictions & CmFinoFIX.SubscriberRestrictions_SecurityLocked ) != 0 );
		Boolean isOldAbsoluteLocked = ((oldRestrictions & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) != 0);
		Boolean isNewLocked = (isNewSecurityLocked || isNewAbsoluteLocked);
		
		//if security lock and absolute lock removed activate partner
		if((isOldSecurityLocked && !isNewSecurityLocked) || (isOldAbsoluteLocked && !isNewAbsoluteLocked)){
			if(!isNewLocked){
			partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_Active);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriber.setStatustime(new Timestamp());
			mdn.setStatus(CmFinoFIX.MDNStatus_Active);
			mdn.setStatustime(new Timestamp());
			subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
			}else{
				partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriber.setStatustime(new Timestamp());
				mdn.setStatus(CmFinoFIX.MDNStatus_InActive);
				mdn.setStatustime(new Timestamp());
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
			}
		}
	}

	/**
	 *  find the lastname 
	 *  set it to empty string if the lastname is null
	 * @param e
	 * @return
	 */
	private String findLastName(CGEntries e) 
	{
		return e.getLastName()!=null?e.getLastName():" ";
	}

	/**
	 * find the firstname that need to be set for the subssriber
	 *  use tradename if firstname is not defined.
	 * @param e
	 * @return
	 */
    private String findFirstName(CGEntries e) 
    {
    	return StringUtils.isNotBlank(e.getFirstName())?e.getFirstName():e.getTradeName();
	}

	private void updateEntity(Partner partner,Subscriber subscriber, SubscriberMdn subscriberMdn,CMJSPartner.CGEntries entry) {
    	
    	/*TODO 
    	 * Not sure if this is reqd for creating partner, setting default values
    	 * */
    	if(entry.getMDN()!=null){
    		subscriberMdn.setMdn(subscriberService.normalizeMDN(entry.getMDN()));
    	}
    	if(entry.getBusinessPartnerType()!=null){
    		partner.setBusinesspartnertype(entry.getBusinessPartnerType().longValue());
    	}
        
        
        if (entry.getPartnerStatus() != null) {
        	// *FindbugsChange*
        	// Previous -- if (entry.getPartnerStatus() != partner.getPartnerStatus()) {
            if (!(entry.getPartnerStatus().equals(partner.getPartnerstatus()))) {
                subscriberMdn.setStatus(entry.getPartnerStatus());
                subscriber.setStatus(entry.getPartnerStatus());
                subscriberMdn.setStatustime(new Timestamp());
                subscriber.setStatustime(new Timestamp());
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
                partner.setPartnerstatus(entry.getPartnerStatus());
            }
        }
        
        if (entry.getIsForceCloseRequested() != null && entry.getIsForceCloseRequested().booleanValue()) {
        	subscriberMdn.setIsforcecloserequested((short) Boolean.compare(entry.getIsForceCloseRequested(), false));
        }
        
        // subscriber related fields
      
        if (entry.getLanguage() != null) {
            subscriber.setLanguage(entry.getLanguage());
        }
        else
        {
        	subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
        }
    
        if (entry.getTimezone() != null) {
            subscriber.setTimezone(entry.getTimezone());
        }
        if (entry.getCurrency() != null) {
            subscriber.setCurrency(entry.getCurrency());
        }
        
        if(entry.getBusinessPartnerType()!=null){
        	partner.setBusinesspartnertype(entry.getBusinessPartnerType().longValue());
        }
        if(entry.getPartnerCode() != null){
        	partner.setPartnercode(entry.getPartnerCode());
        }
        
        //populate address information
        if(entry.getAuthorizedEmail() != null){
        	partner.setAuthorizedemail(entry.getAuthorizedEmail());
        	subscriber.setEmail(entry.getAuthorizedEmail());
        	subscriber.setIsemailverified((short) Boolean.compare(false, true));
        }
        
        //TODO why should i do a null check?
        if(entry.getAuthorizedFaxNumber() != null){
        	partner.setAuthorizedfaxnumber(entry.getAuthorizedFaxNumber());
        }
        if(entry.getAuthorizedRepresentative() != null){
        	partner.setAuthorizedrepresentative(entry.getAuthorizedRepresentative());
        }
        if(entry.getClassification() != null){
        	partner.setClassification(entry.getClassification());
        }
        if(entry.getFaxNumber() != null){
        	partner.setFaxnumber(entry.getFaxNumber());
        }
        if(entry.getDesignation() != null){
        	partner.setDesignation(entry.getDesignation());
        }
        if(entry.getFranchisePhoneNumber() != null){
        	partner.setFranchisephonenumber(entry.getFranchisePhoneNumber());
        }
        if(entry.getIndustryClassification() != null){
        	partner.setIndustryclassification(entry.getIndustryClassification());
        }
        
        partner.setMfinoServiceProvider(mspDAO.getById(1));
        
        if(entry.getNumberOfOutlets() != null){
        	partner.setNumberofoutlets(entry.getNumberOfOutlets().longValue());
        }
        
        if(entry.getRepresentativeName() != null){
        	partner.setRepresentativename(entry.getRepresentativeName());
        }
        if(entry.getTradeName() != null){
        	partner.setTradename(entry.getTradeName());
        	  subscriber.setFirstname(entry.getTradeName());
         }
        if(entry.getTypeOfOrganization() != null){
        	partner.setTypeoforganization(entry.getTypeOfOrganization());
        }
        if(entry.getWebSite() != null){
        	partner.setWebsite(entry.getWebSite());
        }
        if(entry.getYearEstablished() != null){
        	partner.setYearestablished(entry.getYearEstablished().longValue());
        }
        
        if (entry.getRestrictions() != null) {
        	subscriber.setRestrictions(entry.getRestrictions());
        	subscriberMdn.setRestrictions(entry.getRestrictions());
        }
        
        //for merchant and outlet addresses
        Address merchantAddress = partner.getAddressByMerchantaddressid();
        	
        	if(merchantAddress == null){
        		merchantAddress = new Address();
        		partner.setAddressByMerchantaddressid(merchantAddress);
           	}
        	if(entry.getMerchantAddressLine1()!= null){
        	merchantAddress.setLine1(entry.getMerchantAddressLine1());
        	}
        	if(entry.getMerchantAddressLine2()!= null){
        	merchantAddress.setLine2(entry.getMerchantAddressLine2());
        	}
        	if(entry.getMerchantAddressCity()!= null){
        	merchantAddress.setCity(entry.getMerchantAddressCity());
        	}
        	if(entry.getMerchantAddressState()!= null){
        	merchantAddress.setState(entry.getMerchantAddressState());
        	}
        	if(entry.getMerchantAddressZipcode()!= null){
        	merchantAddress.setZipcode(entry.getMerchantAddressZipcode());
        	}
        	if(entry.getMerchantAddressCountry()!= null){
        	merchantAddress.setCountry(entry.getMerchantAddressCountry());
        	}
        	addressDao.save(merchantAddress);
        
        //outlet address
           	Address outletAddress = partner.getAddressByFranchiseoutletaddressid();
            	if(outletAddress == null){
            		outletAddress = new Address();
            		partner.setAddressByFranchiseoutletaddressid(outletAddress);
            	}
            	if(entry.getOutletAddressLine1()!= null){
                	outletAddress.setLine1(entry.getOutletAddressLine1());
                	}
                	if(entry.getOutletAddressLine2()!= null){
                	outletAddress.setLine2(entry.getOutletAddressLine2());
                	}
                	if(entry.getOutletAddressCity()!= null){
                	outletAddress.setCity(entry.getOutletAddressCity());
                	}
                	if(entry.getOutletAddressState()!= null){
                	outletAddress.setState(entry.getOutletAddressState());
                	}
                	if(entry.getOutletAddressZipcode()!= null){
                	outletAddress.setZipcode(entry.getOutletAddressZipcode());
                	}
                	if(entry.getOutletAddressCountry()!= null){
                	outletAddress.setCountry(entry.getOutletAddressCountry());
                	}   	
            	addressDao.save(outletAddress);
            	
        		if((null != entry.getGroupID()) && !("".equals(entry.getGroupID()))){
        			
        			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
        			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
        			
        			if((subscriber.getSubscriberGroupFromSubscriberID() != null) && (subscriber.getSubscriberGroupFromSubscriberID().size() > 0)){
        				Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
        				SubscriberGroup sg = subscriberGroups.iterator().next();
        				if(sg.getGroup().getID().longValue() != Long.valueOf(entry.getGroupID()).longValue()){
        					Groups group = (Groups)groupDao.getById(Long.valueOf(entry.getGroupID()));
        					sg.setGroupid(group.getId().longValue());
        					subscriberGroupDao.save(sg);
        				}
        			}
        			else{
        				Groups group = (Groups)groupDao.getById(Long.valueOf(entry.getGroupID()));
        				SubscriberGroup sg = new SubscriberGroup();
        				sg.setSubscriberid(subscriber.getId().longValue());
        				sg.setGroupid(group.getId().longValue());
        				subscriber.getSubscriberGroupFromSubscriberID().add(sg);
        				
        				if(subscriber.getId() != null){
        					subscriberGroupDao.save(sg);
        				}
        				
//        				subscriberGroupDao.save(sg);
        				//save subscriber group
//        				Set<SubscriberGroup> subscriberGroups = new HashSet<SubscriberGroup>();
//        				subscriberGroups.add(sg);
//        				s.getSubscriber().setSubscriberGroupFromSubscriberID(subscriberGroups);
        			}
        		}            	
        }
    

    private void updateMessage(Partner partner,Subscriber subscriber, SubscriberMdn subscriberMdn,  CMJSPartner.CGEntries entry) {

        if(partner != null){
        	if(subscriberMdn.getMdn()!=null){
        		entry.setMDN(subscriberMdn.getMdn());
        	}
        
	        if(partner.getId() != null){
	        	entry.setID(partner.getId().longValue());
	        }	        
	        if(partner.getLastupdatetime() != null){
	        	entry.setLastUpdateTime(partner.getLastupdatetime());
	        }
	        if(partner.getUpdatedby() != null){
	        	entry.setUpdatedBy(partner.getUpdatedby());
	        }
	        if(partner.getCreatetime() != null){
	        	entry.setCreateTime(partner.getCreatetime());
	        }
	        if(partner.getCreatedby() != null){
	        	entry.setCreatedBy(partner.getCreatedby());
	        }
	        if(partner.getSubscriber() != null){
	        	entry.setSubscriberID(subscriber.getId().longValue());
	        	entry.setMDNID(subscriberMdn.getId().longValue());
	        }
	        if(partner.getPartnercode() != null){
	        	entry.setPartnerCode(partner.getPartnercode());
	        }
	        if(partner.getPartnerstatus() != 0){
	        	entry.setPartnerStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerStatus, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), partner.getPartnerstatus()));
	        	entry.setPartnerStatus((int) partner.getPartnerstatus());
	        }
	        if(partner.getTradename() != null){
	        	entry.setTradeName(partner.getTradename());
	        }
	        if(partner.getTypeoforganization() != null){
	        	entry.setTypeOfOrganization(partner.getTypeoforganization());
	        }
	        if(partner.getFaxnumber() != null){
	        	entry.setFaxNumber(partner.getFaxnumber());
	        }
	        if(partner.getWebsite() != null){
	        	entry.setWebSite(partner.getWebsite());
	        }
	        if(partner.getAuthorizedrepresentative() != null){
	        	entry.setAuthorizedRepresentative(partner.getAuthorizedrepresentative());
	        }
	        if(partner.getRepresentativename() != null){
	        	entry.setRepresentativeName(partner.getRepresentativename());
	        }
	        if(partner.getDesignation() != null){
	        	entry.setDesignation(partner.getDesignation());
	        }
	        if(partner.getFranchisephonenumber() != null){
	        	entry.setFranchisePhoneNumber(partner.getFranchisephonenumber());
	        }
	        if(partner.getAddressByFranchiseoutletaddressid() != null){
	        	Address outletAddress = partner.getAddressByFranchiseoutletaddressid();
	        	
	        	if(outletAddress.getLine1() != null){
	        		entry.setOutletAddressLine1(outletAddress.getLine1());
	        	}
	        	if(outletAddress.getLine2() != null){
	        		entry.setOutletAddressLine2(outletAddress.getLine2());
	        	}
	        	if(outletAddress.getCity() != null){
	        		entry.setOutletAddressCity(outletAddress.getCity());
	        	}
	        	if(outletAddress.getCity() != null){
	        		entry.setOutletAddressCity(outletAddress.getCity());
	        	}
	        	if(outletAddress.getState() != null){
	        		entry.setOutletAddressState(outletAddress.getState());
	        	}
	        	if(outletAddress.getCountry() != null){
	        		entry.setOutletAddressCountry(outletAddress.getCountry());
	        	}	        	
	        	if(outletAddress.getZipcode() != null){
	        		entry.setOutletAddressZipcode(outletAddress.getZipcode());
	        	}
	        }
	        if(partner.getAddressByMerchantaddressid() != null){
	        	Address merchantAddress = partner.getAddressByMerchantaddressid();
	        	
	        	if(merchantAddress.getLine1() != null){
	        		entry.setMerchantAddressLine1(merchantAddress.getLine1());
	        	}
	        	if(merchantAddress.getLine2() != null){
	        		entry.setMerchantAddressLine2(merchantAddress.getLine2());
	        	}
	        	if(merchantAddress.getCity() != null){
	        		entry.setMerchantAddressCity(merchantAddress.getCity());
	        	}
	        	if(merchantAddress.getCity() != null){
	        		entry.setMerchantAddressCity(merchantAddress.getCity());
	        	}
	        	if(merchantAddress.getState() != null){
	        		entry.setMerchantAddressState(merchantAddress.getState());
	        	}
	        	if(merchantAddress.getCountry() != null){
	        		entry.setMerchantAddressCountry(merchantAddress.getCountry());
	        	}
	        	if(merchantAddress.getZipcode() != null){
	        		entry.setMerchantAddressZipcode(merchantAddress.getZipcode());
	        	}	        	
	        }
	        if(partner.getClassification() != null){
	        	entry.setClassification(partner.getClassification());
	        }
	        if(partner.getNumberofoutlets() != null){
	        	entry.setNumberOfOutlets(partner.getNumberofoutlets().intValue());
	        }
	        if(partner.getIndustryclassification() != null){
	        	entry.setIndustryClassification(partner.getIndustryclassification());
	        }
	        if(partner.getYearestablished() != null){
	        	entry.setYearEstablished(partner.getYearestablished().intValue());
	        }
	        if(partner.getAuthorizedfaxnumber() != null){
	        	entry.setAuthorizedFaxNumber(partner.getAuthorizedfaxnumber());
	        }
	        if(partner.getAuthorizedemail() != null){
	        	entry.setAuthorizedEmail(partner.getAuthorizedemail());
	        }
	        if (partner.getVersion() != 0) {
	            entry.setRecordVersion(Integer.valueOf(Long.valueOf(partner.getVersion()).intValue()));
	        }
	        if(partner.getBusinesspartnertype() != null){
	        	entry.setBusinessPartnerTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BusinessPartnerType, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), partner.getBusinesspartnertype()));
	        	entry.setBusinessPartnerType(partner.getBusinesspartnertype().intValue());
	        }
	        entry.setRestrictions(Integer.valueOf(Long.valueOf(subscriberMdn.getRestrictions()).intValue()));
        }
        if(subscriber != null){
        	if(subscriber.getFirstname() != null){
        		entry.setFirstName(subscriber.getFirstname());
        	}
        	if(subscriber.getLastname() != null){
        		entry.setLastName(subscriber.getLastname());
        	}
	        if(subscriber.getLanguage() != 0){
	        	entry.setLanguageText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), subscriber.getLanguage()));
	        	entry.setLanguage(Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()));
	        }
	        else
	        {
	        	subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
	        }
	        if(subscriber.getTimezone() != null){
	        	entry.setTimezone(subscriber.getTimezone());
	        }
	        if(subscriber.getCurrency() != null){
	        	entry.setCurrency(subscriber.getCurrency());	        	
	        }
	        if(subscriber.getUpgradestate()!=null){
	        	entry.setUpgradeState(subscriber.getUpgradestate().intValue());
	        	entry.setUpgradeStateText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_UpgradeState, CmFinoFIX.Language_English, subscriber.getUpgradestate()));
	        }
	        if(subscriber.getAppliedby()!=null){
	        	entry.setAppliedBy(subscriber.getAppliedby());
	        }
	        if(subscriber.getAppliedtime()!=null){
	        	entry.setAppliedTime(subscriber.getAppliedtime());
	        }
	        if(subscriber.getApprovedorrejectedby()!=null){
	        	entry.setApprovedOrRejectedBy(subscriber.getApprovedorrejectedby());
	        }
	        if(subscriber.getApproveorrejecttime()!=null){
	        	entry.setApproveOrRejectTime(subscriber.getApproveorrejecttime());
	        }
	        if(subscriber.getApproveorrejectcomment()!=null){
	        	entry.setApproveOrRejectComment(subscriber.getApproveorrejectcomment());
	        }
	        
			if((subscriber.getSubscriberGroupFromSubscriberID() != null) && (subscriber.getSubscriberGroupFromSubscriberID().size() > 0)) {
				SubscriberGroup sg = subscriber.getSubscriberGroupFromSubscriberID().iterator().next();
				entry.setGroupName(sg.getGroup().getGroupName());
				entry.setGroupID(""+sg.getGroup().getID());
			}
	        
        }
        if(partner.getMfinoUser()!=null){
        	entry.setUsername(partner.getMfinoUser().getUsername());
        }
        
    }
    
    private void generateAndSendOTP(Partner partner, SubscriberMdn subscriberMDN, Subscriber subscriber) {
        User u =partner.getMfinoUser();
		Integer OTPLength = systemParametersService.getOTPLength();
        String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMdn(), oneTimePin);
		subscriberMDN.setOtp(digestPin1);
		subscriberMDN.setOtpexpirationtime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
        String toEmail=u.getEmail();
        String toName=u.getFirstname() + " " + u.getLastname();

        String mdn=subscriberMDN.getMdn();
        NotificationWrapper smsNotificationWrapper = partnerService.genratePartnerOTPMessage(partner, oneTimePin,subscriberMDN.getMdn(), CmFinoFIX.NotificationMethod_SMS);
        String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper,true);;
        smsService.setDestinationMDN(mdn);
        smsService.setMessage(smsMessage);
        smsService.setNotificationCode(smsNotificationWrapper.getCode());
        smsService.asyncSendSMS();
        if(subscriberServiceExtended.isSubscriberEmailVerified(subscriber)) {
        NotificationWrapper emailNotificationWrapper = partnerService.genratePartnerOTPMessage(partner, oneTimePin,subscriberMDN.getMdn(), CmFinoFIX.NotificationMethod_Email);
        String emailMessage = notificationMessageParserService.buildMessage(emailNotificationWrapper,true);; 
        String sub= ConfigurationUtil.getOTPMailSubsject();
        mailService.asyncSendEmail(toEmail, toName, sub, emailMessage);
		log.info("OTP mail sent to subscriber with MDN:" + subscriberMDN.getMdn());
        } else {
        	log.info("OTP mail not sent as the email is not verified yet");
        }
    }

    private void validateBusinessPartnerType(CGEntries entry) throws Exception
    {
    	List<Partner> entries = DAOFactory.getInstance().getPartnerDAO().getAll();
		Iterator<Partner> it = entries.iterator();
		while(it.hasNext())
		{
			Partner existingPartner = it.next();
			
			if(CmFinoFIX.BusinessPartnerType_ServicePartner.equals(entry.getBusinessPartnerType())
					// *FindbugsChange*
		        	// Previous -- && (entry.getID() != (existingPartner.getID())))
					&& (entry!= null && !(entry.getID().equals(existingPartner.getId()))))
			{				
				throw new Exception("Service Partner already defined.");
			}
		}
    	
    }
}
