package com.mfino.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.AddressDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PartnerDefaultServicesDAO;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.RolePermissionDAO;
import com.mfino.dao.ScheduleTemplateDAO;
import com.mfino.dao.ServiceSettlementConfigDAO;
import com.mfino.dao.SettlementTemplateDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.PartnerDefaultServicesQuery;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.dao.query.RolePermissionQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Address;
import com.mfino.domain.Company;
import com.mfino.domain.Groups;
import com.mfino.domain.KycLevel;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerDefaultServices;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.RolePermission;
import com.mfino.domain.ScheduleTemplate;
import com.mfino.domain.ServiceSettlementCfg;
import com.mfino.domain.SettlementTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.exceptions.InvalidPasswordException;
import com.mfino.exceptions.MDNNotFoudException;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.exceptions.PartnerRegistrationException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMPartnerRegistrationThroughAPI;
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
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.PasswordGenUtil;


/**
 * @author Maruthi
 *  partner service.
 */
@Service("PartnerServiceImpl")
public class PartnerServiceImpl implements PartnerService {
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	private static Logger log = LoggerFactory.getLogger(PartnerServiceImpl.class);
	  //Before Correcting errors reported by Findbugs:
		//public static List<Integer> agenttypes=new ArrayList<Integer>();
	
	  //After Correcting the errors reported by Findbugs
	public static final List<Integer> agenttypes=new ArrayList<Integer>();
	private PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();	
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void getAgentTypes(){
		HashMap<String, String> results = enumTextService.getEnumTextSet(CmFinoFIX.TagID_BusinessPartnerTypeAgent, null);
		if(!results.isEmpty()){
			Iterator<String> iterator=results.keySet().iterator();
			while(iterator.hasNext()){
				agenttypes.add(Integer.valueOf(iterator.next()));
			}
		}
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public boolean isAgentType(Integer businessPartnerType) {
		if(agenttypes==null||agenttypes.isEmpty()){
			getAgentTypes();
		}
		for(Integer type:agenttypes){
			if(type.equals(businessPartnerType)){
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer getRole(Integer businessPartnerType) {
		//add roles for agents
		if(isAgentType(businessPartnerType)){
			return CmFinoFIX.Role_Business_Partner;
		}
		else if(CmFinoFIX.BusinessPartnerType_BranchOffice.equals(businessPartnerType)){
			return CmFinoFIX.Role_BankTeller;
		}else if(CmFinoFIX.BusinessPartnerType_CorporateUser.equals(businessPartnerType)){
			return CmFinoFIX.Role_Corporate_User;
		}
//		if(CmFinoFIX.BusinessPartnerType_RegulatoryBody.equals(businessPartnerType)){
//			return CmFinoFIX.Role_Integration_Partner;
//		}
//		if(CmFinoFIX.BusinessPartnerType_ServicePartner.equals(businessPartnerType)){
//			return CmFinoFIX.Role_Service_Partner;
//		}
		return CmFinoFIX.Role_Service_Partner;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Partner getPartner(String mdn){
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		Partner partner=null;
		if(StringUtils.isNotBlank(mdn)){
			log.info("getting subscriberMDN for mdn string: "+mdn);
			SubscriberMdn subscriberMDN = subscriberMDNDAO.getByMDN(mdn);
			if(subscriberMDN!=null){
				partner = getPartner(subscriberMDN);
				log.info("got partner: "+partner+" for mdn: "+mdn);
			}
		}
		return partner;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Partner getPartner(SubscriberMdn mdn){
		if(mdn!=null){
			Subscriber agentsubscriber=mdn.getSubscriber();
			
			Long tempTypeL = agentsubscriber.getType().longValue();
			Integer tempTypeLI = tempTypeL.intValue();
			
			if((tempTypeLI.equals(CmFinoFIX.SubscriberType_Partner))){
				Set<Partner> agentPartner = agentsubscriber.getPartners();
				if(!agentPartner.isEmpty()){
					return agentPartner.iterator().next();
				}
			}
		}
		return null;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Partner getPartnerByPartnerCode(String code){
		Partner partner = null;
		if(StringUtils.isNotBlank(code)){
			log.info("getting partner by partner code: "+code);
			PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
			partner=partnerDAO.getPartnerByPartnerCode(code);
		}
		return partner;
	}


	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public String processActivation(String mdn, String otp) throws MfinoRuntimeException{
		Integer result=activatePartner(mdn, otp);
		String response="Unable to process your activation request\n" +
				"Please contact customercare for more info.";
		try {
			//add notifications
			NotificationWrapper notificationWrapper = new NotificationWrapper();
			Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
			SubscriberMdn smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(mdn);
			if(smdn != null)
			{
				language = (int) smdn.getSubscriber().getLanguage();
				notificationWrapper.setFirstName(smdn.getSubscriber().getFirstname());
				notificationWrapper.setLastName(smdn.getSubscriber().getLastname());
				
				Partner partner=smdn.getSubscriber().getPartners().iterator().next();
				notificationWrapper.setPartnerCode(partner.getPartnercode());				
			}
			
			
			notificationWrapper.setLanguage(language);
			 notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Web);
			 notificationWrapper.setCode(result);
			 response = notificationMessageParserService.buildMessage(notificationWrapper,true); 
		}catch (Exception error) {
			log.error("failed to generate message:",error);
			throw new MfinoRuntimeException(error);
		}
		
		return response;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer activatePartner(String mdn, String otp) {
		SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMdn subscriberMDN = subscriberMdnDao.getByMDN(mdn);
		if(subscriberMDN==null){
			return CmFinoFIX.NotificationCode_MDNNotFound;			
		}
		
		if(!CmFinoFIX.SubscriberType_Partner.equals(subscriberMDN.getSubscriber().getType())){
			return CmFinoFIX.NotificationCode_PartnerNotFound;
		}
		
		if(!(CmFinoFIX.SubscriberStatus_Registered.equals(subscriberMDN.getStatus())
				||CmFinoFIX.SubscriberStatus_Initialized.equals(subscriberMDN.getStatus()))){
			return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
		}
		if(!CmFinoFIX.UpgradeState_Approved.equals(subscriberMDN.getSubscriber().getUpgradestate())){
			return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
		}
		Partner partner=subscriberMDN.getSubscriber().getPartners().iterator().next();

// [Bala] Commented this as we dont have Agent App to activet the agent. So we will the Agent also like partner flow for Registration and activation.		
//		if(isAgentType(partner.getBusinessPartnerType())){
//			return CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner;
//		}
		if(!(CmFinoFIX.SubscriberStatus_Registered.equals(partner.getPartnerstatus())
				||CmFinoFIX.SubscriberStatus_Initialized.equals(partner.getPartnerstatus()))){
			return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
		}
		
		if(subscriberMDN.getOtpexpirationtime().before(new Date())){
			return CmFinoFIX.NotificationCode_OTPExpired;			
		}		
		String otpdiget =MfinoUtil.calculateDigestPin(subscriberMDN.getMdn(), otp);
		if(!otpdiget.equals(subscriberMDN.getOtp())){
			return CmFinoFIX.NotificationCode_OTPInvalid;
		}

		Subscriber subscriber =subscriberMDN.getSubscriber();
		MfinoUser user=partner.getMfinoUser();
		
		Pocket bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		Long groupID = null;
		SubscriberGroupDao sgDao = DAOFactory.getInstance().getSubscriberGroupDao();
		SubscriberGroups subscriberGroup = sgDao.getBySubscriberID(subscriber.getId().longValue());
		if(subscriberGroup != null)
		{
			groupID = subscriberGroup.getGroupid();
		}
		PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(subscriber.getKycLevel().getKyclevel().longValue(), Boolean.TRUE, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, partner.getBusinesspartnertype().intValue(), groupID);
		Pocket emoneyPocket =subscriberService.getDefaultPocket(subscriberMDN.getId().longValue(), svaPocketTemplate.getId().longValue());
		
		if(emoneyPocket==null){
			return CmFinoFIX.NotificationCode_MoneySVAPocketNotFound;
		}
		if(bankPocket==null){
			return CmFinoFIX.NotificationCode_DefaultBankAccountPocketNotFound;
		}
		
		Long emoneyPocketL = emoneyPocket.getStatus().longValue();
		Integer emoneyPocketLI = emoneyPocketL.intValue();
		
		Long bankPocketL = bankPocket.getStatus().longValue();
		Integer bankPocketLI = bankPocketL.intValue();
		
		if(emoneyPocketLI.equals(CmFinoFIX.PocketStatus_PendingRetirement)
				||emoneyPocketLI.equals(CmFinoFIX.PocketStatus_Retired)
				||bankPocketLI.equals(CmFinoFIX.PocketStatus_PendingRetirement)
				||bankPocketLI.equals(CmFinoFIX.PocketStatus_Retired)){
			return CmFinoFIX.NotificationCode_PocketStatusDoesNotEnableActivation;			
		}
		String tradeName=partner.getTradename();
		 if(!emoneyPocketLI.equals(CmFinoFIX.PocketStatus_Active)){
			emoneyPocket.setActivationtime(new Timestamp());
			emoneyPocket.setIsdefault(true);
			emoneyPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			emoneyPocket.setStatustime(new Timestamp());
			emoneyPocket.setUpdatedby(tradeName);
			}
		 if(!bankPocketLI.equals(CmFinoFIX.PocketStatus_Active)){
				bankPocket.setActivationtime(new Timestamp());
				bankPocket.setIsdefault(true);
				bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
				bankPocket.setStatustime(new Timestamp());
				bankPocket.setUpdatedby(tradeName);
				pocketDAO.save(bankPocket);
				log.info("PartnerActivation : bankPocket activation id:"+bankPocket.getId()+" agentid"+partner.getId());
			}
		
		RolePermissionQuery query = new RolePermissionQuery();
		query.setPermission(CmFinoFIX.Permission_PinPrompt);
		query.setUserRole(user.getRole().intValue());
		RolePermissionDAO rolePermissionDAO = DAOFactory.getInstance().getRolePermissionDAO();
		List<RolePermission> rolePermission = rolePermissionDAO.get(query);
		
		if(rolePermission==null||rolePermission.isEmpty()){
			Integer OTPLength = systemParametersService.getOTPLength();
			String newpin = MfinoUtil.generateOTP(OTPLength);
			//newpin = newpin.substring(0,SystemParametersUtil.getInteger(SystemParameterKeys.OTP_LENGTH));
			String digestpin = MfinoUtil.calculateDigestPin(subscriberMDN.getMdn(), newpin);
			subscriberMDN.setDigestedpin(digestpin);
			String authToken = MfinoUtil.calculateAuthorizationToken(subscriberMDN.getMdn(), newpin);
			subscriberMDN.setAuthorizationtoken(authToken);
		}
		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriberMDN.setStatustime(new Timestamp());
		subscriberMDN.setActivationtime(new Timestamp());
		subscriberMDN.setUpdatedby(tradeName);
		subscriber.setUpdatedby(tradeName);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriber.setStatustime(new Timestamp());
		subscriber.setActivationtime(new Timestamp());
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
		subscriberMDN.setOtp(null);
		subscriberMDN.setOtpexpirationtime(null);
		if(emoneyPocket!=null){
			pocketDAO.save(emoneyPocket);
			log.info("PartnerActivation : emoneyPocket with id:"+emoneyPocket.getId()+" agent:"+partner.getId());
		}
		partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_Active);
		user.setStatus(CmFinoFIX.UserStatus_Active);
		user.setStatustime(new Timestamp());
		user.setFailedlogincount(0);
		user.setFirsttimelogin(Boolean.TRUE);
		user.setUseractivationtime(new Timestamp());
		user.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		String password = PasswordGenUtil.generate();
		PasswordEncoder encoder = new ShaPasswordEncoder(1);
		String encPassword = encoder.encodePassword(password, user.getUsername());
		user.setPassword(encPassword);
		String emailSubject = "Partner Registration";
		String emailMsg = genratePartnerRegistrationMail(partner, user,partner.getBusinesspartnertype().intValue(), password);
		String email=partner.getAuthorizedemail();
		String to=partner.getTradename();
		activateServices(partner);
		activateNonTransactionable(subscriberMDN);
		SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
		UserDAO userDao = DAOFactory.getInstance().getUserDAO();
		PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
		
		userDao.save(user);
		partnerDao.save(partner);
		subscriberDAO.save(subscriber);
		subscriberMdnDao.save(subscriberMDN);	
		
		subscriberServiceExtended.updateUnRegisteredTxnInfoToActivated(subscriberMDN);
		
		//mailService.asyncSendEmail(email,to , emailSubject,emailMsg);
		String smsMsg="activation notification";
		NotificationWrapper notificationWrapper = new NotificationWrapper();
		try {
			 //add notifications
			Long languangeL = subscriber.getLanguage().longValue();
			Integer languangeLI = languangeL.intValue();
			
			 notificationWrapper.setLanguage(languangeLI);
			 notificationWrapper.setCompany(subscriber.getCompany());
			 notificationWrapper.setFirstName(subscriber.getFirstname());
			 notificationWrapper.setLastName(subscriber.getLastname());				
			 notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			 notificationWrapper.setPartnerCode(partner.getPartnercode());
			 notificationWrapper.setCode(CmFinoFIX.NotificationCode_PartnerActivationCompleted);//partneractivation
			 smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); //use thread pool to send message
		}catch (Exception e) {
			log.error("failed to generate message:",e);
		}
			String mdn1 = partner.getFranchisephonenumber();
			smsService.setDestinationMDN(mdn1);
			smsService.setMessage(smsMsg);
			smsService.asyncSendSMS();
			
		return CmFinoFIX.NotificationCode_PartnerActivationCompleted;//partneractivation
	}
	
	private boolean checkIsAllowed(String businessPartnerType) {
		try{
			String[] allowedPartners = systemParametersService.getString(SystemParameterKeys.ALLOWED_PARTNERS_TOREGISTER_THROUGHAPI).split(GeneralConstants.COMMA_STRING);
			for(String type:allowedPartners){
				if(type.equalsIgnoreCase(businessPartnerType))
					return true;
			}
		}catch (Exception e) {
			log.error("checkIsAllowed():Error",e);
		}
			return false;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void activateServices(Partner partner) {
		Set<PartnerServices> ps=partner.getPartnerServicesesForPartnerid();
		PartnerServicesDAO psDao = DAOFactory.getInstance().getPartnerServicesDAO();
		for(PartnerServices service:ps){
			Long tempStatusL = service.getStatus().longValue();
			Integer tempStatusLI = tempStatusL.intValue();
			if(tempStatusLI.equals(CmFinoFIX.PartnerServiceStatus_Initialized)
					&&checkPocket(service.getPocketBySourcepocket(),true)
					&&checkPocket(service.getPocketByDestpocketid(),false)
					&&checkPocket(service.getPocketBySourcepocket(),false)){
				service.setStatus(CmFinoFIX.PartnerServiceStatus_Active);
				String tradeName=partner.getTradename();
				service.setUpdatedby(tradeName);
				psDao.save(service);
			}
		}
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public  boolean checkPocket(Pocket pocket, boolean isCollector) {
		if (pocket == null) {
			return isCollector ? false : true;
		} else if (!(CmFinoFIX.PocketStatus_Active.equals(pocket.getStatus()) || CmFinoFIX.PocketStatus_Initialized.equals(pocket.getStatus()))) {
			return false;
		} else if(!isCollector&&!Boolean.valueOf(pocket.getIsdefault().toString())&&CmFinoFIX.PocketStatus_Initialized.equals(pocket.getStatus())) {
			PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
			pocket.setActivationtime(new Timestamp());
			pocket.setStatus(CmFinoFIX.PocketStatus_Active);
			pocket.setStatustime(new Timestamp());
			pocketDao.save(pocket);
		}

		return true;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void activateNonTransactionable(SubscriberMdn subscriberMDN) {
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		Set<Pocket> pockets = subscriberMDN.getPockets();
		for(Pocket pocket:pockets){
			Long tempStatusL = pocket.getStatus().longValue();
			Integer tempStatusLI = tempStatusL.intValue();
			if(tempStatusLI.equals(CmFinoFIX.PocketStatus_Initialized)&&
					(pocket.getPocketTemplateByPockettemplateid().getIscollectorpocket()
							|| pocket.getPocketTemplateByPockettemplateid().getIssuspencepocket()
							|| pocket.getPocketTemplateByPockettemplateid().getIssystempocket())){
					
				pocket.setActivationtime(new Timestamp());
//				pocket.setIsDefault(true);
				pocket.setStatus(CmFinoFIX.PocketStatus_Active);
				pocket.setStatustime(new Timestamp());
				pocketDAO.save(pocket);
			}
		}
		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void retireServices(Partner objPartner) {
		Set<PartnerServices> partnerServices = objPartner.getPartnerServicesesForPartnerid();
		PartnerServicesDAO psDao = DAOFactory.getInstance().getPartnerServicesDAO();
		for(PartnerServices ps:partnerServices){
			Long tempStatusL = ps.getStatus().longValue();
			Integer tempStatusLI = tempStatusL.intValue();
			 if (!tempStatusLI.equals(CmFinoFIX.PartnerServiceStatus_Retired)) {
				 ps.setStatus(CmFinoFIX.PartnerServiceStatus_PendingRetirement);
	              psDao.save(ps);
	            }
		}		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void changePin(String username, String transactionPin) throws Exception {
		 MfinoUser user;
	        UserQuery query = new UserQuery();
	        query.setUserName(username);
	        UserDAO userdao = DAOFactory.getInstance().getUserDAO();
	        List<MfinoUser> results = userdao.get(query);
	        if (results != null && results.size() > 0) {
	            user = results.get(0);
	            Set<Partner> partners = user.getPartners();
	            if(StringUtils.isBlank(transactionPin)) {
	            	throw new InvalidPasswordException("Invalid Pin");
	            }
	            if(!MfinoUtil.isPinStrongEnough(transactionPin)) {
	            	throw new InvalidPasswordException("Pin is not strong enough.");
	            }
	            if(partners.isEmpty()){
	            	throw new MDNNotFoudException("Partner not found");
	            }
				Partner partner = partners.iterator().next();
				Subscriber subscriber = partner.getSubscriber();
				SubscriberMdn subMdn = subscriber.getSubscriberMdns().iterator().next();
				String calcPIN = MfinoUtil.calculateDigestPin(subMdn.getMdn(), transactionPin);
				subMdn.setDigestedpin(calcPIN);
				String authToken = MfinoUtil.calculateAuthorizationToken(subMdn.getMdn(), transactionPin);
				subMdn.setAuthorizationtoken(authToken);
				SubscriberMDNDAO mdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
				mdnDao.save(subMdn);
	        } else {
	            throw new UsernameNotFoundException(MessageText._("Invalid username or password"));
	        }
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Partner getPartnerById(Long partnerId){
		log.info("PartnerService : getPartnerById BEGIN");
		PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
		Partner partner = partnerDao.getById(partnerId);
		
		log.info("PartnerService : getPartnerById END");
		return partner;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<Partner> get(PartnerQuery query){
		PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
		return partnerDao.get(query);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void savePartner(Partner partner){
		PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
		partnerDAO.save(partner);
	}
	
	/**
	 * 
	 * @param partner
	 * @param user
	 * @param type
	 * @param password
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public String genratePartnerRegistrationMail(Partner partner, MfinoUser user, Integer type, String password) {
		String mailBody = ConfigurationUtil.getPartnerRegistrationMail();
		//mailBody = mailBody.replace("$(firstname)", user.getFirstName());
		//mailBody = mailBody.replace("$(Lastname)", user.getLastName());
		mailBody = mailBody.replace("$(tradename)", partner.getTradename());
		mailBody = mailBody.replace("$(partnerCode)", partner.getPartnercode());
		mailBody = mailBody.replace("$(username)", user.getUsername());
		mailBody = mailBody.replace("$(password)", password);
		mailBody = mailBody.replace("$(partnerType)", enumTextService.getEnumTextValue(CmFinoFIX.TagID_BusinessPartnerType, CmFinoFIX.Language_English, type));
		return mailBody;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mfino.service.PartnerService#genratePartnerOTPMessage(com.mfino.domain.Partner, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public NotificationWrapper genratePartnerOTPMessage(Partner partner, String oneTimePin, String mdn, Integer notificationMethod) {
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		language = (int) partner.getSubscriber().getLanguage();
// [Bala] Commented this as we dont have Agent App to activet the agent. So we will the Agent also like partner flow for Registration and activation.		
//		if (isAgentType(partner.getBusinessPartnerType())) {
//			 NotificationWrapper notificationWrapper =  subscriberServiceExtended.generateOTPMessage(oneTimePin, notificationMethod);
//			 notificationWrapper.setLanguage(language);
//			 notificationWrapper.setFirstName(partner.getSubscriber().getFirstName());
//			 notificationWrapper.setLastName(partner.getSubscriber().getLastName());
//			 notificationWrapper.setPartnerCode(partner.getPartnerCode());
//			 notificationWrapper.setCode(CmFinoFIX.NotificationCode_PromptForActivation);
//			 return notificationWrapper;
//		}

		NotificationWrapper notificationWrapper = new NotificationWrapper();
		notificationWrapper.setNotificationMethod(notificationMethod);
		notificationWrapper.setPartnerCode(partner.getPartnercode());
		notificationWrapper.setCode(CmFinoFIX.NotificationCode_PartnerActivation);
		notificationWrapper.setOneTimePin(oneTimePin);
		notificationWrapper.setService("activation");
		notificationWrapper.setAppURL(ConfigurationUtil.getAppURL());
		notificationWrapper.setSourceMDN(mdn);
		notificationWrapper.setLanguage(language);
		notificationWrapper.setFirstName(partner.getSubscriber().getFirstname());
		notificationWrapper.setLastName(partner.getSubscriber().getLastname());
		
		return notificationWrapper;
	}

	public List<PartnerServices> getPartnerServices(Long partnerId, Long serviceProviderId, Long serviceId) {
		PartnerServicesDAO partnerServicesDao=DAOFactory.getInstance().getPartnerServicesDAO();

		List<PartnerServices> partnerServices = partnerServicesDao.getPartnerServices(partnerId, serviceProviderId, serviceId);
		return partnerServices;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Partner registerPartner(CMPartnerRegistrationThroughAPI partnerRegistration) throws Exception{
		log.info("registerPartner():Begin:"+partnerRegistration.getMDN());
		PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
		PartnerQuery query = new PartnerQuery();
		query.setPartnerCode(partnerRegistration.getPartnerCode());
		List<Partner> results = partnerDAO.get(query);
		if (CollectionUtils.isNotEmpty(results) && results.size() > 0)
			 throw new PartnerRegistrationException("Partner Code already exists");
		
		query.setPartnerCode(null);
		query.setTradeName(partnerRegistration.getTradeName());
		results = partnerDAO.get(query);
		if (CollectionUtils.isNotEmpty(results) && results.size() > 0) 
			throw new PartnerRegistrationException("TradeName already exists");
			 
		//set default fields
		setDefaultValues(partnerRegistration);
		
		Partner partner = new Partner();
		SubscriberMdn subscriberMDN;
		Subscriber subscriber;
		MfinoUser user ;
		
		subscriberMDN= createSubscriberMDNEntityForPartner(partnerRegistration);
		subscriber= subscriberMDN.getSubscriber();
		user= createUserEntityForPartner(partnerRegistration);        
        
          partner.setSubscriber(subscriber);
          partner.setMfinoUser(user);
          updatePartner(partner, partnerRegistration);

          SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
          UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
          SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
             
          userDAO.save(user);                                               
          subscriberDAO.save(subscriber);
          subMdndao.save(subscriberMDN);
          partnerDAO.save(partner);
          SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
          List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(subscriber.getId());
          if(subscriberGroups!= null && subscriberGroups.size() > 0){
				for(SubscriberGroups sg: subscriberGroups){
					subscriberGroupDao.save(sg);
				}
			}
          Map<Integer, Pocket> defaultPockets =  createPocketsForPartner(partner, partnerRegistration.getAccountNumber());
          if(defaultPockets==null||defaultPockets.get(CmFinoFIX.ServicePocketType_Bank)==null)
        		throw new PartnerRegistrationException("Required Pockets creation failed");
        		
          createServiceForPartner(partner, null,defaultPockets);
           
           if(partnerRegistration.getAuthorizedEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
           		mailService.generateEmailVerificationMail(subscriber, partnerRegistration.getAuthorizedEmail());
 			}
           
         if(!partnerRegistration.getApprovalRequired()){   		
   			String email=subscriber.getEmail();
   			String to=partner.getTradename();
   			String mdn=subscriberMDN.getMdn();
   			NotificationWrapper smsWrapper = genratePartnerOTPMessage(partner, partnerRegistration.getOTP(),subscriberMDN.getMdn(), CmFinoFIX.NotificationMethod_SMS);
   			NotificationWrapper emailWrapper = genratePartnerOTPMessage(partner, partnerRegistration.getOTP(),subscriberMDN.getMdn(), CmFinoFIX.NotificationMethod_Email);
   			SubscriberMdn smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(mdn);
   			if(smdn != null)
   			{
   				smsWrapper.setFirstName(smdn.getSubscriber().getFirstname());
   				smsWrapper.setLastName(smdn.getSubscriber().getLastname());	
   				emailWrapper.setFirstName(smdn.getSubscriber().getFirstname());
   				emailWrapper.setLastName(smdn.getSubscriber().getLastname());
   			}
   			smsWrapper.setDestMDN(subscriberMDN.getMdn());
   			String smsMessage = notificationMessageParserService.buildMessage(smsWrapper,true);

   			smsService.setDestinationMDN(mdn);
   			smsService.setMessage(smsMessage);
   			smsService.setNotificationCode(smsWrapper.getCode());
   			smsService.asyncSendSMS();
   			
   			emailWrapper.setDestMDN(subscriberMDN.getMdn());
   			String emailMessage = notificationMessageParserService.buildMessage(emailWrapper,true);
   			String sub= ConfigurationUtil.getOTPMailSubsject();
   			mailService.asyncSendEmail(email, to, sub, emailMessage);
   		}
         log.info("registerPartner():End:"+partnerRegistration.getMDN());
         return partner;		
	}
	
	private void setDefaultValues(CMPartnerRegistrationThroughAPI partnerRegistration) {
		if(StringUtils.isBlank(partnerRegistration.getFirstName()))
			partnerRegistration.setFirstName(partnerRegistration.getTradeName());
		if(StringUtils.isBlank(partnerRegistration.getLastName()))
			partnerRegistration.setLastName(partnerRegistration.getTradeName());
		if(StringUtils.isBlank(partnerRegistration.getUsername()))
			partnerRegistration.setUsername(userService.generateUserName(partnerRegistration.getTradeName()));
		if(StringUtils.isBlank(partnerRegistration.getTimezone())){
			if(ConfigurationUtil.getLocalTimeZone()!=null){
				partnerRegistration.setTimezone(ConfigurationUtil.getLocalTimeZone().getDisplayName());
			}
			else{
				partnerRegistration.setTimezone(systemParametersService.getString(SystemParameterKeys.TIME_ZONE));
			}
		}
		if(partnerRegistration.getLanguage()==null) {
			Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
			partnerRegistration.setLanguage(language);
		}
		if(partnerRegistration.getGroupID()==null){
			Groups defaultGroup = DAOFactory.getInstance().getGroupDao().getSystemGroup();
			partnerRegistration.setGroupID(defaultGroup.getId().toString());
		}
		if(StringUtils.isBlank(partnerRegistration.getTypeOfOrganization()))
			partnerRegistration.setTypeOfOrganization(CmFinoFIX.TypeOfOrganization_Others);
		if(partnerRegistration.getApprovalRequired()==null)
			partnerRegistration.setApprovalRequired(Boolean.valueOf(systemParametersService.getString(SystemParameterKeys.PARTNER_REGISTER_THROUGHAPI_APPROVAL)));
	}

	private MfinoUser createUserEntityForPartner(CMPartnerRegistrationThroughAPI partnerRegistration) throws Exception{
		 MfinoUser user = new MfinoUser();
         Company company = subscriberService.getCompanyFromMDN(partnerRegistration.getMDN());
         if (company != null) {
             user.setCompany(company);
          } else {
            log.info("Company does not exist for MDN");
            throw new PartnerRegistrationException("Company does not exist for MDN");
         }
         String username = partnerRegistration.getUsername();
         if(userService.getByUserName(username)!=null){
        	 throw new PartnerRegistrationException("User already Exist with this user name");
         }
         user.setUsername(username);
         user.setEmail(partnerRegistration.getAuthorizedEmail());
         user.setFirstname(partnerRegistration.getFirstName());
         user.setLastname(partnerRegistration.getLastName());
         user.setLanguage(partnerRegistration.getLanguage());
         user.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
         user.setStatus(CmFinoFIX.UserStatus_Registered);
         user.setTimezone(partnerRegistration.getTimezone());
         user.setRole(getRole(partnerRegistration.getBusinessPartnerType()).longValue());
         
         return user;		
	}
	private SubscriberMdn createSubscriberMDNEntityForPartner(CMPartnerRegistrationThroughAPI partnerRegistration) throws Exception{
		SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMdn subscriberMDN = subMdndao.getByMDN(subscriberService.normalizeMDN(partnerRegistration.getMDN()));
		Subscriber subscriber;
		boolean isExistingMDN = false;
		if(subscriberMDN==null){
       	  	  subscriberMDN=new SubscriberMdn();
         	  subscriber=new Subscriber();
         	  MfinoServiceProviderDAO mspDAO =DAOFactory.getInstance().getMfinoServiceProviderDAO();
              subscriber.setMfinoServiceProvider(mspDAO.getById(1));
              subscriberMDN.setMdn(subscriberService.normalizeMDN(partnerRegistration.getMDN()));
              subscriberMDN.setSubscriber(subscriber);
              subscriber.getSubscriberMdns().add(subscriberMDN);
              Company company = subscriberService.getCompanyFromMDN(subscriberMDN.getMdn());
              if (company != null) {
                  subscriber.setCompany(company);
               } else {
                 log.info("Company does not exist for MDN");
                 throw new PartnerRegistrationException("Company does not exist for MDN");
              }
         }else{
        	 isExistingMDN = true;
        	 subscriber = subscriberMDN.getSubscriber();
        	 if(CmFinoFIX.SubscriberType_Partner.equals(subscriber.getType()))
       		  		throw new PartnerRegistrationException("Partner already exist with MDN");
        	 
        	 Long tempStatusL = subscriberMDN.getStatus().longValue();
        	 Integer tempStatusLI= tempStatusL.intValue();
        	 
         	 if(tempStatusLI.equals(CmFinoFIX.SubscriberStatus_PendingRetirement)
         			 ||tempStatusLI.equals(CmFinoFIX.SubscriberStatus_Retired))
         		 throw new PartnerRegistrationException("Invalid MDN status not allowed to register as partner");
         }
		 subscriber.setType(CmFinoFIX.SubscriberType_Partner);
         KYCLevelDAO kyclevelDao  = DAOFactory.getInstance().getKycLevelDAO();
         KycLevel kycLevel = kyclevelDao.getByKycLevel(ConfigurationUtil.getBulkUploadSubscriberKYClevel());
         subscriber.setKycLevel(kycLevel);
         subscriber.setUpgradablekyclevel(null);
         Integer tempupgradeState = partnerRegistration.getApprovalRequired()?CmFinoFIX.UpgradeState_Upgradable:CmFinoFIX.UpgradeState_Approved;
         subscriber.setUpgradestate(tempupgradeState);
         subscriber.setRegistrationmedium(CmFinoFIX.RegistrationMedium_Web);
         subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS|CmFinoFIX.NotificationMethod_Email);
         if(!partnerRegistration.getApprovalRequired()){
        	 Integer OTPLength = systemParametersService.getOTPLength();
             String oneTimePin = MfinoUtil.generateOTP(OTPLength);
             partnerRegistration.setOTP(oneTimePin);
     		 String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMdn(), oneTimePin);
     		 subscriberMDN.setOtp(digestPin1);
 			 subscriberMDN.setOtpexpirationtime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
		 }
         subscriberMDN.setAuthorizationtoken(null);
         subscriber.setLastname(partnerRegistration.getLastName());
         subscriber.setAppliedby("System");
         subscriber.setAppliedtime(new Timestamp());
         subscriber.setApprovedorrejectedby("");
         subscriber.setApproveorrejectcomment("");
         subscriber.setApproveorrejecttime(partnerRegistration.getApprovalRequired()?new Timestamp():null);
         subscriberMDN.setStatus(CmFinoFIX.MDNStatus_Initialized);
	     subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
	     subscriberMDN.setStatustime(new Timestamp());
	     subscriber.setStatustime(new Timestamp());
	     subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
		 
		  if (partnerRegistration.getLanguage() != null) {
	            subscriber.setLanguage(partnerRegistration.getLanguage());
	        }
//		  	else if(!isExistingMDN){
//	        	subscriber.setLanguage(CmFinoFIX.Language_English);
//	        }	
		  else
		  {
			  subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
		  }
		   if(ConfigurationUtil.getLocalTimeZone()!=null){
				subscriber.setTimezone(ConfigurationUtil.getLocalTimeZone().getDisplayName());
			}
			else{
				subscriber.setTimezone(systemParametersService.getString(SystemParameterKeys.TIME_ZONE));
			}	
			subscriber.setCurrency(systemParametersService.getString(SystemParameterKeys.DEFAULT_CURRENCY_CODE));
			subscriber.setEmail(partnerRegistration.getAuthorizedEmail());
        	subscriber.setIsemailverified(true);
        	
        	if(StringUtils.isNotBlank(partnerRegistration.getGroupID())){  
        		
    			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
    			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();

    	        List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(subscriber.getId());
    			if((subscriberGroups != null) && (subscriberGroups.size() > 0)){
    				SubscriberGroups sg = subscriberGroups.iterator().next();
    				if(sg.getGroupid() != Long.valueOf(partnerRegistration.getGroupID())){
    					sg.setGroupid(Long.valueOf(partnerRegistration.getGroupID()));
    					subscriberGroupDao.save(sg);
    				}
    			}
    			else{
    				Groups group = (Groups)groupDao.getById(Long.valueOf(partnerRegistration.getGroupID()));
    				SubscriberGroups sg = new SubscriberGroups();
    				sg.setSubscriber(subscriber);
    				sg.setGroupid(group.getId().longValue());
    			    subscriberGroupDao.save(sg);
    				
    			}
    		}            
         
		return subscriberMDN;		
	}
	
	private Map<Integer, Pocket> createPocketsForPartner(Partner partner,String bankCardPan) throws Exception{
		Subscriber subscriber = partner.getSubscriber();
		SubscriberMdn subscriberMDN = subscriber.getSubscriberMdns().iterator().next();
		SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
		List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(subscriber.getId());
		GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
		Groups group = groupDao.getById(subscriberGroups.iterator().next().getGroupid());
		Map<Integer, Pocket> defaultPockets = getDefaultPocketsMap(partner);
		
		// Emoney Pocket creation
		if (defaultPockets.get(CmFinoFIX.ServicePocketType_Emoney) == null) {
				PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), Boolean.TRUE,CmFinoFIX.PocketType_SVA,CmFinoFIX.SubscriberType_Partner, partner.getBusinesspartnertype().intValue(), group.getId().longValue());
				if (svaPocketTemplate == null) {
					log.info("No Default SVA Pocket template set for groupID:"+ group.getId() + "(" + group.getDescription()+ ")" + " PartnerType:"+ partner.getBusinesspartnertype());
					throw new PartnerRegistrationException("No Default SVA Pocket template set for groupID:"+ group.getId() + "(" + group.getDescription()+ ")" + " PartnerType:"+ partner.getBusinesspartnertype());					
				} else {
					String cardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMdn());
					Pocket pocket = pocketService.createPocket(svaPocketTemplate, subscriberMDN,CmFinoFIX.PocketStatus_Initialized, true, cardPan);
					log.info("Default emoney pocket successfully created for the partner -->"+ partner.getId() + " pocketID:" + pocket.getId());
					subscriberMDN.getPockets().add(pocket);
					defaultPockets.put(CmFinoFIX.ServicePocketType_Emoney, pocket);
				}
		}
		
		// bank Pocket creation
		if (defaultPockets.get(CmFinoFIX.ServicePocketType_Bank) == null
				&& StringUtils.isNotBlank(bankCardPan)) {
				PocketTemplate bankTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), Boolean.TRUE,CmFinoFIX.PocketType_BankAccount,CmFinoFIX.SubscriberType_Partner, partner.getBusinesspartnertype().intValue(), group.getId().longValue());
				if (bankTemplate == null) {
					log.info("No Default bank Pocket template set for groupID:"+ group.getId() + "(" + group.getDescription()+ ")" + " PartnerType:"+ partner.getBusinesspartnertype());
					throw new PartnerRegistrationException("No Default bank Pocket template set for groupID:"+ group.getId() + "(" + group.getDescription()+ ")" + " PartnerType:"+ partner.getBusinesspartnertype());
				} else {
					Pocket bankPocket = pocketService.createPocket(bankTemplate, subscriberMDN,CmFinoFIX.PocketStatus_Initialized, true,bankCardPan);
					log.info("Default bank pocket Id --> " + bankPocket.getId()	+ " partnerid:" + partner.getId());
					subscriberMDN.getPockets().add(bankPocket);
					defaultPockets.put(CmFinoFIX.ServicePocketType_Bank, bankPocket);
				}
			}

		// collector Pocket creation
		if (defaultPockets.get(CmFinoFIX.ServicePocketType_Collector) == null) {
				PocketTemplate collectorPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE,CmFinoFIX.PocketType_SVA,CmFinoFIX.SubscriberType_Partner, partner.getBusinesspartnertype().intValue(), group.getId().longValue());
				if (collectorPocketTemplate == null) {
					log.info("No Default collector Pocket template set for groupID:"+ group.getId()+ "("+ group.getDescription()+ ")"+ " PartnerType:"+ partner.getBusinesspartnertype());
					throw new PartnerRegistrationException("No Default collector Pocket template set for groupID:"+ group.getId()+ "("+ group.getDescription()+ ")"+ " PartnerType:"+ partner.getBusinesspartnertype());
				} else {
					String collectorPocketCardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMdn());
					Pocket collectorPocket = pocketService.createPocket(collectorPocketTemplate, subscriberMDN,CmFinoFIX.PocketStatus_Initialized, true,collectorPocketCardPan);
					log.info("Default collector pocket Id --> "+ collectorPocket.getId() + " partnerID:"+ partner.getId());
					subscriberMDN.getPockets().add(collectorPocket);
					defaultPockets.put(CmFinoFIX.ServicePocketType_Collector, collectorPocket);
				}
			}
		
		// suspence Pocket creation
		if (defaultPockets.get(CmFinoFIX.ServicePocketType_Suspence) == null) {
				PocketTemplate suspensePocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, true, false,CmFinoFIX.PocketType_SVA,CmFinoFIX.SubscriberType_Partner, partner.getBusinesspartnertype().intValue(), group.getId().longValue());
				if (suspensePocketTemplate == null) {
					log.info("No Default suspence Pocket template set for groupID:"+ group.getId()+ "("+ group.getDescription()+ ")"+ " PartnerType:"+ partner.getBusinesspartnertype());
					throw new PartnerRegistrationException("No Default suspence Pocket template set for groupID:"+ group.getId()+ "("+ group.getDescription()+ ")"+ " PartnerType:"+ partner.getBusinesspartnertype());	
				} else {
					String suspensePocketCardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMdn());
					Pocket suspensePocket = pocketService.createPocket(suspensePocketTemplate, subscriberMDN,CmFinoFIX.PocketStatus_Initialized, true,suspensePocketCardPan);
					log.info("Default suspense pocket Id --> "+ suspensePocket.getId() + " partnerid:"+ partner.getId());
					subscriberMDN.getPockets().add(suspensePocket);
					defaultPockets.put(CmFinoFIX.ServicePocketType_Suspence, suspensePocket);
				}
			}
		return defaultPockets;
	}
	
	private List<PartnerServices> createServiceForPartner(Partner partner,SettlementTemplate settlementTemplate,Map<Integer, Pocket> pockets) throws Exception{
		CMJSError error = new CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		PartnerServicesDAO partnerServicesDAO = DAOFactory.getInstance().getPartnerServicesDAO();
		PartnerDefaultServicesDAO partnerDefaultServicesDAO = DAOFactory.getInstance().getPartnerDefaultServicesDAO();
		MfinoServiceProviderDAO mspDAO =DAOFactory.getInstance().getMfinoServiceProviderDAO();
		
		PartnerDefaultServicesQuery query = new PartnerDefaultServicesQuery();
		query.setBusinessPartnerType(partner.getBusinesspartnertype().intValue());
		List<PartnerDefaultServices> partnerDefaultServices = partnerDefaultServicesDAO.get(query);
		if(partnerDefaultServices == null||partnerDefaultServices.isEmpty()){
			log.info("Default services not configured for partner type:"+partner.getBusinesspartnertype());
			throw new PartnerRegistrationException("Default services not configured for partner type:"+partner.getBusinesspartnertype());			
		}
		if(pockets == null)
			pockets = getDefaultPocketsMap(partner);
		if(pockets == null){
			log.info("Default pockets not set for partner");
			throw new PartnerRegistrationException("Default pockets not set for partner");
		}
		
		if(settlementTemplate == null)
			settlementTemplate = createSettlementTemplate(partner,pockets.get(CmFinoFIX.ServicePocketType_Bank));
		List<PartnerServices> partnerServices= new ArrayList<PartnerServices>();		
		for(PartnerDefaultServices defaultService:partnerDefaultServices){
			PartnerServices partnerService = new PartnerServices();
			partnerService.setPartnerByParentid(partner);
			partnerService.setMfinoServiceProvider(mspDAO.getById(1l));
			partnerService.setPartnerByPartnerid(defaultService.getServiceDefualtConfig().getPartner());
			partnerService.setService(defaultService.getServiceDefualtConfig().getService());
			partnerService.setCollectorpocket(pockets.get(CmFinoFIX.ServicePocketType_Collector));
			partnerService.setPocketBySourcepocket(pockets.get(defaultService.getServiceDefualtConfig().getSourcepockettype()));
			partnerService.setPocketByDestpocketid(pockets.get(defaultService.getServiceDefualtConfig().getSourcepockettype()));
			partnerService.setIsservicechargeshare(CmFinoFIX.IsServiceChargeShare_Individual);
			partnerServicesDAO.save(partnerService);
			log.info("PartnerService created for Partnerid:"+partner.getId()+"partnerserviceid:"+partnerService.getId());
			createServiceSettlementConfig(partnerService,settlementTemplate);
			partnerServices.add(partnerService);
		}
		return partnerServices;
				
	}
	
	private ServiceSettlementCfg createServiceSettlementConfig(PartnerServices partnerService,SettlementTemplate settlementTemplate) throws Exception {
		if(partnerService==null||settlementTemplate==null){
			log.info("ServiceSettlementConfig not created as partnerservice="+partnerService+"SettlementTemplate="+settlementTemplate);
			throw new PartnerRegistrationException("ServiceSettlementConfig not created as partnerservice="+partnerService+"SettlementTemplate="+settlementTemplate);
		}
		ServiceSettlementConfigDAO serviceSettlementConfigDAO = DAOFactory.getInstance().getServiceSettlementConfigDAO();
		ServiceSettlementCfg serviceSettlementConfig = new ServiceSettlementCfg();
		serviceSettlementConfig.setIsdefault(Boolean.TRUE);
		serviceSettlementConfig.setMfinoServiceProvider(serviceSettlementConfig.getMfinoServiceProvider());
		serviceSettlementConfig.setPartnerServices(partnerService);
		Pocket collectorPocket = pocketDAO.getById(partnerService.getCollectorpocket().getId());
		serviceSettlementConfig.setPocket(collectorPocket);
		serviceSettlementConfig.setSchedulerstatus(CmFinoFIX.SchedulerStatus_TobeScheduled.longValue());
		serviceSettlementConfig.setSettlementTemplate(settlementTemplate);
		serviceSettlementConfigDAO.save(serviceSettlementConfig);
		log.info("Servicesettlementconfig ID:"+serviceSettlementConfig.getId()+" for partnerserviceID:"+partnerService.getId());
		return serviceSettlementConfig;		
	}

	private Map<Integer, Pocket> getDefaultPocketsMap(Partner partner) {
		Map<Integer, Pocket> pocketMap = new HashMap<Integer, Pocket>();
		SubscriberMdn subscriberMDN = partner.getSubscriber().getSubscriberMdns().iterator().next();
		Set<Pocket> pockets = subscriberMDN.getPockets();
		boolean collectorFound = false;
		boolean bankFound = false;
		boolean emaoneyFound = false;
		boolean suspenceFound = false;
		for(Pocket pocket:pockets){
			if(!(CmFinoFIX.PocketStatus_Initialized.equals(pocket.getStatus())
					||CmFinoFIX.PocketStatus_Active.equals(pocket.getStatus()))
					&&!Boolean.valueOf(pocket.getIsdefault().toString()))
				continue;
			PocketTemplate pocketTemplate = pocket.getPocketTemplateByPockettemplateid();
			if(Boolean.valueOf(pocketTemplate.getIscollectorpocket().toString())){
				if(!collectorFound)
					pocketMap.put(CmFinoFIX.ServicePocketType_Collector, pocket);
				collectorFound =true;
				continue;
			}
			if(CmFinoFIX.PocketType_BankAccount.equals(pocketTemplate.getType())){
				if(!bankFound)
					pocketMap.put(CmFinoFIX.ServicePocketType_Bank, pocket);
				bankFound = true;
				continue;
			}
			if(Boolean.valueOf(pocketTemplate.getIssuspencepocket().toString())){
				if(!suspenceFound)
					pocketMap.put(CmFinoFIX.ServicePocketType_Suspence, pocket);
				suspenceFound = true;
				continue;
			}
			if(CmFinoFIX.PocketType_SVA.equals(pocketTemplate.getType())
					&&CmFinoFIX.Commodity_Money.equals(pocketTemplate.getCommodity())){
				if(!emaoneyFound)
					pocketMap.put(CmFinoFIX.ServicePocketType_Emoney, pocket);
				emaoneyFound = true;
				continue;
			}
			if(collectorFound&&bankFound&&emaoneyFound&&suspenceFound)
				break;
		}
		return pocketMap;
	}

	private SettlementTemplate createSettlementTemplate(Partner partner,Pocket pocket)  {
		log.info("creating SettlementTemplate for partner id:"+partner.getId());
		SettlementTemplateDAO settlementTemplateDAO = DAOFactory.getInstance().getSettlementTemplateDAO();
		MfinoServiceProviderDAO mspDAO =DAOFactory.getInstance().getMfinoServiceProviderDAO();
		ScheduleTemplateDAO scheduleTemplateDAO = DAOFactory.getInstance().getScheduleTemplateDao();
		SettlementTemplate settlementTemplate = new SettlementTemplate();
		List<ScheduleTemplate> scheduleTemplates = scheduleTemplateDAO.getAll();
		for(ScheduleTemplate scheduleTemplate: scheduleTemplates ){
			if("3".equals(scheduleTemplate.getModetype())){
				settlementTemplate.setScheduleTemplateByCutofftime(scheduleTemplate);
				break;
			}
		}		
		settlementTemplate.setPartner(partner);
		settlementTemplate.setSettlementname(partner.getTradename());
		settlementTemplate.setPocket(pocket);
		settlementTemplate.setMfinoServiceProvider(mspDAO.getById(1l));
		settlementTemplateDAO.save(settlementTemplate);
		log.info("SettlementTemplate ID:"+settlementTemplate.getId()+" created for partner id:"+partner.getId());
		return settlementTemplate;
	}

	private void updatePartner(Partner partner, CMPartnerRegistrationThroughAPI partnerRegistration) {
		
			partner.setBusinesspartnertype(partnerRegistration.getBusinessPartnerType());        
	        partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_Initialized);
            partner.setPartnercode(partnerRegistration.getPartnerCode());
           	partner.setAuthorizedemail(partnerRegistration.getAuthorizedEmail());
        	partner.setAuthorizedfaxnumber(partnerRegistration.getAuthorizedFaxNumber());
           	partner.setAuthorizedrepresentative(partnerRegistration.getAuthorizedRepresentative());
           	partner.setClassification(partnerRegistration.getClassification());
           	partner.setFaxnumber(partnerRegistration.getFaxNumber());
           	partner.setDesignation(partnerRegistration.getDesignation());
           	partner.setFranchisephonenumber(partnerRegistration.getFranchisePhoneNumber());
           	partner.setIndustryclassification(partnerRegistration.getIndustryClassification());
            MfinoServiceProviderDAO mspDAO =DAOFactory.getInstance().getMfinoServiceProviderDAO();
            partner.setMfinoServiceProvider(mspDAO.getById(1));
            partner.setNumberofoutlets(partnerRegistration.getNumberOfOutlets());
            partner.setRepresentativename(partnerRegistration.getRepresentativeName());
            partner.setTradename(partnerRegistration.getTradeName());
            partner.setTypeoforganization(partnerRegistration.getTypeOfOrganization());
            partner.setWebsite(partnerRegistration.getWebSite());
            partner.setYearestablished(partnerRegistration.getYearEstablished());
         //for merchant and outlet addresses
            AddressDAO addressDAO = DAOFactory.getInstance().getAddressDAO();
          	Address merchantAddress = new Address();
        		partner.setAddressByMerchantaddressid(merchantAddress);
            merchantAddress.setLine1(partnerRegistration.getMerchantAddressLine1());
            merchantAddress.setCity(partnerRegistration.getMerchantAddressCity());
            merchantAddress.setLine2(partnerRegistration.getMerchantAddressLine2());
            merchantAddress.setState(partnerRegistration.getMerchantAddressState());
        	merchantAddress.setZipcode(partnerRegistration.getMerchantAddressZipcode());
        	merchantAddress.setCountry(partnerRegistration.getMerchantAddressCountry());
        	addressDAO.save(merchantAddress);
        
        //outlet address
           	Address outletAddress = new Address();
    		partner.setAddressByFranchiseoutletaddressid(outletAddress);
    		outletAddress.setLine1(StringUtils.isNotBlank(partnerRegistration.getOutletAddressLine1())?partnerRegistration.getOutletAddressLine1():partnerRegistration.getMerchantAddressLine1());
    		outletAddress.setLine2(StringUtils.isNotBlank(partnerRegistration.getOutletAddressLine2())?partnerRegistration.getOutletAddressLine2():partnerRegistration.getMerchantAddressLine2());
    		outletAddress.setState(StringUtils.isNotBlank(partnerRegistration.getOutletAddressState())?partnerRegistration.getOutletAddressState():partnerRegistration.getMerchantAddressState());
    		outletAddress.setCity(StringUtils.isNotBlank(partnerRegistration.getOutletAddressCity())?partnerRegistration.getOutletAddressCity():partnerRegistration.getMerchantAddressCity());
    		outletAddress.setZipcode(StringUtils.isNotBlank(partnerRegistration.getOutletAddressZipcode())?partnerRegistration.getOutletAddressZipcode():partnerRegistration.getMerchantAddressZipcode());
    		outletAddress.setCountry(StringUtils.isNotBlank(partnerRegistration.getOutletAddressCountry())?partnerRegistration.getOutletAddressCountry():partnerRegistration.getMerchantAddressCountry());
          	addressDAO.save(outletAddress);	
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public String getMDN(Long partnerId){
		log.info("PartnerService : getMDN BEGIN");
		String result = "";
		PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
		Partner partner = partnerDao.getById(partnerId);
		if (partner != null && partner.getSubscriber()!= null) {
			result = partner.getSubscriber().getSubscriberMdns().iterator().next().getMdn();
		}
		
		log.info("PartnerService : getMDN END");
		return result;
	}
}
