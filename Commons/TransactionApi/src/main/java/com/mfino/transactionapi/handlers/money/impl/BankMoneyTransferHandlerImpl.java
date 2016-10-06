/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.MFAService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.BankMoneyTransferHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Deva
 * 
 */
@Service("BankMoneyTransferHandlerImpl")
public class BankMoneyTransferHandlerImpl extends FIXMessageHandler implements BankMoneyTransferHandler{
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	private static Logger log = LoggerFactory.getLogger(BankMoneyTransferHandlerImpl.class);
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;

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
	
	public Result handle(TransactionDetails transactionDetails) {
		
		CMBankAccountToBankAccountConfirmation	transferConfirmation =new CMBankAccountToBankAccountConfirmation();

		String transactionOtp   = transactionDetails.getTransactionOTP();
 		String serviceName 		= transactionDetails.getServiceName();
 		String SourcePocketCode = transactionDetails.getSourcePocketCode();
 		//String DestPocketCode   = transactionDetails.getDestPocketCode();
 		Long channelCodeId      = transactionDetails.getCc().getId().longValue();

		ChannelCode cc= transactionDetails.getCc();
		transferConfirmation.setSourceMDN(transactionDetails.getSourceMDN());
		transferConfirmation.setDestMDN(subscriberService.normalizeMDN(transactionDetails.getDestMDN()));
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setTransferID(transactionDetails.getTransferId());
		transferConfirmation.setConfirmed(Boolean.parseBoolean(transactionDetails.getConfirmString()));
		transferConfirmation.setSourceApplication((int)cc.getChannelsourceapplication());
		transferConfirmation.setChannelCode(cc.getChannelcode());
		transferConfirmation.setParentTransactionID(transactionDetails.getParentTxnId());
		transferConfirmation.setDestinationBankAccountNo(transactionDetails.getDestinationBankAccountNo());
		transferConfirmation.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

		
		log.info("Handling Bank Account transfer confirmation WebAPI request");
		
		XMLResult result = new MoneyTransferXMLResult();
		
		ServiceChargeTxnLog sctlForMFA = sctlService.getByTransactionLogId(transferConfirmation.getParentTransactionID());

		if(mfaService.isMFATransaction(serviceName, ServiceAndTransactionConstants.TRANSACTION_TRANSFER, channelCodeId)){
			if(transactionOtp == null || !(mfaService.isValidOTP(transactionOtp, sctlForMFA.getId().longValue(), transferConfirmation.getSourceMDN()))){
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidMFAOTP);
				return result;
			}
		}

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation,transferConfirmation.DumpFields(), transferConfirmation.getParentTransactionID());
		transferConfirmation.setTransactionID(transactionsLog.getId().longValue());

		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(transferConfirmation);
		result.setTransactionID(transactionsLog.getId().longValue());
		//validating source mdn
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(transferConfirmation.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+sourceMDN.getMdn()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		//validation of destination mdn
		SubscriberMdn destinationMDN = subscriberMdnService.getByMDN(transferConfirmation.getDestMDN());
		validationResult = transactionApiValidationService.validateSubscriberAsDestination(destinationMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Destination subscriber with mdn : "+destinationMDN.getMdn()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		
//		addCompanyANDLanguageToResult(sourceMDN, result);
		
		Pocket srcPocket = pocketService.getDefaultPocket(sourceMDN, SourcePocketCode);
		//Validation of both source pocket and destination pocket
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		

		/*if(StringUtils.isBlank(DestPocketCode)){
			
			if(destinationMDN.getSubscriber().getType().equals(CmFinoFIX.SubscriberType_Subscriber)) {
				
				DestPocketCode = (String.valueOf(CmFinoFIX.PocketType_LakuPandai));
				
			} else if(destinationMDN.getSubscriber().getType().equals(CmFinoFIX.SubscriberType_Partner)) {
				
				DestPocketCode = (String.valueOf(CmFinoFIX.PocketType_SVA));
				
			}
		}*/
		
		Pocket destPocket = null;
		
		if(StringUtils.isNotBlank(transactionDetails.getDestPocketCode())) {
			
			destPocket = pocketService.getDefaultPocket(destinationMDN, transactionDetails.getDestPocketCode());
		}
		else {
			
			/*if(destinationMDN.getSubscriber().getType().equals(CmFinoFIX.SubscriberType_Subscriber)) {
				
				transactionDetails.setDestPocketCode(String.valueOf(CmFinoFIX.PocketType_LakuPandai));
				
			} else if(destinationMDN.getSubscriber().getType().equals(CmFinoFIX.SubscriberType_Partner)) {
				
				transactionDetails.setDestPocketCode(String.valueOf(CmFinoFIX.PocketType_SVA));
				
			}*/
			
			destPocket = pocketService.getDefaultPocket(destinationMDN, String.valueOf(CmFinoFIX.PocketType_SVA));
			
			if(null != destPocket) {
				
				transactionDetails.setDestPocketCode(String.valueOf(CmFinoFIX.PocketType_SVA));
				
			} else {
				
				destPocket = pocketService.getDefaultPocket(destinationMDN, String.valueOf(CmFinoFIX.PocketType_LakuPandai));
				transactionDetails.setDestPocketCode(String.valueOf(CmFinoFIX.PocketType_LakuPandai));
			}
		}
		
		validationResult = transactionApiValidationService.validateSourcePocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		
		transferConfirmation.setSourcePocketID(srcPocket.getId().longValue());
		transferConfirmation.setDestPocketID(destPocket.getId().longValue());

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 

		ServiceChargeTxnLog sctl = transactionChargingService.getServiceChargeTransactionLog(transferConfirmation.getParentTransactionID(),transferConfirmation.getTransactionIdentifier());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}

		} else {
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
 			return result;
		}		
		
		transferConfirmation.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		CFIXMsg response = super.process(transferConfirmation);
		
		
		result.setMultixResponse(response);
		result.setSctlID(sctl.getId().longValue());

		// Changing the Service_charge_transaction_log status based on the response from Core engine. 
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult() && sctl!=null) {
				transactionChargingService.confirmTheTransaction(sctl, transferConfirmation.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, transferConfirmation.getTransferID());
				result.setDebitAmount(sctl.getTransactionamount());
				result.setCreditAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
				result.setServiceCharge(sctl.getCalculatedcharge());

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

		//		sendSMS(transferConfirmation, result);
		result.setMessage(transactionResponse.getMessage());
		return result;
	}

}
