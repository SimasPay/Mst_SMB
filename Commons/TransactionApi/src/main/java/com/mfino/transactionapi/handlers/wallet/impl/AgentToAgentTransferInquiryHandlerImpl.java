/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAgentToAgentTransferInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.AgentToAgentTransferInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 *
 * @author Srinu
 */
@Service("AgentToAgentTransferInquiryHandlerImpl")
public class AgentToAgentTransferInquiryHandlerImpl extends FIXMessageHandler implements AgentToAgentTransferInquiryHandler{

	private static Logger log = LoggerFactory.getLogger(AgentToAgentTransferInquiryHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
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
	
	public Result handle(TransactionDetails transactionDetails)
	{
		log.info("Extracting data from transactionDetails in AgentToAgentTransferInquiryHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());

		ChannelCode cc = transactionDetails.getCc();
		CMAgentToAgentTransferInquiry agentToAgentTrfInquiry = new CMAgentToAgentTransferInquiry();
		BigDecimal amount = transactionDetails.getAmount();
		
		String serviceName = ServiceAndTransactionConstants.SERVICE_WALLET;
		if(StringUtils.isNotBlank(transactionDetails.getServiceName())){
			serviceName = transactionDetails.getServiceName();
		}
		String txnName = ServiceAndTransactionConstants.TRANSACTION_AGENT_TO_AGENT_TRANSFER;
		/*if(StringUtils.isNotBlank(transactionDetails.getTransactionName())){
			txnName = transactionDetails.getTransactionName();
		}*/

		agentToAgentTrfInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		agentToAgentTrfInquiry.setPartnerCode(transactionDetails.getPartnerCode());
		agentToAgentTrfInquiry.setPin(transactionDetails.getSourcePIN());
		agentToAgentTrfInquiry.setAmount(amount);
		agentToAgentTrfInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		agentToAgentTrfInquiry.setChannelCode(cc.getChannelcode());
		agentToAgentTrfInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		agentToAgentTrfInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		agentToAgentTrfInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

		log.info("Handling Agent To Agent transfer Inquiry webapi request::From " + agentToAgentTrfInquiry.getSourceMDN() + 
				" To " + agentToAgentTrfInquiry.getDestMDN() + " For Amount = " + agentToAgentTrfInquiry.getAmount());
		XMLResult result = new TransferInquiryXMLResult();

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AgentToAgentTransferInquiry, agentToAgentTrfInquiry.DumpFields());
		agentToAgentTrfInquiry.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(agentToAgentTrfInquiry);
		result.setTransactionTime(transactionsLog.getTransactiontime());

		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(agentToAgentTrfInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateAgentMDN(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source Agent with mdn : "+agentToAgentTrfInquiry.getSourceMDN()+" has failed validations");
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}

		Pocket srcSubscriberPocket = null;
		if(transactionDetails.getSrcPocketId()!=null){
			srcSubscriberPocket = pocketService.getById(transactionDetails.getSrcPocketId());
		}
		else 
		{
			srcSubscriberPocket = pocketService.getDefaultPocket(srcSubscriberMDN, transactionDetails.getSourcePocketCode());
		}
		validationResult = transactionApiValidationService.validateSourcePocket(srcSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(srcSubscriberPocket);
		result.setPocketList(pocketList);

		Partner destAgent = partnerService.getPartnerByPartnerCode(agentToAgentTrfInquiry.getPartnerCode());
		validationResult = transactionApiValidationService.validateAgentByPartnerType(destAgent);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination Agent has failed validations");
			result.setPartnerCode(agentToAgentTrfInquiry.getPartnerCode());
			validationResult = processValidationResultForDestinationAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		SubscriberMdn destAgentMDN = destAgent.getSubscriber().getSubscriberMdns().iterator().next();
		
		Pocket destAgentPocket; 
		if(transactionDetails.getDestinationPocketId()!=null){
			destAgentPocket = pocketService.getById(transactionDetails.getDestinationPocketId());
		}
		else 
		{
			destAgentPocket = pocketService.getDefaultPocket(destAgentMDN, null);
		}
		validationResult = transactionApiValidationService.validateDestinationPocket(destAgentPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destAgentPocket!=null? destAgentPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		// add service charge to amount

		log.info("creating the serviceCharge object....");
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setDestMDN(destAgentMDN.getMdn());
		sc.setServiceName(serviceName);//change to service
		sc.setTransactionTypeName(txnName);
		sc.setSourceMDN(srcSubscriberMDN.getMdn());
		sc.setTransactionAmount(agentToAgentTrfInquiry.getAmount());
		sc.setMfsBillerCode(agentToAgentTrfInquiry.getPartnerCode());
		sc.setTransactionLogId(agentToAgentTrfInquiry.getTransactionID());
		sc.setTransactionIdentifier(agentToAgentTrfInquiry.getTransactionIdentifier());

		validationResult = hierarchyService.validate(srcSubscriberMDN.getSubscriber(), ((destAgentMDN != null) ? destAgentMDN.getSubscriber() : null), sc.getServiceName(), sc.getTransactionTypeName());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Transaction transaction = null;
		try{
			transaction =transactionChargingService.getCharge(sc);
			agentToAgentTrfInquiry.setAmount(transaction.getAmountToCredit());
			agentToAgentTrfInquiry.setCharges(transaction.getAmountTowardsCharges());

		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);//change to service not found
			//saveActivitiesLog(result, subscriberMDN);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		agentToAgentTrfInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());
		agentToAgentTrfInquiry.setDestMDN(destAgentMDN.getMdn());
		agentToAgentTrfInquiry.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		agentToAgentTrfInquiry.setDestPocketID(destAgentPocket.getId().longValue());
		
		log.info("sending the agentToAgentTrfInquiry request to backend for processing");
		CFIXMsg response = super.process(agentToAgentTrfInquiry);
		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);		
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!transactionResponse.isResult() && sctl!=null) 
		{
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}
		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionid(BigDecimal.valueOf(transactionResponse.getTransactionId()));
			agentToAgentTrfInquiry.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
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
		return result;
	}
}
