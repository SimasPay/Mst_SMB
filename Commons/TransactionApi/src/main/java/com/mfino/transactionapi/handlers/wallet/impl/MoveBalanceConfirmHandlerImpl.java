package com.mfino.transactionapi.handlers.wallet.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.MoveBalanceConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
@Service("MoveBalanceConfirmHandlerImpl")
public class MoveBalanceConfirmHandlerImpl extends FIXMessageHandler implements MoveBalanceConfirmHandler{
	private static Logger log = LoggerFactory.getLogger(MoveBalanceConfirmHandlerImpl.class);
	
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
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in MoveBalanceConfirmHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		ChannelCode cc = transactionDetails.getCc();
		CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
		transferConfirmation.setSourceMDN(transactionDetails.getSourceMDN());
		transferConfirmation.setDestMDN(transactionDetails.getDestMDN());
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setTransferID(transactionDetails.getTransferId());
		transferConfirmation.setConfirmed(true);
		transferConfirmation.setSourceApplication(cc.getChannelSourceApplication());
		transferConfirmation.setChannelCode(cc.getChannelCode());
		transferConfirmation.setParentTransactionID(transactionDetails.getParentTxnId());
		log.info("Handling Transfer Confirm request for moving money from Retired Subscriber::From "+ transferConfirmation.getSourceMDN() + " To " + 
				transferConfirmation.getDestMDN());
		XMLResult result = new MoneyTransferXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation,transferConfirmation.DumpFields(), transferConfirmation.getParentTransactionID());
		transferConfirmation.setTransactionID(transactionsLog.getID());
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(transferConfirmation);
		result.setTransactionID(transactionsLog.getID());
		
		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(transferConfirmation.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+transferConfirmation.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		addCompanyANDLanguageToResult(srcSubscriberMDN, result);
		
		Pocket srcSubscriberPocket = null;
		if(transactionDetails.getSrcPocketId()!=null){
			srcSubscriberPocket = pocketService.getById(transactionDetails.getSrcPocketId());
		}
		else 
		{
			srcSubscriberPocket = pocketService.getDefaultPocket(srcSubscriberMDN, transactionDetails.getSourcePocketCode());
		}
		validationResult = transactionApiValidationService.validateSourcePocket(srcSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		

		SubscriberMDN destSubscriberMDN = subscriberMdnService.getByMDN(transferConfirmation.getDestMDN());
		validationResult = transactionApiValidationService.validateSubscriberAsDestination(destSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Destination subscriber with mdn : "+transferConfirmation.getDestMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		
		
		Pocket destSubscriberPocket = null;
		if(transactionDetails.getDestinationPocketId()!=null){
			destSubscriberPocket = pocketService.getById(transactionDetails.getDestinationPocketId());
		}
		else 
		{
			destSubscriberPocket = pocketService.getDefaultPocket(destSubscriberMDN, transactionDetails.getDestPocketCode());
		}
		validationResult = transactionApiValidationService.validateDestinationPocket(destSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destSubscriberPocket!=null? destSubscriberPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		transferConfirmation.setSourcePocketID(srcSubscriberPocket.getID());
		transferConfirmation.setDestPocketID(destSubscriberPocket.getID());
		
		// Changing the Service_charge_transaction_log status based on the response from Core engine. 

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(transferConfirmation.getParentTransactionID());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				log.error("The status of Sctl with id: "+sctl.getID()+"has been changed from Inquiry to: "+sctl.getStatus());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);				
				return result;
			}

		} else {
			log.error("Could not find sctl with parentTransaction ID: "+transferConfirmation.getParentTransactionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);			
			return result;
		}
		
		transferConfirmation.setServiceChargeTransactionLogID(sctl.getID());
		
		log.info("sending the transferConfirmation request to backend for processing");
		CFIXMsg response = super.process(transferConfirmation);
		result.setMultixResponse(response);

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult() && sctl!=null) {
				transactionChargingService.confirmTheTransaction(sctl, transferConfirmation.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, transferConfirmation.getTransferID());
				result.setDebitAmount(sctl.getTransactionAmount());
				result.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
				result.setServiceCharge(sctl.getCalculatedCharge());
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
		
		result.setMultixResponse(response);
		result.setSctlID(sctl.getID());
		result.setDestinationMDN(transferConfirmation.getDestMDN());
		result.setMessage(transactionResponse.getMessage());
		result.setCode(transactionResponse.getCode());
		result.setSourcePocket(destSubscriberPocket);
		return result;
	}
}
