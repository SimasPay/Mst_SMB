package com.mfino.bsim.iso8583.handlers;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMChangePin;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.handlers.hsm.HSMHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.validators.SubscriberValidator;

public class ATMChangePinHandler extends FIXMessageHandler implements IATMChangePinHandler {

	private static Logger log = LoggerFactory.getLogger(ATMChangePinHandler.class);
	private HibernateTransactionManager htm;
	private static ATMChangePinHandler atmChangePinHandler;
	private SubscriberService subscriberService ;
	private TransactionLogService transactionLogService;
	private SubscriberMdnService subscriberMdnService;
	private TransactionChargingService transactionChargingService ;
	private SubscriberServiceExtended subscriberServiceExtended ;
	private MfinoUtilService mfinoUtilService;
	private SystemParametersService systemParametersService;
	private NotificationService notificationService ;
	private PocketService pocketService;
	
	public static ATMChangePinHandler createInstance(){
		if(atmChangePinHandler==null){
			atmChangePinHandler = new ATMChangePinHandler();
		}
		
		return atmChangePinHandler;
	}
	
	public static ATMChangePinHandler getInstance(){
		if(atmChangePinHandler==null){
			throw new RuntimeException("Instance is not already created");
		}
		return atmChangePinHandler;
	}

	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Integer handle(ISOMsg msg,Session session) throws Exception {
			log.info("ATMChangePinHandler :: handle() BEGIN");
			String sourceMDN=subscriberService.normalizeMDN(msg.getString("61"));
			String accountNumber=msg.getString("2");
			accountNumber = accountNumber.substring(0, accountNumber.length()-1);
			log.info("ATMRegistrationHandler :: handle() accountNumber="+accountNumber);
			String encryptedPin = msg.getString("52");
			HSMHandler handler = new HSMHandler();
			CMChangePin changePin = new CMChangePin();
			String cc = CmFinoFIX.SourceApplication_ATM.toString();
			changePin.setChannelCode(cc);
			changePin.setSourceMDN(sourceMDN);
			changePin.setSourceApplication(CmFinoFIX.SourceApplication_ATM);
			Integer regResponse = CmFinoFIX.ResponseCode_Failure;
			SubscriberValidator subscribervalidator = new SubscriberValidator(changePin.getSourceMDN());
			Integer validationResult = subscribervalidator.validate();
			SubscriberMDN subscriberMDN = subscriberMdnService.getByMDN(changePin.getSourceMDN());
			Subscriber subscriber = subscriberMDN.getSubscriber();
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				msg.set(39, GetConstantCodes.FAILURE);
				return validationResult;
			}else if(validationResult.equals(CmFinoFIX.ResponseCode_Success)){
				regResponse = CmFinoFIX.ResponseCode_Success;
			}
			/*
			SourceMDNValidator smdnv = new SourceMDNValidator(subscriberMDN);
			validationResult = smdnv.validate();
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				msg.set(39, GetConstantCodes.FAILURE);
				regResponse = validationResult;
			}else{
				msg.set(39,GetConstantCodes.SUCCESS);
				regResponse = CmFinoFIX.ResponseCode_Success;
			}
			*/
			

			// if status is either active,suspended or inactive (not absolute locked) in both subscriber and subscribermdn then only allow txn else fail it
			if(!((subscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_Active)) || 
					(subscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_Suspend)) ||
					(subscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_Initialized)) ||
					(subscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_InActive)))){
				msg.set(39,GetConstantCodes.REJECT);
				return CmFinoFIX.NotificationCode_MDNIsRestricted;
			}
			if(!((subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Active)) || 
					(subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Suspend)) ||
					(subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_Initialized)) ||
					(subscriber.getStatus().equals(CmFinoFIX.SubscriberStatus_InActive)))){
				msg.set(39,GetConstantCodes.REJECT);
				return CmFinoFIX.NotificationCode_MDNIsRestricted;
			}
			TransactionsLog transactionsLog = null;
			msg.set(39, GetConstantCodes.FAILURE);
			log.info("Handling ChangePin atm request");
			transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ChangePin,changePin.DumpFields());
			changePin.setTransactionID(transactionsLog.getID());
			Transaction transactionDetails = null;
			ServiceCharge sc = new ServiceCharge();
			sc.setSourceMDN(changePin.getSourceMDN());
			sc.setDestMDN(changePin.getSourceMDN());
			if(StringUtils.isNotBlank(cc))
			sc.setChannelCodeId(Long.parseLong(cc));
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CHANGEPIN);
			sc.setTransactionAmount(BigDecimal.ZERO);
			sc.setTransactionLogId(transactionsLog.getID());
			sc.setTransactionIdentifier(changePin.getTransactionIdentifier());
			try{
				transactionDetails =transactionChargingService.getCharge(sc);
			}catch (InvalidServiceException e) {
				msg.set(39, GetConstantCodes.FAILURE);
				log.error(e.getMessage()); // return null so as to not construct sms in this case
				return null;
			} catch (InvalidChargeDefinitionException e) {
				msg.set(39, GetConstantCodes.FAILURE);
				log.error(e.getMessage());
				return null;
			}

			if(regResponse.equals(CmFinoFIX.ResponseCode_Success)){
			log.info("ATMChangePinHandler :: handle() Passed all the validations . Hence sending request to HSM");
			String decryptedPin="";
			try{
			decryptedPin=handler.generateOffsetForATMRequest(sourceMDN,accountNumber, encryptedPin);
			if(null==decryptedPin){
				log.info("ATMRegistratinoHandler :: handle invalid Pin block");
				msg.set(39,GetConstantCodes.FAILURE);
				return CmFinoFIX.NotificationCode_Failure;
			}
			}catch(Exception e){
				log.error(e.getMessage());
				msg.set(39,GetConstantCodes.FAILURE);
				return CmFinoFIX.NotificationCode_Failure;
			}
			String clearPIN = decryptedPin.substring(0, systemParametersService.getPinLength());
			log.info("ATMChangePinHandler :: handle encryptedPin = " + encryptedPin);
			changePin.setConfirmPin(clearPIN);
			changePin.setNewPin(clearPIN);
			String calcPIN = null; 
			try{ 
	        calcPIN = mfinoUtilService.modifyPINForStoring(changePin.getSourceMDN(), changePin.getNewPin()); 
	        log.info("ATMChangePinHandler :: handle calcPIN = " + calcPIN);
	        } catch(Exception e) 
	        { 
	        log.error("Error during PIN conversion "+e); 
		    msg.set(39, GetConstantCodes.FAILURE); 
		    return CmFinoFIX.NotificationCode_Failure; 
	        } 
			subscriberMDN.setDigestedPIN(calcPIN); 
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
			subscriberMDN.setStatusTime(new Timestamp());
			subscriberMDN.setActivationTime(new Timestamp());
			subscriber.setActivationTime(new Timestamp());
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
			subscriber.setStatusTime(new Timestamp());
			subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
			subscriberMDN.setOTP(null);
			subscriberMDN.setOTPExpirationTime(null);
			//since pin is changed set authtoken to null so that after next login it is updated
			subscriberMDN.setAuthorizationToken(null);
			subscriberService.saveSubscriber(subscriber);
			subscriberMdnService.saveSubscriberMDN(subscriberMDN);
			Pocket bankPocket = subscriberService.getDefaultPocket(subscriberMDN.getID(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
			if(!(bankPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active))){
				log.info("ATMChangePinHandler :: handle () Activating Bank Pocket");
				bankPocket.setStatusTime(new Timestamp());
				bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
				pocketService.save(bankPocket);
			}
			}
			ServiceChargeTransactionLog sctl = transactionDetails.getServiceChargeTransactionLog();
			Notification  notification;
			String notificationName = "";
			if (!regResponse.equals(CmFinoFIX.ResponseCode_Success)) {
				msg.set(39,GetConstantCodes.FAILURE);
				notification = notificationService.getByNoticationCode(regResponse);
				if(notification != null){
					notificationName = notification.getCodeName();
				}else{
					log.error("Could not find the failure notification code: "+regResponse);
				}
				transactionChargingService.failTheTransaction(sctl, MessageText._("Change Pin failed. Notification Code: "+regResponse+" NotificationName: "+notificationName));
			}else{
				msg.set(39,GetConstantCodes.SUCCESS);
				if (sctl != null) {
					regResponse=CmFinoFIX.NotificationCode_ChangePINCompleted;
					sc.setSctlId(sctl.getID());
					//sctl.setCalculatedCharge(BigDecimal.ZERO);
					transactionChargingService.confirmTheTransaction(sctl);
				}
			}
			log.info("ATMChangePinHandler :: handle() END");
			return regResponse;

	}


	public HibernateTransactionManager getHtm() {
		return htm;
	}

	public void setHtm(HibernateTransactionManager htm) {
		this.htm = htm;
	}
	
	public SubscriberMdnService getSubscriberMdnService() {
		return subscriberMdnService;
	}

	public void setSubscriberMdnService(SubscriberMdnService subscriberMdnService) {
		this.subscriberMdnService = subscriberMdnService;
	}
	
	public TransactionLogService getTransactionLogService() {
		return transactionLogService;
	}

	public void setTransactionLogService(TransactionLogService transactionLogService) {
		this.transactionLogService = transactionLogService;
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

	public MfinoUtilService getMfinoUtilService() {
		return mfinoUtilService;
	}

	public void setMfinoUtilService(MfinoUtilService mfinoUtilService) {
		this.mfinoUtilService = mfinoUtilService;
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
	
	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
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
	

}

