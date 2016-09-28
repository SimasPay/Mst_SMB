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
import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
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
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.SubscriberActivationHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ActivationXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;

/*
 * 
 * @author Maruthi
 */
@Service("SubscriberActivationHandlerImpl")
public class SubscriberActivationHandlerImpl extends FIXMessageHandler implements SubscriberActivationHandler{
	private static Logger log = LoggerFactory.getLogger(SubscriberActivationHandlerImpl.class);
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	public Result handle(TransactionDetails transactionDetails) {
		
		Date dateOfBirth = null;
		CMSubscriberActivation	subscriberActivation = new CMSubscriberActivation();
		ChannelCode	cc = transactionDetails.getCc();
		boolean isHttps= transactionDetails.isHttps();
 		subscriberActivation.setPin(transactionDetails.getNewPIN());
		subscriberActivation.setSourceMDN(transactionDetails.getSourceMDN());
		subscriberActivation.setOTP(transactionDetails.getActivationOTP());
		subscriberActivation.setSourceApplication(new Integer(String.valueOf(cc.getChannelsourceapplication())));
		subscriberActivation.setChannelCode(cc.getChannelcode());
		subscriberActivation.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		if (transactionDetails.getDateOfBirth() != null) {
			dateOfBirth = transactionDetails.getDateOfBirth();
			subscriberActivation.setDateOfBirth(new Timestamp(dateOfBirth));
		}
 		TransactionsLog transactionsLog = null;
		log.info("Handling subscriber services activation webapi request");

		XMLResult result = new ActivationXMLResult();
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());


		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberActivation, subscriberActivation.DumpFields());
		
		result.setSourceMessage(subscriberActivation);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());
		subscriberActivation.setTransactionID(transactionsLog.getID());


		Transaction transaction = null;
		
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(subscriberActivation.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_ACTIVATION);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionsLog.getID());
		sc.setTransactionIdentifier(subscriberActivation.getTransactionIdentifier());

		try{
			transaction =transactionChargingService.getCharge(sc);
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
 			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
 			return result;
		}
		
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		subscriberActivation.setServiceChargeTransactionLogID(sctl.getID());
		boolean isEMoneyPocketRequired = ConfigurationUtil.getIsEMoneyPocketRequired();

		boolean isHashedPin = ConfigurationUtil.getuseHashedPIN(); 
		Integer code=subscriberServiceExtended.activeSubscriber(subscriberActivation,isHttps,isHashedPin);	
		if(code.equals(CmFinoFIX.NotificationCode_BOBPocketActivationCompleted)){
			result.setActivityStatus(BOOL_TRUE);

//			addCompanyANDLanguageToResult(sourceMDN, result);	
 
			if (sctl != null) {
				sctl.setCalculatedCharge(BigDecimal.ZERO);
				transactionChargingService.completeTheTransaction(sctl);
			}
		}else{
			result.setActivityStatus(BOOL_FALSE);

			NotificationQuery notificationQuery = new NotificationQuery();
			notificationQuery.setNotificationCode(code);
			notificationQuery.setNotificationMethod(CmFinoFIX.NotificationMethod_Web);
		
			List<Notification> notification=notificationService.getByQuery(notificationQuery);
			String notificationName = null;
		
			if(CollectionUtils.isNotEmpty(notification)){
				notificationName = notification.get(0).getCodename();
			}else{
				log.error("Could not find the failure notification code: "+code);
			}
			
			transactionChargingService.failTheTransaction(sctl, MessageText._("Activation Falied.Notification Code: "+code+" NotificationName: "+notificationName));
		}

		if(sourceMDN != null)
		{
			sourceMDN.setWrongpincount(0);
			subscriberMdnService.saveSubscriberMDN(sourceMDN);
 
			if(isEMoneyPocketRequired==false){
				log.info("isEmoneyPocketRequired = " + isEMoneyPocketRequired + " hence activating and approving");
				Set<Pocket> pockets = sourceMDN.getPockets();
				boolean bankPocketFound = false;
				Pocket bankPocket = null;
 			
				for (Pocket pocket : pockets) {
					if (!bankPocketFound 
							&& pocket.getPocketTemplate().getType() == (CmFinoFIX.PocketType_BankAccount.longValue())
							&& pocket.getCardpan() != null
							&& (pocket.getStatus() == (CmFinoFIX.PocketStatus_Active.longValue()) || 
								pocket.getStatus() == (CmFinoFIX.PocketStatus_Initialized.longValue()))) {
						bankPocketFound = true;
						bankPocket = pocket;
						break;
					}
				}
				
				log.info("Bank Pocket Found = " + bankPocketFound);
				Subscriber subscriber = sourceMDN.getSubscriber();
				String subscriberName = subscriber.getFirstname();
				if(bankPocketFound)
				{
					bankPocket.setActivationtime(new Timestamp());
					bankPocket.setIsdefault(Short.valueOf("1"));
					bankPocket.setStatus(CmFinoFIX.PocketStatus_Active);
					bankPocket.setStatustime(new Timestamp());
					bankPocket.setUpdatedby(subscriberName);
					pocketService.save(bankPocket);
					log.info("SubscriberActivation : bankPocket activation id:"
							+ bankPocket.getId() + " subscriberid"
							+ subscriber.getId());
				}
				subscriber.setUpgradablekyclevel(null);
                subscriber.setUpgradestate(CmFinoFIX.UpgradeState_Approved.longValue());
				subscriber.setApproveorrejectcomment("Approved for No Emoney");
				subscriber.setApprovedorrejectedby("System");
				subscriber.setApproveorrejecttime(new Timestamp());
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
