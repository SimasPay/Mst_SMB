/**
 * 
 */
package com.mfino.transactionapi.handlers.mobileshopping.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPurchase;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.PartnerService;
import com.mfino.service.PendingCommodityTransferService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.mobileshopping.PurchaseConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Maruthi
 * 
 */
@Service("PurchaseConfirmHandlerImpl")
public class PurchaseConfirmHandlerImpl extends FIXMessageHandler implements PurchaseConfirmHandler{

	private static Logger log	= LoggerFactory.getLogger(PurchaseConfirmHandlerImpl.class);
	
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
	
	@Autowired
	@Qualifier("PendingCommodityTransferServiceImpl")
	private PendingCommodityTransferService pendingCommodityTransferService;
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in PurchaseConfirmHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		CMPurchase purchase = new CMPurchase();
		ChannelCode cc = transactionDetails.getCc();
		boolean confirmed = false;
		confirmed = Boolean.parseBoolean(transactionDetails.getConfirmString());
		purchase.setSourceMDN(transactionDetails.getSourceMDN());
		purchase.setPartnerCode(transactionDetails.getPartnerCode());
		purchase.setParentTransactionID(transactionDetails.getParentTxnId());
		purchase.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		purchase.setTransferID(transactionDetails.getTransferId());
		purchase.setConfirmed(confirmed);
		purchase.setSourceApplication((int)cc.getChannelsourceapplication());
		purchase.setChannelCode(cc.getChannelcode());
		purchase.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		log.info("Handling Subscriber Purchase confirmation WebAPI request");
		XMLResult result = new MoneyTransferXMLResult();

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_Purchase,purchase.DumpFields(),purchase.getParentTransactionID());
		purchase.setTransactionID(transactionsLog.getId().longValue());

		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(purchase);
		result.setTransactionID(purchase.getTransactionID());

		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(purchase.getSourceMDN());
		
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+purchase.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		//Pocket subPocket = getDefaultPocket(smdn, null); this line of code is commented as the source pocket can be eMoney or bank
		// The below 6 lines of code is added as we are not asking the subscriber to enter the srcPocketCode code when doing confirmation as per the current webapi. 
		// So, we are retrieving his source pocket id using pending commdity transferid that is generated during the Inquiry. For More Information refer Red mine ticket 1053

		PendingCommodityTransfer pct = pendingCommodityTransferService.getById(purchase.getTransferID());
		Pocket srcSubscriberPocket = null;
		if(pct != null){
			srcSubscriberPocket = pct.getPocket();
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

		Partner destMerchant = partnerService.getPartnerByPartnerCode(purchase.getPartnerCode());
		validationResult = transactionApiValidationService.validateMerchantByPartnerType(destMerchant);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination Agent has failed validations");
			result.setPartnerCode(purchase.getPartnerCode());
			validationResult = processValidationResultForDestinationAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		SubscriberMdn destMerchantMDN = destMerchant.getSubscriber().getSubscriberMdns().iterator().next();

		purchase.setDestMDN(destMerchantMDN.getMdn());
		purchase.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		purchase.setSourceApplication((int)cc.getChannelsourceapplication());

		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTxnLog sctl = transactionChargingService.getServiceChargeTransactionLog(purchase.getParentTransactionID(),purchase.getTransactionIdentifier());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				log.error("The status of Sctl with id: "+sctl.getId()+"has been changed from Inquiry to: "+sctl.getStatus());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}

		} else {
			log.error("Could not find sctl with parentTransaction ID: "+purchase.getParentTransactionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}		

		Pocket destMerchantPocket;
		PartnerServices partnerServices = transactionChargingService.getPartnerService(destMerchant.getId().longValue(), sctl.getServiceproviderid().longValue(), sctl.getServiceid().longValue());
		if (partnerServices == null) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
			return result;
		}
		destMerchantPocket = partnerServices.getPocketByDestpocketid();
		validationResult = transactionApiValidationService.validateDestinationPocket(destMerchantPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destMerchantPocket!=null? destMerchantPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		purchase.setDestPocketID(destMerchantPocket.getId().longValue());
		purchase.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		log.info("sending the purchase request to backend for processing");
		CFIXMsg response = super.process(purchase);
		result.setMultixResponse(response);
		commodityTransferService.addCommodityTransferToResult(result);
		result.setSctlID(sctl.getId().longValue());

		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {
				transactionChargingService.confirmTheTransaction(sctl, purchase.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, purchase.getTransferID());
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
		result.setMessage(transactionResponse.getMessage());
		return result;
	}
}