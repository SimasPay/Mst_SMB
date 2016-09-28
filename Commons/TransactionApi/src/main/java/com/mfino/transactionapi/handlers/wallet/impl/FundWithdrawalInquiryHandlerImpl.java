package com.mfino.transactionapi.handlers.wallet.impl;

import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.FundDefinition;
import com.mfino.domain.FundDistributionInfo;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.FundStorageService;
import com.mfino.service.FundValidationService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.FundWithdrawalInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
@Service("FundWithdrawalInquiryHandlerImpl")
public class FundWithdrawalInquiryHandlerImpl extends FIXMessageHandler implements FundWithdrawalInquiryHandler{
	private static Logger log = LoggerFactory.getLogger(FundWithdrawalInquiryHandlerImpl.class);
	
	@Autowired
	@Qualifier("FundValidationServiceImpl")
	private FundValidationService fundValidationService;	
	@Autowired
	@Qualifier("FundStorageServiceImpl")
	private FundStorageService fundStorageService;
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;
	
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
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	public Result handle(TransactionDetails transactionDetails)
	{
		log.info("Extracting data from transactionDetails in FundWithdrawalInquiryHandlerImpl" +
				"for sourceMDN: "+transactionDetails.getSourceMDN());
		CMFundWithdrawalInquiry fundWithdrawalInquiry = new CMFundWithdrawalInquiry();
		ChannelCode cc = transactionDetails.getCc();
		fundWithdrawalInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		fundWithdrawalInquiry.setPin(transactionDetails.getSourcePIN());
		fundWithdrawalInquiry.setAmount(transactionDetails.getAmount());
		fundWithdrawalInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		fundWithdrawalInquiry.setChannelCode(cc.getChannelcode());
		fundWithdrawalInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		fundWithdrawalInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		fundWithdrawalInquiry.setOneTimePassCode(transactionDetails.getActivationOTP());
		//withdrawal mdn to be used in backend if quering the unreg table is needed
		fundWithdrawalInquiry.setWithdrawalMDN(transactionDetails.getOnBehalfOfMDN());
		fundWithdrawalInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		log.info("Handling Fund Withdrawal Inquiry webapi request::From " + fundWithdrawalInquiry.getSourceMDN() + 
				 " For Amount = " + fundWithdrawalInquiry.getAmount());
		XMLResult result = new TransferInquiryXMLResult();
		XMLResult onBehalfOfMDNResult = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_FundWithdrawalInquiry,fundWithdrawalInquiry.DumpFields());
		fundWithdrawalInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(fundWithdrawalInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		
		onBehalfOfMDNResult.setTransactionID(transactionsLog.getID());
		onBehalfOfMDNResult.setSourceMessage(fundWithdrawalInquiry);
		onBehalfOfMDNResult.setTransactionTime(transactionsLog.getTransactionTime());
			
		log.info("validating merchant details");
		SubscriberMdn destPartnerMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validatePartnerMDN(destPartnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination partner with mdn : "+transactionDetails.getSourceMDN()+" has failed validations");
			validationResult = processValidationResultForPartner(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		validationResult = transactionApiValidationService.validatePin(destPartnerMDN, transactionDetails.getSourcePIN());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: "+destPartnerMDN.getMDN());
			result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - destPartnerMDN.getWrongPINCount());
			result.setNotificationCode(validationResult);
			return result;
		}
		Set<Partner> partnerSet = destPartnerMDN.getSubscriber().getPartnerFromSubscriberID();
		Partner destPartner = partnerSet.iterator().next();
		fundWithdrawalInquiry.setPartnerCode(destPartner.getPartnercode());
		
		UnRegisteredTxnInfo unRegisteredTxnInfo = fundValidationService.queryUnRegisteredTxnInfo(fundWithdrawalInquiry.getWithdrawalMDN(), fundWithdrawalInquiry.getOneTimePassCode(), null,fundWithdrawalInquiry.getPartnerCode());
		if(unRegisteredTxnInfo==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidFundRequest);
			result.setDestinationMDN(fundWithdrawalInquiry.getWithdrawalMDN());
			return result;
		}
		FundDefinition fundDefinition = unRegisteredTxnInfo.getFundDefinition();

		//validate fund withdrawal
		Integer fundValidationResult = fundValidationService.validate(unRegisteredTxnInfo,result,fundWithdrawalInquiry.getAmount(),fundWithdrawalInquiry.getOneTimePassCode(),fundDefinition,fundWithdrawalInquiry.getPartnerCode());
		
		String partnerTradeName = destPartner.getTradename();
		result.setPartnerCode(partnerTradeName);
		onBehalfOfMDNResult.setPartnerCode(partnerTradeName);
		onBehalfOfMDNResult.setOneTimePin(result.getOneTimePin());
		if(!CmFinoFIX.ResponseCode_Success.equals(fundValidationResult)){
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
			log.error("Withdrawal failed validations");
			onBehalfOfMDNResult.setNotificationCode(fundValidationResult);
			onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
			sendSms(fundWithdrawalInquiry.getWithdrawalMDN(), onBehalfOfMDNResult);
			return result;
		}	
		
		String newFac = fundValidationService.regenerateFAC(unRegisteredTxnInfo);
		int notificationCode;

		//Source partner(Third part suspence)--------------------------------------------------------------------------------
		String thirdPartyPartnerMDN = systemParametersService.getString(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
		if (StringUtils.isBlank(thirdPartyPartnerMDN)) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
			log.error("Third Party Partner MDN Value in System Parameters is Null");
			notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
			
			if(fundDefinition.getMaxfailattemptsallowed()!=-1){
				onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
			}else{
				onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
			}
			
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				onBehalfOfMDNResult.setOneTimePin(newFac);
				onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFoundNewFac);
			}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(notificationCode)){
				onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			}else if(CmFinoFIX.NotificationCode_ReverseFundRequestInitaited.equals(notificationCode)){
				onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFoundReversal);
			}
			sendSms(fundWithdrawalInquiry.getWithdrawalMDN(), onBehalfOfMDNResult);
			
			return result;
		}

		SubscriberMdn srcPartnerMDN = subscriberMdnService.getByMDN(thirdPartyPartnerMDN);
		validationResult= transactionApiValidationService.validatePartnerMDN(srcPartnerMDN);		
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
			log.error("Third Party Partner is not Active");
			notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
			validationResult = processValidationResultForDestinationAgent(validationResult,notificationCode);

			if(fundDefinition.getMaxfailattemptsallowed()!=-1){
				onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
			}else{
				onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
			}
			
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				onBehalfOfMDNResult.setOneTimePin(newFac);
			}
			sendSms(fundWithdrawalInquiry.getWithdrawalMDN(),onBehalfOfMDNResult);
			onBehalfOfMDNResult.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket srcPocket= pocketService.getSuspencePocket(partnerService.getPartner(srcPartnerMDN));
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
			
			if(fundDefinition.getMaxfailattemptsallowed()!=-1){
				onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
			}else{
				onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
			}
			notificationCode = processValidationResultForPocket(notificationCode,validationResult);
			onBehalfOfMDNResult.setNotificationCode(notificationCode);
			if( CmFinoFIX.NotificationCode_SourceMoneyPocketNotFoundNewFac.equals(notificationCode)||
					CmFinoFIX.NotificationCode_MoneyPocketNotActiveNewFac.equals(notificationCode) ){
				onBehalfOfMDNResult.setOneTimePin(newFac);
			}
			sendSms(fundWithdrawalInquiry.getWithdrawalMDN(),onBehalfOfMDNResult);
			
			return result;
		}
		
//		//Destination MDN(Merchant)------------------------------------------------------------------------------------------------------
//		PartnerValidator destMDNValidator = new PartnerValidator();
//		destMDNValidator.setCode(fundWithdrawalInquiry.getPartnerCode());
//
//		validationResult= destMDNValidator.validate();
//		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
//			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
//			onBehalfOfMDNResult.setPartnerCode(fundWithdrawalInquiry.getPartnerCode());
//			notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
//			validationResult = processValidationResultForDestinationAgent(validationResult,notificationCode);
//
//			if(fundDefinition.getMaxFailAttemptsAllowed()!=-1){
//				onBehalfOfMDNResult.setNumberOfTriesLeft(fundDefinition.getMaxFailAttemptsAllowed()-unRegisteredTxnInfo.getWithdrawalFailureAttempt());
//			}else{
//				onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
//			}
//			
//			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
//				onBehalfOfMDNResult.setOneTimePin(newFac);
//			}
//			onBehalfOfMDNResult.setNotificationCode(validationResult);
//			sendSms(fundWithdrawalInquiry.getWithdrawalMDN(),onBehalfOfMDNResult);
//			
//			return result;
//		}
//		Partner destAgent = destMDNValidator.getPartner();
//		Subscriber destSub = destAgent.getSubscriber();
//		SubscriberMDN destMDN = destSub.getSubscriberMDNFromSubscriberID().iterator().next();
		
		fundWithdrawalInquiry.setSourceMDN(srcPartnerMDN.getMdn());
		// add service charge to amount

		log.info("creating the serviceCharge object....");
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setDestMDN(destPartnerMDN.getMDN());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_FUND_WITHDRAWAL);
		sc.setSourceMDN(srcPartnerMDN.getMdn());
		sc.setTransactionAmount(fundWithdrawalInquiry.getAmount());
		sc.setTransactionLogId(fundWithdrawalInquiry.getTransactionID());
		sc.setOnBeHalfOfMDN(fundWithdrawalInquiry.getWithdrawalMDN());
		sc.setTransactionIdentifier(fundWithdrawalInquiry.getTransactionIdentifier());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
//
//		
//		if(destAgent.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_BranchOffice)){
//			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
//		}else{
//		}
		
		Pocket destPocket;
		try {
			long servicePartnerId = transactionChargingService.getServiceProviderId(null);
			long serviceId = transactionChargingService.getServiceId(sc.getServiceName());
			PartnerServices partnerService = transactionChargingService.getPartnerService(destPartner.getId(), servicePartnerId, serviceId);
			if (partnerService == null) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
				log.error("Partner service NULL");
				notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
				
				if(fundDefinition.getMaxfailattemptsallowed()!=-1){
					onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
				}else{
					onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
				}
				
				if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
					onBehalfOfMDNResult.setOneTimePin(newFac);
					onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgentNewFac);
				}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(notificationCode)){
					onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
				}else if(CmFinoFIX.NotificationCode_ReverseFundRequestInitaited.equals(notificationCode)){
					onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgentReversal);
				}
				sendSms(fundWithdrawalInquiry.getWithdrawalMDN(),onBehalfOfMDNResult);
				
				return result;
			}
			destPocket = partnerService.getPocketByDestpocketid();
			validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
			if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
				log.error("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
				result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
				notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
				
				if(fundDefinition.getMaxfailattemptsallowed()!=-1){
					onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
				}else{
					onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
				}
				notificationCode = processValidationResultForPocket(notificationCode,validationResult);
				onBehalfOfMDNResult.setNotificationCode(notificationCode);
				if( CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFoundNewFac.equals(notificationCode)||
						CmFinoFIX.NotificationCode_MoneyPocketNotActiveNewFac.equals(notificationCode) ){
					onBehalfOfMDNResult.setOneTimePin(newFac);
				}
				sendSms(fundWithdrawalInquiry.getWithdrawalMDN(),onBehalfOfMDNResult);
				return result;
			}
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting Source Pocket",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}
		if(srcPocket.getPocketTemplate().getType()==(CmFinoFIX.PocketType_BankAccount)||destPocket.getPocketTemplate().getType()==(CmFinoFIX.PocketType_BankAccount))
		{
			if(!systemParametersService.getBankServiceStatus())
			{
				log.info("The bank service is down as set in the system parameter");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
				return result;
			}
		}
		fundWithdrawalInquiry.setDestMDN(destPartnerMDN.getMDN());
		fundWithdrawalInquiry.setSourcePocketID(srcPocket.getId().longValue());
		fundWithdrawalInquiry.setDestPocketID(destPocket.getId().longValue());
		fundWithdrawalInquiry.setIsSystemIntiatedTransaction(true);
		
		
		//For Hierarchy:
		validationResult = hierarchyService.validate(srcPartnerMDN.getSubscriber(), destPartnerMDN.getSubscriber(), 
				sc.getServiceName(), sc.getTransactionTypeName());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			log.error("Due to DCT Restrictions the Transaction " + ServiceAndTransactionConstants.TRANSACTION_FUND_WITHDRAWAL + " Is Failed");
			return result;
		}
		
		Transaction transaction=null;
		try{
			transaction =transactionChargingService.getCharge(sc);
			fundWithdrawalInquiry.setAmount(transaction.getAmountToCredit());
			fundWithdrawalInquiry.setCharges(transaction.getAmountTowardsCharges());

		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			
			return result;
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		
		fundWithdrawalInquiry.setDistributionType(CmFinoFIX.DistributionType_Withdrawal);
		fundWithdrawalInquiry.setServiceChargeTransactionLogID(sctl.getID());
		fundValidationService.updateAvailableAmount(unRegisteredTxnInfo, fundWithdrawalInquiry, true,fundWithdrawalInquiry.getAmount());
		onBehalfOfMDNResult.setSctlID(sctl.getID());
		
		log.info("Sending the fundWithdrawalInquiry request to backend for processing");
		CFIXMsg response = super.process(fundWithdrawalInquiry);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if(!transactionResponse.isResult()){
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
			notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
			if(fundDefinition.getMaxfailattemptsallowed()!=-1){
				onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
			}else{
				onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
			}
			
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				onBehalfOfMDNResult.setOneTimePin(newFac);
			}
			onBehalfOfMDNResult.setNotificationCode(notificationCode);
			fundValidationService.updateAvailableAmount(unRegisteredTxnInfo, fundWithdrawalInquiry,false,fundWithdrawalInquiry.getAmount());
			failTheTransaction(transactionResponse.getMessage(),sctl.getID());
			sendSms(fundWithdrawalInquiry.getWithdrawalMDN(), onBehalfOfMDNResult);
			return result;
		}
		
		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}

		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		addCompanyANDLanguageToResult(destPartnerMDN,result);
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		return result;
	
	}
	
	private Integer processValidationResultForPocket(int notificationCode, int validationResult) {
		if(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound.equals(validationResult)){
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				return CmFinoFIX.NotificationCode_SourceMoneyPocketNotFoundNewFac;
			}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(notificationCode)){
				return CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound;
			}else if(CmFinoFIX.NotificationCode_ReverseFundRequestInitaited.equals(notificationCode)){
				return CmFinoFIX.NotificationCode_SourceMoneyPocketNotFoundReversal;
			}
		}
		else if(CmFinoFIX.NotificationCode_MoneyPocketNotActive.equals(validationResult)){
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				return CmFinoFIX.NotificationCode_MoneyPocketNotActiveNewFac;
			}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(notificationCode)){
				return CmFinoFIX.NotificationCode_MoneyPocketNotActive;
			}else if(CmFinoFIX.NotificationCode_ReverseFundRequestInitaited.equals(notificationCode)){
				return CmFinoFIX.NotificationCode_MoneyPocketNotActiveReversal;
			}
		}
		else if(CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFound.equals(validationResult)){
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				return CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFoundNewFac;
			}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(notificationCode)){
				return CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFound;
			}else if(CmFinoFIX.NotificationCode_ReverseFundRequestInitaited.equals(notificationCode)){
				return CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFoundReversal;
			}
		}
		
		return null;
		
	}

	public Integer processValidationResultForDestinationAgent(Integer validationResult,Integer notificationCode){
		
		if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
			if (CmFinoFIX.NotificationCode_MDNIsNotActive.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsNotActiveNewFac;
			} else if (CmFinoFIX.NotificationCode_MDNNotFound.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentNotFoundNewFac;
			} else if (CmFinoFIX.NotificationCode_MDNIsRestricted.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsRestrictedNewFac;
			} else if (CmFinoFIX.NotificationCode_DestinationMDNIsRestricted.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsRestrictedNewFac;
			} else if (CmFinoFIX.NotificationCode_DestinationMDNIsNotActive.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsNotActiveNewFac;
			} else if (CmFinoFIX.NotificationCode_DestinationMDNNotFound.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentNotFoundNewFac;
			}
		}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(notificationCode)){
			if (CmFinoFIX.NotificationCode_MDNIsNotActive.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsNotActive;
			} else if (CmFinoFIX.NotificationCode_MDNNotFound.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentNotFound;
			} else if (CmFinoFIX.NotificationCode_MDNIsRestricted.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsRestricted;
			} else if (CmFinoFIX.NotificationCode_DestinationMDNIsRestricted.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsRestricted;
			} else if (CmFinoFIX.NotificationCode_DestinationMDNIsNotActive.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsNotActive;
			} else if (CmFinoFIX.NotificationCode_DestinationMDNNotFound.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentNotFound;
			}
		}else if(CmFinoFIX.NotificationCode_ReverseFundRequestInitaited.equals(notificationCode)){
			if (CmFinoFIX.NotificationCode_MDNIsNotActive.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsNotActiveReversal;
			} else if (CmFinoFIX.NotificationCode_MDNNotFound.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentNotFoundReversal;
			} else if (CmFinoFIX.NotificationCode_MDNIsRestricted.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsRestrictedReversal;
			} else if (CmFinoFIX.NotificationCode_DestinationMDNIsRestricted.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsRestrictedReversal;
			} else if (CmFinoFIX.NotificationCode_DestinationMDNIsNotActive.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsNotActiveReversal;
			} else if (CmFinoFIX.NotificationCode_DestinationMDNNotFound.equals(validationResult)) {
				validationResult = CmFinoFIX.NotificationCode_DestinationAgentNotFoundReversal;
			}
		}

		
		return validationResult;
	
	}
	
	
	private void failTheTransaction(String failureReason,Long sctlID){
		FundDistributionInfo fundDistributionInfo = fundValidationService.queryFundDistributionInfo(sctlID);
		fundDistributionInfo.setDistributionstatus((long)CmFinoFIX.DistributionStatus_TRANSFER_FAILED);
		fundDistributionInfo.setFailurereason(failureReason);
		fundStorageService.withdrawFunds(fundDistributionInfo);

	}
	private void sendSms(String destMDN,XMLResult result){
		String message = null;
		try {
			result.buildMessage();
			StringBuilder sb = new StringBuilder("(");
			sb.append(result.getXMlelements().get("code"));
			sb.append(")");
			sb.append(result.getXMlelements().get("message"));
			message = sb.toString();
		} catch (XMLStreamException e) {
			log.error("Error While parsing the Result...", e);
		}

		smsService.setDestinationMDN(destMDN);
		smsService.setMessage(message);
		smsService.setNotificationCode(result.getNotificationCode());
		smsService.setSctlId(result.getSctlID());
		smsService.asyncSendSMS();
	}
	
}
