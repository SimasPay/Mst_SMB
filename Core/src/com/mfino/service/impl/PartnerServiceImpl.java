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
import com.mfino.domain.Group;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerDefaultServices;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.RolePermission;
import com.mfino.domain.ScheduleTemplate;
import com.mfino.domain.ServiceSettlementConfig;
import com.mfino.domain.SettlementTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
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
			SubscriberMDN subscriberMDN = subscriberMDNDAO.getByMDN(mdn);
			if(subscriberMDN!=null){
				partner = getPartner(subscriberMDN);
				log.info("got partner: "+partner+" for mdn: "+mdn);
			}
		}
		return partner;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Partner getPartner(SubscriberMDN mdn){
		if(mdn!=null){
			Subscriber agentsubscriber=mdn.getSubscriber();
			if((agentsubscriber.getType().equals(CmFinoFIX.SubscriberType_Partner))){
				Set<Partner> agentPartner = agentsubscriber.getPartnerFromSubscriberID();
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
			SubscriberMDN smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(mdn);
			if(smdn != null)
			{
				language = smdn.getSubscriber().getLanguage();
				notificationWrapper.setFirstName(smdn.getSubscriber().getFirstName());
				notificationWrapper.setLastName(smdn.getSubscriber().getLastName());
				
				Partner partner=smdn.getSubscriber().getPartnerFromSubscriberID().iterator().next();
				notificationWrapper.setPartnerCode(partner.getPartnerCode());				
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
		SubscriberMDN subscriberMDN = subscriberMdnDao.getByMDN(mdn);
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
		if(!CmFinoFIX.UpgradeState_Approved.equals(subscriberMDN.getSubscriber().getUpgradeState())){
			return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
		}
		Partner partner=subscriberMDN.getSubscriber().getPartnerFromSubscriberID().iterator().next();

// [Bala] Commented this as we dont have Agent App to activet the agent. So we will the Agent also like partner flow for Registration and activation.		
//		if(isAgentType(partner.getBusinessPartnerType())){
//			return CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner;
//		}
		if(!(CmFinoFIX.SubscriberStatus_Registered.equals(partner.getPartnerStatus())
				||CmFinoFIX.SubscriberStatus_Initialized.equals(partner.getPartnerStatus()))){
			return CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation;
		}
		
		if(subscriberMDN.getOTPExpirationTime().before(new Date())){
			return CmFinoFIX.NotificationCode_OTPExpired;			
		}		
		String otpdiget =MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), otp);
		if(!otpdiget.equals(subscriberMDN.getOTP())){
			return CmFinoFIX.NotificationCode_OTPInvalid;
		}

		Subscriber subscriber =subscriberMDN.getSubscriber();
		User user=partner.getUser();
		
		Pocket bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getID(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		Long groupID = null;
		Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroup().getID();
		}
		PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(subscriber.getKYCLevelByKYCLevel().getKYCLevel(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Partner, partner.getBusinessPartnerType(), groupID);
		Pocket emoneyPocket =subscriberService.getDefaultPocket(subscriberMDN.getID(), svaPocketTemplate.getID());
		
		if(emoneyPocket==null){
			return CmFinoFIX.NotificationCode_MoneySVAPocketNotFound;
		}
		if(bankPocket==null){
			return CmFinoFIX.NotificationCode_DefaultBankAccountPocketNotFound;
		}
		if(emoneyPocket.getStatus().equals(CmFinoFIX.PocketStatus_PendingRetirement)
				||emoneyPocket.getStatus().equals(CmFinoFIX.PocketStatus_Retired)
				||bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_PendingRetirement)
				||bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_Retired)){
			return CmFinoFIX.NotificationCode_PocketStatusDoesNotEnableActivation;			
		}
		String tradeName=partner.getTradeName();
		 if(!emoneyPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)){
			emoneyPocket.setActivationTime(new Timestamp());
			emoneyPocket.setIsDefault(true);
			emoneyPocket.setStatus(CmFinoFIX.PocketStatus_Active);
			emoneyPocket.setStatusTime(new Timestamp());
			emoneyPocket.setUpdatedBy(tradeName);
			}
		 if(!bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)){
				bankPocket.setActivationTime(new Timestamp());
				bankPocket.setIsDefault(true);
				bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
				bankPocket.setStatusTime(new Timestamp());
				bankPocket.setUpdatedBy(tradeName);
				pocketDAO.save(bankPocket);
				log.info("PartnerActivation : bankPocket activation id:"+bankPocket.getID()+" agentid"+partner.getID());
			}
		
		RolePermissionQuery query = new RolePermissionQuery();
		query.setPermission(CmFinoFIX.Permission_PinPrompt);
		query.setUserRole(user.getRole());
		RolePermissionDAO rolePermissionDAO = DAOFactory.getInstance().getRolePermissionDAO();
		List<RolePermission> rolePermission = rolePermissionDAO.get(query);
		
		if(rolePermission==null||rolePermission.isEmpty()){
			Integer OTPLength = systemParametersService.getOTPLength();
			String newpin = MfinoUtil.generateOTP(OTPLength);
			//newpin = newpin.substring(0,SystemParametersUtil.getInteger(SystemParameterKeys.OTP_LENGTH));
			String digestpin = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), newpin);
			subscriberMDN.setDigestedPIN(digestpin);
			String authToken = MfinoUtil.calculateAuthorizationToken(subscriberMDN.getMDN(), newpin);
			subscriberMDN.setAuthorizationToken(authToken);
		}
		subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriberMDN.setStatusTime(new Timestamp());
		subscriberMDN.setActivationTime(new Timestamp());
		subscriberMDN.setUpdatedBy(tradeName);
		subscriber.setUpdatedBy(tradeName);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
		subscriber.setStatusTime(new Timestamp());
		subscriber.setActivationTime(new Timestamp());
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
		subscriberMDN.setOTP(null);
		subscriberMDN.setOTPExpirationTime(null);
		if(emoneyPocket!=null){
			pocketDAO.save(emoneyPocket);
			log.info("PartnerActivation : emoneyPocket with id:"+emoneyPocket.getID()+" agent:"+partner.getID());
		}
		partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_Active);
		user.setStatus(CmFinoFIX.UserStatus_Active);
		user.setStatusTime(new Timestamp());
		user.setFailedLoginCount(0);
		user.setFirstTimeLogin(true);
		user.setUserActivationTime(new Timestamp());
		user.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		String password = PasswordGenUtil.generate();
		PasswordEncoder encoder = new ShaPasswordEncoder(1);
		String encPassword = encoder.encodePassword(password, user.getUsername());
		user.setPassword(encPassword);
		String emailSubject = "Partner Registration";
		String emailMsg = genratePartnerRegistrationMail(partner, user,partner.getBusinessPartnerType(), password);
		String email=partner.getAuthorizedEmail();
		String to=partner.getTradeName();
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
			 notificationWrapper.setLanguage(subscriber.getLanguage());
			 notificationWrapper.setCompany(subscriber.getCompany());
			 notificationWrapper.setFirstName(subscriber.getFirstName());
			 notificationWrapper.setLastName(subscriber.getLastName());				
			 notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			 notificationWrapper.setPartnerCode(partner.getPartnerCode());
			 notificationWrapper.setCode(CmFinoFIX.NotificationCode_PartnerActivationCompleted);//partneractivation
			 smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true); //use thread pool to send message
		}catch (Exception e) {
			log.error("failed to generate message:",e);
		}
			String mdn1 = partner.getFranchisePhoneNumber();
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
		Set<PartnerServices> ps=partner.getPartnerServicesFromPartnerID();
		PartnerServicesDAO psDao = DAOFactory.getInstance().getPartnerServicesDAO();
		for(PartnerServices service:ps){
			if(service.getStatus().equals(CmFinoFIX.PartnerServiceStatus_Initialized)
					&&checkPocket(service.getPocketByCollectorPocket(),true)
					&&checkPocket(service.getPocketByDestPocketID(),false)
					&&checkPocket(service.getPocketBySourcePocket(),false)){
				service.setStatus(CmFinoFIX.PartnerServiceStatus_Active);
				String tradeName=partner.getTradeName();
				service.setUpdatedBy(tradeName);
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
		} else if(!isCollector&&!pocket.getIsDefault()&&CmFinoFIX.PocketStatus_Initialized.equals(pocket.getStatus())) {
			PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
			pocket.setActivationTime(new Timestamp());
			pocket.setStatus(CmFinoFIX.PocketStatus_Active);
			pocket.setStatusTime(new Timestamp());
			pocketDao.save(pocket);
		}

		return true;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void activateNonTransactionable(SubscriberMDN subscriberMDN) {
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		Set<Pocket> pockets = subscriberMDN.getPocketFromMDNID();
		for(Pocket pocket:pockets){
			if(pocket.getStatus().equals(CmFinoFIX.PocketStatus_Initialized)&&
					(pocket.getPocketTemplate().getIsCollectorPocket() || pocket.getPocketTemplate().getIsSuspencePocket() || pocket.getPocketTemplate().getIsSystemPocket())){
				pocket.setActivationTime(new Timestamp());
//				pocket.setIsDefault(true);
				pocket.setStatus(CmFinoFIX.PocketStatus_Active);
				pocket.setStatusTime(new Timestamp());
				pocketDAO.save(pocket);
			}
		}
		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void retireServices(Partner objPartner) {
		Set<PartnerServices> partnerServices = objPartner.getPartnerServicesFromPartnerID();
		PartnerServicesDAO psDao = DAOFactory.getInstance().getPartnerServicesDAO();
		for(PartnerServices ps:partnerServices){
			 if (!ps.getStatus().equals(CmFinoFIX.PartnerServiceStatus_Retired)) {
				 ps.setStatus(CmFinoFIX.PartnerServiceStatus_PendingRetirement);
	              psDao.save(ps);
	            }
		}		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void changePin(String username, String transactionPin) throws Exception {
		 User user;
	        UserQuery query = new UserQuery();
	        query.setUserName(username);
	        UserDAO userdao = DAOFactory.getInstance().getUserDAO();
	        List<User> results = userdao.get(query);
	        if (results != null && results.size() > 0) {
	            user = results.get(0);
	            Set<Partner> partners = user.getPartnerFromUserID();
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
				SubscriberMDN subMdn = subscriber.getSubscriberMDNFromSubscriberID().iterator().next();
				String calcPIN = MfinoUtil.calculateDigestPin(subMdn.getMDN(), transactionPin);
				subMdn.setDigestedPIN(calcPIN);
				String authToken = MfinoUtil.calculateAuthorizationToken(subMdn.getMDN(), transactionPin);
				subMdn.setAuthorizationToken(authToken);
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
	public String genratePartnerRegistrationMail(Partner partner, User user, Integer type, String password) {
		String mailBody = ConfigurationUtil.getPartnerRegistrationMail();
		//mailBody = mailBody.replace("$(firstname)", user.getFirstName());
		//mailBody = mailBody.replace("$(Lastname)", user.getLastName());
		mailBody = mailBody.replace("$(tradename)", partner.getTradeName());
		mailBody = mailBody.replace("$(partnerCode)", partner.getPartnerCode());
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
		language = partner.getSubscriber().getLanguage();
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
		notificationWrapper.setPartnerCode(partner.getPartnerCode());
		notificationWrapper.setCode(CmFinoFIX.NotificationCode_PartnerActivation);
		notificationWrapper.setOneTimePin(oneTimePin);
		notificationWrapper.setService("activation");
		notificationWrapper.setAppURL(ConfigurationUtil.getAppURL());
		notificationWrapper.setSourceMDN(mdn);
		notificationWrapper.setLanguage(language);
		notificationWrapper.setFirstName(partner.getSubscriber().getFirstName());
		notificationWrapper.setLastName(partner.getSubscriber().getLastName());
		
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
		SubscriberMDN subscriberMDN;
		Subscriber subscriber;
		User user ;
		
		subscriberMDN= createSubscriberMDNEntityForPartner(partnerRegistration);
		subscriber= subscriberMDN.getSubscriber();
		user= createUserEntityForPartner(partnerRegistration);        
        
          partner.setSubscriber(subscriber);
          partner.setUser(user);
          updatePartner(partner, partnerRegistration);

          SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
          UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
          SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
             
          userDAO.save(user);                                               
          subscriberDAO.save(subscriber);
          subMdndao.save(subscriberMDN);
          partnerDAO.save(partner);
          if(subscriber.getSubscriberGroupFromSubscriberID().size() > 0){
				SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
				for(SubscriberGroup sg: subscriber.getSubscriberGroupFromSubscriberID()){
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
   			String to=partner.getTradeName();
   			String mdn=subscriberMDN.getMDN();
   			NotificationWrapper smsWrapper = genratePartnerOTPMessage(partner, partnerRegistration.getOTP(),subscriberMDN.getMDN(), CmFinoFIX.NotificationMethod_SMS);
   			NotificationWrapper emailWrapper = genratePartnerOTPMessage(partner, partnerRegistration.getOTP(),subscriberMDN.getMDN(), CmFinoFIX.NotificationMethod_Email);
   			SubscriberMDN smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(mdn);
   			if(smdn != null)
   			{
   				smsWrapper.setFirstName(smdn.getSubscriber().getFirstName());
   				smsWrapper.setLastName(smdn.getSubscriber().getLastName());	
   				emailWrapper.setFirstName(smdn.getSubscriber().getFirstName());
   				emailWrapper.setLastName(smdn.getSubscriber().getLastName());
   			}
   			smsWrapper.setDestMDN(subscriberMDN.getMDN());
   			String smsMessage = notificationMessageParserService.buildMessage(smsWrapper,true);

   			smsService.setDestinationMDN(mdn);
   			smsService.setMessage(smsMessage);
   			smsService.setNotificationCode(smsWrapper.getCode());
   			smsService.asyncSendSMS();
   			
   			emailWrapper.setDestMDN(subscriberMDN.getMDN());
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
			Group defaultGroup = DAOFactory.getInstance().getGroupDao().getSystemGroup();
			partnerRegistration.setGroupID(defaultGroup.getID().toString());
		}
		if(StringUtils.isBlank(partnerRegistration.getTypeOfOrganization()))
			partnerRegistration.setTypeOfOrganization(CmFinoFIX.TypeOfOrganization_Others);
		if(partnerRegistration.getApprovalRequired()==null)
			partnerRegistration.setApprovalRequired(Boolean.valueOf(systemParametersService.getString(SystemParameterKeys.PARTNER_REGISTER_THROUGHAPI_APPROVAL)));
	}

	private User createUserEntityForPartner(CMPartnerRegistrationThroughAPI partnerRegistration) throws Exception{
		 User user = new User();
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
         user.setFirstName(partnerRegistration.getFirstName());
         user.setLastName(partnerRegistration.getLastName());
         user.setLanguage(partnerRegistration.getLanguage());
         user.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
         user.setStatus(CmFinoFIX.UserStatus_Registered);
         user.setTimezone(partnerRegistration.getTimezone());
         user.setRole(getRole(partnerRegistration.getBusinessPartnerType()));
         
         return user;		
	}
	private SubscriberMDN createSubscriberMDNEntityForPartner(CMPartnerRegistrationThroughAPI partnerRegistration) throws Exception{
		SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN subscriberMDN = subMdndao.getByMDN(subscriberService.normalizeMDN(partnerRegistration.getMDN()));
		Subscriber subscriber;
		boolean isExistingMDN = false;
		if(subscriberMDN==null){
       	  	  subscriberMDN=new SubscriberMDN();
         	  subscriber=new Subscriber();
         	  MfinoServiceProviderDAO mspDAO =DAOFactory.getInstance().getMfinoServiceProviderDAO();
              subscriber.setmFinoServiceProviderByMSPID(mspDAO.getById(1));
              subscriberMDN.setMDN(subscriberService.normalizeMDN(partnerRegistration.getMDN()));
              subscriberMDN.setSubscriber(subscriber);
              subscriber.getSubscriberMDNFromSubscriberID().add(subscriberMDN);
              Company company = subscriberService.getCompanyFromMDN(subscriberMDN.getMDN());
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
        	 
         	 if(subscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_PendingRetirement)
         			 ||subscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_Retired))
         		 throw new PartnerRegistrationException("Invalid MDN status not allowed to register as partner");
         }
		 subscriber.setType(CmFinoFIX.SubscriberType_Partner);
         KYCLevelDAO kyclevelDao  = DAOFactory.getInstance().getKycLevelDAO();
         KYCLevel kycLevel = kyclevelDao.getByKycLevel(ConfigurationUtil.getBulkUploadSubscriberKYClevel());
         subscriber.setKYCLevelByKYCLevel(kycLevel);
         subscriber.setUpgradableKYCLevel(null);
         subscriber.setUpgradeState(partnerRegistration.getApprovalRequired()?CmFinoFIX.UpgradeState_Upgradable:CmFinoFIX.UpgradeState_Approved);
         subscriber.setRegistrationMedium(CmFinoFIX.RegistrationMedium_Web);
         subscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS|CmFinoFIX.NotificationMethod_Email);
         if(!partnerRegistration.getApprovalRequired()){
        	 Integer OTPLength = systemParametersService.getOTPLength();
             String oneTimePin = MfinoUtil.generateOTP(OTPLength);
             partnerRegistration.setOTP(oneTimePin);
     		 String digestPin1 = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), oneTimePin);
     		 subscriberMDN.setOTP(digestPin1);
 			 subscriberMDN.setOTPExpirationTime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
		 }
         subscriberMDN.setAuthorizationToken(null);
         subscriber.setLastName(partnerRegistration.getLastName());
         subscriber.setAppliedBy("System");
         subscriber.setAppliedTime(new Timestamp());
         subscriber.setApprovedOrRejectedBy("");
         subscriber.setApproveOrRejectComment("");
         subscriber.setApproveOrRejectTime(partnerRegistration.getApprovalRequired()?new Timestamp():null);
         subscriberMDN.setStatus(CmFinoFIX.MDNStatus_Initialized);
	     subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
	     subscriberMDN.setStatusTime(new Timestamp());
	     subscriber.setStatusTime(new Timestamp());
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
        	subscriber.setIsEmailVerified(false);
        	
        	if(StringUtils.isNotBlank(partnerRegistration.getGroupID())){    			
    			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
    			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
    			
    			if((subscriber.getSubscriberGroupFromSubscriberID() != null) && (subscriber.getSubscriberGroupFromSubscriberID().size() > 0)){
    				Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
    				SubscriberGroup sg = subscriberGroups.iterator().next();
    				if(sg.getGroup().getID().longValue() != Long.valueOf(partnerRegistration.getGroupID()).longValue()){
    					Group group = (Group)groupDao.getById(Long.valueOf(partnerRegistration.getGroupID()));
    					sg.setGroup(group);
    					subscriberGroupDao.save(sg);
    				}
    			}
    			else{
    				Group group = (Group)groupDao.getById(Long.valueOf(partnerRegistration.getGroupID()));
    				SubscriberGroup sg = new SubscriberGroup();
    				sg.setSubscriber(subscriber);
    				sg.setGroup(group);
    				subscriber.getSubscriberGroupFromSubscriberID().add(sg);    				
    				if(subscriber.getID() != null){
    					subscriberGroupDao.save(sg);
    				}
    			}
    		}            
         
		return subscriberMDN;		
	}
	
	private Map<Integer, Pocket> createPocketsForPartner(Partner partner,String bankCardPan) throws Exception{
		Subscriber subscriber = partner.getSubscriber();
		SubscriberMDN subscriberMDN = subscriber.getSubscriberMDNFromSubscriberID().iterator().next();
		Group group = subscriber.getSubscriberGroupFromSubscriberID().iterator().next().getGroup();
		Map<Integer, Pocket> defaultPockets = getDefaultPocketsMap(partner);
		
		// Emoney Pocket creation
		if (defaultPockets.get(CmFinoFIX.ServicePocketType_Emoney) == null) {
				PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true,CmFinoFIX.PocketType_SVA,CmFinoFIX.SubscriberType_Partner, partner.getBusinessPartnerType(), group.getID());
				if (svaPocketTemplate == null) {
					log.info("No Default SVA Pocket template set for groupID:"+ group.getID() + "(" + group.getDescription()+ ")" + " PartnerType:"+ partner.getBusinessPartnerType());
					throw new PartnerRegistrationException("No Default SVA Pocket template set for groupID:"+ group.getID() + "(" + group.getDescription()+ ")" + " PartnerType:"+ partner.getBusinessPartnerType());					
				} else {
					String cardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMDN());
					Pocket pocket = pocketService.createPocket(svaPocketTemplate, subscriberMDN,CmFinoFIX.PocketStatus_Initialized, true, cardPan);
					log.info("Default emoney pocket successfully created for the partner -->"+ partner.getID() + " pocketID:" + pocket.getID());
					subscriberMDN.getPocketFromMDNID().add(pocket);
					defaultPockets.put(CmFinoFIX.ServicePocketType_Emoney, pocket);
				}
		}
		
		// bank Pocket creation
		if (defaultPockets.get(CmFinoFIX.ServicePocketType_Bank) == null
				&& StringUtils.isNotBlank(bankCardPan)) {
				PocketTemplate bankTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true,CmFinoFIX.PocketType_BankAccount,CmFinoFIX.SubscriberType_Partner, partner.getBusinessPartnerType(), group.getID());
				if (bankTemplate == null) {
					log.info("No Default bank Pocket template set for groupID:"+ group.getID() + "(" + group.getDescription()+ ")" + " PartnerType:"+ partner.getBusinessPartnerType());
					throw new PartnerRegistrationException("No Default bank Pocket template set for groupID:"+ group.getID() + "(" + group.getDescription()+ ")" + " PartnerType:"+ partner.getBusinessPartnerType());
				} else {
					Pocket bankPocket = pocketService.createPocket(bankTemplate, subscriberMDN,CmFinoFIX.PocketStatus_Initialized, true,bankCardPan);
					log.info("Default bank pocket Id --> " + bankPocket.getID()	+ " partnerid:" + partner.getID());
					subscriberMDN.getPocketFromMDNID().add(bankPocket);
					defaultPockets.put(CmFinoFIX.ServicePocketType_Bank, bankPocket);
				}
			}

		// collector Pocket creation
		if (defaultPockets.get(CmFinoFIX.ServicePocketType_Collector) == null) {
				PocketTemplate collectorPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, false, true,CmFinoFIX.PocketType_SVA,CmFinoFIX.SubscriberType_Partner, partner.getBusinessPartnerType(), group.getID());
				if (collectorPocketTemplate == null) {
					log.info("No Default collector Pocket template set for groupID:"+ group.getID()+ "("+ group.getDescription()+ ")"+ " PartnerType:"+ partner.getBusinessPartnerType());
					throw new PartnerRegistrationException("No Default collector Pocket template set for groupID:"+ group.getID()+ "("+ group.getDescription()+ ")"+ " PartnerType:"+ partner.getBusinessPartnerType());
				} else {
					String collectorPocketCardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMDN());
					Pocket collectorPocket = pocketService.createPocket(collectorPocketTemplate, subscriberMDN,CmFinoFIX.PocketStatus_Initialized, true,collectorPocketCardPan);
					log.info("Default collector pocket Id --> "+ collectorPocket.getID() + " partnerID:"+ partner.getID());
					subscriberMDN.getPocketFromMDNID().add(collectorPocket);
					defaultPockets.put(CmFinoFIX.ServicePocketType_Collector, collectorPocket);
				}
			}

		// suspence Pocket creation
		if (defaultPockets.get(CmFinoFIX.ServicePocketType_Suspence) == null) {
				PocketTemplate suspensePocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(CmFinoFIX.RecordType_SubscriberFullyBanked.longValue(), true, true, false,CmFinoFIX.PocketType_SVA,CmFinoFIX.SubscriberType_Partner, partner.getBusinessPartnerType(), group.getID());
				if (suspensePocketTemplate == null) {
					log.info("No Default suspence Pocket template set for groupID:"+ group.getID()+ "("+ group.getDescription()+ ")"+ " PartnerType:"+ partner.getBusinessPartnerType());
					throw new PartnerRegistrationException("No Default suspence Pocket template set for groupID:"+ group.getID()+ "("+ group.getDescription()+ ")"+ " PartnerType:"+ partner.getBusinessPartnerType());	
				} else {
					String suspensePocketCardPan = pocketService.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMDN());
					Pocket suspensePocket = pocketService.createPocket(suspensePocketTemplate, subscriberMDN,CmFinoFIX.PocketStatus_Initialized, true,suspensePocketCardPan);
					log.info("Default suspense pocket Id --> "+ suspensePocket.getID() + " partnerid:"+ partner.getID());
					subscriberMDN.getPocketFromMDNID().add(suspensePocket);
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
		query.setBusinessPartnerType(partner.getBusinessPartnerType());
		List<PartnerDefaultServices> partnerDefaultServices = partnerDefaultServicesDAO.get(query);
		if(partnerDefaultServices == null||partnerDefaultServices.isEmpty()){
			log.info("Default services not configured for partner type:"+partner.getBusinessPartnerType());
			throw new PartnerRegistrationException("Default services not configured for partner type:"+partner.getBusinessPartnerType());			
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
			partnerService.setPartner(partner);
			partnerService.setmFinoServiceProviderByMSPID(mspDAO.getById(1l));
			partnerService.setPartnerByServiceProviderID(defaultService.getServiceDefaultConfiguration().getPartnerByServiceProviderID());
			partnerService.setService(defaultService.getServiceDefaultConfiguration().getService());
			partnerService.setPocketByCollectorPocket(pockets.get(CmFinoFIX.ServicePocketType_Collector));
			partnerService.setPocketBySourcePocket(pockets.get(defaultService.getServiceDefaultConfiguration().getSourcePocketType()));
			partnerService.setPocketByDestPocketID(pockets.get(defaultService.getServiceDefaultConfiguration().getSourcePocketType()));
			partnerService.setIsServiceChargeShare(CmFinoFIX.IsServiceChargeShare_Individual);
			partnerServicesDAO.save(partnerService);
			log.info("PartnerService created for Partnerid:"+partner.getID()+"partnerserviceid:"+partnerService.getID());
			createServiceSettlementConfig(partnerService,settlementTemplate);
			partnerServices.add(partnerService);
		}
		return partnerServices;
				
	}
	
	private ServiceSettlementConfig createServiceSettlementConfig(PartnerServices partnerService,SettlementTemplate settlementTemplate) throws Exception {
		if(partnerService==null||settlementTemplate==null){
			log.info("ServiceSettlementConfig not created as partnerservice="+partnerService+"SettlementTemplate="+settlementTemplate);
			throw new PartnerRegistrationException("ServiceSettlementConfig not created as partnerservice="+partnerService+"SettlementTemplate="+settlementTemplate);
		}
		ServiceSettlementConfigDAO serviceSettlementConfigDAO = DAOFactory.getInstance().getServiceSettlementConfigDAO();
		ServiceSettlementConfig serviceSettlementConfig = new ServiceSettlementConfig();
		serviceSettlementConfig.setIsDefault(true);
		serviceSettlementConfig.setmFinoServiceProviderByMSPID(serviceSettlementConfig.getmFinoServiceProviderByMSPID());
		serviceSettlementConfig.setPartnerServicesByPartnerServiceID(partnerService);
		serviceSettlementConfig.setPocketByCollectorPocket(partnerService.getPocketByCollectorPocket());
		serviceSettlementConfig.setSchedulerStatus(CmFinoFIX.SchedulerStatus_TobeScheduled);
		serviceSettlementConfig.setSettlementTemplate(settlementTemplate);
		serviceSettlementConfigDAO.save(serviceSettlementConfig);
		log.info("Servicesettlementconfig ID:"+serviceSettlementConfig.getID()+" for partnerserviceID:"+partnerService.getID());
		return serviceSettlementConfig;		
	}

	private Map<Integer, Pocket> getDefaultPocketsMap(Partner partner) {
		Map<Integer, Pocket> pocketMap = new HashMap<Integer, Pocket>();
		SubscriberMDN subscriberMDN = partner.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
		Set<Pocket> pockets = subscriberMDN.getPocketFromMDNID();
		boolean collectorFound = false;
		boolean bankFound = false;
		boolean emaoneyFound = false;
		boolean suspenceFound = false;
		for(Pocket pocket:pockets){
			if(!(CmFinoFIX.PocketStatus_Initialized.equals(pocket.getStatus())
					||CmFinoFIX.PocketStatus_Active.equals(pocket.getStatus()))
					&&!pocket.getIsDefault())
				continue;
			PocketTemplate pocketTemplate = pocket.getPocketTemplate();
			if(pocketTemplate.getIsCollectorPocket()){
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
			if(pocketTemplate.getIsSuspencePocket()){
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
		log.info("creating SettlementTemplate for partner id:"+partner.getID());
		SettlementTemplateDAO settlementTemplateDAO = DAOFactory.getInstance().getSettlementTemplateDAO();
		MfinoServiceProviderDAO mspDAO =DAOFactory.getInstance().getMfinoServiceProviderDAO();
		ScheduleTemplateDAO scheduleTemplateDAO = DAOFactory.getInstance().getScheduleTemplateDao();
		SettlementTemplate settlementTemplate = new SettlementTemplate();
		List<ScheduleTemplate> scheduleTemplates = scheduleTemplateDAO.getAll();
		for(ScheduleTemplate scheduleTemplate: scheduleTemplates ){
			if("3".equals(scheduleTemplate.getModeType())){
				settlementTemplate.setScheduleTemplate(scheduleTemplate);
				settlementTemplate.setCutoffTime(scheduleTemplate.getID());
				break;
			}
		}		
		settlementTemplate.setPartner(partner);
		settlementTemplate.setSettlementName(partner.getTradeName());
		settlementTemplate.setPocketBySettlementPocket(pocket);
		settlementTemplate.setmFinoServiceProviderByMSPID(mspDAO.getById(1l));
		settlementTemplateDAO.save(settlementTemplate);
		log.info("SettlementTemplate ID:"+settlementTemplate.getID()+" created for partner id:"+partner.getID());
		return settlementTemplate;
	}

	private void updatePartner(Partner partner, CMPartnerRegistrationThroughAPI partnerRegistration) {
		
			partner.setBusinessPartnerType(partnerRegistration.getBusinessPartnerType());        
	        partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_Initialized);
            partner.setPartnerCode(partnerRegistration.getPartnerCode());
           	partner.setAuthorizedEmail(partnerRegistration.getAuthorizedEmail());
        	partner.setAuthorizedFaxNumber(partnerRegistration.getAuthorizedFaxNumber());
           	partner.setAuthorizedRepresentative(partnerRegistration.getAuthorizedRepresentative());
           	partner.setClassification(partnerRegistration.getClassification());
           	partner.setFaxNumber(partnerRegistration.getFaxNumber());
           	partner.setDesignation(partnerRegistration.getDesignation());
           	partner.setFranchisePhoneNumber(partnerRegistration.getFranchisePhoneNumber());
           	partner.setIndustryClassification(partnerRegistration.getIndustryClassification());
            MfinoServiceProviderDAO mspDAO =DAOFactory.getInstance().getMfinoServiceProviderDAO();
            partner.setmFinoServiceProviderByMSPID(mspDAO.getById(1));
            partner.setNumberOfOutlets(partnerRegistration.getNumberOfOutlets());
            partner.setRepresentativeName(partnerRegistration.getRepresentativeName());
            partner.setTradeName(partnerRegistration.getTradeName());
            partner.setTypeOfOrganization(partnerRegistration.getTypeOfOrganization());
            partner.setWebSite(partnerRegistration.getWebSite());
            partner.setYearEstablished(partnerRegistration.getYearEstablished());
         //for merchant and outlet addresses
            AddressDAO addressDAO = DAOFactory.getInstance().getAddressDAO();
          	Address merchantAddress = new Address();
        		partner.setAddressByMerchantAddressID(merchantAddress);
            merchantAddress.setLine1(partnerRegistration.getMerchantAddressLine1());
            merchantAddress.setCity(partnerRegistration.getMerchantAddressCity());
            merchantAddress.setLine2(partnerRegistration.getMerchantAddressLine2());
            merchantAddress.setState(partnerRegistration.getMerchantAddressState());
        	merchantAddress.setZipCode(partnerRegistration.getMerchantAddressZipcode());
        	merchantAddress.setCountry(partnerRegistration.getMerchantAddressCountry());
        	addressDAO.save(merchantAddress);
        
        //outlet address
           	Address outletAddress = new Address();
    		partner.setAddressByFranchiseOutletAddressID(outletAddress);
    		outletAddress.setLine1(StringUtils.isNotBlank(partnerRegistration.getOutletAddressLine1())?partnerRegistration.getOutletAddressLine1():partnerRegistration.getMerchantAddressLine1());
    		outletAddress.setLine2(StringUtils.isNotBlank(partnerRegistration.getOutletAddressLine2())?partnerRegistration.getOutletAddressLine2():partnerRegistration.getMerchantAddressLine2());
    		outletAddress.setState(StringUtils.isNotBlank(partnerRegistration.getOutletAddressState())?partnerRegistration.getOutletAddressState():partnerRegistration.getMerchantAddressState());
    		outletAddress.setCity(StringUtils.isNotBlank(partnerRegistration.getOutletAddressCity())?partnerRegistration.getOutletAddressCity():partnerRegistration.getMerchantAddressCity());
    		outletAddress.setZipCode(StringUtils.isNotBlank(partnerRegistration.getOutletAddressZipcode())?partnerRegistration.getOutletAddressZipcode():partnerRegistration.getMerchantAddressZipcode());
    		outletAddress.setCountry(StringUtils.isNotBlank(partnerRegistration.getOutletAddressCountry())?partnerRegistration.getOutletAddressCountry():partnerRegistration.getMerchantAddressCountry());
          	addressDAO.save(outletAddress);	
	}
}
