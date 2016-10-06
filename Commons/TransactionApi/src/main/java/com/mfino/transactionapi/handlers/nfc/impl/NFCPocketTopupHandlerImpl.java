package com.mfino.transactionapi.handlers.nfc.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.constants.GeneralConstants;
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
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.nfc.NFCPocketTopupHandler;
import com.mfino.transactionapi.result.xmlresulttypes.nfc.NFCPocketTopupXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

@org.springframework.stereotype.Service("NFCPocketTopupHandlerImpl")
public class NFCPocketTopupHandlerImpl  extends FIXMessageHandler implements NFCPocketTopupHandler{

	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
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
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	public NFCPocketTopupHandlerImpl()
	{
		
	}
	
	public Result handle(TransactionDetails transactionDetails) {

		ChannelCode cc = transactionDetails.getCc();

		CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
		transferConfirmation.setSourceMDN(transactionDetails.getSourceMDN());
		transferConfirmation.setDestMDN(transactionDetails.getSourceMDN());
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setTransferID(transactionDetails.getTransferId());
		transferConfirmation.setConfirmed(Boolean.parseBoolean(transactionDetails.getConfirmString()));
		transferConfirmation.setSourceApplication((int)cc.getChannelsourceapplication());
		transferConfirmation.setChannelCode(cc.getChannelcode());
		transferConfirmation.setParentTransactionID(transactionDetails.getParentTxnId());
		transferConfirmation.setIsSystemIntiatedTransaction(transactionDetails.isSystemIntiatedTransaction());
		transferConfirmation.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		transferConfirmation.setUICategory(CmFinoFIX.TransactionUICategory_NFC_Pocket_Topup);		
		
		log.info("Handling NFC Pocket Topup Transfer confirmation WebAPI request");
		XMLResult result = new NFCPocketTopupXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation,transferConfirmation.DumpFields(), transferConfirmation.getParentTransactionID());
	
		transferConfirmation.setTransactionID(transactionsLog.getId().longValue());

		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(transferConfirmation);
		result.setTransactionID(transactionsLog.getId().longValue());


		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(transferConfirmation.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("NFC Pocket Topup Transfer confirmation: Source subscriber with mdn : "+sourceMDN.getMdn()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
	//	addCompanyANDLanguageToResult(sourceMDN, result);
		result.setNickName(sourceMDN.getSubscriber().getNickname());

		Pocket srcPocket = null;
		srcPocket = pocketService.getDefaultPocket(sourceMDN, CmFinoFIX.PocketType_SVA.toString());
		
		
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("NFC Pocket Topup Transfer confirmation: Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket destPocket = null;
		if (StringUtils.isNotBlank(transactionDetails.getCardPAN())) {
			destPocket = pocketService.getNFCPocket(sourceMDN, transactionDetails.getCardPAN());
		}
					
		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("NFC Pocket Topup Transfer confirmation: Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		SubscriberMdn destinationMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
		
		log.info("NFC Pocket Topup Transfer confirmation: TransferInquiryHandler destMdn="+destinationMDN+", destPocketCode="+transactionDetails.getDestPocketCode());
		
		
		
		transferConfirmation.setSourcePocketID(srcPocket.getId().longValue());
		transferConfirmation.setDestPocketID(destPocket.getId().longValue());
		transferConfirmation.setDestinationBankAccountNo(destPocket.getCardpan());

		result.setCardPan(destPocket.getCardpan());
		result.setCardAlias(destPocket.getCardalias());
		
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
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
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
		result.setCode(transactionResponse.getCode());
		return result;
	}
	
}
