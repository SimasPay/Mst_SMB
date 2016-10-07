/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistration;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToNonRegistered;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.NonRegisteredTransferInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;

/**
 * Transfer Inquiry Handler for cases where Source or Destination is Unregistered MDN
 * 
 * @author Chaitanya
 *
 */
@Service("NonRegisteredTransferInquiryHandlerImpl")
public class NonRegisteredTransferInquiryHandlerImpl extends FIXMessageHandler implements NonRegisteredTransferInquiryHandler{

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
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
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	public Result handle(TransactionDetails transactionDetails)
	{
		log.info("Extracting data from transactionDetails in NonRegisteredTransferInquiryHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		ChannelCode channelCode = transactionDetails.getCc();
		CMTransferInquiryToNonRegistered transferInquiry = new CMTransferInquiryToNonRegistered();
		transferInquiry.setDestMDN(transactionDetails.getDestMDN());
		transferInquiry.setAmount(transactionDetails.getAmount());
		transferInquiry.setPin(transactionDetails.getSourcePIN());
		transferInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		transferInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		transferInquiry.setSourceApplication((int)channelCode.getChannelsourceapplication());
		transferInquiry.setChannelCode(channelCode.getChannelcode());
		transferInquiry.setServiceName(transactionDetails.getServiceName());
		transferInquiry.setSourcePocketID(transactionDetails.getSrcPocketId());
		transferInquiry.setRemarks(transactionDetails.getDescription());
		transferInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		XMLResult result = new TransferInquiryXMLResult();
		
		SubscriberMdn srcMdn = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
		Subscriber srcSub = srcMdn.getSubscriber();
		KYCLevel srcKyc = srcSub.getKycLevel();
		if(srcKyc.getKyclevel().equals(new Long(CmFinoFIX.SubscriberKYCLevel_NoKyc))){
			log.info(String.format("TransferToUnregistered is Failed as the the Source Subscriber(%s) KycLevel is NoKyc",srcMdn.getMdn()));
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyTransferFromNoKycSubscriberNotAllowed);
			return result;
		}
		
		
		if(ServiceAndTransactionConstants.TRANSACTION_CASH_IN_TO_AGENT_INQUIRY.equals(transactionDetails.getTransactionName())){
			transferInquiry.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
			transferInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Cash_In_To_Agent);
		}
		log.info("Handling NonRegisteredTransferInquiry WebAPI request::From " + transactionDetails.getSourceMDN() + " To " + 
				transactionDetails.getDestMDN() + " For Amount = " + transactionDetails.getAmount());

		
		
		//Create new subscriber with NonRegistered Status
		boolean requiresName = ConfigurationUtil.getRequiresNameWhenMoneyTransferedToUnregisterd();
		if((!requiresName) || (StringUtils.isNotBlank(transactionDetails.getFirstName()) && StringUtils.isNotBlank(transactionDetails.getLastName())))
		{
			transferInquiry.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
			String sourceMsg = ServiceAndTransactionConstants.MESSAGE_SUB_BULK_TRANSFER.equals(transactionDetails.getSourceMessage()) ? 
					transactionDetails.getSourceMessage() : ServiceAndTransactionConstants.MESSAGE_TRANSFER_UNREGISTERED;
			transferInquiry.setSourceMessage(sourceMsg);
			SubscriberMdn destMDN = subscriberMdnService.getByMDN(transactionDetails.getDestMDN());
			if(destMDN==null)
			{
				log.info("The destinationMDN: "+transactionDetails.getDestMDN()+"does not exist in our system");
				Subscriber subscriber = new Subscriber();
				SubscriberMdn subscriberMDN = new SubscriberMdn();
				Pocket epocket = new Pocket();
				String oneTimePin = null;
				//String oneTimePin = MfinoUtil.generateOTP();
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_NotRegistered);

				CMSubscriberRegistration subscriberRegistration = new CMSubscriberRegistration();
				subscriberRegistration.setSourceMDN(transactionDetails.getSourceMDN());
				subscriberRegistration.setMDN(subscriberService.normalizeMDN(transactionDetails.getDestMDN()));
				subscriberRegistration.setFirstName(transactionDetails.getFirstName());
				subscriberRegistration.setLastName(transactionDetails.getLastName());
				subscriberRegistration.setKYCLevel(1L);
				subscriberRegistration.setChannelCode(transferInquiry.getChannelCode());
				subscriberRegistration.setSourceApplication(transferInquiry.getSourceApplication());
				subscriberRegistration.setPin(transactionDetails.getSourcePIN());
				subscriberRegistration.setSubscriberStatus(CmFinoFIX.SubscriberStatus_NotRegistered);
				
				log.info("Registering the destination mdn...");
				Integer regResponse = subscriberServiceExtended.registerSubscriber(subscriber, subscriberMDN, subscriberRegistration, 
						epocket,oneTimePin,null);
				transferInquiry.setDestPocketID(epocket.getId().longValue());
				if (!regResponse.equals(CmFinoFIX.ResponseCode_Success)) 
				{
					//Send Failure notification
					log.error("Subscriber creation failed: "+regResponse);
					result.setNotificationCode(regResponse);
					return result;
				}
				else 
				{
					result = handleTransfer(transferInquiry,transactionDetails);
				}
			}
			else if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(destMDN.getSubscriber().getStatus()))
			{
				log.info("The destinationMDN: "+destMDN.getMdn()+"is an unregistered subscriber");
				Pocket destPocket = pocketService.getDefaultPocket(destMDN, transactionDetails.getDestPocketCode());

				Integer validationResult = transactionApiValidationService.validateDestinationPocketForUnregistered(destPocket);
				if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
					log.error("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
					result.setNotificationCode(validationResult);
					return result;
				}

				transferInquiry.setDestPocketID(destPocket.getId().longValue());
				result = handleTransfer(transferInquiry,transactionDetails);
			}
			else
			{
				log.error("The non registered transfer inquiry has failed due to unsupported status for unregistered");
				//return failure notification
				result.setNotificationCode(CmFinoFIX.NotificationCode_UnSupportedStatusForUnRegistered);
				result.setDestinationMDN(transferInquiry.getDestMDN());
				return result;
			}
		}
		else
		{
			log.error("The non registered transfer inquiry has failed due to Invalid inputs for unregistered");
			//return failure notification
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidInputsForUnRegistered);
			result.setDestinationMDN(transferInquiry.getDestMDN());
			return result;
		}

		return result;
	}

	private XMLResult handleTransfer(CMTransferInquiryToNonRegistered transferInquiry,TransactionDetails transactionDetails)
	{
		XMLResult result = new TransferInquiryXMLResult();
		Transaction transaction = null;

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_TransferInquiryToNonRegistered, transferInquiry.DumpFields());
		transferInquiry.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(transferInquiry);
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setDestinationMDN(transferInquiry.getDestMDN());
		
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(transferInquiry.getSourceMDN());
		addCompanyANDLanguageToResult(srcSubscriberMDN, result);


		log.info("creating the serviceCharge object....");
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(transferInquiry.getSourceMDN());
		sc.setDestMDN(transferInquiry.getDestMDN());
		sc.setChannelCodeId(transactionDetails.getCc().getId().longValue());
		sc.setServiceName(transferInquiry.getServiceName());
		if (ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER_INQUIRY.equals(transactionDetails.getTransactionName())) {
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER);
		} 
		else {
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_UNREGISTERED);
		}
		sc.setTransactionAmount(transferInquiry.getAmount());
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		sc.setTransactionIdentifier(transferInquiry.getTransactionIdentifier());
		sc.setDescription(transactionDetails.getDescription());
		
		try{
			transaction =transactionChargingService.getCharge(sc);
			transferInquiry.setAmount(transaction.getAmountToCredit());
			transferInquiry.setCharges(transaction.getAmountTowardsCharges());

		} catch (InvalidServiceException ise) {
			log.error("Exception occured in getting charges",ise);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);//change to service not found
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		transferInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		log.info("sending the transferInquiry request to backend for processing");
		CFIXMsg response = super.process(transferInquiry);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionid(BigDecimal.valueOf(transactionResponse.getTransactionId()));
			transferInquiry.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}

		transactionChargingService.updateTransactionStatus(transactionResponse, sctl);
		
		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setSctlID(sctl.getId().longValue());
		result.setUnRegistered(true);
		return result;
	}
}
