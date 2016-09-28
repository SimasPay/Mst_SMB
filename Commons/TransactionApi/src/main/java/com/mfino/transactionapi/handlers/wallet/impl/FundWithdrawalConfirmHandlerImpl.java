package com.mfino.transactionapi.handlers.wallet.impl;

import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.FundDefinition;
import com.mfino.domain.FundDistributionInfo;
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
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalConfirm;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.FundStorageService;
import com.mfino.service.FundValidationService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.FundWithdrawalConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
@Service("FundWithdrawalConfirmHandlerImpl")
public class FundWithdrawalConfirmHandlerImpl extends FIXMessageHandler implements FundWithdrawalConfirmHandler{
	
	private static Logger log	= LoggerFactory.getLogger(FundWithdrawalConfirmHandlerImpl.class);
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("FundStorageServiceImpl")
	private FundStorageService fundStorageService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("FundValidationServiceImpl")
	private FundValidationService fundValidationService;


	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in FundWithdrawalConfirmHandlerImpl" +
				"for sourceMDN: "+transactionDetails.getSourceMDN());
		CMFundWithdrawalConfirm fundWithdrawalConfirm = new CMFundWithdrawalConfirm();
		ChannelCode cc = transactionDetails.getCc();
		boolean confirmed = false;
		confirmed = Boolean.parseBoolean(transactionDetails.getConfirmString());
		fundWithdrawalConfirm.setSourceMDN(transactionDetails.getSourceMDN());
		fundWithdrawalConfirm.setParentTransactionID(transactionDetails.getParentTxnId());
		fundWithdrawalConfirm.setTransferID(transactionDetails.getTransferId());
		fundWithdrawalConfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		fundWithdrawalConfirm.setConfirmed(confirmed);
		fundWithdrawalConfirm.setSourceApplication((int)cc.getChannelsourceapplication());
		fundWithdrawalConfirm.setChannelCode(cc.getChannelcode());
		fundWithdrawalConfirm.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		log.info("Handling Fund Withdrawal confirmation WebAPI request for Parent Txn Id = " + fundWithdrawalConfirm.getParentTransactionID());
		XMLResult result = new MoneyTransferXMLResult();
		XMLResult onBehalfOfMDNResult = new MoneyTransferXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_FundWithdrawalConfirm,fundWithdrawalConfirm.DumpFields(),fundWithdrawalConfirm.getParentTransactionID());
		fundWithdrawalConfirm.setTransactionID(transactionsLog.getID());

		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(fundWithdrawalConfirm);
		result.setTransactionID(fundWithdrawalConfirm.getTransactionID());
		
		onBehalfOfMDNResult.setTransactionTime(transactionsLog.getTransactionTime());
		onBehalfOfMDNResult.setSourceMessage(fundWithdrawalConfirm);
		onBehalfOfMDNResult.setTransactionID(fundWithdrawalConfirm.getTransactionID());
		
		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(fundWithdrawalConfirm.getParentTransactionID());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				log.error("The status of Sctl with id: "+sctl.getID()+"has been changed from Inquiry to: "+sctl.getStatus());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}

		} else {
			log.error("Could not find sctl with parentTransaction ID: "+fundWithdrawalConfirm.getParentTransactionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}
		result.setSctlID(sctl.getID());
		onBehalfOfMDNResult.setSctlID(sctl.getID());
		fundWithdrawalConfirm.setServiceChargeTransactionLogID(sctl.getID());
		
		fundWithdrawalConfirm.setWithdrawalMDN(sctl.getOnBeHalfOfMDN());
		FundDistributionInfo fundDistributionInfo = fundValidationService.queryFundDistributionInfo(sctl.getID());
		UnRegisteredTxnInfo unRegisteredTxnInfo = fundDistributionInfo.getUnregisteredTxnInfo();
		FundDefinition fundDefinition= unRegisteredTxnInfo.getFundDefinition();
		int notificationCode;		
		fundWithdrawalConfirm.setPartnerCode(unRegisteredTxnInfo.getPartnercode());
		fundWithdrawalConfirm.setAmount(fundDistributionInfo.getDistributedamount());
		fundWithdrawalConfirm.setDistributionType(CmFinoFIX.DistributionType_Withdrawal);

		//Source partner(Third party suspence)--------------------------------------------------------------------------------
		String thirdPartyPartnerMDN = systemParametersService.getString(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
		if (StringUtils.isBlank(thirdPartyPartnerMDN)) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
			log.error("Third Party Partner MDN Value in System Parameters is Null");
			failTheTransaction("Third Party Partner MDN Value in System Parameters is Null",sctl.getID());
			notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
			
			if(fundDefinition.getMaxfailattemptsallowed()!=-1){
				onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
			}else{
				onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
			}
			
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				onBehalfOfMDNResult.setOneTimePin(fundValidationService.regenerateFAC(unRegisteredTxnInfo));
				onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFoundNewFac);
			}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(notificationCode)){
				onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			}else if(CmFinoFIX.NotificationCode_ReverseFundRequestInitaited.equals(notificationCode)){
				onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFoundReversal);
			}
			fundValidationService.updateAvailableAmount(unRegisteredTxnInfo, null,false,fundWithdrawalConfirm.getAmount());
			sendSms(fundWithdrawalConfirm.getWithdrawalMDN(),onBehalfOfMDNResult);
			
			return result;
		}

		SubscriberMdn srcPartnerMDN = subscriberMdnService.getByMDN(thirdPartyPartnerMDN);

		Integer validationResult= transactionApiValidationService.validatePartnerMDN(srcPartnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
			log.error("Third Party Partner is not Active");
			failTheTransaction("Third Party Partner is not Active",sctl.getID());
			notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
			validationResult = processValidationResultForDestinationAgent(validationResult,notificationCode);

			if(fundDefinition.getMaxfailattemptsallowed()!=-1){
				onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
			}else{
				onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
			}
			
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				onBehalfOfMDNResult.setOneTimePin(fundValidationService.regenerateFAC(unRegisteredTxnInfo));
			}
			fundValidationService.updateAvailableAmount(unRegisteredTxnInfo, null,false,fundWithdrawalConfirm.getAmount());
			sendSms(fundWithdrawalConfirm.getWithdrawalMDN(),onBehalfOfMDNResult);
			result.setNotificationCode(validationResult);
			
			return result;
		}
		
		Pocket srcPocket= pocketService.getSuspencePocket(partnerService.getPartner(srcPartnerMDN));
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			failTheTransaction("Source Pocket failed validations",sctl.getID());

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
				onBehalfOfMDNResult.setOneTimePin(fundValidationService.regenerateFAC(unRegisteredTxnInfo));
			}
			fundValidationService.updateAvailableAmount(unRegisteredTxnInfo, null,false,fundWithdrawalConfirm.getAmount());
			sendSms(fundWithdrawalConfirm.getWithdrawalMDN(),onBehalfOfMDNResult);
			
			return result;
		}
		
		//Destination MDN(Merchant)------------------------------------------------------------------------------------------------------
		log.info("validating merchant details");
		SubscriberMdn destPartnerMDN = subscriberMdnService.getByMDN(fundWithdrawalConfirm.getSourceMDN());
		validationResult = transactionApiValidationService.validatePartnerMDN(destPartnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination partner with mdn : "+transactionDetails.getSourceMDN()+" has failed validations");
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
			String enteredPartnerCode = (DAOFactory.getInstance().getPartnerDAO().getById(sctl.getDestPartnerID())).getPartnercode();
			onBehalfOfMDNResult.setPartnerCode(enteredPartnerCode);
			failTheTransaction("Destination merchant failed partner validations",sctl.getID());
			notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
			validationResult = processValidationResultForDestinationAgent(validationResult,notificationCode);

			if(fundDefinition.getMaxfailattemptsallowed()!=-1){
				onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
			}else{
				onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
			}
			
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				onBehalfOfMDNResult.setOneTimePin(fundValidationService.regenerateFAC(unRegisteredTxnInfo));
			}
			fundValidationService.updateAvailableAmount(unRegisteredTxnInfo, null,false,fundWithdrawalConfirm.getAmount());
			onBehalfOfMDNResult.setNotificationCode(validationResult);
			sendSms(fundWithdrawalConfirm.getWithdrawalMDN(),onBehalfOfMDNResult);
			
			return result;
		}
		Set<Partner> partnerSet = destPartnerMDN.getSubscriber().getPartners();
		Partner destPartner = partnerSet.iterator().next();
		fundWithdrawalConfirm.setSourceMDN(srcPartnerMDN.getMdn());
		fundWithdrawalConfirm.setDestMDN(destPartnerMDN.getMdn());
		fundWithdrawalConfirm.setSourcePocketID(srcPocket.getId().longValue());
		fundWithdrawalConfirm.setIsSystemIntiatedTransaction(true);
		
		Pocket destPocket;
		PartnerServices partnerService = transactionChargingService.getPartnerService(destPartner.getId(), sctl.getServiceProviderID(), sctl.getServiceID());
		if (partnerService == null) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
			log.error("Partner service NULL");
			failTheTransaction("Service not available for agent",sctl.getID());
			notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
			
			if(fundDefinition.getMaxfailattemptsallowed()!=-1){
				onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
			}else{
				onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
			}
			
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				onBehalfOfMDNResult.setOneTimePin(fundValidationService.regenerateFAC(unRegisteredTxnInfo));
				onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgentNewFac);
			}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(notificationCode)){
				onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
			}else if(CmFinoFIX.NotificationCode_ReverseFundRequestInitaited.equals(notificationCode)){
				onBehalfOfMDNResult.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgentReversal);
			}
			fundValidationService.updateAvailableAmount(unRegisteredTxnInfo, null,false,fundWithdrawalConfirm.getAmount());
			sendSms(fundWithdrawalConfirm.getWithdrawalMDN(),onBehalfOfMDNResult);
			
			return result;
		}
		destPocket = partnerService.getPocketByDestpocketid();
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			failTheTransaction("Destination Money Pocket has failed validations",sctl.getID());
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
				onBehalfOfMDNResult.setOneTimePin(fundValidationService.regenerateFAC(unRegisteredTxnInfo));
			}
			fundValidationService.updateAvailableAmount(unRegisteredTxnInfo, null,false,fundWithdrawalConfirm.getAmount());
			sendSms(fundWithdrawalConfirm.getWithdrawalMDN(),onBehalfOfMDNResult);
			return result;
		}
		fundWithdrawalConfirm.setDestPocketID(destPocket.getId().longValue());
		
		log.info("Sending the fundWithdrawalConfirm request to backend for processing");
		CFIXMsg response = super.process(fundWithdrawalConfirm);
		result.setMultixResponse(response);

		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {
				transactionChargingService.addTransferID(sctl, fundWithdrawalConfirm.getTransferID());
				transactionChargingService.confirmTheTransaction(sctl, fundWithdrawalConfirm.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, fundWithdrawalConfirm.getTransferID());
				result.setDebitAmount(sctl.getTransactionAmount());
				result.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
				result.setServiceCharge(sctl.getCalculatedCharge());
			} else {
				String errorMsg = transactionResponse.getMessage();
				// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				transactionChargingService.failTheTransaction(sctl, errorMsg);
				failTheTransaction(errorMsg,sctl.getID());
				result.setNotificationCode(CmFinoFIX.NotificationCode_FundWithdrawalFailedToMerchant);
				notificationCode=fundValidationService.updateFailureAttempts(unRegisteredTxnInfo, fundDefinition);
				if(fundDefinition.getMaxfailattemptsallowed()!=-1){
					onBehalfOfMDNResult.setNumberOfTriesLeft((int)(fundDefinition.getMaxfailattemptsallowed()-unRegisteredTxnInfo.getWithdrawalfailureattempt()));
				}else{
					onBehalfOfMDNResult.setNumberOfTriesLeft(99999999);
				}
				
				if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
					onBehalfOfMDNResult.setOneTimePin(fundValidationService.regenerateFAC(unRegisteredTxnInfo));
				}
				onBehalfOfMDNResult.setNotificationCode(notificationCode);
				fundValidationService.updateAvailableAmount(unRegisteredTxnInfo, null,false,fundWithdrawalConfirm.getAmount());
				sendSms(fundWithdrawalConfirm.getWithdrawalMDN(),onBehalfOfMDNResult);
				return result;
				
			}
		}
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
	private void failTheTransaction(String failureReason,Long sctlId){
		log.error("Transaction with sctl "+sctlId+" failed.Reason: "+failureReason);
		FundDistributionInfo fundDistributionInfo = fundValidationService.queryFundDistributionInfo(sctlId);
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
