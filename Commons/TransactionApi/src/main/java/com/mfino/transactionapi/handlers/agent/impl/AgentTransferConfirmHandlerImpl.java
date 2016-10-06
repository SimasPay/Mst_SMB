/**
 * 
 */
package com.mfino.transactionapi.handlers.agent.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMTransferToNonRegistered;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.MFAService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.agent.AgentTransferConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author srinivaas
 * 
 */
@Service("AgentTransferConfirmHandlerImpl")
public class AgentTransferConfirmHandlerImpl extends FIXMessageHandler implements AgentTransferConfirmHandler{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

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
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	public Result handle(TransactionDetails transactionDetails) {

		log.info("BEGIN handle :: AgentTransferConfirmHandlerImpl");
		
		String SourcePocketCode = transactionDetails.getSourcePocketCode();
		String DestPocketCode	= transactionDetails.getDestPocketCode();
		String sourcePocketId	= transactionDetails.getSourcePocketId();
		String destPocketId  	= transactionDetails.getDestPocketId();
		Long parentTrxnId 		= transactionDetails.getParentTxnId();
		String mfaOneTimeOTP 	= transactionDetails.getTransactionOTP();

		XMLResult result = new MoneyTransferXMLResult();		
		ChannelCode cc = transactionDetails.getCc();
 		ServiceChargeTxnLog sctlForMFA = sctlService.getByTransactionLogId(parentTrxnId);

		if(mfaOneTimeOTP == null || !(mfaService.isValidOTP(mfaOneTimeOTP,sctlForMFA.getId().longValue(), transactionDetails.getSourceMDN()))){
			log.info("Invalid OTP Entered");
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidMFAOTP);
			result.setSctlID(sctlForMFA.getId().longValue());
			result.setMessage("Invalid OTP Entered");
			return result;
		}

		CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
		transferConfirmation.setSourceMDN(transactionDetails.getSourceMDN());
		transferConfirmation.setDestMDN(transactionDetails.getDestMDN());
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setTransferID(transactionDetails.getTransferId());
		transferConfirmation.setConfirmed(Boolean.parseBoolean(transactionDetails.getConfirmString()));
		transferConfirmation.setSourceApplication(new Integer(String.valueOf(cc.getChannelsourceapplication())));
		transferConfirmation.setChannelCode(cc.getChannelcode());
		transferConfirmation.setParentTransactionID(transactionDetails.getParentTxnId());
		transferConfirmation.setDestinationBankAccountNo(transactionDetails.getDestinationBankAccountNo());
		transferConfirmation.setIsSystemIntiatedTransaction(transactionDetails.isSystemIntiatedTransaction());
		transferConfirmation.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		


		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation,transferConfirmation.DumpFields(), transferConfirmation.getParentTransactionID());
	
		transferConfirmation.setTransactionID(transactionsLog.getId().longValue());

		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(transferConfirmation);
		result.setTransactionID(transactionsLog.getId().longValue());


		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(transferConfirmation.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+sourceMDN.getMdn()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Subscriber srcSub = sourceMDN.getSubscriber();
		KYCLevel srcKyc = srcSub.getKycLevel();
		if(srcKyc.getKyclevel().equals(new Long(CmFinoFIX.SubscriberKYCLevel_NoKyc))){
			log.info(String.format("MoneyTransfer is Failed as the the Source Subscriber(%s) KycLevel is NoKyc",transactionDetails.getSourceMDN()));
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyTransferFromNoKycSubscriberNotAllowed);
			return result;
		}
	//	addCompanyANDLanguageToResult(sourceMDN, result);
		Pocket srcPocket = null;
		
		if (StringUtils.isNotBlank(sourcePocketId)) {
			srcPocket = pocketService.getById(new Long(sourcePocketId));
		} else {
			srcPocket = pocketService.getDefaultPocket(sourceMDN, SourcePocketCode);
		}
		 validationResult=transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		SubscriberMdn destinationMdn = subscriberMdnService.getByMDN(transferConfirmation.getDestMDN());
		
		validationResult=transactionApiValidationService.validateSubscriberAsDestination(destinationMdn);
		boolean isNonRegisteredDestination = CmFinoFIX.NotificationCode_SubscriberNotRegistered.equals(validationResult);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult) && !isNonRegisteredDestination){
			result.setNotificationCode(validationResult);
			return result;
		}
		Pocket destinationPocket = null;
		if(StringUtils.isNotBlank(destPocketId)){
			destinationPocket = pocketService.getById(new Long(destPocketId));
		}else{
			
			if(destinationMdn.getSubscriber().getType() == (CmFinoFIX.SubscriberType_Subscriber.longValue())) {
				
				transactionDetails.setDestPocketCode(String.valueOf(CmFinoFIX.PocketType_LakuPandai));
				
			} else if(destinationMdn.getSubscriber().getType() == (CmFinoFIX.SubscriberType_Partner.longValue())) {
				
				transactionDetails.setDestPocketCode(String.valueOf(CmFinoFIX.PocketType_SVA));
				
			}

			destinationPocket = pocketService.getDefaultPocket(destinationMdn, DestPocketCode);
		}
		if(destinationPocket==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFound);
			return result;
		}
		
		transferConfirmation.setSourcePocketID(srcPocket.getId().longValue());
		transferConfirmation.setDestPocketID(destinationPocket.getId().longValue());

		if(isNonRegisteredDestination && destinationPocket.getStatus() == (CmFinoFIX.PocketStatus_OneTimeActive.longValue())){
			CMTransferToNonRegistered transferConfirmForNonRegistered = new CMTransferToNonRegistered();
			transferConfirmForNonRegistered.copy(transferConfirmation);
			transferConfirmation = transferConfirmForNonRegistered;
		}else if (destinationPocket.getStatus() != (CmFinoFIX.PocketStatus_Active.longValue())) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_DestinationMoneySVAPocketNotActive);
			return result;
		}

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 

		ServiceChargeTxnLog sctl = transactionChargingService.getServiceChargeTransactionLog(transferConfirmation.getParentTransactionID(),transferConfirmation.getTransactionIdentifier());
		if (sctl != null) {
			result.setSctlID(sctl.getId().longValue());
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}

		} else {
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}
		
		transferConfirmation.setRemarks(sctl.getDescription());
		transferConfirmation.setServiceChargeTransactionLogID(sctl.getId().longValue());
			
		CFIXMsg response = super.process(transferConfirmation);
		result.setMultixResponse(response);

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult() && sctl!=null) {
				transactionChargingService.confirmTheTransaction(sctl, transferConfirmation.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, transferConfirmation.getTransferID());
				result.setDebitAmount(sctl.getTransactionamount());
				result.setCreditAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
				result.setServiceCharge(sctl.getCalculatedcharge());
				transactionApiValidationService.checkAndChangeStatus(destinationMdn);								
			} else {
				String errorMsg = transactionResponse.getMessage();
				// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				if(sctl!=null){
					transactionChargingService.failTheTransaction(sctl, errorMsg);
				}
			}
		}
		Subscriber destSubscriber = destinationMdn.getSubscriber();
       	String receiverAccountName = (StringUtils.isNotBlank(destSubscriber.getFirstname()) ? destSubscriber.getFirstname() : "")
					+ " " + (StringUtils.isNotBlank(destSubscriber.getLastname()) ? destSubscriber.getLastname() : "");
       	result.setReceiverAccountName(receiverAccountName);

		//	sendSMS(transferConfirmation, result);
		result.setMessage(transactionResponse.getMessage());
		result.setCode(transactionResponse.getCode());
		log.info("END handle :: AgentTransferConfirmHandlerImpl");
		return result;
	}
}