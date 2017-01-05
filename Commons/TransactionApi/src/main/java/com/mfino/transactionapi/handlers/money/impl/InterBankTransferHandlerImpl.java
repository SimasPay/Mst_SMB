/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransfer;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.MFAService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.InterBankTransferHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Deva
 * 
 */
@Service("InterBankTransferHandlerImpl")
public class InterBankTransferHandlerImpl extends FIXMessageHandler implements InterBankTransferHandler{

	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	private static Logger log = LoggerFactory.getLogger(InterBankTransferHandlerImpl.class);
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

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
		
		log.info("Handling Bank Account transfer confirmation WebAPI request");
		boolean confirmed 		= 	Boolean.parseBoolean(transactionDetails.getConfirmString());
		Long transferId 		= 	transactionDetails.getTransferId();
		Long parentTrxnId 		= 	transactionDetails.getParentTxnId();
		String transactionOtp 	= 	transactionDetails.getTransactionOTP();
		ChannelCode channelCode = 	transactionDetails.getCc();
		
		XMLResult result = new MoneyTransferXMLResult();
		//2FA
 		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());

 		ServiceChargeTxnLog sctlForMFA = sctlService.getByTransactionLogId(parentTrxnId);

 		if(mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionDetails.getTransactionName(), channelCode.getId().longValue()) == true){
			if(transactionOtp == null || !(mfaService.isValidOTP(transactionOtp,sctlForMFA.getId().longValue(), transactionDetails.getSourceMDN()))){
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidMFAOTP);
				return result;
			}
		}

		CMInterBankFundsTransfer transferConfirmation = new CMInterBankFundsTransfer();

		transferConfirmation.setSourceMDN(transactionDetails.getSourceMDN());
		transferConfirmation.setDestMDN(transactionDetails.getDestMDN());
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setSourceApplication((int)channelCode.getChannelsourceapplication());
		transferConfirmation.setChannelCode(channelCode.getChannelcode());
		transferConfirmation.setParentTransactionID(parentTrxnId);
		transferConfirmation.setTransferID(transferId);
		transferConfirmation.setConfirmed(confirmed);
		transferConfirmation.setUICategory(CmFinoFIX.TransactionUICategory_InterBank_Transfer);


		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation,transferConfirmation.DumpFields(), transferConfirmation.getParentTransactionID());
		transferConfirmation.setTransactionID(transactionsLog.getId().longValue());

		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(transferConfirmation);
		result.setTransactionID(transactionsLog.getId().longValue());
		addCompanyANDLanguageToResult(sourceMDN, result);


		Integer validationResult=transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			return result;
		}

		Pocket srcPocket = pocketService.getDefaultPocket(sourceMDN, transactionDetails.getSourcePocketCode());
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Subscriber srcSub = sourceMDN.getSubscriber();
		KycLevel srcKyc = srcSub.getKycLevel();
		if(srcKyc.getKyclevel().equals(new Long(CmFinoFIX.SubscriberKYCLevel_NoKyc))){
			log.info(String.format("MoneyTransfer is Failed as the the Source Subscriber(%s) KycLevel is NoKyc",transactionDetails.getSourceMDN()));
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyTransferFromNoKycSubscriberNotAllowed);
			return result;
		}
		/*
		 * Commented the below code to allow interbank transfer from an e-money pocket
		 */
		//if(!srcPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)){
		//	result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidSourcePocketCode);
		//	return result;
		//}
		
		String destinationMDN = systemParametersService.getString(SystemParameterKeys.INTERBANK_PARTNER_MDN_KEY);
		transactionDetails.setDestMDN(destinationMDN);
 		SubscriberMdn destMDN = subscriberMdnService.getByMDN(transactionDetails.getDestMDN());

		Pocket destPocket = pocketService.getDefaultPocket(destMDN, "2");
		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		
		transferConfirmation.setSourcePocketID(srcPocket.getId().longValue());
		transferConfirmation.setDestPocketID(destPocket.getId().longValue());

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 

		ServiceChargeTxnLog sctl = transactionChargingService.getServiceChargeTransactionLog(transferConfirmation.getParentTransactionID(),transferConfirmation.getTransactionIdentifier());
		if (sctl != null) {
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
		transferConfirmation.setServiceChargeTransactionLogID(sctl.getId().longValue());

		CFIXMsg response = super.process(transferConfirmation);
		
		result.setMultixResponse(response);
		result.setSctlID(sctl.getId().longValue());

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult() && sctl!=null) {
				transactionChargingService.confirmTheTransaction(sctl, transferConfirmation.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, transferConfirmation.getTransferID());
				
				result.setDebitAmount(sctl.getTransactionamount());
				result.setCreditAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
				result.setServiceCharge(sctl.getCalculatedcharge());
				transactionApiValidationService.checkAndChangeStatus(destMDN);
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

		//		sendSMS(transferConfirmation, result);
		result.setMessage(transactionResponse.getMessage());
		return result;
	}
}