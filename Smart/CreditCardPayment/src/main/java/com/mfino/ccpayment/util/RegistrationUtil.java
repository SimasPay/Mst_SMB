/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.ccpayment.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.cc.message.CCInfo;
import com.mfino.cc.message.CCRegistrationInfo;
import com.mfino.dao.CardInfoDAO;
import com.mfino.dao.CreditCardDestinationDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.domain.CardInfo;
import com.mfino.domain.Company;
import com.mfino.domain.CreditCardDestinations;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCardInfo;
import com.mfino.fix.CmFinoFIX.CMJSCreditCardDestination;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSUsers;
import com.mfino.hibernate.Timestamp;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.uicore.fix.processor.UserProcessor;
import com.mfino.uicore.service.UserService;
import com.mfino.uicore.smart.processor.CCDestinationsProcessor;
import com.mfino.uicore.smart.processor.CardInfoProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MailUtil;
import com.mfino.validators.BrandValidator;

/**
 * 
 * @author admin
 */
public class RegistrationUtil {

	private static Logger log = LoggerFactory.getLogger(RegistrationUtil.class);
	protected static SessionFactory sessionFactory = null;

	protected static HibernateSessionHolder hibernateSessionHolder = null;

	static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
	}

	
	public CCRegistrationInfo process(CCRegistrationInfo registrationInfo,
			RequestType requestType) {
		log.info("RegistrationUtil: process");
		try {

			processRequest(registrationInfo, requestType);
			// don't we need explict roll back
		} catch (Exception e) {
			log.error("Error processing request " + requestType);
			log.info("error code " + registrationInfo.getErrorCode());
			log.info("error description "+ registrationInfo.getErrorDescription());
			if (registrationInfo.getErrorCode() == null) {
				registrationInfo.setErrorCode(5);
				registrationInfo.setErrorDescription(regErrorCodes.get(RegistrationError.unableToPerformOperation));
			} else {
				log.info("error code " + registrationInfo.getErrorCode());
				log.info("error description "+ registrationInfo.getErrorDescription());
			}
		}
		return registrationInfo;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void processRequest(CCRegistrationInfo registrationInfo,
			RequestType requestType) throws Exception {
		if (RequestType.Select.equals(requestType)) {
			getInfo(registrationInfo);
		} else {
			if (RequestType.Register.equals(requestType)) {
				registerInfo(registrationInfo);
			} else if (RequestType.Update.equals(requestType)) {
				updateInfo(registrationInfo);
			} else {
				log.info("Invalid Request Type");
			}
		}
	}

	private void updateUser(CMJSUsers.CGEntries userEntry,
			CCRegistrationInfo ccRegInfo) {
		if (ccRegInfo.getUserid() != null) {
			userEntry.setID(ccRegInfo.getUserid());
		}
		if (ccRegInfo.getUserVersion() != null) {
			userEntry.setRecordVersion(ccRegInfo.getUserVersion());
		}
		userEntry.setFirstName(ccRegInfo.getFirstName());
		userEntry.setLastName(ccRegInfo.getLastName());
		userEntry.setSecurityQuestion(ccRegInfo.getSecurityQuestion());
		userEntry.setSecurityAnswer(ccRegInfo.getSecurityAnswer());
		userEntry.setHomePhone(ccRegInfo.getHomePhone());
		userEntry.setWorkPhone(ccRegInfo.getWorkPhone());
		if (ccRegInfo.getDateOfBirth() != null) {
			userEntry.setDateOfBirth(new Timestamp(ccRegInfo.getDateOfBirth()));
		}
		userEntry.setIsCreditCardUserModified(Boolean.TRUE);
	}

	private void updateCardInfo(CMJSCardInfo.CGEntries cardEntry,CCInfo ccInfo, Long pocketID, Long subscriberID) {
		if (ccInfo.getCardId() != null) {
			cardEntry.setID(ccInfo.getCardId());
		}
		if (ccInfo.getCardInfoVersion() != null) {
			cardEntry.setRecordVersion(ccInfo.getCardInfoVersion());
		}
		cardEntry.setCardCity(ccInfo.getCity());
		cardEntry.setCardF6(ccInfo.getCCNumberF6());
		cardEntry.setCardIssuerName(ccInfo.getIssuerName());
		cardEntry.setCardL4(ccInfo.getCCNumberL4());
		cardEntry.setCardLine1(ccInfo.getAddress1());
		cardEntry.setCardLine2(ccInfo.getAddressLine2());
		cardEntry.setCardNameOnCard(ccInfo.getNameOnCard());
		/*
		 * if (pocketID != null) { cardEntry.setCardPocketID(pocketID); }
		 */
		cardEntry.setCardRegionName(ccInfo.getRegion());
		cardEntry.setCardState(ccInfo.getState());
		cardEntry.setCardZipCode(ccInfo.getZipCode());
		cardEntry.setCardBillingCity(ccInfo.getBillingcity());
		cardEntry.setCardBillingLine1(ccInfo.getBillingAddress());
		cardEntry.setCardBillingLine2(ccInfo.getBillingaddressLine2());
		cardEntry.setCardBillingRegionName(ccInfo.getBillingregion());
		cardEntry.setCardBillingState(ccInfo.getBillingstate());
		cardEntry.setCardBillingZipCode(ccInfo.getBillingzipCode());
		if (subscriberID != null) {
			cardEntry.setSubscriberID(subscriberID);
		}

	}

	private RegistrationError userStatusCheck(Integer status) {

		if (CmFinoFIX.UserStatus_Active.equals(status)) {
			return RegistrationError.alreadyActive;
		}
		if (CmFinoFIX.UserStatus_Confirmed.equals(status)) {
			return RegistrationError.alreadyConfirmed;
		}
		if (CmFinoFIX.UserStatus_Registered.equals(status)) {
			return RegistrationError.alreadyRegistered;
		}
		return RegistrationError.noError;
	}

	private CCRegistrationInfo registerInfo(CCRegistrationInfo ccRegInfo)
			throws Exception {
		log.info("RegistrationUtil: registerInfo");
		// get the subscriber
		SubscriberMDN subscriberMDN = new SubscriberMDNDAO().getByMDN(ccRegInfo.getMdn());
		// check if already user exits
		User user = subscriberMDN.getSubscriber().getUserBySubscriberUserID();
		if (null != user) {
			RegistrationError regError = userStatusCheck(user.getStatus());
			if (!regError.equals(RegistrationError.noError)) {
				ccRegInfo.setErrorCode(knownError);
				ccRegInfo.setErrorDescription(regErrorCodes.get(regError));
				log.info("registration error" + regError.toString() + ": "+ ccRegInfo.toString());
				return ccRegInfo;
			}
		}

		// create the user
		log.info("registerInfo: creating user");
		CMJSUsers jsUsers = new CMJSUsers();
		jsUsers.allocateEntries(1);
		jsUsers.setaction(CmFinoFIX.JSaction_Insert);
		CMJSUsers.CGEntries userEntry = new CMJSUsers.CGEntries();
		
		userEntry.setCompanyID(subscriberMDN.getSubscriber().getCompany().getID());
		userEntry.setAdminComment("");
		userEntry.setEmail(ccRegInfo.getEmail());
		userEntry.setFailedLoginCount(0);
		userEntry.setUserStatus(CmFinoFIX.UserStatus_Registered);
		userEntry.setRole(CmFinoFIX.Role_Subscriber);
		userEntry.setUsername(ccRegInfo.getUsername());
		userEntry.setPassword(ccRegInfo.getPassword());
		userEntry.setFirstTimeLogin(false);
		userEntry.setCreatedBy("System");
		userEntry.setTimezone(subscriberMDN.getSubscriber().getTimezone());
		userEntry.setLanguage(subscriberMDN.getSubscriber().getLanguage());
		updateUser(userEntry, ccRegInfo);
		jsUsers.getEntries()[0] = userEntry;
		
		UserProcessor userProcessor = new UserProcessor();
		
		CFIXMsg usrFixMsg = userProcessor.processSubscriber(jsUsers);
		
		if (usrFixMsg instanceof CMJSError) {
			CMJSError errSubsFixMsg = (CMJSError) usrFixMsg;
			ccRegInfo.setErrorCode(errSubsFixMsg.getErrorCode());
			ccRegInfo.setErrorDescription(errSubsFixMsg.getErrorDescription());
			log.info("registration error " + errSubsFixMsg.getErrorCode() + ":"
					+ errSubsFixMsg.getErrorDescription()
					+ ccRegInfo.toString());
			throw new Exception();
		} else {
			jsUsers = (CMJSUsers) usrFixMsg;
		}
		
		if (jsUsers.getEntries().length == 0) {
			ccRegInfo.setErrorCode(knownError);
			ccRegInfo.setErrorDescription(regErrorCodes.get(RegistrationError.userCreationFailed));
			log.info("registration error "
					+ RegistrationError.userCreationFailed.toString()
					+ ccRegInfo.toString());
			throw new Exception();
		}
		
		log.info("registerInfo: After creating user");

		// create card info
		log.info("registerInfo: Creating Card Info");
		CMJSCardInfo jsCardInfo = new CMJSCardInfo();
		jsCardInfo.setaction(CmFinoFIX.JSaction_Insert);
		jsCardInfo.allocateEntries(ccRegInfo.getCcList().size());
		
		for (int i = 0; i < ccRegInfo.getCcList().size(); ++i) {
			CMJSCardInfo.CGEntries cardEntry = new CMJSCardInfo.CGEntries();
			updateCardInfo(cardEntry, ccRegInfo.getCcList().get(i), null,subscriberMDN.getSubscriber().getID());
			jsCardInfo.getEntries()[i] = cardEntry;
		}
		
		CardInfoProcessor cardProcessor = new CardInfoProcessor();
		CFIXMsg cardFixMsg = cardProcessor.process(jsCardInfo);
		
		if (cardFixMsg instanceof CMJSError) {
			CMJSError errSubsFixMsg = (CMJSError) cardFixMsg;
			ccRegInfo.setErrorCode(errSubsFixMsg.getErrorCode());
			ccRegInfo.setErrorDescription(errSubsFixMsg.getErrorDescription());
			log.info("registration error " + errSubsFixMsg.getErrorCode() + ":"
					+ errSubsFixMsg.getErrorDescription()
					+ ccRegInfo.toString());
			throw new Exception();
		} else {
			jsCardInfo = (CMJSCardInfo) cardFixMsg;
		}
		
		if (jsCardInfo.getEntries().length == 0) {
			ccRegInfo.setErrorCode(knownError);
			ccRegInfo.setErrorDescription(regErrorCodes
					.get(RegistrationError.cardCreationFailed));
			log.info("registration error "
					+ RegistrationError.cardCreationFailed.toString()
					+ ccRegInfo.toString());
			throw new Exception();
		}
		log.info("registerInfo:after Card Info created");
		
		CMJSCreditCardDestination ccDestinations = new CMJSCreditCardDestination();
		ccDestinations.setaction(CmFinoFIX.JSaction_Insert);
		ccDestinations.allocateEntries(ccRegInfo.getCcDestinations().size());
		
		for (int i = 0; i < ccRegInfo.getCcDestinations().size(); ++i) {
			CMJSCreditCardDestination.CGEntries ccdestEntry = new CMJSCreditCardDestination.CGEntries();
			ccdestEntry.setDestMDN(ccRegInfo.getCcDestinations().get(i).getDestMDN());
			ccdestEntry.setCCMDNStatus(CmFinoFIX.CCMDNStatus_Registered);
			ccdestEntry.setSubscriberID(subscriberMDN.getSubscriber().getID());
			ccDestinations.getEntries()[i] = ccdestEntry;
		}
		
		if (ccDestinations.getEntries() != null	&& ccDestinations.getEntries().length != 0) {
			CCDestinationsProcessor ccdestProcessor = new CCDestinationsProcessor();
			CFIXMsg ccdestFixMsg = ccdestProcessor.process(ccDestinations);
			if (ccdestFixMsg instanceof CMJSError) {
				CMJSError errSubsFixMsg = (CMJSError) ccdestFixMsg;
				ccRegInfo.setErrorCode(errSubsFixMsg.getErrorCode());
				ccRegInfo.setErrorDescription(errSubsFixMsg.getErrorDescription());
				log.info("registration error " + errSubsFixMsg.getErrorCode()
						+ ":" + errSubsFixMsg.getErrorDescription()
						+ ccRegInfo.toString());
				throw new Exception();
			} else {
				ccDestinations = (CMJSCreditCardDestination) ccdestFixMsg;
				log.info("total Destinations added"+ ccDestinations.getEntries().length);
			}
		}

		// set the subscriber user id
		Subscriber subscriber = subscriberMDN.getSubscriber();
		subscriber.setUserBySubscriberUserID(new UserDAO().getById(jsUsers.getEntries()[0].getID()));
		log.info("registerInfo: CreditCardUser Created for Subscriber "+ccRegInfo.getMdn());
		new SubscriberDAO().save(subscriber);

		// send mail
		String codeBody = StringUtils.replace(ConfigurationUtil.getRegisterCCSubscriberCodeBody(), "$(confirmationURL)",
				ConfigurationUtil.getCCPaymentDeploymentURL()
						+ "/registrationCodeConfirmation.jsp");
		codeBody = StringUtils.replace(codeBody, "$(autoConfirmationURL)",
				ConfigurationUtil.getCCPaymentDeploymentURL()
						+ "/RegistrationServlet");
		codeBody = StringUtils.replace(codeBody, "$(confirmationCode)", jsUsers.getEntries()[0].getConfirmationCode());
		codeBody = StringUtils.replace(codeBody, "$(userName)", jsUsers.getEntries()[0].getUsername());
		String emailMsg = ConfigurationUtil.getRegisterCCSubscriberStandardBody()
				+ codeBody
				+ ConfigurationUtil.getRegisterCCSubscriberAdditionalMsg()
				+ ConfigurationUtil.getRegisterCCSubscriberSignature();
		String emailSubject = ConfigurationUtil.getRegisterCCSubscriberSubject();

		try {
			log.info("registerInfo: Sending mail");
			MailUtil.sendMail(ccRegInfo.getEmail(), ccRegInfo.getFirstName()
					+ " " + ccRegInfo.getLastName(), emailSubject, emailMsg);
		} catch (Exception ee) {
			log.error("Failed to send User Add information.", ee);
		}
		ccRegInfo.setErrorCode(noError);
		ccRegInfo.setErrorDescription(regErrorCodes.get(RegistrationError.noError));
		log.info("registration success " + ccRegInfo.toString());
		return ccRegInfo;
	}

	private CCRegistrationInfo updateInfo(CCRegistrationInfo ccRegInfo)
			throws Exception {
		CMJSUsers jsUsers = new CMJSUsers();
		jsUsers.setaction(CmFinoFIX.JSaction_Update);
		jsUsers.allocateEntries(1);
		CMJSUsers.CGEntries userEntry = new CMJSUsers.CGEntries();
		// update user
		updateUser(userEntry, ccRegInfo);
		jsUsers.getEntries()[0] = userEntry;
		UserProcessor userProcessor = new UserProcessor();
		CFIXMsg userFixMsg = userProcessor.process(jsUsers);
		if (userFixMsg instanceof CMJSError) {
			CMJSError errSubsFixMsg = (CMJSError) userFixMsg;
			ccRegInfo.setErrorCode(errSubsFixMsg.getErrorCode());
			ccRegInfo.setErrorDescription(errSubsFixMsg.getErrorDescription());
			log.info("updation error " + errSubsFixMsg.getErrorCode() + ":"
					+ errSubsFixMsg.getErrorDescription()
					+ ccRegInfo.toString());
			throw new Exception();
		}

		// update cardinfo
		CMJSCardInfo jsCardInfo = new CMJSCardInfo();
		jsCardInfo.setaction(CmFinoFIX.JSaction_Update);
		jsCardInfo.allocateEntries(ccRegInfo.getCcList().size());
		for (int i = 0; i < ccRegInfo.getCcList().size(); i++) {
			if (null != ccRegInfo.getCcList().get(i).getCardId()
					&& 0 != ccRegInfo.getCcList().get(i).getCardId()) {
				CMJSCardInfo.CGEntries cardEntry = new CMJSCardInfo.CGEntries();
				updateCardInfo(cardEntry, ccRegInfo.getCcList().get(i), null,
						null);
				jsCardInfo.getEntries()[i] = cardEntry;
			}
		}
		CardInfoProcessor cardProcessor = new CardInfoProcessor();
		CFIXMsg cardFixMsg = cardProcessor.process(jsCardInfo);
		if (cardFixMsg instanceof CMJSError) {
			CMJSError errSubsFixMsg = (CMJSError) cardFixMsg;
			ccRegInfo.setErrorCode(errSubsFixMsg.getErrorCode());
			ccRegInfo.setErrorDescription(errSubsFixMsg.getErrorDescription());
			log.info("updation error " + errSubsFixMsg.getErrorCode() + ":"
					+ errSubsFixMsg.getErrorDescription()
					+ ccRegInfo.toString());
			throw new Exception();
		}
		// update Destinations
		CMJSCreditCardDestination ccDestination = new CMJSCreditCardDestination();
		ccDestination.setaction(CmFinoFIX.JSaction_Update);
		ccDestination.allocateEntries(ccRegInfo.getOldDestinations());
		CMJSCreditCardDestination ccDestinationnew = new CMJSCreditCardDestination();
		ccDestinationnew.setaction(CmFinoFIX.JSaction_Insert);
		ccDestinationnew.allocateEntries(ccRegInfo.getNewDestinations());
		int i = 0, j = 0;
		CreditCardDestinationDAO ccCardDestinationDAO = new CreditCardDestinationDAO();
		CreditCardDestinations ccDestinations = new CreditCardDestinations();
		while (i < ccRegInfo.getCcDestinations().size()) {
			CMJSCreditCardDestination.CGEntries ccdestEntries = new CMJSCreditCardDestination.CGEntries();
			ccdestEntries.setDestMDN(ccRegInfo.getCcDestinations().get(i).getDestMDN());
			ccdestEntries.setSubscriberID(ccRegInfo.getSubscriberid());
			if (ccRegInfo.getCcDestinations().get(i).getID() != null) {
				ccDestinations = ccCardDestinationDAO.getById(ccRegInfo.getCcDestinations().get(i).getID());
				if(!ccDestinations.getDestMDN().equals(ccRegInfo.getCcDestinations().get(i).getDestMDN())){
				ccdestEntries.setCCMDNStatus(CmFinoFIX.CCMDNStatus_Updated);
				ccdestEntries.setID(ccRegInfo.getCcDestinations().get(i).getID());
				ccdestEntries.setRecordVersion(ccRegInfo.getCcDestinations().get(i).getVersion());
				ccDestination.getEntries()[i] = ccdestEntries;
				}
				i++;
			} else {
				ccdestEntries.setCCMDNStatus(CmFinoFIX.CCMDNStatus_New);
				ccDestinationnew.getEntries()[j] = ccdestEntries;
				j++;
				i++;
			}
		}
		CFIXMsg ccdestFixMsg;
		CCDestinationsProcessor ccDestinationsProcessor = new CCDestinationsProcessor();
		if (ccDestination.getEntries() != null) {
			ccdestFixMsg = ccDestinationsProcessor.process(ccDestination);
			if (ccdestFixMsg instanceof CMJSError) {
				CMJSError errSubsFixMsg = (CMJSError) ccdestFixMsg;
				ccRegInfo.setErrorCode(errSubsFixMsg.getErrorCode());
				ccRegInfo.setErrorDescription(errSubsFixMsg
						.getErrorDescription());
				log.info("updation error " + errSubsFixMsg.getErrorCode() + ":"
						+ errSubsFixMsg.getErrorDescription()
						+ ccRegInfo.toString());
				throw new Exception();
			}
		}
		if (ccDestinationnew.getEntries() != null) {
			ccdestFixMsg = ccDestinationsProcessor.process(ccDestinationnew);
			if (ccdestFixMsg instanceof CMJSError) {
				CMJSError errSubsFixMsg = (CMJSError) ccdestFixMsg;
				ccRegInfo.setErrorCode(errSubsFixMsg.getErrorCode());
				ccRegInfo.setErrorDescription(errSubsFixMsg
						.getErrorDescription());
				log.info("updation error " + errSubsFixMsg.getErrorCode() + ":"
						+ errSubsFixMsg.getErrorDescription()
						+ ccRegInfo.toString());
				throw new Exception();
			}
		}
		String codeBody = StringUtils.replace(ConfigurationUtil
				.getUpdateCCSubscriberCodeBody(), "$(confirmationURL)",
				ConfigurationUtil.getCCPaymentDeploymentURL()
						+ "/registrationCodeConfirmation.jsp");
		codeBody = StringUtils.replace(codeBody, "$(autoConfirmationURL)",
				ConfigurationUtil.getCCPaymentDeploymentURL()
						+ "/RegistrationServlet");
		codeBody = StringUtils.replace(codeBody, "$(confirmationCode)", jsUsers
				.getEntries()[0].getConfirmationCode());
		codeBody = StringUtils.replace(codeBody, "$(userName)", jsUsers
				.getEntries()[0].getUsername());
		String emailMsg = ConfigurationUtil.getUpdateCCSubscriberStandardBody()
				+ codeBody
				+ ConfigurationUtil.getRegisterCCSubscriberAdditionalMsg()
				+ ConfigurationUtil.getRegisterCCSubscriberSignature();
		String emailSubject = ConfigurationUtil
				.getRegisterCCSubscriberSubject();

		try {
			log.info("UpdateInfo: Sending mail");
			MailUtil.sendMail(ccRegInfo.getEmail(), ccRegInfo.getFirstName()
					+ " " + ccRegInfo.getLastName(), emailSubject, emailMsg);
		} catch (Exception ee) {
			log.error("Failed to send User Update information.", ee);
		}

		ccRegInfo.setErrorCode(noError);
		ccRegInfo.setErrorDescription(regErrorCodes
				.get(RegistrationError.noError));
		log.info("update success " + ccRegInfo.toString());
		return ccRegInfo;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private CCRegistrationInfo getInfo(CCRegistrationInfo ccRegInfo)
			throws Exception {
		User user = UserService.getCurrentUser();
		Subscriber subs = user.getSubscriberFromSubscriberUserID().iterator().next();
		SubscriberMDN subsMdn = subs.getSubscriberMDNFromSubscriberID().iterator().next();
		CardInfoDAO cardInfodao = DAOFactory.getInstance().getCardInfoDAO();
		List<CardInfo> cardInfoList = cardInfodao.getCards(subs);
		CreditCardDestinationDAO ccdestdao = DAOFactory.getInstance().getCreditCardDestinationDAO();
		List<CreditCardDestinations> creditCardDestinations = ccdestdao.getAllDestinations(subs);
		List<CreditCardDestinations> ccDestinations = new ArrayList<CreditCardDestinations>();
		List<CCInfo> ccInfoList = new ArrayList<CCInfo>();
		Map<String, Long> ccDestCompID = new HashMap<String, Long>();
		// getting active destinations
		ccRegInfo.setCcList(ccInfoList);
		int i = 1;
		ccDestCompID.put(subsMdn.getMDN(), subs.getCompany().getID());
		for (Iterator<CreditCardDestinations> ccDestinationIterator = creditCardDestinations.iterator(); ccDestinationIterator.hasNext()
				&& i < ConfigurationUtil.getCCDestinationLimit();) {
			CreditCardDestinations ccCardDestinations = ccDestinationIterator.next();
			if (ccCardDestinations.getCCMDNStatus().equals(CmFinoFIX.CCMDNStatus_Active)) {
				ccDestinations.add(ccCardDestinations);
				ccDestCompID.put(ccCardDestinations.getDestMDN(), getCompanyfromMDN(ccCardDestinations.getDestMDN()));				
				i++;
			}
		}
		ccRegInfo.setCcDestinations(ccDestinations);
		ccRegInfo.setCcDestCompIDs(ccDestCompID);
		ccRegInfo.setIsConfirmationRequired(false);

		// getting active cards info
		for (Iterator<CardInfo> cardInfoIterator = cardInfoList.iterator(); cardInfoIterator.hasNext();) {
			CardInfo cardInfo = cardInfoIterator.next();
			if (CmFinoFIX.UserStatus_Active.equals(cardInfo.getCardStatus())
					&& (CmFinoFIX.PocketStatus_Active.equals(cardInfo.getPocket().getStatus()) || CmFinoFIX.PocketStatus_PendingRetirement.equals(cardInfo.getPocket().getStatus()))
					&& (CmFinoFIX.SubscriberRestrictions_None.equals(cardInfo.getPocket().getRestrictions()) || null == cardInfo.getPocket().getRestrictions())) {

				CCInfo ccInfo = new CCInfo();
				ccInfo.setCardId(cardInfo.getID());
				ccInfo.setCardInfoVersion(cardInfo.getVersion());
				ccInfo.setAddressLine2(cardInfo.getAddress().getLine2());
				ccInfo.setAddress1(cardInfo.getAddress().getLine1());
				ccInfo.setCCNumberF6(cardInfo.getCardF6());
				ccInfo.setCCNumberL4(cardInfo.getCardL4());
				ccInfo.setCardInfoVersion(cardInfo.getVersion());
				ccInfo.setCity(cardInfo.getAddress().getCity());
				ccInfo.setNameOnCard(cardInfo.getNameOnCard());
				ccInfo.setIssuerName(cardInfo.getIssuerName());
				ccInfo.setPocketId(cardInfo.getPocket().getID());
				ccInfo.setRegion(cardInfo.getAddress().getRegionName());
				ccInfo.setState(cardInfo.getAddress().getState());
				ccInfo.setZipCode(cardInfo.getAddress().getZipCode());
				ccInfo.setBillingAddress(cardInfo.getAddressByBillingAddressID().getLine1());
				ccInfo.setBillingaddressLine2(cardInfo.getAddressByBillingAddressID().getLine2());
				ccInfo.setBillingcity(cardInfo.getAddressByBillingAddressID().getCity());
				ccInfo.setBillingregion(cardInfo.getAddressByBillingAddressID().getRegionName());
				ccInfo.setBillingstate(cardInfo.getAddressByBillingAddressID().getState());
				ccInfo.setBillingzipCode(cardInfo.getAddressByBillingAddressID().getZipCode());
				if (cardInfo.getisConformationRequired()) {
					if (System.currentTimeMillis()- cardInfo.getLastUpdateTime().getTime() > ConfigurationUtil.getCreditcardUpdateExpirationTimeInHrs() * 60 * 60 * 1000) {
						ExpireCCUpdatesTool expireCCUpdatesTool = new ExpireCCUpdatesTool();
						log.info("user profile changes are expired"+user.getUsername());
						
						boolean revertChanges = false;
						try
						{
							revertChanges = expireCCUpdatesTool.revertChanges(cardInfo);
						}
						catch(Exception e)
						{
							log.info("Revert profile changes failed for user " + user.getUsername());
						}
						if (revertChanges) {
							return getInfo(ccRegInfo);
						}
					}
					ccRegInfo.setIsConfirmationRequired(true);
				}
				ccInfoList.add(ccInfo);
			}
		}

		if (ccInfoList.size() < 1) {
			ccRegInfo.setErrorCode(knownError);
			ccRegInfo.setErrorDescription(getInfoErrorCodes.get(GetInfoError.hasNoValidPockets));
			log.info("getinfo error "
					+ GetInfoError.hasNoValidPockets.toString() + ":"
					+ ccRegInfo.toString());
			throw new Exception();
		}

		// setting other information
		ccRegInfo.setSubscriberid(subs.getID());
		ccRegInfo.setCompanyid(user.getCompany().getID());
		ccRegInfo.setSecurityAnswer(user.getSecurityAnswer());
		ccRegInfo.setEmail(user.getEmail());
		ccRegInfo.setFirstName(user.getFirstName());
		ccRegInfo.setLastName(user.getLastName());
		ccRegInfo.setMdn(subsMdn.getMDN());
		ccRegInfo.setPassword(user.getPassword());
		ccRegInfo.setSecurityQuestion(user.getSecurityQuestion());
		ccRegInfo.setSubscriberVersion(subs.getVersion());
		ccRegInfo.setUserVersion(user.getVersion());
		ccRegInfo.setUserid(user.getID());
		ccRegInfo.setHomePhone(user.getHomePhone());
		ccRegInfo.setWorkPhone(user.getWorkPhone());
		if (user.getDateOfBirth() != null) {
			ccRegInfo.setDateOfBirth(new Date(user.getDateOfBirth().getTime()));
		}

		ccRegInfo.setErrorCode(noError);
		ccRegInfo.setErrorDescription(getInfoErrorCodes
				.get(GetInfoError.noError));
		log.info("getinfo success " + ccRegInfo.toString());
		if (ccRegInfo.getIsConfirmationRequired())
			ccRegInfo.setErrorDescription(getInfoErrorCodes.get(GetInfoError.confirmationRequired));

		return ccRegInfo;
	}

	public static CMJSError getSecurityQuestion() {
		CMJSError errMsg = new CMJSError();
		errMsg.setErrorCode(1);
		errMsg.setErrorDescription("Could not load securityQuestion");
		log.info("Getting security question");
		Session session = sessionFactory.openSession();
		hibernateSessionHolder.setSession(session);		
		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);

		try {
			Subscriber subscriber = UserService.getCurrentUser().getSubscriberFromSubscriberUserID().iterator().next();
			Set<CardInfo> cardinfo = subscriber.getCardInfoFromSubscriberID();
			CardInfo card = null;
			for (CardInfo cardInfo2 : cardinfo) {
				if (cardInfo2.getCardStatus().equals(CmFinoFIX.UserStatus_Active)) {
					card = cardInfo2;
					break;
				}
			}
			if (card != null && card.getisConformationRequired()) {
				errMsg.setErrorCode(1);
				errMsg.setErrorDescription(getInfoErrorCodes.get(GetInfoError.confirmationRequired));
			} else if (card != null) {
				errMsg.setErrorCode(0);
				errMsg.setErrorDescription(UserService.getCurrentUser().getSecurityQuestion());
			}

		} catch (Exception e) {
			log.info("security question", e);
		} finally {
			if(session!=null)
			{
				session.close();
			}
		}
		return errMsg;
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public static CMJSError getSecurityAnswer() {
		CMJSError errMsg = new CMJSError();
		errMsg.setErrorCode(1);
		errMsg.setErrorDescription("Could not load security answer");
		log.info("Getting security answer");
		try {
			errMsg.setErrorCode(0);
			errMsg.setErrorDescription(UserService.getCurrentUser().getSecurityAnswer());
		} catch (Exception e) {
			log.info("security answer", e);
		} 
		return errMsg;
	}

	public static CMJSError getEmail() {
		CMJSError errMsg = new CMJSError();
		errMsg.setErrorCode(1);
		errMsg.setErrorDescription("Could not load user Email");
		log.info("Getting Email");
		Session session = sessionFactory.openSession();
		hibernateSessionHolder.setSession(session);		
		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);

		try {
			String email = UserService.getCurrentUser().getEmail();
			errMsg.setErrorCode(0);
			errMsg.setErrorDescription(email);
		} catch (Exception e) {
			log.info("getEmail ", e);
		} finally {
			if(session!=null)
			{
				session.close();
			}
		}
		return errMsg;
	}

	public Long getCompanyfromMDN(String mdn){
		BrandValidator brandValidator = new BrandValidator(mdn);
		Integer validationResult = brandValidator.validate();
		Company company = brandValidator.getCompany();
		if(company != null)
			return company.getID();
		else 
			return null;
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public static boolean checkCompany(Long SubscriberId, String mdn) {
		log.info("company check: subscriberid" + SubscriberId + " mdn " + mdn);
		if (ConfigurationUtil.isInterCompanyCCPaymentAllowed()) {
			return true;
		}
		try {
			SubscriberDAO sDao = DAOFactory.getInstance().getSubscriberDAO();
			BrandValidator brandValidator = new BrandValidator(mdn, sDao.getById(SubscriberId).getCompany());
			if (!brandValidator.validate().equals(CmFinoFIX.ResponseCode_Success)) {
				return false;
			}
		} catch (Exception e) {
			log.info("Exception in companyCheck", e);
			return false;
		}
		return true;
	}

	public enum RequestType {

		Update, Register, Select
	}

	private enum RegistrationError {

		noError, alreadyActive, alreadyRegistered, alreadyConfirmed, alreadyRejected, userCreationFailed, cardCreationFailed, unableToPerformOperation
	}

	private enum GetInfoError {
		noError, hasNoValidPockets, unableToPerformOperation, confirmationRequired
	}

	private static Integer knownError = -1;
	private static Integer noError = 0;
	private static Map<RegistrationError, String> regErrorCodes = new HashMap<RegistrationError, String>();

	static {
		regErrorCodes.put(RegistrationError.noError, "Success");
		regErrorCodes.put(RegistrationError.alreadyActive,"Your registration is already active.<br> Please try and login.");
		regErrorCodes.put(RegistrationError.alreadyRegistered,"Your have already registered.<br> Please check your email to confirm.");
		regErrorCodes.put(RegistrationError.alreadyConfirmed,"Your registration is confirmed.<br> Please wait for administrator to activate the same.");
		regErrorCodes.put(RegistrationError.alreadyRejected,"Your registration is rejected.<br> Please consult administrator for further information.");
		regErrorCodes.put(RegistrationError.userCreationFailed,"Internal error, please try after some time.");
		regErrorCodes.put(RegistrationError.cardCreationFailed,"Internal error, please try after some time.");
		regErrorCodes.put(RegistrationError.unableToPerformOperation,"Internal error, please consult administrator.");
	}
	private static Map<GetInfoError, String> getInfoErrorCodes = new HashMap<GetInfoError, String>();

	static {
		getInfoErrorCodes.put(GetInfoError.noError, "Success");
		getInfoErrorCodes.put(GetInfoError.hasNoValidPockets,"No active cards found, please consult administrator.");
		getInfoErrorCodes.put(GetInfoError.unableToPerformOperation,"Internal error, please consult administrator.");
		getInfoErrorCodes.put(GetInfoError.confirmationRequired,"Please confirm the changes via email or wait to expire changes");
	}
}
