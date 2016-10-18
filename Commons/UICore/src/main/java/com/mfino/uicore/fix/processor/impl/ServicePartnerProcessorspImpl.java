package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.AddressDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.DistrictDAO;
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.KtpDetailsDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ProvinceDAO;
import com.mfino.dao.ProvinceRegionDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SubscribersAdditionalFieldsDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.VillageDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.Address;
import com.mfino.domain.Company;
import com.mfino.domain.Groups;
import com.mfino.domain.KtpDetails;
import com.mfino.domain.KycLevel;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberAddiInfo;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.errorcodes.Codes;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSAgent;
import com.mfino.fix.CmFinoFIX.CMJSAgentError;
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
import com.mfino.uicore.fix.processor.ServicePartnerProcessorsp;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.wsclient.RSClientPostHttps;

@Service("ServicePartnerProcessorspImpl")
public class ServicePartnerProcessorspImpl extends BaseFixProcessor implements ServicePartnerProcessorsp{
	
	private PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
	private MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
	private SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
	private SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
	private AddressDAO addressDao = DAOFactory.getInstance().getAddressDAO();
	private UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
	private KYCLevelDAO kyclevelDao = DAOFactory.getInstance().getKycLevelDAO();
	private PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	private SubscribersAdditionalFieldsDAO subAddFldsDAO = DAOFactory.getInstance().getSubscribersAdditionalFieldsDAO();
	private KtpDetailsDAO ktpDetailsDAO = DAOFactory.getInstance().getKtpDetailsDAO();
	private ProvinceDAO provinceDAO = DAOFactory.getInstance().getProvinceDAO();
	private ProvinceRegionDAO provinceRegionDAO = DAOFactory.getInstance().getProvinceRegionDAO();
	private DistrictDAO districtDAO = DAOFactory.getInstance().getDistrictDAO();
	private VillageDAO villageDAO = DAOFactory.getInstance().getVillageDAO();
	private SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
	private  GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
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
		log.info("ServicePartnerProcessorspImpl::process() method BEGIN");
		
		CMJSAgent realMsg = (CMJSAgent) msg;
		isOTPEnabled = ConfigurationUtil.getSendOTPOnIntialized();
		log.info("ServicePartnerProcessorspImpl::process() :: realMsg.getaction()="+realMsg.getaction());
		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			log.info("entered into update block");
			CMJSAgent.CGEntries[] entries = realMsg.getEntries();

            for (CMJSAgent.CGEntries entry : entries) {
            	Partner objPartner = partnerDao.getById(Long.valueOf(entry.getID()));
                //Partner objPartner = objServicePartner.getPartner();
                Subscriber objSubscriber = objPartner.getSubscriber();
                SubscriberMdn objSubscriberMdn = ((objSubscriber != null) && (objSubscriber.getSubscriberMdns().size() > 0))? objSubscriber.getSubscriberMdns().iterator().next() : null;
                
                // Check for Stale Data
                if (!entry.getRecordVersion().equals(objPartner.getVersion())) {
                    handleStaleDataException();
                }
                if(entry.getEMail() != null){
                	entry.setAuthorizedEmail(entry.getEMail());
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
                updateEntityAddInfo(objSubscriber,entry);
                
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
                	MfinoUser user = objPartner.getMfinoUser();
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
                
//        		if(StringUtils.isNotBlank(objPartner.getAuthorizedEmail())) {
//        			objSubscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS|CmFinoFIX.NotificationMethod_Email);
//        		} else {
//        			objSubscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
//        		}
//        		subscriberDao.save(objSubscriber);
                
                if(entry.getAuthorizedEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
                	mailService.generateEmailVerificationMail(objSubscriber, entry.getAuthorizedEmail());					
				}
                updateMessage(objPartner, objSubscriber, objSubscriberMdn, entry);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        
			
		}else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			CmFinoFIX.CMJSAgentError errorMsg = new CmFinoFIX.CMJSAgentError();
			if("agentprimarydata".equals(realMsg.getTypeAgentObject()))
			{
				log.info("entered into agentprimarydata block");
				
				KtpDetails ktpDetail = new KtpDetails();
				ktpDetail.setMdn(realMsg.getMDN());
				ktpDetail.setKtpid(realMsg.getKTPID());
				ktpDetail.setFullname(realMsg.getUsername());
/*				ktpDetail.setBankResponseStatus("00");
				ktpDetail.setBankResponse("Success");*/
				
				//errorMsg = (CMJSAgentError)verifyAgentData(errorMsg,ktpDetail);
				errorMsg = (CMJSAgentError)verifyAgentDataFromWS(errorMsg,realMsg,ktpDetail);

                return errorMsg;
			}
/*          CMJSAgent.CGEntries[] entries = realMsg.getEntries();
            for (CMJSAgent.CGEntries e : entries) {*/
				log.info("entered into agent data add block");            	
            	Partner partner = new Partner();
                Subscriber subscriber;
                SubscriberMdn subscriberMdn;
                SubscriberMdn existingSubscriberMDN = null;  
                PartnerQuery query = null;
                //CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
            	PartnerDAO dao = DAOFactory.getInstance().getPartnerDAO();
            	
            	int agntTyp = realMsg.getBusinessPartnerType();
                if(agntTyp == 4)
                {
                	realMsg.setAgentType("1");                	
                }
                if(agntTyp == 5)
                {
                	realMsg.setAgentType("2");  
                }
            	
        		query = new PartnerQuery();
        		query.setBranchCode(realMsg.getBranchCode());
        		query.setBusinessPartnerType(realMsg.getBusinessPartnerType());
        		Partner prtnr = dao.getBranchSequence(query);
        		Integer branchSeq = null;
        		Integer seq = 0;
        		if(null != prtnr){
	    			seq = prtnr.getBranchsequence().intValue();
        		}
    			if(null != seq && seq >= 0){
    				branchSeq = seq + 1;
    			}else{
    				branchSeq = 1;
    			}
    			String bc = StringUtils.leftPad(realMsg.getBranchCode(), 3, "0");
    			String bs = StringUtils.leftPad(""+branchSeq, 4, "0");
                //String agentCode = "153"+realMsg.getBranchCode()+realMsg.getAgentType() + branchSeq;
    			String agentCode = "153" + bc + realMsg.getAgentType() + bs;

                realMsg.setPartnerCode(agentCode);
                realMsg.setTradeName(realMsg.getUsername());
                realMsg.setAuthorizedEmail(realMsg.getEMail());
                //realMsg.setLanguage(CmFinoFIX.Language_English);
                realMsg.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
                realMsg.setCurrency(CmFinoFIX.Currency_IDR);
                realMsg.setTimezone(ConfigurationUtil.getLocalTimeZone().getDisplayName());
                realMsg.setGroupID("1");
                realMsg.setGroupName("ANY");
                realMsg.setPartnerStatus(CmFinoFIX.MDNStatus_Initialized);
                
/*                if(StringUtils.isBlank(e.getTradeName())){
                	WebContextError.addError(errorMsg);
                	errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                	errorMsg.setErrorDescription(MessageText._("MDN already registered as Partner or Agent"));
        			throw new InvalidMDNException(MessageText._("MDN already registered as Partner or Agent"));  
                }*/
                
                if(realMsg.getBusinessPartnerType()<0){
                	errorMsg.setErrorDescription(MessageText._("Set Type for Partner"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;
                }
                
                try
                {
                	validateBusinessPartnerTypesp(realMsg);
                }
                catch(Exception ex)
                {
                	errorMsg.setErrorDescription(ex.getMessage());
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;
                }
                
                if(StringUtils.isBlank(realMsg.getUsername())){
                	errorMsg.setErrorDescription(MessageText._("User name Required"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg; 
                }
                
                if(StringUtils.isBlank(realMsg.getMDN())){
                	errorMsg.setErrorDescription(MessageText._("MDN Required"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg; 
                }
               
        		query = new PartnerQuery();
        		query.setPartnerCode(realMsg.getPartnerCode());
        		List<Partner> results = dao.get(query);
        		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
                     errorMsg.setErrorDescription(MessageText._("Partner Code already exists in DB, please enter different Partner Code."));
                     errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                     return errorMsg;
        		}
        		
        		String tr=realMsg.getTradeName();
        		Partner p = dao.getPartnerByTradeName(tr);
        		if(p!= null && tr.equals(p.getTradename())) {
        			errorMsg.setErrorDescription(MessageText._("Trade name exists"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;        			
        		}
        	
        		String username = agentCode;
                log.info("User name = " + username);
                MfinoUser user = userDAO.getByUserName(username);
                if (user != null) {
                    // username already in use. So skip adding and report failure
                    errorMsg.setErrorDescription(String.format("Username %s is not available", username));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    errorMsg.setsuccess(CmFinoFIX.Boolean_False);
                    log.info("Username already exist in DB " + username);
                    return errorMsg;
                }
                existingSubscriberMDN=subscriberMdnDao.getByMDN(subscriberService.normalizeMDN(realMsg.getMDN()));
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
              

                MfinoUser u = new MfinoUser();
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
                u.setEmail(realMsg.getAuthorizedEmail());
                u.setFirstname(findFirstName(realMsg));
                u.setLastname(findLastName(realMsg));
                u.setLanguage(realMsg.getLanguage());
                u.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
                u.setStatus(CmFinoFIX.UserStatus_Registered);
                u.setTimezone(realMsg.getTimezone());
                u.setRole(partnerService.getRole(realMsg.getBusinessPartnerType()).longValue());
                u.setBranchcodeid(Long.valueOf(realMsg.getBranchCode()));
                subscriber.setType(CmFinoFIX.SubscriberType_Partner);
                KycLevel kycLevel = kyclevelDao.getByKycLevel(ConfigurationUtil.getBulkUploadSubscriberKYClevel());
                subscriber.setKycLevel(kycLevel);
                subscriber.setUpgradablekyclevel(null);
                subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Upgradable);
                subscriber.setRegistrationmedium(CmFinoFIX.RegistrationMedium_AdminApp);
                //subscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS|CmFinoFIX.NotificationMethod_Email);
                subscriberMdn.setOtp(null);
                subscriberMdn.setDigestedpin(null);
                subscriberMdn.setAuthorizationtoken(null);
                updateEntitysp(partner, subscriber, subscriberMdn, realMsg);
                subscriberMdn.setSubscriber(subscriber);
                partner.setSubscriber(subscriber);
                partner.setMfinoUser(u);
                subscriber.setLastname(findLastName(realMsg));
                subscriber.setAppliedby(userService.getCurrentUser().getUsername());
                subscriber.setAppliedtime(new Timestamp());
                subscriber.setApprovedorrejectedby("");
                subscriber.setApproveorrejectcomment("");
                subscriber.setApproveorrejecttime(null);
                partner.setBranchsequence(branchSeq.longValue());
                
                userDAO.save(u);                                               
                subscriberDao.save(subscriber);
                subscriberMdnDao.save(subscriberMdn);
                partnerDao.save(partner);
                
                Long subid = subscriberMdn.getId().longValue();
                
                int cifnoLength = systemParametersService.getInteger(SystemParameterKeys.LAKUPANDIA_SUBSCRIBER_CIFNO_LENGTH);
        		String cifnoPrefix = systemParametersService.getString(SystemParameterKeys.LAKUPANDIA_SUBSCRIBER_PREFIX_CIFNO);
        		
        		if((cifnoPrefix.length() + String.valueOf(subid).length()) >= cifnoLength) {
        			
        			errorMsg.setErrorDescription(MessageText._("Agent Creation Failed..."));
    				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
    				
    				log.warn("Agent Creation Failed..." + subscriberMdn.getId());
    				
    				return errorMsg;
        		}
        		
        		String cifno = cifnoPrefix + StringUtils.leftPad(String.valueOf(subid),(cifnoLength - cifnoPrefix.length()),"0");
        		
        		subscriberMdn.setApplicationid(cifno);
        		
        		subscriberMdnDao.save(subscriberMdn);
                
                updateEntityAddInfosp(subscriber,realMsg);
                
        		if(StringUtils.isNotBlank(partner.getAuthorizedemail())) {
        			subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS|CmFinoFIX.NotificationMethod_Email);
        		} else {
        			subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS);
        		}
        		subscriberDao.save(subscriber);
        		
                if(realMsg.getAuthorizedEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
                	mailService.generateEmailVerificationMail(subscriber, realMsg.getAuthorizedEmail());
				}
                List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(new BigDecimal(subscriber.getId()));
				if(subscriberGroups != null && subscriberGroups.size() > 0){
					for(SubscriberGroups sg: subscriberGroups){
						subscriberGroupDao.save(sg);
					}
				}
				updateMessagesp(partner,subscriber,subscriberMdn, realMsg);
				
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
				if (StringUtils.isNotEmpty(realMsg.getGroupID())) {
					groupID = Long.valueOf(realMsg.getGroupID());
				}
				StringBuilder  errDescription = new StringBuilder();
				boolean isError = false;
                
				boolean isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
				if(isEMoneyPocketRequired == true){
				
					PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, realMsg.getBusinessPartnerType(), groupID);
                
					if (svaPocketTemplate == null) {
						
						log.info("No Default SVA Pocket template set for " + subscriber.getId());
						isError = true;
						errDescription.append("No Default SVA pocket template set for this group and partner type<br/>");
						
					} else {
	                	
						if(epocket!=null&&!(Integer.valueOf(Long.valueOf(epocket.getStatus()).intValue()).equals(CmFinoFIX.PocketStatus_PendingRetirement)||Integer.valueOf(Long.valueOf(epocket.getStatus()).intValue()).equals(CmFinoFIX.PocketStatus_Retired))){
	                		epocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
	                		epocket.setStatustime(new Timestamp());
	                		epocket.setPocketTemplateByOldpockettemplateid(epocket.getPocketTemplateByPockettemplateid());
	                		epocket.setPocketTemplateByPockettemplateid(svaPocketTemplate);
	                		epocket.setPockettemplatechangedby(userService.getCurrentUser().getUsername());
	                		epocket.setPockettemplatechangetime(new Timestamp());
	                		pocketDao.save(epocket);
	
	                	}else{
	                		try{
	                		epocket = pocketService.createPocket(svaPocketTemplate, subscriberMdn, CmFinoFIX.PocketStatus_Initialized, true, cardPan);
	                		}catch(Exception ex){
	                			log.error("Exception in creating pocket",ex);
	                		}
	                		log.info("Default emoney pocket successfully created for the partner -->"+partner.getId());
	                	}
	                }
				}
				
                Pocket collectorPocket = null;
                PocketTemplate collectorPocketTemplate = null;
                try {
                	collectorPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, false, true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, realMsg.getBusinessPartnerType(), groupID);
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
                	suspensePocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, true, false, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, realMsg.getBusinessPartnerType(), groupID);
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
                
				Pocket bankPocket = null;
				PocketTemplate bankPocketTemplate = null;
                try {
                	 String bankCardPan=realMsg.getAccountnumberofBankSinarmas();
                	bankPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(),
                			true, CmFinoFIX.PocketType_BankAccount, CmFinoFIX.SubscriberType_Partner, realMsg.getBusinessPartnerType(), groupID);
        			if (bankPocketTemplate == null) {
        				errorMsg.setErrorDescription(MessageText._("No Default Bank Pocket set for this KYC"));
        				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        				log.warn("No Default Bank Pocket set for " + subscriberMdn.getId());
        				return errorMsg;
        			}
        			if(bankPocketTemplate.getId().longValue() >= 0 && bankCardPan != null)
        			{
        				boolean isallowed=pocketService.checkCount(bankPocketTemplate,subscriberMdn);
        				if(!isallowed){
        					log.error("PocketProcessor :: Pocket count limit reached for template:"+bankPocketTemplate.getDescription()+" for MDN:"+subscriberMdn.getMdn()+" by user:"+getLoggedUserNameWithIP());	
        					return getErrorMessage(MessageText._(" Pocket count Limit reached for this template  "), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Pocket count Limit reached for this template"));  		
        				}           

        				bankPocket = pocketService.createDefaultBankPocket(bankPocketTemplate.getId().longValue(), subscriberMdn, bankCardPan);
        				if(bankPocket==null){
        					errorMsg.setErrorDescription(MessageText._("Default Bank Pocket creation failed for the Subscriber"));
        					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        					log.info("Default Bank Pocket creation failed for Subscriber "+subscriberMdn.getId());
        					return errorMsg;
        				}
        			}
				} catch (Exception e1) {
					log.info("Default Bank Pocket creation failed for the Partner --> " + partner.getId());
					isError = true;
					errDescription.append("Default Bank Pocket creation failed for the Partner<br/>");
				}
                
                Pocket lakuPocket = null;
                PocketTemplate lakuPocketTemplate = null;
                try {
                	
                	lakuPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, false, false, CmFinoFIX.PocketType_LakuPandai, CmFinoFIX.SubscriberType_Partner, realMsg.getBusinessPartnerType(), groupID);
                	
                	if(lakuPocketTemplate == null) {
                		
                		log.error("There is no laku pocket configured for the Partner type and group");
						isError = true;
                		errDescription.append("There is no Laku pocket configured for this Partner type and group<br/>" );
                		
					} else {
						
                		String lakuPocketCardPan = pocketService.generateLakupandia16DigitCardPAN(subscriberMdn.getMdn());
                		lakuPocket = pocketService.createPocket(lakuPocketTemplate, subscriberMdn, CmFinoFIX.PocketStatus_Initialized, true, lakuPocketCardPan);
                		log.info("Default collector pocket Id --> " + lakuPocket.getId());
                	}
                	
				} catch (Exception e1) {
					
					log.info("Default Laku Pocket creation is failed for the Partner --> " + partner.getId());
					isError = true;
					errDescription.append("Default Laku Pocket creation failed for the Partner<br/>");
				}
                
                
                if(isError)
                {
                	realMsg.setErrorDescription(errDescription.toString());
                	//return realMsg;
                	
                	 errorMsg.setsuccess(CmFinoFIX.Boolean_True);
                	 errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
	           		 errorMsg.setErrorDescription(errDescription.toString());
                	 //errorMsg.setErrorDescription("Agent Created Successfully");
                	 log.info("ServicePartnerProcessorspImpl::process() method in ADD block END");
	                 return errorMsg;
                }	                
           // }            

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            //realMsg.settotal(entries.length);
            
            errorMsg.setsuccess(CmFinoFIX.Boolean_True);
            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            errorMsg.setErrorDescription("Agent Created Successfully");
            
    		log.info("ServicePartnerProcessorspImpl::process() method in ADD block END");
    		return errorMsg;
            
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
				partnerQuery.setPartnerCodeLike(Boolean.TRUE);
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
			
			if (realMsg.getStartDateSearch() != null) {
				
				partnerQuery.setLastUpdateTimeGE(realMsg.getStartDateSearch());
			}
			
			if (realMsg.getEndDateSearch() != null) {
				
				partnerQuery.setLastUpdateTimeLT(realMsg.getEndDateSearch());
			}
			
            List<Partner> results = partnerDao.get(partnerQuery);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                Partner objPartner = results.get(i);
                Subscriber objSubscriber = objPartner.getSubscriber();
                SubscriberMdn objSubscriberMdn = ((objSubscriber != null) && (objSubscriber.getSubscriberMdns().size() > 0))? objSubscriber.getSubscriberMdns().iterator().next() : null;

                CMJSAgent.CGEntries entry = new CMJSAgent.CGEntries();

                updateMessage(objPartner, objSubscriber, objSubscriberMdn, entry);
                realMsg.getEntries()[i] = entry;
            }
            
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(partnerQuery.getTotal());			
		}
		
		log.info("ServicePartnerProcessorspImpl::process() method END");
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
	private String findLastName(CMJSAgent e) 
	{
		return e.getLastName()!=null?e.getLastName():" ";
	}

	/**
	 * find the firstname that need to be set for the subssriber
	 *  use tradename if firstname is not defined.
	 * @param e
	 * @return
	 */
    private String findFirstName(CMJSAgent e) 
    {
    	return StringUtils.isNotBlank(e.getFirstName())?e.getFirstName():e.getTradeName();
	}

	private void updateEntitysp(Partner partner,Subscriber subscriber, SubscriberMdn subscriberMdn,CMJSAgent entry) {
    	
    	/*TODO 
    	 * Not sure if this is reqd for creating partner, setting default values
    	 * */
    	if(entry.getMDN()!=null){
    		subscriberMdn.setMdn(subscriberService.normalizeMDN(entry.getMDN()));
    	}
    	if(entry.getKTPID()!= null){
    		subscriberMdn.setKtpid(entry.getKTPID());
    	}
    	if(entry.getApplicationID()!= null){
    		subscriberMdn.setApplicationid(entry.getApplicationID());
    	}
        if (entry.getPartnerStatus() != null) {
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
        } else {
        	subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
        }
    
        if (entry.getTimezone() != null) {
            subscriber.setTimezone(entry.getTimezone());
        }
        if (entry.getCurrency() != null) {
            subscriber.setCurrency(entry.getCurrency());
        }
      	if(entry.getPlaceofBirth()!= null){
      		subscriber.setBirthplace(entry.getPlaceofBirth());
    	}
    	if(entry.getDateofBirth()!= null){
    		subscriber.setDateofbirth(new Timestamp(getDate(entry.getDateofBirth())));
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
        if(entry.getCompanyEmailId() != null){
        	partner.setCompanyemailid(entry.getCompanyEmailId());
        }
        
        partner.setMfinoServiceProvider(mspDAO.getById(1));
        
        if(entry.getTradeName() != null){
        	partner.setTradename(entry.getTradeName());
        	  subscriber.setFirstname(entry.getTradeName());
         }
        if (entry.getRestrictions() != null) {
        	subscriber.setRestrictions(entry.getRestrictions());
        	subscriberMdn.setRestrictions(entry.getRestrictions());
        }
        
		if(entry.getClassificationAgent() != null){
        	partner.setClassification(entry.getClassificationAgent());
        }
        if(entry.getTypeofBusinessAgent() != null){
        	partner.setTypeoforganization(entry.getTypeofBusinessAgent());
        }
        if(entry.getPhoneNumber() != null){
        	partner.setFranchisephonenumber(entry.getPhoneNumber());
        }
        if(entry.getBranchCode() != null){
        	partner.setBranchcode(entry.getBranchCode());
        }
        if(entry.getAccountnumberofBankSinarmas() != null){
        	partner.setAccountnumberofbanksinarmas(entry.getAccountnumberofBankSinarmas());
        }
        
        //for merchant and outlet addresses
        Address merchantAddress = partner.getAddressByMerchantaddressid();        	
        	if(merchantAddress == null){
        		merchantAddress = new Address();
        		partner.setAddressByMerchantaddressid(merchantAddress);
           	}
       	
        	if(entry.getAlamatInAccordanceIdentity()!= null){
        	merchantAddress.setLine1(entry.getAlamatInAccordanceIdentity());
        	}
        	if(entry.getRTAl()!= null){
        	merchantAddress.setRt(entry.getRTAl());
        	}
        	if(entry.getRWAl()!= null){
        	merchantAddress.setRw(entry.getRWAl());
        	}
        	if(entry.getProvincialAl()!= null){
        	merchantAddress.setState(entry.getProvincialAl());
        	}
        	if(entry.getCityAl()!= null){
        	merchantAddress.setRegionname(entry.getCityAl());
        	}
        	if(entry.getDistrictAl()!= null){
        	merchantAddress.setSubstate(entry.getDistrictAl());
        	}
        	if(entry.getVillageAl()!= null){
        	merchantAddress.setCity(entry.getVillageAl());
        	}
        	if(entry.getPotalCodeAl()!= null){
        	merchantAddress.setZipcode(entry.getPotalCodeAl());
        	}

        	addressDao.save(merchantAddress);
        	        
        	//outlet address
           	Address outletAddress = partner.getAddressByFranchiseoutletaddressid();
            	if(outletAddress == null){
            		outletAddress = new Address();
            		partner.setAddressByFranchiseoutletaddressid(outletAddress);
            	}
            	if(entry.getCompanyAddress()!= null){
            		outletAddress.setLine1(entry.getCompanyAddress());
            	}
            	if(entry.getRTCom()!= null){
            		outletAddress.setRt(entry.getRTCom());
            	}
            	if(entry.getRWCom()!= null){
            		outletAddress.setRw(entry.getRWCom());
            	}
            	if(entry.getProvincialCom()!= null){
            		outletAddress.setState(entry.getProvincialCom());
            	}
            	if(entry.getCityCom()!= null){
            		outletAddress.setRegionname(entry.getCityCom());
            	}
            	if(entry.getDistrictCom()!= null){
            		outletAddress.setSubstate(entry.getDistrictCom());
            	}
            	if(entry.getVillageCom()!= null){
            		outletAddress.setCity(entry.getVillageCom());
            	}
            	if(entry.getPotalCodeCom()!= null){
            		outletAddress.setZipcode(entry.getPotalCodeCom());
            	}

            	addressDao.save(outletAddress);
            	
        		if((null != entry.getGroupID()) && !("".equals(entry.getGroupID()))){
        			
        			List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(new BigDecimal(subscriber.getId()));
        			if((subscriberGroups != null) && (subscriberGroups.size() > 0)){
        				SubscriberGroups sg = subscriberGroups.iterator().next();
        				if(sg.getGroupid() != Long.valueOf(entry.getGroupID()).longValue()){
        					Groups group = (Groups)groupDao.getById(Long.valueOf(entry.getGroupID()));
        					sg.setGroupid(group.getId().longValue());
        					subscriberGroupDao.save(sg);
        				}
        			}
        			else{
        				Groups group = (Groups)groupDao.getById(Long.valueOf(entry.getGroupID()));
        				SubscriberGroups sg = new SubscriberGroups();
        				sg.setSubscriberid(subscriber.getId().longValue());
        				sg.setGroupid(group.getId().longValue());
        				
        				if(subscriber.getId() != null){
        					subscriberGroupDao.save(sg);
        				}
        			}
        		}
        }
    
	private void updateEntity(Partner partner,Subscriber subscriber, SubscriberMdn subscriberMdn,CMJSAgent.CGEntries entry) {
    	
    	/*TODO 
    	 * Not sure if this is reqd for creating partner, setting default values
    	 * */
    	if(entry.getMDN()!=null){
    		subscriberMdn.setMdn(subscriberService.normalizeMDN(entry.getMDN()));
    	}
    	if(entry.getKTPID()!= null){
    		subscriberMdn.setApplicationid(entry.getKTPID());
    	}
        if (entry.getPartnerStatus() != null) {
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
        } else {
        	subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
        }
    
        if (entry.getTimezone() != null) {
            subscriber.setTimezone(entry.getTimezone());
        }
        if (entry.getCurrency() != null) {
            subscriber.setCurrency(entry.getCurrency());
        }
      	if(entry.getPlaceofBirth()!= null){
      		subscriber.setBirthplace(entry.getPlaceofBirth());
    	}
    	if(entry.getDateofBirth()!= null){
    		subscriber.setDateofbirth(new Timestamp(getDate(entry.getDateofBirth())));
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
        if(entry.getCompanyEmailId() != null){
        	partner.setCompanyemailid(entry.getCompanyEmailId());
        }
        
        partner.setMfinoServiceProvider(mspDAO.getById(1));
        
        if(entry.getTradeName() != null){
        	partner.setTradename(entry.getTradeName());
        	  subscriber.setFirstname(entry.getTradeName());
         }
        if (entry.getRestrictions() != null) {
        	subscriber.setRestrictions(entry.getRestrictions());
        	subscriberMdn.setRestrictions(entry.getRestrictions());
        }
        
		if(entry.getClassificationAgent() != null){
        	partner.setClassification(entry.getClassificationAgent());
        }
        if(entry.getTypeofBusinessAgent() != null){
        	partner.setTypeoforganization(entry.getTypeofBusinessAgent());
        }
        if(entry.getPhoneNumber() != null){
        	partner.setFranchisephonenumber(entry.getPhoneNumber());
        }
        if(entry.getBranchCode() != null){
        	partner.setBranchcode(entry.getBranchCode());
        }
        if(entry.getAccountnumberofBankSinarmas() != null){
        	partner.setAccountnumberofbanksinarmas(entry.getAccountnumberofBankSinarmas());
        }
        
        //for merchant and outlet addresses
        Address merchantAddress = partner.getAddressByMerchantaddressid();        	
        	if(merchantAddress == null){
        		merchantAddress = new Address();
        		partner.setAddressByMerchantaddressid(merchantAddress);
           	}
       	
        	if(entry.getAlamatInAccordanceIdentity()!= null){
        	merchantAddress.setLine1(entry.getAlamatInAccordanceIdentity());
        	}
        	if(entry.getRTAl()!= null){
        	merchantAddress.setRt(entry.getRTAl());
        	}
        	if(entry.getRWAl()!= null){
        	merchantAddress.setRw(entry.getRWAl());
        	}
        	if(entry.getProvincialAl()!= null){
        	merchantAddress.setState(entry.getProvincialAl());
        	}
        	if(entry.getCityAl()!= null){
        	merchantAddress.setRegionname(entry.getCityAl());
        	}
        	if(entry.getDistrictAl()!= null){
        	merchantAddress.setSubstate(entry.getDistrictAl());
        	}
        	if(entry.getVillageAl()!= null){
        	merchantAddress.setCity(entry.getVillageAl());
        	}
        	if(entry.getPotalCodeAl()!= null){
        	merchantAddress.setZipcode(entry.getPotalCodeAl());
        	}

        	addressDao.save(merchantAddress);
        	        
        	//outlet address
           	Address outletAddress = partner.getAddressByFranchiseoutletaddressid();
            	if(outletAddress == null){
            		outletAddress = new Address();
            		partner.setAddressByFranchiseoutletaddressid(outletAddress);
            	}
            	if(entry.getCompanyAddress()!= null){
            		outletAddress.setLine1(entry.getCompanyAddress());
            	}
            	if(entry.getRTCom()!= null){
            		outletAddress.setRt(entry.getRTCom());
            	}
            	if(entry.getRWCom()!= null){
            		outletAddress.setRw(entry.getRWCom());
            	}
            	if(entry.getProvincialCom()!= null){
            		outletAddress.setState(entry.getProvincialCom());
            	}
            	if(entry.getCityCom()!= null){
            		outletAddress.setRegionname(entry.getCityCom());
            	}
            	if(entry.getDistrictCom()!= null){
            		outletAddress.setSubstate(entry.getDistrictCom());
            	}
            	if(entry.getVillageCom()!= null){
            		outletAddress.setCity(entry.getVillageCom());
            	}
            	if(entry.getPotalCodeCom()!= null){
            		outletAddress.setZipcode(entry.getPotalCodeCom());
            	}

            	addressDao.save(outletAddress);
            	
        		if((null != entry.getGroupID()) && !("".equals(entry.getGroupID()))){
        			
        			List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(new BigDecimal(subscriber.getId()));
        			if((subscriberGroups != null) && (subscriberGroups.size() > 0)){
        				SubscriberGroups sg = subscriberGroups.iterator().next();
        				if(sg.getGroupid() != Long.valueOf(entry.getGroupID()).longValue()){
        					Groups group = (Groups)groupDao.getById(Long.valueOf(entry.getGroupID()));
        					sg.setGroupid(group.getId().longValue());
        					subscriberGroupDao.save(sg);
        				}
        			}
        			else{
        				Groups group = (Groups)groupDao.getById(Long.valueOf(entry.getGroupID()));
        				SubscriberGroups sg = new SubscriberGroups();
        				sg.setSubscriberid(subscriber.getId().longValue());
        				sg.setGroupid(group.getId().longValue());
        				
        				if(subscriber.getId() != null){
        					subscriberGroupDao.save(sg);
        				}
        			}
        		}            	
        		
        		if(entry.getNotificationMethod()!=null){
					subscriber.setNotificationmethod(entry.getNotificationMethod());
				}
        		
    			if (entry.getSecurityQuestion() != null) {
    				subscriber.setSecurityquestion(entry.getSecurityQuestion());
				}
	
				if (entry.getAuthenticationPhrase() != null) {
					subscriber.setSecurityanswer(entry.getAuthenticationPhrase());
				}
				
        }
    
    private void updateMessagesp(Partner partner,Subscriber subscriber, SubscriberMdn subscriberMdn,  CMJSAgent entry) {

        if(partner != null){
        	if(subscriberMdn.getMdn()!=null){
        		entry.setMDN(subscriberMdn.getMdn());
        		entry.setMobilePhoneNumber(subscriberMdn.getMdn());
        	}
	    	if(subscriberMdn.getKtpid()!= null){
	    		entry.setKTPID(subscriberMdn.getKtpid());
	    	}
	    	if(subscriberMdn.getApplicationid()!= null){
	    		entry.setApplicationID(subscriberMdn.getApplicationid());
	    	}
			if(! subscriber.getSubscriberAddiInfos().isEmpty()){
				SubscriberAddiInfo saf=subscriber.getSubscriberAddiInfos().iterator().next();
				
				if(saf.getElectonicdeviceused()!= null){
					entry.setElectonicDevieusedText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ElectonicDevieused, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), saf.getElectonicdeviceused()));
		    		entry.setElectonicDevieused(String.valueOf(saf.getElectonicdeviceused()));
		    	}
		    	if(saf.getAgreementnumber()!= null){
		    		entry.setAgreementNumber(saf.getAgreementnumber());
		    	}
		    	if(saf.getAgrementdate()!= null){
		    		entry.setAgreementDate(String.valueOf(saf.getAgrementdate()));
		    	}
		    	if(saf.getImplementatindate()!= null){
		    		entry.setImplementationdate(String.valueOf(saf.getImplementatindate()));
		    	}
		    	if(saf.getAgentcompanyname()!= null){
		    		entry.setAgentCompanyName(saf.getAgentcompanyname());
		    	}
		    	if(saf.getLatitude()!= null){
		    		entry.setLatitude(saf.getLatitude());
		    	}
		    	if(saf.getLongitude()!= null){
		    		entry.setLongitude(saf.getLongitude());
		    	}
		    	if(saf.getUserbankbranch()!= null){
		    		entry.setUserBankBranch(saf.getUserbankbranch());
		    	}
		    	if(saf.getBankacountstatus()!= null){
		    		entry.setBankAccountStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankAccountStatus, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), saf.getBankacountstatus()));
		    		entry.setBankAccountStatus(String.valueOf(saf.getBankacountstatus()));
		    	}
			}
        	
	        if(partner.getAddressByFranchiseoutletaddressid() != null){
	        	Address outletAddress = partner.getAddressByFranchiseoutletaddressid();
	        	
	        	if(outletAddress.getLine1() != null){
	        		entry.setCompanyAddress(outletAddress.getLine1());
	        	}
	        	if(outletAddress.getRt() != null){
	        		entry.setRTCom(outletAddress.getRt());
	        	}
	        	if(outletAddress.getRw() != null){
	        		entry.setRWCom(outletAddress.getRw());
	        	}
	        	if(outletAddress.getState() != null){
	        		String province = provinceDAO.getProvinceName(outletAddress.getState());
	        		entry.setProvincialCom(province);
	        	}
	        	if(outletAddress.getRegionname() != null){
	        		String region = provinceRegionDAO.getProvinceRegionName(outletAddress.getRegionname());
	        		entry.setCityCom(region);
	        	}
	        	if(outletAddress.getSubstate() != null){
	        		String district = districtDAO.getDistrictName(outletAddress.getSubstate());
	        		entry.setDistrictCom(district);
	        	}
	        	if(outletAddress.getCity() != null){
	        		String village = villageDAO.getVillageName(outletAddress.getCity());
	        		entry.setVillageCom(village);
	        	}
	        	if(outletAddress.getZipcode() != null){
	        		entry.setPotalCodeCom(outletAddress.getZipcode());
	        	}
	        }
	        if(partner.getAddressByMerchantaddressid() != null){
	        	Address merchantAddress = partner.getAddressByMerchantaddressid();
	        	
	        	if(merchantAddress.getLine1() != null){
	        		entry.setAlamatInAccordanceIdentity(merchantAddress.getLine1());
	        	}
	        	if(merchantAddress.getRt() != null){
	        		entry.setRTAl(merchantAddress.getRt());
	        	}
	        	if(merchantAddress.getRw() != null){
	        		entry.setRWAl(merchantAddress.getRw());
	        	}
	        	if(merchantAddress.getState() != null){
	        		entry.setProvincialAl(merchantAddress.getState());
	        	}
	        	if(merchantAddress.getRegionname() != null){
	        		entry.setCityAl(merchantAddress.getRegionname());
	        	}
	        	if(merchantAddress.getSubstate() != null){
	        		entry.setDistrictAl(merchantAddress.getSubstate());
	        	}
	        	if(merchantAddress.getCity() != null){
	        		entry.setVillageAl(merchantAddress.getCity());
	        	}
	        	if(merchantAddress.getZipcode() != null){
	        		entry.setPotalCodeAl(merchantAddress.getZipcode());
	        	}
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
	        	entry.setAgentCode(partner.getPartnercode());
	        }
	        if(partner.getPartnerstatus() != 0){
	        	entry.setPartnerStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerStatus, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), partner.getPartnerstatus()));
	        	entry.setPartnerStatus(Integer.valueOf(Long.valueOf(partner.getPartnerstatus()).intValue()));
	        }
	        if(partner.getTradename() != null){
	        	entry.setTradeName(partner.getTradename());
	        }
	        if(partner.getTypeoforganization() != null){
	        	entry.setTypeofBusinessAgentText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TypeofBusinessAgent, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), partner.getTypeoforganization()));
	        	entry.setTypeOfOrganization(partner.getTypeoforganization());
	        	entry.setTypeofBusinessAgent(partner.getTypeoforganization());
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
	        	entry.setPhoneNumber(partner.getFranchisephonenumber());
	        }
	        if(partner.getClassification() != null){
	        	entry.setClassificationAgentText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ClassificationAgent, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), partner.getClassification()));
	        	entry.setClassification(partner.getClassification());
	        	entry.setClassificationAgent(partner.getClassification());
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
	        	entry.setEMail(partner.getAuthorizedemail());
	        }
	        if(partner.getCompanyemailid() != null){
	        	entry.setCompanyEmailId(partner.getCompanyemailid());
	        }
	        if (partner.getVersion() != 0) {
	            entry.setRecordVersion(Integer.valueOf(Long.valueOf(partner.getVersion()).intValue()));
	        }
	        if (partner.getBranchcode() != null) {
	        	entry.setBranchCodeText(userService.getUserBranchCode(Integer.valueOf(partner.getBranchcode())));
	            entry.setBranchCode(partner.getBranchcode());
	        }
	        if (partner.getAccountnumberofbanksinarmas() != null) {
	            entry.setAccountnumberofBankSinarmas(partner.getAccountnumberofbanksinarmas());
	        }
	        if(partner.getBusinesspartnertype() != null){
	        	entry.setBusinessPartnerTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BusinessPartnerTypeAgent, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), partner.getBusinesspartnertype()));
	        	entry.setBusinessPartnerType(partner.getBusinesspartnertype().intValue());
	        	entry.setAgentType(String.valueOf(partner.getBusinesspartnertype()));
	        	entry.setAgentTypeText(entry.getBusinessPartnerTypeText());
	        }
	        entry.setRestrictions(Integer.valueOf(Long.valueOf(subscriberMdn.getRestrictions()).intValue()));
	        entry.setCloseAcctStatus(partner.getCloseacctstatus().intValue());
        }
        if(subscriber != null){
        	if(subscriber.getFirstname() != null){
        		entry.setFirstName(subscriber.getFirstname());
            	entry.setUsername(subscriber.getFirstname());
            	entry.setNameInAccordanceIdentity(subscriber.getFirstname());
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
	        if(subscriber.getBirthplace()!=null){
	        	entry.setPlaceofBirth(subscriber.getBirthplace());
	        }
	        if(subscriber.getDateofbirth()!=null){
	        	entry.setDateofBirth(String.valueOf(subscriber.getDateofbirth()));
	        }
	        List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(new BigDecimal(subscriber.getId()));
			
			if((subscriberGroups != null) && (subscriberGroups.size() > 0)) {
				SubscriberGroups sg = subscriberGroups.iterator().next();
				Groups groups = groupDao.getById(sg.getGroupid().longValue());
				entry.setGroupName(groups.getGroupname());
				entry.setGroupID(""+sg.getGroupid());
			}
        }
/*        if(partner.getUser()!=null){
        	entry.setUsername(partner.getUser().getUsername());
        	entry.setNameInAccordanceIdentity(partner.getUser().getUsername());
        }*/
    }	
	
    private void updateMessage(Partner partner,Subscriber subscriber, SubscriberMdn subscriberMdn,  CMJSAgent.CGEntries entry) {

        if(partner != null){
        	if(subscriberMdn.getMdn()!=null){
        		entry.setMDN(subscriberMdn.getMdn());
        		entry.setMobilePhoneNumber(subscriberMdn.getMdn());
        	}
	    	if(subscriberMdn.getApplicationid()!= null){
	    		entry.setKTPID(subscriberMdn.getApplicationid());
	    	}
			if(! subscriber.getSubscriberAddiInfos().isEmpty()){
				SubscriberAddiInfo saf=subscriber.getSubscriberAddiInfos().iterator().next();
				
				if(saf.getElectonicdeviceused()!= null){
					entry.setElectonicDevieusedText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ElectonicDevieused, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), saf.getElectonicdeviceused()));
		    		entry.setElectonicDevieused(String.valueOf(saf.getElectonicdeviceused()));
		    	}
		    	if(saf.getAgreementnumber()!= null){
		    		entry.setAgreementNumber(saf.getAgreementnumber());
		    	}
		    	if(saf.getAgrementdate()!= null){
		    		entry.setAgreementDate(String.valueOf(saf.getAgrementdate()));
		    	}
		    	if(saf.getImplementatindate()!= null){
		    		entry.setImplementationdate(String.valueOf(saf.getImplementatindate()));
		    	}
		    	if(saf.getAgentcompanyname()!= null){
		    		entry.setAgentCompanyName(saf.getAgentcompanyname());
		    	}
		    	if(saf.getLatitude()!= null){
		    		entry.setLatitude(saf.getLatitude());
		    	}
		    	if(saf.getLongitude()!= null){
		    		entry.setLongitude(saf.getLongitude());
		    	}
		    	if(saf.getUserbankbranch()!= null){
		    		entry.setUserBankBranch(saf.getUserbankbranch());
		    	}
		    	if(saf.getBankacountstatus()!= null){
		    		entry.setBankAccountStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankAccountStatus, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), saf.getBankacountstatus()));
		    		entry.setBankAccountStatus(String.valueOf(saf.getBankacountstatus()));
		    	}
			}
        	
	        if(partner.getAddressByFranchiseoutletaddressid() != null){
	        	Address outletAddress = partner.getAddressByFranchiseoutletaddressid();
	        	
	        	if(outletAddress.getLine1() != null){
	        		entry.setCompanyAddress(outletAddress.getLine1());
	        	}
	        	if(outletAddress.getRt() != null){
	        		entry.setRTCom(outletAddress.getRt());
	        	}
	        	if(outletAddress.getRw() != null){
	        		entry.setRWCom(outletAddress.getRw());
	        	}
	        	if(outletAddress.getState() != null){
	        		String province = provinceDAO.getProvinceName(outletAddress.getState());
	        		entry.setProvincialCom(province);
	        	}
	        	if(outletAddress.getRegionname() != null){
	        		String region = provinceRegionDAO.getProvinceRegionName(outletAddress.getRegionname());
	        		entry.setCityCom(region);
	        	}
	        	if(outletAddress.getSubstate() != null){
	        		String district = districtDAO.getDistrictName(outletAddress.getSubstate());
	        		entry.setDistrictCom(district);
	        	}
	        	if(outletAddress.getCity() != null){
	        		String village = villageDAO.getVillageName(outletAddress.getCity());
	        		entry.setVillageCom(village);
	        	}
	        	if(outletAddress.getZipcode() != null){
	        		entry.setPotalCodeCom(outletAddress.getZipcode());
	        	}
	        }
	        if(partner.getAddressByMerchantaddressid() != null){
	        	Address merchantAddress = partner.getAddressByMerchantaddressid();
	        	
	        	if(merchantAddress.getLine1() != null){
	        		entry.setAlamatInAccordanceIdentity(merchantAddress.getLine1());
	        	}
	        	if(merchantAddress.getRt() != null){
	        		entry.setRTAl(merchantAddress.getRt());
	        	}
	        	if(merchantAddress.getRw() != null){
	        		entry.setRWAl(merchantAddress.getRw());
	        	}
	        	if(merchantAddress.getState() != null){
	        		entry.setProvincialAl(merchantAddress.getState());
	        	}
	        	if(merchantAddress.getRegionname() != null){
	        		entry.setCityAl(merchantAddress.getRegionname());
	        	}
	        	if(merchantAddress.getSubstate() != null){
	        		entry.setDistrictAl(merchantAddress.getSubstate());
	        	}
	        	if(merchantAddress.getCity() != null){
	        		entry.setVillageAl(merchantAddress.getCity());
	        	}
	        	if(merchantAddress.getZipcode() != null){
	        		entry.setPotalCodeAl(merchantAddress.getZipcode());
	        	}
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
	        	entry.setAgentCode(partner.getPartnercode());
	        }
	        if(partner.getPartnerstatus() != 0){
	        	entry.setPartnerStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerStatus, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), partner.getPartnerstatus()));
	        	entry.setPartnerStatus(Integer.valueOf(Long.valueOf(partner.getPartnerstatus()).intValue()));
	        }
	        if(partner.getTradename() != null){
	        	entry.setTradeName(partner.getTradename());
	        }
	        if(partner.getTypeoforganization() != null){
	        	entry.setTypeofBusinessAgentText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TypeofBusinessAgent, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), partner.getTypeoforganization()));
	        	entry.setTypeOfOrganization(partner.getTypeoforganization());
	        	entry.setTypeofBusinessAgent(partner.getTypeoforganization());
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
	        	entry.setPhoneNumber(partner.getFranchisephonenumber());
	        }
	        if(partner.getClassification() != null){
	        	entry.setClassificationAgentText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ClassificationAgent, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), partner.getClassification()));
	        	entry.setClassification(partner.getClassification());
	        	entry.setClassificationAgent(partner.getClassification());
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
	        	entry.setEMail(partner.getAuthorizedemail());
	        }
	        if(partner.getCompanyemailid() != null){
	        	entry.setCompanyEmailId(partner.getCompanyemailid());
	        }
	        if (partner.getVersion() != 0) {
	            entry.setRecordVersion(Integer.valueOf(Long.valueOf(partner.getVersion()).intValue()));
	        }
	        if (partner.getBranchcode() != null) {
	        	entry.setBranchCodeText(userService.getUserBranchCode(Integer.valueOf(partner.getBranchcode())));
	            entry.setBranchCode(partner.getBranchcode());
	        }
	        if (partner.getAccountnumberofbanksinarmas() != null) {
	            entry.setAccountnumberofBankSinarmas(partner.getAccountnumberofbanksinarmas());
	        }
	        if(partner.getBusinesspartnertype() != null){
	        	entry.setBusinessPartnerTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BusinessPartnerTypeAgent, Integer.valueOf(Long.valueOf(subscriber.getLanguage()).intValue()), partner.getBusinesspartnertype()));
	        	entry.setBusinessPartnerType(partner.getBusinesspartnertype().intValue());
	        	entry.setAgentType(String.valueOf(partner.getBusinesspartnertype()));
	        	entry.setAgentTypeText(entry.getBusinessPartnerTypeText());
	        	entry.setCloseAcctStatus(partner.getCloseacctstatus().intValue());
	        }
	        entry.setRestrictions(Integer.valueOf(Long.valueOf(subscriberMdn.getRestrictions()).intValue()));
        }
        if(subscriber != null){
        	if(subscriber.getFirstname() != null){
        		entry.setFirstName(subscriber.getFirstname());
            	entry.setUsername(subscriber.getFirstname());
            	entry.setNameInAccordanceIdentity(subscriber.getFirstname());
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
	        if(subscriber.getBirthplace()!=null){
	        	entry.setPlaceofBirth(subscriber.getBirthplace());
	        }
	        if(subscriber.getDateofbirth()!=null){
	        	entry.setDateofBirth(String.valueOf(subscriber.getDateofbirth()));
	        }
	        List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(new BigDecimal(subscriber.getId()));
			if((subscriberGroups != null) && (subscriberGroups.size() > 0)) {
				Groups group = groupDao.getById(Long.valueOf(entry.getGroupID()));
				SubscriberGroups sg = subscriberGroups.iterator().next();
				entry.setGroupName(group.getGroupname());
				entry.setGroupID(""+sg.getGroupid());
			}
			try{
				
			
				if (subscriber.getSecurityquestion() != null) {
					entry.setSecurityQuestion(subscriber.getSecurityquestion());
				}
	
				if (subscriber.getSecurityanswer() != null) {
					entry.setAuthenticationPhrase(subscriber.getSecurityanswer());
				}
				if(subscriber.getNotificationmethod()!=null){
					entry.setNotificationMethod(subscriber.getNotificationmethod().intValue());
				}
				
			}catch(Exception e){
				log.error("Error :"+e.getMessage(),e);
			}
        }
        if(partner.getPartnerstatus()!=0){
        	entry.setPartnerStatus(Integer.valueOf(Long.valueOf(partner.getPartnerstatus()).intValue()));
        }
        
/*        if(partner.getUser()!=null){
        	entry.setUsername(partner.getUser().getUsername());
        	entry.setNameInAccordanceIdentity(partner.getUser().getUsername());
        }*/
    }

    private void generateAndSendOTP(Partner partner, SubscriberMdn subscriberMDN, Subscriber subscriber) {
        MfinoUser u =partner.getMfinoUser();
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

    private void validateBusinessPartnerType(CMJSAgent.CGEntries entry) throws Exception
    {
    	List<Partner> entries = DAOFactory.getInstance().getPartnerDAO().getAll();
		Iterator<Partner> it = entries.iterator();
		while(it.hasNext())
		{
			Partner existingPartner = it.next();
			
			if(CmFinoFIX.BusinessPartnerType_ServicePartner.equals(entry.getBusinessPartnerType()) && (entry!= null && !(entry.getID().equals(existingPartner.getId()))))
			{				
				throw new Exception("Service Partner already defined.");
			}
		}
    	
    }
    
    private void validateBusinessPartnerTypesp(CMJSAgent entry) throws Exception
    {
    	List<Partner> entries = DAOFactory.getInstance().getPartnerDAO().getAll();
		Iterator<Partner> it = entries.iterator();
		while(it.hasNext())
		{
			Partner existingPartner = it.next();
			
			if(CmFinoFIX.BusinessPartnerType_ServicePartner.equals(entry.getBusinessPartnerType()) && (entry!= null && !(entry.getID().equals(existingPartner.getId()))))
			{				
				throw new Exception("Service Partner already defined.");
			}
		}
    }
    
    private void updateEntityAddInfosp(Subscriber sub, CMJSAgent entry){
 		SubscriberAddiInfo addnlData; 
 		if (CollectionUtils.isNotEmpty(sub.getSubscriberAddiInfos())) {
 			addnlData = sub.getSubscriberAddiInfos().iterator().next();
 		} 
 		else {
 			addnlData = new SubscriberAddiInfo();
 			addnlData.setSubscriber(sub);
 		}
         if(entry.getElectonicDevieused()!= null){
     		addnlData.setElectonicdeviceused(Integer.valueOf(entry.getElectonicDevieused()).longValue());
     	}
      	if(entry.getAgreementNumber()!= null){
     		addnlData.setAgreementnumber(entry.getAgreementNumber());
     	}
     	if(entry.getAgreementDate()!= null){
     		addnlData.setAgrementdate(new Timestamp(getDate(entry.getAgreementDate())));
     	}
     	if(entry.getImplementationdate()!= null){
     		addnlData.setImplementatindate(new Timestamp(getDate(entry.getImplementationdate())));
     	}
     	if(entry.getAgentCompanyName()!= null){
     		addnlData.setAgentcompanyname(entry.getAgentCompanyName());
     	}
     	if(entry.getLatitude()!= null){
     		addnlData.setLatitude(entry.getLatitude());
     	}
     	if(entry.getLongitude()!= null){
     		addnlData.setLongitude(entry.getLongitude());
     	}
     	if(entry.getUserBankBranch()!= null){
     		addnlData.setUserbankbranch(entry.getUserBankBranch());
     	}
     	if(entry.getBankAccountStatus()!= null){
     		addnlData.setBankacountstatus(Integer.valueOf(entry.getBankAccountStatus()).longValue());
     	}
     	subAddFldsDAO.save(addnlData);
     }
    
    private void updateEntityAddInfo(Subscriber sub, CMJSAgent.CGEntries entry){
		SubscriberAddiInfo addnlData; 
		if (CollectionUtils.isNotEmpty(sub.getSubscriberAddiInfos())) {
			addnlData = sub.getSubscriberAddiInfos().iterator().next();
		} 
		else {
			addnlData = new SubscriberAddiInfo();
			addnlData.setSubscriber(sub);
		}
        if(entry.getElectonicDevieused()!= null){
    		addnlData.setElectonicdeviceused(Integer.valueOf(entry.getElectonicDevieused()).longValue());
    	}
     	if(entry.getAgreementNumber()!= null){
    		addnlData.setAgreementnumber(entry.getAgreementNumber());
    	}
    	if(entry.getAgreementDate()!= null){
    		addnlData.setAgrementdate(new Timestamp(getDate(entry.getAgreementDate())));
    	}
    	if(entry.getImplementationdate()!= null){
    		addnlData.setImplementatindate(new Timestamp(getDate(entry.getImplementationdate())));
    	}
    	if(entry.getAgentCompanyName()!= null){
    		addnlData.setAgentcompanyname(entry.getAgentCompanyName());
    	}
    	if(entry.getLatitude()!= null){
    		addnlData.setLatitude(entry.getLatitude());
    	}
    	if(entry.getLongitude()!= null){
    		addnlData.setLongitude(entry.getLongitude());
    	}
    	if(entry.getUserBankBranch()!= null){
    		addnlData.setUserbankbranch(entry.getUserBankBranch());
    	}
    	if(entry.getBankAccountStatus()!= null){
    		addnlData.setBankacountstatus(Integer.valueOf(entry.getBankAccountStatus()).longValue());
    	}
    	subAddFldsDAO.save(addnlData);
    }
    
    public CMJSAgentError verifyAgentData(CmFinoFIX.CMJSAgentError errorAgentMsg, KtpDetails ktpDetail){
    	//CmFinoFIX.CMJSAgentError errorAgentMsg = new CMJSAgentError();
    	ktpDetailsDAO.save(ktpDetail);
    	errorAgentMsg.setUserBankBranch(userService.getUserBranchCodeString());
    	errorAgentMsg.setAlamatInAccordanceIdentity("jakarta");
    	errorAgentMsg.setRTAl("01");
    	errorAgentMsg.setRWAl("02");
    	errorAgentMsg.setVillageAl("kel gambir village");
    	errorAgentMsg.setDistrictAl("gambir district");
    	errorAgentMsg.setCityAl("kota adm city");
    	errorAgentMsg.setProvincialAl("dkjakarta province");
    	errorAgentMsg.setPotalCodeAl("12910");
    	errorAgentMsg.setErrorDescription(MessageText._("Agent KTP validation successfull"));
    	errorAgentMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
    	errorAgentMsg.setsuccess(CmFinoFIX.Boolean_True);
    	
    	return errorAgentMsg;
    }
    
    public CMJSAgentError verifyAgentDataFromWS(CmFinoFIX.CMJSAgentError errorMsg, CMJSAgent realMsg, KtpDetails ktpDetail)
    {
    	log.info("Entered into verifyAgentDataFromWS method for making a WS call");
  		RSClientPostHttps wsCall = new RSClientPostHttps();
		try {
			JSONObject request = new JSONObject();
			
			ktpDetailsDAO.save(ktpDetail);
			
			request.put("accno",realMsg.getAccountnumberofBankSinarmas());
			request.put("nik",realMsg.getKTPID());
			request.put("namalengkap",realMsg.getFirstName());
			request.put("reffno",StringUtils.leftPad(String.valueOf(ktpDetail.getId()),12,"0"));
			request.put("action","inquiryEKTPAgent");
						
			JSONObject response = wsCall.callHttpsPostService(request.toString(), ConfigurationUtil.getKTPServerURL(), ConfigurationUtil.getKTPServerTimeout(), "KTP Server Validation");
			if(null != response) {
				if(response.has("status") && StringUtils.isNotBlank(response.get("status").toString()) && (response.get("status").toString().equals("CommunicationFailure"))) {
					log.info("Did not received proper data from WS call");
					//ktpDetail.setBankResponse(response.toString());
					//ktpDetail.setBankResponse(response.toString().substring(0, 1000));
					if(response.toString().length() > 1000){
					ktpDetail.setBankresponse(response.toString().substring(0, 1000));
					}else{
						ktpDetail.setBankresponse(response.toString());
					}
					//ktpDetail.setBankResponseStatus(response.get("responsecode").toString());
					ktpDetailsDAO.save(ktpDetail);
					
					errorMsg.setErrorDescription(MessageText._("Agent KTP validation Failed"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					errorMsg.setsuccess(CmFinoFIX.Boolean_False);
				} else {
					//ktpDetail.setBankResponse(response.toString());
					//ktpDetail.setBankResponse(response.toString().substring(0, 1000));
					if(response.toString().length() > 1000){
					ktpDetail.setBankresponse(response.toString().substring(0, 1000));
					}else{
						ktpDetail.setBankresponse(response.toString());
					}
					ktpDetail.setBankresponsestatus(response.get("responsecode").toString());
					ktpDetailsDAO.save(ktpDetail);
					
					errorMsg.setUserBankBranch(userService.getUserBranchCodeString());
					errorMsg.setAlamatInAccordanceIdentity(response.get("alamat").toString());
					errorMsg.setRTAl(response.get("rt").toString());
					errorMsg.setRWAl(response.get("rw").toString());
					errorMsg.setVillageAl(response.get("kelurahan").toString());
					errorMsg.setDistrictAl(response.get("kecamatan").toString());
					errorMsg.setCityAl(response.get("kota").toString());
					errorMsg.setProvincialAl(response.get("provinsi").toString());
					errorMsg.setPotalCodeAl(response.get("kodepos").toString());
					errorMsg.setErrorDescription(MessageText._("Agent KTP validation successfull"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
					errorMsg.setsuccess(CmFinoFIX.Boolean_True);
				}
			}
		} catch(Exception ex) {
			log.error("Error in parsing the response from WS Server..." + ex);
			errorMsg.setErrorDescription(MessageText._("Agent KTP validation Failed"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			errorMsg.setsuccess(CmFinoFIX.Boolean_False);
			return errorMsg;
		}
		log.info("Webservice call completed");
		return errorMsg;
    }
    
	private Date getDate(String dateStr) {
		Date dateOfBirth = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			dateFormat.setLenient(false);
			dateOfBirth = dateFormat.parse(dateStr);
		} catch (Exception e) {
			log.error("Exception in Registration: Invalid Date",e);
		}		
		return dateOfBirth;
	}
}
