/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.IntegrationPartnerMapping;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMThirdPartyCashOut;
import com.mfino.fix.CmFinoFIX.CMWithdrawFromATM;
import com.mfino.fix.CmFinoFIX.CMWithdrawFromATMInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.IntegrationPartnerMappingService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionIdentifierService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.transactionapi.handlers.wallet.WithdrawFromATMHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.util.MfinoUtil;
import com.mfino.validators.PartnerValidator;

/**
 * @author Bala Sunku
 *
 */
@Service("WithdrawFromATMHandlerImpl")
public class WithdrawFromATMHandlerImpl extends FIXMessageHandler implements WithdrawFromATMHandler{
	
	private Logger log = LoggerFactory.getLogger(WithdrawFromATMHandlerImpl.class);	
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
 	
 	@Autowired
 	@Qualifier("UnRegisteredTxnInfoServiceImpl")
 	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;

	@Autowired
	@Qualifier("IntegrationPartnerMappingServiceImpl")
	private IntegrationPartnerMappingService integrationPartnerMappingService;
		
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	

	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
		
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
		
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

 	@Autowired
 	@Qualifier("TransactionIdentifierServiceImpl")
 	private TransactionIdentifierService transactionIdentifierService;
 	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	public Result handle(CMThirdPartyCashOut thirdPartyCashOut) {
		String mobileNum = thirdPartyCashOut.getSourceMDN();
		String mPin = thirdPartyCashOut.getPin();
		String fac = thirdPartyCashOut.getOneTimePassCode();
		BigDecimal amount = thirdPartyCashOut.getAmount();
		
		log.info("Handling Withdraw from ATM request for mdn: " + mobileNum + " with FAC: " + fac + " and Amout: " + amount);
		

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ThirdPartyCashOut, thirdPartyCashOut.DumpFields());
		XMLResult result = new TransferInquiryXMLResult();
		result.setTransactionTime(new Timestamp());
		result.setTransactionID(transactionsLog.getID());
		thirdPartyCashOut.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(thirdPartyCashOut);
		
		String sourceMDN = subscriberService.normalizeMDN(mobileNum);

		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(sourceMDN);
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if (! (validationResult.equals(CmFinoFIX.ResponseCode_Success) || validationResult.equals(CmFinoFIX.NotificationCode_SubscriberNotRegistered)) )  {
			result.setNotificationCode(validationResult);
			log.error("Source subscriber with mdn : "+sourceMDN+" has failed validations");
			return result;
		}
		boolean useFacAsPin = false;
		boolean isUnRegistered = false;
		if (! CmFinoFIX.SubscriberStatus_NotRegistered.equals(srcSubscriberMDN.getStatus())) {
			isUnRegistered = false;
			String paramValue = systemParametersService.getString(SystemParameterKeys.CASHOUT_AT_ATM_FAC_AS_PIN);
			if(paramValue!=null)
			{
				useFacAsPin = Boolean.parseBoolean(paramValue);
			}
		} 
		else {
			isUnRegistered = true;
			useFacAsPin = true;
		}
		if(!thirdPartyCashOut.getIsSystemIntiatedTransaction()){
			if (useFacAsPin) {
				if (!((fac.substring(fac.length() - 4)).equals(mPin))) {
					result.setNotificationCode(CmFinoFIX.NotificationCode_WrongPINSpecified);
					result.setNumberOfTriesLeft(2);
					log.info("Invalid mPin");
					return result;
				}
			} else {
				validationResult = transactionApiValidationService.validatePin(srcSubscriberMDN, mPin);
				if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
					result.setNotificationCode(validationResult);
					log.error("Pin validation failed for mdn: " + sourceMDN);
					result.setNumberOfTriesLeft((int)(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - srcSubscriberMDN.getWrongpincount()));
					return result;
				}
			}
		}
		
		String digestedFAC = MfinoUtil.calculateDigestPin(sourceMDN, fac);
		Integer[] status = new Integer[2];
		status[0] = CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED;
//		status[1] = CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED; // Removed as per tkt #2306
		
		UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
		urtiQuery.setSubscriberMDNID(srcSubscriberMDN.getId().longValue());
		urtiQuery.setFundAccessCode(digestedFAC);
		urtiQuery.setAmount(amount);
		urtiQuery.setMultiStatus(status);
		

		List<UnRegisteredTxnInfo> lstUnRegisteredTxnInfos = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(urtiQuery);
		
		UnRegisteredTxnInfo unRegisteredTxnInfo = null;
		if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
			unRegisteredTxnInfo = lstUnRegisteredTxnInfos.get(0);
		}

		if (unRegisteredTxnInfo != null) {
			Timestamp currentDate = new Timestamp();
			Timestamp transactionDate = unRegisteredTxnInfo.getCreatetime();
			long cashOutExpiryTime = systemParametersService.getLong(SystemParameterKeys.CASHOUT_AT_ATM_EXPIRY_TIME);
			boolean isTxnExpired = false;
			
			result.setSctlID(unRegisteredTxnInfo.getTransferSCTLId());
			result.setAmount(amount);
			
			//save the transactionidentifier along with sctl to db

			transactionIdentifierService.createTrxnIdentifierDbEntry(thirdPartyCashOut.getTransactionIdentifier(), unRegisteredTxnInfo.getTransferSCTLId());
			
			//Check whether the Cash out transaction is expired or not.
			if (cashOutExpiryTime == -1) {
				if (! DateUtils.isSameDay(currentDate, transactionDate)) {
					isTxnExpired = true;
				}
			} 
			else {
				long diffTime = currentDate.getTime() - transactionDate.getTime();
				if (diffTime > (cashOutExpiryTime * 60*60*1000)) {
					isTxnExpired = true;
				}
			}
			
			if (isTxnExpired) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_CashOutAtATMExpired);
				log.info("Cash out request is expired");
				return result;
			}
			else {
				unRegisteredTxnInfo.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_REQUESTED);
				unRegisteredTxnInfoService.save(unRegisteredTxnInfo);
				log.info("Processing the Withdraw request ...");
				result = (XMLResult)processCashOut(unRegisteredTxnInfo, result, isUnRegistered,thirdPartyCashOut);
			}
		}
		else {
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			log.info("Cash Out At ATM Transfer record not found");
			return result;
		}
		
		return result;
	}
	
	private Result processCashOut(UnRegisteredTxnInfo unRegisteredTxnInfo, XMLResult result, boolean isUnRegistered,CMThirdPartyCashOut thirdPartyCashOut) {
		SubscriberMdn srcPartnerMDN = null;
		Pocket srcPocket = null;

		ServiceChargeTransactionLog sctl = sctlService.getBySCTLID(unRegisteredTxnInfo.getTransferSCTLId());


		if (isUnRegistered) {
			// Get the UnRegistered SubscriberMDN and  Emoney pocket
			srcPartnerMDN = unRegisteredTxnInfo.getSubscriberMdn();

			srcPocket = pocketService.getDefaultPocket(srcPartnerMDN, null);
		}
		else {
			// Getting the ATM Partner(Third Party Partner) Details and the Suspense pocket Details 
			String ATMPartnerMDN = systemParametersService.getString(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
			if (StringUtils.isBlank(ATMPartnerMDN)) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
				log.info("Third Party Partner MDN Value in System Parameters is Null");
				failCashOutRequest(MessageText._("Third Party Partner MDN Value in System Parameters is Null"),unRegisteredTxnInfo,sctl);
				return result;
			}

			srcPartnerMDN = subscriberMdnService.getByMDN(ATMPartnerMDN);
			Integer validationResult= transactionApiValidationService.validatePartnerMDN(srcPartnerMDN);
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				log.info("Third Party Partner has failed validations");
				result.setNotificationCode(validationResult);
				failCashOutRequest(MessageText._("Third Party Partner is not Active"),unRegisteredTxnInfo,sctl);
				return result;
			}
			
			srcPocket = pocketService.getSuspencePocket(partnerService.getPartner(srcPartnerMDN));
		}
		
		Integer validationResult = transactionApiValidationService.validateSourcePocketForATMWithdrawal(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			failCashOutRequest(MessageText._("Source Money Pocket has failed validations"),unRegisteredTxnInfo,sctl);
			return result;
		}
		
		//Getting the QuickTeller Partner details and Bank Pocket Details
		String institutionId = thirdPartyCashOut.getInstitutionID();
		
		String terminalPrefix = systemParametersService.getString(SystemParameterKeys.ATM_TERMINAL_PREFIX_CODE);
		String terminalId = thirdPartyCashOut.getCATerminalId();
		if ( StringUtils.isNotBlank(terminalId) && (StringUtils.isNotBlank(terminalPrefix)) && (terminalId.startsWith(terminalPrefix) )) {
			institutionId = terminalPrefix;
		}
		
		IntegrationPartnerMapping ipm = integrationPartnerMappingService.getByInstitutionID(institutionId);
		if (ipm == null) {
			log.info("Integration Partner Mapping is missing for InstitutionId : " + institutionId);
			result.setNotificationCode(CmFinoFIX.NotificationCode_Integration_InvalidInstituionID);
			result.setInstitutionID(institutionId);
			failCashOutRequest(MessageText._("Integration Partner Mapping is missing for InstitutionId : " + institutionId),unRegisteredTxnInfo,sctl);
			return result;
		}
		
		Partner QTPartner = ipm.getPartner();
		PartnerValidator partnerValidator = new PartnerValidator();
		partnerValidator.setPartner(QTPartner);
		validationResult = transactionApiValidationService.validatePartnerByPartnerType(QTPartner);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.info("Institution Partner is not Active");
			result.setNotificationCode(validationResult);
			failCashOutRequest(MessageText._("Institution Partner is not Active"),unRegisteredTxnInfo,sctl);
			return result;
		}
		SubscriberMdn destSubscriberMDN = QTPartner.getSubscriber().getSubscriberMdns().iterator().next();
		
		Pocket destPocket = pocketService.getDefaultPocket(destSubscriberMDN, ServiceAndTransactionConstants.BANK_POCKET_CODE);
	//	PartnerServices partnerService = transactionChargingService.getPartnerService(QTPartner.getID(), sctl.getmFinoServiceProviderByMSPID().getID(), sctl.getServiceID());
		PartnerServices partnerService = transactionChargingService.getPartnerService(QTPartner.getId(), sctl.getServiceProviderID(), sctl.getServiceID());
		if (partnerService == null ){
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner);
			log.info("Institution Partner is not opt for the required service");
			failCashOutRequest(MessageText._("Institution Partner is not opt for the required service"),unRegisteredTxnInfo,sctl);
			return result;
		}
		destPocket = partnerService.getPocketByDestpocketid();
		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Institution Partner Destination Money Pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			failCashOutRequest(MessageText._("Institution Partner Destination Money Pocket with id has failed validations"),unRegisteredTxnInfo,sctl);
			return result;
		}
		


		// Generating the Withdraw Inquiry 
		CMWithdrawFromATMInquiry withdrawInquiry = new CMWithdrawFromATMInquiry();
		withdrawInquiry.setSourceMDN(srcPartnerMDN.getMdn());
		withdrawInquiry.setDestMDN(destSubscriberMDN.getMdn());
		withdrawInquiry.setAmount(thirdPartyCashOut.getAmount());
		withdrawInquiry.setPin("dummy");
		withdrawInquiry.setIsSystemIntiatedTransaction(true);
		withdrawInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		withdrawInquiry.setSourceMessage(thirdPartyCashOut.getSourceMessage());
		withdrawInquiry.setSourceApplication(thirdPartyCashOut.getSourceApplication());
		withdrawInquiry.setSourcePocketID(srcPocket.getId().longValue());
		withdrawInquiry.setDestPocketID(destPocket.getId().longValue());
		withdrawInquiry.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		withdrawInquiry.setServiceChargeTransactionLogID(unRegisteredTxnInfo.getTransferSCTLId());
		withdrawInquiry.setTerminalID(thirdPartyCashOut.getCATerminalId());
		withdrawInquiry.setDestinationBankAccountNo(thirdPartyCashOut.getAccountNumber());
		withdrawInquiry.setTransactionIdentifier(thirdPartyCashOut.getTransactionIdentifier());
		
		log.info("Sending the Withdraw Inquiry Object to Backend for Processing...");
		result.setSourceMessage(withdrawInquiry);
		CFIXMsg response = super.process(withdrawInquiry);
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the inquiry response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (transactionResponse != null && CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString()
				.equals(transactionResponse.getCode())) {
			CMWithdrawFromATM withdraw = new CMWithdrawFromATM();
			withdraw.setSourceMDN(srcPartnerMDN.getMdn());
			withdraw.setDestMDN(destSubscriberMDN.getMdn());
			withdraw.setIsSystemIntiatedTransaction(true);
			withdraw.setSourcePocketID(srcPocket.getId().longValue());
			withdraw.setDestPocketID(destPocket.getId().longValue());
			withdraw.setServletPath(CmFinoFIX.ServletPath_Subscribers);
			withdraw.setSourceApplication(thirdPartyCashOut.getSourceApplication());
			withdraw.setParentTransactionID(transactionResponse.getTransactionId());
			withdraw.setTransferID(transactionResponse.getTransferId());
			withdraw.setConfirmed(CmFinoFIX.Boolean_True);
			withdraw.setServiceChargeTransactionLogID(unRegisteredTxnInfo.getTransferSCTLId());
			withdraw.setTerminalID(thirdPartyCashOut.getCATerminalId());
			withdraw.setDestinationBankAccountNo(thirdPartyCashOut.getAccountNumber());
			withdraw.setTransactionIdentifier(thirdPartyCashOut.getTransactionIdentifier());

			
			log.info("Sending the Withdraw Confirmation Object to Backend for Processing...");
			
			result.setSourceMessage(withdraw);
			response = super.process(withdraw);
			transactionResponse = checkBackEndResponse(response);
			log.info("Got the confirmation response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

			sctl.setOnBeHalfOfMDN(thirdPartyCashOut.getAccountNumber());
			int atmWithdrawStatus = 1;
			if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
				if (transactionResponse.isResult()) {
					transactionChargingService.confirmTheTransaction(sctl);
					commodityTransferService.addCommodityTransferToResult(result, transactionResponse.getTransferId());
					// Setting the status as Success
					atmWithdrawStatus = 0;
					log.info("ATM withdraw has been Successfully Completed for " + thirdPartyCashOut.getSourceMDN() + " with amount " + thirdPartyCashOut.getAmount());
					result.setNotificationCode(CmFinoFIX.NotificationCode_SuccessfulCashOutFromATM);
					unRegisteredTxnInfo.setUnregisteredtxnstatus((long)CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_COMPLETED);
					unRegisteredTxnInfo.setCashoutctid(new BigDecimal(transactionResponse.getTransferId()));
					unRegisteredTxnInfo.setCashoutsctlid(new BigDecimal(sctl.getID()));
					unRegisteredTxnInfoService.save(unRegisteredTxnInfo);
				}
				else {
					// Setting the status as Failure
					atmWithdrawStatus = 1;
					log.info("ATM withdraw has failed for " + thirdPartyCashOut.getSourceMDN() + " with amount " + thirdPartyCashOut.getAmount());
					result.setNotificationCode(CmFinoFIX.NotificationCode_FailedCashOutFromATM);
					failCashOutRequest(transactionResponse.getMessage(),unRegisteredTxnInfo,sctl);
				}
			} 
			else {
				// Setting the status as Pending
				atmWithdrawStatus = 2;
			}
		}
		else { // Inquiry fails
			log.info("ATM withdraw inquiry has failed for " + thirdPartyCashOut.getSourceMDN() + " with amount " + thirdPartyCashOut.getAmount());
			result.setNotificationCode(CmFinoFIX.NotificationCode_FailedCashOutFromATM);
			failCashOutRequest(transactionResponse.getMessage(),unRegisteredTxnInfo,sctl);
		}
		return result;
	}
	
	private void failCashOutRequest(String failureReason,UnRegisteredTxnInfo unRegisteredTxnInfo,ServiceChargeTransactionLog sctl) {
		failureReason = (failureReason.length() > 255) ? failureReason.substring(0,255) : failureReason;
		unRegisteredTxnInfo.setUnregisteredtxnstatus((long)CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED);
		unRegisteredTxnInfo.setFailurereason(failureReason);

		unRegisteredTxnInfoService.save(unRegisteredTxnInfo);
		
		sctl.setFailureReason(failureReason);

		transactionChargingService.saveServiceTransactionLog(sctl);
	}
}
