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

import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAgentToAgentTransfer;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.AgentToAgentTransferConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Maruthi
 * 
 */
@Service("AgentToAgentTransferConfirmHandlerImpl")
public class AgentToAgentTransferConfirmHandlerImpl extends FIXMessageHandler implements AgentToAgentTransferConfirmHandler{

	private static Logger log	= LoggerFactory.getLogger(AgentToAgentTransferConfirmHandlerImpl.class);
	
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
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in AgentToAgentTransferConfirmHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		ChannelCode cc = transactionDetails.getCc();
		boolean confirmed = false;
		confirmed = Boolean.parseBoolean(transactionDetails.getConfirmString());
		//setting the needed transactionDetails into the message
		CMAgentToAgentTransfer agentToAgentTransfer = new CMAgentToAgentTransfer();
		agentToAgentTransfer.setSourceMDN(transactionDetails.getSourceMDN());
		agentToAgentTransfer.setPartnerCode(transactionDetails.getPartnerCode());
		agentToAgentTransfer.setParentTransactionID(transactionDetails.getParentTxnId());
		agentToAgentTransfer.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		agentToAgentTransfer.setTransferID(transactionDetails.getTransferId());
		agentToAgentTransfer.setConfirmed(confirmed);
		agentToAgentTransfer.setSourceApplication((int)cc.getChannelsourceapplication());
		agentToAgentTransfer.setChannelCode(cc.getChannelcode());
		agentToAgentTransfer.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		Long sourcePocketId = transactionDetails.getSrcPocketId();
		Long destPocketId = transactionDetails.getDestinationPocketId();

		log.info("Handling Subscriber Cashout confirmation WebAPI request");
		XMLResult result = new MoneyTransferXMLResult();

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AgentToAgentTransfer,agentToAgentTransfer.DumpFields(),agentToAgentTransfer.getParentTransactionID());
		agentToAgentTransfer.setTransactionID(transactionsLog.getId().longValue());

		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(agentToAgentTransfer);
		result.setTransactionID(agentToAgentTransfer.getTransactionID());
		
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(agentToAgentTransfer.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateAgentMDN(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source Agent with mdn : "+agentToAgentTransfer.getSourceMDN()+" has failed validations");
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}

		Pocket srcSubscriberPocket = null;
		if(sourcePocketId!=null){
			srcSubscriberPocket = pocketService.getById(sourcePocketId);
		}
		else 
		{
			srcSubscriberPocket = pocketService.getDefaultPocket(srcSubscriberMDN, null);
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

		Partner destAgent = partnerService.getPartnerByPartnerCode(agentToAgentTransfer.getPartnerCode());
		validationResult = transactionApiValidationService.validateAgentByPartnerType(destAgent);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination Agent has failed validations");
			result.setPartnerCode(agentToAgentTransfer.getPartnerCode());
			validationResult = processValidationResultForDestinationAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		SubscriberMdn destAgentMDN = destAgent.getSubscriber().getSubscriberMdns().iterator().next();

		Pocket destAgentPocket; 
		if(destPocketId!=null){
			destAgentPocket = pocketService.getById(destPocketId);
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

		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTxnLog sctl = transactionChargingService.getServiceChargeTransactionLog(agentToAgentTransfer.getParentTransactionID(),agentToAgentTransfer.getTransactionIdentifier());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				log.error("The status of Sctl with id: "+sctl.getId()+"has been changed from Inquiry to: "+sctl.getStatus());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}

		} else {
			log.error("Could not find sctl with parentTransaction ID: "+agentToAgentTransfer.getParentTransactionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}		
		
		agentToAgentTransfer.setServiceChargeTransactionLogID(sctl.getId().longValue());
		agentToAgentTransfer.setDestMDN(destAgentMDN.getMdn());
		agentToAgentTransfer.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		agentToAgentTransfer.setDestPocketID(destAgentPocket.getId().longValue());

		log.info("sending the agentToAgentTransfer request to backend for processing");
		CFIXMsg response = super.process(agentToAgentTransfer);
		result.setMultixResponse(response);

		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {
				transactionChargingService.confirmTheTransaction(sctl, agentToAgentTransfer.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, agentToAgentTransfer.getTransferID());
				result.setDebitAmount(sctl.getTransactionamount());
				result.setCreditAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
				result.setServiceCharge(sctl.getCalculatedcharge());
			} else {
				String errorMsg = transactionResponse.getMessage();
				// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				transactionChargingService.failTheTransaction(sctl, errorMsg);
			}
		}

		result.setSctlID(sctl.getId().longValue());
		result.setMessage(transactionResponse.getMessage());
		result.setCode(transactionResponse.getCode());
		return result;
	}
}