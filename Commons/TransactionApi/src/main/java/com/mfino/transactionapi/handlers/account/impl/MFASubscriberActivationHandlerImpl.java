package com.mfino.transactionapi.handlers.account.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.crypto.CryptographyService;
import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.ChannelCode;
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
import com.mfino.fix.CmFinoFIX.CMSubscriberActivation;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.MFASubscriberActivationHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ActivationXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

@Service("MFASubscriberActivationHandlerImpl")
public class MFASubscriberActivationHandlerImpl extends FIXMessageHandler implements MFASubscriberActivationHandler{
	private static Logger log = LoggerFactory.getLogger(SubscriberActivationHandlerImpl.class);
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	private String transactionName = ServiceAndTransactionConstants.TRANSACTION_ACTIVATION;

	public Result handle(TransactionDetails transactionDetails) {
		
		boolean isHttps = 		transactionDetails.isHttps();
		Date dateOfBirth = 		transactionDetails.getDateOfBirth();
		Long parentTxnId = 		transactionDetails.getParentTxnId();
		String serviceName = 	transactionDetails.getServiceName();
		String transactionOTP = transactionDetails.getTransactionOTP();
		ChannelCode channelCode   =	transactionDetails.getCc();
		String mfaTransactionType = transactionDetails.getMfaTransaction();
		
		CMSubscriberActivation subscriberActivation = new CMSubscriberActivation();
		
		subscriberActivation.setChannelCode(channelCode.getChannelCode());
		subscriberActivation.setPin(transactionDetails.getNewPIN());
		subscriberActivation.setOTP(transactionDetails.getActivationOTP());
		subscriberActivation.setSourceMDN(transactionDetails.getSourceMDN());
		subscriberActivation.setSourceApplication(channelCode.getChannelSourceApplication());
		subscriberActivation.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

		if(dateOfBirth!=null){
			subscriberActivation.setDateOfBirth(new Timestamp(dateOfBirth));
		}
		TransactionsLog transactionsLog = null;
		ServiceChargeTransactionLog sctl = null;

		log.info("Handling subscriber services activation webapi request");
		XMLResult result = new ActivationXMLResult();


		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberActivation, subscriberActivation.DumpFields());
		
		result.setSourceMessage(subscriberActivation);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());
		
		subscriberActivation.setTransactionID(transactionsLog.getID());

		SubscriberMDN subscribermdn = subscriberMdnService.getByMDN(subscriberActivation.getSourceMDN());
		Integer code = null;
		if(mfaTransactionType.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_INQUIRY)){
			String newpin = null;
	 		try{
	 			newpin = CryptographyService.decryptWithPrivateKey(subscriberActivation.getPin());
	 		}
	 		catch(Exception e){
	 			log.error("Exception occured while decrypting pin ", e);
	 			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing);
	 			return result; 
	 		}
			if(MfinoUtil.containsSequenceOfDigits(newpin)){
				log.info("The pin is not strong enough for subscribermdn "+subscriberActivation.getSourceMDN() + " for sequence of digits");
				result.setNotificationCode(CmFinoFIX.NotificationCode_SequenceNumberAsPin);
				return result;
			} else if(MfinoUtil.containsRepetitiveDigits(newpin)){
				log.info("The pin is not strong enough for subscribermdn "+subscriberActivation.getSourceMDN() + " for repetitive digits");
				result.setNotificationCode(CmFinoFIX.NotificationCode_SameNumbersAsPin);
				return result;
			}

			Transaction transaction = null;
			ServiceCharge serviceCharge = new ServiceCharge();
			
			serviceCharge.setSourceMDN(subscriberActivation.getSourceMDN());
			serviceCharge.setDestMDN(null);
			serviceCharge.setChannelCodeId(channelCode.getID());
			serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
			serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_ACTIVATION);
			serviceCharge.setTransactionAmount(BigDecimal.ZERO);
			serviceCharge.setTransactionLogId(transactionsLog.getID());
			serviceCharge.setTransactionIdentifier(subscriberActivation.getTransactionIdentifier());

			try{
				transaction =transactionChargingService.getCharge(serviceCharge);

			}catch (InvalidServiceException e) {
				log.error("Exception occured in getting charges",e);
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
 				return result;
			
			} catch (InvalidChargeDefinitionException e) {
				log.error(e.getMessage());
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
 				return result;
			}catch(Exception e){
				log.error("Error in Validating OTP",e);
				result.setNotificationCode(CmFinoFIX.NotificationCode_OTPInvalid);
 				return result;
			}
			//Validating OTP for SimaspayActivity

			sctl = transaction.getServiceChargeTransactionLog();
			result.setSctlID(sctl.getID());
			result.setMfaMode("None");
			
			result.setMfaMode("OTP");
			mfaService.handleMFATransaction(sctl.getID(), subscriberActivation.getSourceMDN());
		
			if(sctl!=null){
				transactionChargingService.saveServiceTransactionLog(sctl);
			}
			result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberActivationInquirySuccessful);
 			return result;			
		}
		
		if((mfaTransactionType.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_CONFIRM))){
			
			ServiceChargeTransactionLog sctlForMFA=sctlService.getBySCTLID(parentTxnId);

			sctl=sctlForMFA;

			if(transactionOTP == null || !(mfaService.isValidOTP(transactionOTP,sctlForMFA.getID(), subscriberActivation.getSourceMDN()))){
					result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidData);
					return result;
				}			
		}
		
		subscriberActivation.setServiceChargeTransactionLogID(sctl.getID());
		boolean isHashedPin = ConfigurationUtil.getuseHashedPIN();		
		
		code=subscriberServiceExtended.activeSubscriber(subscriberActivation,isHttps,isHashedPin);
		if(code.equals(CmFinoFIX.NotificationCode_BOBPocketActivationCompleted)){
			
			result.setActivityStatus(BOOL_TRUE);
			addCompanyANDLanguageToResult(subscribermdn, result);	
		 
			if (sctl != null) {
				sctl.setCalculatedCharge(BigDecimal.ZERO);

				transactionChargingService.completeTheTransaction(sctl);
			}
		}else{
			
			result.setActivityStatus(BOOL_FALSE);
			NotificationQuery notificationQuery = new NotificationQuery();
			notificationQuery.setNotificationCode(code);
			notificationQuery.setNotificationMethod(CmFinoFIX.NotificationMethod_Web);
			List<Notification> notification = notificationService.getByQuery(notificationQuery);

			String notificationName = null;
			if(CollectionUtils.isNotEmpty(notification)){
				notificationName = notification.get(0).getCodeName();
			}else{
				log.error("Could not find the failure notification code: "+code);
			}

			transactionChargingService.failTheTransaction(sctl, MessageText._("Activation Falied.Notification Code: "+code+" NotificationName: "+notificationName));
		}

		if(subscribermdn != null)
		{
			subscribermdn.setWrongPINCount(0);
			subscriberMdnService.saveSubscriberMDN(subscribermdn);
 
			boolean isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();			
			if(isEMoneyPocketRequired==false){
				log.info("isEmoneyPocketRequired = " + isEMoneyPocketRequired + " hence activating and approving");
				Set<Pocket> pockets = subscribermdn.getPocketFromMDNID();
				boolean bankPocketFound = false;
				Pocket bankPocket = null;

				for (Pocket pocket : pockets) {
					if (!bankPocketFound
							&& pocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)
							&& pocket.getCardPAN() != null
							&& (pocket.getStatus().equals(CmFinoFIX.PocketStatus_Active) 
									|| pocket.getStatus().equals(CmFinoFIX.PocketStatus_Initialized))) {
						bankPocketFound = true;
						bankPocket = pocket;
						break;
					}
				}
				
				log.info("Bank Pocket Found = " + bankPocketFound);
				Subscriber subscriber = subscribermdn.getSubscriber();
				String subscriberName = subscriber.getFirstName();
				if(bankPocketFound)
				{
					bankPocket.setActivationTime(new Timestamp());
					bankPocket.setIsDefault(true);
					bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
					bankPocket.setStatusTime(new Timestamp());
					bankPocket.setUpdatedBy(subscriberName);

					pocketService.save(bankPocket);

					log.info("SubscriberActivation : bankPocket activation id:"
							+ bankPocket.getID() 
							+ " subscriberid"
							+ subscriber.getID());
				}
				subscriber.setUpgradableKYCLevel(null);
                subscriber.setUpgradeState(CmFinoFIX.UpgradeState_Approved);
				subscriber.setApproveOrRejectComment("Approved for No Emoney");
				subscriber.setApprovedOrRejectedBy("System");
				subscriber.setApproveOrRejectTime(new Timestamp());
			}
			result.setNotificationCode(code);
			result.setSctlID(sctl.getID());
 		}
		else 
		{
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
		}
		result.setSctlID(sctl.getID());
		return result;
	}

}
