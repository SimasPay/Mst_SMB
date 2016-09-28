/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.transactionapi.handlers.wallet.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
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
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.SubscriberDonationInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 *
 * @author Bala Sunku
 */
@Service("SubscriberDonationInquiryHandlerImpl")
public class SubscriberDonationInquiryHandlerImpl extends FIXMessageHandler implements SubscriberDonationInquiryHandler {

	private static Logger log = LoggerFactory.getLogger(SubscriberDonationInquiryHandlerImpl.class);

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	public Result handle(TransactionDetails transactionDetails)
	{
		log.info("Subscriber Donation Inquiry from sourceMDN: " +  transactionDetails.getSourceMDN() +" for Amount: " + transactionDetails.getAmount());
		CMBankAccountToBankAccount txnInquiry = new CMBankAccountToBankAccount();
		ChannelCode cc = transactionDetails.getCc();
		txnInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		txnInquiry.setPin(transactionDetails.getSourcePIN());
		txnInquiry.setAmount(transactionDetails.getAmount());
		txnInquiry.setRemarks(transactionDetails.getDescription());
		txnInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		txnInquiry.setChannelCode(cc.getChannelcode());
		txnInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		txnInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		txnInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		txnInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Donation);
		
		XMLResult result = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccount, txnInquiry.DumpFields());
		txnInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(txnInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());

		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(txnInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+txnInquiry.getSourceMDN()+" has failed validations");
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
		txnInquiry.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		
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
		txnInquiry.setDestMDN(donationPartnerMDN);
		txnInquiry.setDestPocketID(destPocket.getId().longValue());

		// Calculate Service charge if applicable and crerates SCTL.
		ServiceCharge sc=new ServiceCharge();
		sc.setSourceMDN(srcSubscriberMDN.getMdn());
		sc.setDestMDN(donationPartnerMDN);
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_DONATION);
		sc.setTransactionAmount(txnInquiry.getAmount());
		sc.setTransactionLogId(txnInquiry.getTransactionID());
		sc.setTransactionIdentifier(txnInquiry.getTransactionIdentifier());
		sc.setDescription(transactionDetails.getDescription());
		
		Transaction transaction=null;
		try{
			transaction =transactionChargingService.getCharge(sc);
			txnInquiry.setAmount(transaction.getAmountToCredit());
			txnInquiry.setCharges(transaction.getAmountTowardsCharges());
		}catch (InvalidServiceException e) {
			log.error("InvalidServiceException occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error("InvalidChargeDefinitionException occured in getting charges", e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);			
			return result;
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		txnInquiry.setServiceChargeTransactionLogID(sctl.getID());

		log.info("sending the donation inquiry request to backend for processing");
		CFIXMsg response = super.process(txnInquiry);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			txnInquiry.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}
		if (!transactionResponse.isResult() && sctl!=null) 
		{
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}
		
		result.setSctlID(sctl.getID());
		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		addCompanyANDLanguageToResult(srcSubscriberMDN,result);
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		return result;
	}
}
