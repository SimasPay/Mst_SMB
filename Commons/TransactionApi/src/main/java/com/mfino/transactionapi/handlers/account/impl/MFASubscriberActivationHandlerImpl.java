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
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberActivation;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.service.AgentService;
import com.mfino.service.MFAService;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
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
	
	@Autowired
	@Qualifier("AgentServiceImpl")
	private AgentService agentService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	public Result handle(TransactionDetails transactionDetails) {
		
		boolean isHttps = 		transactionDetails.isHttps();
		Date dateOfBirth = 		transactionDetails.getDateOfBirth();
		Long parentTxnId = 		transactionDetails.getParentTxnId();
		String serviceName = 	transactionDetails.getServiceName();
		String transactionOTP = transactionDetails.getTransactionOTP();
		ChannelCode channelCode   =	transactionDetails.getCc();
		String mfaTransactionType = transactionDetails.getMfaTransaction();
		boolean isSimaspayActivity=transactionDetails.isSimpaspayActivity();
		
		CMSubscriberActivation subscriberActivation = new CMSubscriberActivation();
		
		subscriberActivation.setChannelCode(channelCode.getChannelcode());
		subscriberActivation.setPin(transactionDetails.getNewPIN());
		subscriberActivation.setOTP(transactionDetails.getActivationOTP());
		subscriberActivation.setSourceMDN(transactionDetails.getSourceMDN());
		subscriberActivation.setSourceApplication(new Integer(String.valueOf(channelCode.getChannelsourceapplication())));
		subscriberActivation.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

		if(dateOfBirth!=null){
			subscriberActivation.setDateOfBirth(new Timestamp(dateOfBirth));
		}
		TransactionLog transactionsLog = null;
		ServiceChargeTxnLog sctl = null;

		log.info("Handling subscriber services activation webapi request");
		ActivationXMLResult result = new ActivationXMLResult();


		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberActivation, subscriberActivation.DumpFields());
		
		result.setSourceMessage(subscriberActivation);
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());
		
		subscriberActivation.setTransactionID(transactionsLog.getId().longValue());

		SubscriberMdn subscribermdn = subscriberMdnService.getByMDN(subscriberActivation.getSourceMDN());
		Integer code = null;
		
		if(null == subscribermdn) {
		
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			return result;
			
		} else {
			
			if(!CmFinoFIX.SubscriberStatus_Initialized.equals(subscribermdn.getStatus())) {
				
				result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberStatusDoesNotEnableActivation);
				return result;
				
			}
		}
		
		ChannelCode cc = transactionDetails.getCc();
		
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
			serviceCharge.setChannelCodeId(channelCode.getId().longValue());
			serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
			serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_ACTIVATION);
			serviceCharge.setTransactionAmount(BigDecimal.ZERO);
			serviceCharge.setTransactionLogId(transactionsLog.getId().longValue());
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
			}
			
			//Validating OTP for SimaspayActivity
			try{
			
				if(isSimaspayActivity){
					
					boolean isHashedPin = ConfigurationUtil.getuseHashedPIN();
					code=subscriberServiceExtended.validateOTP(subscriberActivation,isHttps,isHashedPin);
					
					if(!code.equals(CmFinoFIX.NotificationCode_OTPValidationSuccessful)){
						result.setNotificationCode(CmFinoFIX.NotificationCode_OTPInvalid);
		 				return result;
					}
				}
			}catch(Exception e){
				log.error("Error in Validating OTP",e);
				result.setNotificationCode(CmFinoFIX.NotificationCode_OTPInvalid);
 				return result;
			}
			//Validating OTP for SimaspayActivity

			sctl = transaction.getServiceChargeTransactionLog();
			result.setSctlID(sctl.getId().longValue());
			result.setMfaMode("None");
			
			if(mfaService.isMFATransaction(ServiceAndTransactionConstants.SERVICE_ACCOUNT, ServiceAndTransactionConstants.TRANSACTION_ACTIVATION, cc.getId().longValue()) == true){
			
				result.setMfaMode("OTP");
				mfaService.handleMFATransaction(sctl.getId().longValue(), subscriberActivation.getSourceMDN());
			}
			
			if(sctl!=null){
				transactionChargingService.saveServiceTransactionLog(sctl);
			}
			
			result.setName(subscribermdn.getSubscriber().getFirstname());
			result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberActivationInquirySuccessful);
			
 			return result;			
		}
		
		if((mfaTransactionType.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_CONFIRM))){
			
			ServiceChargeTxnLog sctlForMFA=sctlService.getBySCTLID(parentTxnId);

			sctl=sctlForMFA;

			if(mfaService.isMFATransaction(ServiceAndTransactionConstants.SERVICE_ACCOUNT, ServiceAndTransactionConstants.TRANSACTION_ACTIVATION, cc.getId().longValue()) == true){
				
				if(transactionOTP == null || !(mfaService.isValidOTP(transactionOTP,sctlForMFA.getId().longValue(), subscriberActivation.getSourceMDN()))){
						result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidMFAOTP);
						return result;
				}
			}
		}
		
		subscriberActivation.setServiceChargeTransactionLogID(sctl.getId().longValue());
		boolean isHashedPin = ConfigurationUtil.getuseHashedPIN();
		
		if(subscribermdn!=null){
			Subscriber subscriber=subscribermdn.getSubscriber();
			int int_subscriberType = new Integer(String.valueOf(subscriber.getType()));
			if(int_subscriberType==CmFinoFIX.SubscriberType_Partner){
				NotificationWrapper wrapper = agentService.activeAgent(subscriberActivation,isHttps, ConfigurationUtil.getuseHashedPIN());		
				 code= wrapper.getCode();
			}else{
				code=subscriberServiceExtended.activeSubscriber(subscriberActivation,isHttps,isHashedPin);
			}
		}
		
		
		if(code.equals(CmFinoFIX.NotificationCode_BOBPocketActivationCompleted)){
			
			result.setActivityStatus(BOOL_TRUE);
			addCompanyANDLanguageToResult(subscribermdn, result);	
		 
			if (sctl != null) {
				sctl.setCalculatedcharge(BigDecimal.ZERO);

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
				notificationName = notification.get(0).getCodename();
			}else{
				log.error("Could not find the failure notification code: "+code);
			}

			transactionChargingService.failTheTransaction(sctl, MessageText._("Activation Falied.Notification Code: "+code+" NotificationName: "+notificationName));
		}

		if(subscribermdn != null)
		{
			subscribermdn.setWrongpincount(0);
			
			subscriberMdnService.saveSubscriberMDN(subscribermdn);
 
			boolean isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();			
			if(isEMoneyPocketRequired==false){
				log.info("isEmoneyPocketRequired = " + isEMoneyPocketRequired + " hence activating and approving");
				Set<Pocket> pockets = subscribermdn.getPockets();
				boolean bankPocketFound = false;
				Pocket bankPocket = null;

				for (Pocket pocket : pockets) {
					if (!bankPocketFound
							&& pocket.getPocketTemplateByPockettemplateid().getType() == (CmFinoFIX.PocketType_BankAccount.longValue())
							&& pocket.getCardpan() != null
							&& (pocket.getStatus() == CmFinoFIX.PocketStatus_Active.longValue() 
									|| pocket.getStatus() == CmFinoFIX.PocketStatus_Initialized.longValue())) {
						bankPocketFound = true;
						bankPocket = pocket;
						break;
					}
				}
				
				log.info("Bank Pocket Found = " + bankPocketFound);
				Subscriber subscriber = subscribermdn.getSubscriber();
				String subscriberName = subscriber.getFirstname();
				if(bankPocketFound)
				{
					bankPocket.setActivationtime(new Timestamp());
					bankPocket.setIsdefault(CmFinoFIX.Boolean_True);
					bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
					bankPocket.setStatustime(new Timestamp());
					bankPocket.setUpdatedby(subscriberName);

					pocketService.save(bankPocket);

					log.info("SubscriberActivation : bankPocket activation id:"
							+ bankPocket.getId() 
							+ " subscriberid"
							+ subscriber.getId());
				}
				subscriber.setUpgradablekyclevel(null);
                subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Approved);
				subscriber.setApproveorrejectcomment("Approved for No Emoney");
				subscriber.setApprovedorrejectedby("System");
				subscriber.setApproveorrejecttime(new Timestamp());
				
				result.setName(subscriber.getFirstname());
			}
			
			result.setNotificationCode(code);
			result.setSctlID(sctl.getId().longValue());
 		}
		else 
		{
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
		}
		result.setSctlID(sctl.getId().longValue());
		return result;
	}
}
