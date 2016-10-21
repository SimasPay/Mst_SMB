package com.mfino.cashin;

import java.math.BigDecimal;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
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
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.result.xmlresulttypes.wallet.WalletConfirmXMLResult;
import com.mfino.validators.DestMDNValidator;
import com.mfino.validators.PartnerValidator;
import com.mfino.validators.Validator;

public class CashinHandlerImpl extends FIXMessageHandler implements CashinHandler{

	private static Logger	log	= LoggerFactory.getLogger(CashinHandlerImpl.class);

	
	CMInterswitchCashin	  cashinDetails;
	ChannelCode	          channel;
	CMCashInInquiry	      cashinInquiry;

	SubscriberMdn	      partnerMDN;
	SubscriberMdn	      destMDN;

	Partner	              partner;

	//@Autowired
	//@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public void setTransactionChargingService(
			TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}

	public CommodityTransferService getCommodityTransferService() {
		return commodityTransferService;
	}

	public void setCommodityTransferService(
			CommodityTransferService commodityTransferService) {
		this.commodityTransferService = commodityTransferService;
	}

	public HierarchyService getHierarchyService() {
		return hierarchyService;
	}

	public void setHierarchyService(HierarchyService hierarchyService) {
		this.hierarchyService = hierarchyService;
	}

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
	}

	public TransactionLogService getTransactionLogService() {
		return transactionLogService;
	}

	public void setTransactionLogService(TransactionLogService transactionLogService) {
		this.transactionLogService = transactionLogService;
	}

	//@Autowired
	//@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	//@Autowired
	//@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;
	
	//@Autowired
	//@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	//@Autowired
	//@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	//@Autowired
	//@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;

	/*public CashinHandler(CMInterswitchCashin details, ChannelCode cc,String transactionIdentifier) {
		this.cashinDetails = details;
		this.channel = cc;
		this.cashinDetails.setTransactionIdentifier(transactionIdentifier);
	}*/

	public CashinHandlerImpl(){
		
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Result handle(CMInterswitchCashin details, ChannelCode cc,String transactionIdentifier) {

		this.cashinDetails = details;
		this.channel = cc;
		this.cashinDetails.setTransactionIdentifier(transactionIdentifier);
		
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
		//TransactionLogServiceImpl transactionLogServiceImpl = new TransactionLogServiceImpl();
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_InterswitchCashin, cashinDetails.DumpFields());
		cashinDetails.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(cashinDetails);
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setDestinationMDN(cashinDetails.getDestMDN());

		String originalDestMDN = cashinDetails.getDestMDN();
		
		String pIdStr = DAOFactory.getInstance().getSystemParameterDao().getSystemParameterByName(SystemParameterKeys.SERVICE_PARTNER__ID_KEY).getParametervalue();
		PartnerDAO pdao = DAOFactory.getInstance().getPartnerDAO();
		Partner cashinPartner = pdao.getById(Long.parseLong(pIdStr));
		if (cashinPartner == null) {
			log.info("integration partner is not available for the given institutioid" + cashinDetails.getInstitutionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			return result;
		}
		
		log.info("getting the partner mdn and setting it as sourcemdn in cashindetauls obejct");
		Set<SubscriberMdn> set = cashinPartner.getSubscriber().getSubscriberMdns();
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
		//SubscriberServiceImpl subscriberServiceImpl = new SubscriberServiceImpl();
		cashinDetails.setDestMDN(subscriberService.normalizeMDN(cashinDetails.getDestMDN()));

		log.info("validating dest mdn");
		DestMDNValidator destMdnValidator = new DestMDNValidator(cashinDetails.getDestMDN());
		validationResult = destMdnValidator.validate();
		if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
			log.info("dest mdn validation failed.result" + validationResult);
			result.setNotificationCode(validationResult);
			return result;
		}

		this.destMDN = destMdnValidator.getSubscriberMDN();

		SubscriberMdn sourceMDN = pValidator.getSubscriberMDN();
		SubscriberMdn destinationMDN = destMdnValidator.getSubscriberMDN();

		log.info("getting the emoney pocket of destinatiomdn");
		//PocketServiceImpl pocketService = new PocketServiceImpl();
		Pocket subPocket = pocketService.getDefaultPocket(destinationMDN, "1");
		if (subPocket == null) {
			log.info("the defualt emoney pocket for " + destinationMDN + " is null");
			result.setNotificationCode(CmFinoFIX.NotificationCode_DestinationEMoneyPocketNotFound);
			// saveActivitiesLog(result, destinationMDN);
			return result;
		}
		log.info("default emoney pocket for destmdn=" + this.destMDN + " is " + subPocket.getId());

		log.info("transactionchargingservice -->");

		Transaction transDetails = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setChannelCodeId(channel.getId().longValue());
		sc.setDestMDN(destinationMDN.getMdn());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
		sc.setTransactionTypeName(cashinDetails.getPaymentMethod());
		sc.setSourceMDN(pValidator.getSubscriberMDN().getMdn());
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
			String ppid = DAOFactory.getInstance().getSystemParameterDao().getSystemParameterByName(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY).getParametervalue();
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

		ServiceChargeTxnLog sctl = transDetails.getServiceChargeTransactionLog();

		saveIntegrationSummary(originalDestMDN, sctl);
		
		log.info("building CMCashinInquiry object for processing -->");
		CMCashInInquiry cashIn = new CMCashInInquiry();
		cashIn.setSourceMDN(pValidator.getSubscriberMDN().getMdn());
		cashIn.setDestMDN(destinationMDN.getMdn());
		cashIn.setAmount(cashinDetails.getAmount());
		cashIn.setCharges(transDetails.getAmountTowardsCharges());
		cashIn.setTransactionID(cashinDetails.getTransactionID());
		cashIn.setChannelCode(channel.getChannelcode());
		cashIn.setPin("a");
		cashIn.setSourcePocketID(partnerPocket.getId().longValue());
		cashIn.setDestPocketID(subPocket.getId().longValue());
		cashIn.setSourceApplication(cashinDetails.getSourceApplication());
		cashIn.setSourceMessage("from BSM");
		cashIn.setServletPath(cashinDetails.getServletPath());
		cashIn.setServiceChargeTransactionLogID(sctl.getId().longValue());
		cashIn.setIsSystemIntiatedTransaction(true);
		cashIn.setTransactionIdentifier(cashinDetails.getTransactionIdentifier());

		this.cashinInquiry = cashIn;

		log.info("sending cashin to backend -->");
		CFIXMsg response = super.process(cashIn);
		// Saves the Transaction Id returned from Back End
		TransactionResponse transactionResponse = checkBackEndResponse(response);

		log.info("received backend response");

		if (transactionResponse.getTransactionId() != null) {
			sctl.setTransactionid(transactionResponse.getTransactionId());
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
		//TransactionLogServiceImpl transactionLogServiceImpl = new TransactionLogServiceImpl();
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_InterswitchCashin, cashinDetails.DumpFields(), parentTxnId);
		cashinDetails.setTransactionID(transactionsLog.getId().longValue());

		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(cashinDetails);
		result.setTransactionID(transactionsLog.getId().longValue());

		log.info("constructing CMCashin object for cashin confirmation");
		CMCashIn cashin = new CMCashIn();
		cashin.setSourceMDN(this.partnerMDN.getMdn());
		cashin.setDestMDN(this.destMDN.getMdn());
		cashin.setParentTransactionID(parentTxnId);
		cashin.setTransferID(transferId);
		cashin.setConfirmed(confirmed);
		cashin.setChannelCode(channel.getChannelcode());
		cashin.setDestPocketID(cashinInquiry.getDestPocketID());
		cashin.setSourcePocketID(cashinInquiry.getSourcePocketID());
		cashin.setSourceApplication((int)channel.getChannelsourceapplication());
		cashin.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashin.setIsSystemIntiatedTransaction(true);
		cashin.setPassword("");
		cashin.setTransactionIdentifier(cashinInquiry.getTransactionIdentifier());

		// Changing the Service_charge_transaction_log status based on the
		// response from Core engine.
		log.info("getting sctl from id");

		ServiceChargeTxnLog sctl = transactionChargingService.getServiceChargeTransactionLog(parentTxnId);
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

		cashin.setServiceChargeTransactionLogID(sctl.getId().longValue());
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
				result.setDebitAmount(sctl.getTransactionamount());
				result.setCreditAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
				result.setServiceCharge(sctl.getCalculatedcharge());
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
		result.setSctlID(sctl.getId().longValue());
		result.setNotificationCode(Integer.parseInt(transactionResponse.getCode()));

		return result;

	}

	private void saveIntegrationSummary(String originalDestMDN, ServiceChargeTxnLog sctl) {
	    IntegrationSummaryDao isdao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummary isummary = new IntegrationSummary();
		isummary.setSctlid(sctl.getId());
		isummary.setReconcilationid1(originalDestMDN);
		isummary.setReconcilationid2(cashinDetails.getReceiptNo());
		isummary.setReconcilationid3(cashinDetails.getPaymentLogID());
		isdao.save(isummary);
    }
	
}
