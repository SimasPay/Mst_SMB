package com.mfino.transactionapi.handlers.nfc.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.Service;
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
import com.mfino.fix.CmFinoFIX.CMNFCCardTopup;
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
import com.mfino.transactionapi.handlers.nfc.NFCCardTopupHandler;
import com.mfino.transactionapi.result.xmlresulttypes.nfc.NFCCardTopupXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

@org.springframework.stereotype.Service("NFCCardTopupHandlerImpl")
public class NFCCardTopupHandlerImpl  extends FIXMessageHandler implements NFCCardTopupHandler{

	
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

	public NFCCardTopupHandlerImpl()
	{
		
	}

	public Result handle(TransactionDetails txnDetails)
	{
		log.info(String.format("NFCCardTopupHandlerImpl::handle() -- Handling NFC CardTopup request (soureMDN:%s, sourceCardPan:%s, amount:%s, transID:%s)",txnDetails.getSourceMDN(),txnDetails.getCardPAN(),txnDetails.getAmount().toString(),txnDetails.getTransID().toString()) );
		XMLResult inquiryResult = new NFCCardTopupXMLResult();
		
		ChannelCode cc =  txnDetails.getCc();
		CMNFCCardTopup nfcCardTopup = new CMNFCCardTopup();
		nfcCardTopup.setSourceMDN(subscriberService.normalizeMDN(txnDetails.getSourceMDN()));
		nfcCardTopup.setSourceCardPAN(txnDetails.getCardPAN());
		nfcCardTopup.setIntegrationTransactionID(Long.parseLong(txnDetails.getTransID()));
		nfcCardTopup.setChannelCode(cc.getChannelcode());
		nfcCardTopup.setSourceApplication((int)cc.getChannelsourceapplication());
		nfcCardTopup.setAmount(txnDetails.getAmount());
		
		
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_NFCCardTopup, nfcCardTopup.DumpFields());
		
		nfcCardTopup.setTransactionID(transactionsLog.getId().longValue());
		inquiryResult = (XMLResult) handleNFCCardTopupInquiry(txnDetails);
		
		if(!isNFCCardTopupInquirySuccessfull(inquiryResult)){
			return inquiryResult;
		}
		log.info(String.format("NFCCardTopupHandlerImpl::handle() -- Handling NFC CardTopup request (soureMDN:%s, sourceCardPan:%s, amount:%s, transID:%s), inquiry Success ",txnDetails.getSourceMDN(),txnDetails.getCardPAN(),txnDetails.getAmount().toString(),txnDetails.getTransID().toString()) );
		
		//Inquiry Success, send Confirmation for NFC Card Topup
		
		XMLResult confirmResult = (XMLResult)handleNFCCardTopupConfirm(txnDetails,inquiryResult);
		if(confirmResult.getResponseStatus() != null && confirmResult.getResponseStatus().equals(GeneralConstants.RESPONSE_CODE_SUCCESS)){
			confirmResult.setMultixResponse(null);
			confirmResult.setNotificationCode(CmFinoFIX.NotificationCode_NFCCardTopupSuccess);
		}
		log.info(String.format("NFCCardTopupHandlerImpl::handle() -- Completed NFC CardTopup request (soureMDN:%s, sourceCardPan:%s, amount:%s, transID:%s) and returning the result ",txnDetails.getSourceMDN(),txnDetails.getCardPAN(),txnDetails.getAmount().toString(),txnDetails.getTransID().toString()) );
		return confirmResult;
	}
	
	
	
	private Result handleNFCCardTopupInquiry(TransactionDetails txnDetails){
		log.info(String.format("NFCCardTopupHandlerImpl::handleNFCCardTopupInquiry() -- Handling NFC CardTopup Inquiry (soureMDN:%s, sourceCardPan:%s, amount:%s, transID:%s)",txnDetails.getSourceMDN(),txnDetails.getCardPAN(),txnDetails.getAmount().toString(),txnDetails.getTransID().toString()) );
		XMLResult result = new NFCCardTopupXMLResult();
		ChannelCode cc =  txnDetails.getCc();
		result.setTransID(txnDetails.getTransID());
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		sctlQuery.setIntegrationTxnID(Long.parseLong(txnDetails.getTransID()));
		List<ServiceChargeTxnLog> resultSctlList = SCTLService.getByQuery(sctlQuery);
		if(resultSctlList != null && resultSctlList.size() != 0){
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransIDAlreadyExist);
			return result;
		}
		
		String partnerCode = systemParametersService.getString(SystemParameterKeys.NFC_CARD_TOPUP_PARTNER_CODE);
		Partner pt = partnerService.getPartnerByPartnerCode(partnerCode);
		Set<SubscriberMdn> subscriberSet= pt.getSubscriber().getSubscriberMdns();
		String destMdn = null;
		if(subscriberSet != null && subscriberSet.size() != 0){
			destMdn = subscriberSet.iterator().next().getMdn();
		}
		
		CMBankAccountToBankAccount bankAccountToBankAccount = new CMBankAccountToBankAccount();
		bankAccountToBankAccount.setSourceMDN(subscriberService.normalizeMDN(txnDetails.getSourceMDN()));
		bankAccountToBankAccount.setDestMDN(destMdn);
		bankAccountToBankAccount.setAmount(txnDetails.getAmount());
		bankAccountToBankAccount.setIsSystemIntiatedTransaction(true);
		bankAccountToBankAccount.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		bankAccountToBankAccount.setSourceMessage("NFC CardTopup Transfer Inquiry");
		bankAccountToBankAccount.setSourceApplication((int)cc.getChannelsourceapplication());
		bankAccountToBankAccount.setChannelCode(cc.getChannelcode());
		bankAccountToBankAccount.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		
		
		result.setDestinationMDN(destMdn);
		result.setSourceMessage(bankAccountToBankAccount);
		result.setCardPan(txnDetails.getCardPAN());
		
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(subscriberService.normalizeMDN(txnDetails.getSourceMDN()));
		Integer validationResult=transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			if(!CmFinoFIX.NotificationCode_MDNIsNotActive.equals(validationResult)){
				result.setNotificationCode(validationResult);
				return result;
			}
		}
		
		txnDetails.setSourceMDN(sourceMDN.getMdn());
		
		Pocket srcPocket = null;
		if (StringUtils.isNotBlank(txnDetails.getCardPAN())) {
			PocketQuery pkQuery = new PocketQuery();
			pkQuery.setCardPan(txnDetails.getCardPAN());
			List<Pocket> pkList =  pocketService.get(pkQuery);
			if(pkList != null && pkList.size() != 0){
				srcPocket = pkList.get(0);
			}
		}
					
		if(srcPocket==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
			return result;
		}
		result.setCardAlias(srcPocket.getCardalias());
		
		if(new BigDecimal((srcPocket.getCurrentbalance())).subtract(txnDetails.getAmount()).compareTo(BigDecimal.ZERO) == -1){
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransactionFailedDueToInsufficientBalance);
			result.setAmount(txnDetails.getAmount());
			return result;
		}
		
		txnDetails.setSrcPocketId(srcPocket.getId().longValue());
		
		SubscriberMdn destMDN = subscriberMdnService.getByMDN(destMdn);
		validationResult= transactionApiValidationService.validateSubscriberAsDestination(destMDN);
		
		if((CmFinoFIX.NotificationCode_DestinationMDNNotFound.equals(validationResult) ||
				CmFinoFIX.NotificationCode_SubscriberNotRegistered.equals(validationResult))){
			result.setNotificationCode(validationResult);
			return result;
		}
		else if(!CmFinoFIX.ResponseCode_Success.equals(validationResult))
		{
			result.setNotificationCode(validationResult);
			if(destMDN != null)
			{
				String receiverAccountName = destMDN.getSubscriber().getFirstname() + " " + destMDN.getSubscriber().getLastname();
				result.setReceiverAccountName(receiverAccountName);
			}
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			return result;
		}
		
		txnDetails.setDestMDN(destMdn);
		addCompanyANDLanguageToResult(sourceMDN, result);
		
		Service ser = mfinoService.getServiceByName(ServiceAndTransactionConstants.SERVICE_NFC);
		Long serviceProviderId = 1L;
		try{
			serviceProviderId = transactionChargingService.getServiceProviderId(null);
		}catch(InvalidServiceException e)
		{
			log.error("Exception while fetching the ServiceProviderId", e);
		}		
		List<PartnerServices> psList = partnerService.getPartnerServices(pt.getId().longValue(), serviceProviderId, ser.getId().longValue());
		PartnerServices ps = null;
		if(psList != null && psList.size() != 0){
			ps = psList.get(0);
		}
		Pocket destPocket = null;
		if(ps != null){
			destPocket = ps.getPocketByDestpocketid();
		}
		
		if(destPocket == null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFound);
			return result;
		}		
		if (!(destPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
			return result;
		}
		
		txnDetails.setDestinationPocketId(destPocket.getId().longValue());
		
		bankAccountToBankAccount.setSourcePocketID(srcPocket.getId().longValue());
		bankAccountToBankAccount.setDestPocketID(destPocket.getId().longValue());
		bankAccountToBankAccount.setIsSystemIntiatedTransaction(true);
		bankAccountToBankAccount.setPin("");
		
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccount, bankAccountToBankAccount.DumpFields());
		bankAccountToBankAccount.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setDestinationMDN(bankAccountToBankAccount.getDestMDN());
		
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(bankAccountToBankAccount.getSourceMDN());
		sc.setDestMDN(bankAccountToBankAccount.getDestMDN());
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_NFC);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_TOPUP);
		sc.setTransactionAmount(bankAccountToBankAccount.getAmount());
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		
		Transaction transaction = null;
		try{
			transaction =transactionChargingService.getCharge(sc);
			bankAccountToBankAccount.setAmount(transaction.getAmountToCredit());
			bankAccountToBankAccount.setCharges(transaction.getAmountTowardsCharges());
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
		bankAccountToBankAccount.setServiceChargeTransactionLogID(sctl.getId().longValue());
		sctl.setIntegrationtransactionid(new Long(txnDetails.getTransID()));
		transactionChargingService.saveServiceTransactionLog(sctl);
		log.info(String.format("NFCCardTopupHandlerImpl::handleNFCCardTopupInquiry() -- Sending NFC CardTopup Inquiry to Backend (soureMDN:%s, srcPocketID:%s, amount:%s, transID:%s, destMDN:%s, destPocketID:%s, sctlID:%s )",txnDetails.getSourceMDN(),srcPocket.getId().toString(),txnDetails.getAmount().toString(),txnDetails.getTransID().toString(),destMDN.getMdn(),destPocket.getId(),sctl.getId()));
		
		CFIXMsg response = super.process(bankAccountToBankAccount);
		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionid(transactionResponse.getTransactionId());
			bankAccountToBankAccount.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
			log.info(String.format("NFCCardTopupHandlerImpl::handleNFCCardTopupInquiry() -- got response from backend and saving sctl(ID:%s)",sctl.getId()));
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
		result.setSourcePocket(srcPocket);
		return result;		
		
	}
	
	private Result handleNFCCardTopupConfirm(TransactionDetails txnDetails,XMLResult inquiryResult){
		log.info(String.format("NFCCardTopupHandlerImpl::handleNFCCardTopupConfirm() -- Handling NFC CardTopup Confirm (soureMDN:%s, sourceCardPan:%s, amount:%s, transID:%s)",txnDetails.getSourceMDN(),txnDetails.getCardPAN(),txnDetails.getAmount().toString(),txnDetails.getTransID().toString()) );
		XMLResult confirmResult = new NFCCardTopupXMLResult();
		ChannelCode cc =  txnDetails.getCc();
		confirmResult.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		confirmResult.setSourceMDN(txnDetails.getSourceMDN());
		confirmResult.setCardPan(txnDetails.getCardPAN());
		confirmResult.setCardAlias(inquiryResult.getCardAlias());
		
		CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
		transferConfirmation.setSourceMDN(txnDetails.getSourceMDN());
		transferConfirmation.setDestMDN(txnDetails.getDestMDN());
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setTransferID(new Long(inquiryResult.getTransferID()));
		transferConfirmation.setConfirmed(true);
		transferConfirmation.setSourceApplication((int)cc.getChannelsourceapplication());
		transferConfirmation.setChannelCode(cc.getChannelcode());
		transferConfirmation.setParentTransactionID(new Long(inquiryResult.getParentTransactionID()));
		transferConfirmation.setUICategory(CmFinoFIX.TransactionUICategory_NFC_Card_Topup);
		
		TransactionLog confirmTransactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation,transferConfirmation.DumpFields(), transferConfirmation.getParentTransactionID());
		
		transferConfirmation.setTransactionID(confirmTransactionsLog.getId().longValue());

		confirmResult.setTransactionTime(confirmTransactionsLog.getTransactiontime());
		confirmResult.setSourceMessage(transferConfirmation);
		confirmResult.setTransactionID(confirmTransactionsLog.getId().longValue());
		confirmResult.setTransID(txnDetails.getTransID());
		
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(txnDetails.getSourceMDN());
		

		addCompanyANDLanguageToResult(sourceMDN, confirmResult);
		
		
		transferConfirmation.setSourcePocketID(txnDetails.getSrcPocketId());
		transferConfirmation.setDestPocketID(txnDetails.getDestinationPocketId());
		
		// Changing the Service_charge_transaction_log status based on the response from Core engine. 
		ServiceChargeTxnLog sctl = transactionChargingService.getServiceChargeTransactionLog(transferConfirmation.getParentTransactionID());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				confirmResult.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return confirmResult;
			}

		} else {
			confirmResult.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return confirmResult;
		}
		
		transferConfirmation.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		log.info(String.format("NFCCardTopupHandlerImpl::handleNFCCardTopupConfirm() -- Sending NFC CardTopup Confirm to Backend (soureMDN:%s, srcPocketID:%s, amount:%s, transID:%s, destMDN:%s, destPocketID:%s, sctlID:%s, parentTxnId:%s, transferID:%s )",txnDetails.getSourceMDN(),txnDetails.getSourcePocketId(),txnDetails.getAmount().toString(),txnDetails.getTransID().toString(),txnDetails.getDestMDN(),txnDetails.getDestPocketId(),sctl.getId(),inquiryResult.getParentTransactionID(),inquiryResult.getTransferID()));
		
		CFIXMsg response = super.process(transferConfirmation);
		
		log.info(String.format("NFCCardTopupHandlerImpl::handleNFCCardTopupConfirm() -- got response from backend"));
		
		confirmResult.setMultixResponse(response);

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult() && sctl!=null) {
				transactionChargingService.confirmTheTransaction(sctl, transferConfirmation.getTransferID());
				commodityTransferService.addCommodityTransferToResult(confirmResult, transferConfirmation.getTransferID());
				confirmResult.setDebitAmount(sctl.getTransactionamount());
				confirmResult.setCreditAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
				confirmResult.setServiceCharge(sctl.getCalculatedcharge());
				confirmResult.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
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
		confirmResult.setMultixResponse(response);
		confirmResult.setSctlID(sctl.getId().longValue());
		confirmResult.setDestinationMDN(transferConfirmation.getDestMDN());
		confirmResult.setMessage(transactionResponse.getMessage());
		confirmResult.setCode(transactionResponse.getCode());
		confirmResult.setSourcePocket(inquiryResult.getSourcePocket());
		return confirmResult;
	}
	
	private boolean isNFCCardTopupInquirySuccessfull(XMLResult inquiryResult) {
		if (inquiryResult != null) {
			if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt
					.toString().equals(inquiryResult.getCode())) {
				return true;
			}
		}
		return false;
	}
	
}
