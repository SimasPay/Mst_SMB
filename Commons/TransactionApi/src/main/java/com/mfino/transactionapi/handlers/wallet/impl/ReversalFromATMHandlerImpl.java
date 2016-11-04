/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.IntegrationPartnerMap;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMThirdPartyCashOut;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
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
import com.mfino.transactionapi.handlers.wallet.ReversalFromATMHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.util.MfinoUtil;

/**
 * @author Bala Sunku
 *
 */
@Service("ReversalFromATMHandlerImpl")
public class ReversalFromATMHandlerImpl extends FIXMessageHandler implements ReversalFromATMHandler{	
	private Logger log = LoggerFactory.getLogger(ReversalFromATMHandlerImpl.class);	
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
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
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

 	@Autowired
 	@Qualifier("TransactionIdentifierServiceImpl")
 	private TransactionIdentifierService transactionIdentifierService;
 	
 	@Autowired
 	@Qualifier("UnRegisteredTxnInfoServiceImpl")
 	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;
 	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
 	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	public Result handle(CMThirdPartyCashOut thirdPartyCashOut) {
		String mobileNum = thirdPartyCashOut.getSourceMDN();
		String fac = thirdPartyCashOut.getOneTimePassCode();
		BigDecimal amount = thirdPartyCashOut.getAmount();
		
		log.info("Handling Reversal from ATM request for mdn: " + mobileNum + " with FAC: " + fac + " and Amout: " + amount);

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_ThirdPartyCashOut, thirdPartyCashOut.DumpFields());
		XMLResult result = new TransferInquiryXMLResult();
		result.setTransactionTime(new Timestamp());
		result.setTransactionID(transactionsLog.getId().longValue());
		thirdPartyCashOut.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(thirdPartyCashOut);
	
		String sourceMDN = subscriberService.normalizeMDN(mobileNum);
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(sourceMDN);
		boolean isUnRegistered = false;
		if (! CmFinoFIX.SubscriberStatus_NotRegistered.equals(srcSubscriberMDN.getStatus())) {
			isUnRegistered = false;
		} 
		else {
			isUnRegistered = true;
		}
		
		String digestedFAC = MfinoUtil.calculateDigestPin(sourceMDN, fac);
		Integer[] status = new Integer[2];
		status[0] = CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_COMPLETED;
		
		UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
		urtiQuery.setSubscriberMDNID(srcSubscriberMDN.getId().longValue());
		urtiQuery.setFundAccessCode(digestedFAC);
		urtiQuery.setAmount(amount);
		urtiQuery.setMultiStatus(status);
		

		List<UnregisteredTxnInfo> lstUnRegisteredTxnInfos = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(urtiQuery);
		UnregisteredTxnInfo unRegisteredTxnInfo = null;
		if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
			unRegisteredTxnInfo = lstUnRegisteredTxnInfos.get(0);
		}

		if (unRegisteredTxnInfo != null) {
			result.setSctlID(unRegisteredTxnInfo.getTransferctid().longValue());
			result.setAmount(amount);
			
			//save the transactionidentifier along with sctl to db

			transactionIdentifierService.createTrxnIdentifierDbEntry(thirdPartyCashOut.getTransactionIdentifier(), unRegisteredTxnInfo.getTransferctid().longValue());
			
			log.info("Processing the Reversal request From ATM ");
			result = (XMLResult)processReversal(unRegisteredTxnInfo, result, isUnRegistered,thirdPartyCashOut);
		}
		else {
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			log.info("Invalid Reversal request From ATM.");
			return result;
		}
		
		return result;
	}
	
	private Result processReversal(UnregisteredTxnInfo unRegisteredTxnInfo, XMLResult result, boolean isUnRegistered, CMThirdPartyCashOut thirdPartyCashOut) {
		SubscriberMdn destPartnerMDN = null;
		Pocket destPocket = null;

		ServiceChargeTxnLog sctl = sctlService.getBySCTLID(unRegisteredTxnInfo.getTransferctid().longValue());
		
		//Getting the QuickTeller Partner details and Bank Pocket Details (Source details)
		String institutionId = thirdPartyCashOut.getInstitutionID();
		
		String terminalPrefix = systemParametersService.getString(SystemParameterKeys.ATM_TERMINAL_PREFIX_CODE);
		String terminalId = thirdPartyCashOut.getCATerminalId();
		if ( StringUtils.isNotBlank(terminalId) && (StringUtils.isNotBlank(terminalPrefix)) && (terminalId.startsWith(terminalPrefix) )) {
			institutionId = terminalPrefix;
		}
		
		IntegrationPartnerMap ipm = integrationPartnerMappingService.getByInstitutionID(institutionId);
		if (ipm == null) {
			log.info("Integration Partner Mapping is missing for InstitutionId : " + institutionId);
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			return result;
		}
		
		Partner QTPartner = ipm.getPartner();

		Integer validationResult = transactionApiValidationService.validatePartnerByPartnerType(QTPartner);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.info("Institution Partner is not Active");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		SubscriberMdn srcSubscriberMDN = QTPartner.getSubscriber().getSubscriberMdns().iterator().next();

		// Modified as the destination pocket in Withdraw from ATM is changed and getting the pocket based on the Partner service incoming pocket.
		Pocket srcPocket = null;
	//	PartnerServices partnerServices = transactionChargingService.getPartnerService(QTPartner.getID(), sctl.getmFinoServiceProviderByMSPID().getID(), sctl.getServiceID());
		PartnerServices partnerServices = transactionChargingService.getPartnerService(QTPartner.getId().longValue(), sctl.getServiceid().longValue(), sctl.getServiceid().longValue());
		if (partnerService == null ){
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner);
			log.info("Institution Partner is not opt for the required service");
			return result;
		}
		srcPocket = partnerServices.getPocketByDestpocketid();
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		if (isUnRegistered) {
			// Get the UnRegistered SubscriberMDN and  Emoney pocket
			destPartnerMDN = unRegisteredTxnInfo.getSubscriberMdn();
			destPocket = pocketService.getDefaultPocket(destPartnerMDN, null);
		}
		else {
			// Getting the ATM Partner(Third Party Partner) Details and the Suspense pocket Details 
			String ATMPartnerMDN = systemParametersService.getString(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
			if (StringUtils.isBlank(ATMPartnerMDN)) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
				log.info("Third Party Partner MDN Value in System Parameters is Null");
				return result;
			}

			destPartnerMDN = subscriberMdnService.getByMDN(ATMPartnerMDN);
			validationResult = transactionApiValidationService.validatePartnerMDN(destPartnerMDN);
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				log.error("Destination Agent has failed validations");
				result.setNotificationCode(validationResult);
				return result;
			}
			destPocket = pocketService.getSuspencePocket(partnerService.getPartner(destPartnerMDN));
		}

		validationResult = transactionApiValidationService.validateDestinationPocketForATMReversal(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}


		// Generating the Reverse From ATM Inquiry Object 
		CMBankAccountToBankAccount inquiry = new CMBankAccountToBankAccount();
		inquiry.setSourceMDN(srcSubscriberMDN.getMdn());
		inquiry.setDestMDN(destPartnerMDN.getMdn());
		inquiry.setAmount(thirdPartyCashOut.getAmount());
		inquiry.setPin("dummy");
		inquiry.setIsSystemIntiatedTransaction(true);
		inquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		inquiry.setSourceMessage(thirdPartyCashOut.getSourceMessage());
		inquiry.setSourceApplication(thirdPartyCashOut.getSourceApplication());
		inquiry.setSourcePocketID(srcPocket.getId().longValue());
		inquiry.setDestPocketID(destPocket.getId().longValue());
		inquiry.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
		inquiry.setServiceChargeTransactionLogID(unRegisteredTxnInfo.getTransferctid().longValue());
		inquiry.setUICategory(CmFinoFIX.TransactionUICategory_Reverse_From_ATM);
		inquiry.setTerminalID(thirdPartyCashOut.getCATerminalId());
		inquiry.setSourceBankAccountNo(thirdPartyCashOut.getAccountNumber());
		inquiry.setTransactionIdentifier(thirdPartyCashOut.getTransactionIdentifier());
		
		log.info("Sending the Reversal from ATM Inquiry Object to Backend for Processing...");
		result.setSourceMessage(inquiry);
		CFIXMsg response = super.process(inquiry);
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the inquiry response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (transactionResponse != null && CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString()
				.equals(transactionResponse.getCode())) {
			CMBankAccountToBankAccountConfirmation confirm = new CMBankAccountToBankAccountConfirmation();
			confirm.setSourceMDN(srcSubscriberMDN.getMdn());
			confirm.setDestMDN(destPartnerMDN.getMdn());
			confirm.setIsSystemIntiatedTransaction(true);
			confirm.setSourcePocketID(srcPocket.getId().longValue());
			confirm.setDestPocketID(destPocket.getId().longValue());
			confirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
			confirm.setSourceApplication(thirdPartyCashOut.getSourceApplication());
			confirm.setParentTransactionID(transactionResponse.getTransactionId());
			confirm.setTransferID(transactionResponse.getTransferId());
			confirm.setConfirmed(CmFinoFIX.Boolean_True);
			confirm.setServiceChargeTransactionLogID(unRegisteredTxnInfo.getTransferctid().longValue());
			confirm.setTerminalID(thirdPartyCashOut.getCATerminalId());
			confirm.setSourceBankAccountNo(thirdPartyCashOut.getAccountNumber());
			confirm.setTransactionIdentifier(thirdPartyCashOut.getTransactionIdentifier());
			
			log.info("Sending the Reversal from ATM Confirmation Object to Backend for Processing...");
			result.setSourceMessage(confirm);
			response = super.process(confirm);
			transactionResponse = checkBackEndResponse(response);
			log.info("Got the confirmation response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());


			sctl = sctlService.getBySCTLID(unRegisteredTxnInfo.getTransferctid().longValue());

			if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
				if (transactionResponse.isResult()) {
					transactionChargingService.chnageStatusToProcessing(sctl);
					commodityTransferService.addCommodityTransferToResult(result, transactionResponse.getTransferId());
					log.info("ATM Reversal has been Successfully Completed for " + thirdPartyCashOut.getSourceMDN() + " with amount " + thirdPartyCashOut.getAmount());
					result.setNotificationCode(CmFinoFIX.NotificationCode_SuccessfulReversalFromATM);
					unRegisteredTxnInfo.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED);
					unRegisteredTxnInfo.setFailurereason("Reversal Request From ATM");
					unRegisteredTxnInfo.setCashoutctid(null);
					unRegisteredTxnInfo.setCashoutsctlid(null);
					unRegisteredTxnInfoService.save(unRegisteredTxnInfo);
				}
				else {
					log.info("ATM Reversal has failed for " + thirdPartyCashOut.getSourceMDN() + " with amount " + thirdPartyCashOut.getAmount());
					result.setNotificationCode(CmFinoFIX.NotificationCode_FailedReversalFromATM);
					unRegisteredTxnInfo.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_COMPLETED);
					unRegisteredTxnInfoService.save(unRegisteredTxnInfo);
				}
			} 
		}
		else { // Inquiry fails
			log.info("ATM Reversal inquiry has failed for " + thirdPartyCashOut.getSourceMDN() + " with amount " + thirdPartyCashOut.getAmount());
			unRegisteredTxnInfo.setUnregisteredtxnstatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_COMPLETED);
			result.setNotificationCode(CmFinoFIX.NotificationCode_FailedReversalFromATM);
			unRegisteredTxnInfoService.save(unRegisteredTxnInfo);
		}
		return result;
	}
}
