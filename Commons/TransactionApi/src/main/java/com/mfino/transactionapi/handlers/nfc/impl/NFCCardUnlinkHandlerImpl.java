/**
 * 
 */
package com.mfino.transactionapi.handlers.nfc.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
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
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlink;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkReversal;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.nfc.NFCCardUnlinkHandler;
import com.mfino.transactionapi.result.xmlresulttypes.nfc.NFCXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Srikanth
 * 
 */
@Service("NFCCardUnlinkHandlerImpl")
public class NFCCardUnlinkHandlerImpl extends FIXMessageHandler implements NFCCardUnlinkHandler{

	private static Logger log = LoggerFactory.getLogger(NFCCardUnlinkHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

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
		log.info("Extracting data from transactionDetails in NFCCardUnlinkHandlerImpl for sourceMDN: "+transactionDetails.getSourceMDN());
		ChannelCode cc = transactionDetails.getCc();
		log.debug("Channel code is :" + cc.getChannelcode());
		CMNFCCardUnlink nfcCardUnlink = new CMNFCCardUnlink();
		nfcCardUnlink.setPin(transactionDetails.getSourcePIN());
		nfcCardUnlink.setSourceMDN(transactionDetails.getSourceMDN());
		//nfcCardUnlink.setServletPath(CmFinoFIX.ServletPath_BankAccount);
		nfcCardUnlink.setSourceCardPAN(transactionDetails.getCardPAN());
		nfcCardUnlink.setSourceApplication((int)cc.getChannelsourceapplication());
		nfcCardUnlink.setChannelCode(cc.getChannelcode());
		nfcCardUnlink.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		
		log.info("Handling NFCCardUnlink WebAPI request");
		XMLResult result = new NFCXMLResult();

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_NFCCardUnlink, nfcCardUnlink.DumpFields());
		nfcCardUnlink.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(nfcCardUnlink);
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		result.setCardAlias("");
		result.setCardPan(transactionDetails.getCardPAN());
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(nfcCardUnlink.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+nfcCardUnlink.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}	
		if(!CmFinoFIX.SourceApplication_CMS.toString().equals(cc.getChannelcode())) {
			validationResult = transactionApiValidationService.validatePin(srcSubscriberMDN, nfcCardUnlink.getPin());
			if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
				log.error("Pin validation failed for mdn: "+nfcCardUnlink.getSourceMDN());
				result.setNumberOfTriesLeft((int)(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - srcSubscriberMDN.getWrongpincount()));
				result.setNotificationCode(validationResult);
				return result;
			}
		}	
		
		addCompanyANDLanguageToResult(srcSubscriberMDN,result);

		// Get the NFC pocket with the given CardPAN and mdn
		Pocket nfcPocket = pocketService.getNFCPocket(srcSubscriberMDN, transactionDetails.getCardPAN());
		if(nfcPocket == null){
			log.info("NFC Pocket not found");
			result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);			
			return result;
		}
		result.setCardAlias(nfcPocket.getCardalias());
		if (!(nfcPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
			log.info("NFC Pocket with Card Pan " + transactionDetails.getCardPAN() +" is already unlinked");
			result.setNotificationCode(CmFinoFIX.NotificationCode_PocketAlreadyUnlinked);
			return result;
		}
		
		// Calculate the Service Charge

		log.info("creating the serviceCharge object....");
		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(nfcCardUnlink.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_NFC);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_UNLINK);
		sc.setTransactionAmount(new BigDecimal(nfcPocket.getCurrentbalance()));
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		sc.setTransactionIdentifier(nfcCardUnlink.getTransactionIdentifier());

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
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		nfcCardUnlink.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		boolean isSuccess = false, 
				proceedWithCardUnlink = true,
				cmsCardUnlinkSuccess = false;
		String message = null;
		Integer code = CmFinoFIX.NotificationCode_NFCCardUnlinkFailed;
		if(!CmFinoFIX.SourceApplication_CMS.toString().equals(cc.getChannelcode())) {
			log.info("sending the request to CMS(backend) for processing");
			CFIXMsg response = super.process(nfcCardUnlink);
			TransactionResponse transactionResponse = checkBackEndResponse(response);
			if(transactionResponse.isResult() && sctl!=null) {
				log.info("CMS request to get approval for unlink is success");
				cmsCardUnlinkSuccess = true;
			} else {	
				log.info("CMS request to get approval for unlink failed");				
				proceedWithCardUnlink = false;
				String errorMsg = ((CMJSError) response).getErrorDescription();
				// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				message = errorMsg;				
			}
		}
		if(CmFinoFIX.SourceApplication_CMS.toString().equals(cc.getChannelcode())) { //set the transID if the request is from CMS channel
			sctl.setIntegrationtransactionid(BigDecimal.valueOf(Long.valueOf(transactionDetails.getTransID())));
		}
		if(proceedWithCardUnlink) {						
			if(BigDecimal.ZERO.compareTo(new BigDecimal(nfcPocket.getCurrentbalance())) == 0) {
				log.info("NFC Pocket has zero balance... so straightaway deactivate it");
				isSuccess = true;
			} else {				
				log.info("NFC Pocket has balance... transfer it to default emoney pocket and deactivate it");
				Pocket emoneyPocket = pocketService.getDefaultPocket(srcSubscriberMDN, CmFinoFIX.PocketType_SVA.toString());
				if(emoneyPocket == null){
					log.info("Default Emoney Pocket not found");
					result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);				
					return result;
				}
				if (!(emoneyPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
					log.info("Default Emoney Pocket is not active");
					result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
					return result;
				}		
				
				CMBankAccountToBankAccount transferInquiry = new CMBankAccountToBankAccount();
				transferInquiry.setSourceMDN(nfcCardUnlink.getSourceMDN());
				transferInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());
				transferInquiry.setDestMDN(nfcCardUnlink.getSourceMDN());
				transferInquiry.setSourcePocketID(nfcPocket.getId().longValue());
				transferInquiry.setDestPocketID(emoneyPocket.getId().longValue());
				transferInquiry.setAmount(new BigDecimal(nfcPocket.getCurrentbalance()));
				transferInquiry.setUICategory(CmFinoFIX.TransactionUICategory_EMoney_EMoney_Trf);
				if(CmFinoFIX.SourceApplication_CMS.toString().equals(cc.getChannelcode())) {					
					transferInquiry.setIsSystemIntiatedTransaction(BOOL_TRUE);
				}
				transferInquiry.setPin(nfcCardUnlink.getPin());
				transferInquiry.setTransactionID(transactionsLog.getId().longValue());
				transferInquiry.setChannelCode(cc.getChannelcode());
				transferInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
				transferInquiry.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
				transferInquiry.setServiceName(ServiceAndTransactionConstants.SERVICE_NFC);
				
				log.info("Sending the transfer inquiry request to Backend");
				CFIXMsg inquiryResponse = super.process(transferInquiry);
				TransactionResponse inquiryTxnResponse = checkBackEndResponse(inquiryResponse);
				
				if (inquiryTxnResponse.isResult()) {
					CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
					transferConfirmation.setSourceMDN(nfcCardUnlink.getSourceMDN());
					transferConfirmation.setDestMDN(nfcCardUnlink.getSourceMDN());
					transferConfirmation.setSourcePocketID(nfcPocket.getId().longValue());
					transferConfirmation.setDestPocketID(emoneyPocket.getId().longValue());				
					transferConfirmation.setChannelCode(cc.getChannelcode());
					transferConfirmation.setSourceApplication((int)cc.getChannelsourceapplication());
					transferConfirmation.setParentTransactionID(inquiryTxnResponse.getTransactionId());
					transferConfirmation.setTransferID(inquiryTxnResponse.getTransferId());
					transferConfirmation.setServiceChargeTransactionLogID(sctl.getId().longValue());
					transferConfirmation.setConfirmed(CmFinoFIX.Boolean_True);
					
					log.info("Sending the transfer confirm request to Backend");
					CFIXMsg confirmResponse = super.process(transferConfirmation);
					TransactionResponse confirmTxnResponse = checkBackEndResponse(confirmResponse);
					
					if(confirmTxnResponse.isResult()) {
						isSuccess = true;					
					} else {
						log.info("transfer confirm request failed");
						isSuccess = false;
						message = confirmTxnResponse.getMessage();
						code = Integer.valueOf(inquiryTxnResponse.getCode());
					}
				} else {
					log.info("transfer inquiry request failed");
					isSuccess = false;
					message = inquiryTxnResponse.getMessage();
					code = Integer.valueOf(inquiryTxnResponse.getCode());
				}
			}
		}
		
		if(isSuccess) {
			transactionChargingService.completeTheTransaction(sctl);
			//get the updated nfc pocket and set its status to Retired
			nfcPocket = pocketService.getPocketAferEvicting(nfcPocket);
			nfcPocket.setStatus(CmFinoFIX.PocketStatus_Retired);
			pocketService.save(nfcPocket);
			message = "Card unlinked successfully";
			code = CmFinoFIX.NotificationCode_NFCCardUnlinkSuccess;
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		} else {
			if(cmsCardUnlinkSuccess) { //send the card unlink reversal request to CMS as the money transfer is not successful
				log.info("sending the reversal request to CMS(backend) for processing");
				CMNFCCardUnlinkReversal cardUnlinkReversal = new CMNFCCardUnlinkReversal();
				cardUnlinkReversal.setSourceMDN(transactionDetails.getSourceMDN());
				cardUnlinkReversal.setPin(transactionDetails.getSourcePIN());
				cardUnlinkReversal.setSourceCardPAN(transactionDetails.getCardPAN());
				cardUnlinkReversal.setSourceApplication((int)cc.getChannelsourceapplication());
				cardUnlinkReversal.setTransactionID(transactionsLog.getId().longValue());
				cardUnlinkReversal.setChannelCode(cc.getChannelcode());
				cardUnlinkReversal.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
				CFIXMsg reversalResponse = super.process(cardUnlinkReversal);
				TransactionResponse reversalTxnResponse = checkBackEndResponse(reversalResponse);
				if(reversalTxnResponse.isResult()) {
					log.info("Card Unlink reversal request is success");
				} else {
					log.info("Card Unlink reversal request is failed");
					// TODO : Handle the failed reversal request
				}				
			}
			transactionChargingService.failTheTransaction(sctl, message);			
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		}
		result.setSctlID(sctl.getId().longValue());
		result.setSourceMDN(transactionDetails.getSourceMDN());
		result.setCardPan(transactionDetails.getCardPAN());
		result.setTransID(transactionDetails.getTransID());		
		result.setMessage(message);
		result.setNotificationCode(code);
		result.setCode(code.toString());
		return result;
	}
}
