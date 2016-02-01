package com.mfino.uicore.fix.processor.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.KtpDetailsDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SubscribersAdditionalFieldsDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.Address;
import com.mfino.domain.Company;
import com.mfino.domain.Group;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.KtpDetails;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.SubscribersAdditionalFields;
import com.mfino.domain.User;
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
	
	@SuppressWarnings("null")
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("ServicePartnerProcessorspImpl::process() method BEGIN");
		
		CMJSAgent realMsg = (CMJSAgent) msg;
		isOTPEnabled = ConfigurationUtil.getSendOTPOnIntialized();
		log.info("ServicePartnerProcessorspImpl::process() :: realMsg.getaction()="+realMsg.getaction());
		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			System.out.println("entered into update block");
			CMJSAgent.CGEntries[] entries = realMsg.getEntries();

            for (CMJSAgent.CGEntries entry : entries) {
            	Partner objPartner = partnerDao.getById(Long.valueOf(entry.getID()));
                //Partner objPartner = objServicePartner.getPartner();
                Subscriber objSubscriber = objPartner.getSubscriber();
                SubscriberMDN objSubscriberMdn = ((objSubscriber != null) && (objSubscriber.getSubscriberMDNFromSubscriberID().size() > 0))? objSubscriber.getSubscriberMDNFromSubscriberID().iterator().next() : null;
                
                // Check for Stale Data
                if (!entry.getRecordVersion().equals(objPartner.getVersion())) {
                    handleStaleDataException();
                }
                if(entry.isRemoteModifiedPartnerCode()&&(!objPartner.getPartnerCode().equalsIgnoreCase(entry.getPartnerCode()))){
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
                if(entry.isRemoteModifiedTradeName()&&(!objPartner.getTradeName().equalsIgnoreCase(entry.getTradeName()))){
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
                if(CmFinoFIX.BusinessPartnerType_ServicePartner.equals(objPartner.getBusinessPartnerType())&&entry.getBusinessPartnerType()!=null){
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
                		&&!objPartner.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_ServicePartner)
                		&&!CmFinoFIX.SubscriberStatus_Active.equals(objPartner.getPartnerStatus())){
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
				if(CmFinoFIX.SubscriberStatus_Initialized.equals(entry.getPartnerStatus()) && !(CmFinoFIX.SubscriberStatus_Initialized.equals(objPartner.getPartnerStatus()) ||
						CmFinoFIX.SubscriberStatus_Suspend.equals(objPartner.getPartnerStatus()) || CmFinoFIX.SubscriberStatus_InActive.equals(objPartner.getPartnerStatus())) ){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Intializing partner not allowed."));
					return error;
				}                
				if(CmFinoFIX.SubscriberStatus_Suspend.equals(entry.getPartnerStatus())&&!CmFinoFIX.SubscriberStatus_Suspend.equals(objPartner.getPartnerStatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Suspending of Partner / Agent not allowed."));
					return error;
				}
				if(entry.getPartnerStatus() != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(entry.getPartnerStatus()) &&
						CmFinoFIX.SubscriberStatus_Suspend.equals(objPartner.getPartnerStatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Suspended Partner / Agent can be moved to Intialized status only"));
					return error;
				}				
				if(entry.getPartnerStatus() != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(entry.getPartnerStatus()) && 
						CmFinoFIX.SubscriberStatus_InActive.equals(objPartner.getPartnerStatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Inactive Partner / Agent can be moved to Intialized status only"));
					return error;
				}
				Integer partnerRestrictions = objSubscriberMdn.getRestrictions();
                updateEntity(objPartner, objSubscriber,objSubscriberMdn, entry);
                updateEntityAddInfo(objSubscriber,entry);
                
				//Generate OTP for the Partner / agent if the status is changed from Suspend to Initialise or Inactive to Initialise. 
				if(entry.getPartnerStatus() != null && CmFinoFIX.SubscriberStatus_Initialized.equals(entry.getPartnerStatus())
						&&CmFinoFIX.UpgradeState_Approved.equals(objSubscriber.getUpgradeState())){
					if(isOTPEnabled){
						generateAndSendOTP(objPartner, objSubscriberMdn, objSubscriber);
						objSubscriberMdn.setWrongPINCount(0);
					}else{
						hanldleNoOTP(partnerRestrictions,objPartner,objSubscriber,objSubscriberMdn);
					}						
				}
				
                 if(entry.getAuthorizedEmail()!=null||entry.getTradeName()!=null||entry.getBusinessPartnerType()!=null){
                	User user = objPartner.getUser();
                	if(entry.getAuthorizedEmail()!=null){
                 	user.setEmail(entry.getAuthorizedEmail());
                	}
                	if(entry.getTradeName()!=null){
                     	user.setFirstName(entry.getTradeName());
                    }
                	if(entry.getBusinessPartnerType()!=null){
                		user.setRole(partnerService.getRole(entry.getBusinessPartnerType()));
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
			CmFinoFIX.CMJSAgentError errorMsg = new CmFinoFIX.CMJSAgentError();
			if("agentprimarydata".equals(realMsg.getTypeAgentObject()))
			{
				System.out.println("entered into agentprimarydata block");
				
				
				errorMsg.setUserBankBranch(userService.getUserBranchCodeString());
				
/*				errorMsg = (CMJSAgentError)verifyAgentData();
				errorMsg.setErrorDescription("Agent KTP validation successfull");
				errorMsg.setsuccess(CmFinoFIX.Boolean_True);
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);*/
				
				KtpDetails ktpDetail = new KtpDetails();
				ktpDetail.setMDN(realMsg.getMDN());
				ktpDetail.setKTPID(realMsg.getKTPID());
				ktpDetail.setFullName(realMsg.getUsername());
				ktpDetail.setBankResponseStatus("00");
				ktpDetail.setBankResponse("Success");
				
				ktpDetailsDAO.save(ktpDetail);
				
				RSClientPostHttps wsCall = new RSClientPostHttps();
				try {
					JSONObject request = new JSONObject();
					
					request.put("accno",realMsg.getAccountnumberofBankSinarmas());
					request.put("nik",realMsg.getKTPID());
					request.put("namalengkap",realMsg.getFirstName());
					request.put("reffno",StringUtils.leftPad(String.valueOf(ktpDetail.getID()),12,"0"));
					request.put("action","inquiryEKTPAgent");
								
					JSONObject response = wsCall.callHttpsPostService(request.toString(), ConfigurationUtil.getKTPServerURL(), ConfigurationUtil.getKTPServerTimeout(), "KTP Server Validation");
				
					if(null != response) {// && response.get("status").toString().equals("Success")) {
						
						ktpDetail.setBankResponse(response.toString());
						ktpDetail.setBankResponseStatus(response.get("responsecode").toString());
						ktpDetailsDAO.save(ktpDetail);
										
						errorMsg.setAlamatInAccordanceIdentity(response.get("alamat").toString());
						errorMsg.setRTAl(response.get("rt").toString());
						errorMsg.setRWAl(response.get("rw").toString());
						errorMsg.setVillageAl(response.get("kelurahan").toString());
						errorMsg.setDistrictAl(response.get("kecamatan").toString());
						errorMsg.setCityAl(response.get("kota").toString());
						errorMsg.setProvincialAl(response.get("provinsi").toString());
						errorMsg.setPotalCodeAl(response.get("kodepos").toString());
						errorMsg.setErrorDescription("Agent KTP validation successfull");
						errorMsg.setsuccess(CmFinoFIX.Boolean_True);
						errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
					} else {
						errorMsg.setErrorDescription("Agent KTP validation Failed");
						errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
						errorMsg.setsuccess(CmFinoFIX.Boolean_False);
					}
				} catch(Exception ex) {
					
					log.error("Error in parsing the response from server..." + ex);
				}
                return errorMsg;
			}
/*            CMJSAgent.CGEntries[] entries = realMsg.getEntries();

            for (CMJSAgent.CGEntries e : entries) {*/
				System.out.println("entered into agent data add block");            	
            	Partner partner = new Partner();
                Subscriber subscriber;
                SubscriberMDN subscriberMdn;
                SubscriberMDN existingSubscriberMDN = null;  
                PartnerQuery query = null;
                //CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
            	PartnerDAO dao = DAOFactory.getInstance().getPartnerDAO();
        		           	
                int agntTyp = Integer.valueOf(realMsg.getAgentType());
                if(agntTyp == 1)
                {
                	realMsg.setBusinessPartnerType(4);                	
                }
                if(agntTyp == 2)
                {
                	realMsg.setBusinessPartnerType(5);  
                }
        		query = new PartnerQuery();
        		query.setBranchCode(realMsg.getBranchCode());
        		query.setBusinessPartnerType(realMsg.getBusinessPartnerType());
        		Partner prtnr = dao.getBranchSequence(query);
        		Integer branchSeq = null;
        		Integer seq = 0;
        		if(null != prtnr){
	    			seq = prtnr.getBranchSequence();
        		}
    			if(null != seq && seq >= 0){
    				branchSeq = seq + 1;
    			}else{
    				branchSeq = 1;
    			}
        		
                String agentCode = "153"+realMsg.getBranchCode()+realMsg.getAgentType() + branchSeq;

                realMsg.setPartnerCode(agentCode);
                realMsg.setTradeName(realMsg.getUsername());
                realMsg.setAuthorizedEmail(realMsg.getEMail());
                realMsg.setLanguage(CmFinoFIX.Language_English);
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
        		
/*        		query.setPartnerCode(null);
        		query.setTradeName(e.getTradeName());
        		results = dao.get(query);
        		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) {
        			  errorMsg.setErrorDescription(MessageText._(" TradeName already exists in DB, please enter different TradeName."));
                     errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                     return errorMsg;
        		}*/
        		
                String username = realMsg.getUsername();
                log.info("User name = " + username);
                User user = userDAO.getByUserName(username);
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
                	subscriberMdn=new SubscriberMDN();
                	subscriber=new Subscriber();
                    subscriber.setmFinoServiceProviderByMSPID(mspDAO.getById(1));
                }else{
                	 if(existingSubscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_PendingRetirement)
                			 ||existingSubscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_Retired)){
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
                u.setEmail(realMsg.getAuthorizedEmail());
                u.setFirstName(findFirstName(realMsg));
                u.setLastName(findLastName(realMsg));
                u.setLanguage(realMsg.getLanguage());
                u.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
                u.setStatus(CmFinoFIX.UserStatus_Registered);
                u.setTimezone(realMsg.getTimezone());
                u.setRole(partnerService.getRole(realMsg.getBusinessPartnerType()));
                u.setBranchCodeID(Long.valueOf(realMsg.getBranchCode()));
                subscriber.setType(CmFinoFIX.SubscriberType_Partner);
                KYCLevel kycLevel = kyclevelDao.getByKycLevel(ConfigurationUtil.getBulkUploadSubscriberKYClevel());
                subscriber.setKYCLevelByKYCLevel(kycLevel);
                subscriber.setUpgradableKYCLevel(null);
                subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Upgradable);
                subscriber.setRegistrationMedium(CmFinoFIX.RegistrationMedium_AdminApp);
                subscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS|CmFinoFIX.NotificationMethod_Email);
                subscriberMdn.setOTP(null);
                subscriberMdn.setDigestedPIN(null);
                subscriberMdn.setAuthorizationToken(null);
                updateEntitysp(partner, subscriber, subscriberMdn, realMsg);
                subscriberMdn.setSubscriber(subscriber);
                partner.setSubscriber(subscriber);
                partner.setUser(u);
                subscriber.setLastName(findLastName(realMsg));
                subscriber.setAppliedBy(userService.getCurrentUser().getUsername());
                subscriber.setAppliedTime(new Timestamp());
                subscriber.setApprovedOrRejectedBy("");
                subscriber.setApproveOrRejectComment("");
                subscriber.setApproveOrRejectTime(null);
                partner.setBranchSequence(branchSeq);
                
                userDAO.save(u);                                               
                subscriberDao.save(subscriber);
                subscriberMdnDao.save(subscriberMdn);
                partnerDao.save(partner);
                
                updateEntityAddInfosp(subscriber,realMsg);
                
                if(realMsg.getAuthorizedEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
                	mailService.generateEmailVerificationMail(subscriber, realMsg.getAuthorizedEmail());
				}
				if(subscriber.getSubscriberGroupFromSubscriberID().size() > 0){
					SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
					for(SubscriberGroup sg: subscriber.getSubscriberGroupFromSubscriberID()){
						subscriberGroupDao.save(sg);
					}
				}
				updateMessagesp(partner,subscriber,subscriberMdn, realMsg);
				
                String cardPan=null;
                Pocket epocket=null;
                if(existingSubscriberMDN!=null){
                	epocket=subscriberService.getDefaultPocket(existingSubscriberMDN.getID(),CmFinoFIX.PocketType_SVA,CmFinoFIX.Commodity_Money);
                }
                try{
                	cardPan=pocketService.generateSVAEMoney16DigitCardPAN(subscriberMdn.getMDN());
                }catch (Exception ex) {
					log.error("Exception to create cardPan",ex);
				}
                Long groupID = null;
				if (StringUtils.isNotEmpty(realMsg.getGroupID())) {
					groupID = Long.valueOf(realMsg.getGroupID());
				}
				StringBuilder  errDescription = new StringBuilder();
				boolean isError = false;
                PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, realMsg.getBusinessPartnerType(), groupID);
                if (svaPocketTemplate == null) {
					log.info("No Default SVA Pocket template set for " + subscriber.getID());
					isError = true;
					errDescription.append("No Default SVA pocket template set for this group and partner type<br/>");
				}
                else
                {
                	if(epocket!=null&&!(epocket.getStatus().equals(CmFinoFIX.PocketStatus_PendingRetirement)||epocket.getStatus().equals(CmFinoFIX.PocketStatus_Retired))){
                		epocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
                		epocket.setStatusTime(new Timestamp());
                		epocket.setPocketTemplateByOldPocketTemplateID(epocket.getPocketTemplate());
                		epocket.setPocketTemplate(svaPocketTemplate);
                		epocket.setPocketTemplateChangedBy(userService.getCurrentUser().getUsername());
                		epocket.setPocketTemplateChangeTime(new Timestamp());
                		pocketDao.save(epocket);

                	}else{
                		epocket = pocketService.createPocket(svaPocketTemplate, subscriberMdn, CmFinoFIX.PocketStatus_Initialized, true, cardPan);
                		log.info("Default emoney pocket successfully created for the partner -->"+partner.getID());
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
                		String collectorPocketCardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMdn.getMDN());
                		collectorPocket = pocketService.createPocket(collectorPocketTemplate, subscriberMdn, CmFinoFIX.PocketStatus_Initialized, true, collectorPocketCardPan);
                		log.info("Default collector pocket Id --> " + collectorPocket.getID());
                	}
				} catch (Exception e1) {
					log.info("Default Collector Pocket creation is failed for the Partner --> " + partner.getID());
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
						String suspensePocketCardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMdn.getMDN());
						suspensePocket = pocketService.createPocket(suspensePocketTemplate, subscriberMdn, CmFinoFIX.PocketStatus_Initialized, true, suspensePocketCardPan);
						log.info("Default suspense pocket Id --> " + suspensePocket.getID());
					}
				} catch (Exception e1) {
					log.info("Default Suspense Pocket creation failed for the Partner --> " + partner.getID());
					isError = true;
					errDescription.append("Default Suspense Pocket creation failed for the Partner<br/>");
				}
                
				Pocket bankPocket = null;
				PocketTemplate bankPocketTemplate = null;
                try {
                	 String bankCardPan=realMsg.getAccountnumberofBankSinarmas();
                	bankPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, CmFinoFIX.PocketType_BankAccount, CmFinoFIX.SubscriberType_Partner, null, groupID);
        			if (bankPocketTemplate == null) {
        				errorMsg.setErrorDescription(MessageText._("No Default Bank Pocket set for this KYC"));
        				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        				log.warn("No Default Bank Pocket set for " + subscriberMdn.getID());
        				return errorMsg;
        			}
        			if(bankPocketTemplate.getID() >= 0 && bankCardPan != null)
        			{
        				boolean isallowed=pocketService.checkCount(bankPocketTemplate,subscriberMdn);
        				if(!isallowed){
        					log.error("PocketProcessor :: Pocket count limit reached for template:"+bankPocketTemplate.getDescription()+" for MDN:"+subscriberMdn.getMDN()+" by user:"+getLoggedUserNameWithIP());	
        					return getErrorMessage(MessageText._(" Pocket count Limit reached for this template  "), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Pocket count Limit reached for this template"));  		
        				}           

        				bankPocket = pocketService.createDefaultBankPocket(bankPocketTemplate.getID(), subscriberMdn, bankCardPan);
        				if(bankPocket==null){
        					errorMsg.setErrorDescription(MessageText._("Default Bank Pocket creation failed for the Subscriber"));
        					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        					log.info("Default Bank Pocket creation failed for Subscriber "+subscriberMdn.getID());
        					return errorMsg;
        				}
        			}
				} catch (Exception e1) {
					log.info("Default Bank Pocket creation failed for the Partner --> " + partner.getID());
					isError = true;
					errDescription.append("Default Bank Pocket creation failed for the Partner<br/>");
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
					partnerQuery.setId(partner.getID());
					if(StringUtils.isNotBlank(realMsg.getPartnerIDSearch())&&!partner.getID().equals(Long.valueOf(realMsg.getPartnerIDSearch()))){
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
                SubscriberMDN objSubscriberMdn = ((objSubscriber != null) && (objSubscriber.getSubscriberMDNFromSubscriberID().size() > 0))? objSubscriber.getSubscriberMDNFromSubscriberID().iterator().next() : null;

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
			Subscriber subscriber, SubscriberMDN mdn) {
		Boolean isNewSecurityLocked = ((subscriber.getRestrictions() & CmFinoFIX.SubscriberRestrictions_SecurityLocked ) != 0);
		Boolean isNewAbsoluteLocked = ((subscriber.getRestrictions() & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) != 0 );
		Boolean isOldSecurityLocked = ((oldRestrictions & CmFinoFIX.SubscriberRestrictions_SecurityLocked ) != 0 );
		Boolean isOldAbsoluteLocked = ((oldRestrictions & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) != 0);
		Boolean isNewLocked = (isNewSecurityLocked || isNewAbsoluteLocked);
		
		//if security lock and absolute lock removed activate partner
		if((isOldSecurityLocked && !isNewSecurityLocked) || (isOldAbsoluteLocked && !isNewAbsoluteLocked)){
			if(!isNewLocked){
			partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriber.setStatusTime(new Timestamp());
			mdn.setStatus(CmFinoFIX.MDNStatus_Active);
			mdn.setStatusTime(new Timestamp());
			subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
			}else{
				partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriber.setStatusTime(new Timestamp());
				mdn.setStatus(CmFinoFIX.MDNStatus_InActive);
				mdn.setStatusTime(new Timestamp());
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

	private void updateEntitysp(Partner partner,Subscriber subscriber, SubscriberMDN subscriberMdn,CMJSAgent entry) {
    	
    	/*TODO 
    	 * Not sure if this is reqd for creating partner, setting default values
    	 * */
    	if(entry.getMDN()!=null){
    		subscriberMdn.setMDN(subscriberService.normalizeMDN(entry.getMDN()));
    	}
    	if(entry.getKTPID()!= null){
    		subscriberMdn.setApplicationID(entry.getKTPID());
    	}
    	if(entry.getBusinessPartnerType()!=null){
    		partner.setBusinessPartnerType(entry.getBusinessPartnerType());
    	}
        if (entry.getPartnerStatus() != null) {
            if (!(entry.getPartnerStatus().equals(partner.getPartnerStatus()))) {
                subscriberMdn.setStatus(entry.getPartnerStatus());
                subscriber.setStatus(entry.getPartnerStatus());
                subscriberMdn.setStatusTime(new Timestamp());
                subscriber.setStatusTime(new Timestamp());
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
                partner.setPartnerStatus(entry.getPartnerStatus());
            }
        }
        if (entry.getIsForceCloseRequested() != null && entry.getIsForceCloseRequested().booleanValue()) {
        	subscriberMdn.setIsForceCloseRequested(entry.getIsForceCloseRequested());
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
      		subscriber.setBirthPlace(entry.getPlaceofBirth());
    	}
    	if(entry.getDateofBirth()!= null){
    		subscriber.setDateOfBirth(new Timestamp(getDate(entry.getDateofBirth())));
    	}
        if(entry.getBusinessPartnerType()!=null){
        	partner.setBusinessPartnerType(entry.getBusinessPartnerType());
        }
        if(entry.getPartnerCode() != null){
        	partner.setPartnerCode(entry.getPartnerCode());
        }
        
        //populate address information
        if(entry.getAuthorizedEmail() != null){
        	partner.setAuthorizedEmail(entry.getAuthorizedEmail());
        	subscriber.setEmail(entry.getAuthorizedEmail());
        	subscriber.setIsEmailVerified(false);
        }
        if(entry.getCompanyEmailId() != null){
        	partner.setCompanyEmailId(entry.getCompanyEmailId());
        }
       
        partner.setmFinoServiceProviderByMSPID(mspDAO.getById(1));
        
        if(entry.getTradeName() != null){
        	partner.setTradeName(entry.getTradeName());
        	  subscriber.setFirstName(entry.getTradeName());
         }
        if (entry.getRestrictions() != null) {
        	subscriber.setRestrictions(entry.getRestrictions());
        	subscriberMdn.setRestrictions(entry.getRestrictions());
        }
        
        //simaspay changes starts
		if(entry.getClassificationAgent() != null){
        	partner.setClassification(entry.getClassificationAgent());
        }
        if(entry.getTypeofBusinessAgent() != null){
        	partner.setTypeOfOrganization(entry.getTypeofBusinessAgent());
        }
        if(entry.getPhoneNumber() != null){
        	partner.setFranchisePhoneNumber(entry.getPhoneNumber());
        }
        if(entry.getBranchCode() != null){
        	partner.setBankBranchCode(Integer.valueOf(entry.getBranchCode()));
        }

        if(entry.getAccountnumberofBankSinarmas() != null){
        	partner.setAccountnumberofBankSinarmas(entry.getAccountnumberofBankSinarmas());
        }

        //simaspay changes ends
        
        //for merchant and outlet addresses
        Address merchantAddress = partner.getAddressByMerchantAddressID();        	
        	if(merchantAddress == null){
        		merchantAddress = new Address();
        		partner.setAddressByMerchantAddressID(merchantAddress);
           	}
       	
        	//simaspay changes starts
        	if(entry.getAlamatInAccordanceIdentity()!= null){
        	merchantAddress.setLine1(entry.getAlamatInAccordanceIdentity());
        	}
        	if(entry.getRTAl()!= null){
        	merchantAddress.setRT(entry.getRTAl());
        	}
        	if(entry.getRWAl()!= null){
        	merchantAddress.setRW(entry.getRWAl());
        	}
        	if(entry.getDistrictAl()!= null){
        	merchantAddress.setState(entry.getDistrictAl());
        	}
        	if(entry.getCityAl()!= null){
        	merchantAddress.setCity(entry.getCityAl());
        	}
        	if(entry.getVillageAl()!= null){
        	merchantAddress.setSubState(entry.getVillageAl());
        	}
        	if(entry.getPotalCodeAl()!= null){
        	merchantAddress.setZipCode(entry.getPotalCodeAl());
        	}
        	if(entry.getProvincialAl()!= null){
        	merchantAddress.setRegionName(entry.getProvincialAl());
        	}
        	//simaspay changes ends
        	addressDao.save(merchantAddress);
        	        
        	//outlet address
           	Address outletAddress = partner.getAddressByFranchiseOutletAddressID();
            	if(outletAddress == null){
            		outletAddress = new Address();
            		partner.setAddressByFranchiseOutletAddressID(outletAddress);
            	}
            	//simaspay changes starts
            	if(entry.getCompanyAddress()!= null){
            		outletAddress.setLine1(entry.getCompanyAddress());
            	}
            	if(entry.getRTCom()!= null){
            		outletAddress.setRT(entry.getRTCom());
            	}
            	if(entry.getRWCom()!= null){
            		outletAddress.setRW(entry.getRWCom());
            	}
            	if(entry.getDistrictCom()!= null){
            		outletAddress.setState(entry.getDistrictCom());
            	}
            	if(entry.getCityCom()!= null){
            		outletAddress.setCity(entry.getCityCom());
            	}
            	if(entry.getVillageCom()!= null){
            		outletAddress.setSubState(entry.getVillageCom());
            	}
            	if(entry.getPotalCodeCom()!= null){
            		outletAddress.setZipCode(entry.getPotalCodeCom());
            	}
            	if(entry.getProvincialCom()!= null){
            		outletAddress.setRegionName(entry.getProvincialCom());
            	}
            	//simaspay changes ends  	
            	addressDao.save(outletAddress);
            	
            	
        		if((null != entry.getGroupID()) && !("".equals(entry.getGroupID()))){
        			
        			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
        			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
        			
        			if((subscriber.getSubscriberGroupFromSubscriberID() != null) && (subscriber.getSubscriberGroupFromSubscriberID().size() > 0)){
        				Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
        				SubscriberGroup sg = subscriberGroups.iterator().next();
        				if(sg.getGroup().getID().longValue() != Long.valueOf(entry.getGroupID()).longValue()){
        					Group group = (Group)groupDao.getById(Long.valueOf(entry.getGroupID()));
        					sg.setGroup(group);
        					subscriberGroupDao.save(sg);
        				}
        			}
        			else{
        				Group group = (Group)groupDao.getById(Long.valueOf(entry.getGroupID()));
        				SubscriberGroup sg = new SubscriberGroup();
        				sg.setSubscriber(subscriber);
        				sg.setGroup(group);
        				subscriber.getSubscriberGroupFromSubscriberID().add(sg);
        				
        				if(subscriber.getID() != null){
        					subscriberGroupDao.save(sg);
        				}
        			}
        		}            	
        }
    
	private void updateEntity(Partner partner,Subscriber subscriber, SubscriberMDN subscriberMdn,CMJSAgent.CGEntries entry) {
    	
    	/*TODO 
    	 * Not sure if this is reqd for creating partner, setting default values
    	 * */
    	if(entry.getMDN()!=null){
    		subscriberMdn.setMDN(subscriberService.normalizeMDN(entry.getMDN()));
    	}
    	if(entry.getKTPID()!= null){
    		subscriberMdn.setApplicationID(entry.getKTPID());
    	}
    	if(entry.getBusinessPartnerType()!=null){
    		partner.setBusinessPartnerType(entry.getBusinessPartnerType());
    	}
        if (entry.getPartnerStatus() != null) {
            if (!(entry.getPartnerStatus().equals(partner.getPartnerStatus()))) {
                subscriberMdn.setStatus(entry.getPartnerStatus());
                subscriber.setStatus(entry.getPartnerStatus());
                subscriberMdn.setStatusTime(new Timestamp());
                subscriber.setStatusTime(new Timestamp());
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
                partner.setPartnerStatus(entry.getPartnerStatus());
            }
        }
        if (entry.getIsForceCloseRequested() != null && entry.getIsForceCloseRequested().booleanValue()) {
        	subscriberMdn.setIsForceCloseRequested(entry.getIsForceCloseRequested());
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
      		subscriber.setBirthPlace(entry.getPlaceofBirth());
    	}
    	if(entry.getDateofBirth()!= null){
    		subscriber.setDateOfBirth(new Timestamp(getDate(entry.getDateofBirth())));
    	}
        if(entry.getBusinessPartnerType()!=null){
        	partner.setBusinessPartnerType(entry.getBusinessPartnerType());
        }
        if(entry.getPartnerCode() != null){
        	partner.setPartnerCode(entry.getPartnerCode());
        }
        
        //populate address information
        if(entry.getAuthorizedEmail() != null){
        	partner.setAuthorizedEmail(entry.getAuthorizedEmail());
        	subscriber.setEmail(entry.getAuthorizedEmail());
        	subscriber.setIsEmailVerified(false);
        }
        if(entry.getCompanyEmailId() != null){
        	partner.setCompanyEmailId(entry.getCompanyEmailId());
        }
       
        partner.setmFinoServiceProviderByMSPID(mspDAO.getById(1));
        
        if(entry.getTradeName() != null){
        	partner.setTradeName(entry.getTradeName());
        	  subscriber.setFirstName(entry.getTradeName());
         }
        if (entry.getRestrictions() != null) {
        	subscriber.setRestrictions(entry.getRestrictions());
        	subscriberMdn.setRestrictions(entry.getRestrictions());
        }
        
        //simaspay changes starts
		if(entry.getClassificationAgent() != null){
        	partner.setClassification(entry.getClassificationAgent());
        }
        if(entry.getTypeofBusinessAgent() != null){
        	partner.setTypeOfOrganization(entry.getTypeofBusinessAgent());
        }
        if(entry.getPhoneNumber() != null){
        	partner.setFranchisePhoneNumber(entry.getPhoneNumber());
        }
        if(entry.getBranchCode() != null){
        	partner.setBankBranchCode(Integer.valueOf(entry.getBranchCode()));
        }

        if(entry.getAccountnumberofBankSinarmas() != null){
        	partner.setAccountnumberofBankSinarmas(entry.getAccountnumberofBankSinarmas());
        }

        //simaspay changes ends
        
        //for merchant and outlet addresses
        Address merchantAddress = partner.getAddressByMerchantAddressID();        	
        	if(merchantAddress == null){
        		merchantAddress = new Address();
        		partner.setAddressByMerchantAddressID(merchantAddress);
           	}
       	
        	//simaspay changes starts
        	if(entry.getAlamatInAccordanceIdentity()!= null){
        	merchantAddress.setLine1(entry.getAlamatInAccordanceIdentity());
        	}
        	if(entry.getRTAl()!= null){
        	merchantAddress.setRT(entry.getRTAl());
        	}
        	if(entry.getRWAl()!= null){
        	merchantAddress.setRW(entry.getRWAl());
        	}
        	if(entry.getDistrictAl()!= null){
        	merchantAddress.setState(entry.getDistrictAl());
        	}
        	if(entry.getCityAl()!= null){
        	merchantAddress.setCity(entry.getCityAl());
        	}
        	if(entry.getVillageAl()!= null){
        	merchantAddress.setSubState(entry.getVillageAl());
        	}
        	if(entry.getPotalCodeAl()!= null){
        	merchantAddress.setZipCode(entry.getPotalCodeAl());
        	}
        	if(entry.getProvincialAl()!= null){
        	merchantAddress.setRegionName(entry.getProvincialAl());
        	}
        	//simaspay changes ends
        	addressDao.save(merchantAddress);
        	        
        	//outlet address
           	Address outletAddress = partner.getAddressByFranchiseOutletAddressID();
            	if(outletAddress == null){
            		outletAddress = new Address();
            		partner.setAddressByFranchiseOutletAddressID(outletAddress);
            	}
            	//simaspay changes starts
            	if(entry.getCompanyAddress()!= null){
            		outletAddress.setLine1(entry.getCompanyAddress());
            	}
            	if(entry.getRTCom()!= null){
            		outletAddress.setRT(entry.getRTCom());
            	}
            	if(entry.getRWCom()!= null){
            		outletAddress.setRW(entry.getRWCom());
            	}
            	if(entry.getDistrictCom()!= null){
            		outletAddress.setState(entry.getDistrictCom());
            	}
            	if(entry.getCityCom()!= null){
            		outletAddress.setCity(entry.getCityCom());
            	}
            	if(entry.getVillageCom()!= null){
            		outletAddress.setSubState(entry.getVillageCom());
            	}
            	if(entry.getPotalCodeCom()!= null){
            		outletAddress.setZipCode(entry.getPotalCodeCom());
            	}
            	if(entry.getProvincialCom()!= null){
            		outletAddress.setRegionName(entry.getProvincialCom());
            	}
            	//simaspay changes ends  	
            	addressDao.save(outletAddress);
            	
            	
        		if((null != entry.getGroupID()) && !("".equals(entry.getGroupID()))){
        			
        			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
        			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
        			
        			if((subscriber.getSubscriberGroupFromSubscriberID() != null) && (subscriber.getSubscriberGroupFromSubscriberID().size() > 0)){
        				Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
        				SubscriberGroup sg = subscriberGroups.iterator().next();
        				if(sg.getGroup().getID().longValue() != Long.valueOf(entry.getGroupID()).longValue()){
        					Group group = (Group)groupDao.getById(Long.valueOf(entry.getGroupID()));
        					sg.setGroup(group);
        					subscriberGroupDao.save(sg);
        				}
        			}
        			else{
        				Group group = (Group)groupDao.getById(Long.valueOf(entry.getGroupID()));
        				SubscriberGroup sg = new SubscriberGroup();
        				sg.setSubscriber(subscriber);
        				sg.setGroup(group);
        				subscriber.getSubscriberGroupFromSubscriberID().add(sg);
        				
        				if(subscriber.getID() != null){
        					subscriberGroupDao.save(sg);
        				}
        			}
        		}            	
        }
    
    private void updateMessage(Partner partner,Subscriber subscriber, SubscriberMDN subscriberMdn,  CMJSAgent.CGEntries entry) {

        if(partner != null){
        	if(subscriberMdn.getMDN()!=null){
        		entry.setMDN(subscriberMdn.getMDN());
        		entry.setMobilePhoneNumber(subscriberMdn.getMDN());
        	}
	    	if(subscriberMdn.getApplicationID()!= null){
	    		entry.setKTPID(subscriberMdn.getApplicationID());
	    	}
        	
			if(! subscriber.getSubscribersAdditionalFieldsFromSubscriberID().isEmpty()){
				SubscribersAdditionalFields saf=subscriber.getSubscribersAdditionalFieldsFromSubscriberID().iterator().next();
				
				if(saf.getElectonicDeviceUsed()!= null){
					entry.setElectonicDevieusedText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ElectonicDevieused, subscriber.getLanguage(), saf.getElectonicDeviceUsed()));
		    		entry.setElectonicDevieused(String.valueOf(saf.getElectonicDeviceUsed()));
		    	}
		    	if(saf.getAgreementNumber()!= null){
		    		entry.setAgreementNumber(saf.getAgreementNumber());
		    	}
		    	if(saf.getAgrementDate()!= null){
		    		entry.setAgreementDate(String.valueOf(saf.getAgrementDate()));
		    	}
		    	if(saf.getImplementatindate()!= null){
		    		entry.setImplementationdate(String.valueOf(saf.getImplementatindate()));
		    	}
		    	if(saf.getAgentCompanyName()!= null){
		    		entry.setAgentCompanyName(saf.getAgentCompanyName());
		    	}
		    	if(saf.getLatitude()!= null){
		    		entry.setLatitude(saf.getLatitude());
		    	}
		    	if(saf.getLongitude()!= null){
		    		entry.setLongitude(saf.getLongitude());
		    	}
		    	if(saf.getUserBankBranch()!= null){
		    		entry.setUserBankBranch(saf.getUserBankBranch());
		    	}
		    	if(saf.getBankAcountStatus()!= null){
		    		entry.setBankAccountStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankAccountStatus, subscriber.getLanguage(), saf.getBankAcountStatus()));
		    		entry.setBankAccountStatus(String.valueOf(saf.getBankAcountStatus()));
		    	}
			}
        	
	        if(partner.getAddressByFranchiseOutletAddressID() != null){
	        	Address outletAddress = partner.getAddressByFranchiseOutletAddressID();
	        	
	        	if(outletAddress.getLine1() != null){
	        		entry.setCompanyAddress(outletAddress.getLine1());
	        	}
	        	if(outletAddress.getRT() != null){
	        		entry.setRTCom(outletAddress.getRT());
	        	}
	        	if(outletAddress.getRW() != null){
	        		entry.setRWCom(outletAddress.getRW());
	        	}
	        	if(outletAddress.getState() != null){
	        		entry.setDistrictCom(outletAddress.getState());
	        	}
	        	if(outletAddress.getSubState() != null){
	        		entry.setVillageCom(outletAddress.getSubState());
	        	}
	        	if(outletAddress.getCity() != null){
	        		entry.setCityCom(outletAddress.getCity());
	        	}
	        	if(outletAddress.getRegionName() != null){
	        		entry.setProvincialCom(outletAddress.getRegionName());
	        	}	        	
	        	if(outletAddress.getZipCode() != null){
	        		entry.setPotalCodeCom(outletAddress.getZipCode());
	        	}
	        }
	        if(partner.getAddressByMerchantAddressID() != null){
	        	Address merchantAddress = partner.getAddressByMerchantAddressID();
	        	
	        	if(merchantAddress.getLine1() != null){
	        		entry.setAlamatInAccordanceIdentity(merchantAddress.getLine1());
	        	}
	        	if(merchantAddress.getRT() != null){
	        		entry.setRTAl(merchantAddress.getRT());
	        	}
	        	if(merchantAddress.getRW() != null){
	        		entry.setRWAl(merchantAddress.getRW());
	        	}
	        	if(merchantAddress.getState() != null){
	        		entry.setDistrictAl(merchantAddress.getState());
	        	}
	        	if(merchantAddress.getSubState() != null){
	        		entry.setVillageAl(merchantAddress.getSubState());
	        	}
	        	if(merchantAddress.getCity() != null){
	        		entry.setCityAl(merchantAddress.getCity());
	        	}
	        	if(merchantAddress.getRegionName() != null){
	        		entry.setProvincialAl(merchantAddress.getRegionName());
	        	}	        	
	        	if(merchantAddress.getZipCode() != null){
	        		entry.setPotalCodeAl(merchantAddress.getZipCode());
	        	}
	        }
	        if(partner.getID() != null){
	        	entry.setID(partner.getID());
	        }	        
	        if(partner.getLastUpdateTime() != null){
	        	entry.setLastUpdateTime(partner.getLastUpdateTime());
	        }
	        if(partner.getUpdatedBy() != null){
	        	entry.setUpdatedBy(partner.getUpdatedBy());
	        }
	        if(partner.getCreateTime() != null){
	        	entry.setCreateTime(partner.getCreateTime());
	        }
	        if(partner.getCreatedBy() != null){
	        	entry.setCreatedBy(partner.getCreatedBy());
	        }
	        if(partner.getSubscriber() != null){
	        	entry.setSubscriberID(subscriber.getID());
	        	entry.setMDNID(subscriberMdn.getID());
	        }
	        if(partner.getPartnerCode() != null){
	        	entry.setPartnerCode(partner.getPartnerCode());
	        	entry.setAgentCode(partner.getPartnerCode());
	        }
	        if(partner.getPartnerStatus() != null){
	        	entry.setPartnerStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerStatus, subscriber.getLanguage(), partner.getPartnerStatus()));
	        	entry.setPartnerStatus(partner.getPartnerStatus());
	        }
	        if(partner.getTradeName() != null){
	        	entry.setTradeName(partner.getTradeName());
	        }
	        if(partner.getTypeOfOrganization() != null){
	        	entry.setTypeofBusinessAgentText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TypeofBusinessAgent, subscriber.getLanguage(), partner.getTypeOfOrganization()));
	        	entry.setTypeOfOrganization(partner.getTypeOfOrganization());
	        	entry.setTypeofBusinessAgent(partner.getTypeOfOrganization());
	        }
	        if(partner.getFaxNumber() != null){
	        	entry.setFaxNumber(partner.getFaxNumber());
	        }
	        if(partner.getWebSite() != null){
	        	entry.setWebSite(partner.getWebSite());
	        }
	        if(partner.getAuthorizedRepresentative() != null){
	        	entry.setAuthorizedRepresentative(partner.getAuthorizedRepresentative());
	        }
	        if(partner.getRepresentativeName() != null){
	        	entry.setRepresentativeName(partner.getRepresentativeName());
	        }
	        if(partner.getDesignation() != null){
	        	entry.setDesignation(partner.getDesignation());
	        }
	        if(partner.getFranchisePhoneNumber() != null){
	        	entry.setFranchisePhoneNumber(partner.getFranchisePhoneNumber());
	        	entry.setPhoneNumber(partner.getFranchisePhoneNumber());
	        }
	        if(partner.getClassification() != null){
	        	entry.setClassificationAgentText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ClassificationAgent, subscriber.getLanguage(), partner.getClassification()));
	        	entry.setClassification(partner.getClassification());
	        	entry.setClassificationAgent(partner.getClassification());
	        }
	        if(partner.getNumberOfOutlets() != null){
	        	entry.setNumberOfOutlets(partner.getNumberOfOutlets());
	        }
	        if(partner.getIndustryClassification() != null){
	        	entry.setIndustryClassification(partner.getIndustryClassification());
	        }
	        if(partner.getYearEstablished() != null){
	        	entry.setYearEstablished(partner.getYearEstablished());
	        }
	        if(partner.getAuthorizedFaxNumber() != null){
	        	entry.setAuthorizedFaxNumber(partner.getAuthorizedFaxNumber());
	        }
	        if(partner.getAuthorizedEmail() != null){
	        	entry.setAuthorizedEmail(partner.getAuthorizedEmail());
	        	entry.setEMail(partner.getAuthorizedEmail());
	        }
	        if(partner.getCompanyEmailId() != null){
	        	entry.setCompanyEmailId(partner.getCompanyEmailId());
	        }
	        if (partner.getVersion() != null) {
	            entry.setRecordVersion(partner.getVersion());
	        }

	        if (partner.getBankBranchCode() != null) {
	        	entry.setBranchCodeText(userService.getUserBranchCode(partner.getBankBranchCode()));
	            entry.setBranchCode(String.valueOf(partner.getBankBranchCode()));
	        }

	        if (partner.getAccountnumberofBankSinarmas() != null) {
	            entry.setAccountnumberofBankSinarmas(partner.getAccountnumberofBankSinarmas());
	        }

	        if(partner.getBusinessPartnerType() != null){
	        	entry.setBusinessPartnerTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BusinessPartnerTypeAgent, subscriber.getLanguage(), partner.getBusinessPartnerType()));
	        	entry.setBusinessPartnerType(partner.getBusinessPartnerType());
	        	entry.setAgentType(String.valueOf(partner.getBusinessPartnerType()));
	        }
	        entry.setRestrictions(subscriberMdn.getRestrictions());
        }
        if(subscriber != null){
        	if(subscriber.getFirstName() != null){
        		entry.setFirstName(subscriber.getFirstName());
        	}
        	if(subscriber.getLastName() != null){
        		entry.setLastName(subscriber.getLastName());
        	}
	        if(subscriber.getLanguage() != null){
	        	entry.setLanguageText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, subscriber.getLanguage(), subscriber.getLanguage()));
	        	entry.setLanguage(subscriber.getLanguage());
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
	        if(subscriber.getUpgradeState()!=null){
	        	entry.setUpgradeState(subscriber.getUpgradeState());
	        	entry.setUpgradeStateText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_UpgradeState, CmFinoFIX.Language_English, subscriber.getUpgradeState()));
	        }
	        if(subscriber.getAppliedBy()!=null){
	        	entry.setAppliedBy(subscriber.getAppliedBy());
	        }
	        if(subscriber.getAppliedTime()!=null){
	        	entry.setAppliedTime(subscriber.getAppliedTime());
	        }
	        if(subscriber.getApprovedOrRejectedBy()!=null){
	        	entry.setApprovedOrRejectedBy(subscriber.getApprovedOrRejectedBy());
	        }
	        if(subscriber.getApproveOrRejectTime()!=null){
	        	entry.setApproveOrRejectTime(subscriber.getApproveOrRejectTime());
	        }
	        if(subscriber.getApproveOrRejectComment()!=null){
	        	entry.setApproveOrRejectComment(subscriber.getApproveOrRejectComment());
	        }
	        if(subscriber.getBirthPlace()!=null){
	        	entry.setPlaceofBirth(subscriber.getBirthPlace());
	        }
	        if(subscriber.getDateOfBirth()!=null){
	        	entry.setDateofBirth(String.valueOf(subscriber.getDateOfBirth()));
	        }
	        
			if((subscriber.getSubscriberGroupFromSubscriberID() != null) && (subscriber.getSubscriberGroupFromSubscriberID().size() > 0)) {
				SubscriberGroup sg = subscriber.getSubscriberGroupFromSubscriberID().iterator().next();
				entry.setGroupName(sg.getGroup().getGroupName());
				entry.setGroupID(""+sg.getGroup().getID());
			}
        }
        if(partner.getUser()!=null){
        	entry.setUsername(partner.getUser().getUsername());
        	entry.setNameInAccordanceIdentity(partner.getUser().getUsername());
        }
        
    }

    private void updateMessagesp(Partner partner,Subscriber subscriber, SubscriberMDN subscriberMdn,  CMJSAgent entry) {

        if(partner != null){
        	if(subscriberMdn.getMDN()!=null){
        		entry.setMDN(subscriberMdn.getMDN());
        		entry.setMobilePhoneNumber(subscriberMdn.getMDN());
        	}
	    	if(subscriberMdn.getApplicationID()!= null){
	    		entry.setKTPID(subscriberMdn.getApplicationID());
	    	}
        	
			if(! subscriber.getSubscribersAdditionalFieldsFromSubscriberID().isEmpty()){
				SubscribersAdditionalFields saf=subscriber.getSubscribersAdditionalFieldsFromSubscriberID().iterator().next();
				
				if(saf.getElectonicDeviceUsed()!= null){
					entry.setElectonicDevieusedText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ElectonicDevieused, subscriber.getLanguage(), saf.getElectonicDeviceUsed()));
		    		entry.setElectonicDevieused(String.valueOf(saf.getElectonicDeviceUsed()));
		    	}
		    	if(saf.getAgreementNumber()!= null){
		    		entry.setAgreementNumber(saf.getAgreementNumber());
		    	}
		    	if(saf.getAgrementDate()!= null){
		    		entry.setAgreementDate(String.valueOf(saf.getAgrementDate()));
		    	}
		    	if(saf.getImplementatindate()!= null){
		    		entry.setImplementationdate(String.valueOf(saf.getImplementatindate()));
		    	}
		    	if(saf.getAgentCompanyName()!= null){
		    		entry.setAgentCompanyName(saf.getAgentCompanyName());
		    	}
		    	if(saf.getLatitude()!= null){
		    		entry.setLatitude(saf.getLatitude());
		    	}
		    	if(saf.getLongitude()!= null){
		    		entry.setLongitude(saf.getLongitude());
		    	}
		    	if(saf.getUserBankBranch()!= null){
		    		entry.setUserBankBranch(saf.getUserBankBranch());
		    	}
		    	if(saf.getBankAcountStatus()!= null){
		    		entry.setBankAccountStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankAccountStatus, subscriber.getLanguage(), saf.getBankAcountStatus()));
		    		entry.setBankAccountStatus(String.valueOf(saf.getBankAcountStatus()));
		    	}
			}
        	
	        if(partner.getAddressByFranchiseOutletAddressID() != null){
	        	Address outletAddress = partner.getAddressByFranchiseOutletAddressID();
	        	
	        	if(outletAddress.getLine1() != null){
	        		entry.setCompanyAddress(outletAddress.getLine1());
	        	}
	        	if(outletAddress.getRT() != null){
	        		entry.setRTCom(outletAddress.getRT());
	        	}
	        	if(outletAddress.getRW() != null){
	        		entry.setRWCom(outletAddress.getRW());
	        	}
	        	if(outletAddress.getState() != null){
	        		entry.setDistrictCom(outletAddress.getState());
	        	}
	        	if(outletAddress.getSubState() != null){
	        		entry.setVillageCom(outletAddress.getSubState());
	        	}
	        	if(outletAddress.getCity() != null){
	        		entry.setCityCom(outletAddress.getCity());
	        	}
	        	if(outletAddress.getRegionName() != null){
	        		entry.setProvincialCom(outletAddress.getRegionName());
	        	}	        	
	        	if(outletAddress.getZipCode() != null){
	        		entry.setPotalCodeCom(outletAddress.getZipCode());
	        	}
	        }
	        if(partner.getAddressByMerchantAddressID() != null){
	        	Address merchantAddress = partner.getAddressByMerchantAddressID();
	        	
	        	if(merchantAddress.getLine1() != null){
	        		entry.setAlamatInAccordanceIdentity(merchantAddress.getLine1());
	        	}
	        	if(merchantAddress.getRT() != null){
	        		entry.setRTAl(merchantAddress.getRT());
	        	}
	        	if(merchantAddress.getRW() != null){
	        		entry.setRWAl(merchantAddress.getRW());
	        	}
	        	if(merchantAddress.getState() != null){
	        		entry.setDistrictAl(merchantAddress.getState());
	        	}
	        	if(merchantAddress.getSubState() != null){
	        		entry.setVillageAl(merchantAddress.getSubState());
	        	}
	        	if(merchantAddress.getCity() != null){
	        		entry.setCityAl(merchantAddress.getCity());
	        	}
	        	if(merchantAddress.getRegionName() != null){
	        		entry.setProvincialAl(merchantAddress.getRegionName());
	        	}	        	
	        	if(merchantAddress.getZipCode() != null){
	        		entry.setPotalCodeAl(merchantAddress.getZipCode());
	        	}
	        }
	        if(partner.getID() != null){
	        	entry.setID(partner.getID());
	        }	        
	        if(partner.getLastUpdateTime() != null){
	        	entry.setLastUpdateTime(partner.getLastUpdateTime());
	        }
	        if(partner.getUpdatedBy() != null){
	        	entry.setUpdatedBy(partner.getUpdatedBy());
	        }
	        if(partner.getCreateTime() != null){
	        	entry.setCreateTime(partner.getCreateTime());
	        }
	        if(partner.getCreatedBy() != null){
	        	entry.setCreatedBy(partner.getCreatedBy());
	        }
	        if(partner.getSubscriber() != null){
	        	entry.setSubscriberID(subscriber.getID());
	        	entry.setMDNID(subscriberMdn.getID());
	        }
	        if(partner.getPartnerCode() != null){
	        	entry.setPartnerCode(partner.getPartnerCode());
	        	entry.setAgentCode(partner.getPartnerCode());
	        }
	        if(partner.getPartnerStatus() != null){
	        	entry.setPartnerStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerStatus, subscriber.getLanguage(), partner.getPartnerStatus()));
	        	entry.setPartnerStatus(partner.getPartnerStatus());
	        }
	        if(partner.getTradeName() != null){
	        	entry.setTradeName(partner.getTradeName());
	        }
	        if(partner.getTypeOfOrganization() != null){
	        	entry.setTypeofBusinessAgentText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TypeofBusinessAgent, subscriber.getLanguage(), partner.getTypeOfOrganization()));
	        	entry.setTypeOfOrganization(partner.getTypeOfOrganization());
	        	entry.setTypeofBusinessAgent(partner.getTypeOfOrganization());
	        }
	        if(partner.getFaxNumber() != null){
	        	entry.setFaxNumber(partner.getFaxNumber());
	        }
	        if(partner.getWebSite() != null){
	        	entry.setWebSite(partner.getWebSite());
	        }
	        if(partner.getAuthorizedRepresentative() != null){
	        	entry.setAuthorizedRepresentative(partner.getAuthorizedRepresentative());
	        }
	        if(partner.getRepresentativeName() != null){
	        	entry.setRepresentativeName(partner.getRepresentativeName());
	        }
	        if(partner.getDesignation() != null){
	        	entry.setDesignation(partner.getDesignation());
	        }
	        if(partner.getFranchisePhoneNumber() != null){
	        	entry.setFranchisePhoneNumber(partner.getFranchisePhoneNumber());
	        	entry.setPhoneNumber(partner.getFranchisePhoneNumber());
	        }
	        if(partner.getClassification() != null){
	        	entry.setClassificationAgentText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ClassificationAgent, subscriber.getLanguage(), partner.getClassification()));
	        	entry.setClassification(partner.getClassification());
	        	entry.setClassificationAgent(partner.getClassification());
	        }
	        if(partner.getNumberOfOutlets() != null){
	        	entry.setNumberOfOutlets(partner.getNumberOfOutlets());
	        }
	        if(partner.getIndustryClassification() != null){
	        	entry.setIndustryClassification(partner.getIndustryClassification());
	        }
	        if(partner.getYearEstablished() != null){
	        	entry.setYearEstablished(partner.getYearEstablished());
	        }
	        if(partner.getAuthorizedFaxNumber() != null){
	        	entry.setAuthorizedFaxNumber(partner.getAuthorizedFaxNumber());
	        }
	        if(partner.getAuthorizedEmail() != null){
	        	entry.setAuthorizedEmail(partner.getAuthorizedEmail());
	        	entry.setEMail(partner.getAuthorizedEmail());
	        }
	        if(partner.getCompanyEmailId() != null){
	        	entry.setCompanyEmailId(partner.getCompanyEmailId());
	        }
	        if (partner.getVersion() != null) {
	            entry.setRecordVersion(partner.getVersion());
	        }

	        if (partner.getBankBranchCode() != null) {
	        	entry.setBranchCodeText(userService.getUserBranchCode(partner.getBankBranchCode()));
	            entry.setBranchCode(String.valueOf(partner.getBankBranchCode()));
	        }

	        if (partner.getAccountnumberofBankSinarmas() != null) {
	            entry.setAccountnumberofBankSinarmas(partner.getAccountnumberofBankSinarmas());
	        }

	        if(partner.getBusinessPartnerType() != null){
	        	entry.setBusinessPartnerTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BusinessPartnerTypeAgent, subscriber.getLanguage(), partner.getBusinessPartnerType()));
	        	entry.setBusinessPartnerType(partner.getBusinessPartnerType());
	        	entry.setAgentType(String.valueOf(partner.getBusinessPartnerType()));
	        }
	        entry.setRestrictions(subscriberMdn.getRestrictions());
        }
        if(subscriber != null){
        	if(subscriber.getFirstName() != null){
        		entry.setFirstName(subscriber.getFirstName());
        	}
        	if(subscriber.getLastName() != null){
        		entry.setLastName(subscriber.getLastName());
        	}
	        if(subscriber.getLanguage() != null){
	        	entry.setLanguageText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, subscriber.getLanguage(), subscriber.getLanguage()));
	        	entry.setLanguage(subscriber.getLanguage());
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
	        if(subscriber.getUpgradeState()!=null){
	        	entry.setUpgradeState(subscriber.getUpgradeState());
	        	entry.setUpgradeStateText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_UpgradeState, CmFinoFIX.Language_English, subscriber.getUpgradeState()));
	        }
	        if(subscriber.getAppliedBy()!=null){
	        	entry.setAppliedBy(subscriber.getAppliedBy());
	        }
	        if(subscriber.getAppliedTime()!=null){
	        	entry.setAppliedTime(subscriber.getAppliedTime());
	        }
	        if(subscriber.getApprovedOrRejectedBy()!=null){
	        	entry.setApprovedOrRejectedBy(subscriber.getApprovedOrRejectedBy());
	        }
	        if(subscriber.getApproveOrRejectTime()!=null){
	        	entry.setApproveOrRejectTime(subscriber.getApproveOrRejectTime());
	        }
	        if(subscriber.getApproveOrRejectComment()!=null){
	        	entry.setApproveOrRejectComment(subscriber.getApproveOrRejectComment());
	        }
	        if(subscriber.getBirthPlace()!=null){
	        	entry.setPlaceofBirth(subscriber.getBirthPlace());
	        }
	        if(subscriber.getDateOfBirth()!=null){
	        	entry.setDateofBirth(String.valueOf(subscriber.getDateOfBirth()));
	        }
	        
			if((subscriber.getSubscriberGroupFromSubscriberID() != null) && (subscriber.getSubscriberGroupFromSubscriberID().size() > 0)) {
				SubscriberGroup sg = subscriber.getSubscriberGroupFromSubscriberID().iterator().next();
				entry.setGroupName(sg.getGroup().getGroupName());
				entry.setGroupID(""+sg.getGroup().getID());
			}
        }
        if(partner.getUser()!=null){
        	entry.setUsername(partner.getUser().getUsername());
        	entry.setNameInAccordanceIdentity(partner.getUser().getUsername());
        }
        
    }
    
    private void generateAndSendOTP(Partner partner, SubscriberMDN subscriberMDN, Subscriber subscriber) {
        User u =partner.getUser();
		Integer OTPLength = systemParametersService.getOTPLength();
        String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), oneTimePin);
		subscriberMDN.setOTP(digestPin1);
		subscriberMDN.setOTPExpirationTime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
        String toEmail=u.getEmail();
        String toName=u.getFirstName() + " " + u.getLastName();

        String mdn=subscriberMDN.getMDN();
        NotificationWrapper smsNotificationWrapper = partnerService.genratePartnerOTPMessage(partner, oneTimePin,subscriberMDN.getMDN(), CmFinoFIX.NotificationMethod_SMS);
        String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper,true);;
        smsService.setDestinationMDN(mdn);
        smsService.setMessage(smsMessage);
        smsService.setNotificationCode(smsNotificationWrapper.getCode());
        smsService.asyncSendSMS();
        if(subscriberServiceExtended.isSubscriberEmailVerified(subscriber)) {
        NotificationWrapper emailNotificationWrapper = partnerService.genratePartnerOTPMessage(partner, oneTimePin,subscriberMDN.getMDN(), CmFinoFIX.NotificationMethod_Email);
        String emailMessage = notificationMessageParserService.buildMessage(emailNotificationWrapper,true);; 
        String sub= ConfigurationUtil.getOTPMailSubsject();
        mailService.asyncSendEmail(toEmail, toName, sub, emailMessage);
		log.info("OTP mail sent to subscriber with MDN:" + subscriberMDN.getMDN());
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
			
			if(CmFinoFIX.BusinessPartnerType_ServicePartner.equals(entry.getBusinessPartnerType()) && (entry!= null && !(entry.getID().equals(existingPartner.getID()))))
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
			
			if(CmFinoFIX.BusinessPartnerType_ServicePartner.equals(entry.getBusinessPartnerType()) && (entry!= null && !(entry.getID().equals(existingPartner.getID()))))
			{				
				throw new Exception("Service Partner already defined.");
			}
		}
    	
    }
    
    private void updateEntityAddInfo(Subscriber sub, CMJSAgent.CGEntries entry){
 	  	
		SubscribersAdditionalFields addnlData; 
		if (CollectionUtils.isNotEmpty(sub.getSubscribersAdditionalFieldsFromSubscriberID())) {
			addnlData = sub.getSubscribersAdditionalFieldsFromSubscriberID().iterator().next();
		} 
		else {
			addnlData = new SubscribersAdditionalFields();
			addnlData.setSubscriber(sub);
		}
        if(entry.getElectonicDevieused()!= null){
    		addnlData.setElectonicDeviceUsed(Integer.valueOf(entry.getElectonicDevieused()));
    	}
     	if(entry.getAgreementNumber()!= null){
    		addnlData.setAgreementNumber(entry.getAgreementNumber());
    	}
    	if(entry.getAgreementDate()!= null){
    		addnlData.setAgrementDate(new Timestamp(getDate(entry.getAgreementDate())));
    	}
    	if(entry.getImplementationdate()!= null){
    		addnlData.setImplementatindate(new Timestamp(getDate(entry.getImplementationdate())));
    	}
    	if(entry.getAgentCompanyName()!= null){
    		addnlData.setAgentCompanyName(entry.getAgentCompanyName());
    	}
    	if(entry.getLatitude()!= null){
    		addnlData.setLatitude(entry.getLatitude());
    	}
    	if(entry.getLongitude()!= null){
    		addnlData.setLongitude(entry.getLongitude());
    	}
    	if(entry.getUserBankBranch()!= null){
    		addnlData.setUserBankBranch(entry.getUserBankBranch());
    	}
    	if(entry.getBankAccountStatus()!= null){
    		addnlData.setBankAcountStatus(Integer.valueOf(entry.getBankAccountStatus()));
    	}
    	subAddFldsDAO.save(addnlData);
    }
    
    private void updateEntityAddInfosp(Subscriber sub, CMJSAgent entry){
 	  	
		SubscribersAdditionalFields addnlData; 
		if (CollectionUtils.isNotEmpty(sub.getSubscribersAdditionalFieldsFromSubscriberID())) {
			addnlData = sub.getSubscribersAdditionalFieldsFromSubscriberID().iterator().next();
		} 
		else {
			addnlData = new SubscribersAdditionalFields();
			addnlData.setSubscriber(sub);
		}
        if(entry.getElectonicDevieused()!= null){
    		addnlData.setElectonicDeviceUsed(Integer.valueOf(entry.getElectonicDevieused()));
    	}
     	if(entry.getAgreementNumber()!= null){
    		addnlData.setAgreementNumber(entry.getAgreementNumber());
    	}
    	if(entry.getAgreementDate()!= null){
    		addnlData.setAgrementDate(new Timestamp(getDate(entry.getAgreementDate())));
    	}
    	if(entry.getImplementationdate()!= null){
    		addnlData.setImplementatindate(new Timestamp(getDate(entry.getImplementationdate())));
    	}
    	if(entry.getAgentCompanyName()!= null){
    		addnlData.setAgentCompanyName(entry.getAgentCompanyName());
    	}
    	if(entry.getLatitude()!= null){
    		addnlData.setLatitude(entry.getLatitude());
    	}
    	if(entry.getLongitude()!= null){
    		addnlData.setLongitude(entry.getLongitude());
    	}
    	if(entry.getUserBankBranch()!= null){
    		addnlData.setUserBankBranch(entry.getUserBankBranch());
    	}
    	if(entry.getBankAccountStatus()!= null){
    		addnlData.setBankAcountStatus(Integer.valueOf(entry.getBankAccountStatus()));
    	}
    	subAddFldsDAO.save(addnlData);
    }
    
    public CMJSAgentError verifyAgentData(){
    	CmFinoFIX.CMJSAgentError errorAgentMsg = new CMJSAgentError();
    	errorAgentMsg.setAlamatInAccordanceIdentity("jakarta");
    	errorAgentMsg.setRTAl("01");
    	errorAgentMsg.setRWAl("02");
    	errorAgentMsg.setVillageAl("kel gambir");
    	errorAgentMsg.setDistrictAl("gambir");
    	errorAgentMsg.setCityAl("kota adm");
    	errorAgentMsg.setProvincialAl("dkjakarta");
    	errorAgentMsg.setPotalCodeAl("12910");
    	
    	return errorAgentMsg;
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
