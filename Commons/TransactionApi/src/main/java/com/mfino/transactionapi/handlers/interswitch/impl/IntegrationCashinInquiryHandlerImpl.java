package com.mfino.transactionapi.handlers.interswitch.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashin;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.service.IntegrationPartnerMappingService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.interswitch.IntegrationCashinInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
/**
 * This the Interswitch cashIn inquiry handler that will called from transaction aware handlers presently.Will handle the inquiry part of transaction.
 * The way to use this class is to use either the handle method or use the preprocess,communicate and postprocess methods in the same order
 * @author Sreenath
 *
 */
@Service("IntegrationCashinInquiryHandlerImpl")
public class IntegrationCashinInquiryHandlerImpl extends FIXMessageHandler implements IntegrationCashinInquiryHandler{

	private static Logger	log	= LoggerFactory.getLogger(IntegrationCashinInquiryHandlerImpl.class);

	
	@Autowired
	@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;
	
 	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("IntegrationPartnerMappingServiceImpl")
	private IntegrationPartnerMappingService integrationPartnerMappingService;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	 private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;

	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService ;

	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService ;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParameterService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;

	/**
	 * To be called first in this handler before communicate or postprocess
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public TransferInquiryXMLResult preprocess(TransactionDataContainerImpl cashinDataConatiner, ChannelCode channel) {
		TransferInquiryXMLResult result = new TransferInquiryXMLResult();
		if(cashinDataConatiner==null || channel==null){
			log.error("Input data is null.cashinDataConatiner:"+cashinDataConatiner+"channel:"+channel);
			throw new IllegalArgumentException();
		}
		CMInterswitchCashin cashinDetails = (CMInterswitchCashin) cashinDataConatiner.getMsg();
		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_InterswitchCashin, cashinDetails.DumpFields());
		cashinDetails.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(cashinDetails);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setDestinationMDN(cashinDetails.getDestMDN());
		
		
		try{
			log.info("validating for duplicate paymentlogid");
			if(cashinDetails.getPaymentLogID().isEmpty()){
				log.info("Payment Log ID is empty");
				result.setCode(CmFinoFIX.NotificationCode_InvalidPaymentLogID.toString());
				return result;
			}
			//incase we receive duplicate paymentlogid
			//return the old one's status			

			ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
			sctlQuery.setIntegrationTxnID(Long.parseLong(cashinDetails.getPaymentLogID()));
			sctlQuery.setInfo1(cashinDetails.getCustReference());
			List<ServiceChargeTransactionLog> oldsctlList = sctlService.getByQuery(sctlQuery);
			
			ServiceChargeTransactionLog oldsctl = null;
			if(!oldsctlList.isEmpty()){
				oldsctl = oldsctlList.get(0); // Only one match would be there as we do not allow duplicate entry
			}
			
			if (oldsctl != null) {
				log.warn("duplicate paymentlogid received. so returning the status of the original transaction");
				result.setTransferID(oldsctl.getID());
				if (oldsctl.getStatus().equals(CmFinoFIX.SCTLStatus_Confirmed) || oldsctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Completed)
						|| oldsctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Started))
					result.setCode(CmFinoFIX.NotificationCode_CashInToEMoneyCompletedToSender.toString());
				else
					result.setCode(CmFinoFIX.NotificationCode_Failure.toString());
				
				log.info("oldsctl status: " + oldsctl.getStatus());
				return result;
				
			}
		}
		catch(Exception ex){
			log.info("not a duplicate paymentlogid");
		}

		String pIdStr = systemParameterService.getString(SystemParameterKeys.SERVICE_PARTNER__ID_KEY);
		Partner cashinPartner = partnerService.getPartnerById(Long.parseLong(pIdStr));
 		SubscriberMdn sourceMDN = cashinPartner.getSubscriber().getSubscriberMdns().iterator().next();

		log.info("transactionchargingservice -->");
		ServiceCharge sc = new ServiceCharge();
		sc.setChannelCodeId(channel.getId().longValue());
		sc.setDestMDN(cashinDetails.getDestMDN());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
		sc.setTransactionTypeName(cashinDetails.getPaymentMethod());
		sc.setSourceMDN(sourceMDN.getMdn());
		sc.setTransactionAmount(cashinDetails.getAmount());
		// sc.setMfsBillerCode(cashinDetails.getPartnerCode());
		sc.setTransactionLogId(cashinDetails.getTransactionID());
		sc.setIntegrationTxnID(Long.parseLong(cashinDetails.getPaymentLogID()));
		sc.setTransactionIdentifier(cashinDetails.getTransactionIdentifier());
		
		Transaction transDetails = null;

		try {
			log.info("getting charge from charging service");
			transDetails = transactionChargingService.getChargeDetails(sc);
			
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges", e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}

		ServiceChargeTransactionLog sctl = transDetails.getServiceChargeTransactionLog();
		
		log.info("retrieving integration partner for institutionID" + cashinDetails.getInstitutionID());
		
		//Removed the check as per request
		/*IntegrationPartnerMapping ipm = integrationPartnerMappingService.getByInstitutionID(cashinDetails.getInstitutionID());
		if (ipm == null) {
			log.info("integration partner is not available for the given institutionId" + cashinDetails.getInstitutionID());
			result.setCode(CmFinoFIX.NotificationCode_PartnerNotFound.toString());
			return result;
		}*/

		Integer validationResult = transactionApiValidationService.validatePartnerByPartnerType(cashinPartner);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.info("Institution Partner is not Active");
			result.setNotificationCode(validationResult);
			transactionChargingService.failTheTransaction(sctl, "Institution Partner is not Active. Error code " + validationResult);
			return result;
		}

		log.info("getting the partner mdn and setting it as sourcemdn in cashindetails object");
		//Set<SubscriberMDN> set = cashinPartner.getSubscriber().getSubscriberMDNFromSubscriberID();
 		cashinDetails.setSourceMDN(sourceMDN.getMdn());
		addCompanyANDLanguageToResult(sourceMDN,result);

		validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!validationResult.equals(CmFinoFIX.ResponseCode_Success)){
			result.setNotificationCode(validationResult);
			transactionChargingService.failTheTransaction(sctl, "validateSubscriberAsSource failed. Error code " + validationResult);
			return result;
		}

		cashinDataConatiner.setPartnerMDN(sourceMDN);

		log.info("mdn of the partner=" +cashinDataConatiner.getPartnerMDN().getMdn());

		log.info("validating the partner");
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.info("partner validation failed.result=" + validationResult);
			validationResult = processValidationResultForPartner(validationResult);
			result.setCode(validationResult.toString());
			transactionChargingService.failTheTransaction(sctl, "validatePartner failed. Error code " + validationResult);
			return result;
		}

		log.info("normalizing destination mdn");

		cashinDetails.setDestMDN(subscriberService.normalizeMDN(cashinDetails.getDestMDN()));
		SubscriberMdn destinationMDN = subscriberMdnService.getByMDN(cashinDetails.getDestMDN());
		log.info("validating dest mdn");
		
		validationResult = transactionApiValidationService.validateSubscriberAsDestination(destinationMDN);
		if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
			log.info("dest mdn validation failed.result" + validationResult);
			result.setCode(validationResult.toString());
			transactionChargingService.failTheTransaction(sctl, "validateSubscriberAsDestination failed. Error code " + validationResult);
			return result;
		}

		//this.destMDN = destMdnValidator.getSubscriberMDN();
		cashinDataConatiner.setDestinationMDN(destinationMDN);

		log.info("getting the emoney pocket of destination mdn");
		Pocket subPocket = pocketService.getDefaultPocket(destinationMDN, "1");
		validationResult = transactionApiValidationService.validateSourcePocket(subPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(subPocket!=null? subPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			transactionChargingService.failTheTransaction(sctl, "validateSourcePocket failed. Error code " + validationResult);
			return result;
		}

		log.info("default emoney pocket for destmdn=" + destinationMDN + " is " + subPocket.getId());

		

		log.info("hierarchy service -->");
		validationResult = hierarchyService.validate(sourceMDN.getSubscriber(), destinationMDN.getSubscriber(), sc.getServiceName(),
		        sc.getTransactionTypeName());
		if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
			log.info("hierarchy services validation failed.result=" + validationResult);
			result.setNotificationCode(validationResult);
			transactionChargingService.failTheTransaction(sctl, "Hierarchy services validation failed. Error code " + validationResult);
			return result;
		}
		
		log.info("getting the partner's agent service pocket");
		Pocket partnerPocket;
		try {
			/*PartnerServices partnerService = transactionChargingService.getPartnerService(sc);
			if (partnerService == null) {
				log.info("agentservice is not available for the partner.");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner);
				return result;
			}
			partnerPocket = partnerService.getPocketBySourcePocket();*/
			String ppid = systemParameterService.getString(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY);
			partnerPocket = pocketService.getById(Long.parseLong(ppid));
			validationResult = transactionApiValidationService.validateSourcePocket(partnerPocket);
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				log.error("Source pocket with id "+(partnerPocket!=null? partnerPocket.getId():null)+" has failed validations");
				result.setNotificationCode(validationResult);
				transactionChargingService.failTheTransaction(sctl, "validateSourcePocket for Partner failed. Error code " + validationResult);
				return result;
			}	

		}
		catch (Exception e) {
			log.error("Exception occured in getting Source Pocket", e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
			return result;
		}
		
		
		cashinDataConatiner.setTransDetails(transDetails);

		cashinDataConatiner.setSourcePocketID(partnerPocket.getId().longValue());
		cashinDataConatiner.setDestPocketID(subPocket.getId().longValue());
		return result;		
	}
	
	/**
	 * To be called after preprocess only
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public CFIXMsg communicate(TransactionDataContainerImpl cashinDataConatiner,ChannelCode channel) {
		if(cashinDataConatiner==null || channel==null){
			log.error("Input data is null.cashinDataConatiner:"+cashinDataConatiner+"channel:"+channel);
			throw new IllegalArgumentException();
		}
		Transaction transDetails = cashinDataConatiner.getTransDetails();
		ServiceChargeTxnLog sctl = transDetails.getServiceChargeTransactionLog();
		log.info("building CMCashinInquiry object for processing -->");
		CMCashInInquiry cashIn = new CMCashInInquiry();
		CMInterswitchCashin cashinDetails = (CMInterswitchCashin) cashinDataConatiner.getMsg();
		
		//Saving integration details 
		saveIntegrationSummary(sctl, cashinDetails);
		
		cashIn.setSourceMDN(cashinDataConatiner.getPartnerMDN().getMdn());
		cashIn.setDestMDN(cashinDataConatiner.getDestinationMDN().getMdn());
		cashIn.setAmount(cashinDetails.getAmount());
		cashIn.setCharges(transDetails.getAmountTowardsCharges());
		cashIn.setTransactionID(cashinDetails.getTransactionID());
		cashIn.setChannelCode(channel.getChannelcode());
		cashIn.setPin("a");
		cashIn.setSourcePocketID(cashinDataConatiner.getSourcePocketID());
		cashIn.setDestPocketID(cashinDataConatiner.getDestPocketID());
		cashIn.setSourceApplication(cashinDetails.getSourceApplication());
		cashIn.setSourceMessage("from BSM");
		cashIn.setServletPath(cashinDetails.getServletPath());
		cashIn.setServiceChargeTransactionLogID(sctl.getId().longValue());
		cashIn.setIsSystemIntiatedTransaction(true);
		cashIn.setTransactionIdentifier(cashinDetails.getTransactionIdentifier());
		log.info("sending cashin inquiry to backend -->");
		CFIXMsg response = super.process(cashIn);
		return response;		
	}
	
	/**
	 * To be called after communicate only
	 * @param response
	 * @param result
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public TransferInquiryXMLResult postprocess(TransactionDataContainerImpl cashinDataConatiner,ChannelCode channel, CFIXMsg response, TransferInquiryXMLResult result) {
		if(cashinDataConatiner==null || channel==null || response==null || result==null){
			log.error("Input data is null.cashinDataConatiner:"+cashinDataConatiner+"channel:"+channel+"response:"+response+"result:"+result);
			throw new IllegalArgumentException();
		}
		// Saves the Transaction Id returned from Back End
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		CMInterswitchCashin cashinDetails = (CMInterswitchCashin) cashinDataConatiner.getMsg();
		Transaction transDetails = cashinDataConatiner.getTransDetails();
		log.info("received backend response");
		ServiceChargeTransactionLog sctl = transDetails.getServiceChargeTransactionLog();

		if (transactionResponse.getTransactionId() != null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			sctl.setInfo1(cashinDetails.getCustReference());
			cashinDetails.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}

		log.info("the notificationcode returned by backend-->" + transactionResponse.getCode());
		
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setMultixResponse(response);
		result.setDebitAmount(transDetails.getAmountToDebit());
		result.setCreditAmount(transDetails.getAmountToCredit());
		result.setServiceCharge(transDetails.getAmountTowardsCharges());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		
		return result;
	}
	
	/** Saving integration details separately. Can be used in report requirements etc. */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	private void saveIntegrationSummary(ServiceChargeTxnLog sctl, CMInterswitchCashin cashinDetails) {
		IntegrationSummaryDao isdao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummary isummary = new IntegrationSummary();
		isummary.setSctlid(sctl.getId());
		isummary.setReconcilationid1(sctl.getDestmdn());
		isummary.setReconcilationid2(cashinDetails.getReceiptNo());
		isummary.setReconcilationid3(cashinDetails.getPaymentLogID());
		isummary.setInstitutionid(cashinDetails.getInstitutionID());
		isdao.save(isummary);
    }

}
