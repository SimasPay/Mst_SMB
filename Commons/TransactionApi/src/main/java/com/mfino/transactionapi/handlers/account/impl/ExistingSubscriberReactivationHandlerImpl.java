package com.mfino.transactionapi.handlers.account.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivation;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMSubscriberNotification;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.ExistingSubscriberReactivationHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.ReactivationXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

@Service("ExistingSubscriberReactivationHandlerImpl")
public class ExistingSubscriberReactivationHandlerImpl extends FIXMessageHandler implements ExistingSubscriberReactivationHandler{

	private static Logger log = LoggerFactory.getLogger(ExistingSubscriberReactivationHandlerImpl.class);
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
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
	
	public Result handle(TransactionDetails transactionDetails) {

		log.info("ExistingSubscriberReactivationHandlerImpl :: Handling Subscriber Reactivation webapi request for mdn "+transactionDetails.getSourceMDN());

		Transaction transaction=null;
		ChannelCode	channelCode= transactionDetails.getCc();
		CMExistingSubscriberReactivation subscriberReactivation = new CMExistingSubscriberReactivation();
		
		ServiceCharge serviceCharge = new ServiceCharge();
		XMLResult result 			= new ReactivationXMLResult();

		boolean isHttps = transactionDetails.isHttps();
		subscriberReactivation.setChannelCode(channelCode.getChannelcode());
 		subscriberReactivation.setSourceMDN(transactionDetails.getSourceMDN());
		subscriberReactivation.setPin(transactionDetails.getSourcePIN());
		subscriberReactivation.setSourceApplication(new Integer(String.valueOf(channelCode.getChannelsourceapplication())));
		subscriberReactivation.setSourceCardPAN(transactionDetails.getCardPAN());
		subscriberReactivation.setNewPin(transactionDetails.getNewPIN());
		subscriberReactivation.setConfirmPin(transactionDetails.getConfirmPIN());
  		subscriberReactivation.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
  		
		
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ExistingSubscriberReactivation, subscriberReactivation.DumpFields());
		result.setSourceMessage(subscriberReactivation);
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());
		
 		try{
 			String clearPin = transactionDetails.getSourcePIN();
 			subscriberReactivation.setPin(clearPin);
 			String clearCardPan = transactionDetails.getCardPAN();
 			subscriberReactivation.setSourceCardPAN(clearCardPan);
 		}
 		catch(Exception e){
 			log.error("Exception occured while decrypting pin ");
 			e.printStackTrace();
 			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing);
 			return result;
 		}
		
		Pocket srcpocket = pocketService.getDefaultPocket(sourceMDN, "2");

		subscriberReactivation.setSourcePocketID(srcpocket.getId().longValue());
		subscriberReactivation.setTransactionID(transactionsLog.getId().longValue());

		log.info("ExistingSubscriberReactivationHandler::Handle "+transactionDetails.getSourcePocketId());
 	
		serviceCharge.setSourceMDN(subscriberReactivation.getSourceMDN());
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(channelCode.getId().longValue());
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_REACTIVATION);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(transactionsLog.getId().longValue());
		serviceCharge.setTransactionIdentifier(subscriberReactivation.getTransactionIdentifier());

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
		
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		subscriberReactivation.setServiceChargeTransactionLogID(sctl.getId().longValue());

		log.info("ExistingSubscriberReactivationHandlerImpl :: Sending request to backend for processing");

		CFIXMsg response = super.process(subscriberReactivation);

		log.info("ExistingSubscriberReactivationHandlerImpl :: Recieved response from backend");

		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (transactionResponse.isResult() && sctl!=null) {  //need to check the success return value
		
			subscriberReactivation.setSourceCardPAN(transactionResponse.getSourceCardPAN());
			Integer code=subscriberServiceExtended.ReactivateSubscriber(subscriberReactivation,isHttps);
	
			if(code.equals(CmFinoFIX.NotificationCode_BOBPocketActivationCompleted)){
				result.setActivityStatus(BOOL_TRUE);
				addCompanyANDLanguageToResult(sourceMDN,result);	
				
				if (sctl != null) {
					sctl.setCalculatedcharge(BigDecimal.ZERO);
					transactionChargingService.completeTheTransaction(sctl);
				}
				result.setSctlID(sctl.getId().longValue());
				result.setMultixResponse(response);
				result.setMessage(transactionResponse.getMessage());
				return result;	
			}
			else{
				Notification notification = notificationService.getByNoticationCode(code);
				String notificationName = null;
				
				if(notification != null){
					notificationName = notification.getCodename();
				}else{
					log.error("Could not find the failure notification code: "+code);
				}
				result.setActivityStatus(BOOL_FALSE);
				transactionChargingService.failTheTransaction(sctl, MessageText._("Reactivation Failed. Notification Code: "+code+" NotificationName: "+notificationName));
				result.setNotificationCode(code);
				return result;
			}
		}
		else {
			String errorMsg = ((CMJSError) response).getErrorDescription();
			// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
			if (errorMsg.length() > 255) {
				errorMsg = errorMsg.substring(0, 255);
			}
			if(sctl!=null){
				String code = transactionResponse.getCode();
				
				Notification notification = notificationService.getByNoticationCode(Integer.parseInt(code));
				String notificationName = null;
				if(notification != null){
					notificationName = notification.getCodename();
				}else{
					log.error("Could not find the failure notification code: "+code);
				}
				transactionChargingService.failTheTransaction(sctl, MessageText._("Reactivation Failed. Notification Code: "+code+" NotificationName: "+notificationName));
			}
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}
 	}
	public CFIXMsg handleResponse(CFIXMsg pMsg) {
		 if ((pMsg!=null) && (pMsg instanceof CmFinoFIX.CMSubscriberNotification)) {
			log.info("NewSubscriberActivationHandler::handleResponse pMsg is CMSubscriberNotification " );
			CMSubscriberNotification errMsg = (CMSubscriberNotification) pMsg;
			}
		return super.handleResponse(pMsg);
	}
}
