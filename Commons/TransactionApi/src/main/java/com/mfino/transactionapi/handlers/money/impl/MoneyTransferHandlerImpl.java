/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

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
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
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
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.MoneyTransferHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Deva
 * 
 */
@Service("MoneyTransferHandlerImpl")
public class MoneyTransferHandlerImpl extends FIXMessageHandler implements MoneyTransferHandler{

	private static Logger log = LoggerFactory.getLogger(MoneyTransferHandlerImpl.class);
	
	private String transactionName = ServiceAndTransactionConstants.TRANSACTION_TRANSFER;
	
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
	
	public Result handle(TransactionDetails transactionDetails) {

		String SourcePocketCode = transactionDetails.getSourcePocketCode();
		String DestPocketCode	= transactionDetails.getDestPocketCode();
		String sourcePocketId	= transactionDetails.getSourcePocketId();
		String destPocketId  	= transactionDetails.getDestPocketId();
		String mfaOneTimeOTP 	= transactionDetails.getTransactionOTP();

		ChannelCode cc = transactionDetails.getCc();

		CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
		transferConfirmation.setSourceMDN(transactionDetails.getSourceMDN());
		transferConfirmation.setDestMDN(transactionDetails.getDestMDN());
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setTransferID(transactionDetails.getTransferId());
		transferConfirmation.setConfirmed(Boolean.parseBoolean(transactionDetails.getConfirmString()));
		transferConfirmation.setSourceApplication(cc.getChannelSourceApplication());
		transferConfirmation.setChannelCode(cc.getChannelCode());
		transferConfirmation.setParentTransactionID(transactionDetails.getParentTxnId());
		transferConfirmation.setDestinationBankAccountNo(transactionDetails.getDestinationBankAccountNo());
		transferConfirmation.setIsSystemIntiatedTransaction(transactionDetails.isSystemIntiatedTransaction());
		transferConfirmation.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		log.info("Handling Bank Account transfer confirmation WebAPI request");
		XMLResult result = new MoneyTransferXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation,transferConfirmation.DumpFields(), transferConfirmation.getParentTransactionID());
	
		transferConfirmation.setTransactionID(transactionsLog.getID());

		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(transferConfirmation);
		result.setTransactionID(transactionsLog.getID());


		SubscriberMDN sourceMDN = subscriberMdnService.getByMDN(transferConfirmation.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+sourceMDN.getMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Subscriber srcSub = sourceMDN.getSubscriber();
		KYCLevel srcKyc = srcSub.getKYCLevelByKYCLevel();
		if(srcKyc.getKYCLevel().equals(new Long(CmFinoFIX.SubscriberKYCLevel_NoKyc))){
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
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		SubscriberMDN destinationMdn = subscriberMdnService.getByMDN(transferConfirmation.getDestMDN());
		
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
			
			if(destinationMdn.getSubscriber().getType().equals(CmFinoFIX.SubscriberType_Subscriber)) {
				
				transactionDetails.setDestPocketCode(String.valueOf(CmFinoFIX.PocketType_LakuPandai));
				
			} else if(destinationMdn.getSubscriber().getType().equals(CmFinoFIX.SubscriberType_Partner)) {
				
				transactionDetails.setDestPocketCode(String.valueOf(CmFinoFIX.PocketType_SVA));
				
			} 
			
			destinationPocket = pocketService.getDefaultPocket(destinationMdn, transactionDetails.getDestPocketCode());
		}
		if(destinationPocket==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFound);
			return result;
		}
		
		transferConfirmation.setSourcePocketID(srcPocket.getID());
		transferConfirmation.setDestPocketID(destinationPocket.getID());

		if(isNonRegisteredDestination && destinationPocket.getStatus().equals(CmFinoFIX.PocketStatus_OneTimeActive)){
			CMTransferToNonRegistered transferConfirmForNonRegistered = new CMTransferToNonRegistered();
			transferConfirmForNonRegistered.copy(transferConfirmation);
			transferConfirmation = transferConfirmForNonRegistered;
		}else if (!destinationPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
			return result;
		}

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(transferConfirmation.getParentTransactionID(),transferConfirmation.getTransactionIdentifier());
		if (sctl != null) {
			result.setSctlID(sctl.getID());
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
		
		
		/*
		 * If the Destination Pocket Type is of type SVA or LakuPandai then it is E2ETransfer type; else it is E2BTransfer type.
		 */
		
		if(srcPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_SVA) || srcPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_LakuPandai)){
			if(destinationPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)){
				transactionName = ServiceAndTransactionConstants.TRANSACTION_E2BTRANSFER;
			}
			if(destinationPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_LakuPandai) || destinationPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_SVA)){
				transactionName = ServiceAndTransactionConstants.TRANSACTION_E2ETRANSFER;
			}
		}
		if(srcPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)){
			if(destinationPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)){
				transactionName = ServiceAndTransactionConstants.TRANSACTION_TRANSFER;
			}
			if(destinationPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_LakuPandai) || destinationPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_SVA)){
				transactionName = ServiceAndTransactionConstants.TRANSACTION_B2ETRANSFER;
			}
		}		
		
		if(mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionName, cc.getID())){
			
			if(mfaOneTimeOTP == null || !(mfaService.isValidOTP(mfaOneTimeOTP,sctl.getID(), sourceMDN.getMDN()))){
				
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidData);
				return result;
			}
		}
		
		transferConfirmation.setRemarks(sctl.getDescription());
		transferConfirmation.setServiceChargeTransactionLogID(sctl.getID());
			
		CFIXMsg response = super.process(transferConfirmation);
		result.setMultixResponse(response);

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult() && sctl!=null) {
				transactionChargingService.confirmTheTransaction(sctl, transferConfirmation.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, transferConfirmation.getTransferID());
				result.setDebitAmount(sctl.getTransactionAmount());
				result.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
				result.setServiceCharge(sctl.getCalculatedCharge());
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
       	String receiverAccountName = (StringUtils.isNotBlank(destSubscriber.getFirstName()) ? destSubscriber.getFirstName() : "")
					+ " " + (StringUtils.isNotBlank(destSubscriber.getLastName()) ? destSubscriber.getLastName() : "");
       	result.setReceiverAccountName(receiverAccountName);

		//		sendSMS(transferConfirmation, result);
		result.setMessage(transactionResponse.getMessage());
		result.setCode(transactionResponse.getCode());
		return result;
	}
}