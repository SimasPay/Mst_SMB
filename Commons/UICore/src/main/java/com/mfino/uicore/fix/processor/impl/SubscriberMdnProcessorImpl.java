/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.AddressDAO;
import com.mfino.dao.AuthorizingPersonDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.PocketTemplateConfigDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SubscribersAdditionalFieldsDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Address;
import com.mfino.domain.AuthorizingPerson;
import com.mfino.domain.BankAdmin;
import com.mfino.domain.Company;
import com.mfino.domain.Group;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Merchant;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.SubscribersAdditionalFields;
import com.mfino.domain.User;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.errorcodes.Codes;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.exceptions.MerchantAlreadyExistsForMDNException;
import com.mfino.exceptions.NoSubscriberFoundException;
import com.mfino.exceptions.SubscriberRetiredException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSForwardNotificationRequest;
import com.mfino.fix.CmFinoFIX.CMJSMDNCheck;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberMDN;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberMDN.CGEntries;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.AuthorizationService;
import com.mfino.service.EnumTextService;
import com.mfino.service.MDNRangeService;
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
import com.mfino.uicore.fix.processor.ForwardNotificationRequestProcessor;
import com.mfino.uicore.fix.processor.MDNCheckProcessor;
import com.mfino.uicore.fix.processor.SubscriberMdnProcessor;
import com.mfino.uicore.web.WebContextError;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

@Service("SubscriberMdnProcessorImpl")
public class SubscriberMdnProcessorImpl extends BaseFixProcessor implements SubscriberMdnProcessor{

	private MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
	private Company company = null;
	private KYCLevelDAO kyclevelDao = DAOFactory.getInstance().getKycLevelDAO();
	private PocketTemplateConfigDAO ptcDao = DAOFactory.getInstance().getPocketTemplateConfigDao();
	private  boolean sendOTPOnIntialized;
	private  boolean isEMoneyPocketRequired;
	
	@Autowired
    private HttpServletRequest httpServletRequest;
	
	@Autowired
	@Qualifier("ForwardNotificationRequestProcessorImpl")
	private ForwardNotificationRequestProcessor forwardNotificationRequestProcessor;
	
	@Autowired
	@Qualifier("MDNCheckProcessorImpl")
	private MDNCheckProcessor mdnCheckProcessor;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("MDNRangeServiceImpl")
	private MDNRangeService mdnRangeService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	private void handleMerchantAlreadyExists() throws MerchantAlreadyExistsForMDNException {
		CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		error.setErrorDescription(MessageText._("Partner Already Exists for this MDN"));
		WebContextError.addError(error);
		throw new MerchantAlreadyExistsForMDNException(MessageText._(" PartnerAlreadyExistsForMDN "));
	}

	private void handleNoSubscriberFound() throws NoSubscriberFoundException {
		CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		error.setErrorDescription(MessageText._("No Subscriber Found"));
		WebContextError.addError(error);
		throw new NoSubscriberFoundException(MessageText._(" NoSubscriberFound "));
	}
	
	private void handleUniqueConstraintViolation(ConstraintViolationException consVoilError) throws ConstraintViolationException {
		CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
		String message = MessageText._("Duplicaite MDN");
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		error.setErrorDescription(message);
		error.allocateEntries(1);
		error.getEntries()[0] = new CmFinoFIX.CMJSError.CGEntries();
		error.getEntries()[0].setErrorName(CmFinoFIX.CMJSSubscriberMDN.CGEntries.FieldName_MDN);
		error.getEntries()[0].setErrorDescription(message);
		WebContextError.addError(error);
		log.warn(message, consVoilError);
		throw consVoilError;
	}

	private void handleRetiredSubscriber() throws SubscriberRetiredException {
		CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		error.setErrorDescription(MessageText._("Can't Change Subscriber Status"));
		WebContextError.addError(error);
		throw new SubscriberRetiredException(MessageText._(" Can't Change Status"));
	}

	private void validateSubscriberMDN(String MDN) throws Exception{
		CMJSMDNCheck chkMDN = new CmFinoFIX.CMJSMDNCheck();
		chkMDN.setMDN(MDN);
		CMJSError err = (CMJSError) mdnCheckProcessor.process(chkMDN);
		if (!CmFinoFIX.ErrorCode_NoError.equals(err.getErrorCode())) {
			WebContextError.addError(err);
			err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			err.setErrorDescription(MessageText._("MDN already exist in DB"));
			throw new InvalidMDNException(MessageText._(" MDN already exist in DB"));
		}
	}

	private void validateAccountNumber(String accountNumber) {
		PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
		PocketQuery pocketQuery = new PocketQuery();
		pocketQuery.setCardPan(accountNumber);
		
		List<Pocket> pocketList = pocketDao.get(pocketQuery);

		CMJSError err = (CMJSError) new CMJSError();
		if((null != pocketList) && (pocketList.size() > 0)) {
			WebContextError.addError(err);
			err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			err.setErrorDescription(MessageText._("Account Number already exists in DB"));
			throw new RuntimeException(MessageText._(" Account Number already exists in DB"));
		}
	}
	
	//private void updateEntity(SubscriberMDN s, CMJSSubscriberMDN.CGEntries e, CMJSSubscribersAdditionalFields.CGEntries saf) {
	private void updateEntity(SubscriberMDN s, CMJSSubscriberMDN.CGEntries e) {
		String ID = String.valueOf(s.getID());

		if (e.getMDN() != null) {
			String mdn = subscriberService.normalizeMDN(e.getMDN());
			if(!mdn.equals(s.getMDN())){
        		log.info("Subscriber MDN:"+ID+" MDN updated to "+mdn+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setMDN(mdn);
		}
		if (e.getAuthenticationPhoneNumber() != null) {
			if(!e.getAuthenticationPhoneNumber().equals(s.getAuthenticationPhoneNumber())){
        		log.info("Subscriber MDN:"+ID+" Authentication Phone Number updated to "+e.getAuthenticationPhoneNumber()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setAuthenticationPhoneNumber(e.getAuthenticationPhoneNumber());
		}
		if (e.getSecurityQuestion() != null) {
			if(!e.getSecurityQuestion().equals(s.getSubscriber().getSecurityQuestion())){
        		log.info("Subscriber MDN:"+ID+" Security Question updated to "+e.getSecurityQuestion()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setSecurityQuestion(e.getSecurityQuestion());
		}

		if (e.getAuthenticationPhrase() != null) {
			if(!e.getAuthenticationPhrase().equals(s.getSubscriber().getSecurityAnswer())){
        		log.info("Subscriber MDN:"+ID+" Security Answer updated to "+e.getAuthenticationPhrase()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setSecurityAnswer(e.getAuthenticationPhrase());
		}
		if (e.getDigestedPIN() != null) {
			if(!e.getDigestedPIN().equals(s.getDigestedPIN())){
        		log.info("Subscriber MDN:"+ID+" Digested Pin updated to "+e.getDigestedPIN()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setDigestedPIN(e.getDigestedPIN());
		}
		if(e.getApplicationID() != null){
			if(!e.getApplicationID().equals(s.getApplicationID())){
        		log.info("Subscriber MDN:"+ID+" Application ID updated to "+e.getApplicationID()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setApplicationID(e.getApplicationID());
		}

		if (e.getH2HAllowedIP() != null) {
			if(!e.getH2HAllowedIP().equals(s.getH2HAllowedIP())){
        		log.info("Subscriber MDN:"+ID+" H2H Allowed IP updated to "+e.getH2HAllowedIP()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setH2HAllowedIP(e.getH2HAllowedIP());
		}
		if (e.getMDNRestrictions() != null) {
			if(!e.getMDNRestrictions().equals(s.getRestrictions())){
        		log.info("Subscriber MDN:"+ID+" MDN Restrictions updated to "+e.getMDNRestrictions()+" by user:"+getLoggedUserNameWithIP());
        		log.info("Subscriber :"+s.getSubscriber().getID()+" Restrictions updated to "+e.getMDNRestrictions()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setRestrictions(e.getMDNRestrictions());
			s.getSubscriber().setRestrictions(e.getMDNRestrictions());
		}
		if (e.getIsForceCloseRequested() != null && e.getIsForceCloseRequested().booleanValue()) {
			if(!e.getIsForceCloseRequested().equals(s.getIsForceCloseRequested())){
        		log.info("Subscriber:"+ID+" Force Close Requested field is updated to " + userService.getCurrentUser().getUsername() + " by user:"+getLoggedUserNameWithIP());
        	}
			s.setIsForceCloseRequested(e.getIsForceCloseRequested());
		}
		if (e.getStatus() != null) {
			if(!e.getStatus().equals(s.getStatus())){
        		log.info("Subscriber MDN:"+ID+" Status updated to "+e.getStatus()+" by user:"+getLoggedUserNameWithIP());
        		log.info("Subscriber :"+s.getSubscriber().getID()+" Status updated to "+e.getStatus()+" by user:"+getLoggedUserNameWithIP());
        	}
			// *FindbugsChange*
        	// Previous -- if (e.getStatus() != s.getStatus()) {
			if (!(e.getStatus().equals(s.getStatus()))) {
				s.setStatus(e.getStatus());
				s.setStatusTime(new Timestamp());
				s.getSubscriber().setStatus(e.getStatus());
				s.getSubscriber().setStatusTime(new Timestamp());
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(s.getSubscriber(),true);
			}
		}
		if (s.getStatusTime() == null) {
			log.info("Subscriber MDN:"+ID+" Status Time updated by user:"+getLoggedUserNameWithIP());
			s.setStatusTime(new Timestamp());
		}

		if (s.getSubscriber().getStatusTime() == null) {
			log.info("Subscriber :"+s.getSubscriber().getID()+" Status Time updated by user:"+getLoggedUserNameWithIP());
			s.getSubscriber().setStatusTime(new Timestamp());
		}

		ID = String.valueOf(s.getSubscriber().getID());
		// subscriber related fields
		if (e.getFirstName() != null) {
			if(!e.getFirstName().equals(s.getSubscriber().getFirstName())){
        		log.info("Subscriber:"+ID+" First name updated to "+e.getFirstName()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setFirstName(e.getFirstName());
		}
		if (e.getLastName() != null) {
			if(!e.getLastName().equals(s.getSubscriber().getLastName())){
        		log.info("Subscriber:"+ID+" Last name updated to "+e.getLastName()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setLastName(e.getLastName());
		}
		if (e.getNickname() != null) {
			if(!e.getNickname().equals(s.getSubscriber().getNickname())){
        		log.info("Subscriber:"+ID+" Last name updated to "+e.getNickname()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setNickname(e.getNickname());
		}
		if (e.getEmail() != null) {
			if(!e.getEmail().equals(s.getSubscriber().getEmail())){
        		log.info("Subscriber:"+ID+" Email updated to "+e.getEmail()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setEmail(e.getEmail());
			s.getSubscriber().setIsEmailVerified(false);
		}		
		if (e.getLanguage() != null) {
			if(!e.getLanguage().equals(s.getSubscriber().getLanguage())){
        		log.info("Subscriber:"+ID+" Language updated to "+e.getLanguage()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setLanguage(e.getLanguage());
		}
		else
		{
			s.getSubscriber().setLanguage(systemParametersService.getSubscribersDefaultLanguage());
		}

		if (e.getActivationTime() != null) {
			if(e.getActivationTime()!=(s.getSubscriber().getActivationTime())){
        		log.info("Subscriber:"+ID+" Language updated to "+e.getActivationTime()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setActivationTime(e.getActivationTime());

		}
		if (e.getMSPID() != null) {
			mFinoServiceProvider msp = mspDAO.getById(e.getMSPID());
        	if(s.getSubscriber().getmFinoServiceProviderByMSPID()!=msp){
        		log.info("Subscriber:"+ID+" mFinoServiceProvider updated to "+msp.getID()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setmFinoServiceProviderByMSPID(mspDAO.getById(e.getMSPID()));
		}
		if (e.getNotificationMethod() != null) {
			if(!e.getNotificationMethod().equals(s.getSubscriber().getNotificationMethod())){
        		log.info("Subscriber:"+ID+" Notification method updated to "+e.getNotificationMethod()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setNotificationMethod(e.getNotificationMethod());
		}
		if (e.getStatus() != null) {
			/*if(!e.getStatus().equals(s.getSubscriber().getStatus())){
        		log.info("Subscriber:"+ID+" Status updated to "+e.getStatus()+" by user:"+getLoggedUserName());
        	}*/
			// s.getSubscriber().setStatus(s.getStatus());
		}
		if (e.getSubscriberType() != null) {
			if(!e.getSubscriberType().equals(s.getSubscriber().getType())){
        		log.info("Subscriber:"+ID+" Type updated to "+e.getSubscriberType()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setType(e.getSubscriberType());
		}
		if (e.getTimezone() != null) {
			if(!e.getTimezone().equals(s.getSubscriber().getTimezone())){
        		log.info("Subscriber:"+ID+" Timezone updated to "+e.getTimezone()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setTimezone(e.getTimezone());
		}

		if (e.getCurrency() != null) {
			if(!e.getCurrency().equals(s.getSubscriber().getCurrency())){
        		log.info("Subscriber:"+ID+" Currency updated to "+e.getCurrency()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setCurrency(e.getCurrency());
		}
		if (e.getPartnerType() != null) {
			if(!e.getPartnerType().equals(s.getSubscriber().getPartnerType())){
        		log.info("Subscriber:"+ID+" Partner Type updated to "+e.getCurrency()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setPartnerType(e.getPartnerType());
		}
		//not null check is addded because update message can come for update and insert actions
		if (e.getCompanyID() != null) {
			// *FindbugsChange*
        	// Previous -- if(!e.getCompanyID().equals(s.getSubscriber().getCompany())){
			if((s.getSubscriber().getCompany() != null) && (!e.getCompanyID().equals(s.getSubscriber().getCompany().getID()))){
        		log.info("Subscriber:"+ID+" Company updated to "+e.getCompanyID()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setCompany(company);
		}
		if(e.getDateOfBirth()!=null){
			if(!e.getDateOfBirth().equals(s.getSubscriber().getDateOfBirth())){
        		log.info("Subscriber:"+ID+" Date Of Birth updated to "+e.getDateOfBirth()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setDateOfBirth(e.getDateOfBirth());
		}
		if(e.getIDType()!=null){
			if(!e.getIDType().equals(s.getIDType())){
        		log.info("Subscriber:"+ID+" ID Type updated to "+e.getDateOfBirth()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setIDType(e.getIDType());
		}
		if(e.getIDNumber()!=null){
			if(!e.getIDNumber().equals(s.getIDNumber())){
        		log.info("Subscriber:"+ID+" ID Number updated to "+e.getIDNumber()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setIDNumber(e.getIDNumber());
		}
		if(e.getExpirationTime()!=null){
			if(!e.getExpirationTime().equals(s.getSubscriber().getIDExiparetionTime())){
        		log.info("Subscriber:"+ID+" ID Expiration time updated to "+e.getIDNumber()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setIDExiparetionTime(e.getExpirationTime());
		}
		if(e.getBirthPlace()!=null){
			if(!e.getBirthPlace().equals(s.getSubscriber().getBirthPlace())){
        		log.info("Subscriber:"+ID+" Birth Place updated to "+e.getIDNumber()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setBirthPlace(e.getBirthPlace());
		}

		// s.getSubscriber().setIDExiparetionTime();
		if(e.getReferenceAccount()!=null){
			if(!e.getReferenceAccount().equals(s.getSubscriber().getReferenceAccount())){
        		log.info("Subscriber:"+ID+" Reference No updated to "+e.getReferenceAccount()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setReferenceAccount(e.getReferenceAccount());
		}
		if(e.getKYCLevel()!=null&& e.getKYCLevel()>s.getSubscriber().getKYCLevelByKYCLevel().getKYCLevel()){
			if(!e.getKYCLevel().equals(s.getSubscriber().getUpgradableKYCLevel())){
        		log.info("Subscriber:"+ID+" Upgradable KYC updated to "+e.getKYCLevel()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setUpgradableKYCLevel(e.getKYCLevel());
			s.getSubscriber().setUpgradeState(CmFinoFIX.UpgradeState_Upgradable);
		}
		if(e.getKYCLevel()!=null){
			if(!e.getKYCLevel().equals(s.getSubscriber().getUpgradableKYCLevel())){
        		log.info("Subscriber:"+ID+" Applied User Name is updated to " + userService.getCurrentUser().getUsername() + " by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setAppliedBy(userService.getCurrentUser().getUsername());
			s.getSubscriber().setAppliedTime(new Timestamp());
		}

		// if (e.getDompetMerchant() != null) {
		// s.getSubscriber().setDompetMerchant(e.getDompetMerchant());
		// }
		
		if(e.getOtherMDN()!=null){
			if(!e.getOtherMDN().equals(s.getOtherMDN())){
        		log.info("SubscriberMDN:"+ID+" Other MDN updated to "+e.getOtherMDN()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setOtherMDN(e.getOtherMDN());
		}
		
		if((null != e.getGroupID()) && !("".equals(e.getGroupID()))){

			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();

			if((s.getSubscriber().getSubscriberGroupFromSubscriberID() != null) && (s.getSubscriber().getSubscriberGroupFromSubscriberID().size() > 0)){
				Set<SubscriberGroup> subscriberGroups = s.getSubscriber().getSubscriberGroupFromSubscriberID();
				SubscriberGroup sg = subscriberGroups.iterator().next();
				if(sg.getGroup().getID().longValue() != Long.valueOf(e.getGroupID()).longValue()){
					Group group = (Group)groupDao.getById(Long.valueOf(e.getGroupID()));
					sg.setGroup(group);
					subscriberGroupDao.save(sg);
				}
			}
			else{
				Group group = (Group)groupDao.getById(Long.valueOf(e.getGroupID()));
				SubscriberGroup sg = new SubscriberGroup();
				sg.setSubscriber(s.getSubscriber());
				sg.setGroup(group);
				s.getSubscriber().getSubscriberGroupFromSubscriberID().add(sg);

				if(s.getSubscriber().getID() != null){
					subscriberGroupDao.save(sg);
				}

//				subscriberGroupDao.save(sg);
				//save subscriber group
//				Set<SubscriberGroup> subscriberGroups = new HashSet<SubscriberGroup>();
//				subscriberGroups.add(sg);
//				s.getSubscriber().setSubscriberGroupFromSubscriberID(subscriberGroups);
			}
		}
	}

	private void updateAdditionalFields(SubscribersAdditionalFields saf, CMJSSubscriberMDN.CGEntries e) {
		if (e.getProofofAddress() != null) {
			saf.setProofofAddress(e.getProofofAddress());
		}
		if (e.getCreditCheck() != null) {
			saf.setCreditCheck(e.getCreditCheck());
		}
		if (e.getKinName() != null) {
			saf.setKinName(e.getKinName());
		}
		if (e.getKinMDN() != null) {
			saf.setKinMDN(e.getKinMDN());
		}
		if (e.getNationality() != null) {
			saf.setNationality(e.getNationality());
		}
		//if (e.getReferenceAccount() != null) {
			//    saf.setsu(e.getReferenceAccount());
			//}
		if (e.getCompanyName() != null) {
			saf.setSubsCompanyName(e.getCompanyName());
		}
		if (e.getSubscriberMobileCompany() != null) {
			saf.setSubscriberMobileCompany(e.getSubscriberMobileCompany());
		}
		if (e.getCertofIncorporation() != null){
			saf.setCertofIncorporation(e.getCertofIncorporation());
		}
		if(e.getNationality()!=null){
			saf.setNationality(e.getNationality());
		}
	}
	private void updateAuthorizing( AuthorizingPerson ap, CMJSSubscriberMDN.CGEntries e) {
		if (e.getAuthoFirstName() != null) {
			ap.setFirstName(e.getAuthoFirstName());
		}
		if (e.getAuthoLastName()!=null){
			ap.setLastName(e.getAuthoLastName());
		}
		if (e.getAuthoDateofBirth()!=null){
			ap.setDateOfBirth(e.getAuthoDateofBirth());

		}
		if (e.getAuthoIDDescription()!=null){
			ap.setIDDesc(e.getAuthoIDDescription());

		}

		if (e.getAuthorizingPersonIDNumber()!=null){
			ap.setIDNumber(e.getAuthorizingPersonIDNumber());

		}
		if(ap.getmFinoServiceProviderByMSPID()==null){
			ap.setmFinoServiceProviderByMSPID(mspDAO.getById(1L));
		}
	}
	private void updateAddress(Address ads, CMJSSubscriberMDN.CGEntries e) {
		if (e.getStreetAddress() != null) {
			ads.setLine2(e.getStreetAddress());
		}
		if(e.getPlotNo()!=null){
			ads.setLine1(e.getPlotNo());
		}
		if (e.getCity()!=null){
			ads.setCity(e.getCity());
		}
		if(e.getTownVillage()!=null){
			//	ads.setTownVillage(e.getTownVillage());
		}
		if (e.getCountry()!=null){
			ads.setCountry(e.getCountry());
		}
		if (e.getRegionName()!=null){
			ads.setRegionName(e.getRegionName());
		}
	}

	private void updateMessage(SubscriberMDN s, CMJSSubscriberMDN.CGEntries entry, SubscribersAdditionalFields saf, AuthorizingPerson ap, Address ads, Boolean isExcelDownload,String str_tomcatPath) {
		entry.setID(s.getID());

		if (s.getActivationTime() != null) {
			entry.setActivationTime(s.getActivationTime());
		}
		if (s.getAuthenticationPhoneNumber() != null) {
			try {
				entry.setAuthenticationPhoneNumber(new String(s.getAuthenticationPhoneNumber().getBytes("ASCII")));
			} catch (UnsupportedEncodingException ex) {
				log.error("Conversion of authentication number to ASCII failed ",ex);
			}
		}
		if (s.getSubscriber().getSecurityQuestion() != null) {
			entry.setSecurityQuestion(s.getSubscriber().getSecurityQuestion());
		}

		if (s.getSubscriber().getSecurityAnswer() != null) {
			entry.setAuthenticationPhrase(s.getSubscriber().getSecurityAnswer());
		}

		if (s.getCreateTime() != null) {
			entry.setCreateTime(s.getCreateTime());
		}

		if (s.getDigestedPIN() != null) {
			entry.setDigestedPIN(s.getDigestedPIN());
		}

		if (s.getH2HAllowedIP() != null) {
			entry.setH2HAllowedIP(s.getH2HAllowedIP());
		}

		if ((Long) s.getLastTransactionID() != null) {
			entry.setLastTransactionID(s.getLastTransactionID());
		}

		if (s.getLastTransactionTime() != null) {
			entry.setLastTransactionTime(s.getLastTransactionTime());
		}

		if (s.getLastUpdateTime() != null) {
			entry.setLastUpdateTime(s.getLastUpdateTime());
		}
		if(!isExcelDownload) {
			if(s.getSubscriber().getUpgradableKYCLevel()!=null&&
					(CmFinoFIX.UpgradeState_Upgradable.equals(s.getSubscriber().getUpgradeState())
							||CmFinoFIX.UpgradeState_Rejected.equals(s.getSubscriber().getUpgradeState()))){
				KYCLevel kycLevel=kyclevelDao.getByKycLevel(s.getSubscriber().getUpgradableKYCLevel());
				entry.setUpgradableKYCLevelText(kycLevel.getKYCLevelName());
				entry.setKYCLevel(kycLevel.getKYCLevel());
				entry.setKYCLevelText(s.getSubscriber().getKYCLevelByKYCLevel().getKYCLevelName());
			}else if (s.getSubscriber().getKYCLevelByKYCLevel()!=null){
				entry.setKYCLevel(s.getSubscriber().getKYCLevelByKYCLevel().getKYCLevel());
				entry.setKYCLevelText(s.getSubscriber().getKYCLevelByKYCLevel().getKYCLevelName());
			}
		}
		if (s.getMDN() != null) {
			entry.setMDN(s.getMDN());
		}
		if ( s.getApplicationID()!= null){
			entry.setApplicationID(s.getApplicationID());
		}

		entry.setMDNRestrictions(s.getRestrictions());
		entry.setStatus(s.getStatus());

		if (s.getUpdatedBy() != null) {
			entry.setUpdatedBy(s.getUpdatedBy());
		}
		if (s.getCreatedBy() != null) {
			entry.setCreatedBy(s.getCreatedBy());
		}

		if (s.getStatusTime() != null) {
			entry.setStatusTime(s.getStatusTime());
		}


		// a mdn will always have a subscriber
		if (s.getSubscriber() != null) {
			entry.setSubscriberID(s.getSubscriber().getID());
		}

		entry.setWrongPINCount(s.getWrongPINCount());

		// subscriber realted fields
		if (s.getSubscriber().getFirstName() != null) {
			entry.setFirstName(s.getSubscriber().getFirstName());
		}

		if (s.getSubscriber().getLastName() != null) {
			entry.setLastName(s.getSubscriber().getLastName());
		}
		
		if (s.getSubscriber().getNickname() != null) {
			entry.setNickname(s.getSubscriber().getNickname());
		}
		if(s.getKTPDocumentPath()!=null){
			entry.setKTPDocumentPath(str_tomcatPath+"/"+s.getKTPDocumentPath());
		}
		
		if(s.getSubscriberFormPath()!=null){
			entry.setSubscriberFormPath(str_tomcatPath+"/"+s.getSubscriberFormPath());
		}
		
		if(s.getSupportingDocumentPath()!=null){
			entry.setSupportingDocumentPath(str_tomcatPath+"/"+s.getSupportingDocumentPath());
		}

		if (s.getSubscriber().getEmail() != null) {
			entry.setEmail(s.getSubscriber().getEmail());
		}



		entry.setLanguage(s.getSubscriber().getLanguage());

		entry.setMSPID(s.getSubscriber().getmFinoServiceProviderByMSPID().getID());

		if (s.getSubscriber().getNotificationMethod() != null) {
			entry.setNotificationMethod(s.getSubscriber().getNotificationMethod());
		}

		if (s.getSubscriber().getEmail() != null) {
			entry.setEmail(s.getSubscriber().getEmail());
		}

		entry.setSubscriberRestrictions(s.getSubscriber().getRestrictions());
		
		if(s.getOtherMDN() != null){
			entry.setOtherMDN(s.getOtherMDN());
		}

		// @XC, Teja: Do we still need this?
				if (s.getSubscriber().getStatus() != null) {
					entry.setSubscriberStatus(s.getStatus());
				}

				// if (s.getSubscriber().getUpdatedBy() != null) {
				// entry.setUpdatedBy(s.getSubscriber().getUpdatedBy());
				// }

				entry.setSubscriberType(s.getSubscriber().getType());

				if (s.getSubscriber().getTimezone() != null) {
					entry.setTimezone(s.getSubscriber().getTimezone());
				}

				entry.setCurrency(s.getSubscriber().getCurrency());
				entry.setSubscriberTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberType, s.getSubscriber().getLanguage(), s.getSubscriber().getType()));
				entry.setLanguageText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, s.getSubscriber().getLanguage(), s.getSubscriber().getLanguage()));
				entry.setSubscriberStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, s.getSubscriber().getLanguage(), s.getStatus()));
//				entry.setPartnerTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerType, s.getSubscriber().getLanguage(), s.getSubscriber().getPartnerType()));
				entry.setMDNRestrictionsText(enumTextService.getRestrictionsText(CmFinoFIX.TagID_SubscriberRestrictions, s.getSubscriber().getLanguage(), s.getRestrictions().toString()));
				entry.setDoGeneratePin(false);

				if (s.getVersion() != null) {
					entry.setRecordVersion(s.getVersion());
				}
				/* used in 1.9
				 * if (s.getSubscriber().getPartnerType() != null) {
					entry.setPartnerType(s.getSubscriber().getPartnerType());
				}*/
				if (CmFinoFIX.SubscriberType_Partner.equals(s.getSubscriber().getType())) {
					Set<Partner> partners = s.getSubscriber().getPartnerFromSubscriberID();
					if (partners != null && !partners.isEmpty()) {
						entry.setPartnerType(partners.iterator().next().getBusinessPartnerType());
						if (partnerService.isAgentType(entry.getPartnerType()))
							entry.setPartnerType(CmFinoFIX.TagID_BusinessPartnerTypeAgent);
					}
		
				}
				
				if(!isExcelDownload) {
					Pocket p = subscriberService.getDefaultPocket(s.getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
					entry.setDompetMerchant(Boolean.FALSE);
					if (p != null) {
						if ((p.getPocketTemplate().getAllowance().intValue() & CmFinoFIX.PocketAllowance_MerchantDompet.intValue()) > 0) {
							entry.setDompetMerchant(Boolean.TRUE);
						}
						if( p.getCardPAN() != null ) {
							entry.setAccountNumber(p.getCardPAN());
						}
					}
				}
				if(s.getIDType()!=null){
					entry.setIDType(s.getIDType());
				}
				if(s.getIDNumber()!=null){
					entry.setIDNumber(s.getIDNumber());
				}
				if(s.getSubscriber().getIDExiparetionTime()!=null){
					entry.setExpirationTime(s.getSubscriber().getIDExiparetionTime());
				}
				if(s.getSubscriber().getDateOfBirth()!=null){
					entry.setDateOfBirth(s.getSubscriber().getDateOfBirth());
				}
				if(s.getSubscriber().getBirthPlace()!=null){
					entry.setBirthPlace(s.getSubscriber().getBirthPlace());
				}
				if(s.getSubscriber().getReferenceAccount()!=null){
					entry.setReferenceAccount(s.getSubscriber().getReferenceAccount());
				}
				if(s.getSubscriber().getUpgradeState()!=null){
					entry.setUpgradeState(s.getSubscriber().getUpgradeState());
					entry.setUpgradeStateText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_UpgradeState, CmFinoFIX.Language_English, s.getSubscriber().getUpgradeState()));
				}
				if(!CmFinoFIX.UpgradeState_Upgradable.equals(s.getSubscriber().getUpgradeState())){
					if(s.getSubscriber().getApprovedOrRejectedBy()!=null){
						entry.setApprovedOrRejectedBy(s.getSubscriber().getApprovedOrRejectedBy());
					}
					if(s.getSubscriber().getApproveOrRejectTime()!=null){
						entry.setApproveOrRejectTime(s.getSubscriber().getApproveOrRejectTime());
					}
					if(s.getSubscriber().getApproveOrRejectComment()!=null){
						entry.setApproveOrRejectComment(s.getSubscriber().getApproveOrRejectComment());
					}
				}
				if(s.getSubscriber().getAppliedBy()!=null){
					entry.setAppliedBy(s.getSubscriber().getAppliedBy());
				}
				if(s.getSubscriber().getAppliedTime()!=null){
					entry.setAppliedTime(s.getSubscriber().getAppliedTime());
				}
				if(s.getSubscriber().getDetailsRequired()!=null&&s.getSubscriber().getDetailsRequired()){
					entry.setDetailsRequired(true);
				}else{
					entry.setDetailsRequired(false);
				}

				if(saf!=null){
					if (saf.getProofofAddress() != null) {
						entry.setProofofAddress(saf.getProofofAddress());
					}
					if (saf.getCreditCheck() != null) {
						entry.setCreditCheck(saf.getCreditCheck());
					}
					if (saf.getKinName() != null) {
						entry.setKinName(saf.getKinName());
					}
					if (saf.getKinMDN() != null) {
						entry.setKinMDN(saf.getKinMDN());
					}
					if (saf.getNationality() != null) {
						entry.setNationality(saf.getNationality());
					}
					//if (saf.getReferenceAccount() != null) {
						//    entry.setsu(saf.getReferenceAccount());
						//}
					if (saf.getSubsCompanyName() != null) {
						entry.setCompanyName(saf.getSubsCompanyName());
					}
					if (saf.getSubscriberMobileCompany() != null) {
						entry.setSubscriberMobileCompany(saf.getSubscriberMobileCompany());
					}
					if (saf.getCertofIncorporation() != null){
						entry.setCertofIncorporation(saf.getCertofIncorporation());
					}
					if(saf.getNationality()!=null){
						entry.setNationality(saf.getNationality());
					}
				}
				if(ap!=null){
					if (ap.getFirstName() != null) {
						entry.setAuthoFirstName(ap.getFirstName());
					}
					if (ap.getLastName()!=null){
						entry.setAuthoLastName(ap.getLastName());
					}
					if (ap.getDateOfBirth()!=null){
						entry.setAuthoDateofBirth(ap.getDateOfBirth());

					}
					if (ap.getIDDesc()!=null){
						entry.setAuthoIDDescription(ap.getIDDesc());

					}
					if (ap.getIDNumber()!=null){
						entry.setAuthorizingPersonIDNumber(ap.getIDNumber());

					}
				}
				if(ads!=null){
					if (ads.getLine1() != null) {
						entry.setPlotNo(ads.getLine1());
					}
					if(ads.getLine2()!=null){
						entry.setStreetAddress(ads.getLine2());
					}
					if (ads.getCity()!=null){
						entry.setCity(ads.getCity());
					}
					if(ads.getState()!=null){
						//	ads.setTownVillage(e.getTownVillage());
					}
					if (ads.getCountry()!=null){
						entry.setCountry(ads.getCountry());
					}
					if (ads.getRegionName()!=null){
						entry.setRegionName(ads.getRegionName());
					}
				}

				if((s.getSubscriber().getSubscriberGroupFromSubscriberID() != null) && (s.getSubscriber().getSubscriberGroupFromSubscriberID().size() > 0)) {
					SubscriberGroup sg = s.getSubscriber().getSubscriberGroupFromSubscriberID().iterator().next();
					entry.setGroupName(sg.getGroup().getGroupName());
					entry.setGroupID(""+sg.getGroup().getID());
				}
				
				if(s.getSubscriber().getRegistrationMedium()!=null){
					entry.setRegistrationMedium(s.getSubscriber().getRegistrationMedium());
				}

	}

	public void updateForwardMessage(CMJSForwardNotificationRequest newMsg, CMJSSubscriberMDN.CGEntries e, Integer oldRestristions) throws Exception{

		newMsg.setDestMDN(e.getMDN());
		newMsg.setFormatOnly(Boolean.FALSE);
		newMsg.setMSPID(e.getMSPID());
		newMsg.setSourceMDN(e.getMDN());
		Integer newRestrictions = e.getMDNRestrictions();
		Boolean isNewAbsolutLocked = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) == CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
		Boolean isNewRestrictionsNone = ((newRestrictions | CmFinoFIX.SubscriberRestrictions_None) == CmFinoFIX.SubscriberRestrictions_None);
		Boolean isNewSecurityLocked = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_SecurityLocked) == CmFinoFIX.SubscriberRestrictions_SecurityLocked);
		Boolean isNewSelfSuspended = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_SelfSuspended) == CmFinoFIX.SubscriberRestrictions_SelfSuspended);
		Boolean isNewSuspended = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_Suspended) == CmFinoFIX.SubscriberRestrictions_Suspended);

		Boolean isOldAbsolutLocked = ((oldRestristions & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) == CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
		Boolean isOldRestrictionsNone = ((oldRestristions | CmFinoFIX.SubscriberRestrictions_None) == CmFinoFIX.SubscriberRestrictions_None);
		Boolean isOldSecurityLocked = ((oldRestristions & CmFinoFIX.SubscriberRestrictions_SecurityLocked) == CmFinoFIX.SubscriberRestrictions_SecurityLocked);
		Boolean isOldSelfSuspended = ((oldRestristions & CmFinoFIX.SubscriberRestrictions_SelfSuspended) == CmFinoFIX.SubscriberRestrictions_SelfSuspended);
		Boolean isOldSuspended = ((oldRestristions & CmFinoFIX.SubscriberRestrictions_Suspended) == CmFinoFIX.SubscriberRestrictions_Suspended);

		Boolean isAbsolutLockedChanged = (isOldAbsolutLocked ^ isNewAbsolutLocked);
		Boolean isRestrictionsNoneChanged = !isOldRestrictionsNone;
		Boolean isSecurityLockedChanged = (isNewSecurityLocked ^ isOldSecurityLocked);
		Boolean isSelfSuspendedChanged = (isOldSelfSuspended ^ isNewSelfSuspended);
		Boolean isSuspendedChanged = (isOldSuspended ^ isNewSuspended);

		if ((isAbsolutLockedChanged && isNewAbsolutLocked) || (isSecurityLockedChanged && isNewSecurityLocked) || (isSelfSuspendedChanged && isNewSelfSuspended) || (isSuspendedChanged && isNewSuspended)) {
			newMsg.setCode(getMDNNotificationCode(true));
			forwardNotificationRequestProcessor.process(newMsg);
		} else if (isRestrictionsNoneChanged && isNewRestrictionsNone) {
			newMsg.setCode(getMDNNotificationCode(false));
			forwardNotificationRequestProcessor.process(newMsg);
		}
	}

	Integer getMDNNotificationCode(Boolean chk) {
		if (chk) {
			return CmFinoFIX.NotificationCode_MDNAccountSuspendNotification;
		} else {
			return CmFinoFIX.NotificationCode_MDNReleaseSuspension;
		}
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSSubscriberMDN realMsg = (CMJSSubscriberMDN) msg;
		SubscriberMDNDAO mdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
		SubscribersAdditionalFieldsDAO subscribersadditionalfieldsDao = DAOFactory.getInstance().getSubscribersAdditionalFieldsDAO();
		AuthorizingPersonDAO authorizingPersonDAO = DAOFactory.getInstance().getAuthorizingPersonDAO();
		AddressDAO addressDAO = DAOFactory.getInstance().getAddressDAO();

		//tomcat path
		String str_requestPath=httpServletRequest.getRequestURL().toString();
		String str_contextPath=httpServletRequest.getContextPath();
		String str_tomcatPath=str_requestPath.substring(0, str_requestPath.indexOf(str_contextPath));
		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			CMJSSubscriberMDN.CGEntries[] entries = realMsg.getEntries();
			boolean isResetPinSuccess = true;
			CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			for (CMJSSubscriberMDN.CGEntries e : entries) {
				Integer mdnRestrictions = e.getMDNRestrictions();
				SubscriberMDN s = mdnDao.getById(e.getID());
				Subscriber sub=s.getSubscriber();
				log.info("SubscriberMDN:"+s.getID()+" details edit requested by user:"+getLoggedUserNameWithIP());

				Integer oldRestrictions = s.getRestrictions();

				if (!e.getRecordVersion().equals(s.getVersion())) {
					log.warn("SubscriberMDN:"+s.getID()+" Stale Data Exception for user:"+getLoggedUserNameWithIP());
					handleStaleDataException();
				}
				
				//setting the company code before calling update entity
				//cal company code based on the MDN if mdn is edited
				if (e.getMDN() != null && e.getMDN().length() > 0) {
					String mNumber = e.getMDN();
					validateSubscriberMDN(mNumber);
					company = subscriberService.getCompanyFromMDN(mNumber);
					if (company != null && company.getID() != userService.getUserCompany().getID()) {
						errorMsg.setErrorDescription(MessageText._("Cannot Edit MDN of other Brands"));
						errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
						log.warn(getLoggedUserNameWithIP() + " entered " + e.getMDN() + ", not allowed to edit MDN of other Brands");
						return errorMsg;
					} else if (company == null) {
						// return failure message saying invalid mdn
						errorMsg.setErrorDescription(MessageText._("Invalid MDN"));
						log.warn("SubscriberMDN:"+s.getID()+" entered by user " + getLoggedUserNameWithIP() +" does not match to any company code ");
						return errorMsg;
					}
				}

				if(CmFinoFIX.SubscriberStatus_Active.equals(e.getStatus())&& !isActivationAllowed(sub,e)){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Subscriber Activation not allowed."));
					log.warn("SubscriberMDN:"+s.getID() + "Subscriber Activation not allowed for user:"+ getLoggedUserNameWithIP());
					return error;
				}
				if(CmFinoFIX.SubscriberStatus_InActive.equals(e.getStatus())){
          if(!CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(e.getMDNRestrictions())) 
          {
						CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
						error.setErrorDescription(MessageText._("Subscriber InActivation not allowed."));
						log.warn("SubscriberMDN:"+s.getID() + "Subscriber InActivation not allowed for user:"+ getLoggedUserNameWithIP());
						return error;
					}
				}
				if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(e.getStatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Changing Subscriber status to 'Not Registered' is not allowed."));
					log.warn("SubscriberMDN:"+s.getID() + "Changing Subscriber status to 'Not Registered' is not allowed for user:"+ getLoggedUserNameWithIP());
					return error;
				}
				if(CmFinoFIX.SubscriberStatus_Initialized.equals(e.getStatus()) && !(CmFinoFIX.SubscriberStatus_Initialized.equals(sub.getStatus()) ||
						CmFinoFIX.SubscriberStatus_Suspend.equals(sub.getStatus()) || CmFinoFIX.SubscriberStatus_InActive.equals(sub.getStatus())
						||CmFinoFIX.SubscriberStatus_NotRegistered.equals(sub.getStatus())) ){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Intializing subscriber not allowed."));
					log.warn("SubscriberMDN:"+s.getID() + "Initializing subscriber not allowed for "+ getLoggedUserNameWithIP());
					return error;
				}
				if(CmFinoFIX.SubscriberStatus_Suspend.equals(e.getStatus())&&!CmFinoFIX.SubscriberStatus_Suspend.equals(sub.getStatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Suspending of subscriber not allowed."));
					log.warn("SubscriberMDN:"+s.getID() + "Suspending of subscriber not allowed for "+ getLoggedUserNameWithIP());
					return error;
				}
				if(e.getStatus() != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(e.getStatus()) &&
						CmFinoFIX.SubscriberStatus_Suspend.equals(sub.getStatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Suspended subscriber can be moved to Intialized status only"));
					log.warn("SubscriberMDN:"+s.getID() + "Suspended subscriber can be moved to Intialized status only for "+ getLoggedUserNameWithIP());
					return error;
				}
				if(e.getStatus() != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(e.getStatus()) &&
						CmFinoFIX.SubscriberStatus_InActive.equals(sub.getStatus()) && !isActivationAllowed(sub,e)){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Inactive subscriber can be moved to Intialized status only"));
					log.warn("SubscriberMDN:"+s.getID() + "Suspended subscriber can be moved to Intialized status only for "+ getLoggedUserNameWithIP());
					return error;
				}
				// Dont allow to change status when subscriber is PendingRetired or
				// Retired.
				if (e.getStatus() != null) {
					if (s.getStatus().equals(CmFinoFIX.SubscriberStatus_Retired) || s.getStatus().equals(CmFinoFIX.SubscriberStatus_PendingRetirement)) {
						if (e.getStatus() != CmFinoFIX.SubscriberStatus_Retired && e.getStatus() != CmFinoFIX.SubscriberStatus_PendingRetirement) {
							handleRetiredSubscriber();
						}
						else
							log.warn("SubscriberMDN:"+s.getID() + "Subscriber status is eigther retired or pending retirement for "+ getLoggedUserNameWithIP());
					}
				}

				if (e.getStatus() != null && (e.getStatus().equals(CmFinoFIX.SubscriberStatus_Retired) || e.getStatus().equals(CmFinoFIX.SubscriberStatus_PendingRetirement))) {
					/*if(!isSubscriberEligibleTobeRetired(s)){
						CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
						error.setErrorDescription(MessageText._("Subscriber retirement failed as his emoney pocket balance is not equal to zero"));
						log.warn("SubscriberMDN:"+s.getID() + " Subscriber retirement failed as his emoney pocket balance is not equal to zero "+ getLoggedUserName());
						return error;
					}*/
					e.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
					e.setMDNRestrictions(CmFinoFIX.SubscriberRestrictions_None);
					int code = subscriberService.retireSubscriber(s);
					if (code == Codes.OPERATION_NOT_ALLOWED) {
						CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
						error.setErrorDescription(MessageText._("Subscriber retirement failed. This subscriber is registered as an active merchant. MDN will be suspended to prevent merchant performing transactions."));
						log.warn("SubscriberMDN:"+s.getID() + " Merchant MDN suspended to prevent transactions for user "+ getLoggedUserNameWithIP());
						return error;
					}
				}


				if (e.getMDN() != null) {
					//Need to take care of MDNs which are tagged with R
					int findR = e.getMDN().indexOf('R');
					if (findR == -1) {
						//chk whether he is a merchant if yes try to see the mdnrange is valid or not
						Merchant merchant = s.getSubscriber().getMerchant();
						log.info("Checking MDN range is valid for merchant: " + merchant.getID());
						if (merchant != null) {
							if (merchant.getMerchantByParentID() != null) {
								Merchant merchantparent = merchant.getMerchantByParentID();
								if (!mdnRangeService.isMDNInParentsRange(Long.parseLong(e.getMDN().substring(2)), merchantparent)) {
									CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
									error.setErrorDescription(MessageText._("Subscriber updation failed. This subscriber is a merchant, and his MDN should lie inside his parent's range."));
									log.warn("Subscriber updation failed for merchant " + merchant.getID()+" as his his MDN does not lie in his parent's range");
									return error;
								} else {
									//since merchant is now in the range reset his range check
									merchant.setRangeCheck(merchant.getRangeCheck() == null ? null : merchant.getRangeCheck() & CmFinoFIX.RangeCheck_MDNRangeNotInParentsRange);
									MerchantDAO merchantDao = DAOFactory.getInstance().getMerchantDAO();
									merchantDao.save(merchant);
									log.info("merchant range check is reset for " + merchant.getID());
								}
							}
						}
					}
				}
				if(CmFinoFIX.UpgradeState_Upgradable.equals(sub.getUpgradeState())
						&&e.getKYCLevel()!=null
						&&(!e.getKYCLevel().equals(sub.getUpgradableKYCLevel()))){
					errorMsg.setErrorDescription(MessageText._("Subscriber approval pending so kyclevel edit not allowed"));
					log.info("Subscriber " + sub.getID() +" approval is pending so kyc level edit not allowed");
					return errorMsg;
				}else if(e.getKYCLevel()!=null && sub.getKYCLevelByKYCLevel().getKYCLevel()>e.getKYCLevel()){
					errorMsg.setErrorDescription(MessageText._("Degrade of kyclevel not allowed"));
					log.warn("kyc level can not be degraded for " + sub.getID());
					return errorMsg;
				}
							
				Set<SubscribersAdditionalFields> saf2 = sub.getSubscribersAdditionalFieldsFromSubscriberID();
				SubscribersAdditionalFields saf;
				if(saf2.isEmpty()){
					if(checkAdditionalFields(e)){
						saf= new SubscribersAdditionalFields();
					}else{
						saf=null;
					}
				}else{
					saf=saf2.iterator().next();
				}

				Address ads = sub.getAddressBySubscriberAddressID();
				if (ads == null && checkAddress(e)){
					ads = new Address();
				}
				AuthorizingPerson ap= sub.getAuthorizingPerson();
				if (ap == null && checkAuthorizingPersonDetails(e)){
					ap = new AuthorizingPerson();
				}
				//check on kyc level need to be added on edit to lower kycleve
				if(sub.getDetailsRequired()!=null&&sub.getDetailsRequired()){
					sub.setDetailsRequired(false);
				}
				if(sub.getStatus().equals(CmFinoFIX.SubscriberStatus_NotRegistered)){
					updatePocket(s);
//					SubscriberServiceExtended.updateUnRegisteredTxnInfoToActivated(s);
				}
				updateEntity(s, e);

				if(ap!=null){
					updateAuthorizing(ap, e);
					authorizingPersonDAO.save(ap);
					log.info("authorizing person updated for " + e.getID());
					sub.setAuthorizingPerson(ap);
				}
				if(ads!=null){
					updateAddress(ads, e);
					addressDAO.save(ads);
					log.info("Address updated for " + e.getID());
					sub.setAddressBySubscriberAddressID(ads);
				}
				//Generate OTP for the subscriber if the status is changed from Suspend to Initialise or Inactive to Initialise.
				if(e.getStatus() != null && CmFinoFIX.SubscriberStatus_Initialized.equals(e.getStatus())){
					sendOTPOnIntialized = ConfigurationUtil.getSendOTPOnIntialized();
					if(sendOTPOnIntialized){
						generateAndSendOTP(mdnDao, s, CmFinoFIX.NotificationCode_New_OTP_Success);
						log.info("new OTP is generated for the subscriber" + s.getID() + "as the status is changed from Suspend to Initialise or Inactive to Initialise");
					}
					/*
					 * Commented as per requirement for hub
					else{
						//no otp so activate subscriber
						s.setStatus(CmFinoFIX.SubscriberStatus_Active);
						sub.setStatus(CmFinoFIX.SubscriberStatus_Active);
						subscriberStatusEventService.upsertNextPickupDateForStatusChange(sub,true);
					}
					*/
				}
				subscriberDao.save(sub);
				log.info("updated subscriber: " + sub.getID());
				if(e.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
					mailService.generateEmailVerificationMail(sub, e.getEmail());
				}				
				if(saf!=null){
					updateAdditionalFields(saf, e);
					saf.setSubscriber(sub);
					log.info("additional fields are updated for subscriber: " + sub.getID());
					subscribersadditionalfieldsDao.save(saf);
				}
				
				mdnDao.save(s);
				updateMessage(s, e, saf, ap, ads,false,str_tomcatPath);

				if (mdnRestrictions != null) {
					CMJSForwardNotificationRequest forwardMsg = new CMJSForwardNotificationRequest();
					updateForwardMessage(forwardMsg, e, oldRestrictions);
					//updateForwardMessage(forwardMsg, e, oldRestrictions, saf);
				}
//				if (e.getIsForceCloseRequested() != null && e.getIsForceCloseRequested().booleanValue()) {
//					MDNRetireServiceImpl retireService = new MDNRetireServiceImpl();
//					retireService.retireMDN(s);
//					//TODO need to handle failure case
//				}
				
				/*
				 * if Kyc level is upgraded and a/c no is given, then Bank Pocket is created for the given configuration.
				 * If there is no default pocket template configured, an error message is returned.
				 */
				if(StringUtils.isNotBlank(e.getAccountNumber()))
				{
					Pocket existingBankPocket = subscriberService.getDefaultPocket(s.getMDN(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
					PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
					if(existingBankPocket != null)
					{
						log.info("Bank pocket already exists for subscriber with mdn " + s.getMDN());
						String existingAccountNo = existingBankPocket.getCardPAN();
						if(!existingAccountNo.equals(e.getAccountNumber()))
						{
							log.info("Updating the old bank a/c no " + existingAccountNo + " with the new a/c no "+e.getAccountNumber() + "for subscriber with mdn "+ s.getMDN());
							existingBankPocket.setCardPAN(e.getAccountNumber());
							pocketDao.save(existingBankPocket);								 
						}
					}
					else
					{	if(e.getPocketTemplateConfigID() != null) {
							PocketTemplateConfig ptc = ptcDao.getById(e.getPocketTemplateConfigID());
							PocketTemplate pocketTemplate = ptc.getPocketTemplate();
							log.info("Selected Bank pocket template is " + pocketTemplate.getDescription());
							boolean isallowed=pocketService.checkCount(pocketTemplate,s);
							if(!isallowed){
								log.error("PocketProcessor :: Pocket count limit reached for template:"+pocketTemplate.getDescription()+" for MDN:"+ s.getMDN() + " by user:"+getLoggedUserNameWithIP());	
								return getErrorMessage(MessageText._(" Pocket count Limit reached for this template  "), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Pocket count Limit reached for this template"));  		
							}
							
							Pocket bankPocket = pocketService.createPocket(pocketTemplate, s, CmFinoFIX.PocketStatus_Active, true, e.getAccountNumber());
							if(bankPocket == null){								
								errorMsg.setErrorDescription(MessageText._("Default Bank Pocket creation failed for the Subscriber with the selected ptc:" + ptc.getID()));
								errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
								log.info("Default Bank Pocket creation failed for Subscriber "+s.getID());
								return errorMsg;
							}
						} else {
							errorMsg = createBankPocket(e.getAccountNumber(), s, e.getKYCLevel(), Long.parseLong(e.getGroupID()));
							if( !CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode()) )
							{
								return errorMsg;
							}
						}
					}
				}	
			}

			realMsg.setsuccess(isResetPinSuccess);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {

			SubscriberMdnQuery query = new SubscriberMdnQuery();
			//setting the company to the query parameter.
			if (userService.getUserCompany() != null) {
				query.setCompany(userService.getUserCompany());
			}
			query.setFirstName(realMsg.getFirstNameSearch());
			query.setLastName(realMsg.getLastNameSearch());
			query.setMdn(subscriberService.normalizeMDN(realMsg.getMDNSearch()));
			query.setExactMDN(realMsg.getExactMDNSearch());
			query.setAccountNumber(realMsg.getCardPAN());
			if(realMsg.getSubscriberSearch()!=null&&realMsg.getSubscriberSearch()){
				query.setOnlySubscribers(realMsg.getSubscriberSearch());
			}else{
				query.setOnlySubscribers(false);
			}

			if (!CmFinoFIX.JSmFinoAction_Update.equals(realMsg.getmfinoaction())) {
				query.setStart(realMsg.getstart());
				query.setLimit(realMsg.getlimit());
			}

			query.setId(realMsg.getIDSearch());

			query.setStartRegistrationDate(realMsg.getStartDateSearch());
			query.setEndRegistrationDate(realMsg.getEndDateSearch());

			if (StringUtils.isEmpty(realMsg.getExactMDNSearch())) {
				query.setAssociationOrdered(true);
			}
			if (authorizationService.isAuthorized(CmFinoFIX.Permission_Transaction_OnlyBank_View)) {
				User user = userService.getCurrentUser();
				Set<BankAdmin> admins = user.getBankAdminFromUserID();

				if (admins != null && admins.size() > 0) {
					BankAdmin admin = (BankAdmin) admins.toArray()[0];
					if (admin != null && admin.getBank() != null) {
						query.setBankCode(admin.getBank().getBankCode());
						// setting company as null for bank roles..
						query.setCompany(null);
					}
				}
			}
			if(realMsg.getUpgradeStateSearch()!=null&&!CmFinoFIX.UpgradeStateSearch_All.equals(realMsg.getUpgradeStateSearch())){
				query.setState(realMsg.getUpgradeStateSearch());
			}
			if(realMsg.getMDNStatus()!=null){
					query.setStatusEQ(realMsg.getMDNStatus());
			}
			List<SubscriberMDN> results = mdnDao.get(query);

			realMsg.allocateEntries(results.size());

			for (int i = 0; i < results.size(); i++) {
				SubscriberMDN s = results.get(i);
				CMJSSubscriberMDN.CGEntries entry = new CMJSSubscriberMDN.CGEntries();
				SubscribersAdditionalFields saf=null;
				Address ads=s.getSubscriber().getAddressBySubscriberAddressID();
				AuthorizingPerson ap=s.getSubscriber().getAuthorizingPerson();
				if(! s.getSubscriber().getSubscribersAdditionalFieldsFromSubscriberID().isEmpty()){
					saf=s.getSubscriber().getSubscribersAdditionalFieldsFromSubscriberID().iterator().next();
				}
				updateMessage(s, entry, saf, ap, ads,realMsg.getIsExcelDownload(),str_tomcatPath);
				realMsg.getEntries()[i] = entry;
				log.info("Subscriber:"+s.getID()+" details viewed completed by user:"+getLoggedUserNameWithIP());
			}

			/*if (realMsg.getExactMDNSearch() != null) {
				if (results.size() == 1) {
					SubscriberMDN subMdn = (SubscriberMDN) results.get(0);
					if (subMdn.getSubscriber().getType().equals(CmFinoFIX.SubscriberType_Partner)) {
						handleMerchantAlreadyExists();
					}else if (subMdn.getStatus().equals(CmFinoFIX.SubscriberStatus_PendingRetirement)||subMdn.getStatus().equals(CmFinoFIX.SubscriberStatus_Retired)) {
						handleInvalidSubscriberStatus();
					}
				}
			}*/

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());


		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			CMJSSubscriberMDN.CGEntries[] entries = realMsg.getEntries();

			for (CMJSSubscriberMDN.CGEntries e : entries) {
				SubscriberMDN mdn = new SubscriberMDN();
				Subscriber s = new Subscriber();
				SubscribersAdditionalFields saf = new SubscribersAdditionalFields();
				AuthorizingPerson ap = new AuthorizingPerson();
				Address ads = new Address();
				KYCLevel kyclevel = null;
				String bankAccountNumber = null;
				
				String cardPan=e.getAccountNumber();
				if(StringUtils.isNotBlank(cardPan)) {
					validateAccountNumber(cardPan);
				}
				
				mdn.setSubscriber(s);
				e.setStatus(CmFinoFIX.MDNStatus_Initialized);
				e.setSubscriberType(CmFinoFIX.SubscriberType_Subscriber);
				//setting the company code before calling update entity
				//cal company code based on the MDN
				String mNumber = subscriberService.normalizeMDN(e.getMDN());
				validateSubscriberMDN(mNumber);
				company = subscriberService.getCompanyFromMDN(mNumber);
				if (company != null && company.getID() == userService.getUserCompany().getID()) {
					e.setCompanyID(company.getID());
				} else if (company != null && company.getID() != userService.getUserCompany().getID()) {
					CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
					errorMsg.setErrorDescription(MessageText._("Cannot Add MDN of other Brands"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					log.warn("Subscriber " + s.getID() + "can not add MDN for other brands");
					return errorMsg;
				} else if (company == null) {
					// return failure message saying invalid mdn
					CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
					errorMsg.setErrorDescription(MessageText._("Invalid MDN"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					log.warn("For This MDN " + s.getID() + "Company is null");
					return errorMsg;
				}
				if(e.getKYCLevel()!=null){
					kyclevel = kyclevelDao.getByKycLevel(ConfigurationUtil.getIntialKyclevel());
					if(kyclevel==null){
						CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
						errorMsg.setErrorDescription(MessageText._("Intial Kyclevel not available"));
						errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
						log.warn("Initial KYC level not available for " + s.getID());
						return errorMsg;
					}
					s.setKYCLevelByKYCLevel(kyclevel);
				}

				updateEntity(mdn, e);
				isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();
				if(isEMoneyPocketRequired == false){
					//TODO: handle BSIM validation part here
					
					//bankAccountNumber = nsah.getAccountNumber();		//Use this to get Account Number from ISO
					bankAccountNumber = e.getAccountNumber(); 			//changed this to accept the account number from Admin App 
					log.info("BankAccountNumber"+bankAccountNumber);
					if(bankAccountNumber == null || StringUtils.isEmpty(bankAccountNumber)){
						CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
						errorMsg.setErrorDescription(MessageText._("Failed due to invalid Bank Account Number"));
						errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
						log.warn("Failed due to invalid Bank Account Number for:" + mdn.getMDN());
						return errorMsg;
					}
				}
				
				log.info("SubscriberMDN:"+mdn.getID()+" created by user:"+getLoggedUserNameWithIP());

				boolean addressFlag = checkAddress(e);
				if(addressFlag){
					updateAddress(ads, e);
					s.setAddressBySubscriberAddressID(ads);
					log.info("Address updated for " + s.getID() + "by user " + getLoggedUserNameWithIP());
					addressDAO.save(ads);
				}else{
					ads=null;
				}

				boolean authorizingFlag = checkAuthorizingPersonDetails(e);
				if(authorizingFlag){
					updateAuthorizing(ap, e);
					authorizingPersonDAO.save(ap);
					s.setAuthorizingPerson(ap);
				}else{
					ap=null;
				}
				subscriberDao.save(s);
				log.info("Subscriber "+ s.getID()+ " updated by user " + getLoggedUserNameWithIP());
				if(e.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
					mailService.generateEmailVerificationMail(s, e.getEmail());
				}
				if(s.getSubscriberGroupFromSubscriberID().size() > 0){
					SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
					for(SubscriberGroup sg: s.getSubscriberGroupFromSubscriberID()){
						subscriberGroupDao.save(sg);
					}
				}

				boolean additionalFieldsFlag = checkAdditionalFields(e);
				if(additionalFieldsFlag){
					updateAdditionalFields(saf, e);
					saf.setSubscriber(s);
					subscribersadditionalfieldsDao.save(saf);
				} else{
					saf=null;
				}
				try {
					if (ConfigurationUtil.getSendOTPBeforeApproval()) {
						//OTP
						Integer OTPLength = systemParametersService.getOTPLength();
						String oneTimePin = MfinoUtil.generateOTP(OTPLength);
						String digestPin1 = MfinoUtil.calculateDigestPin(mdn.getMDN(), oneTimePin);
						mdn.setOTP(digestPin1);
						mdn.setOTPExpirationTime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));

						mdnDao.save(mdn);
						log.info("new OTP set for " + mdn.getID() + " by user " + getLoggedUserNameWithIP());
						NotificationWrapper smsNotificationWrapper=subscriberServiceExtended.generateOTPMessage(oneTimePin, CmFinoFIX.NotificationMethod_SMS);
						smsNotificationWrapper.setDestMDN(mdn.getMDN());
						smsNotificationWrapper.setLanguage(mdn.getSubscriber().getLanguage());
						smsNotificationWrapper.setFirstName(mdn.getSubscriber().getFirstName());
		            	smsNotificationWrapper.setLastName(mdn.getSubscriber().getLastName());
						String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper,true);
						log.info("OTP SMS:" + smsMessage);
						String mdn2 = mdn.getMDN();
						smsService.setDestinationMDN(mdn2);
						smsService.setMessage(smsMessage);
						smsService.setNotificationCode(smsNotificationWrapper.getCode());
						smsService.asyncSendSMS();
						if(((e.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0) && e.getEmail() != null){
							NotificationWrapper emailNotificationWrapper=subscriberServiceExtended.generateOTPMessage(oneTimePin, CmFinoFIX.NotificationMethod_Email);
							emailNotificationWrapper.setDestMDN(mdn.getMDN());
							emailNotificationWrapper.setLanguage(mdn.getSubscriber().getLanguage());
							emailNotificationWrapper.setFirstName(mdn.getSubscriber().getFirstName());
							emailNotificationWrapper.setLastName(mdn.getSubscriber().getLastName());
							String emailMessage = notificationMessageParserService.buildMessage(emailNotificationWrapper,true);
							String to=s.getEmail();
							String name=s.getFirstName();
							String sub = ConfigurationUtil.getOTPMailSubsject();
							mailService.asyncSendEmail(to, name, sub, emailMessage);
						}
					}
					else {
						mdnDao.save(mdn);
					}
				} catch (ConstraintViolationException t) {
					handleUniqueConstraintViolation(t);
					CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
					errorMsg.setErrorDescription(MessageText._("Exception")+t);
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					return errorMsg;
				}

				Long groupID = null;
				if (StringUtils.isNotEmpty(e.getGroupID())) {
					groupID = Long.valueOf(e.getGroupID());
				}

				
				if(isEMoneyPocketRequired == true){
					PocketTemplate svaPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(e.getKYCLevel(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
					if (svaPocketTemplate == null) {
						CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
						errorMsg.setErrorDescription(MessageText._("No Default SVA Pocket set for this KYC"));
						errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
						log.warn("No Default SVA Pocket set for " + s.getID());
						return errorMsg;
					}
					
					log.info("SubscriberMdnProcessor:: "+isEMoneyPocketRequired);
					if(isEMoneyPocketRequired == true){
					try{
						cardPan=pocketService.generateSVAEMoney16DigitCardPAN(mdn.getMDN());
					}catch (Exception ex) {
						log.error("Exception to create cardPan",ex);
					}
					
					//Create default emoney pocket
					Pocket epocket = pocketService.createPocket(svaPocketTemplate, mdn, CmFinoFIX.PocketStatus_Initialized, true, cardPan);
					if(epocket==null){
						log.info("Default emoney pocket creation failed for subscriber "+mdn.getID());
					}			
					}
				}
				
				cardPan=e.getAccountNumber();
				if(StringUtils.isNotEmpty(cardPan)) {
					if(e.getPocketTemplateConfigID() != null) {	//Create bank pocket with the selected ptc(from UI) associated pocket template					
						PocketTemplateConfig ptc = ptcDao.getById(e.getPocketTemplateConfigID());
						PocketTemplate pocketTemplate = ptc.getPocketTemplate();
						log.info("Selected Bank pocket template is " + pocketTemplate.getDescription());
						boolean isallowed=pocketService.checkCount(pocketTemplate,mdn);
						if(!isallowed){
							log.error("PocketProcessor :: Pocket count limit reached for template:"+pocketTemplate.getDescription()+" for MDN:"+ mdn.getMDN() + " by user:"+getLoggedUserNameWithIP());	
							return getErrorMessage(MessageText._(" Pocket count Limit reached for this template  "), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Pocket count Limit reached for this template"));  		
						}
						
						Pocket bankPocket = pocketService.createPocket(pocketTemplate, mdn, CmFinoFIX.PocketStatus_Active, true, e.getAccountNumber());
						if(bankPocket == null){
							CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
							errorMsg.setErrorDescription(MessageText._("Default Bank Pocket creation failed for the Subscriber with the selected ptc:" + ptc.getID()));
							errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
							log.info("Default Bank Pocket creation failed for Subscriber "+s.getID());
							return errorMsg;
						}
					} else { // create default bank pocket template as per ptc group mapping if ptc is not selected in UI
						log.info("Selected Bank pocket template is null, hence creating pocket as per the default ptc setting");
						CmFinoFIX.CMJSError errorMsg = createBankPocket(cardPan, mdn, e.getKYCLevel(), groupID);
						log.info("Error Code"+errorMsg.getErrorCode());
						if( !CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode()) )
						{
							return errorMsg;
						}
					}
				}
				updateMessage(mdn, e,saf,ap,ads, false,str_tomcatPath);
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
		}

		return realMsg;
	}

	/**
	 * Creates bank pocket for the subscriber with the given account no.
	 * An error message is thrown in case of exceptions
	 * 
	 * @param cardPan
	 * @param mdn
	 * @param kycLevel
	 * @param groupID
	 * @return
	 */
	private CMJSError createBankPocket(String cardPan, SubscriberMDN mdn, Long kycLevel, Long groupID)
	{

		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		if(cardPan != null && cardPan.length() > 0){
			PocketTemplate bankPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevel, true, CmFinoFIX.PocketType_BankAccount, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			if (bankPocketTemplate == null) {
				errorMsg.setErrorDescription(MessageText._("No Default Bank Pocket set for this KYC"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				log.warn("No Default Bank Pocket set for " + mdn.getID());
				return errorMsg;
			}
			if(bankPocketTemplate.getID() >= 0 && cardPan != null)
			{
				boolean isallowed=pocketService.checkCount(bankPocketTemplate,mdn);
				if(!isallowed){
					log.error("PocketProcessor :: Pocket count limit reached for template:"+bankPocketTemplate.getDescription()+" for MDN:"+mdn.getMDN()+" by user:"+getLoggedUserNameWithIP());	
					return getErrorMessage(MessageText._(" Pocket count Limit reached for this template  "), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Pocket count Limit reached for this template"));  		
				}           

				Pocket bankPocket = pocketService.createDefaultBankPocket(bankPocketTemplate.getID(), mdn, cardPan);
				if(bankPocket==null){
					errorMsg.setErrorDescription(MessageText._("Default Bank Pocket creation failed for the Subscriber"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					log.info("Default Bank Pocket creation failed for Subscriber "+mdn.getID());
					return errorMsg;
				}
			}
		}
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		return errorMsg;
	}

	private void updatePocket(SubscriberMDN s) {
		Long unregTemplateID = systemParametersService.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED);
		KYCLevel kycLevel = kyclevelDao.getByKycLevel(ConfigurationUtil.getIntialKyclevel());
		
		Long groupID = null;
		Set<SubscriberGroup> subscriberGroups = s.getSubscriber().getSubscriberGroupFromSubscriberID();
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroup().getID();
		}
		PocketTemplate template = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevel.getKYCLevel(), true, CmFinoFIX.PocketType_SVA, s.getSubscriber().getType(), null, groupID);
		
		Pocket pocket = subscriberService.getDefaultPocket(s.getID(),unregTemplateID);
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		if(pocket!=null){
			pocket.setPocketTemplateByOldPocketTemplateID(pocket.getPocketTemplate());
			pocket.setPocketTemplate(template);
			pocket.setPocketTemplateChangedBy(userService.getCurrentUser().getUsername());
			pocket.setPocketTemplateChangeTime(new Timestamp());
			pocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
			pocket.setStatusTime(new Timestamp());
			pocketDAO.save(pocket);
		}
	}

	/**
	 * Generates new OTP and Send the same as SMS and Email if exists.
	 * @param mdnDao
	 * @param mdn
	 * @param notificationCode
	 */
	private void generateAndSendOTP(SubscriberMDNDAO mdnDao, SubscriberMDN mdn, Integer notificationCode) {
		Subscriber sub = mdn.getSubscriber();
		Integer OTPLength = systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(mdn.getMDN(), oneTimePin);
		mdn.setOTP(digestPin1);
		mdn.setDigestedPIN(null);
		mdn.setAuthorizationToken(null);
		mdn.setOTPExpirationTime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));

		NotificationWrapper notification = new NotificationWrapper();
		notification.setLanguage(sub.getLanguage());
		notification.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		notification.setCode(notificationCode);
		notification.setOneTimePin(oneTimePin);
		notification.setFirstName(sub.getFirstName());
    	notification.setLastName(sub.getLastName());
		String message = notificationMessageParserService.buildMessage(notification,true);
		String mdn2 = mdn.getMDN();
		smsService.setDestinationMDN(mdn2);
		smsService.setMessage(message);
		smsService.setNotificationCode(notification.getCode());
		smsService.asyncSendSMS();
		if(((sub.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0) && sub.getEmail() != null)
		{
			notification.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			String emailMessage = notificationMessageParserService.buildMessage(notification,true);
			String to=sub.getEmail();
			String name=sub.getFirstName();
			String subject = ConfigurationUtil.getOTPMailSubsject();
			mailService.asyncSendEmail(to, name, subject, emailMessage);
		}
	}

	private boolean checkAddress(CGEntries e) {
		if (e.getPlotNo() != null||e.getStreetName()!=null||e.getCountry()!=null
				||e.getCity()!=null||e.getRegionName()!=null) {
			return true;
		}
		return false;
	}

	private boolean checkAdditionalFields(CGEntries e) {
		if (e.getProofofAddress() != null||e.getCreditCheck() != null||e.getKinName() != null||e.getKinMDN() != null
				||e.getNationality() != null||e.getCompanyName() != null||e.getSubscriberMobileCompany() != null||
				e.getCertofIncorporation() != null) {
			return true;
		}
		return false;
	}

	//    public static void main(String[] args) throws UnsupportedEncodingException {
	//    	String  authPhoneNo ="(BEN-G§VP)";
	//    	authPhoneNo = new String(authPhoneNo.getBytes("ASCII"));
	//	System.out.println(authPhoneNo);
	//	}

	private boolean checkAuthorizingPersonDetails(CGEntries e) {
		if (e.getAuthoFirstName() != null||e.getAuthoLastName()!=null||e.getAuthoDateofBirth()!=null
				||e.getAuthoIDDescription()!=null||e.getAuthorizingPersonIDNumber()!=null) {
			return true;
		}
		return false;
	}

	public List<SubscriberMDN> get(SubscriberMdnQuery query) {
		SubscriberMDNDAO mdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		return mdnDao.get(query);
	}

	public boolean isActivationAllowed(Subscriber sub,CMJSSubscriberMDN.CGEntries e){
		if(CmFinoFIX.SubscriberStatus_Active.equals(sub.getStatus())){
			return true;
		}
		if(CmFinoFIX.SubscriberStatus_InActive.equals(sub.getStatus())){
			if(CmFinoFIX.SubscriberRestrictions_NoFundMovement.equals(sub.getRestrictions()) && ((e.getMDNRestrictions()==null) || (CmFinoFIX.SubscriberRestrictions_None.equals(e.getMDNRestrictions())))){
				return true;
			}
		}
		return false;
	}
	
	private boolean isSubscriberEligibleTobeRetired(SubscriberMDN subscriberMDN){	
		List<Pocket> pocketList = getSubscriberPocketsList(subscriberMDN);
		for(Pocket pk:pocketList){
			if(pk.getCurrentBalance().compareTo(BigDecimal.ZERO) != 0 ){
				return false;
			}
		}
		return true;
	}
	
	private List<Pocket> getSubscriberPocketsList(SubscriberMDN subscriberMDN){
		List<Pocket> pkList = null;
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		PocketQuery pocketQuery = new PocketQuery();
		pocketQuery.setMdnIDSearch(subscriberMDN.getID());
		pocketQuery.setPocketType(CmFinoFIX.PocketType_SVA);
		pkList = pocketDAO.get(pocketQuery);
		return pkList;
	}
}
