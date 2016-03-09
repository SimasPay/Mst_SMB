/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
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
import com.mfino.fix.CmFinoFIX.CMAgentCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMCashInInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.AgentCashInInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 *
 * @author Srinu
 */
@Service("AgentCashInInquiryHandlerImpl")
public class AgentCashInInquiryHandlerImpl extends FIXMessageHandler implements AgentCashInInquiryHandler{

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
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
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	public Result handle(TransactionDetails transactionDetails) 
	{
		log.info("Extracting data from transactionDetails in AgentCashInInquiryHandlerImpl from sourceMDN: "+ transactionDetails.getSourceMDN()+"to"+transactionDetails.getDestMDN());
		
		transactionDetails.setDestPocketCode(String.valueOf(CmFinoFIX.PocketType_LakuPandai));
		//transactionDetails.setSystemIntiatedTransaction(true);
		
		CMAgentCashInInquiry agentCashinInquiry = new CMAgentCashInInquiry();

		ChannelCode cc = transactionDetails.getCc();
		BigDecimal amount = transactionDetails.getAmount();
		String destpocketcode=transactionDetails.getDestPocketCode();

		agentCashinInquiry.setSourceMDN(subscriberService.normalizeMDN(transactionDetails.getSourceMDN()));
		agentCashinInquiry.setAmount(amount);
		agentCashinInquiry.setPin(transactionDetails.getSourcePIN());
		agentCashinInquiry.setDestMDN(subscriberService.normalizeMDN(transactionDetails.getDestMDN()));
		agentCashinInquiry.setSourceApplication(cc.getChannelSourceApplication());
		agentCashinInquiry.setChannelCode(cc.getChannelCode());
		agentCashinInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		agentCashinInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		agentCashinInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		log.info("Handling Agent to subscriber cashin inquiry webapi request::From " + agentCashinInquiry.getSourceMDN() + " To " + 
				agentCashinInquiry.getDestMDN() + " For Amount = " + agentCashinInquiry.getAmount());

		//XMLResult result = new TransferInquiryXMLResult();
		TransferInquiryXMLResult result = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AgentCashInInquiry, agentCashinInquiry.DumpFields());
		agentCashinInquiry.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(agentCashinInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setDestinationMDN(agentCashinInquiry.getDestMDN());
		
		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(agentCashinInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateAgentMDN(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Agent with mdn : "+agentCashinInquiry.getSourceMDN()+" has failed validations");
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		SubscriberMDN destinationMDN = subscriberMdnService.getByMDN(agentCashinInquiry.getDestMDN());
		validationResult = transactionApiValidationService.validateSubscriberAsDestination(destinationMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Destination subscriber with mdn : "+agentCashinInquiry.getDestMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		Pocket destSubscriberPocket = pocketService.getDefaultPocket(destinationMDN, destpocketcode);
		validationResult = transactionApiValidationService.validateDestinationPocket(destSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with pocket id : "+(destSubscriberPocket!=null? destSubscriberPocket.getID():null)+" of the subscriber "+destinationMDN.getMDN()+
					" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		log.info("creating the serviceCharge object....");
		Transaction transDetails = null;
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getID());
		sc.setDestMDN(destinationMDN.getMDN());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CASHIN);
		sc.setSourceMDN(srcSubscriberMDN.getMDN());
		sc.setTransactionAmount(agentCashinInquiry.getAmount());
		sc.setMfsBillerCode(agentCashinInquiry.getPartnerCode());
		sc.setTransactionLogId(agentCashinInquiry.getTransactionID());
		sc.setTransactionIdentifier(agentCashinInquiry.getTransactionIdentifier());

		validationResult = hierarchyService.validate(srcSubscriberMDN.getSubscriber(), destinationMDN.getSubscriber(), sc.getServiceName(), sc.getTransactionTypeName());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("validations falied at hierarchy service for sourceSubscriberMDN : "+srcSubscriberMDN+" destinationSubscriberMDN: "+destinationMDN+
					" service: "+sc.getServiceName()+" transactionType: "+sc.getTransactionTypeName());
			result.setNotificationCode(validationResult);
			return result;
		}
		
		BigDecimal minCashInValue = systemParametersService.getBigDecimal(SystemParameterKeys.MINIMUM_VALUE_OF_CASHIN);
		//BigDecimal maxCashInValue = systemParametersService.getBigDecimal(SystemParameterKeys.MAXIMUM_VALUE_OF_CASHIN);
		
		if (minCashInValue.compareTo(transactionDetails.getAmount()) > 0) {
			result.setMinAmount(minCashInValue);
			//result.setMaxAmount(maxCashInValue);
			result.setAmount(transactionDetails.getAmount());
			result.setNotificationCode(CmFinoFIX.NotificationCode_CashInAmountMustbeMinAmount);
			result.setCode(String.valueOf(CmFinoFIX.NotificationCode_CashInAmountMustbeMinAmount));
			
			return result;
		}
		
		/*if (maxCashInValue.compareTo(transactionDetails.getAmount()) < 0) {
			result.setMinAmount(minCashInValue);
			result.setMaxAmount(maxCashInValue);
			result.setAmount(transactionDetails.getAmount());
			result.setNotificationCode(CmFinoFIX.NotificationCode_CashInAmountMustbeMaxAmount);
			result.setCode(String.valueOf(CmFinoFIX.NotificationCode_CashInAmountMustbeMaxAmount));
			
			return result;
		}*/
		
		BigDecimal cashInValueMulOf = systemParametersService.getBigDecimal(SystemParameterKeys.CASHIN_VALUE_MULTIPLES_OFF);
		
		if ( BigDecimal.ZERO.compareTo(transactionDetails.getAmount().remainder(cashInValueMulOf)) != 0 ) {
			result.setMultiplesOff(cashInValueMulOf);
			result.setAmount(transactionDetails.getAmount());
			result.setNotificationCode(CmFinoFIX.NotificationCode_CashInAmountMustbeMultiplesOff);
			result.setCode(String.valueOf(CmFinoFIX.NotificationCode_CashInAmountMustbeMultiplesOff));
			
			return result;
		}
		
		Pocket srcAgentPocket;
		try {
			PartnerServices partnerService = transactionChargingService.getPartnerService(sc);
			if (partnerService == null) {
				log.error("PartnerService obtained null ");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
				return result;
			}
			srcAgentPocket = partnerService.getPocketBySourcePocket();
			validationResult = transactionApiValidationService.validateSourcePocket(srcAgentPocket);
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				log.error("Source pocket with id "+(srcAgentPocket!=null? srcAgentPocket.getID():null)+" has failed validations");
				result.setNotificationCode(validationResult);
				return result;
			}
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting Source Pocket",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}

		try{
			transDetails =transactionChargingService.getCharge(sc);
			agentCashinInquiry.setAmount(transDetails.getAmountToCredit());
			agentCashinInquiry.setCharges(transDetails.getAmountTowardsCharges());
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		if(destSubscriberPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount) || 
				srcAgentPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount))
		{
			if(!systemParametersService.getBoolean("SystemParameterKeys.BANK_SERVICE_STATUS"))
			{
				log.info("The bank service is down as set in the system parameter");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
				return result;
			}
		}

		ServiceChargeTransactionLog sctl = transDetails.getServiceChargeTransactionLog();
		
		CMCashInInquiry cashIn = new CMCashInInquiry();
		cashIn.setSourceMDN(srcSubscriberMDN.getMDN());
		cashIn.setDestMDN(destinationMDN.getMDN());
		cashIn.setAmount(agentCashinInquiry.getAmount());
		cashIn.setCharges(agentCashinInquiry.getCharges());
		cashIn.setTransactionID(agentCashinInquiry.getTransactionID());
		cashIn.setChannelCode(cc.getChannelCode());
		cashIn.setPin(agentCashinInquiry.getPin());
		cashIn.setSourcePocketID(srcAgentPocket.getID());
		cashIn.setDestPocketID(destSubscriberPocket.getID());
		cashIn.setSourceApplication(cc.getChannelSourceApplication());
		cashIn.setSourceMessage(agentCashinInquiry.getSourceMessage());
		cashIn.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashIn.setUICategory(CmFinoFIX.TransactionUICategory_Cashin_At_Agent);
		cashIn.setServiceChargeTransactionLogID(sctl.getID());
		cashIn.setTransactionIdentifier(agentCashinInquiry.getTransactionIdentifier());
		//cashIn.setIsSystemIntiatedTransaction(transactionDetails.isSystemIntiatedTransaction());
		
		log.info("sending request to backend for processing");
		CFIXMsg response = super.process(cashIn);
		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());
		
		if (!transactionResponse.isResult() && sctl!=null) {
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}
		
		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			//agentCashinInquiry.setTransactionID(transactionResponse.getTransactionId());
			cashIn.setTransactionID(transactionResponse.getTransactionId());			
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
			
			result.setMfaMode("OTP");
			mfaService.handleMFATransaction(sctl.getID(), srcSubscriberMDN.getMDN());
		}

		result.setSctlID(sctl.getID());
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setMultixResponse(response);
		result.setDebitAmount(transDetails.getAmountToDebit());
		result.setCreditAmount(transDetails.getAmountToCredit());
		result.setServiceCharge(transDetails.getAmountTowardsCharges());
		addCompanyANDLanguageToResult(srcSubscriberMDN, result);
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setName(destinationMDN.getSubscriber().getFirstName());
		return result;
	}
}
