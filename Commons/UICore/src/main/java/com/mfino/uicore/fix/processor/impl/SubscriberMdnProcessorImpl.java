/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.mfino.dao.KtpDetailsDAO;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.PocketTemplateConfigDAO;
import com.mfino.dao.SubsUpgradeBalanceLogDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.dao.SubscribersAdditionalFieldsDAO;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.KtpDetailsQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.Address;
import com.mfino.domain.AuthPersonDetails;
import com.mfino.domain.BankAdmin;
import com.mfino.domain.Company;
import com.mfino.domain.Groups;
import com.mfino.domain.KtpDetails;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Merchant;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberAddiInfo;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberUpgradeBalanceLog;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.domain.UnregisteredTxnInfo;
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
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	private  boolean sendOTPOnIntialized;
	private  boolean isEMoneyPocketRequired;
	
	private KtpDetailsDAO ktpDetailsDAO= DAOFactory.getInstance().getKtpDetailsDAO();
	private KtpDetailsQuery ktpDetailsQuery= new KtpDetailsQuery();
	
	
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
	private void updateEntity(SubscriberMdn s, CMJSSubscriberMDN.CGEntries e) {
		String ID = String.valueOf(s.getId());

		if (e.getMDN() != null) {
			String mdn = subscriberService.normalizeMDN(e.getMDN());
			if(!mdn.equals(s.getMdn())){
        		log.info("Subscriber MDN:"+ID+" MDN updated to "+mdn+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setMdn(mdn);
		}
		if(e.getIsIdLifetimeText()!=null){
			if(e.getIsIdLifetimeText().equals("true")){
				s.setIsidlifetime(CmFinoFIX.ISIDLifetime_LifeTime_True.toString());
			}else{
				s.setIsidlifetime(CmFinoFIX.ISIDLifetime_LifeTime_False.toString());
				if(e.getIDValidUntil()!=null){
					s.getSubscriber().setIdexiparetiontime(e.getIDValidUntil());
				}
			}
			
		}else{
			if(e.getIDValidUntil()!=null){
				s.getSubscriber().setIdexiparetiontime(e.getIDValidUntil());
			}
		}
		if (e.getAuthenticationPhoneNumber() != null) {
			if(!e.getAuthenticationPhoneNumber().equals(s.getAuthenticationphonenumber())){
        		log.info("Subscriber MDN:"+ID+" Authentication Phone Number updated to "+e.getAuthenticationPhoneNumber()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setAuthenticationphonenumber(e.getAuthenticationPhoneNumber());
		}
		if (e.getSecurityQuestion() != null) {
			if(!e.getSecurityQuestion().equals(s.getSubscriber().getSecurityquestion())){
        		log.info("Subscriber MDN:"+ID+" Security Question updated to "+e.getSecurityQuestion()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setSecurityquestion(e.getSecurityQuestion());
		}

		if (e.getAuthenticationPhrase() != null) {
			if(!e.getAuthenticationPhrase().equals(s.getSubscriber().getSecurityanswer())){
        		log.info("Subscriber MDN:"+ID+" Security Answer updated to "+e.getAuthenticationPhrase()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setSecurityanswer(e.getAuthenticationPhrase());
		}
		if (e.getDigestedPIN() != null) {
			if(!e.getDigestedPIN().equals(s.getDigestedpin())){
        		log.info("Subscriber MDN:"+ID+" Digested Pin updated to "+e.getDigestedPIN()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setDigestedpin(e.getDigestedPIN());
		}
		if(e.getApplicationID() != null){
			if(!e.getApplicationID().equals(s.getApplicationid())){
        		log.info("Subscriber MDN:"+ID+" Application ID updated to "+e.getApplicationID()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setApplicationid(e.getApplicationID());
		}

		if (e.getH2HAllowedIP() != null) {
			if(!e.getH2HAllowedIP().equals(s.getH2hallowedip())){
        		log.info("Subscriber MDN:"+ID+" H2H Allowed IP updated to "+e.getH2HAllowedIP()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setH2hallowedip(e.getH2HAllowedIP());
		}
		if (e.getMDNRestrictions() != null) {
			if(!e.getMDNRestrictions().equals(s.getRestrictions())){
        		log.info("Subscriber MDN:"+ID+" MDN Restrictions updated to "+e.getMDNRestrictions()+" by user:"+getLoggedUserNameWithIP());
        		log.info("Subscriber :"+s.getSubscriber().getId()+" Restrictions updated to "+e.getMDNRestrictions()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setRestrictions(e.getMDNRestrictions());
			s.getSubscriber().setRestrictions(e.getMDNRestrictions());
		}
		if (e.getIsForceCloseRequested() != null && e.getIsForceCloseRequested().booleanValue()) {
			if(!e.getIsForceCloseRequested().equals(s.getIsforcecloserequested())){
        		log.info("Subscriber:"+ID+" Force Close Requested field is updated to " + userService.getCurrentUser().getUsername() + " by user:"+getLoggedUserNameWithIP());
        	}
			s.setIsforcecloserequested(e.getIsForceCloseRequested());
		}
		if (e.getStatus() != null) {
			if(!e.getStatus().equals(s.getStatus())){
        		log.info("Subscriber MDN:"+ID+" Status updated to "+e.getStatus()+" by user:"+getLoggedUserNameWithIP());
        		log.info("Subscriber :"+s.getSubscriber().getId()+" Status updated to "+e.getStatus()+" by user:"+getLoggedUserNameWithIP());
        	}
			// *FindbugsChange*
        	// Previous -- if (e.getStatus() != s.getStatus()) {
			if (!(e.getStatus().equals(s.getStatus()))) {
				s.setStatus(e.getStatus());
				s.setStatustime(new Timestamp());
				s.getSubscriber().setStatus(e.getStatus());
				s.getSubscriber().setStatustime(new Timestamp());
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(s.getSubscriber(),true);
			}
		}
		if (s.getStatustime() == null) {
			log.info("Subscriber MDN:"+ID+" Status Time updated by user:"+getLoggedUserNameWithIP());
			s.setStatustime(new Timestamp());
		}

		if (s.getSubscriber().getStatustime() == null) {
			log.info("Subscriber :"+s.getSubscriber().getId()+" Status Time updated by user:"+getLoggedUserNameWithIP());
			s.getSubscriber().setStatustime(new Timestamp());
		}

		ID = String.valueOf(s.getSubscriber().getId());
		// subscriber related fields
		if (e.getFirstName() != null) {
			if(!e.getFirstName().equals(s.getSubscriber().getFirstname())){
        		log.info("Subscriber:"+ID+" First name updated to "+e.getFirstName()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setFirstname(e.getFirstName());
		}
		if (e.getLastName() != null) {
			if(!e.getLastName().equals(s.getSubscriber().getLastname())){
        		log.info("Subscriber:"+ID+" Last name updated to "+e.getLastName()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setLastname(e.getLastName());
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
			s.getSubscriber().setIsemailverified(CmFinoFIX.Boolean_True);
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
			if(e.getActivationTime()!=(s.getSubscriber().getActivationtime())){
        		log.info("Subscriber:"+ID+" Language updated to "+e.getActivationTime()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setActivationtime(e.getActivationTime());

		}
		if (e.getMSPID() != null) {
			MfinoServiceProvider msp = mspDAO.getById(e.getMSPID());
        	if(s.getSubscriber().getMfinoServiceProvider()!=msp){
        		log.info("Subscriber:"+ID+" mFinoServiceProvider updated to "+msp.getId()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setMfinoServiceProvider(mspDAO.getById(e.getMSPID()));
		}
		if (e.getNotificationMethod() != null) {
			if(!e.getNotificationMethod().equals(s.getSubscriber().getNotificationmethod())){
        		log.info("Subscriber:"+ID+" Notification method updated to "+e.getNotificationMethod()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setNotificationmethod(e.getNotificationMethod());
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
			if(!e.getPartnerType().equals(s.getSubscriber().getPartnertype())){
        		log.info("Subscriber:"+ID+" Partner Type updated to "+e.getCurrency()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setPartnertype(e.getPartnerType());
		}
		//not null check is addded because update message can come for update and insert actions
		if (e.getCompanyID() != null) {
			// *FindbugsChange*
        	// Previous -- if(!e.getCompanyID().equals(s.getSubscriber().getCompany())){
			if((s.getSubscriber().getCompany() != null) && (!e.getCompanyID().equals(s.getSubscriber().getCompany().getId()))){
        		log.info("Subscriber:"+ID+" Company updated to "+e.getCompanyID()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setCompany(company);
		}
		if(e.getDateOfBirth()!=null){
			if(!e.getDateOfBirth().equals(s.getSubscriber().getDateofbirth())){
        		log.info("Subscriber:"+ID+" Date Of Birth updated to "+e.getDateOfBirth()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setDateofbirth(e.getDateOfBirth());
		}
		if(e.getIDType()!=null){
			if(!e.getIDType().equals(s.getIdtype())){
        		log.info("Subscriber:"+ID+" ID Type updated to "+e.getDateOfBirth()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setIdtype(e.getIDType());
		}
		if(e.getIDNumber()!=null){
			if(!e.getIDNumber().equals(s.getIdnumber())){
        		log.info("Subscriber:"+ID+" ID Number updated to "+e.getIDNumber()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setIdnumber(e.getIDNumber());
		}
		if(e.getExpirationTime()!=null){
			if(!e.getExpirationTime().equals(s.getSubscriber().getIdexiparetiontime())){
        		log.info("Subscriber:"+ID+" ID Expiration time updated to "+e.getIDNumber()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setIdexiparetiontime(e.getExpirationTime());
		}
		if(e.getBirthPlace()!=null){
			if(!e.getBirthPlace().equals(s.getSubscriber().getBirthplace())){
        		log.info("Subscriber:"+ID+" Birth Place updated to "+e.getIDNumber()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setBirthplace(e.getBirthPlace());
		}

		// s.getSubscriber().setIDExiparetionTime();
		if(e.getReferenceAccount()!=null){
			if(!e.getReferenceAccount().equals(s.getSubscriber().getReferenceaccount())){
        		log.info("Subscriber:"+ID+" Reference No updated to "+e.getReferenceAccount()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setReferenceaccount(e.getReferenceAccount());
		}
		if(e.getKYCLevel()!=null&& e.getKYCLevel().longValue() > s.getSubscriber().getKycLevel().getKyclevel().longValue()){
			if(!e.getKYCLevel().equals(s.getSubscriber().getUpgradablekyclevel())){
        		log.info("Subscriber:"+ID+" Upgradable KYC updated to "+e.getKYCLevel()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setUpgradablekyclevel(e.getKYCLevel());
			s.getSubscriber().setUpgradestate(CmFinoFIX.UpgradeState_Upgradable);
		}
		if(e.getKYCLevel()!=null){
			if(!e.getKYCLevel().equals(s.getSubscriber().getUpgradablekyclevel())){
        		log.info("Subscriber:"+ID+" Applied User Name is updated to " + userService.getCurrentUser().getUsername() + " by user:"+getLoggedUserNameWithIP());
        	}
			s.getSubscriber().setAppliedby(userService.getCurrentUser().getUsername());
			s.getSubscriber().setAppliedtime(new Timestamp());
		}

		// if (e.getDompetMerchant() != null) {
		// s.getSubscriber().setDompetMerchant(e.getDompetMerchant());
		// }
		
		if(e.getOtherMDN()!=null){
			if(!e.getOtherMDN().equals(s.getOthermdn())){
        		log.info("SubscriberMDN:"+ID+" Other MDN updated to "+e.getOtherMDN()+" by user:"+getLoggedUserNameWithIP());
        	}
			s.setOthermdn(e.getOtherMDN());
		}
		
		if((null != e.getGroupID()) && !("".equals(e.getGroupID()))){

			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
			List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(s.getSubscriber().getId());;
				if((subscriberGroups != null) && (subscriberGroups.size() > 0)){
					SubscriberGroups sg = subscriberGroups.iterator().next();
					if(sg.getGroupid() != Long.valueOf(e.getGroupID()).longValue()){
						Groups group = (Groups)groupDao.getById(Long.valueOf(e.getGroupID()));
						sg.setGroupid(group.getId().longValue());
						subscriberGroupDao.save(sg);
					}
				}
			
		else{
				Groups group = (Groups)groupDao.getById(Long.valueOf(e.getGroupID()));

				if(s.getSubscriber()!= null){
					SubscriberGroups sg = new SubscriberGroups();
					sg.setSubscriber(s.getSubscriber());
					sg.setGroupid(group.getId().longValue());
					subscriberGroupDao.save(sg);
				}
			}
		}
	}

	private void updateAdditionalFields(SubscriberAddiInfo saf, CMJSSubscriberMDN.CGEntries e) {
		if (e.getProofofAddress() != null) {
			saf.setProofofaddress(e.getProofofAddress());
		}
		if (e.getCreditCheck() != null) {
			saf.setCreditcheck(e.getCreditCheck());
		}
		if (e.getKinName() != null) {
			saf.setKinname(e.getKinName());
		}
		if (e.getKinMDN() != null) {
			saf.setKinmdn(e.getKinMDN());
		}
		if (e.getNationality() != null) {
			saf.setNationality(e.getNationality());
		}
		//if (e.getReferenceAccount() != null) {
			//    saf.setsu(e.getReferenceAccount());
			//}
		if (e.getCompanyName() != null) {
			saf.setSubscompanyname(e.getCompanyName());
		}
		if (e.getSubscriberMobileCompany() != null) {
			saf.setSubscribermobilecompany(e.getSubscriberMobileCompany());
		}
		if (e.getCertofIncorporation() != null){
			saf.setCertofincorporation(e.getCertofIncorporation());
		}
		if(e.getNationality()!=null){
			saf.setNationality(e.getNationality());
		}
		if(e.getIncome()!=null){
			saf.setIncome(e.getIncome());
		}
		if(e.getGoalOfAcctOpening()!=null){
			saf.setGoalofacctopening(e.getGoalOfAcctOpening());
		}
		if(e.getSourceOfFund()!=null){
			saf.setSourceoffund(e.getSourceOfFund());
		}
		if(e.getWork()!=null){
			saf.setWork(e.getWork());
		}
		if( e.getOtherWork()!=null){
			saf.setOtherwork(e.getOtherWork());
		}
	}
	private void updateAuthorizing( AuthPersonDetails ap, CMJSSubscriberMDN.CGEntries e) {
		if (e.getAuthoFirstName() != null) {
			ap.setFirstname(e.getAuthoFirstName());
		}
		if (e.getAuthoLastName()!=null){
			ap.setLastname(e.getAuthoLastName());
		}
		if (e.getAuthoDateofBirth()!=null){
			ap.setDateofbirth(e.getAuthoDateofBirth());

		}
		if (e.getAuthoIDDescription()!=null){
			ap.setIddesc(e.getAuthoIDDescription());

		}

		if (e.getAuthorizingPersonIDNumber()!=null){
			ap.setIdnumber(e.getAuthorizingPersonIDNumber());

		}
		if(ap.getMfinoServiceProvider()==null){
			ap.setMfinoServiceProvider(mspDAO.getById(1L));
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
			ads.setRegionname(e.getRegionName());
		}
		if(e.getRT()!=null){
			ads.setRt(e.getRT());
		}
		if(e.getRW()!=null){
			ads.setRw(e.getRW());
		}
		if(e.getState()!=null){
			ads.setState(e.getState());
		}
		if(e.getSubState()!=null){
			ads.setSubstate(e.getSubState());
		}
		if(e.getZipCode()!=null){
			ads.setZipcode(e.getZipCode());
		}
		
	}

	private void updateMessage(SubscriberMdn s, CMJSSubscriberMDN.CGEntries entry, SubscriberAddiInfo saf, AuthPersonDetails ap, Address ads, Address adsktp, Boolean isExcelDownload,String str_tomcatPath,KtpDetails ktpDetails, Boolean isNonRegisterActivation) {
		entry.setID(s.getId().longValue());
		
		if (s.getActivationtime() != null) {
			entry.setActivationTime(s.getActivationtime());
		}
		if (s.getAuthenticationphonenumber() != null) {
			try {
				entry.setAuthenticationPhoneNumber(new String(s.getAuthenticationphonenumber().getBytes("ASCII")));
			} catch (UnsupportedEncodingException ex) {
				log.error("Conversion of authentication number to ASCII failed ",ex);
			}
		}
		if (s.getSubscriber().getSecurityquestion() != null) {
			entry.setSecurityQuestion(s.getSubscriber().getSecurityquestion());
		}

		if (s.getSubscriber().getSecurityanswer() != null) {
			entry.setAuthenticationPhrase(s.getSubscriber().getSecurityanswer());
		}

		if (s.getCreatetime() != null) {
			entry.setCreateTime(s.getCreatetime());
		}

		if (s.getDigestedpin() != null) {
			entry.setDigestedPIN(s.getDigestedpin());
		}

		if (s.getH2hallowedip() != null) {
			entry.setH2HAllowedIP(s.getH2hallowedip());
		}

		if ( s.getLasttransactionid() != null) {
			entry.setLastTransactionID(s.getLasttransactionid());
		}

		if (s.getLasttransactiontime() != null) {
			entry.setLastTransactionTime(s.getLasttransactiontime());
		}

		if (s.getLastupdatetime() != null) {
			entry.setLastUpdateTime(s.getLastupdatetime());
		}
		if(!isExcelDownload) {
			if(s.getSubscriber().getUpgradablekyclevel()!=null&&
					(CmFinoFIX.UpgradeState_Upgradable.equals(s.getSubscriber().getUpgradestate())
							||CmFinoFIX.UpgradeState_Rejected.equals(s.getSubscriber().getUpgradestate()))){
				KycLevel kycLevel=kyclevelDao.getByKycLevel(s.getSubscriber().getUpgradablekyclevel().longValue());
				entry.setUpgradableKYCLevelText(kycLevel.getKyclevelname());
				entry.setKYCLevel(kycLevel.getKyclevel().longValue());
				entry.setKYCLevelText(s.getSubscriber().getKycLevel().getKyclevelname());
			}else if (s.getSubscriber().getKycLevel()!=null){
				entry.setKYCLevel(s.getSubscriber().getKycLevel().getKyclevel().longValue());
				entry.setKYCLevelText(s.getSubscriber().getKycLevel().getKyclevelname());
			}
		}
		if (s.getMdn() != null) {
			entry.setMDN(s.getMdn());
		}
		if ( s.getApplicationid()!= null){
			entry.setApplicationID(s.getApplicationid());
		}
		
		if ( s.getKtpid()!= null){
			entry.setKTPID(s.getKtpid());
		}

		entry.setMDNRestrictions(s.getRestrictions());
		entry.setStatus(s.getStatus());

		if (s.getUpdatedby() != null) {
			entry.setUpdatedBy(s.getUpdatedby());
		}
		if (s.getCreatedby() != null) {
			entry.setCreatedBy(s.getCreatedby());
		}

		if (s.getStatustime() != null) {
			entry.setStatusTime(s.getStatustime());
		}


		// a mdn will always have a subscriber
		if (s.getSubscriber() != null) {
			entry.setSubscriberID(s.getSubscriber().getId());
		}

		entry.setWrongPINCount(s.getWrongpincount());

		// subscriber realted fields
		if (s.getSubscriber().getFirstname() != null) {
			entry.setFirstName(s.getSubscriber().getFirstname());
		}

		if (s.getSubscriber().getLastname() != null) {
			entry.setLastName(s.getSubscriber().getLastname());
		}
		
		if (s.getSubscriber().getNickname() != null) {
			entry.setNickname(s.getSubscriber().getNickname());
		}
		if(s.getKtpdocumentpath()!=null){
			entry.setKTPDocumentPath(str_tomcatPath+"/"+s.getKtpdocumentpath());
		}
		
		if(s.getSubscriberformpath()!=null){
			entry.setSubscriberFormPath(str_tomcatPath+"/"+s.getSubscriberformpath());
		}
		
		if(s.getSupportingdocumentpath()!=null){
			entry.setSupportingDocumentPath(str_tomcatPath+"/"+s.getSupportingdocumentpath());
		}

		if (s.getSubscriber().getEmail() != null) {
			entry.setEmail(s.getSubscriber().getEmail());
		}



		entry.setLanguage(Integer.valueOf(Long.valueOf(s.getSubscriber().getLanguage()).intValue()));

		entry.setMSPID(s.getSubscriber().getMfinoServiceProvider().getId().longValue());

		if (s.getSubscriber().getNotificationmethod() != null) {
			entry.setNotificationMethod(s.getSubscriber().getNotificationmethod().intValue());
		}

		if (s.getSubscriber().getEmail() != null) {
			entry.setEmail(s.getSubscriber().getEmail());
		}

		entry.setSubscriberRestrictions(Integer.valueOf(Long.valueOf(s.getSubscriber().getRestrictions()).intValue()));
		
		if(s.getOthermdn() != null){
			entry.setOtherMDN(s.getOthermdn());
		}

		// @XC, Teja: Do we still need this?
				if (s.getSubscriber().getStatus() != 0) {
					entry.setSubscriberStatus(Integer.valueOf(Long.valueOf(s.getStatus()).intValue()));
				}

				// if (s.getSubscriber().getUpdatedBy() != null) {
				// entry.setUpdatedBy(s.getSubscriber().getUpdatedBy());
				// }

				entry.setSubscriberType(Integer.valueOf(Long.valueOf(s.getSubscriber().getType()).intValue()));

				if (s.getSubscriber().getTimezone() != null) {
					entry.setTimezone(s.getSubscriber().getTimezone());
				}
				
				Integer languageI = Integer.valueOf(Long.valueOf(s.getSubscriber().getLanguage()).intValue());

				entry.setCurrency(s.getSubscriber().getCurrency());
				entry.setSubscriberTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberType, languageI, s.getSubscriber().getType()));
				entry.setLanguageText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, languageI, s.getSubscriber().getLanguage()));
				entry.setSubscriberStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, languageI, s.getStatus()));
//				entry.setPartnerTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerType, s.getSubscriber().getLanguage(), s.getSubscriber().getPartnerType()));
				entry.setMDNRestrictionsText(enumTextService.getRestrictionsText(CmFinoFIX.TagID_SubscriberRestrictions, languageI, Long.valueOf(s.getRestrictions()).toString()));
				entry.setDoGeneratePin(false);

				if (s.getVersion() != 0) {
					entry.setRecordVersion(Integer.valueOf(Long.valueOf(s.getVersion()).intValue()));
				}
				/* used in 1.9
				 * if (s.getSubscriber().getPartnerType() != null) {
					entry.setPartnerType(s.getSubscriber().getPartnerType());
				}*/
				if (CmFinoFIX.SubscriberType_Partner.equals(s.getSubscriber().getType())) {
					Set<Partner> partners = s.getSubscriber().getPartners();
					if (partners != null && !partners.isEmpty()) {
						entry.setPartnerType(partners.iterator().next().getBusinesspartnertype().intValue());
						if (partnerService.isAgentType(entry.getPartnerType()))
							entry.setPartnerType(CmFinoFIX.TagID_BusinessPartnerTypeAgent);
					}
		
				}
				
				if(!isExcelDownload && !isNonRegisterActivation) {
					Pocket p = subscriberService.getDefaultPocket(s.getId().longValue(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
					entry.setDompetMerchant(Boolean.FALSE);
					if (p != null) {
						if ((Long.valueOf(p.getPocketTemplateByPockettemplateid().getAllowance()).intValue() & CmFinoFIX.PocketAllowance_MerchantDompet.intValue()) > 0) {
							entry.setDompetMerchant(Boolean.TRUE);
						}
						/*if( p.getCardpan() != null ) {
							entry.setAccountNumber(p.getCardpan());
						}*/
					}
				}
				if(s.getIdtype()!=null){
					entry.setIDType(s.getIdtype());
				}
				if(s.getIdnumber()!=null){
					entry.setIDNumber(s.getIdnumber());
				}
				if(s.getSubscriber().getIdexiparetiontime()!=null){
					entry.setExpirationTime(s.getSubscriber().getIdexiparetiontime());
				}
				if(s.getSubscriber().getDateofbirth()!=null){
					entry.setDateOfBirth(s.getSubscriber().getDateofbirth());
					entry.setDateOfBirthText(s.getSubscriber().getDateofbirth().toString());
				}
				if(s.getSubscriber().getBirthplace()!=null){
					entry.setBirthPlace(s.getSubscriber().getBirthplace());
				}
				if(s.getSubscriber().getReferenceaccount()!=null){
					entry.setReferenceAccount(s.getSubscriber().getReferenceaccount().longValue());
				}
				if(s.getSubscriber().getUpgradestate()!=null){
					entry.setUpgradeState(s.getSubscriber().getUpgradestate().intValue());
					entry.setUpgradeStateText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_UpgradeState, CmFinoFIX.Language_English, s.getSubscriber().getUpgradestate()));
				}
				if(!CmFinoFIX.UpgradeState_Upgradable.equals(s.getSubscriber().getUpgradestate())){
					if(s.getSubscriber().getApprovedorrejectedby()!=null){
						entry.setApprovedOrRejectedBy(s.getSubscriber().getApprovedorrejectedby());
					}
					if(s.getSubscriber().getApproveorrejecttime()!=null){
						entry.setApproveOrRejectTime(s.getSubscriber().getApproveorrejecttime());
					}
					if(s.getSubscriber().getApproveorrejectcomment()!=null){
						entry.setApproveOrRejectComment(s.getSubscriber().getApproveorrejectcomment());
					}
				}
				if(s.getSubscriber().getAppliedby()!=null){
					entry.setAppliedBy(s.getSubscriber().getAppliedby());
				}
				if(s.getSubscriber().getAppliedtime()!=null){
					entry.setAppliedTime(s.getSubscriber().getAppliedtime());
				}
				if(s.getSubscriber().getDetailsrequired()!=null && s.getSubscriber().getDetailsrequired()){
					entry.setDetailsRequired(true);
				}else{
					entry.setDetailsRequired(false);
				}
				SubscriberUpgradeData subscriberUpgradeData=subscriberUpgradeDataDAO.getUpgradeDataByMdnId(s.getId());
				if(subscriberUpgradeData!=null){
					    entry.setSubscriberActivityText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberActivity, languageI, subscriberUpgradeData.getSubActivity()));
						
				}
				if(saf!=null){
					if (saf.getProofofaddress() != null) {
						entry.setProofofAddress(saf.getProofofaddress());
					}
					if (saf.getCreditcheck() != null) {
						entry.setCreditCheck(saf.getCreditcheck());
					}
					if (saf.getKinname() != null) {
						entry.setKinName(saf.getKinname());
					}
					if (saf.getKinmdn() != null) {
						entry.setKinMDN(saf.getKinmdn());
					}
					if (saf.getNationality() != null) {
						entry.setNationality(saf.getNationality());
					}
					//if (saf.getReferenceAccount() != null) {
						//    entry.setsu(saf.getReferenceAccount());
						//}
					if (saf.getSubscompanyname() != null) {
						entry.setCompanyName(saf.getSubscompanyname());
					}
					if (saf.getSubscribermobilecompany() != null) {
						entry.setSubscriberMobileCompany(saf.getSubscribermobilecompany());
					}
					if (saf.getCertofincorporation() != null){
						entry.setCertofIncorporation(saf.getCertofincorporation());
					}
					if(saf.getNationality()!=null){
						entry.setNationality(saf.getNationality());
					}
					if(saf.getOtherwork()!=null){
						entry.setOtherWork(saf.getOtherwork());
					}
					
				}
				if(ap!=null){
					if (ap.getFirstname() != null) {
						entry.setAuthoFirstName(ap.getFirstname());
					}
					if (ap.getLastname()!=null){
						entry.setAuthoLastName(ap.getLastname());
					}
					if (ap.getDateofbirth()!=null){
						entry.setAuthoDateofBirth(ap.getDateofbirth());

					}
					if (ap.getIddesc()!=null){
						entry.setAuthoIDDescription(ap.getIddesc());

					}
					if (ap.getIdnumber()!=null){
						entry.setAuthorizingPersonIDNumber(ap.getIdnumber());

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
						entry.setState(ads.getState());
					}
					if (ads.getCountry()!=null){
						entry.setCountry(ads.getCountry());
					}
					if (ads.getRegionname()!=null){
						entry.setRegionName(ads.getRegionname());
					}
					if(ads.getSubstate()!=null){
						entry.setSubState(ads.getSubstate());
					}
					if(ads.getRt()!=null){
						entry.setRT(ads.getRt());
					}
					if(ads.getRw()!=null){
						entry.setRW(ads.getRw());
					}
					if(ads.getZipcode()!=null){
						entry.setZipCode(ads.getZipcode());
					}
				}
				
				if(adsktp!=null){
					if (adsktp.getLine1() != null) {
//						entry.setPlotNo(adsktp.getLine1());
						entry.setKTPPlotNo(adsktp.getLine1());
					}
					if(adsktp.getLine2()!=null){
//						entry.setStreetAddress(adsktp.getLine2());
					}
					if (adsktp.getCity()!=null){
//						entry.setCity(adsktp.getCity());
						entry.setKTPCity(adsktp.getCity());
					}
					if(adsktp.getState()!=null){
//						entry.setStreetAddress(adsktp.getState());
						entry.setKTPState(adsktp.getState());
					}
					if (adsktp.getSubstate()!=null){
//						entry.setSubState(adsktp.getSubState());
						entry.setKTPSubState(adsktp.getSubstate());
					}
					if (adsktp.getRegionname()!=null){
//						entry.setRegionName(adsktp.getRegionName());
						entry.setKTPRegionName(adsktp.getRegionname());
					}
					if (adsktp.getZipcode()!=null){
//						entry.setZipCode(adsktp.getZipCode());
						entry.setKTPZipCode(adsktp.getZipcode());
					}
					if (adsktp.getRt()!=null){
//						entry.setRT(adsktp.getRT());
						entry.setKTPRT(adsktp.getRt());
					}
					if (adsktp.getRw()!=null){
//						entry.setRW(adsktp.getRW());
						entry.setKTPRW(adsktp.getRw());
					}
				}

				if(s.getDomaddridentity()!=null && Integer.parseInt(s.getDomaddridentity()) == CmFinoFIX.DomAddrIdentity_According_to_Identity.intValue()){
					
					entry.setIsDomesticAddrIdentity(true);
				}else{
					entry.setIsDomesticAddrIdentity(false);
				}
				SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
				List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(s.getSubscriber().getId());
				if((subscriberGroups != null) && (subscriberGroups.size() > 0)) {
					SubscriberGroups sg = subscriberGroups.iterator().next();
					GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
					Groups groups = groupDao.getById(sg.getGroupid());
					entry.setGroupName(groups.getGroupname());
					entry.setGroupID(""+sg.getGroupid());
				}
				
				if(s.getSubscriber().getRegistrationmedium()!=null){
					entry.setRegistrationMedium(s.getSubscriber().getRegistrationmedium().intValue());
				}
				if(s.getUpgradeacctstatus()!=null){
					int upgradeSttus=s.getUpgradeacctstatus().intValue();
					entry.setUpgradeAcctStatus(upgradeSttus);
					if(upgradeSttus==CmFinoFIX.SubscriberUpgradeStatus_Initialized.intValue()){
						entry.setUpgradeAcctStatusText("Initialized");	
					}else if(upgradeSttus==CmFinoFIX.SubscriberUpgradeStatus_Approve.intValue()){
						entry.setUpgradeAcctStatusText("Approved");	
					}else if(upgradeSttus==CmFinoFIX.SubscriberUpgradeStatus_Reject.intValue()){
						entry.setUpgradeAcctStatusText("Rejected");	
					}else if(upgradeSttus == CmFinoFIX.SubscriberUpgradeKycStatus_Revision.intValue()){
						entry.setUpgradeAcctStatusText("Revision");
					}
				}
				if(s.getUpgradeacctapprovedby()!=null){
					entry.setUpgradeAcctApprovedBy(s.getUpgradeacctapprovedby());
				}
				if(s.getUpgradeaccttime()!=null){
					entry.setUpgradeAcctTime(s.getUpgradeaccttime());
				}
				if(s.getUpgradeacctcomments()!=null){
					entry.setUpgradeAcctComments(s.getUpgradeacctcomments());
				}
				if(s.getIsidlifetime()!=null){
					if(s.getIsidlifetime().equals(CmFinoFIX.ISIDLifetime_LifeTime_True)){
						entry.setIsIdLifetimeText(Boolean.TRUE.toString());
					}else{
						entry.setIsIdLifetimeText(Boolean.FALSE.toString());
					}
				}
				if(s.getSubscriber().getIdexiparetiontime()!=null){
					entry.setIDValidUntil(s.getSubscriber().getIdexiparetiontime());
					entry.setIDValidUntilText(s.getSubscriber().getIdexiparetiontime().toString());
				}
				if(s.getSubscriber().getMothersmaidenname()!=null){
					entry.setMothersMaidenName(s.getSubscriber().getMothersmaidenname());
				}
				if(s.getSubscriber().getRegisteringpartnerid()!=null){
					Partner partner = partnerService.getPartnerById(s.getSubscriber().getRegisteringpartnerid().longValue());
					if(partner != null){
						if(partner.getBranchcode() != null){
							entry.setUserBankBranch(userService.getUserBranchCode(Integer.valueOf(partner.getBranchcode())));
						}
						if(partner.getPartnercode() != null){
							entry.setAgentCode(partner.getPartnercode());
						}
						if(partner.getTradename() != null){
							entry.setAgentName(partner.getTradename());
						}
					}
				}
				
				
				if(ktpDetails!=null && ktpDetails.getDateofbirth()!=null){
					entry.setKTPDateOfBirth(ktpDetails.getDateofbirth());
				}
				Set<SubscriberAddiInfo> subscriberAdditionalFiedlsSet=s.getSubscriber().getSubscriberAddiInfos();
				SubscriberAddiInfo subscribersAdditionalFields=null;
				if(subscriberAdditionalFiedlsSet!=null && subscriberAdditionalFiedlsSet.size()>0){
					Iterator it_subAddfields=subscriberAdditionalFiedlsSet.iterator();
					
					while(it_subAddfields.hasNext()){
						subscribersAdditionalFields=(SubscriberAddiInfo)it_subAddfields.next();
					}
				}
				if(subscribersAdditionalFields!=null){
					
					if(subscribersAdditionalFields.getWork()!=null){
						entry.setWork(subscribersAdditionalFields.getWork());
					}
					if(subscribersAdditionalFields.getIncome()!=null){
						entry.setIncome(subscribersAdditionalFields.getIncome());
					}
					if(subscribersAdditionalFields.getSourceoffund()!=null){
						entry.setSourceOfFund(subscribersAdditionalFields.getSourceoffund());
					}
					if(subscribersAdditionalFields.getGoalofacctopening()!=null){
						entry.setGoalOfAcctOpening(subscribersAdditionalFields.getGoalofacctopening());
					}
					
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
				SubscriberMdn s = mdnDao.getById(e.getID());
				Subscriber sub = s.getSubscriber();
				log.info("SubscriberMDN:"+s.getId()+" details edit requested by user:"+getLoggedUserNameWithIP());
				
				boolean isLakuapandiaSubscriber = false;
				Set<Pocket> subPockets = s.getPockets();
				for (Iterator iterator = subPockets.iterator(); iterator.hasNext();) {
					Pocket pocket = (Pocket) iterator.next();
					if(pocket.getPocketTemplateByPockettemplateid().getType().equals(CmFinoFIX.PocketType_LakuPandai)) {
						isLakuapandiaSubscriber = true;
					}
				}

				Integer oldRestrictions = s.getRestrictions();
				if (e.getRecordVersion() != null && !e.getRecordVersion().equals(s.getVersion())){
					log.warn("SubscriberMDN:"+s.getId()+" Stale Data Exception for user:"+getLoggedUserNameWithIP());
					handleStaleDataException();
				}
				
				//setting the company code before calling update entity
				//cal company code based on the MDN if mdn is edited
				if (e.getMDN() != null && e.getMDN().length() > 0) {
					String mNumber = e.getMDN();
					validateSubscriberMDN(mNumber);
					company = subscriberService.getCompanyFromMDN(mNumber);
					if (company != null && company.getId() != userService.getUserCompany().getId()) {
						errorMsg.setErrorDescription(MessageText._("Cannot Edit MDN of other Brands"));
						errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
						log.warn(getLoggedUserNameWithIP() + " entered " + e.getMDN() + ", not allowed to edit MDN of other Brands");
						return errorMsg;
					} else if (company == null) {
						// return failure message saying invalid mdn
						errorMsg.setErrorDescription(MessageText._("Invalid MDN"));
						log.warn("SubscriberMDN:"+s.getId()+" entered by user " + getLoggedUserNameWithIP() +" does not match to any company code ");
						return errorMsg;
					}
				}

				if(CmFinoFIX.SubscriberStatus_Active.equals(e.getStatus()) && !isActivationAllowed(sub,e)){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Subscriber Activation not allowed."));
					log.warn("SubscriberMDN:"+s.getId() + "Subscriber Activation not allowed for user:"+ getLoggedUserNameWithIP());
					return error;
				}
				
				if(CmFinoFIX.SubscriberStatus_InActive.equals(e.getStatus())){
					if(!CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(e.getMDNRestrictions())){
						CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
						error.setErrorDescription(MessageText._("Subscriber InActivation not allowed."));
						log.warn("SubscriberMDN:"+s.getId() + "Subscriber InActivation not allowed for user:"+ getLoggedUserNameWithIP());
						return error;
					}
				}
				
				if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(e.getStatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Changing Subscriber status to 'Not Registered' is not allowed."));
					log.warn("SubscriberMDN:"+s.getId() + "Changing Subscriber status to 'Not Registered' is not allowed for user:"+ getLoggedUserNameWithIP());
					return error;
				}
				
				if(CmFinoFIX.SubscriberStatus_Initialized.equals(e.getStatus()) && 
						!( CmFinoFIX.SubscriberStatus_Initialized.equals(sub.getStatus()) 
								|| CmFinoFIX.SubscriberStatus_Suspend.equals(sub.getStatus()) 
								|| CmFinoFIX.SubscriberStatus_InActive.equals(sub.getStatus())
								||CmFinoFIX.SubscriberStatus_NotRegistered.equals(sub.getStatus())) 
								){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Intializing subscriber not allowed."));
					log.warn("SubscriberMDN:"+s.getId() + "Initializing subscriber not allowed for "+ getLoggedUserNameWithIP());
					return error;
				}
				
				if(CmFinoFIX.SubscriberStatus_Suspend.equals(e.getStatus()) && !CmFinoFIX.SubscriberStatus_Suspend.equals(sub.getStatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Suspending of subscriber not allowed."));
					log.warn("SubscriberMDN:"+s.getId() + "Suspending of subscriber not allowed for "+ getLoggedUserNameWithIP());
					return error;
				}
				
				if(e.getStatus() != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(e.getStatus()) &&
						CmFinoFIX.SubscriberStatus_Suspend.equals(sub.getStatus())){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Suspended subscriber can be moved to Intialized status only"));
					log.warn("SubscriberMDN:"+s.getId() + "Suspended subscriber can be moved to Intialized status only for "+ getLoggedUserNameWithIP());
					return error;
				}
				
				if(e.getStatus() != null && !CmFinoFIX.SubscriberStatus_Initialized.equals(e.getStatus()) &&
						CmFinoFIX.SubscriberStatus_InActive.equals(sub.getStatus()) && !isActivationAllowed(sub,e)){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorDescription(MessageText._("Inactive subscriber can be moved to Intialized status only"));
					log.warn("SubscriberMDN:"+s.getId() + "Suspended subscriber can be moved to Intialized status only for "+ getLoggedUserNameWithIP());
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
							log.warn("SubscriberMDN:"+s.getId() + "Subscriber status is eigther retired or pending retirement for "+ getLoggedUserNameWithIP());
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
						log.warn("SubscriberMDN:"+s.getId() + " Merchant MDN suspended to prevent transactions for user "+ getLoggedUserNameWithIP());
						return error;
					}
				}

				if (e.getMDN() != null) {
					//Need to take care of MDNs which are tagged with R
					int findR = e.getMDN().indexOf('R');
					if (findR == -1) {
						//chk whether he is a merchant if yes try to see the mdnrange is valid or not
						Merchant merchant = s.getSubscriber().getMerchant();
						
						if (merchant != null) {
							log.info("Checking MDN range is valid for merchant: " + merchant.getId());
							if (merchant.getMerchant() != null) {
								Merchant merchantparent = merchant.getMerchant();
								if (!mdnRangeService.isMDNInParentsRange(Long.parseLong(e.getMDN().substring(2)), merchantparent)) {
									CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
									error.setErrorDescription(MessageText._("Subscriber updation failed. This subscriber is a merchant, and his MDN should lie inside his parent's range."));
									log.warn("Subscriber updation failed for merchant " + merchant.getId()+" as his his MDN does not lie in his parent's range");
									return error;
								} else {
									//since merchant is now in the range reset his range check
									merchant.setRangecheck(merchant.getRangecheck() == null ? null : merchant.getRangecheck() & CmFinoFIX.RangeCheck_MDNRangeNotInParentsRange);
									MerchantDAO merchantDao = DAOFactory.getInstance().getMerchantDAO();
									merchantDao.save(merchant);
									log.info("merchant range check is reset for " + merchant.getId());
								}
							}
						}
					}
				}
				
				if (CmFinoFIX.UpgradeState_Upgradable.equals(sub.getUpgradestate())
						&& e.getKYCLevel() != null && (!e.getKYCLevel().equals(sub.getUpgradablekyclevel()))){
					errorMsg.setErrorDescription(MessageText._("Subscriber approval pending so kyclevel edit not allowed"));
					log.info("Subscriber " + sub.getId() +" approval is pending so kyc level edit not allowed");
					return errorMsg;
					
				} else if(e.getKYCLevel() != null && sub.getKycLevel().getKyclevel().longValue() > e.getKYCLevel().longValue()){
					errorMsg.setErrorDescription(MessageText._("Degrade of kyclevel not allowed"));
					log.warn("kyc level can not be degraded for " + sub.getId());
					return errorMsg;
				}
							
				Set<SubscriberAddiInfo> saf2 = sub.getSubscriberAddiInfos();
				SubscriberAddiInfo saf;
				if (saf2.isEmpty()) {
					if (checkAdditionalFields(e)){
						saf = new SubscriberAddiInfo();
					} else{
						saf = null;
					}
				} else {
					saf = saf2.iterator().next();
				}

				Address ads = sub.getAddressBySubscriberaddressid();
				if (ads == null && checkAddress(e)){
					ads = new Address();
				}
				if(e.getIsDomesticAddrIdentity() != null ){
					if(e.getIsDomesticAddrIdentity()){
						s.setDomaddridentity(Integer.toString(1));
					}else{
						ads = new Address();
						s.setDomaddridentity(Integer.toString(2));
					}
				}
				ktpDetailsQuery.setMdn(s.getMdn());
				ktpDetailsQuery.setOrder("desc");
				
				List<KtpDetails> ktpDetailsList = ktpDetailsDAO.getByMDN(ktpDetailsQuery);
				KtpDetails ktpDetails = null;
				
				if(ktpDetailsList != null && ktpDetailsList.size() > 0){
					 ktpDetails=ktpDetailsList.get(0);
				}
				
				if(e.getKTPDateOfBirth()!=null){
					ktpDetails.setDateofbirth(e.getKTPDateOfBirth());
				}
/*				Address adsktp=s.getSubscriber().getAddressBySubscriberAddressKTPID();
				if (adsktp == null && checkAddress(e)){
					adsktp = new Address();
				}*/
				
				AuthPersonDetails ap= sub.getAuthPersonDetails();
				if (ap == null && checkAuthorizingPersonDetails(e)){
					ap = new AuthPersonDetails();
				}
				
				//check on kyc level need to be added on edit to lower kycleve
				if(sub.getDetailsrequired() != null && sub.getDetailsrequired()){
					sub.setDetailsrequired(CmFinoFIX.Boolean_True);
				}
				
				boolean isNonRegisterActivation = false;
				if(sub.getStatus().equals(CmFinoFIX.SubscriberStatus_NotRegistered) 
						&& e.getStatus().equals(CmFinoFIX.SubscriberStatus_Initialized)){
					
					updateUnregisteredSubsPocket(s);
					
					KycLevel fullyBankedLevel = kyclevelDao.getByKycLevel(CmFinoFIX.SubscriberKYCLevel_FullyBanked.longValue());
					sub.setKycLevel(fullyBankedLevel);
					
					UnRegisteredTxnInfoQuery query = new UnRegisteredTxnInfoQuery();
					query.setSubscriberMDNID(sub.getId());
					UnRegisteredTxnInfoDAO unregisteredDao = DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
					List<UnregisteredTxnInfo> unregisteredSubscriber = unregisteredDao.get(query);
					if(unregisteredSubscriber != null && unregisteredSubscriber.size() > 0) {
						for (UnregisteredTxnInfo txnInfo : unregisteredSubscriber) {
							txnInfo.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_SUBSCRIBER_ACTIVE);					
							unregisteredDao.save(txnInfo);
						}
					}
					isNonRegisterActivation = true;
					sub.setUpgradestate(CmFinoFIX.UpgradeState_Upgradable);
				}
				
				updateEntity(s, e);

				if(ap!=null){
					updateAuthorizing(ap, e);
					authorizingPersonDAO.save(ap);
					log.info("authorizing person updated for " + e.getID());
					sub.setAuthPersonDetails(ap);
				}
				
				if(ads!=null){
					updateAddress(ads, e);
					addressDAO.save(ads);
					log.info("Address updated for " + e.getID());
					
					if(null != s.getDomaddridentity()) {
						
						if(Integer.parseInt(s.getDomaddridentity())==CmFinoFIX.DomAddrIdentity_According_to_Identity.intValue()){
							sub.setAddressBySubscriberaddressid(sub.getAddressBySubscriberaddressktpid());
						}else{
							sub.setAddressBySubscriberaddressid(ads);	
						}
					}
				}
				
				//Generate OTP for the subscriber if the status is changed from Suspend to Initialise or Inactive to Initialise.
				if(e.getStatus() != null && CmFinoFIX.SubscriberStatus_Initialized.equals(e.getStatus()) && !isNonRegisterActivation){
					sendOTPOnIntialized = ConfigurationUtil.getSendOTPOnIntialized();
					if(sendOTPOnIntialized){
						generateAndSendOTP(mdnDao, s, CmFinoFIX.NotificationCode_New_OTP_Success);
						log.info("new OTP is generated for the subscriber" + s.getId() + "as the status is changed from Suspend to Initialise or Inactive to Initialise");
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
				log.info("updated subscriber: " + sub.getId());
				if(e.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
					mailService.generateEmailVerificationMail(sub, e.getEmail());
				}				
				if(saf!=null){
					updateAdditionalFields(saf, e);
					saf.setSubscriber(sub);
					log.info("additional fields are updated for subscriber: " + sub.getId());
					subscribersadditionalfieldsDao.save(saf);
				}
				
				mdnDao.save(s);
				
				if(isLakuapandiaSubscriber) {
				
					ktpDetailsDAO.save(ktpDetails);
				}
				
				updateMessage(s, e, saf, ap, sub.getAddressBySubscriberaddressid(),sub.getAddressBySubscriberaddressktpid(),false,str_tomcatPath,ktpDetails, isNonRegisterActivation);

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
					Pocket existingBankPocket = subscriberService.getDefaultPocket(s.getMdn(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
					PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
					if(existingBankPocket != null)
					{
						log.info("Bank pocket already exists for subscriber with mdn " + s.getMdn());
						String existingAccountNo = existingBankPocket.getCardpan();
						if(!existingAccountNo.equals(e.getAccountNumber()))
						{
							log.info("Updating the old bank a/c no " + existingAccountNo + " with the new a/c no "+e.getAccountNumber() + "for subscriber with mdn "+ s.getMdn());
							existingBankPocket.setCardpan(e.getAccountNumber());
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
								log.error("PocketProcessor :: Pocket count limit reached for template:"+pocketTemplate.getDescription()+" for MDN:"+ s.getMdn() + " by user:"+getLoggedUserNameWithIP());	
								return getErrorMessage(MessageText._(" Pocket count Limit reached for this template  "), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Pocket count Limit reached for this template"));  		
							}
							
							Pocket bankPocket = pocketService.createPocket(pocketTemplate, s, CmFinoFIX.PocketStatus_Active, true, e.getAccountNumber());
							if(bankPocket == null){								
								errorMsg.setErrorDescription(MessageText._("Default Bank Pocket creation failed for the Subscriber with the selected ptc:" + ptc.getId()));
								errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
								log.info("Default Bank Pocket creation failed for Subscriber "+s.getId());
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
			
			if(null != realMsg.getKYCFieldsLevelID()) {
				
				query.setKycLevelId(realMsg.getKYCFieldsLevelID());
			}
			
			if(realMsg.getUpgradeKycStatusSearch() != null && 
					!CmFinoFIX.UpgradeKycStatusSearch_All.equals(realMsg.getUpgradeKycStatusSearch())){
				query.setAccountUpgradeKycStatus(realMsg.getUpgradeKycStatusSearch());
			}
			
			if (authorizationService.isAuthorized(CmFinoFIX.Permission_Transaction_OnlyBank_View)) {
				MfinoUser user = userService.getCurrentUser();
				Set<BankAdmin> admins = user.getBankAdmins();

				if (admins != null && admins.size() > 0) {
					BankAdmin admin = (BankAdmin) admins.toArray()[0];
					if (admin != null && admin.getBank() != null) {
						query.setBankCode(admin.getBank().getBankcode().intValue());
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
			List<SubscriberMdn> results = mdnDao.get(query);

			realMsg.allocateEntries(results.size());

			for (int i = 0; i < results.size(); i++) {
				SubscriberMdn s = results.get(i);
				CMJSSubscriberMDN.CGEntries entry = new CMJSSubscriberMDN.CGEntries();
				SubscriberAddiInfo saf=null;
				Address ads=s.getSubscriber().getAddressBySubscriberaddressid();
				Address adsktp=s.getSubscriber().getAddressBySubscriberaddressktpid();
				AuthPersonDetails ap=s.getSubscriber().getAuthPersonDetails();
				if(! s.getSubscriber().getSubscriberAddiInfos().isEmpty()){
					saf=s.getSubscriber().getSubscriberAddiInfos().iterator().next();
				}
				ktpDetailsQuery.setMdn(s.getMdn());
				ktpDetailsQuery.setOrder("desc");
				
				List<KtpDetails> ktpDetailsList=ktpDetailsDAO.getByMDN(ktpDetailsQuery);
				KtpDetails ktpDetails=null;
				if(ktpDetailsList!=null && ktpDetailsList.size()>0){
					 ktpDetails=ktpDetailsList.get(0);
				}
				updateMessage(s, entry, saf, ap, ads,adsktp,realMsg.getIsExcelDownload(),str_tomcatPath,ktpDetails, false);
				realMsg.getEntries()[i] = entry;
				log.info("Subscriber:"+s.getId()+" details viewed completed by user:"+getLoggedUserNameWithIP());
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
				SubscriberMdn mdn = new SubscriberMdn();
				Subscriber s = new Subscriber();
				SubscriberAddiInfo saf = new SubscriberAddiInfo();
				AuthPersonDetails ap = new AuthPersonDetails();
				Address ads = new Address();
				KycLevel kyclevel = null;
				String bankAccountNumber = null;
				
				String cardPan=e.getAccountNumber();
				if(StringUtils.isNotBlank(cardPan)) {
					validateAccountNumber(cardPan);
				}
				mdn.setLastapppinchange(new Timestamp());
				mdn.setSubscriber(s);
				e.setStatus(CmFinoFIX.MDNStatus_Initialized);
				e.setSubscriberType(CmFinoFIX.SubscriberType_Subscriber);
				//setting the company code before calling update entity
				//cal company code based on the MDN
				String mNumber = subscriberService.normalizeMDN(e.getMDN());
				validateSubscriberMDN(mNumber);
				company = subscriberService.getCompanyFromMDN(mNumber);
				if (company != null && company.getId() == userService.getUserCompany().getId()) {
					e.setCompanyID(company.getId().longValue());
				} else if (company != null && company.getId() != userService.getUserCompany().getId()) {
					CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
					errorMsg.setErrorDescription(MessageText._("Cannot Add MDN of other Brands"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					log.warn("Subscriber " + s.getId() + "can not add MDN for other brands");
					return errorMsg;
				} else if (company == null) {
					// return failure message saying invalid mdn
					CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
					errorMsg.setErrorDescription(MessageText._("Invalid MDN"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					log.warn("For This MDN " + s.getId() + "Company is null");
					return errorMsg;
				}
				if(e.getKYCLevel()!=null){
					kyclevel = kyclevelDao.getByKycLevel(ConfigurationUtil.getIntialKyclevel());
					if(kyclevel==null){
						CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
						errorMsg.setErrorDescription(MessageText._("Intial Kyclevel not available"));
						errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
						log.warn("Initial KYC level not available for " + s.getId());
						return errorMsg;
					}
					s.setKycLevel(kyclevel);
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
						log.warn("Failed due to invalid Bank Account Number for:" + mdn.getMdn());
						return errorMsg;
					}
				}
				
				log.info("SubscriberMDN:"+mdn.getId()+" created by user:"+getLoggedUserNameWithIP());

				boolean addressFlag = checkAddress(e);
				if(addressFlag){
					updateAddress(ads, e);
					s.setAddressBySubscriberaddressid(ads);
					log.info("Address updated for " + s.getId() + "by user " + getLoggedUserNameWithIP());
					addressDAO.save(ads);
				}else{
					ads=null;
				}

				boolean authorizingFlag = checkAuthorizingPersonDetails(e);
				if(authorizingFlag){
					updateAuthorizing(ap, e);
					authorizingPersonDAO.save(ap);
					s.setAuthPersonDetails(ap);;
				}else{
					ap=null;
				}
				subscriberDao.save(s);
				log.info("Subscriber "+ s.getId()+ " updated by user " + getLoggedUserNameWithIP());
				if(e.getEmail() != null && systemParametersService.getIsEmailVerificationNeeded()) {
					mailService.generateEmailVerificationMail(s, e.getEmail());
				}
				SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
				List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(s.getId());
				if(subscriberGroups!=null && subscriberGroups.size() > 0){
					for(SubscriberGroups sg: subscriberGroups){
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
						String digestPin1 = MfinoUtil.calculateDigestPin(mdn.getMdn(), oneTimePin);
						mdn.setOtp(digestPin1);
						mdn.setOtpexpirationtime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));

						mdnDao.save(mdn);
						log.info("new OTP set for " + mdn.getId() + " by user " + getLoggedUserNameWithIP());
						NotificationWrapper smsNotificationWrapper=subscriberServiceExtended.generateOTPMessage(oneTimePin, CmFinoFIX.NotificationMethod_SMS);
						smsNotificationWrapper.setDestMDN(mdn.getMdn());
						smsNotificationWrapper.setLanguage(Integer.valueOf(Long.valueOf(mdn.getSubscriber().getLanguage()).intValue()));
						smsNotificationWrapper.setFirstName(mdn.getSubscriber().getFirstname());
		            	smsNotificationWrapper.setLastName(mdn.getSubscriber().getLastname());
						String smsMessage = notificationMessageParserService.buildMessage(smsNotificationWrapper,true);
						log.info("OTP SMS:" + smsMessage);
						String mdn2 = mdn.getMdn();
						smsService.setDestinationMDN(mdn2);
						smsService.setMessage(smsMessage);
						smsService.setNotificationCode(smsNotificationWrapper.getCode());
						smsService.asyncSendSMS();
						if(((e.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0) && e.getEmail() != null){
							NotificationWrapper emailNotificationWrapper=subscriberServiceExtended.generateOTPMessage(oneTimePin, CmFinoFIX.NotificationMethod_Email);
							emailNotificationWrapper.setDestMDN(mdn.getMdn());
							emailNotificationWrapper.setLanguage(Integer.valueOf(Long.valueOf(mdn.getSubscriber().getLanguage()).intValue()));
							emailNotificationWrapper.setFirstName(mdn.getSubscriber().getFirstname());
							emailNotificationWrapper.setLastName(mdn.getSubscriber().getLastname());
							String emailMessage = notificationMessageParserService.buildMessage(emailNotificationWrapper,true);
							String to=s.getEmail();
							String name=s.getFirstname();
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
						log.warn("No Default SVA Pocket set for " + s.getId());
						return errorMsg;
					}
					
					log.info("SubscriberMdnProcessor:: "+isEMoneyPocketRequired);
					if(isEMoneyPocketRequired == true){
					try{
						cardPan=pocketService.generateSVAEMoney16DigitCardPAN(mdn.getMdn());
					}catch (Exception ex) {
						log.error("Exception to create cardPan",ex);
					}
					
					//Create default emoney pocket
					Pocket epocket = pocketService.createPocket(svaPocketTemplate, mdn, CmFinoFIX.PocketStatus_Initialized, true, cardPan);
					if(epocket==null){
						log.info("Default emoney pocket creation failed for subscriber "+mdn.getId());
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
							log.error("PocketProcessor :: Pocket count limit reached for template:"+pocketTemplate.getDescription()+" for MDN:"+ mdn.getMdn() + " by user:"+getLoggedUserNameWithIP());	
							return getErrorMessage(MessageText._(" Pocket count Limit reached for this template  "), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Pocket count Limit reached for this template"));  		
						}
						
						Pocket bankPocket = pocketService.createPocket(pocketTemplate, mdn, CmFinoFIX.PocketStatus_Active, true, e.getAccountNumber());
						if(bankPocket == null){
							CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
							errorMsg.setErrorDescription(MessageText._("Default Bank Pocket creation failed for the Subscriber with the selected ptc:" + ptc.getId()));
							errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
							log.info("Default Bank Pocket creation failed for Subscriber "+s.getId());
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
				updateMessage(mdn, e,saf,ap,ads,null, false,str_tomcatPath,null, false);
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
		}

		return realMsg;
	}

	private void updateUnregisteredSubsPocket(SubscriberMdn subscriberMDN) {
		Pocket unregisteredPocket = subscriberMDN.getPockets().iterator().next();
		Subscriber subscriber = subscriberMDN.getSubscriber();
		
		KycLevel unBankedLevel = kyclevelDao.getByKycLevel(CmFinoFIX.SubscriberKYCLevel_UnBanked.longValue());
	    PocketTemplate eMoneyUnBankedTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(unBankedLevel.getKyclevel(), 
	    		 true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, null);
	    
	    unregisteredPocket.setPocketTemplateByOldpockettemplateid(unregisteredPocket.getPocketTemplateByPockettemplateid());
		unregisteredPocket.setPocketTemplateByPockettemplateid(eMoneyUnBankedTemplate);
		unregisteredPocket.setStatus(CmFinoFIX.PocketStatus_Active);
		pocketService.save(unregisteredPocket);

		SubsUpgradeBalanceLogDAO subsUpgradeBalanceLogDAO = DAOFactory.getInstance().getSubsUpgradeBalanceLogDAO();
		SubscriberUpgradeBalanceLog subUpgradeBalanceLog = new SubscriberUpgradeBalanceLog();
		subUpgradeBalanceLog.setSubscriberId(subscriber.getId());
		subUpgradeBalanceLog.setPockatBalance(unregisteredPocket.getCurrentbalance());
		subUpgradeBalanceLog.setTxnDate(new Timestamp());
		subsUpgradeBalanceLogDAO.save(subUpgradeBalanceLog);
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
	private CMJSError createBankPocket(String cardPan, SubscriberMdn mdn, Long kycLevel, Long groupID)
	{

		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		if(cardPan != null && cardPan.length() > 0){
			PocketTemplate bankPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevel, true, CmFinoFIX.PocketType_BankAccount, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			if (bankPocketTemplate == null) {
				errorMsg.setErrorDescription(MessageText._("No Default Bank Pocket set for this KYC"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				log.warn("No Default Bank Pocket set for " + mdn.getId());
				return errorMsg;
			}
			if(bankPocketTemplate.getId().intValue() >= 0 && cardPan != null)
			{
				boolean isallowed=pocketService.checkCount(bankPocketTemplate,mdn);
				if(!isallowed){
					log.error("PocketProcessor :: Pocket count limit reached for template:"+bankPocketTemplate.getDescription()+" for MDN:"+mdn.getMdn()+" by user:"+getLoggedUserNameWithIP());	
					return getErrorMessage(MessageText._(" Pocket count Limit reached for this template  "), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Pocket count Limit reached for this template"));  		
				}           

				Pocket bankPocket = pocketService.createDefaultBankPocket(bankPocketTemplate.getId().longValue(), mdn, cardPan);
				if(bankPocket==null){
					errorMsg.setErrorDescription(MessageText._("Default Bank Pocket creation failed for the Subscriber"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					log.info("Default Bank Pocket creation failed for Subscriber "+mdn.getId());
					return errorMsg;
				}
			}
		}
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		return errorMsg;
	}

	private void updatePocket(SubscriberMdn s) {
		Long unregTemplateID = systemParametersService.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED);
		KycLevel kycLevel = kyclevelDao.getByKycLevel(ConfigurationUtil.getIntialKyclevel());
		
		Long groupID = null;
		SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
		List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(s.getId());
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroupid();
		}
		PocketTemplate template = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevel.getKyclevel().longValue(), true, CmFinoFIX.PocketType_SVA, (s.getSubscriber().getType()), null, groupID);
		
		Pocket pocket = subscriberService.getDefaultPocket(s.getId().longValue(),unregTemplateID);
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		if(pocket!=null){
			pocket.setPocketTemplateByOldpockettemplateid(pocket.getPocketTemplateByPockettemplateid());
			pocket.setPocketTemplateByPockettemplateid(template);
			pocket.setPockettemplatechangedby(userService.getCurrentUser().getUsername());
			pocket.setPockettemplatechangetime(new Timestamp());
			pocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
			pocket.setStatustime(new Timestamp());
			pocketDAO.save(pocket);
		}
	}

	/**
	 * Generates new OTP and Send the same as SMS and Email if exists.
	 * @param mdnDao
	 * @param mdn
	 * @param notificationCode
	 */
	private void generateAndSendOTP(SubscriberMDNDAO mdnDao, SubscriberMdn mdn, Integer notificationCode) {
		Subscriber sub = mdn.getSubscriber();
		Integer OTPLength = systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(mdn.getMdn(), oneTimePin);
		mdn.setOtp(digestPin1);
		mdn.setDigestedpin(null);
		mdn.setAuthorizationtoken(null);
		mdn.setOtpexpirationtime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));

		NotificationWrapper notification = new NotificationWrapper();
		notification.setLanguage(Integer.valueOf(Long.valueOf(sub.getLanguage()).intValue()));
		notification.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		notification.setCode(notificationCode);
		notification.setOneTimePin(oneTimePin);
		notification.setFirstName(sub.getFirstname());
    	notification.setLastName(sub.getLastname());
		String message = notificationMessageParserService.buildMessage(notification,true);
		String mdn2 = mdn.getMdn();
		smsService.setDestinationMDN(mdn2);
		smsService.setMessage(message);
		smsService.setNotificationCode(notification.getCode());
		smsService.asyncSendSMS();
		if(((sub.getNotificationmethod() & CmFinoFIX.NotificationMethod_Email) > 0) && sub.getEmail() != null)
		{
			notification.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
			String emailMessage = notificationMessageParserService.buildMessage(notification,true);
			String to=sub.getEmail();
			String name=sub.getFirstname();
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

	public List<SubscriberMdn> get(SubscriberMdnQuery query) {
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
	
	private boolean isSubscriberEligibleTobeRetired(SubscriberMdn subscriberMDN){	
		List<Pocket> pocketList = getSubscriberPocketsList(subscriberMDN);
		for(Pocket pk:pocketList){
			if(pk.getCurrentbalance().compareTo(BigDecimal.ZERO) != 0 ){	
				return false;
			}
		}
		return true;
	}
	
	private List<Pocket> getSubscriberPocketsList(SubscriberMdn subscriberMDN){
		List<Pocket> pkList = null;
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		PocketQuery pocketQuery = new PocketQuery();
		pocketQuery.setMdnIDSearch(subscriberMDN.getId().longValue());
		pocketQuery.setPocketType(CmFinoFIX.PocketType_SVA);
		pkList = pocketDAO.get(pocketQuery);
		return pkList;
	}
}
