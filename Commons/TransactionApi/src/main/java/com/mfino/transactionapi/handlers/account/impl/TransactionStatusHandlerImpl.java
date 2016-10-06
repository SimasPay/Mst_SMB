package com.mfino.transactionapi.handlers.account.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetTransactionStatus;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.service.EnumTextService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.TransactionStatusHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.LastNTxnsXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/*
 * @author Bala Sunku
 * 
 */
@Service("TransactionStatusHandlerImpl")
public class TransactionStatusHandlerImpl extends FIXMessageHandler implements TransactionStatusHandler{
	private static Logger log = LoggerFactory.getLogger(TransactionStatusHandlerImpl.class);

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
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
 
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("Handling emoney transaction status webapi request");
		ChannelCode	cc = transactionDetails.getCc();
		CMGetTransactionStatus transactionStatus = new CMGetTransactionStatus();
		transactionStatus.setSourceMDN(transactionDetails.getSourceMDN());
		transactionStatus.setPin(transactionDetails.getSourcePIN());
		transactionStatus.setTransferID(transactionDetails.getTransferId());
		transactionStatus.setSourceApplication(new Integer(String.valueOf(cc.getChannelsourceapplication())));
		transactionStatus.setChannelCode(cc.getChannelcode());
		transactionStatus.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());		
		
		LastNTxnsXMLResult result = new LastNTxnsXMLResult();
		result.setEnumTextService(enumTextService);

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_GetTransactionStatus, transactionStatus.DumpFields());
		
		transactionStatus.setTransactionID(transactionsLog.getId().longValue());

		result.setSourceMessage(transactionStatus);
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());

		SubscriberMdn sourceMDN=subscriberMdnService.getByMDN(transactionStatus.getSourceMDN());
		

		Integer validationResult =transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+transactionStatus.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		validationResult =transactionApiValidationService.validatePin(sourceMDN, transactionStatus.getPin());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: "+transactionStatus.getSourceMDN());
			result.setNumberOfTriesLeft(new Integer(String.valueOf(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - sourceMDN.getWrongpincount())));
			result.setNotificationCode(validationResult);
			return result;
		}

		addCompanyANDLanguageToResult(sourceMDN,result);

		Transaction transaction = null;

		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(transactionStatus.getSourceMDN());
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(cc.getId().longValue());
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_TRANSACTIONSTATUS);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(transactionsLog.getId().longValue());
		serviceCharge.setTransactionIdentifier(transactionStatus.getTransactionIdentifier());

		try{
			transaction =transactionChargingService.getCharge(serviceCharge);
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
 			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
 			return result;
		}

		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		transactionStatus.setServiceChargeTransactionLogID(sctl.getId().longValue());
	
		ServiceChargeTxnLog sctlList = null;
		
		try {
 			sctlList = sctlService.getBySCTLID(transactionStatus.getTransferID());
			if (sctl != null) {
				sctl.setCalculatedcharge(BigDecimal.ZERO);
				transactionChargingService.completeTheTransaction(sctl);
			}
		} catch (Exception e) {
			log.error("failed get sctl list",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
			transactionChargingService.failTheTransaction(sctl, MessageText._("Failed while getting status of the transaction"));
			return result;
		}
		
		if (sctlList == null) {
			log.info("SCTL record not found for the given id --> " +transactionStatus.getTransferID() );
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}		
		
		ServiceChargeTxnLog sctlrecord =sctlList;
		
		if( (sctlrecord.getSourcemdn() !=null && sctlrecord.getSourcemdn().equals(sourceMDN.getMdn()))
				|| (sctlrecord.getDestmdn()!=null && sctlrecord.getDestmdn().equals(sourceMDN.getMdn())) ){
			result.setAmount(sctlrecord.getTransactionamount());
			result.setSctlID(sctl.getId().longValue());
			result.setSCTLList(sctlrecord);
			result.setNotificationCode(CmFinoFIX.NotificationCode_CommodityTransaferDetails);
			return result;
		}else{
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
		}
		return result;
	}
}
