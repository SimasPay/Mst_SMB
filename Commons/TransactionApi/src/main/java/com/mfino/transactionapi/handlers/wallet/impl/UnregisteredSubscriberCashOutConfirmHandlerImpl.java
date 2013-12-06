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
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashOutForNonRegistered;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.PartnerService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.UnregisteredSubscriberCashOutConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Maruthi
 * 
 */
@Service("UnregisteredSubscriberCashOutConfirmHandlerImpl")
public class UnregisteredSubscriberCashOutConfirmHandlerImpl extends FIXMessageHandler implements UnregisteredSubscriberCashOutConfirmHandler{

	private static Logger log	= LoggerFactory.getLogger(UnregisteredSubscriberCashOutConfirmHandlerImpl.class);
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in UnregisteredSubscriberCashOutConfirmHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		CMCashOutForNonRegistered unregisteredsubscribercashoutconfirm = new CMCashOutForNonRegistered();
		ChannelCode cc = transactionDetails.getCc();
		boolean confirmed = false;
		confirmed = Boolean.parseBoolean(transactionDetails.getConfirmString());
		unregisteredsubscribercashoutconfirm.setSourceMDN(transactionDetails.getDestMDN());
		unregisteredsubscribercashoutconfirm.setDestMDN(transactionDetails.getSourceMDN());
		unregisteredsubscribercashoutconfirm.setParentTransactionID(transactionDetails.getParentTxnId());
		unregisteredsubscribercashoutconfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		unregisteredsubscribercashoutconfirm.setTransferID(transactionDetails.getTransferId());
		unregisteredsubscribercashoutconfirm.setConfirmed(confirmed);
		unregisteredsubscribercashoutconfirm.setSourceApplication(cc.getChannelSourceApplication());
		unregisteredsubscribercashoutconfirm.setChannelCode(cc.getChannelCode());
		unregisteredsubscribercashoutconfirm.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

		log.info("Handling Unregistered Subscriber Cashout confirmation WebAPI request");
		XMLResult result = new MoneyTransferXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_CashOutForNonRegistered, unregisteredsubscribercashoutconfirm.DumpFields(),unregisteredsubscribercashoutconfirm.getParentTransactionID());
		unregisteredsubscribercashoutconfirm.setTransactionID(transactionsLog.getID());

		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(unregisteredsubscribercashoutconfirm);
		result.setTransactionID(unregisteredsubscribercashoutconfirm.getTransactionID());
	
		//Agent Validation
		SubscriberMDN destAgentMDN = subscriberMdnService.getByMDN(unregisteredsubscribercashoutconfirm.getDestMDN());

		Integer validationResult = transactionApiValidationService.validateAgentMDN(destAgentMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Agent with mdn : "+unregisteredsubscribercashoutconfirm.getSourceMDN()+" has failed validations");
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		Partner destAgent = partnerService.getPartner(destAgentMDN);
		unregisteredsubscribercashoutconfirm.setPartnerCode(destAgent.getPartnerCode());
		
		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(unregisteredsubscribercashoutconfirm.getSourceMDN());
		validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!(CmFinoFIX.ResponseCode_Success.equals(validationResult)||
				CmFinoFIX.NotificationCode_SubscriberNotRegistered.equals(validationResult))){
			log.error("Source subscriber with mdn : "+unregisteredsubscribercashoutconfirm.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket srcSubscriberPocket = subscriberService.getDefaultPocket(srcSubscriberMDN.getID(),systemParametersService.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED));		
		validationResult = transactionApiValidationService.validateSourcePocketForUnregistered(srcSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(srcSubscriberPocket);
		result.setPocketList(pocketList);
				
		
		unregisteredsubscribercashoutconfirm.setPartnerCode(destAgent.getPartnerCode());
		unregisteredsubscribercashoutconfirm.setSourcePocketID(srcSubscriberPocket.getID());
	
		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(unregisteredsubscribercashoutconfirm.getParentTransactionID(),unregisteredsubscribercashoutconfirm.getTransactionIdentifier());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				log.error("The status of Sctl with id: "+sctl.getID()+"has been changed from Inquiry to: "+sctl.getStatus());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}

		} else {
			log.error("Could not find sctl with parentTransaction ID: "+unregisteredsubscribercashoutconfirm.getParentTransactionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}		
		
		Pocket destAgentPocket;
		PartnerServices partnerService = transactionChargingService.getPartnerService(destAgent.getID(), sctl.getServiceProviderID(), sctl.getServiceID());
		if (partnerService == null) {
			log.error("Partner service NULL");
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
		unregisteredsubscribercashoutconfirm.setServiceChargeTransactionLogID(sctl.getID());
		unregisteredsubscribercashoutconfirm.setDestPocketID(destAgentPocket.getID());
		unregisteredsubscribercashoutconfirm.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
		
		log.info("sending the unregisteredSubscriberCashOutInquiry request to backend for processing");
		CFIXMsg response = super.process(unregisteredsubscribercashoutconfirm);
		result.setMultixResponse(response);

		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {				
				transactionChargingService.confirmTheTransaction(sctl, unregisteredsubscribercashoutconfirm.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, unregisteredsubscribercashoutconfirm.getTransferID());
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
		result.setMessage(transactionResponse.getMessage());
		return result;
	}
}