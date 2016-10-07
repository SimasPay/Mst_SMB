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

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashOut;
import com.mfino.fix.CmFinoFIX.CMSubscriberCashOutConfirm;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.MFAService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.SubscriberCashOutConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Maruthi
 * 
 */
@Service("SubscriberCashOutConfirmHandlerImpl")
public class SubscriberCashOutConfirmHandlerImpl extends FIXMessageHandler implements SubscriberCashOutConfirmHandler{

	private static Logger log	= LoggerFactory.getLogger(SubscriberCashOutConfirmHandlerImpl.class);
	
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
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in SubscriberCashOutConfirmHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()+"to"+transactionDetails.getDestMDN());
		
		transactionDetails.setSourcePocketCode(String.valueOf(CmFinoFIX.PocketType_LakuPandai));
		ChannelCode cc = transactionDetails.getCc();
		boolean confirmed = false;
		confirmed = Boolean.parseBoolean(transactionDetails.getConfirmString());
		String mfaOneTimeOTP = transactionDetails.getTransactionOTP();
		
		CMSubscriberCashOutConfirm subscribercashoutconfirm = new CMSubscriberCashOutConfirm();
		
		subscribercashoutconfirm.setSourceMDN(transactionDetails.getSourceMDN());
		subscribercashoutconfirm.setDestMDN(transactionDetails.getDestMDN());
		subscribercashoutconfirm.setPartnerCode(transactionDetails.getPartnerCode());
		subscribercashoutconfirm.setParentTransactionID(transactionDetails.getParentTxnId());
		subscribercashoutconfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		subscribercashoutconfirm.setTransferID(transactionDetails.getTransferId());
		subscribercashoutconfirm.setConfirmed(confirmed);
		subscribercashoutconfirm.setSourceApplication((int)cc.getChannelsourceapplication());
		subscribercashoutconfirm.setChannelCode(cc.getChannelcode());
		subscribercashoutconfirm.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		log.info("Handling Subscriber Cashout confirmation WebAPI request for parent trxnID:"+transactionDetails.getParentTxnId());
		XMLResult result = new MoneyTransferXMLResult();

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberCashOutConfirm,subscribercashoutconfirm.DumpFields(),subscribercashoutconfirm.getParentTransactionID());
		subscribercashoutconfirm.setTransactionID(transactionsLog.getId().longValue());

		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(subscribercashoutconfirm);
		result.setTransactionID(subscribercashoutconfirm.getTransactionID());

		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(subscribercashoutconfirm.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+subscribercashoutconfirm.getSourceMDN()+" has failed validations");
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

		//Partner destAgent = partnerService.getPartnerByPartnerCode(subscribercashoutconfirm.getPartnerCode());
		Partner destAgent = partnerService.getPartner(subscribercashoutconfirm.getDestMDN());
		validationResult = transactionApiValidationService.validateAgentByPartnerType(destAgent);
		if(validationResult.equals(CmFinoFIX.NotificationCode_DestinationAgentNotFound)){
			log.info("Destination failed agent validations.validating if the destination is teller");
			validationResult= transactionApiValidationService.validateTellerByPartnerType(destAgent);
		}
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			//result.setPartnerCode(subscribercashoutconfirm.getPartnerCode());
			result.setDestinationMDN(subscribercashoutconfirm.getDestMDN());
			validationResult = processValidationResultForDestinationAgent(validationResult);
			result.setNotificationCode(validationResult);
			return result;
		}
		SubscriberMdn destAgentMDN = destAgent.getSubscriber().getSubscriberMdns().iterator().next();

		CMCashOut cashout = new CMCashOut();
		cashout.setSourceMDN(subscribercashoutconfirm.getSourceMDN());
		cashout.setPartnerCode(subscribercashoutconfirm.getPartnerCode());
		cashout.setDestMDN(destAgentMDN.getMdn());
		cashout.setParentTransactionID(subscribercashoutconfirm.getParentTransactionID());
		cashout.setTransferID(subscribercashoutconfirm.getTransferID());
		cashout.setConfirmed(subscribercashoutconfirm.getConfirmed());
		cashout.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		cashout.setSourceApplication((int)cc.getChannelsourceapplication());
		cashout.setServletPath(subscribercashoutconfirm.getServletPath());
		cashout.setTransactionIdentifier(subscribercashoutconfirm.getTransactionIdentifier());
		if(destAgent.getBusinesspartnertype().equals(CmFinoFIX.BusinessPartnerType_BranchOffice)){
			cashout.setUICategory(CmFinoFIX.TransactionUICategory_Teller_Cashout);
		}
		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTxnLog sctl = transactionChargingService.getServiceChargeTransactionLog(subscribercashoutconfirm.getParentTransactionID(),subscribercashoutconfirm.getTransactionIdentifier());
		if(sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				log.error("The status of Sctl with id: "+sctl.getId()+"has been changed from Inquiry to: "+sctl.getStatus());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}
		} else {
			log.error("Could not find sctl with parentTransaction ID: "+subscribercashoutconfirm.getParentTransactionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);			
			return result;
		}
		
		
		//2FA
		if(mfaService.isMFATransaction(ServiceAndTransactionConstants.SERVICE_WALLET, ServiceAndTransactionConstants.TRANSACTION_CASHOUT, cc.getId().longValue())){
			if(mfaOneTimeOTP == null || !(mfaService.isValidOTP(mfaOneTimeOTP,sctl.getId().longValue(), srcSubscriberMDN.getMdn()))){
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidMFAOTP);
				return result;
			}
		}
		
		Pocket destAgentPocket;
		PartnerServices partnerService = transactionChargingService.getPartnerService(destAgent.getId().longValue(), sctl.getServiceid().longValue(), sctl.getServiceid().longValue());
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
		cashout.setServiceChargeTransactionLogID(sctl.getId().longValue());
		cashout.setDestPocketID(destAgentPocket.getId().longValue());
		
		log.info("sending the cashout request to backend for processing");
		CFIXMsg response = super.process(cashout);
		result.setMultixResponse(response);
		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {
				if(destAgent.getBusinesspartnertype().equals(CmFinoFIX.BusinessPartnerType_BranchOffice)){
					transactionChargingService.addTransferID(sctl, subscribercashoutconfirm.getTransferID());
				}else{
				transactionChargingService.confirmTheTransaction(sctl, subscribercashoutconfirm.getTransferID());
				}
				commodityTransferService.addCommodityTransferToResult(result, subscribercashoutconfirm.getTransferID());
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
		return result;
	}
}