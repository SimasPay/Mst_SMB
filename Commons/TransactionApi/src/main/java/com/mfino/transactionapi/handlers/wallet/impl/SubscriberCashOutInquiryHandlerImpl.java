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

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
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
import com.mfino.fix.CmFinoFIX.CMCashOutInquiry;
import com.mfino.fix.CmFinoFIX.CMSubscriberCashOutInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.SubscriberCashOutInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 *
 * @author Srinu
 */
@Service("SubscriberCashOutInquiryHandlerImpl")
public class SubscriberCashOutInquiryHandlerImpl extends FIXMessageHandler implements SubscriberCashOutInquiryHandler{

	private static Logger log = LoggerFactory.getLogger(SubscriberCashOutInquiryHandlerImpl.class);

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
	@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	public Result handle(TransactionDetails transactionDetails)
	{
		log.info("Extracting data from transactionDetails in SubscriberCashOutInquiryHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		CMSubscriberCashOutInquiry subscriberCashOutInquiry = new CMSubscriberCashOutInquiry();
		ChannelCode cc = transactionDetails.getCc();
		subscriberCashOutInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		subscriberCashOutInquiry.setPartnerCode(transactionDetails.getPartnerCode());
		subscriberCashOutInquiry.setPin(transactionDetails.getSourcePIN());
		subscriberCashOutInquiry.setAmount(transactionDetails.getAmount());
		subscriberCashOutInquiry.setSourceApplication(cc.getChannelSourceApplication());
		subscriberCashOutInquiry.setChannelCode(cc.getChannelCode());
		subscriberCashOutInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		subscriberCashOutInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		subscriberCashOutInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		log.info("Handling Subscriber CashOut Inquiry webapi request::From " + subscriberCashOutInquiry.getSourceMDN() + 
				" To agent " + subscriberCashOutInquiry.getPartnerCode() + " For Amount = " + subscriberCashOutInquiry.getAmount());
		XMLResult result = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberCashOutInquiry, subscriberCashOutInquiry.DumpFields());
		subscriberCashOutInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(subscriberCashOutInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());

		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(subscriberCashOutInquiry.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+subscriberCashOutInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket srcSubscriberPocket = pocketService.getDefaultPocket(srcSubscriberMDN, transactionDetails.getSourcePocketCode());
		validationResult = transactionApiValidationService.validateSourcePocket(srcSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(srcSubscriberPocket);
		result.setPocketList(pocketList);

		Partner destAgent = partnerService.getPartnerByPartnerCode(subscriberCashOutInquiry.getPartnerCode());
		validationResult = transactionApiValidationService.validateAgentByPartnerType(destAgent);
		if(validationResult.equals(CmFinoFIX.NotificationCode_DestinationAgentNotFound)){
			log.info("Destination failed agent validations.validating if the destination is teller");
			validationResult= transactionApiValidationService.validateTellerByPartnerType(destAgent);
		}
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			result.setPartnerCode(subscriberCashOutInquiry.getPartnerCode());
			validationResult = processValidationResultForDestinationAgent(validationResult);
			result.setNotificationCode(validationResult);
			return result;
		}
		SubscriberMDN destAgentMDN = destAgent.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();

		// add service charge to amount

		log.info("creating the serviceCharge object....");
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getID());
		sc.setDestMDN(destAgentMDN.getMDN());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT);
		sc.setSourceMDN(srcSubscriberMDN.getMDN());
		sc.setTransactionAmount(subscriberCashOutInquiry.getAmount());
		sc.setMfsBillerCode(subscriberCashOutInquiry.getPartnerCode());
		sc.setTransactionLogId(subscriberCashOutInquiry.getTransactionID());
		sc.setTransactionIdentifier(subscriberCashOutInquiry.getTransactionIdentifier());
		if(destAgent.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_BranchOffice)){
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
		}else{
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);//change to service
		}
		
		Pocket destAgentPocket;
		try {
			long servicePartnerId = transactionChargingService.getServiceProviderId(null);
			long serviceId = transactionChargingService.getServiceId(sc.getServiceName());
			PartnerServices partnerService = transactionChargingService.getPartnerService(destAgent.getID(), servicePartnerId, serviceId);
			if (partnerService == null) {
				log.error("PartnerService obtained null ");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
				return result;
			}
			destAgentPocket = partnerService.getPocketByDestPocketID();
			validationResult = transactionApiValidationService.validateDestinationPocket(destAgentPocket);
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				log.error("Destination pocket with id "+(destAgentPocket!=null? destAgentPocket.getID():null)+" has failed validations");
				result.setNotificationCode(validationResult);
				return result;
			}
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting Source Pocket",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}
		
		//For Hierarchy
		validationResult = hierarchyService.validate(srcSubscriberMDN.getSubscriber(), destAgentMDN.getSubscriber(), sc.getServiceName(), sc.getTransactionTypeName());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Transaction transaction=null;
		try{
			transaction =transactionChargingService.getCharge(sc);
			subscriberCashOutInquiry.setAmount(transaction.getAmountToCredit());
			subscriberCashOutInquiry.setCharges(transaction.getAmountTowardsCharges());
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);//change to service not found
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);			
			return result;
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();

		CMCashOutInquiry cashout = new CMCashOutInquiry();
		cashout.setSourceMDN(subscriberCashOutInquiry.getSourceMDN());
		cashout.setDestMDN(destAgentMDN.getMDN());
		cashout.setAmount(subscriberCashOutInquiry.getAmount());
		cashout.setCharges(transaction.getAmountTowardsCharges());
		cashout.setTransactionID(subscriberCashOutInquiry.getTransactionID());
		cashout.setChannelCode(cc.getChannelCode());
		cashout.setPin(subscriberCashOutInquiry.getPin());
		cashout.setSourcePocketID(srcSubscriberPocket.getID());
		cashout.setDestPocketID(destAgentPocket.getID());
		cashout.setSourceApplication(cc.getChannelSourceApplication());
		cashout.setServletPath(subscriberCashOutInquiry.getServletPath());
		cashout.setSourceMessage(subscriberCashOutInquiry.getSourceMessage());
		cashout.setServiceChargeTransactionLogID(sctl.getID());
		cashout.setTransactionIdentifier(subscriberCashOutInquiry.getTransactionIdentifier());
		if(destAgent.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_BranchOffice)){
			cashout.setUICategory(CmFinoFIX.TransactionUICategory_Teller_Cashout);
		}
		
		log.info("sending the cashout request to backend for processing");
		CFIXMsg response = super.process(cashout);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			subscriberCashOutInquiry.setTransactionID(transactionResponse.getTransactionId());
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
