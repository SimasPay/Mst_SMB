package com.mfino.bsim.iso8583.handlers;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.SubsUpgradeBalanceLogDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.Groups;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Notification;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberUpgradeBalanceLog;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistration;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.handlers.hsm.HSMHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.GroupService;
import com.mfino.service.KYCLevelService;
import com.mfino.service.MfinoServiceProviderService;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberGroupService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.util.ConfigurationUtil;

public class ATMRegistrationHandler extends FIXMessageHandler implements IATMRegistrationHandler {

	private static Logger log = LoggerFactory.getLogger(ATMRegistrationHandler.class);
	private ISOMsg	msg;
	private HibernateTransactionManager htm;
	private static ATMRegistrationHandler atmRegistrationHandler;
	private SubscriberService subscriberService;
	private SystemParametersService systemParametersService;
	private TransactionChargingService transactionChargingService ;
	private TransactionLogService transactionLogService;
	private NotificationService notificationService ;
	private SubscriberServiceExtended subscriberServiceExtended ;
	private PocketService pocketService;
	private MfinoUtilService mfinoUtilService;
	private SubscriberMdnService subscriberMdnService; 
	private KYCLevelService kycLevelService;
	private GroupService groupService;
	private SubscriberGroupService subGroupService;
	private MfinoServiceProviderService mFinoServiceProviderService;



	public MfinoServiceProviderService getmFinoServiceProviderService() {
		return mFinoServiceProviderService;
	}

	public void setmFinoServiceProviderService(
			MfinoServiceProviderService mFinoServiceProviderService) {
		this.mFinoServiceProviderService = mFinoServiceProviderService;
	}

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
		log.info("ATMRegistrationHandler :: handle() BEGIN");
		String sourceMDN=subscriberService.normalizeMDN(msg.getString("61"));
		if((sourceMDN.length() < systemParametersService.getInteger(SystemParameterKeys.MIN_MDN_LENGTH_WITH_COUNTRYCODE)) ||
				(sourceMDN.length() > systemParametersService.getInteger(SystemParameterKeys.MAX_MDN_LENGTH_WITH_COUNTRYCODE)) ){
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
		String clearPIN = decryptedPin.substring(0, systemParametersService.getPinLength());
		//String clearPIN = "";
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
		TransactionLog transactionsLog = null;
		log.info("Handling subscriberRegistration atm request");
		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberRegistration,subscriberRegistration.DumpFields());
		subscriberRegistration.setTransactionID(transactionsLog.getId().longValue());
		
		SubscriberMdn existingSubscriberMDN = subscriberMdnService.getByMDN(subscriberRegistration.getMDN());
		Transaction transactionDetails = null;
		
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(subscriberRegistration.getSourceMDN());
		sc.setDestMDN(subscriberRegistration.getMDN());
		
		if(StringUtils.isNotBlank(cc))
			sc.setChannelCodeId(Long.parseLong(cc));
		
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		
		if (existingSubscriberMDN == null ) {
			
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION);
			
		} else {
		
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUB_UPGEADE_TO_BANKING);
		}
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		sc.setTransactionIdentifier(subscriberRegistration.getTransactionIdentifier());
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
		regResponse=createsubscriberWithActiveBankPocket(subscriberRegistration,sourceAccountNumber);
		ServiceChargeTxnLog sctl = transactionDetails.getServiceChargeTransactionLog();
		subscriberRegistration.setServiceChargeTransactionLogID(sctl.getId().longValue());  
		Notification  notification;
		String notificationName = "";

		if (!regResponse.equals(CmFinoFIX.ResponseCode_Success)) {
			msg.set(39,GetConstantCodes.FAILURE);
			notification = notificationService.getByNoticationCode(regResponse);
			if(notification != null){
				notificationName = notification.getCodename();
			}else{
				log.error("Could not find the failure notification code: "+regResponse);
			}
			transactionChargingService.failTheTransaction(sctl, MessageText._("Subscriber Registration failed. Notification Code: "+regResponse+" NotificationName: "+notificationName));
		}else{
			msg.set(39,GetConstantCodes.SUCCESS);
			if (sctl != null) {
				regResponse=CmFinoFIX.NotificationCode_SubscriberRegistrationSuccessfulToAgent;
				sc.setSctlId(sctl.getId().longValue());
				//sctl.setCalculatedCharge(BigDecimal.ZERO);
				transactionChargingService.confirmTheTransaction(sctl);
			}
		}
		log.info("ATMRegistrationHandler :: handle() END");
		return regResponse;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private Integer createsubscriberWithActiveBankPocket(CMSubscriberRegistration subscriberRegistration,String accountNumber) throws ISOException {
		log.info("ATMRegistrationHandler :: createsubscriberWithActiveBankPocket BEGINS");
		Subscriber subscriber = new Subscriber();
		SubscriberMdn subscriberMDN = new SubscriberMdn();
		Integer regResponse = registerSubscriber(subscriber, subscriberMDN, subscriberRegistration, null,null, null,accountNumber);
		log.info("ATMRegistrationHandler :: createsubscriberWithActiveBankPocket ENDS :: regResponse = "+regResponse);
		return regResponse;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private Integer registerSubscriber(Subscriber subscriber,
			SubscriberMdn subscriberMDN,
			CMSubscriberRegistration subscriberRegistration, Pocket epocket,
			String oneTimePin, Partner registeringPartner,String accountNumber) throws ISOException {
		log.info("ATMRegistrationHandler :: registerSubscriber BEGINS");
		SubscriberMdn existingSubscriberMDN = subscriberMdnService.getByMDN(subscriberRegistration.getMDN());
		if (existingSubscriberMDN == null ) {
			log.info("ATMRegistrationHandler :: registerSubscriber filling mandatory details");
			fillSubscriberMandatoryFields(subscriber);
			fillSubscriberMDNMandatoryFields(subscriberMDN);
			String createdByName = null;
			createdByName = subscriberRegistration.getFirstName();
			subscriber.setFirstname(subscriberRegistration.getFirstName());
			subscriber.setLastname(subscriberRegistration.getLastName());
			subscriber.setDateofbirth(subscriberRegistration.getDateOfBirth());
			String mothersMaidenName = "MothersMaidenName";
			subscriber.setSecurityquestion(mothersMaidenName);
			if (subscriberRegistration.getMothersMaidenName() != null) {
				subscriber.setSecurityanswer(subscriberRegistration
						.getMothersMaidenName());
			}
			subscriber.setDetailsrequired(CmFinoFIX.Boolean_True);
			subscriber.setRegistrationmedium(CmFinoFIX.RegistrationMedium_ATM);
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriber.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS);
			subscriber.setTimezone(CmFinoFIX.Timezone_UTC);
			subscriber.setStatustime(new Timestamp());
			subscriber.setCreatedby(createdByName);
			subscriber.setUpdatedby(createdByName);
			subscriber.setCreatetime(new Timestamp());
			KycLevel kycLevel = kycLevelService.getByKycLevel(ConfigurationUtil
					.getIntialKyclevel());
			if (kycLevel == null ) {
				return CmFinoFIX.NotificationCode_InvalidKYCLevel;
			}
			
			subscriber.setKycLevel(kycLevel);
			Long groupID = null;
			SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
			List<SubscriberGroups> subscriberGroups = subscriberGroupDao.getAllBySubscriberID(subscriber.getId());
			if(subscriberGroups != null && !subscriberGroups.isEmpty())
			{
				SubscriberGroups subscriberGroup = subscriberGroups.iterator().next();
				groupID = subscriberGroup.getGroupid();
			}
			Long kycLevelNo = null;
			if(null != subscriber.getUpgradablekyclevel())
			{
				kycLevelNo = subscriber.getUpgradablekyclevel().longValue();
			}
			else
			{
				kycLevelNo = subscriber.getKycLevel().getKyclevel().longValue();
			}
			if (!kycLevel.getKyclevel().equals(
					subscriberRegistration.getKYCLevel())) {
				kycLevel = kycLevelService.getByKycLevel(subscriberRegistration.getKYCLevel());
				if (kycLevel == null) {
					return CmFinoFIX.NotificationCode_InvalidKYCLevel;
				}
				subscriber.setUpgradablekyclevel(subscriberRegistration
						.getKYCLevel());
				subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Upgradable);
			} else {
				subscriber.setUpgradestate(CmFinoFIX.UpgradeState_none);
			}
			subscriber.setAppliedby(createdByName);
			subscriber.setAppliedtime(new Timestamp());
			subscriber.setDetailsrequired(CmFinoFIX.Boolean_True);
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setMdn(subscriberRegistration.getMDN());
			subscriberMDN.setApplicationid(subscriberRegistration
					.getApplicationID());
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			if (subscriberRegistration.getSubscriberStatus() != null) {
				subscriberMDN.setStatus(subscriberRegistration
						.getSubscriberStatus());
			}
			subscriberMDN.setStatustime(new Timestamp());
			subscriberMDN.setCreatedby(createdByName);
			subscriberMDN.setCreatetime(new Timestamp());
			subscriberMDN.setUpdatedby(createdByName);
			//moved code begins
			PocketTemplate bankPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(Long.parseLong(CmFinoFIX.RecordType_SubscriberFullyBanked.toString()), true, CmFinoFIX.PocketType_BankAccount, CmFinoFIX.SubscriberType_Subscriber, null, 1L);
			if(bankPocketTemplate == null)
			{
				msg.set(39, GetConstantCodes.FAILURE);
				return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
			}

			log.info("ATMRegistrationHandler :: registerSubscriber creating  bank pocket");
			String calcPIN = null; 
			try{ 
				calcPIN = mfinoUtilService.modifyPINForStoring(subscriberMDN.getMdn(), subscriberRegistration.getPin()); 
				log.info("ATMRegistrationHanlder :: handle calcPIN = " + calcPIN);
			} catch(Exception e) 
			{ 
				log.error("Error during PIN conversion "+e); 
				msg.set(39, GetConstantCodes.FAILURE); 
				return CmFinoFIX.ResponseCode_Failure; 
			} 
			subscriberMDN.setDigestedpin(calcPIN);
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
			KycLevel kyclevel = null;
			kyclevel = kycLevelService.getByKycLevel(subscriber.getUpgradablekyclevel().longValue());
			Timestamp timeStamp = new Timestamp();
			subscriber.setKycLevel(kyclevel);
			subscriber.setUpgradablekyclevel(null);
			subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Approved);
			subscriber.setApproveorrejectcomment(MessageText._("Upgrade N Approved by System"));
			subscriber.setApprovedorrejectedby("System");
			subscriber.setApproveorrejecttime(timeStamp);
			subscriber.setActivationtime(timeStamp);
			subscriber.setFirstname("Simobicustomer");
			subscriber.setLastname("Simobicustomer");
			subscriberMDN.setActivationtime(timeStamp);
			subscriberMDN.setLastapppinchange(new Timestamp());
			log.info("ATMRegistrationHandler :: registerSubscriber saving subscriber record with MDN "+subscriberMDN.getMdn());
			subscriber.setTimezone(CmFinoFIX.Timezone_West_Indonesia_Time);
			subscriberService.saveSubscriber(subscriber);
			subscriberMdnService.saveSubscriberMDN(subscriberMDN);
			if(subscriberGroups == null || subscriberGroups.isEmpty()){
				SubscriberGroups subGroup = new SubscriberGroups(); 
				if(null!=groupService.getById(1L)){
					subGroup.setSubscriber(subscriber);
					subGroup.setGroupid(1L); 
					subGroupService.save(subGroup); 
				}
			}
			pocketService.createPocket(bankPocketTemplate, subscriberMDN, CmFinoFIX.PocketStatus_Active, true,accountNumber);
			return CmFinoFIX.ResponseCode_Success;
		
		} else {
			
			boolean isUnregistered = isRegistrationForUnRegistered(existingSubscriberMDN);
			
			subscriber = existingSubscriberMDN.getSubscriber();
			
			if(subscriber != null && null != subscriber.getKycLevel()) {
				
				KycLevel subKycLevel = subscriber.getKycLevel();
				
				if(subKycLevel != null && 
						((subKycLevel.getKyclevel().intValue() == CmFinoFIX.SubscriberKYCLevel_NoKyc.intValue()) || subKycLevel.getKyclevel().intValue() == CmFinoFIX.SubscriberKYCLevel_UnBanked.intValue() || isUnregistered)
						&& subscriber.getStatus() == CmFinoFIX.SubscriberStatus_Active) {
					
					KycLevel kycLevel = kycLevelService.getByKycLevel(new Long(CmFinoFIX.SubscriberKYCLevel_FullyBanked));
					
					String mothersMaidenName = "MothersMaidenName";
					subscriber.setSecurityquestion(mothersMaidenName);
					
					if (subscriberRegistration.getMothersMaidenName() != null) {
						
						subscriber.setSecurityanswer(subscriberRegistration.getMothersMaidenName());
					}
					
					if(isUnregistered) {
						
						subscriber.setRegistrationmedium(CmFinoFIX.RegistrationMedium_ATM);
					}
					
					subscriber.setDateofbirth(subscriberRegistration.getDateOfBirth());
					subscriber.setKycLevel(kycLevel);
					subscriber.setUpgradablekyclevel(null);
					subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Approved);
					subscriber.setApproveorrejectcomment(MessageText._("Upgrade N Approved by System"));
					subscriber.setApprovedorrejectedby("System");
					
					String calcPIN = null; 
					try{ 
						
						calcPIN = mfinoUtilService.modifyPINForStoring(existingSubscriberMDN.getMdn(), subscriberRegistration.getPin()); 
						log.info("ATMRegistrationHanlder :: handle calcPIN = " + calcPIN);
						
					} catch(Exception e) { 
						
						log.error("Error during PIN conversion "+e); 
						msg.set(39, GetConstantCodes.FAILURE); 
						return CmFinoFIX.ResponseCode_Failure; 
					} 
					
					existingSubscriberMDN.setDigestedpin(calcPIN);
					existingSubscriberMDN.setLastapppinchange(new Timestamp());
					existingSubscriberMDN.setApplicationid(subscriberRegistration.getApplicationID());
					
					subscriberService.saveSubscriber(subscriber);
					subscriberMdnService.saveSubscriberMDN(existingSubscriberMDN);
					
					GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
					Groups defaultGroup = groupDao.getSystemGroup();
					Long groupID = defaultGroup.getId();
					
					PocketTemplate emoneyPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(kycLevel.getId(), true, CmFinoFIX.PocketType_SVA, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
					
					if (emoneyPocketTemplate == null) {
						
						msg.set(39, GetConstantCodes.FAILURE);
						return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
					}
					
					Pocket defaultPocket = pocketService.getDefaultPocket(existingSubscriberMDN, String.valueOf(CmFinoFIX.PocketType_SVA));
					PocketTemplate existingPocketTemplate = defaultPocket.getPocketTemplateByPockettemplateid();
					
					defaultPocket.setPocketTemplateByOldpockettemplateid(existingPocketTemplate);
					defaultPocket.setPocketTemplateByPockettemplateid(emoneyPocketTemplate);
					defaultPocket.setStatus(CmFinoFIX.PocketStatus_Active);
					
					pocketService.save(defaultPocket);
					if(isUnregistered) {
						SubsUpgradeBalanceLogDAO subsUpgradeBalanceLogDAO = DAOFactory.getInstance().getSubsUpgradeBalanceLogDAO();
						SubscriberUpgradeBalanceLog subUpgradeBalanceLog = new SubscriberUpgradeBalanceLog();
						subUpgradeBalanceLog.setSubscriberId(subscriber.getId());
						subUpgradeBalanceLog.setPockatBalance(defaultPocket.getCurrentbalance());
						subUpgradeBalanceLog.setTxnDate(new Timestamp());
						subUpgradeBalanceLog.setCreatedby("ATM");
						subUpgradeBalanceLog.setCreatetime(new Timestamp());
						subUpgradeBalanceLog.setUpdatedby("ATM");
						subUpgradeBalanceLog.setLastupdatetime(new Timestamp());
						subsUpgradeBalanceLogDAO.save(subUpgradeBalanceLog);
					}
					PocketTemplate bankPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(Long.parseLong(CmFinoFIX.RecordType_SubscriberFullyBanked.toString()), true, CmFinoFIX.PocketType_BankAccount, CmFinoFIX.SubscriberType_Subscriber, null, 1L);
					
					if(bankPocketTemplate == null) {
						
						msg.set(39, GetConstantCodes.FAILURE);
						return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
					}
					
					if(isUnregistered) {
						
						UnRegisteredTxnInfoQuery query = new UnRegisteredTxnInfoQuery();
						query.setSubscriberMDNID(subscriberMDN.getId());
						
						UnRegisteredTxnInfoDAO unregisteredDao = DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
						
						List<UnregisteredTxnInfo> unregisteredSubscriber = unregisteredDao.get(query);
						
						if(unregisteredSubscriber != null && unregisteredSubscriber.size() > 0) {
							
							for (UnregisteredTxnInfo txnInfo : unregisteredSubscriber) {
							
								txnInfo.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_SUBSCRIBER_ACTIVE);					
								unregisteredDao.save(txnInfo);
							}
						}
					}
					
					pocketService.createPocket(bankPocketTemplate, existingSubscriberMDN, CmFinoFIX.PocketStatus_Active, true,accountNumber);
					
					return CmFinoFIX.ResponseCode_Success;
				} else {
					
					msg.set(39, GetConstantCodes.FAILURE);
					return CmFinoFIX.NotificationCode_SourceSVAEMoneyPocketNotFound;
				}
				
			} else {
				
				msg.set(39, GetConstantCodes.FAILURE);
				return CmFinoFIX.NotificationCode_SourceSVAEMoneyPocketNotFound;
			}			
		}
	}

	private boolean isRegistrationForUnRegistered(SubscriberMdn subscriberMDN) {
		boolean isUnRegistered = false;
		if (subscriberMDN != null) {
			
			if (CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN.getSubscriber().getStatus())
					&& CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN.getStatus())) {
				
				return true;
			}
		}
		
		return isUnRegistered;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void fillSubscriberMandatoryFields(Subscriber subscriber) {
		if (subscriber.getMfinoServiceProvider() == null) {;
			subscriber.setMfinoServiceProvider(mFinoServiceProviderService.getMFSPbyID(1));
		}
		if (Long.valueOf(subscriber.getLanguage()) == null) {
			subscriber.setLanguage(CmFinoFIX.Language_English);
		}
		if (subscriber.getCurrency() == null) {
			subscriber.setCurrency(systemParametersService.getString(SystemParameterKeys.DEFAULT_CURRENCY_CODE));
		}
		if (Long.valueOf(subscriber.getRestrictions()) == null) {
			subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		}
		if (Long.valueOf(subscriber.getType()) == null) {
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
		}
		if (subscriber.getCompany() == null) {
			subscriber.setCompany(subscriberService.getDefaultCompanyForSubscriber());
		}

	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void fillSubscriberMDNMandatoryFields(
			SubscriberMdn subscriberMDN) {
		if (subscriberMDN.getAuthenticationphrase() == null) {
			subscriberMDN.setAuthenticationphrase("mFino");
		}
		if (Long.valueOf(subscriberMDN.getRestrictions())== null) {
			subscriberMDN
			.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		}
		if (Long.valueOf(subscriberMDN.getWrongpincount()) == null) {
			subscriberMDN.setWrongpincount(0);
		}
	}

	public HibernateTransactionManager getHtm() {
		return htm;
	}

	public void setHtm(HibernateTransactionManager htm) {
		this.htm = htm;
	}

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public SystemParametersService getSystemParametersService() {
		return systemParametersService;
	}

	public void setSystemParametersService(
			SystemParametersService systemParametersService) {
		this.systemParametersService = systemParametersService;
	}

	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public void setTransactionChargingService(
			TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}

	public SubscriberServiceExtended getSubscriberServiceExtended() {
		return subscriberServiceExtended;
	}

	public void setSubscriberServiceExtended(
			SubscriberServiceExtended subscriberServiceExtended) {
		this.subscriberServiceExtended = subscriberServiceExtended;
	}

	public TransactionLogService getTransactionLogService() {
		return transactionLogService;
	}

	public void setTransactionLogService(TransactionLogService transactionLogService) {
		this.transactionLogService = transactionLogService;
	}

	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
	}

	public MfinoUtilService getMfinoUtilService() {
		return mfinoUtilService;
	}

	public void setMfinoUtilService(MfinoUtilService mfinoUtilService) {
		this.mfinoUtilService = mfinoUtilService;
	}

	public SubscriberMdnService getSubscriberMdnService() {
		return subscriberMdnService;
	}

	public void setSubscriberMdnService(SubscriberMdnService subscriberMdnService) {
		this.subscriberMdnService = subscriberMdnService;
	}

	public KYCLevelService getKycLevelService() {
		return kycLevelService;
	}

	public void setKycLevelService(KYCLevelService kycLevelService) {
		this.kycLevelService = kycLevelService;
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public SubscriberGroupService getSubGroupService() {
		return subGroupService;
	}

	public void setSubGroupService(SubscriberGroupService subGroupService) {
		this.subGroupService = subGroupService;
	}
}


