/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
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
import com.mfino.fix.CmFinoFIX.CMBulkDistribution;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.BulkDistributionHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;



/**
 * @author Bala sunku
 *
 */
@Service("BulkDistributionHandlerImpl")
public class BulkDistributionHandlerImpl extends FIXMessageHandler implements BulkDistributionHandler{

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;	
	
	public Result handle(TransactionDetails transactionDetails) {
		
		ChannelCode channelCode = transactionDetails.getCc();

		BigDecimal amount = transactionDetails.getAmount();
		log.info("Handling Bulk distribution request::From " + transactionDetails.getSourceMDN() + " To " + 
				transactionDetails.getDestMDN() + " For Amount = " + transactionDetails.getAmount());
		Transaction transaction = null;
		XMLResult result = new TransferInquiryXMLResult();
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		result.setUnRegistered(false);
		
		CMBulkDistribution bulkDistribution = new CMBulkDistribution();
		bulkDistribution.setDestMDN(transactionDetails.getDestMDN());
		bulkDistribution.setAmount(amount);
		bulkDistribution.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		bulkDistribution.setSourceMDN(transactionDetails.getSourceMDN());
		bulkDistribution.setSourceMessage(transactionDetails.getSourceMessage());
		bulkDistribution.setSourceApplication((int)channelCode.getChannelsourceapplication());
		bulkDistribution.setChannelCode(channelCode.getChannelcode());
		bulkDistribution.setServiceName(transactionDetails.getServiceName());
		bulkDistribution.setUICategory(CmFinoFIX.TransactionUICategory_Sub_Bulk_Transfer);
		bulkDistribution.setSourcePocketID(transactionDetails.getSrcPocketId());
		
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BulkDistribution, 
				bulkDistribution.DumpFields());
		
		bulkDistribution.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());
		
		result.setDestinationMDN(transactionDetails.getDestMDN());
		result.setSourceMessage(bulkDistribution);
		
		SubscriberMdn destinationMDN = subscriberMdnService.getByMDN(transactionDetails.getDestMDN());
		Integer validationResult= transactionApiValidationService.validateSubscriberAsDestination(destinationMDN); 

		Pocket destPocket = null;
		if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
			log.info("Dest MDN failed validation. So transfering to National Treasury pocket");
			long ntpocketId = systemParametersService.getLong(SystemParameterKeys.NATIONAL_TREASURY_POCKET);
			destPocket = pocketService.getById(ntpocketId);
			bulkDistribution.setDestMDN(destPocket.getSubscriberMdn().getMdn());
			result.setTrfToSuspense(true);
		}
		else {
			String destPocketCode = null;
			if (CmFinoFIX.SubscriberType_Partner.equals(destinationMDN.getSubscriber().getType())) {
				destPocketCode = CmFinoFIX.PocketType_SVA + "";
			}
			else {
				destPocketCode = CmFinoFIX.PocketType_LakuPandai + "";
			}
			destPocket = pocketService.getDefaultPocket(destinationMDN, destPocketCode);
			result.setFirstName(destinationMDN.getSubscriber().getFirstname());
			result.setLastName(destinationMDN.getSubscriber().getLastname());
		}
		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		bulkDistribution.setDestPocketID(destPocket.getId().longValue());	
		
		log.info("creating the serviceCharge object....");
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(transactionDetails.getSourceMDN());
		sc.setDestMDN(transactionDetails.getDestMDN());
		sc.setChannelCodeId(transactionDetails.getCc().getId().longValue());
		sc.setServiceName(transactionDetails.getServiceName());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER);
		sc.setTransactionAmount(transactionDetails.getAmount());
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		sc.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		sc.setDescription(transactionDetails.getDescription());
		
		try{
			transaction =transactionChargingService.getCharge(sc);
			bulkDistribution.setAmount(transaction.getAmountToCredit());
			bulkDistribution.setCharges(transaction.getAmountTowardsCharges());

		} catch (InvalidServiceException ise) {
			log.error("Exception occured in getting charges",ise);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);//change to service not found
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		bulkDistribution.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		log.info("sending the request to backend for processing");
		CFIXMsg response = super.process(bulkDistribution);
		
		TransactionResponse transferResponse = checkBackEndResponse(response);
		log.info("Transfer Response = "+transferResponse.getMessage());
		if(transferResponse.getTransactionId()!=null){
			sctl.setTransactionid(BigDecimal.valueOf(transferResponse.getTransactionId()));
		}
		if(transferResponse.getTransferId()!=null){
			sctl.setCommoditytransferid(BigDecimal.valueOf(transferResponse.getTransferId()));
		}
		// Success = 0, Failure = 1, Pending = 2
		result.setTxnStatus(2);
		if (!("Your request is queued. Please check after sometime.".equals(transferResponse.getMessage()))) {
			if (transferResponse.isResult()) {
				result.setTxnStatus(0);
				transactionChargingService.confirmTheTransaction(sctl);
				commodityTransferService.addCommodityTransferToResult(result, transferResponse.getTransferId());
			}else{
				result.setTxnStatus(1);
				String errorMsg = transferResponse.getMessage();
				// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				transactionChargingService.failTheTransaction(sctl, errorMsg);
			}
		}

		result.setMessage(transferResponse.getMessage());
		result.setCode(transferResponse.getCode());
		result.setSctlID(sctl.getId().longValue());
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		return result;
	}
}
