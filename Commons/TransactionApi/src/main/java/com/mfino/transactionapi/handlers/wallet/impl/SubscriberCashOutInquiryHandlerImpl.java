/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiry;
import com.mfino.fix.CmFinoFIX.CMSubscriberCashOutInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.MFAService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
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
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
		
	public Result handle(TransactionDetails transactionDetails)
	{
		log.info("Begin SubscriberCashOutInquiryHandlerImpl :: handle method");
		log.info("Extracting data from transactionDetails in SubscriberCashOutInquiryHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()+"to"+transactionDetails.getDestMDN());
		
		//XMLResult result = new TransferInquiryXMLResult();
		TransferInquiryXMLResult result = new TransferInquiryXMLResult();
		transactionDetails.setSourcePocketCode(String.valueOf(CmFinoFIX.PocketType_LakuPandai));
		ChannelCode cc = transactionDetails.getCc();
		
		CMSubscriberCashOutInquiry subscriberCashOutInquiry = new CMSubscriberCashOutInquiry();
		
		subscriberCashOutInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		subscriberCashOutInquiry.setDestMDN(transactionDetails.getDestMDN());
		subscriberCashOutInquiry.setPartnerCode(transactionDetails.getPartnerCode());
		subscriberCashOutInquiry.setPin(transactionDetails.getSourcePIN());
		subscriberCashOutInquiry.setAmount(transactionDetails.getAmount());
		subscriberCashOutInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		subscriberCashOutInquiry.setChannelCode(cc.getChannelcode());
		subscriberCashOutInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		subscriberCashOutInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		subscriberCashOutInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		//log.info("Handling Subscriber CashOut Inquiry webapi request::From " + subscriberCashOutInquiry.getSourceMDN() +" To agent " + subscriberCashOutInquiry.getPartnerCode() + " For Amount = " + subscriberCashOutInquiry.getAmount());
		log.info("Handling Subscriber CashOut Inquiry webapi request::From " + subscriberCashOutInquiry.getSourceMDN() +" To agent " + subscriberCashOutInquiry.getDestMDN() + " For Amount = " + subscriberCashOutInquiry.getAmount());
		

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberCashOutInquiry, subscriberCashOutInquiry.DumpFields());
		subscriberCashOutInquiry.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(subscriberCashOutInquiry);
		result.setTransactionTime(transactionsLog.getTransactiontime());

		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(subscriberCashOutInquiry.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+subscriberCashOutInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Subscriber srcSub = srcSubscriberMDN.getSubscriber();
		KycLevel srcKyc = srcSub.getKycLevel();
		if(srcKyc.getKyclevel().equals(new Long(CmFinoFIX.SubscriberKYCLevel_NoKyc))){
			log.info(String.format("Cash-out transaction is Failed as the the Source Subscriber(%s) KycLevel is NoKyc",transactionDetails.getSourceMDN()));
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyTransferFromNoKycSubscriberNotAllowed);
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

		//Partner destAgent = partnerService.getPartnerByPartnerCode(subscriberCashOutInquiry.getPartnerCode());
		Partner destAgent = partnerService.getPartner(subscriberCashOutInquiry.getDestMDN());
		validationResult = transactionApiValidationService.validateAgentByPartnerType(destAgent);
		if(validationResult.equals(CmFinoFIX.NotificationCode_DestinationAgentNotFound)){
			log.info("Destination agent failed validations.validating if the destination is teller");
			validationResult= transactionApiValidationService.validateTellerByPartnerType(destAgent);
		}
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			//result.setPartnerCode(subscriberCashOutInquiry.getPartnerCode());
			result.setDestinationMDN(subscriberCashOutInquiry.getDestMDN());
			validationResult = processValidationResultForDestinationAgent(validationResult);
			result.setNotificationCode(validationResult);
			return result;
		}
		SubscriberMdn destAgentMDN = destAgent.getSubscriber().getSubscriberMdns().iterator().next();

		// add service charge to amount

		log.info("creating the serviceCharge object....");
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setDestMDN(destAgentMDN.getMdn());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT);
		sc.setSourceMDN(srcSubscriberMDN.getMdn());
		sc.setTransactionAmount(subscriberCashOutInquiry.getAmount());
		//sc.setMfsBillerCode(subscriberCashOutInquiry.getPartnerCode());
		sc.setMfsBillerCode(destAgent.getPartnercode());
		sc.setTransactionLogId(subscriberCashOutInquiry.getTransactionID());
		sc.setTransactionIdentifier(subscriberCashOutInquiry.getTransactionIdentifier());
		if(destAgent.getBusinesspartnertype().equals(CmFinoFIX.BusinessPartnerType_BranchOffice)){
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
		}else{
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);//change to service
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
		
		Pocket destAgentPocket;
		try {
			long servicePartnerId = transactionChargingService.getServiceProviderId(null);
			long serviceId = transactionChargingService.getServiceId(sc.getServiceName());
			PartnerServices partnerService = transactionChargingService.getPartnerService(destAgent.getId().longValue(), servicePartnerId, serviceId);
			if (partnerService == null) {
				log.error("PartnerService obtained null ");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
				return result;
			}
			destAgentPocket = partnerService.getPocketByDestpocketid();
			validationResult = transactionApiValidationService.validateDestinationPocket(destAgentPocket);
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				log.error("Destination pocket with id "+(destAgentPocket!=null? destAgentPocket.getId():null)+" has failed validations");
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
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();

		CMCashOutInquiry cashout = new CMCashOutInquiry();
		cashout.setSourceMDN(subscriberCashOutInquiry.getSourceMDN());
		cashout.setDestMDN(destAgentMDN.getMdn());
		cashout.setAmount(subscriberCashOutInquiry.getAmount());
		cashout.setCharges(transaction.getAmountTowardsCharges());
		cashout.setTransactionID(subscriberCashOutInquiry.getTransactionID());
		cashout.setChannelCode(cc.getChannelcode());
		cashout.setPin(subscriberCashOutInquiry.getPin());
		cashout.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		cashout.setDestPocketID(destAgentPocket.getId().longValue());
		cashout.setSourceApplication((int)cc.getChannelsourceapplication());
		cashout.setServletPath(subscriberCashOutInquiry.getServletPath());
		cashout.setSourceMessage(subscriberCashOutInquiry.getSourceMessage());
		cashout.setServiceChargeTransactionLogID(sctl.getId().longValue());
		cashout.setTransactionIdentifier(subscriberCashOutInquiry.getTransactionIdentifier());
		if(destAgent.getBusinesspartnertype().equals(CmFinoFIX.BusinessPartnerType_BranchOffice)){
			cashout.setUICategory(CmFinoFIX.TransactionUICategory_Teller_Cashout);
		}
		
		log.info("sending the cashout request to backend for processing");
		CFIXMsg response = super.process(cashout);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionid(BigDecimal.valueOf(transactionResponse.getTransactionId()));
			subscriberCashOutInquiry.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);

		}
		if (!transactionResponse.isResult() && sctl!=null) 
		{
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}
		
		result.setSctlID(sctl.getId().longValue());
		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		addCompanyANDLanguageToResult(srcSubscriberMDN,result);
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setDestinationMDN(destAgentMDN.getMdn());
		result.setName(destAgentMDN.getSubscriber().getFirstname());
		log.info("End SubscriberCashOutInquiryHandlerImpl :: handle method");
		
		
		result.setMfaMode("None");
		
		//For 2 factor authentication
		if(transactionResponse.isResult()){
			if(mfaService.isMFATransaction(transactionDetails.getServiceName(), ServiceAndTransactionConstants.TRANSACTION_CASHOUT, cc.getId().longValue()) == true){
				result.setMfaMode("OTP");
				//mfaService.handleMFATransaction(sctl.getID(), srcSubscriberMDN.getMDN());
			}
		}
		
		return result;
	}
}
