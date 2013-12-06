package com.mfino.bsim.iso8583.handlers;

import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Notification;
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
import com.mfino.service.KYCLevelService;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.SMSService;
import com.mfino.util.SystemParametersUtil;

public class ATMRegistrationHandler extends FIXMessageHandler implements IATMRegistrationHandler{

	private static Logger log = LoggerFactory.getLogger(ATMRegistrationHandler.class);
	private SubscriberService subscriberService ;
	private TransactionLogService transactionLogService;
	private SubscriberMdnService subscriberMdnService;
	private PocketService pocketService ;
	private TransactionChargingService transactionChargingService ;
	private SubscriberServiceExtended subscriberServiceExtended ;
	private MfinoUtilService mfinoUtilService;
	private NotificationService notificationService ;
	private KYCLevelService kycLevelService;
	private SMSService smsService;
	private NotificationMessageParserService notificationMessageParserService;
	private SystemParametersService systemParametersService;
	private static ATMRegistrationHandler atmRegistrationHandler;
	private HibernateTransactionManager htm;
	private ISOMsg	msg;

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
	
	public Integer handle(ISOMsg msg) throws Exception {
			this.msg=msg;
			log.info("ATMRegistrationHandler :: handle() BEGIN");log.info("ATMRegistrationHandler :: handle() BEGIN");
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
				log.info("ATMRegistrationHandler :: handle invalid cifNumber received in de-48");
				msg.set(39,GetConstantCodes.FAILURE);
				return CmFinoFIX.NotificationCode_Failure;
			}else{
			cifNumber = msg.getString("48").substring(3,23);
			}
			String encryptedPin = msg.getString("52");
		/*
			ISOKeyStore ks = ISOKeyStore.getInstance();
			String workingKey = ks.getWorkingKey();
			String decryptedPin = TripleDES.decrypt(workingKey, encryptedPin);
			*/

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
			transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberRegistration,subscriberRegistration.DumpFields());
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
			ServiceChargeTransactionLog sctl = transactionDetails.getServiceChargeTransactionLog();
			subscriberRegistration.setServiceChargeTransactionLogID(sctl.getID());  
			Notification notification;
			String notificationName = "";
			if (!regResponse.equals(CmFinoFIX.ResponseCode_Success)) {
				msg.set(39,GetConstantCodes.FAILURE);
				notification = notificationService.getByNoticationCode(regResponse);
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


	private Integer createsubscriberWithActiveBankPocket(CMSubscriberRegistration subscriberRegistration,String accountNumber) throws ISOException {

		SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
        SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
        GroupDao groupDAO = DAOFactory.getInstance().getGroupDao();
        SubscriberGroupDao subGroupDAO = DAOFactory.getInstance().getSubscriberGroupDao();
		Subscriber subscriber = new Subscriber();
		SubscriberMDN subscriberMDN = new SubscriberMDN();
		Integer regResponse = subscriberServiceExtended.registerSubscriber(subscriber, subscriberMDN, subscriberRegistration, null,null, null);
		subscriberMDN = subscriberMdnService.getByMDN(subscriberRegistration.getMDN());
		//subscriber =subscriberService.getSubscriberbySubscriberId(subscriberMDN.getSubscriber().getID());
		subscriber = subscriberMDN.getSubscriber();
		//Long groupID = 1L;
		Long groupID = null;
		Set<SubscriberGroup> subscriberGroups = subscriber.getSubscriberGroupFromSubscriberID();
		if(subscriberGroups != null && !subscriberGroups.isEmpty())
		{
			SubscriberGroup subscriberGroup = subscriberGroups.iterator().next();
			groupID = subscriberGroup.getGroup().getID();
		}
		
		PocketTemplate bankPocketTemplate = pocketService.getPocketTemplateFromPocketTemplateConfig(Long.parseLong(CmFinoFIX.RecordType_SubscriberFullyBanked.toString()), true, CmFinoFIX.PocketType_BankAccount, CmFinoFIX.SubscriberType_Subscriber, null, groupID);
		if(bankPocketTemplate == null)
         {
			msg.set(39, GetConstantCodes.FAILURE);
			return CmFinoFIX.NotificationCode_DefaultPocketTemplateNotFound;
         }
         Pocket bankPocket = pocketService.createPocket(bankPocketTemplate, subscriberMDN, CmFinoFIX.PocketStatus_Active, true,accountNumber);
         String calcPIN = null;  
         try{  
        	 calcPIN = mfinoUtilService.modifyPINForStoring(subscriberMDN.getMDN(), subscriberRegistration.getPin());  
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
	        KYCLevel kyclevel = kycLevelService.getByKycLevel(subscriber.getUpgradableKYCLevel());
	        Timestamp timeStamp = new Timestamp();
	        subscriber.setKYCLevelByKYCLevel(kyclevel);
	        subscriber.setUpgradableKYCLevel(null);
			subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Approved);
			subscriber.setApproveOrRejectComment(MessageText._("Upgrade N Approved by System"));
			subscriber.setApprovedOrRejectedBy("System");
			subscriber.setApproveOrRejectTime(timeStamp);
			subscriber.setActivationTime(timeStamp);
			subscriber.setFirstName("Simobicustomer");
			subscriber.setLastName("Simobicustomer"); 
			subscriberMDN.setActivationTime(timeStamp);
	        subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriberMDN.setActivationTime(new Timestamp());
			subscriberDAO.save(subscriber);
			subscriberMDNDAO.save(subscriberMDN);
			if(subscriberGroups == null || subscriberGroups.isEmpty()){
				SubscriberGroup subGroup = new SubscriberGroup(); 
					if(null!=groupDAO.getById(1L)){
						subGroup.setSubscriber(subscriber);
						subGroup.setGroup(groupDAO.getById(1L)); 
						subGroupDAO.save(subGroup); 
					}
				}
			return regResponse;

		
	}
	
//	public static void main(String[] args) {
//		Random random = new Random();
//		while(true){
//		Long longValue = random.nextLong();
//		if(!(longValue>0)){
//			longValue = random.nextLong();
//		}
//		String randomText = String.valueOf(longValue);
//		System.out.println(randomText.substring(0, 12));
//		}
//	}
	

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public TransactionLogService getTransactionLogService() {
		return transactionLogService;
	}

	public void setTransactionLogService(TransactionLogService transactionLogService) {
		this.transactionLogService = transactionLogService;
	}

	public SubscriberMdnService getSubscriberMdnService() {
		return subscriberMdnService;
	}

	public void setSubscriberMdnService(SubscriberMdnService subscriberMdnService) {
		this.subscriberMdnService = subscriberMdnService;
	}

	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
	}

	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
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

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
	public MfinoUtilService getMfinoUtilService() {
		return mfinoUtilService;
	}

	public void setMfinoUtilService(MfinoUtilService mfinoUtilService) {
		this.mfinoUtilService = mfinoUtilService;
	}

	public HibernateTransactionManager getHtm() {
		return htm;
	}

	public void setHtm(HibernateTransactionManager htm) {
		this.htm = htm;
	}
	
	public KYCLevelService getKycLevelService() {
		return kycLevelService;
	}

	public void setKycLevelService(KYCLevelService kycLevelService) {
		this.kycLevelService = kycLevelService;
	}
	
	public NotificationMessageParserService getNotificationMessageParserService() {
		return notificationMessageParserService;
	}

	public void setNotificationMessageParserService(
			NotificationMessageParserService notificationMessageParserService) {
		this.notificationMessageParserService = notificationMessageParserService;
	}

	public SMSService getSmsService() {
		return smsService;
	}

	public void setSmsService(SMSService smsService) {
		this.smsService = smsService;
	}
	
	public SystemParametersService getSystemParametersService() {
		return systemParametersService;
	}

	public void setSystemParametersService(
			SystemParametersService systemParametersService) {
		this.systemParametersService = systemParametersService;
	}
}

