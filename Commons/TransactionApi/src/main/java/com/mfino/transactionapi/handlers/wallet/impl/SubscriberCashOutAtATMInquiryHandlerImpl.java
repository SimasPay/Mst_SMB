package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.SubscriberCashOutAtATMInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

@Service("SubscriberCashOutAtATMInquiryHandlerImpl")
public class SubscriberCashOutAtATMInquiryHandlerImpl extends FIXMessageHandler implements SubscriberCashOutAtATMInquiryHandler{
  
	private static Logger log = LoggerFactory.getLogger(SubscriberCashOutAtATMInquiryHandlerImpl.class);
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService;
	
	@Autowired
	@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
  
	public Result handle(TransactionDetails transactionDetails) {
	    log.info("Extracting data from transactionDetails in SubscriberCashOutAtATMInquiryHandlerImpl from sourceMDN: " 
	    		+ transactionDetails.getSourceMDN() + "to" + transactionDetails.getDestMDN());
	    
	    CmFinoFIX.CMCashOutAtATMInquiry cashOutInquiry = new CmFinoFIX.CMCashOutAtATMInquiry();
	    ChannelCode cc = transactionDetails.getCc();
	    cashOutInquiry.setSourceMDN(transactionDetails.getSourceMDN());
	    cashOutInquiry.setPin(transactionDetails.getSourcePIN());
	    cashOutInquiry.setAmount(transactionDetails.getAmount());
	    cashOutInquiry.setSourceApplication(Integer.valueOf(cc.getChannelsourceapplication().intValue()));
	    cashOutInquiry.setChannelCode(cc.getChannelcode());
	    cashOutInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
	    cashOutInquiry.setSourceMessage(transactionDetails.getSourceMessage());
	    cashOutInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
	    
	    log.info("Handling Subscriber CashOut At ATM Inquiry webapi request::From " + cashOutInquiry.getSourceMDN() 
	    		+ " For Amount = " + cashOutInquiry.getAmount());
	    
	    TransactionLog transactionsLog = this.transactionLogService.saveTransactionsLog(Integer.valueOf(1372), cashOutInquiry.DumpFields());
	    cashOutInquiry.setTransactionID(Long.valueOf(transactionsLog.getId().longValue()));
	    
	    XMLResult result = new TransferInquiryXMLResult();
	    result.setTransactionID(Long.valueOf(transactionsLog.getId().longValue()));
	    result.setSourceMessage(cashOutInquiry);
	    result.setTransactionTime(transactionsLog.getTransactiontime());
	    
	    BigDecimal trxAmount = transactionDetails.getAmount();
	    
	    BigDecimal minAmount = this.systemParametersService.getBigDecimal(SystemParameterKeys.MIN_VALUE_OF_CASHOUT_AT_ATM);
	    if (trxAmount.compareTo(minAmount) < 0) {
	    	result.setNotificationCode(CmFinoFIX.NotificationCode_MinValueOfCashOutAtATM);
	    	log.info("Below the Min amount allowed for Cash out at ATM");
	    	result.setMinAmount(minAmount);
	    	return result;
	    }
	    
	    BigDecimal multiplyAmount = this.systemParametersService.getBigDecimal(SystemParameterKeys.MULTIPLES_VALUE_OF_CASHOUT_AT_ATM);
	    if (trxAmount.intValue() % multiplyAmount.intValue() != 0) {
	    	result.setNotificationCode(CmFinoFIX.NotificationCode_MultiplyValueOfCashOutAtATM);
	    	result.setMultiplesOff(multiplyAmount);
	    	return result;
	    }
	    
	    SubscriberMdn srcSubscriberMDN = this.subscriberMdnService.getByMDN(cashOutInquiry.getSourceMDN());
	    Subscriber srcSubscriber = srcSubscriberMDN.getSubscriber();
	    if ((srcSubscriber.getKycLevel() == null) || (srcSubscriber.getKycLevel().getKyclevel() == null) || 
	    		(srcSubscriber.getKycLevel().getKyclevel().intValue() != CmFinoFIX.SubscriberKYCLevel_UnBanked.intValue())) {
	    	result.setNotificationCode(CmFinoFIX.NotificationCode_AtmCashWithdrawalOnlyForKYC);
	    	return result;
	    }
	    
	    Integer validationResult = this.transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
	    if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
	    	log.error("Source subscriber with mdn : " + cashOutInquiry.getSourceMDN() + " has failed validations");
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
	    
	    ServiceCharge sc = new ServiceCharge();
	    sc.setChannelCodeId(cc.getId().longValue());
	    sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM);
	    sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
	    sc.setSourceMDN(srcSubscriberMDN.getMdn());
	    sc.setTransactionAmount(cashOutInquiry.getAmount());
	    sc.setTransactionLogId(cashOutInquiry.getTransactionID().longValue());
	    sc.setTransactionIdentifier(cashOutInquiry.getTransactionIdentifier());
	    sc.setOnBeHalfOfMDN(transactionDetails.getOnBehalfOfMDN());
	    
	    Transaction transaction = null;
	    try {
	    	transaction = this.transactionChargingService.getCharge(sc);
	    	cashOutInquiry.setAmount(transaction.getAmountToCredit());
	    	cashOutInquiry.setCharges(transaction.getAmountTowardsCharges());
	    } catch (InvalidServiceException e) {
	    	log.error("Exception occured in getting charges", e);
	    	result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
	    	return result;
	    } catch (InvalidChargeDefinitionException e) {
	    	log.error(e.getMessage());
	    	result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
	    	return result;
	    }
	    ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
	    
	    cashOutInquiry.setSourcePocketID(Long.valueOf(srcSubscriberPocket.getId().longValue()));
	    cashOutInquiry.setServiceChargeTransactionLogID(Long.valueOf(sctl.getId().longValue()));
	    cashOutInquiry.setOnBehalfMDN(transactionDetails.getOnBehalfOfMDN());
	    
	    String dummySubMdn = this.systemParametersService.getString(SystemParameterKeys.PLATFORM_DUMMY_SUBSCRIBER_MDN);
	    SubscriberMdn objDestSubMdn = this.subscriberMdnService.getByMDN(dummySubMdn);
	    Pocket dummyPocket = (Pocket)objDestSubMdn.getPockets().iterator().next();
	    cashOutInquiry.setDestMDN(dummySubMdn);
	    cashOutInquiry.setDestPocketID(dummyPocket.getId());
	    
	    log.info("sending the cashOutInquiry request to backend for processing");
	    CFIXMsg response = super.process(cashOutInquiry);
	    
	    TransactionResponse transactionResponse = checkBackEndResponse(response);
	    log.info("Got the response from backend .The notification code is : " + transactionResponse.getCode() + 
	    		" and the result: " + transactionResponse.isResult());
	    
	    if (transactionResponse.getTransactionId() != null) {
	    	sctl.setTransactionid(transactionResponse.getTransactionId());
	    	result.setTransactionID(transactionResponse.getTransactionId());
	    	this.transactionChargingService.saveServiceTransactionLog(sctl);
	    }
	    
	    if ((!transactionResponse.isResult()) && (sctl != null)){
	    	String errorMsg = transactionResponse.getMessage();
	    	this.transactionChargingService.failTheTransaction(sctl, errorMsg);
	    }
	    
	    result.setSctlID(Long.valueOf(sctl.getId().longValue()));
	    result.setMultixResponse(response);
	    result.setDebitAmount(transaction.getAmountToDebit());
	    result.setCreditAmount(transaction.getAmountToCredit());
	    result.setServiceCharge(transaction.getAmountTowardsCharges());
	    addCompanyANDLanguageToResult(srcSubscriberMDN, result);
	    result.setParentTransactionID(transactionResponse.getTransactionId());
	    result.setTransferID(transactionResponse.getTransferId());
	    result.setCode(transactionResponse.getCode());
	    result.setMessage(transactionResponse.getMessage());
	    return result;
	}
}