package com.mfino.transactionapi.handlers.wallet.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMFundAllocationConfirm;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.FundValidationService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.FundAllocationConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

@Service("FundAllocationConfirmHandlerImpl")
public class FundAllocationConfirmHandlerImpl extends FIXMessageHandler implements FundAllocationConfirmHandler{
	
	private static Logger log	= LoggerFactory.getLogger(FundAllocationConfirmHandlerImpl.class);
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("FundValidationServiceImpl")
	private FundValidationService fundValidationService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in FundAllocationConfirmHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		CMFundAllocationConfirm fundAllocationConfirm = new CMFundAllocationConfirm();
		boolean confirmed = false;
		confirmed = Boolean.parseBoolean(transactionDetails.getConfirmString());
		ChannelCode cc = transactionDetails.getCc();
		fundAllocationConfirm.setSourceMDN(transactionDetails.getSourceMDN());
		fundAllocationConfirm.setParentTransactionID(transactionDetails.getParentTxnId());
		fundAllocationConfirm.setTransferID(transactionDetails.getTransferId());
		fundAllocationConfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		fundAllocationConfirm.setConfirmed(confirmed);
		fundAllocationConfirm.setSourceApplication((int)cc.getChannelsourceapplication());
		fundAllocationConfirm.setChannelCode(cc.getChannelcode());
		if(StringUtils.isNotEmpty(transactionDetails.getPartnerCode())){
			fundAllocationConfirm.setPartnerCode(transactionDetails.getPartnerCode());
		}
		else{
			fundAllocationConfirm.setPartnerCode(ANY_PARTNER);
		}
		fundAllocationConfirm.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		log.info("Handling Fund Allocation confirmation WebAPI request for Parent Txn Id = " + fundAllocationConfirm.getParentTransactionID());
		XMLResult result = new MoneyTransferXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_FundAllocationConfirm,fundAllocationConfirm.DumpFields(),fundAllocationConfirm.getParentTransactionID());
		fundAllocationConfirm.setTransactionID(transactionsLog.getID());

		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(fundAllocationConfirm);
		result.setTransactionID(fundAllocationConfirm.getTransactionID());
		
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(fundAllocationConfirm.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+fundAllocationConfirm.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		Pocket srcSubscriberPocket = pocketService.getDefaultPocket(srcSubscriberMDN, null);
		validationResult = transactionApiValidationService.validateSourcePocket(srcSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(srcSubscriberPocket);
		result.setPocketList(pocketList);
		//change here
		String thirdPartyPartnerMDN = systemParametersService.getString(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
		log.info("doPost: Begin for destMDN:"+thirdPartyPartnerMDN);
		if (StringUtils.isBlank(thirdPartyPartnerMDN)) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			log.info("Third Party Partner MDN Value in System Parameters is Null");
			return result;
		}

		SubscriberMdn destPartnerMDN = subscriberMdnService.getByMDN(thirdPartyPartnerMDN);
		validationResult = transactionApiValidationService.validatePartnerMDN(destPartnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination Agent has failed validations");
			log.info("Third Party Partner is not Active");
			validationResult = processValidationResultForPartner(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket destPocket= pocketService.getSuspencePocket(partnerService.getPartner(destPartnerMDN));
		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		log.info("Checking for fundDefintions and purpose");
		boolean purposeAndFundDefinitionCheck = fundValidationService.validatePurposeAndFundDefinition(fundAllocationConfirm.getPartnerCode());
		if(!purposeAndFundDefinitionCheck){
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidFundDefinitionOrPurpose);
			log.error("Purpose or fundDefinition invalid");
			return result;
		}
		
		log.info("Getting the sctl id");
		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(fundAllocationConfirm.getParentTransactionID(),fundAllocationConfirm.getTransactionIdentifier());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				log.error("The status of Sctl with id: "+sctl.getID()+"has been changed from Inquiry to: "+sctl.getStatus());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}

		} else {
			log.error("Could not find sctl with parentTransaction ID: "+fundAllocationConfirm.getParentTransactionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			
			return result;
		}
		
		fundAllocationConfirm.setWithdrawalMDN(sctl.getOnBeHalfOfMDN());
		fundAllocationConfirm.setDestMDN(destPartnerMDN.getMdn());
		fundAllocationConfirm.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		fundAllocationConfirm.setDestPocketID(destPocket.getId().longValue());
		fundAllocationConfirm.setServiceChargeTransactionLogID(sctl.getID());
		
		log.info("Sending fundAllocationConfirm request to backend for processing");
		
		CFIXMsg response = super.process(fundAllocationConfirm);
		result.setMultixResponse(response);
		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {
				transactionChargingService.addTransferID(sctl, fundAllocationConfirm.getTransferID());
				transactionChargingService.confirmTheTransaction(sctl, fundAllocationConfirm.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, fundAllocationConfirm.getTransferID());
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
			}
		}
		result.setMessage(transactionResponse.getMessage());
		return result;
	}


}
