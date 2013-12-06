package com.mfino.cashin;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashIn;
import com.mfino.fix.CmFinoFIX.CMCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashin;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.impl.PocketServiceImpl;
import com.mfino.service.impl.SubscriberServiceImpl;
import com.mfino.service.impl.TransactionLogServiceImpl;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.result.xmlresulttypes.wallet.WalletConfirmXMLResult;
import com.mfino.validators.DestMDNValidator;
import com.mfino.validators.PartnerValidator;
import com.mfino.validators.Validator;

public class CashinHandler extends FIXMessageHandler {

	private static Logger	log	= LoggerFactory.getLogger(CashinHandler.class);

	
	CMInterswitchCashin	  cashinDetails;
	ChannelCode	          channel;
	CMCashInInquiry	      cashinInquiry;

	SubscriberMDN	      partnerMDN;
	SubscriberMDN	      destMDN;

	Partner	              partner;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;

	public CashinHandler(CMInterswitchCashin details, ChannelCode cc,String transactionIdentifier) {
		this.cashinDetails = details;
		this.channel = cc;
		this.cashinDetails.setTransactionIdentifier(transactionIdentifier);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Result handle() {

		TransferInquiryXMLResult inquiryResult = doInquiry();

		if (!CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(inquiryResult.getCode())) {
			return inquiryResult;
		}

		WalletConfirmXMLResult confirmResult = doConfirmation(inquiryResult.getTransferID(), inquiryResult.getParentTransactionID(), true);

		return confirmResult;

	}

	private TransferInquiryXMLResult doInquiry() {
		log.info("Handling interswitch cashin inquiry request::" + "To " + cashinDetails.getDestMDN() + " For Amount = " + cashinDetails.getAmount());

		TransferInquiryXMLResult result = new TransferInquiryXMLResult();
		TransactionLogServiceImpl transactionLogServiceImpl = new TransactionLogServiceImpl();
		TransactionsLog transactionsLog = transactionLogServiceImpl.saveTransactionsLog(CmFinoFIX.MessageType_InterswitchCashin, cashinDetails.DumpFields());
		cashinDetails.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(cashinDetails);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setDestinationMDN(cashinDetails.getDestMDN());

		String originalDestMDN = cashinDetails.getDestMDN();
		
		String pIdStr = DAOFactory.getInstance().getSystemParameterDao().getSystemParameterByName(SystemParameterKeys.SERVICE_PARTNER__ID_KEY).getParameterValue();
		PartnerDAO pdao = DAOFactory.getInstance().getPartnerDAO();
		Partner cashinPartner = pdao.getById(Long.parseLong(pIdStr));
		if (cashinPartner == null) {
			log.info("integration partner is not available for the given institutioid" + cashinDetails.getInstitutionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			return result;
		}
		
		log.info("getting the partner mdn and setting it as sourcemdn in cashindetauls obejct");
		Set<SubscriberMDN> set = cashinPartner.getSubscriber().getSubscriberMDNFromSubscriberID();
		PartnerValidator pValidator = new PartnerValidator(set.iterator().next());
		cashinDetails.setSourceMDN(pValidator.getSubscriberMDN().toString());

		this.partnerMDN = pValidator.getSubscriberMDN();
		this.partner = cashinPartner;
		log.info("mdn of the partner=" + this.partnerMDN);

		log.info("validating the partner");
		Validator validator = new Validator();
		validator.addValidator(pValidator);
		Integer validationResult = validator.validateAll();
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.info("partner validation failed.result=" + validationResult);
			validationResult = processValidationResultForPartner(validationResult);
			result.setNotificationCode(validationResult);
			return result;
		}

		log.info("normalizing destination mdn");
		SubscriberServiceImpl subscriberServiceImpl = new SubscriberServiceImpl();
		cashinDetails.setDestMDN(subscriberServiceImpl.normalizeMDN(cashinDetails.getDestMDN()));

		log.info("validating dest mdn");
		DestMDNValidator destMdnValidator = new DestMDNValidator(cashinDetails.getDestMDN());
		validationResult = destMdnValidator.validate();
		if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
			log.info("dest mdn validation failed.result" + validationResult);
			result.setNotificationCode(validationResult);
			return result;
		}

		this.destMDN = destMdnValidator.getSubscriberMDN();

		SubscriberMDN sourceMDN = pValidator.getSubscriberMDN();
		SubscriberMDN destinationMDN = destMdnValidator.getSubscriberMDN();

		log.info("getting the emoney pocket of destinatiomdn");
		PocketServiceImpl pocketService = new PocketServiceImpl();
		Pocket subPocket = pocketService.getDefaultPocket(destinationMDN, "1");
		if (subPocket == null) {
			log.info("the defualt emoney pocket for " + destinationMDN + " is null");
			result.setNotificationCode(CmFinoFIX.NotificationCode_DestinationEMoneyPocketNotFound);
			// saveActivitiesLog(result, destinationMDN);
			return result;
		}
		log.info("default emoney pocket for destmdn=" + this.destMDN + " is " + subPocket.getID());

		log.info("transactionchargingservice -->");

		Transaction transDetails = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setChannelCodeId(channel.getID());
		sc.setDestMDN(destinationMDN.getMDN());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
		sc.setTransactionTypeName(cashinDetails.getPaymentMethod());
		sc.setSourceMDN(pValidator.getSubscriberMDN().getMDN());
		sc.setTransactionAmount(cashinDetails.getAmount());
		sc.setTransactionLogId(cashinDetails.getTransactionID());
		sc.setIntegrationTxnID(Long.parseLong(cashinDetails.getPaymentLogID()));
		sc.setTransactionIdentifier(cashinDetails.getTransactionIdentifier());

		log.info("hierarchy service -->");
		validationResult = hierarchyService.validate(sourceMDN.getSubscriber(), destinationMDN.getSubscriber(), sc.getServiceName(),
		        sc.getTransactionTypeName());
		if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
			log.info("hierarchy services validation failed.result=" + validationResult);
			result.setNotificationCode(validationResult);
			return result;
		}

		log.info("getting the partner's agent service pocket");
		Pocket partnerPocket;
		try {
			String ppid = DAOFactory.getInstance().getSystemParameterDao().getSystemParameterByName(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY).getParameterValue();
			PocketDAO pocketdao = DAOFactory.getInstance().getPocketDAO();
			partnerPocket = pocketdao.getById(Long.parseLong(ppid));
		}
		catch (Exception e) {
			log.error("Exception occured in getting Source Pocket", e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			// saveActivitiesLog(result, destinationMDN);
			return result;
		}

		try {
			log.info("getting charge from charging service");
			transDetails = transactionChargingService.getCharge(sc);
			// cashinDetails.setAmount(transDetails.getAmountToCredit());
			// cashinDetails.setCharges(transDetails.getAmountTowardsCharges());
		}
		catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges", e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			// saveActivitiesLog(result, destinationMDN);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			// saveActivitiesLog(result, destinationMDN);
			return result;
		}

		ServiceChargeTransactionLog sctl = transDetails.getServiceChargeTransactionLog();

		saveIntegrationSummary(originalDestMDN, sctl);
		
		log.info("building CMCashinInquiry object for processing -->");
		CMCashInInquiry cashIn = new CMCashInInquiry();
		cashIn.setSourceMDN(pValidator.getSubscriberMDN().getMDN());
		cashIn.setDestMDN(destinationMDN.getMDN());
		cashIn.setAmount(cashinDetails.getAmount());
		cashIn.setCharges(transDetails.getAmountTowardsCharges());
		cashIn.setTransactionID(cashinDetails.getTransactionID());
		cashIn.setChannelCode(channel.getChannelCode());
		cashIn.setPin("a");
		cashIn.setSourcePocketID(partnerPocket.getID());
		cashIn.setDestPocketID(subPocket.getID());
		cashIn.setSourceApplication(cashinDetails.getSourceApplication());
		cashIn.setSourceMessage("from BSM");
		cashIn.setServletPath(cashinDetails.getServletPath());
		cashIn.setServiceChargeTransactionLogID(sctl.getID());
		cashIn.setIsSystemIntiatedTransaction(true);
		cashIn.setTransactionIdentifier(cashinDetails.getTransactionIdentifier());

		this.cashinInquiry = cashIn;

		log.info("sending cashin to backend -->");
		CFIXMsg response = super.process(cashIn);
		// Saves the Transaction Id returned from Back End
		TransactionResponse transactionResponse = checkBackEndResponse(response);

		log.info("received backend response");

		if (transactionResponse.getTransactionId() != null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			cashinDetails.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}

		transactionChargingService.updateTransactionStatus(transactionResponse, sctl);
		
		log.info("the notificationcode returned by backend-->" + transactionResponse.getCode());

		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setMultixResponse(response);
		result.setDebitAmount(transDetails.getAmountToDebit());
		result.setCreditAmount(transDetails.getAmountToCredit());
		result.setServiceCharge(transDetails.getAmountTowardsCharges());
		//addCompanyANDLanguageToResult(result);
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		return result;

	}

	private WalletConfirmXMLResult doConfirmation(Long transferId, Long parentTxnId, boolean confirmed) {
		log.info("Handling interswitch cashin Confirmation  request");
		WalletConfirmXMLResult result = new WalletConfirmXMLResult();
		TransactionLogServiceImpl transactionLogServiceImpl = new TransactionLogServiceImpl();
		TransactionsLog transactionsLog = transactionLogServiceImpl.saveTransactionsLog(CmFinoFIX.MessageType_InterswitchCashin, cashinDetails.DumpFields(), parentTxnId);
		cashinDetails.setTransactionID(transactionsLog.getID());

		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(cashinDetails);
		result.setTransactionID(transactionsLog.getID());

		log.info("constructing CMCashin object for cashin confirmation");
		CMCashIn cashin = new CMCashIn();
		cashin.setSourceMDN(this.partnerMDN.getMDN());
		cashin.setDestMDN(this.destMDN.getMDN());
		cashin.setParentTransactionID(parentTxnId);
		cashin.setTransferID(transferId);
		cashin.setConfirmed(confirmed);
		cashin.setChannelCode(channel.getChannelCode());
		cashin.setDestPocketID(cashinInquiry.getDestPocketID());
		cashin.setSourcePocketID(cashinInquiry.getSourcePocketID());
		cashin.setSourceApplication(channel.getChannelSourceApplication());
		cashin.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashin.setIsSystemIntiatedTransaction(true);
		cashin.setPassword("");
		cashin.setTransactionIdentifier(cashinInquiry.getTransactionIdentifier());

		// Changing the Service_charge_transaction_log status based on the
		// response from Core engine.
		log.info("getting sctl from id");

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(parentTxnId);
		if (sctl != null) {
			if (CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				log.info("changing sctl status to processing");
				transactionChargingService.chnageStatusToProcessing(sctl);
			}
			else {
				log.info("transfer record changed status");
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				// saveActivitiesLog(result, partnerMDN);
				return result;
			}
		}
		else {
			log.error("transfer record not found");
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			// saveActivitiesLog(result, partnerMDN);
			return result;
		}

		cashin.setServiceChargeTransactionLogID(sctl.getID());
		// cashin.setSourcePocketID(agentPocket.getID());
		CFIXMsg response = super.process(cashin);
		result.setMultixResponse(response);

		// Changing the Service_charge_transaction_log status based on the
		// response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult() && sctl != null) {
				transactionChargingService.confirmTheTransaction(sctl, transferId);
				commodityTransferService.addCommodityTransferToResult(result, transferId);
				result.setDebitAmount(sctl.getTransactionAmount());
				result.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
				result.setServiceCharge(sctl.getCalculatedCharge());
			}
			else {
				String errorMsg = transactionResponse.getMessage();
				// As the length of the Failure reason column is 255, we are
				// trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				if (sctl != null) {
					transactionChargingService.failTheTransaction(sctl, errorMsg);
				}
			}
		}
		result.setMessage(transactionResponse.getMessage());
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setMultixResponse(response);
		//addCompanyANDLanguageToResult(result);
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setSctlID(sctl.getID());
		result.setNotificationCode(Integer.parseInt(transactionResponse.getCode()));

		return result;

	}

	private void saveIntegrationSummary(String originalDestMDN, ServiceChargeTransactionLog sctl) {
	    IntegrationSummaryDao isdao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummary isummary = new IntegrationSummary();
		isummary.setSctlId(sctl.getID());
		isummary.setReconcilationID1(originalDestMDN);
		isummary.setReconcilationID2(cashinDetails.getReceiptNo());
		isummary.setReconcilationID3(cashinDetails.getPaymentLogID());
		isdao.save(isummary);
    }
	
}
