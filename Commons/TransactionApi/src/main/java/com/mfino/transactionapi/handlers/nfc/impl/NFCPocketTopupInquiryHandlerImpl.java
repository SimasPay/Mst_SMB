package com.mfino.transactionapi.handlers.nfc.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
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
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMNFCCardStatus;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.MfinoService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.nfc.NFCCardStatusHandler;
import com.mfino.transactionapi.handlers.nfc.NFCPocketTopupInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.nfc.NFCPocketTopupInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

@org.springframework.stereotype.Service("NFCPocketTopupInquiryHandlerImpl")
public class NFCPocketTopupInquiryHandlerImpl  extends FIXMessageHandler implements NFCPocketTopupInquiryHandler{
	
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
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService	subscriberService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService	partnerService;
	
	@Autowired
	@Qualifier("MfinoServiceImpl")
	private MfinoService mfinoService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService SCTLService;
	
	@Autowired
	@Qualifier("NFCCardStatusHandlerImpl")
	private NFCCardStatusHandler  nFCCardStatusHandlerImpl;

	public NFCPocketTopupInquiryHandlerImpl()
	{
		
	}

	
	public Result handle(TransactionDetails transactionDetails) {
		
		ChannelCode cc = transactionDetails.getCc();

		BigDecimal amount = transactionDetails.getAmount();
		log.info("Handling NFC Pocket Topup Transfer Inquiry WebAPI request::From " + transactionDetails.getSourceMDN() + " To " + transactionDetails.getDestMDN() + " For Amount = " + transactionDetails.getAmount());
		XMLResult result = new NFCPocketTopupInquiryXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		String sourceMessage = transactionDetails.getSourceMessage();
		
		CMBankAccountToBankAccount bankAccountToBankAccount = new CMBankAccountToBankAccount();
		bankAccountToBankAccount.setDestMDN(transactionDetails.getSourceMDN());
		bankAccountToBankAccount.setAmount(amount);
		bankAccountToBankAccount.setPin(transactionDetails.getSourcePIN());
		bankAccountToBankAccount.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		bankAccountToBankAccount.setSourceMDN(transactionDetails.getSourceMDN());
		bankAccountToBankAccount.setSourceMessage(StringUtils.isNotBlank(sourceMessage) ? sourceMessage : "NFC Pocket Topup");
		bankAccountToBankAccount.setSourceApplication((int)cc.getChannelsourceapplication());
		bankAccountToBankAccount.setChannelCode(cc.getChannelcode());
		bankAccountToBankAccount.setServiceName(transactionDetails.getServiceName());
		bankAccountToBankAccount.setIsSystemIntiatedTransaction(transactionDetails.isSystemIntiatedTransaction());
		bankAccountToBankAccount.setUICategory(CmFinoFIX.TransactionUICategory_NFC_Pocket_Topup);
		
		result.setDestinationMDN(transactionDetails.getSourceMDN());
		result.setSourceMessage(bankAccountToBankAccount);
		
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());

		Integer validationResult=transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+sourceMDN.getMdn()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		result.setNickName(sourceMDN.getSubscriber().getNickname());
		
		Pocket srcPocket = null;
		srcPocket = pocketService.getDefaultPocket(sourceMDN, CmFinoFIX.PocketType_SVA.toString());
		
		
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("NFC Pocket Topup Transfer Inquiry: Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		bankAccountToBankAccount.setSourcePocketID(srcPocket.getId().longValue());
		transactionDetails.setSrcPocketId(srcPocket.getId().longValue());
		
		Pocket destPocket = null;
		if (StringUtils.isNotBlank(transactionDetails.getCardPAN())) {
			destPocket = pocketService.getNFCPocket(sourceMDN, transactionDetails.getCardPAN());
		}
		
		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("NFC Pocket Topup Transfer Inquiry: Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		SubscriberMdn destinationMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());

	//	addCompanyANDLanguageToResult(sourceMDN, result);

		log.info("NFC Pocket Topup Transfer Inquiry: destMdn="+destinationMDN+", destPocketCode="+transactionDetails.getDestPocketCode());
		

 		bankAccountToBankAccount.setDestPocketID(destPocket.getId().longValue());
 		bankAccountToBankAccount.setDestinationBankAccountNo(destPocket.getCardpan());
 		transactionDetails.setDestinationPocketId(destPocket.getId().longValue());
 		result.setCardPan(destPocket.getCardpan());
 		result.setCardAlias(destPocket.getCardalias()); 		

		Transaction transaction = null;
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccount, bankAccountToBankAccount.DumpFields());
		
		bankAccountToBankAccount.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(bankAccountToBankAccount);
		result.setTransactionID(transactionsLog.getId().longValue());
		
		addCompanyANDLanguageToResult(sourceMDN, result);
		

		ServiceCharge sc = new ServiceCharge();
		
		sc.setSourceMDN(bankAccountToBankAccount.getSourceMDN());
		sc.setDestMDN(bankAccountToBankAccount.getDestMDN());
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(bankAccountToBankAccount.getServiceName());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_NFC_POCKET_TOPUP);
		
		
		sc.setTransactionAmount(bankAccountToBankAccount.getAmount());
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		sc.setTransactionIdentifier(bankAccountToBankAccount.getTransactionIdentifier());
		
//		if(transactionDetails.getDestinationBankAccountNo() != null){
//			sc.setOnBeHalfOfMDN(transactionDetails.getDestinationBankAccountNo());
//			bankAccountToBankAccount.setDestinationBankAccountNo(transactionDetails.getDestinationBankAccountNo());
//		}

//		SubscriberMDN sourceSubscriberMdn = sourceMDN;
//		SubscriberMDN destSubscriberMdn =  subscriberMdnService.getByMDN(bankAccountToBankAccount.getDestMDN());

//		Subscriber sourceSubscriber = (sourceSubscriberMdn != null) ? sourceSubscriberMdn.getSubscriber() : null;
//		Subscriber destSubscriber = (destSubscriberMdn != null) ? destSubscriberMdn.getSubscriber() : null;
	
//		validationResult = hierarchyService.validate(sourceSubscriber, destSubscriber, sc.getServiceName(), sc.getTransactionTypeName());
//		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
//			result.setNotificationCode(validationResult);
//			return result;
//		}
		
		try{
			transaction =transactionChargingService.getCharge(sc);
			bankAccountToBankAccount.setAmount(transaction.getAmountToCredit());
			bankAccountToBankAccount.setCharges(transaction.getAmountTowardsCharges());

		} catch (InvalidServiceException ise) {
			log.error("NFC Pocket Topup Transfer Inquiry: Exception occured in getting charges",ise);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);//change to service not found
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		bankAccountToBankAccount.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		CMNFCCardStatus nfcCardStatus = new CMNFCCardStatus();
		nfcCardStatus.setSourceMDN(sourceMDN.getMdn());
		nfcCardStatus.setSourceCardPAN(transactionDetails.getCardPAN());
		nfcCardStatus.setServiceChargeTransactionLogID(sctl.getId().longValue());
		nfcCardStatus.setTransactionID(transactionsLog.getId().longValue());
		nfcCardStatus.setSourceApplication((int)cc.getChannelsourceapplication());
		nfcCardStatus.setChannelCode(cc.getChannelcode());
		
		CMJSError nFCCardStatusFromCMS = (CMJSError) nFCCardStatusHandlerImpl.handle(nfcCardStatus);
		if(!nFCCardStatusFromCMS.getCode().equals(CmFinoFIX.NotificationCode_NFCCardActive)){
			log.error("NFC Card is not active");
			result.setNotificationCode(CmFinoFIX.NotificationCode_NFCCardStatusNotActive);
			sctl.setStatus(CmFinoFIX.SCTLStatus_Failed);
			sctl.setFailurereason("NFC Card is not active");
			transactionChargingService.saveServiceTransactionLog(sctl);
			return result;
		}
		CFIXMsg response = super.process(bankAccountToBankAccount);
        
		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (transactionResponse.getTransactionId() !=null){
			sctl.setTransactionid(BigDecimal.valueOf(transactionResponse.getTransactionId()));
			bankAccountToBankAccount.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
			if (transactionResponse.isResult()){
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
			}
		}
		transactionChargingService.updateTransactionStatus(transactionResponse, sctl);
       if(transactionResponse.getDestinationType() !=null){
			if(transactionResponse.getDestinationType().equals("Account")){
				result.setDestinationAccountNumber(bankAccountToBankAccount.getDestinationBankAccountNo());
			}
		}else{
			result.setDestinationMDN(bankAccountToBankAccount.getDestMDN());
		}
		result.setDestinationName(transactionResponse.getDestinationUserName());
		result.setBankName(transactionResponse.getBankName());
		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setSctlID(sctl.getId().longValue());		
		result.setMfaMode("None");	
		return result;
	}	
}
