package com.mfino.transactionapi.handlers.interswitch.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAutoReversal;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashin;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.impl.TransactionIdentifierServiceImpl;
import com.mfino.service.impl.TransactionLogServiceImpl;
import com.mfino.transactionapi.handlers.interswitch.CashinReversalHandler;
import com.mfino.transactionapi.result.xmlresulttypes.wallet.WalletConfirmXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;

@Service("CashinReversalHandlerImpl")
public class CashinReversalHandlerImpl extends FIXMessageHandler implements CashinReversalHandler{

	private static Logger	log	= LoggerFactory.getLogger(CashinReversalHandlerImpl.class);

 	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	 
   	public Result handle(CMInterswitchCashin cashinDetails, ChannelCode cc,String transactionIdentifier) {

		log.info("handling cashin reversal request");

		log.info("creating transaction log ...");
	
		TransactionsLog tLog  = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AutoReversal, cashinDetails.DumpFields());

 		cashinDetails.setTransactionID(tLog.getID());
 
 		WalletConfirmXMLResult result = new WalletConfirmXMLResult();
		result.setTransactionTime(tLog.getTransactionTime());
		result.setSourceMessage(cashinDetails);
		result.setTransactionID(tLog.getID());

		log.info("validating dest mdn");

		SubscriberMDN destinationMDN = subscriberMdnService.getByMDN(cashinDetails.getDestMDN());
		SubscriberMDN subscriberMDN = subscriberMdnService.getByMDN(cashinDetails.getSourceMDN());

		/*Destination MDN validation */

		Integer validationResult = transactionApiValidationService.validateSubscriberAsDestination(destinationMDN);
 		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Destination subscriber with mdn : "+destinationMDN.getMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		log.info("Getting the emoney-pocket of DestinatioMDN");
		
		Pocket subPocket = pocketService.getDefaultPocket(destinationMDN, "1");
		validationResult = transactionApiValidationService.validateSourcePocket(subPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(subPocket!=null? subPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		

		log.info("default emoney pocket for destmdn=" + destinationMDN + " is " + subPocket.getID());

		log.info("retrieving sctl from paymentLogID");

	 	List<ServiceChargeTransactionLog> sctlList = sctlService.getBySCTLIntegrationTxnID(cashinDetails.getPaymentLogID(),cashinDetails.getCustReference());
	
		ServiceChargeTransactionLog sctl = null;
		if(!sctlList.isEmpty())	{
			sctl = sctlList.get(0); // Only one match would be there as we do not allow duplicate entry
		}
		
		if (sctl == null) {
			log.warn("cashin reversal attempted for a non-exsistant paymentlogid=" + cashinDetails.getPaymentLogID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidPaymentLogID);
 			return result;
		}
		else {
			if (sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Reverse_Success)) {
				log.info("previous reversal for this paymentlogid=" + cashinDetails.getPaymentLogID() + " was successful.So returning successful");
				result.setNotificationCode(CmFinoFIX.NotificationCode_AutoReverseSuccessToSource);
				result.setCode(CmFinoFIX.NotificationCode_AutoReverseSuccessToSource.toString());

				return result;
			}
			else if (!sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Confirmed)) {
				log.info("previous reversal for this paymentlogid=" + cashinDetails.getPaymentLogID() + " was failure.So returning status");
				result.setNotificationCode(CmFinoFIX.NotificationCode_AutoReverseFailed);
				result.setCode(CmFinoFIX.NotificationCode_AutoReverseFailed.toString());

				return result;
			}
		}
		
		//save the transactionidentifier along with sctl to db
		TransactionIdentifierServiceImpl transactionIdentifierService= new TransactionIdentifierServiceImpl();
		transactionIdentifierService.createTrxnIdentifierDbEntry(cashinDetails.getTransactionIdentifier(), sctl.getID());
		
		log.info("retrieving ct from sctl");
		 
		CommodityTransfer ct = sctlService.getCTfromSCTL(sctl);

		log.info("constructing autoreversal object");
		CMAutoReversal reversal = new CMAutoReversal();
		reversal.setSourcePocketID(ct.getPocketBySourcePocketID().getID());
		reversal.setDestPocketID(subPocket.getID());
		reversal.setServiceChargeTransactionLogID(sctl.getID());
		reversal.setAmount(ct.getAmount());
		reversal.setCharges(ct.getCharges());
		reversal.setTransactionIdentifier(cashinDetails.getTransactionIdentifier());

		log.info("sending autoreversal to backend -->");

		sctlService.updateSCTLStatus(CmFinoFIX.SCTLStatus_Reverse_Initiated,sctl);
 
		CFIXMsg cfixResponse = super.process(reversal);
		TransactionResponse tresponse = checkBackEndResponse(cfixResponse);

		log.info("received backend reponse");
		log.info("the notificationcode returned by backend-->" + tresponse.getCode());

		cashinDetails.setTransactionID(tresponse.getTransactionId());
		
		result.setTransactionID(tresponse.getTransactionId());
		result.setParentTransactionID(tresponse.getTransactionId());
		result.setMultixResponse(cfixResponse);

		addCompanyANDLanguageToResult(subscriberMDN,result);
		
		result.setTransferID(tresponse.getTransferId());
		result.setCode(tresponse.getCode());
		result.setMessage(tresponse.getMessage());

		if (CmFinoFIX.NotificationCode_AutoReverseSuccessToSource.toString().equals(tresponse.getCode()))
			sctlService.updateSCTLStatus(CmFinoFIX.SCTLStatus_Reverse_Success,sctl);
 		else
			sctlService.updateSCTLStatus(CmFinoFIX.SCTLStatus_Reverse_Failed,sctl);
 
		return result;

	}
}
