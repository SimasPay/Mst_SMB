package com.mfino.transactionapi.handlers.wallet.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.MFAService;
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

@Service("SubscriberCashOutAtATMConfirmHandlerImpl")
public class SubscriberCashOutAtATMConfirmHandlerImpl extends FIXMessageHandler implements SubscriberCashOutAtATMConfirmHandler {
	
	private static Logger log = LoggerFactory.getLogger(SubscriberCashOutConfirmHandlerImpl.class);
  
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
	private TransactionChargingService transactionChargingService;
  
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
  
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
  
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
  
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
  
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED, rollbackFor={Throwable.class})
	public Result handle(TransactionDetails transactionDetails) {
    
		CmFinoFIX.CMCashOutAtATM cashoutConfirm = new CmFinoFIX.CMCashOutAtATM();
		boolean confirmed = false;
		confirmed = Boolean.parseBoolean(transactionDetails.getConfirmString());
    
		ChannelCode cc = transactionDetails.getCc();
		cashoutConfirm.setSourceMDN(transactionDetails.getSourceMDN());
		cashoutConfirm.setParentTransactionID(transactionDetails.getParentTxnId());
	    cashoutConfirm.setTransferID(transactionDetails.getTransferId());
	    cashoutConfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
	    cashoutConfirm.setConfirmed(Boolean.valueOf(confirmed));
	    cashoutConfirm.setSourceApplication(Integer.valueOf(cc.getChannelsourceapplication().intValue()));
	    cashoutConfirm.setChannelCode(cc.getChannelcode());
	    cashoutConfirm.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
    
	    log.info("Handling Subscriber Cashout At ATM confirmation WebAPI request for Parent Txn Id = " + 
	    		cashoutConfirm.getParentTransactionID());
	    
	    TransactionLog transactionsLog = this.transactionLogService.saveTransactionsLog(Integer.valueOf(1274), 
	    		cashoutConfirm.DumpFields(), cashoutConfirm.getParentTransactionID());
	    cashoutConfirm.setTransactionID(Long.valueOf(transactionsLog.getId().longValue()));

	    XMLResult result = new MoneyTransferXMLResult();
	    result.setTransactionTime(transactionsLog.getTransactiontime());
	    result.setSourceMessage(cashoutConfirm);
	    result.setTransactionID(cashoutConfirm.getTransactionID());
    
	    SubscriberMdn srcSubscriberMDN = this.subscriberMdnService.getByMDN(cashoutConfirm.getSourceMDN());
    
	    Integer validationResult = this.transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
	    if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
	    	log.error("Source subscriber with mdn : " + cashoutConfirm.getSourceMDN() + " has failed validations");
	    	result.setNotificationCode(validationResult);
	    	return result;
	    }
	    
	    Pocket srcSubscriberPocket = this.pocketService.getDefaultPocket(srcSubscriberMDN, transactionDetails.getSourcePocketCode());
	    validationResult = this.transactionApiValidationService.validateSourcePocket(srcSubscriberPocket);
	    if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
	    	log.error("Source pocket with id " + (srcSubscriberPocket != null ? srcSubscriberPocket.getId() : null) + " has failed validations");
	    	result.setNotificationCode(validationResult);
	    	return result;
	    }
	    
	    ServiceChargeTxnLog sctl = this.transactionChargingService.getServiceChargeTransactionLog(
	    		cashoutConfirm.getParentTransactionID().longValue(), cashoutConfirm.getTransactionIdentifier());
    
	    if (sctl != null) {
	    	if (CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
	    		this.transactionChargingService.chnageStatusToProcessing(sctl);
	    	} else {
	    		log.error("The status of Sctl with id: " + sctl.getId() + "has been changed from Inquiry to: " + sctl.getStatus());
	    		result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
	    		return result;
	    	}
	    	
	    	if (StringUtils.isNotBlank(sctl.getOnbehalfofmdn())) {
	    		cashoutConfirm.setOnBehalfMDN(sctl.getOnbehalfofmdn());
	    	}
	    } else {
	    	log.error("Could not find sctl with parentTransaction ID: " + cashoutConfirm.getParentTransactionID());
	    	result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
	    	return result;
	    }
	    
	    String dummySubMdn = this.systemParametersService.getString("platform.dummy.subscriber.mdn");
	    SubscriberMdn objDestSubMdn = this.subscriberMdnService.getByMDN(dummySubMdn);
	    Pocket dummyPocket = (Pocket)objDestSubMdn.getPockets().iterator().next();
    
	    cashoutConfirm.setDestMDN(objDestSubMdn.getMdn());
	    cashoutConfirm.setSourcePocketID(srcSubscriberPocket.getId());
	    cashoutConfirm.setDestPocketID(dummyPocket.getId());
	    cashoutConfirm.setServiceChargeTransactionLogID(sctl.getId());
    
	    if (this.mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionDetails.getTransactionName(), cc.getId())) {
	    	String mfaOneTimeOTP = transactionDetails.getTransactionOTP();
	    	if ((mfaOneTimeOTP == null) || (!this.mfaService.isValidOTP(mfaOneTimeOTP, sctl.getId(), srcSubscriberMDN.getMdn()))) {
	    		result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidMFAOTP);
	    		return result;
	    	}
	    }
    
	    log.info("sending the cashoutConfirm request to backend for processing");
	    CFIXMsg response = super.process(cashoutConfirm);
	    result.setMultixResponse(response);
    
	    TransactionResponse transactionResponse = checkBackEndResponse(response);
	    log.info("Got the response from backend .The notification code is : " + transactionResponse.getCode() + 
	    		" and the result: " + transactionResponse.isResult());
	    
	    if (!"Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage())) {
	    	if (transactionResponse.isResult()) {
	    		transactionChargingService.confirmTheTransaction(sctl, cashoutConfirm.getTransferID());
	    		this.commodityTransferService.addCommodityTransferToResult(result, cashoutConfirm.getTransferID());
	    		result.setDebitAmount(sctl.getTransactionamount());
	    		result.setCreditAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
	    		result.setServiceCharge(sctl.getCalculatedcharge());
	    	} else {
	    		String errorMsg = transactionResponse.getMessage();
	    		if (errorMsg.length() > 255) {
	    			errorMsg = errorMsg.substring(0, 255);
	    		}
	    		this.transactionChargingService.failTheTransaction(sctl, errorMsg);
	    	}
	    }
	    
	    result.setSctlID(Long.valueOf(sctl.getId().longValue()));
	    result.setMessage(transactionResponse.getMessage());
	    return result;
	}
}
