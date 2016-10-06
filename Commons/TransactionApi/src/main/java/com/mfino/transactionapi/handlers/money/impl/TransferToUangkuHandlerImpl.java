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
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
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
import com.mfino.transactionapi.handlers.money.TransferToUangkuHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Deva
 * 
 */
@Service("TransferToUangkuHandlerImpl")
public class TransferToUangkuHandlerImpl extends FIXMessageHandler implements TransferToUangkuHandler {

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
	
	private static Logger log = LoggerFactory.getLogger(TransferToUangkuHandlerImpl.class);
	
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
		
		log.info("Handling TransferToUangkuHandler Bank Account transfer confirmation WebAPI request");
		Long parentTrxnId 		= 	transactionDetails.getParentTxnId();
		String transactionOtp 	= 	transactionDetails.getTransactionOTP();
		ChannelCode channelCode = 	transactionDetails.getCc();
		
		XMLResult result = new MoneyTransferXMLResult();
 		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());

 		ServiceChargeTxnLog sctlForMFA = sctlService.getByTransactionLogId(parentTrxnId);

 		if(mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionDetails.getTransactionName(), channelCode.getId().longValue()) == true){
			if(transactionOtp == null || !(mfaService.isValidOTP(transactionOtp,sctlForMFA.getId().longValue(), transactionDetails.getSourceMDN()))){
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidMFAOTP);
				return result;
			}
		}
 		
 		CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
		transferConfirmation.setSourceMDN(transactionDetails.getSourceMDN());
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setTransferID(transactionDetails.getTransferId());
		transferConfirmation.setConfirmed(Boolean.parseBoolean(transactionDetails.getConfirmString()));
		transferConfirmation.setSourceApplication((int)channelCode.getChannelsourceapplication());
		transferConfirmation.setChannelCode(channelCode.getChannelcode());
		transferConfirmation.setParentTransactionID(transactionDetails.getParentTxnId());
		transferConfirmation.setDestinationBankAccountNo(transactionDetails.getDestinationBankAccountNo());
		transferConfirmation.setIsSystemIntiatedTransaction(transactionDetails.isSystemIntiatedTransaction());
		transferConfirmation.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		transferConfirmation.setUICategory(CmFinoFIX.TransactionUICategory_Transfer_To_Uangku);
		
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_TransferToUangku,transferConfirmation.DumpFields(), transferConfirmation.getParentTransactionID());
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
		
		transferConfirmation.setDestMDN(transactionDetails.getDestMDN());
		transferConfirmation.setSourcePocketID(srcPocket.getId().longValue());
		transferConfirmation.setDestPocketID(destPocket.getId().longValue());

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

		result.setMessage(transactionResponse.getMessage());
		return result;
	}
}