package com.mfino.transactionapi.handlers.interswitch.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashIn;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashin;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.interswitch.IntegrationCashinConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.wallet.WalletConfirmXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
/**
 * This the Interswitch cashIn Confirm handler that will called from transaction aware handlers presently.Will handle the confirm part of the transaction
 * The way to use this class is to use either the handle method or use the preprocess,communicate and postprocess methods in the same order
 * @author Sreenath
 *
 */
@Service("IntegrationCashinConfirmHandlerImpl")
public class IntegrationCashinConfirmHandlerImpl extends FIXMessageHandler implements IntegrationCashinConfirmHandler{
	
	private static Logger	log	= LoggerFactory.getLogger(IntegrationCashinConfirmHandlerImpl.class);

 	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

 	@Autowired
 	@Qualifier("TransactionChargingServiceImpl")
 	private TransactionChargingService transactionChargingService;
 	
 	@Autowired
 	@Qualifier("CommodityTransferServiceImpl")
 	private CommodityTransferService commodityTransferService;
 	
 	@Autowired
 	@Qualifier("TransactionLogServiceImpl")
 	private TransactionLogService transactionLogService;


	
	/**
	 * To be called after communicate only
	 * @param response
	 * @param result
	 * @return
	 */
 	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public WalletConfirmXMLResult postprocess(TransactionDataContainerImpl transactionDetailsContainer, ChannelCode channel, CFIXMsg response,WalletConfirmXMLResult result) {
		if(transactionDetailsContainer==null || channel==null || response==null || result==null){
			log.error("Input data is null.cashinDataConatiner:"+transactionDetailsContainer+"channel:"+channel+"response:"+response+"result:"+result);
			throw new IllegalArgumentException();
		}
		result.setMultixResponse(response);
		Long transferId = transactionDetailsContainer.getTransferID();
		SubscriberMDN destMDN = transactionDetailsContainer.getDestinationMDN();
		// Changing the Service_charge_transaction_log status based on the
		// response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		
		ServiceChargeTransactionLog sctl = transactionDetailsContainer.getSctl();
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {

			if (transactionResponse.isResult() && sctl != null) {

				transactionChargingService.confirmTheTransaction(sctl, transferId);

				commodityTransferService.addCommodityTransferToResult(result, transferId);
				result.setDebitAmount(sctl.getTransactionAmount());
				result.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
				result.setServiceCharge(sctl.getCalculatedCharge());

				transactionApiValidationService.checkAndChangeStatus(destMDN);
			}
			else {
				String errorMsg = transactionResponse.getMessage();
				// As the length of the Failure reason column is 255, we are
				// trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				if (sctl != null) {
					transactionChargingService.failTheTransaction(sctl, errorMsg);
				}
			}
		}
		result.setMessage(transactionResponse.getMessage());
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setMultixResponse(response);
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setSctlID(sctl.getID());
//		result.setNotificationCode(Integer.parseInt(transactionResponse.getCode()));

		return result;
	}
	
	/**
	 * TO be called after preprocess only
	 * @return
	 */
	public CFIXMsg communicate(TransactionDataContainerImpl cashinDataConatiner, ChannelCode channel) {
		if(cashinDataConatiner==null || channel==null){
			log.error("Input data is null.cashinDataConatiner:"+cashinDataConatiner+"channel:"+channel);
			throw new IllegalArgumentException();
		}
		log.info("constructing CMCashin object for cashin confirmation");
		SubscriberMDN destMDN = cashinDataConatiner.getDestinationMDN();
		SubscriberMDN partnerMDN = cashinDataConatiner.getPartnerMDN();
		Long transferId = cashinDataConatiner.getTransferID();
		Long parentTxnId = cashinDataConatiner.getParentTxnID();
		boolean confirmed = cashinDataConatiner.isConfirmed();
		CMInterswitchCashin cashinDetails = (CMInterswitchCashin) cashinDataConatiner.getMsg();
		ServiceChargeTransactionLog sctl = cashinDataConatiner.getSctl();

		CMCashIn cashinConfirm = new CMCashIn();
		cashinConfirm.setSourceMDN(partnerMDN.getMDN());
		cashinConfirm.setDestMDN(destMDN.getMDN());
		cashinConfirm.setParentTransactionID(parentTxnId);
		cashinConfirm.setTransferID(transferId);
		cashinConfirm.setConfirmed(confirmed);
		cashinConfirm.setChannelCode(channel.getChannelCode());
		cashinConfirm.setDestPocketID(cashinDataConatiner.getDestPocketID());
		cashinConfirm.setSourcePocketID(cashinDataConatiner.getSourcePocketID());
		cashinConfirm.setSourceApplication(channel.getChannelSourceApplication());
		cashinConfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashinConfirm.setIsSystemIntiatedTransaction(true);
		cashinConfirm.setPassword("");
		cashinConfirm.setINTxnId(cashinDetails.getPaymentLogID()+(StringUtils.isNotEmpty(cashinDetails.getCustReference()) ? cashinDetails.getCustReference() : StringUtils.EMPTY));
		cashinConfirm.setTransactionIdentifier(cashinDetails.getTransactionIdentifier());
		cashinConfirm.setServiceChargeTransactionLogID(sctl.getID());
		log.info("sending confirm request to backend");
		CFIXMsg response = super.process(cashinConfirm);
		return response;
		
	}
	
	/**
	 * To be called first in this handler before communicate or postprocess
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public WalletConfirmXMLResult preprocess(TransactionDataContainerImpl details, ChannelCode channel,String transactionIdentifier) {
 		
		((CMInterswitchCashin)details.getMsg()).setTransactionIdentifier(transactionIdentifier);

		WalletConfirmXMLResult result = new WalletConfirmXMLResult();
		if(details==null || channel==null){
			log.error("Input data is null.cashinDataConatiner:"+details+"channel:"+channel);
			throw new IllegalArgumentException();
		}
		SubscriberMDN partnerMDN = details.getPartnerMDN();
		Long parentTxnId = details.getParentTxnID();

		CMInterswitchCashin cashinDetails = (CMInterswitchCashin) details.getMsg();
		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_InterswitchCashin, cashinDetails.DumpFields(), parentTxnId);
		cashinDetails.setTransactionID(transactionsLog.getID());

		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(cashinDetails);
		result.setTransactionID(transactionsLog.getID());
		addCompanyANDLanguageToResult(partnerMDN,result);

		// Changing the Service_charge_transaction_log status based on the
		// response from Core engine.
		log.info("getting sctl from id");

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(parentTxnId);
		if (sctl != null) {
			if (CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				log.info("changing sctl status to processing");
				transactionChargingService.chnageStatusToProcessing(sctl);
			}
			else {
				log.info("transfer record changed status");
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}
		}
		else {
			log.error("transfer record not found");
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}
		details.setSctl(sctl);
		return result;		
	}
}
