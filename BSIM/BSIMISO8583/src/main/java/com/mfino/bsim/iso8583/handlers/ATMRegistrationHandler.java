package com.mfino.bsim.iso8583.handlers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Notification;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistration;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.handlers.hsm.HSMHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.PocketService;
import com.mfino.service.TransactionChargingService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.SystemParametersUtil;

public class ATMRegistrationHandler extends FIXMessageHandler implements IATMRegistrationHandler {

	private static Logger log = LoggerFactory.getLogger(ATMRegistrationHandler.class);
	private ISOMsg	msg;
	private HibernateTransactionManager htm;
	private static ATMRegistrationHandler atmRegistrationHandler;

	public static ATMRegistrationHandler createInstance(){
		if(atmRegistrationHandler==null){
			atmRegistrationHandler = new ATMRegistrationHandler();
		}
		
		return atmRegistrationHandler;
	}
	
	public static ATMRegistrationHandler getInstance(){
		if(atmRegistrationHandler==null){
			throw new RuntimeException("Instance is not already created");
		}
		return atmRegistrationHandler;
	}

	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Integer handle(ISOMsg msg,Session session) throws Exception {
			this.msg=msg;
			SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
			SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			KYCLevelDAO kycLevelDAO = DAOFactory.getInstance().getKycLevelDAO();
			PocketTemplateDAO pocketTemplateDAO =  DAOFactory.getInstance().getPocketTemplateDao();
			PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
			SubscriberGroupDao subGroupDAO = DAOFactory.getInstance().getSubscriberGroupDao();
			GroupDao groupDAO = DAOFactory.getInstance().getGroupDao();
			subGroupDAO.setSession(session);
			groupDAO.setSession(session);
			subscriberDAO.setSession(session);
			subscriberMDNDAO.setSession(session);
			kycLevelDAO.setSession(session);
			pocketTemplateDAO.setSession(session);
			pocketDAO.setSession(session);
			log.info("ATMRegistrationHandler :: handle() BEGIN");
			String sourceMDN=MfinoUtil.normalizeMDN(msg.getString("61"));
			if((sourceMDN.length() < SystemParametersUtil.getInteger(SystemParameterKeys.MIN_MDN_LENGTH_WITH_COUNTRYCODE)) ||
					(sourceMDN.length() > SystemParametersUtil.getInteger(SystemParameterKeys.MAX_MDN_LENGTH_WITH_COUNTRYCODE)) ){
				log.info("ATMRegistrationHandler :: handle Invalid MDN Length");
				msg.set(39,GetConstantCodes.REJECT);
				return CmFinoFIX.NotificationCode_InvalidMDNLength;
			}
			String accountNumber=msg.getString("2");
			accountNumber =accountNumber.substring(0, accountNumber.length()-1);
			log.info("ATMRegistrationHandler :: handle() accountNumber="+accountNumber);
			String sourceAccountNumber=msg.getString("102");
			
			String cifNumber;
			if(msg.getString("48").length()<23){
				log.info("ATMRegistrationHandler :: field de-48 has invalid length");
				msg.set(39,GetConstantCodes.REJECT);
				return CmFinoFIX.NotificationCode_Failure;
			}else{
			cifNumber = msg.getString("48").substring(3,23).trim();
				if(StringUtils.isBlank(cifNumber)){
					log.info("ATMRegistrationHandler :: handle invalid cifNumber received in de-48");
					msg.set(39,GetConstantCodes.REJECT);
					return CmFinoFIX.NotificationCode_Failure;
				}
			}
			String encryptedPin = msg.getString("52");
			HSMHandler handler = new HSMHandler();
			String decryptedPin;
			try{
			decryptedPin=handler.generateOffsetForATMRequest(sourceMDN,accountNumber, encryptedPin);
			if(null==decryptedPin){
				log.info("ATMRegistratinoHandler :: handle invalid Pin block");
				msg.set(39,GetConstantCodes.FAILURE);
				return CmFinoFIX.NotificationCode_Failure;
			}
			}
			catch(Exception e){
				log.error(e.getMessage());
				msg.set(39,GetConstantCodes.FAILURE);
				return CmFinoFIX.NotificationCode_Failure;
			}
			String cc = CmFinoFIX.SourceApplication_ATM.toString();
			msg.set(39, GetConstantCodes.FAILURE);
			log.info("ATMRegistrationHandler :: handle encryptedPin = " + encryptedPin);
			String clearPIN = decryptedPin.substring(0, SystemParametersUtil.getPinLength());
			CMSubscriberRegistration subscriberRegistration = new CMSubscriberRegistration();
			subscriberRegistration.setSourceMDN(sourceMDN);
			subscriberRegistration.setMDN(sourceMDN);
			//use cifnumber as regbranch code applicationid
			subscriberRegistration.setApplicationID(cifNumber);
			//kyc level fully banked bsim
			subscriberRegistration.setKYCLevel(Long.parseLong(CmFinoFIX.RecordType_SubscriberFullyBanked.toString()));
			subscriberRegistration.setChannelCode(cc);
			subscriberRegistration.setSourceApplication(CmFinoFIX.SourceApplication_ATM);
			subscriberRegistration.setPin(clearPIN);
			TransactionsLog transactionsLog = null;
			log.info("Handling subscriberRegistration atm request");
			transactionsLog = saveTransactionsLog(CmFinoFIX.MessageType_SubscriberRegistration,subscriberRegistration.DumpFields());
			subscriberRegistration.setTransactionID(transactionsLog.getID());
			Transaction transactionDetails = null;
			ServiceCharge sc = new ServiceCharge();
			sc.setSourceMDN(subscriberRegistration.getSourceMDN());
			sc.setDestMDN(subscriberRegistration.getMDN());
			if(StringUtils.isNotBlank(cc))
			sc.setChannelCodeId(Long.parseLong(cc));
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION);
			sc.setTransactionAmount(BigDecimal.ZERO);
			sc.setTransactionLogId(transactionsLog.getID());
			sc.setTransactionIdentifier(subscriberRegistration.getTransactionIdentifier());
			TransactionChargingService transactionChargingService = new TransactionChargingService();
			try{
				transactionDetails =transactionChargingService.getCharge(sc);
			}catch (InvalidServiceException e) {
				msg.set(39, GetConstantCodes.FAILURE);
				log.error(e.getMessage());
				return null;
			} catch (InvalidChargeDefinitionException e) {
				msg.set(39, GetConstantCodes.FAILURE);
				log.error(e.getMessage());
				return null;
			}
			Integer regResponse = null;
			//new account number and new mdn
			regResponse=createsubscriberWithActiveBankPocket(subscriberRegistration,sourceAccountNumber, subscriberMDNDAO, kycLevelDAO, subscriberDAO, pocketTemplateDAO, pocketDAO,subGroupDAO,groupDAO);
			ServiceChargeTransactionLog sctl = transactionDetails.getServiceChargeTransactionLog();
			subscriberRegistration.setServiceChargeTransactionLogID(sctl.getID());  
			Notification  notification;
			String notificationName = "";
			
			if (!regResponse.equals(CmFinoFIX.ResponseCode_Success)) {
				msg.set(39,GetConstantCodes.FAILURE);
				notification = DAOFactory.getInstance().getNotificationDAO().getByNotificationCode(regResponse);
				if(notification != null){
					notificationName = notification.getCodeName();
				}else{
					log.error("Could not find the failure notification code: "+regResponse);
				}
				transactionChargingService.failTheTransaction(sctl, MessageText._("Subscriber Registration failed. Notification Code: "+regResponse+" NotificationName: "+notificationName));
			}else{
				msg.set(39,GetConstantCodes.SUCCESS);
				if (sctl != null) {
					regResponse=CmFinoFIX.NotificationCode_SubscriberRegistrationSuccessfulToAgent;
					sc.setSctlId(sctl.getID());
					//sctl.setCalculatedCharge(BigDecimal.ZERO);
					transactionChargingService.confirmTheTransaction(sctl);
				}
			}
			log.info("ATMRegistrationHandler :: handle() END");
			return regResponse;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private Integer createsubscriberWithActiveBankPocket(CMSubscriberRegistration subscriberRegistration,String accountNumber,
			SubscriberMDNDAO subscriberMdnDao,KYCLevelDAO kycLevelDAO,SubscriberDAO subscriberDao,PocketTemplateDAO pocketTemplateDAO,
			PocketDAO pocketDAO,SubscriberGroupDao subGroupDAO,GroupDao groupDAO) throws ISOException {
		log.info("ATMRegistrationHandler :: createsubscriberWithActiveBankPocket BEGINS");
		Subscriber subscriber = new Subscriber();
		SubscriberMDN subscriberMDN = new SubscriberMDN();
		Integer regResponse = registerSubscriber(subscriber, subscriberMDN, subscriberRegistration, null,null, null, subscriberMdnDao, kycLevelDAO, pocketTemplateDAO, subscriberDao, pocketDAO,subGroupDAO, groupDAO,accountNumber);
		log.info("ATMRegistrationHandler :: createsubscriberWithActiveBankPocket ENDS :: regResponse = "+regResponse);
		return regResponse;
		}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private Integer registerSubscriber(Subscriber subscriber,
			SubscriberMDN subscriberMDN,
			CMSubscriberRegistration subscriberRegistration, Pocket epocket,
			String oneTimePin, Partner registeringPartner,SubscriberMDNDAO subscriberMdnDao,KYCLevelDAO kycLevelDAO,
			PocketTemplateDAO pocketTemplateDAO,SubscriberDAO subscriberDAO,PocketDAO pocketDao,SubscriberGroupDao subGroupDAO,GroupDao groupDAO,String accountNumber) throws ISOException {
		log.info("ATMRegistrationHandler :: registerSubscriber BEGINS");
		SubscriberMDN existingSubscriberMDN = subscriberMdnDao.getByMDN(subscriberRegistration.getMDN());
		if (existingSubscriberMDN == null ) {
			log.info("ATMRegistrationHandler :: registerSubscriber filling mandatory details");
			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);
			String createdByName = null;
			createdByName = subscriberRegistration.getFirstName();
			subscriber.setFirstName(subscriberRegistration.getFirstName());
			subscriber.setLastName(subscriberRegistration.getLastName());
			subscriber.setDateOfBirth(subscriberRegistration.getDateOfBirth());
			String mothersMaidenName = "MothersMaidenName";
			subscriber.setSecurityQuestion(mothersMaidenName);
			if (subscriberRegistration.getMothersMaidenName() != null) {
				subscriber.setSecurityAnswer(subscriberRegistration
						.getMothersMaidenName());
			}
			subscriber.setDetailsRequired(CmFinoFIX.Boolean_True);
			subscriber.setRegistrationMedium(CmFinoFIX.RegistrationMedium_ATM);
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriber.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriber.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			subscriber.setTimezone(CmFinoFIX.Timezone_UTC);
			subscriber.setStatusTime(new Timestamp());
			subscriber.setCreatedBy(createdByName);
			subscriber.setUpdatedBy(createdByName);
			subscriber.setCreateTime(new Timestamp());
			KYCLevel kycLevel = kycLevelDAO.getByKycLevel(ConfigurationUtil
					.getIntialKyclevel());
			if (kycLevel == null ) {
				return CmFinoFIX.NotificationCode_InvalidKYCLevel;
			}
			subscriber.setKYCLevelByKYCLevel(kycLevel);
			Long groupID = null;
			Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
			if(subscriberGroups != null && !subscriberGroups.isEmpty())
			{
				SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
				groupID = subscriberGroup.getGroup().getID();
			}
			Long kycLevelNo = null;
			if(null != subscriber.getUpgradableKYCLevel())
			{
				kycLevelNo = subscriber.getUpgradableKYCLevel();
			}
			else
			{
				kycLevelNo = subscriber.getKYCLevelByKYCLevel().getKYCLevel();
			}
			PocketTemplate emoneyTemplate = null;
			if (!kycLevel.getKYCLevel().equals(
					subscriberRegistration.getKYCLevel())) {
				kycLevel = kycLevelDAO.getByKycLevel(subscriberRegistration.getKYCLevel());
				if (kycLevel == null) {
					return CmFinoFIX.NotificationCode_InvalidKYCLevel;
				}
				subscriber.setUpgradableKYCLevel(subscriberRegistration
						.getKYCLevel());
				subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Upgradable);
			} else {
				subscriber.setUpgradeState(CmFinoFIX.UpgradeState_none);
			}
			int pocketStatus = CmFinoFIX.PocketStatus_Initialized;
			subscriber.setAppliedBy(createdByName);
			subscriber.setAppliedTime(new Timestamp());
			subscriber.setDetailsRequired(true);
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setMDN(subscriberRegistration.getMDN());
			subscriberMDN.setApplicationID(subscriberRegistration
					.getApplicationID());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriberMDN.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriberMDN.setStatusTime(new Timestamp());
			subscriberMDN.setCreatedBy(createdByName);
			subscriberMDN.setCreateTime(new Timestamp());
			subscriberMDN.setUpdatedBy(createdByName);
			setOTPToSubscriber(subscriberMDN, oneTimePin);
			//moved code begins
			PocketTemplate bankPocketTemplate = PocketService.getPocketTemplateFromPocketTemplateConfig(Long.parseLong(CmFinoFIX.RecordType_SubscriberFullyBanked.toString()), true, CmFinoFIX.PocketType_BankAccount, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
			if(bankPocketTemplate == null)
	         {
				msg.set(39, GetConstantCodes.FAILURE);
				return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
	         }

	         log.info("ATMRegistrationHandler :: registerSubscriber creating  bank pocket");
	         String calcPIN = null; 
	         try{ 
 	 	         calcPIN = MfinoUtil.modifyPINForStoring(subscriberMDN.getMDN(), subscriberRegistration.getPin()); 
 	 	         log.info("ATMRegistrationHanlder :: handle calcPIN = " + calcPIN);
 	 	         } catch(Exception e) 
 	 	         { 
 	 	         log.error("Error during PIN conversion "+e); 
				 msg.set(39, GetConstantCodes.FAILURE); 
				 return CmFinoFIX.NotificationCode_Failure; 
				} 
				subscriberMDN.setDigestedPIN(calcPIN);
				subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
		        subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
		        KYCLevel kyclevel = null;
		        kyclevel = kycLevelDAO.getByKycLevel(subscriber.getUpgradableKYCLevel());
		        Timestamp timeStamp = new Timestamp();
		        subscriber.setKYCLevelByKYCLevel(kyclevel);
		        //subscriber.setIDExiparetionTime(new Timestamp(idExpirationDate));
				subscriber.setUpgradableKYCLevel(null);
				subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Approved);
				subscriber.setApproveOrRejectComment(MessageText._("Upgrade N Approved by System"));
				subscriber.setApprovedOrRejectedBy("System");
				subscriber.setApproveOrRejectTime(timeStamp);
				subscriber.setActivationTime(timeStamp);
				subscriber.setFirstName("Simobicustomer");
				subscriber.setLastName("Simobicustomer");
				subscriberMDN.setActivationTime(timeStamp);
				log.info("ATMRegistrationHandler :: registerSubscriber saving subscriber record with MDN "+subscriberMDN.getMDN());
				subscriber.setTimezone(CmFinoFIX.Timezone_West_Indonesia_Time);
				subscriberDAO.save(subscriber);
				subscriberMdnDao.save(subscriberMDN);
				if(subscriberGroups == null || subscriberGroups.isEmpty()){
				SubscriberGroup subGroup = new SubscriberGroup(); 
					if(null!=groupDAO.getById(1L)){
						subGroup.setSubscriber(subscriber);
						subGroup.setGroup(groupDAO.getById(1L)); 
						subGroupDAO.save(subGroup); 
					}
				}
			//moved code ends
			
			Pocket bankPocket = PocketService.createPocket(bankPocketTemplate, subscriberMDN, CmFinoFIX.PocketStatus_Active, true,accountNumber);
		   if(ConfigurationUtil.getIsEMoneyPocketRequired() == true){
			String cardPan = null;
			try {
				cardPan = PocketService
						.generateSVAEMoney16DigitCardPAN(subscriberMDN.getMDN());
			} catch (Exception e) {
				log.error("Cardpan creation failed", e);
			}
			epocket.setID(PocketService.createPocket(emoneyTemplate,
					subscriberMDN, pocketStatus, true, cardPan).getID());
			}
			return CmFinoFIX.ResponseCode_Success;
		}
		return CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Source;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void fillSubscriberMandatoryFields(Subscriber subscriber) {
		if (subscriber.getmFinoServiceProviderByMSPID() == null) {
			MfinoServiceProviderDAO mfinoServiceProviderDAO = DAOFactory
					.getInstance().getMfinoServiceProviderDAO();
			subscriber.setmFinoServiceProviderByMSPID(mfinoServiceProviderDAO
					.getById(1));
		}
		if (subscriber.getLanguage() == null) {
			subscriber.setLanguage(CmFinoFIX.Language_English);
		}
		if (subscriber.getCurrency() == null) {
			subscriber.setCurrency(SystemParametersUtil
					.getString(SystemParameterKeys.DEFAULT_CURRENCY_CODE));
		}
		if (subscriber.getRestrictions() == null) {
			subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		}
		if (subscriber.getType() == null) {
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
		}
		if (subscriber.getCompany() == null) {
			CompanyDAO companyDAO = DAOFactory.getInstance().getCompanyDAO();
			subscriber.setCompany(companyDAO.getById(1));
		}

	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void fillSubscriberMDNMandatoryFields(
			SubscriberMDN subscriberMDN) {
		if (subscriberMDN.getAuthenticationPhrase() == null) {
			subscriberMDN.setAuthenticationPhrase("mFino");
		}
		if (subscriberMDN.getRestrictions() == null) {
			subscriberMDN
					.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		}
		if (subscriberMDN.getWrongPINCount() == null) {
			subscriberMDN.setWrongPINCount(0);
		}
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void setOTPToSubscriber(SubscriberMDN subscriberMDN,
			String oneTimePin) {
		if (subscriberMDN != null && oneTimePin != null) {
			String digestPin1 = MfinoUtil.calculateDigestPin(
					subscriberMDN.getMDN(), oneTimePin);
			subscriberMDN.setOTP(digestPin1);
			subscriberMDN.setOTPExpirationTime(new Timestamp(DateUtil.addHours(
					new Date(), SystemParametersUtil.getInteger(SystemParameterKeys.OTP_TIMEOUT_DURATION))));
		}
	}

	public HibernateTransactionManager getHtm() {
		return htm;
	}

	public void setHtm(HibernateTransactionManager htm) {
		this.htm = htm;
	}

}

