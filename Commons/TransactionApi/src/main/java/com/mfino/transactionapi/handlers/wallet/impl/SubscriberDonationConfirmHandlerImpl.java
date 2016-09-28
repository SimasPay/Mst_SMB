/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.SubscriberDonationConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 * 
 */
@Service("SubscriberDonationConfirmHandlerImpl")
public class SubscriberDonationConfirmHandlerImpl extends FIXMessageHandler implements SubscriberDonationConfirmHandler {

	private static Logger log	= LoggerFactory.getLogger(SubscriberDonationConfirmHandlerImpl.class);
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;

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
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Subscriber Donation Confirm from sourceMDN: "+transactionDetails.getSourceMDN() +" for Amount: "+transactionDetails.getAmount());
		CMBankAccountToBankAccountConfirmation txnConfirm = new CMBankAccountToBankAccountConfirmation();
		ChannelCode cc = transactionDetails.getCc();
		boolean confirmed = false;
		confirmed = Boolean.parseBoolean(transactionDetails.getConfirmString());
		txnConfirm.setSourceMDN(transactionDetails.getSourceMDN());
		txnConfirm.setParentTransactionID(transactionDetails.getParentTxnId());
		txnConfirm.setTransferID(transactionDetails.getTransferId());
		txnConfirm.setConfirmed(confirmed);
		txnConfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		txnConfirm.setSourceApplication((int)cc.getChannelsourceapplication());
		txnConfirm.setChannelCode(cc.getChannelcode());
		txnConfirm.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		txnConfirm.setUICategory(CmFinoFIX.TransactionUICategory_Donation);

		XMLResult result = new MoneyTransferXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation,
				txnConfirm.DumpFields(),txnConfirm.getParentTransactionID());
		txnConfirm.setTransactionID(transactionsLog.getID());
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(txnConfirm);
		result.setTransactionID(txnConfirm.getTransactionID());

		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(txnConfirm.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+txnConfirm.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket srcSubscriberPocket = pocketService.getDefaultPocket(srcSubscriberMDN, transactionDetails.getSourcePocketCode());
		validationResult = transactionApiValidationService.validateSourcePocket(srcSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(srcSubscriberPocket);
		result.setPocketList(pocketList);
		txnConfirm.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		
		String donationPartnerMDN = systemParametersService.getString(SystemParameterKeys.DONATION_PARTNER_MDN);
		SubscriberMdn destSubscriberMDN = subscriberMdnService.getByMDN(donationPartnerMDN);
		validationResult = transactionApiValidationService.validatePartnerMDN(destSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Donation Partner mdn : "+donationPartnerMDN+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		Pocket destPocket;
		try {
			Partner donationPartner = partnerService.getPartner(destSubscriberMDN);
			long servicePartnerId = transactionChargingService.getServiceProviderId(null);
			long serviceId = transactionChargingService.getServiceId(transactionDetails.getServiceName());
			PartnerServices partnerService = transactionChargingService.getPartnerService(donationPartner.getId().longValue(), servicePartnerId, serviceId);
			if (partnerService == null) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner);
				return result;
			}
			destPocket = partnerService.getPocketByDestpocketid();
			validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				log.error("Donation partner pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
				result.setNotificationCode(validationResult);
				return result;
			}	
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting donation partner Pocket",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}
		txnConfirm.setDestMDN(donationPartnerMDN);
		txnConfirm.setDestPocketID(destPocket.getId().longValue());

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(txnConfirm.getParentTransactionID(),
				txnConfirm.getTransactionIdentifier());
		if(sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				log.error("The status of Sctl with id: "+sctl.getID()+"has been changed from Inquiry to: "+sctl.getStatus());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}
		} else {
			log.error("Could not find sctl with parentTransaction ID: "+txnConfirm.getParentTransactionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);			
			return result;
		}		
		
		txnConfirm.setRemarks(sctl.getDescription());
		txnConfirm.setServiceChargeTransactionLogID(sctl.getID());
		
		log.info("sending the Donation confirmation request to backend for processing");
		CFIXMsg response = super.process(txnConfirm);
		result.setMultixResponse(response);
		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {
				transactionChargingService.confirmTheTransaction(sctl, txnConfirm.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, txnConfirm.getTransferID());
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
		
		result.setSctlID(sctl.getID());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		return result;
	}
}