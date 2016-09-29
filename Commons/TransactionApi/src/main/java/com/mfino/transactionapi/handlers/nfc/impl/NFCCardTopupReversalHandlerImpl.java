package com.mfino.transactionapi.handlers.nfc.impl;

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
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMNFCCardTopupReversal;
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
import com.mfino.transactionapi.handlers.nfc.NFCCardTopupReversalHandler;
import com.mfino.transactionapi.result.xmlresulttypes.nfc.NFCCardTopupReversalXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

@org.springframework.stereotype.Service("NFCCardTopupReversalHandlerImpl")
public class NFCCardTopupReversalHandlerImpl  extends FIXMessageHandler implements NFCCardTopupReversalHandler{
	
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
	
	

	public NFCCardTopupReversalHandlerImpl()
	{
		
	}

	public Result handle(TransactionDetails txnDetails)
	{
		log.info(String.format("NFCCardTopupReversalHandlerImpl::handle() -- Handling NFC CardTopup Reversal request (soureMDN:%s, sourceCardPan:%s, transID:%s, parentTransID:%s )",txnDetails.getSourceMDN(),txnDetails.getCardPAN(),txnDetails.getTransID().toString(),txnDetails.getParentTransID().toString()) );
		XMLResult inquiryResult = new NFCCardTopupReversalXMLResult();
		
		ChannelCode cc =  txnDetails.getCc();
		CMNFCCardTopupReversal nfcCardTopupReversal = new CMNFCCardTopupReversal();
		nfcCardTopupReversal.setSourceMDN(subscriberService.normalizeMDN(txnDetails.getSourceMDN()));
		nfcCardTopupReversal.setSourceCardPAN(txnDetails.getCardPAN());
		nfcCardTopupReversal.setIntegrationTransactionID(Long.parseLong(txnDetails.getTransID()));
		nfcCardTopupReversal.setChannelCode(cc.getChannelcode());
		nfcCardTopupReversal.setSourceApplication((int)cc.getChannelsourceapplication());
		nfcCardTopupReversal.setParentTransID(Long.parseLong(txnDetails.getParentTransID()));
		
		
		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_NFCCardTopupReversal, nfcCardTopupReversal.DumpFields());
		
		nfcCardTopupReversal.setTransactionID(transactionsLog.getID());
		inquiryResult = (XMLResult) handleNFCCardTopupReversalInquiry(txnDetails);
		
		if(!isNFCCardTopupReversalInquirySuccessfull(inquiryResult)){
			return inquiryResult;
		}
		log.info(String.format("NFCCardTopupReversalHandlerImpl::handle() -- Handling NFC CardTopup Reversal request (soureMDN:%s, sourceCardPan:%s, transID:%s, parentTransID:%s ), inquiry Success ",txnDetails.getSourceMDN(),txnDetails.getCardPAN(),txnDetails.getTransID().toString(),txnDetails.getParentTransID().toString()) );
		
		//Inquiry Success, send Confirmation for NFC Card Topup
		
		XMLResult confirmResult = (XMLResult)handleNFCCardTopupReversalConfirm(txnDetails,inquiryResult);
		if(confirmResult.getResponseStatus() != null && confirmResult.getResponseStatus().equals(GeneralConstants.RESPONSE_CODE_SUCCESS)){
			confirmResult.setMultixResponse(null);
			confirmResult.setNotificationCode(CmFinoFIX.NotificationCode_NFCCardTopupReversalSuccess);
		}
		log.info(String.format("NFCCardTopupReversalHandlerImpl::handle() -- Completed NFC CardTopup Reversal request (soureMDN:%s, sourceCardPan:%s, transID:%s, parentTransID:%s ) and returning the result ",txnDetails.getSourceMDN(),txnDetails.getCardPAN(),txnDetails.getTransID().toString(),txnDetails.getParentTransID().toString()) );
		return confirmResult;
	}
	
	
	
	private Result handleNFCCardTopupReversalInquiry(TransactionDetails txnDetails){
		log.info(String.format("NFCCardTopupReversalHandlerImpl::handleNFCCardTopupReversalInquiry() -- Handling NFC CardTopup Reversal Inquiry request (soureMDN:%s, sourceCardPan:%s, transID:%s, parentTransID:%s )",txnDetails.getSourceMDN(),txnDetails.getCardPAN(),txnDetails.getTransID().toString(),txnDetails.getParentTransID().toString()) );
		XMLResult result = new NFCCardTopupReversalXMLResult();
		ChannelCode cc =  txnDetails.getCc();
		result.setTransID(txnDetails.getTransID());
		result.setParentTransID(txnDetails.getParentTransID());
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		result.setCardPan(txnDetails.getCardPAN());
		
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		sctlQuery.setIntegrationTxnID(Long.parseLong(txnDetails.getTransID()));
		List<ServiceChargeTransactionLog> resultSctlList = SCTLService.getByQuery(sctlQuery);
		if(resultSctlList != null && resultSctlList.size() != 0){
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransIDAlreadyExist);
			return result;
		}
		sctlQuery.setIntegrationTxnID(null);
		resultSctlList = null;
				
		ServiceChargeTransactionLog parentSCTL = null;
		sctlQuery.setIntegrationTxnID(Long.parseLong(txnDetails.getParentTransID()));
		resultSctlList = SCTLService.getByQuery(sctlQuery);
		if(resultSctlList != null && resultSctlList.size() != 0){
			parentSCTL = resultSctlList.get(0);
		}
		
		if(parentSCTL == null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_ParentTransIDDoesNotExist);
			return result;
		}
		
		if(!isExistingSctlEligibleForReversal(parentSCTL)){
			result.setNotificationCode(CmFinoFIX.NotificationCode_ParentTransIDExistButTransactionIsNotSuccessfull);
			return result;
		}
		
		if(isReversalAlreadyDone(parentSCTL)){
			result.setNotificationCode(CmFinoFIX.NotificationCode_NFCCardTopupReversalAlreadyDone);
			return result;
		}
		
		String partnerCode = systemParametersService.getString(SystemParameterKeys.NFC_CARD_TOPUP_PARTNER_CODE);
		Partner pt = partnerService.getPartnerByPartnerCode(partnerCode);
		Set<SubscriberMdn> subscriberSet= pt.getSubscriber().getSubscriberMdns();
		String srcMdn = null;
		if(subscriberSet != null && subscriberSet.size() != 0){
			srcMdn = subscriberSet.iterator().next().getMdn();
		}
		
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(srcMdn);
		Integer validationResult=transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			if(!CmFinoFIX.NotificationCode_MDNIsNotActive.equals(validationResult)){
				result.setNotificationCode(validationResult);
				return result;
			}
		}
		
		txnDetails.setDestMDN(srcMdn);
		
		String destMdn =  subscriberService.normalizeMDN(txnDetails.getSourceMDN());
		SubscriberMdn destMDN = subscriberMdnService.getByMDN(destMdn);
		validationResult= transactionApiValidationService.validateSubscriberAsDestination(destMDN);
		
		if((CmFinoFIX.NotificationCode_DestinationMDNNotFound.equals(validationResult) ||
				CmFinoFIX.NotificationCode_SubscriberNotRegistered.equals(validationResult))){
			result.setNotificationCode(validationResult);
			return result;
		}else if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			if(destMDN != null)
			{
				String receiverAccountName = destMDN.getSubscriber().getFirstname() + " " + destMDN.getSubscriber().getLastname();
				result.setReceiverAccountName(receiverAccountName);
			}
			return result;
		}
		
		txnDetails.setSourceMDN(destMdn);
		
		CMBankAccountToBankAccount bankAccountToBankAccount = new CMBankAccountToBankAccount();
		bankAccountToBankAccount.setSourceMDN(srcMdn);
		bankAccountToBankAccount.setDestMDN(destMdn);
		bankAccountToBankAccount.setAmount(parentSCTL.getTransactionAmount());
		bankAccountToBankAccount.setIsSystemIntiatedTransaction(true);
		bankAccountToBankAccount.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		bankAccountToBankAccount.setSourceMessage("NFC CardTopup Reversal Transfer Inquiry");
		bankAccountToBankAccount.setSourceApplication((int)cc.getChannelsourceapplication());
		bankAccountToBankAccount.setChannelCode(cc.getChannelcode());
		bankAccountToBankAccount.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		
		
		result.setDestinationMDN(destMdn);
		result.setSourceMessage(bankAccountToBankAccount);
		
		Pocket destPocket = null;
		if (StringUtils.isNotBlank(txnDetails.getCardPAN())) {
			PocketQuery pkQuery = new PocketQuery();
			pkQuery.setCardPan(txnDetails.getCardPAN());
			List<Pocket> pkList =  pocketService.get(pkQuery);
			if(pkList != null && pkList.size() != 0){
				destPocket = pkList.get(0);
			}
		}
					
		if(destPocket==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFound);
			return result;
		}
		result.setCardAlias(destPocket.getCardalias());				
		
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
		Pocket srcPocket = null;
		if(ps != null){
			srcPocket = ps.getPocketByDestpocketid();
		}
		
		if(srcPocket == null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
			return result;
		}		
		if (!(srcPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
			return result;
		}
		
		result.setSourcePocket(srcPocket);
		
		
		bankAccountToBankAccount.setSourcePocketID(srcPocket.getId().longValue());
		bankAccountToBankAccount.setDestPocketID(destPocket.getId().longValue());
		bankAccountToBankAccount.setIsSystemIntiatedTransaction(true);
		bankAccountToBankAccount.setPin("");
		
		txnDetails.setSrcPocketId(destPocket.getId().longValue());
		txnDetails.setDestinationPocketId(srcPocket.getId().longValue());
		
		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccount, bankAccountToBankAccount.DumpFields());
		bankAccountToBankAccount.setTransactionID(transactionsLog.getID());
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());
		result.setDestinationMDN(bankAccountToBankAccount.getDestMDN());
		
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(bankAccountToBankAccount.getSourceMDN());
		sc.setDestMDN(bankAccountToBankAccount.getDestMDN());
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_NFC);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_TOPUP_REVERSAL);
		sc.setTransactionAmount(bankAccountToBankAccount.getAmount());
		sc.setTransactionLogId(transactionsLog.getID());
		
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
		
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		bankAccountToBankAccount.setServiceChargeTransactionLogID(sctl.getID());
		sctl.setIntegrationTransactionID(Long.parseLong(txnDetails.getTransID()));
		sctl.setParentIntegrationTransID(Long.parseLong(txnDetails.getParentTransID()));
		transactionChargingService.saveServiceTransactionLog(sctl);
		log.info(String.format("NFCCardTopupReversalHandlerImpl::handleNFCCardTopupInquiry() -- Sending NFC CardTopup Inquiry to Backend (soureMDN:%s, srcPocketID:%s, amount:%s, transID:%s, destMDN:%s, destPocketID:%s, sctlID:%s )",txnDetails.getSourceMDN(),srcPocket.getId().toString(),sctl.getTransactionAmount().toString(),txnDetails.getTransID().toString(),destMDN.getMdn(),destPocket.getId(),sctl.getID()));
		
		CFIXMsg response = super.process(bankAccountToBankAccount);
		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			bankAccountToBankAccount.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
			log.info(String.format("NFCCardTopupReversalHandlerImpl::handleNFCCardTopupReversalInquiry() -- got response from backend and saving sctl(ID:%s)",sctl.getID()));
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
		result.setSctlID(sctl.getID());
		return result;		
		
	}
	
	private Result handleNFCCardTopupReversalConfirm(TransactionDetails txnDetails,XMLResult inquiryResult){
		log.info(String.format("NFCCardTopupReversalHandlerImpl::handleNFCCardTopupReversalConfirm() -- Handling NFC CardTopup Reversal Confirm request (soureMDN:%s, sourceCardPan:%s, transID:%s, parentTransID:%s )",txnDetails.getSourceMDN(),txnDetails.getCardPAN(),txnDetails.getTransID().toString(),txnDetails.getParentTransID().toString()) );
		XMLResult confirmResult = new NFCCardTopupReversalXMLResult();
		ChannelCode cc =  txnDetails.getCc();
		confirmResult.setTransID(txnDetails.getTransID());
		confirmResult.setParentTransID(txnDetails.getParentTransID());
		confirmResult.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		confirmResult.setSourceMDN(txnDetails.getSourceMDN());
		confirmResult.setCardPan(txnDetails.getCardPAN());
		confirmResult.setCardAlias(inquiryResult.getCardAlias());
		
		CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
		transferConfirmation.setSourceMDN(txnDetails.getDestMDN());
		transferConfirmation.setDestMDN(txnDetails.getSourceMDN());
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setTransferID(new Long(inquiryResult.getTransferID()));
		transferConfirmation.setConfirmed(true);
		transferConfirmation.setSourceApplication((int)cc.getChannelsourceapplication());
		transferConfirmation.setChannelCode(cc.getChannelcode());
		transferConfirmation.setParentTransactionID(new Long(inquiryResult.getParentTransactionID()));
		transferConfirmation.setUICategory(CmFinoFIX.TransactionUICategory_NFC_Card_Topup_Reversal);
		
		TransactionsLog confirmTransactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation,transferConfirmation.DumpFields(), transferConfirmation.getParentTransactionID());
		
		transferConfirmation.setTransactionID(confirmTransactionsLog.getID());

		confirmResult.setTransactionTime(confirmTransactionsLog.getTransactionTime());
		confirmResult.setSourceMessage(transferConfirmation);
		confirmResult.setTransactionID(confirmTransactionsLog.getID());
		
		
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(txnDetails.getSourceMDN());
		

		addCompanyANDLanguageToResult(sourceMDN, confirmResult);
		
		
		transferConfirmation.setSourcePocketID(txnDetails.getDestinationPocketId());
		transferConfirmation.setDestPocketID(txnDetails.getSrcPocketId());
		
		// Changing the Service_charge_transaction_log status based on the response from Core engine. 
		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(transferConfirmation.getParentTransactionID());
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
		
		transferConfirmation.setServiceChargeTransactionLogID(sctl.getID());
		
		//log.info(String.format("NFCCardTopupHandlerImpl::handleNFCCardTopupConfirm() -- Sending NFC CardTopup Reversal Confirm to Backend (soureMDN:%s, srcPocketID:%s, amount:%s, transID:%s, destMDN:%s, destPocketID:%s, sctlID:%s, parentTxnId:%s, transferID:%s )",txnDetails.getSourceMDN(),srcPocket.getID().toString(),txnDetails.getAmount().toString(),txnDetails.getTransID().toString(),destMdn,destPocket.getID(),sctl.getID(),inquiryResult.getParentTransactionID(),inquiryResult.getTransferID()));
		
		CFIXMsg response = super.process(transferConfirmation);
		
		log.info(String.format("NFCCardTopupReversalHandlerImpl::handleNFCCardTopupReversalConfirm() -- got response from backend"));
		
		confirmResult.setMultixResponse(response);

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult() && sctl!=null) {
				transactionChargingService.confirmTheTransaction(sctl, transferConfirmation.getTransferID());
				commodityTransferService.addCommodityTransferToResult(confirmResult, transferConfirmation.getTransferID());
				confirmResult.setDebitAmount(sctl.getTransactionAmount());
				confirmResult.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
				confirmResult.setServiceCharge(sctl.getCalculatedCharge());
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
		confirmResult.setSctlID(sctl.getID());
		confirmResult.setDestinationMDN(transferConfirmation.getDestMDN());
		confirmResult.setMessage(transactionResponse.getMessage());
		confirmResult.setCode(transactionResponse.getCode());
		confirmResult.setSourcePocket(inquiryResult.getSourcePocket());		
		return confirmResult;
	}
	
	private boolean isNFCCardTopupReversalInquirySuccessfull(XMLResult inquiryResult) {
		if (inquiryResult != null) {
			if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt
					.toString().equals(inquiryResult.getCode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isExistingSctlEligibleForReversal(ServiceChargeTransactionLog sctl){
		return isSctlSuccsessFull(sctl);
	}
	
	private boolean isReversalAlreadyDone(ServiceChargeTransactionLog sctl){
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		sctlQuery.setParentIntegrationTransID(sctl.getIntegrationTransactionID());
		List<ServiceChargeTransactionLog> sctlList = SCTLService.getByQuery(sctlQuery);
		for(ServiceChargeTransactionLog sc: sctlList){
			if(isSctlSuccsessFull(sc)){
				return true;
			}
		}		
		return false;	
	}
	
	private boolean isSctlSuccsessFull(ServiceChargeTransactionLog sctl){
		if(sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Confirmed)){
			return true;
		}else if(sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Completed)){
			return true;
		}else if(sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Started)){
			return true;
		}else if(sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Failed)){
			return true;
		}
		return false;
	}
	
}
