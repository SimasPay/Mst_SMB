/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashOutAtATM;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.SubscriberCashOutAtATMConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 * 
 */
@Service("SubscriberCashOutAtATMConfirmHandlerImpl")
public class SubscriberCashOutAtATMConfirmHandlerImpl extends FIXMessageHandler implements SubscriberCashOutAtATMConfirmHandler{

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
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in SubscriberCashOutAtATMConfirmHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		CMCashOutAtATM cashoutConfirm = new CMCashOutAtATM();
		boolean confirmed = false;
		confirmed = Boolean.parseBoolean(transactionDetails.getConfirmString());
		ChannelCode cc = transactionDetails.getCc();
		cashoutConfirm.setSourceMDN(transactionDetails.getSourceMDN());
		cashoutConfirm.setParentTransactionID(transactionDetails.getParentTxnId());
		cashoutConfirm.setTransferID(transactionDetails.getTransferId());
		cashoutConfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashoutConfirm.setConfirmed(confirmed);
		cashoutConfirm.setSourceApplication((int)cc.getChannelsourceapplication());
		cashoutConfirm.setChannelCode(cc.getChannelcode());
		cashoutConfirm.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		log.info("Handling Subscriber Cashout At ATM confirmation WebAPI request for Parent Txn Id = " + cashoutConfirm.getParentTransactionID());
		XMLResult result = new MoneyTransferXMLResult();

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_CashOut,cashoutConfirm.DumpFields(),cashoutConfirm.getParentTransactionID());
		cashoutConfirm.setTransactionID(transactionsLog.getId().longValue());

		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(cashoutConfirm);
		result.setTransactionID(cashoutConfirm.getTransactionID());
		
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(cashoutConfirm.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+cashoutConfirm.getSourceMDN()+" has failed validations");
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
		
		log.info("getting third partner from system parameter 'thirdparty.partner.mdn' ");
		String ATMPartnerMDN = systemParametersService.getString(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
		log.info("doPost: Begin for destMDN:"+ATMPartnerMDN);
		if (StringUtils.isBlank(ATMPartnerMDN)) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			log.info("Third Party Partner MDN Value in System Parameters is Null");
			
			return result;
		}

		SubscriberMdn destPartnerMDN = subscriberMdnService.getByMDN(ATMPartnerMDN);
		validationResult = transactionApiValidationService.validatePartnerMDN(destPartnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination Agent has failed validations");
			validationResult = processValidationResultForPartner(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket destPocket= pocketService.getSuspencePocket(partnerService.getPartner(destPartnerMDN));
		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
	
		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTxnLog sctl = transactionChargingService.getServiceChargeTransactionLog(cashoutConfirm.getParentTransactionID(),cashoutConfirm.getTransactionIdentifier());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				log.error("The status of Sctl with id: "+sctl.getId()+"has been changed from Inquiry to: "+sctl.getStatus());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}

		} else {
			log.error("Could not find sctl with parentTransaction ID: "+cashoutConfirm.getParentTransactionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);	
			return result;
		}

		cashoutConfirm.setDestMDN(destPartnerMDN.getMdn());
		cashoutConfirm.setSourcePocketID(srcSubscriberPocket.getId().longValue());
		cashoutConfirm.setDestPocketID(destPocket.getId().longValue());
		cashoutConfirm.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		log.info("sending the cashoutConfirm request to backend for processing");
		CFIXMsg response = super.process(cashoutConfirm);
		result.setMultixResponse(response);

		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {
				transactionChargingService.addTransferID(sctl, cashoutConfirm.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, cashoutConfirm.getTransferID());
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