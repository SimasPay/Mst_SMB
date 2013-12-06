/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAgentCashInConfirm;
import com.mfino.fix.CmFinoFIX.CMCashIn;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.AgentCashInConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.wallet.WalletConfirmXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Deva
 * 
 */
@Service("AgentCashInConfirmHandlerImpl")
public class AgentCashInConfirmHandlerImpl extends FIXMessageHandler implements AgentCashInConfirmHandler {

	private static Logger log = LoggerFactory.getLogger(AgentCashInConfirmHandlerImpl.class);
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;

	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
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
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	public Result handle(TransactionDetails transactionDetails)  {
		log.info("Extracting data from transactionDetails in AgentCashInConfirmHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());

		ChannelCode cc = transactionDetails.getCc();
		CMAgentCashInConfirm agentcashinconfirm = new CMAgentCashInConfirm();
		boolean confirmed = false;
		confirmed = Boolean.parseBoolean(transactionDetails.getConfirmString());
		String destMDN = subscriberService.normalizeMDN(transactionDetails.getDestMDN());
		
		agentcashinconfirm.setSourceMDN(transactionDetails.getSourceMDN());
		agentcashinconfirm.setDestMDN(destMDN);
		agentcashinconfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		agentcashinconfirm.setTransferID(transactionDetails.getTransferId());
		agentcashinconfirm.setParentTransactionID(transactionDetails.getParentTxnId());
		agentcashinconfirm.setConfirmed(confirmed);
		agentcashinconfirm.setSourceApplication(cc.getChannelSourceApplication());
		agentcashinconfirm.setChannelCode(cc.getChannelCode());
		agentcashinconfirm.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		log.info("Handling Agent cashin confirm webapi request::From " + agentcashinconfirm.getSourceMDN() + " To " + 
				agentcashinconfirm.getDestMDN());
		XMLResult result = new WalletConfirmXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AgentCashInConfirm,agentcashinconfirm.DumpFields(),agentcashinconfirm.getParentTransactionID());
		agentcashinconfirm.setTransactionID(transactionsLog.getID());

		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(agentcashinconfirm);
		result.setTransactionID(transactionsLog.getID());
		
		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(agentcashinconfirm.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateAgentMDN(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Agent with mdn : "+agentcashinconfirm.getSourceMDN()+" has failed validations");
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		Partner agent = partnerService.getPartner(srcSubscriberMDN);
		
		SubscriberMDN destinationMDN = subscriberMdnService.getByMDN(agentcashinconfirm.getDestMDN());
		validationResult = transactionApiValidationService.validateSubscriberAsDestination(destinationMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Destination subscriber with mdn : "+agentcashinconfirm.getDestMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}


		Pocket destSubscriberPocket = pocketService.getDefaultPocket(destinationMDN, null);
		validationResult = transactionApiValidationService.validateDestinationPocket(destSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with pocket id : "+(destSubscriberPocket!=null? destSubscriberPocket.getID():null)+" of the subscriber "+destinationMDN.getMDN()+
					"has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		CMCashIn cashin = new CMCashIn();
		cashin.setSourceMDN(agentcashinconfirm.getSourceMDN());
		cashin.setDestMDN(agentcashinconfirm.getDestMDN());
		cashin.setParentTransactionID(agentcashinconfirm.getParentTransactionID());
		cashin.setTransferID(agentcashinconfirm.getTransferID());
		cashin.setConfirmed(agentcashinconfirm.getConfirmed());
		cashin.setChannelCode(cc.getChannelCode());
		cashin.setDestPocketID(destSubscriberPocket.getID());
		cashin.setSourceApplication(cc.getChannelSourceApplication());
		cashin.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashin.setTransactionIdentifier(agentcashinconfirm.getTransactionIdentifier());

		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(agentcashinconfirm.getParentTransactionID(),agentcashinconfirm.getTransactionIdentifier());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				log.error("The status of Sctl with id: "+sctl.getID()+"has been changed from Inquiry to: "+sctl.getStatus());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}

		} else {
			log.error("Could not find sctl with parentTransaction ID: "+agentcashinconfirm.getParentTransactionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}
		
		Pocket srcAgentPocket;
		PartnerServices partnerService = transactionChargingService.getPartnerService(agent.getID(), sctl.getServiceProviderID(), sctl.getServiceID());
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
		cashin.setServiceChargeTransactionLogID(sctl.getID());
		cashin.setSourcePocketID(srcAgentPocket.getID());
		log.info("sending agentcashinconfirm request to backend for processing");
		CFIXMsg response = super.process(cashin);
		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		result.setMultixResponse(response);
		
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult() && sctl!=null) {
				transactionChargingService.confirmTheTransaction(sctl, agentcashinconfirm.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, agentcashinconfirm.getTransferID());
				result.setDebitAmount(sctl.getTransactionAmount());
				result.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
				result.setServiceCharge(sctl.getCalculatedCharge());
				transactionApiValidationService.checkAndChangeStatus(destinationMDN);
			} else {
				String errorMsg = transactionResponse.getMessage();
				// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				if(sctl!=null){
					transactionChargingService.failTheTransaction(sctl, errorMsg);
				}
			}			
		}
		result.setSctlID(sctl.getID());
		result.setMessage(transactionResponse.getMessage());
		return result;
	}
}