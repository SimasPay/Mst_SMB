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

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.InterBankTransferInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;


/**
 * @author Chaitanya
 *
 */
@Service("InterBankTransferInquiryHandlerImpl")
public class InterBankTransferInquiryHandlerImpl extends FIXMessageHandler implements InterBankTransferInquiryHandler{

	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private String serviceName     		= ServiceAndTransactionConstants.SERVICE_BANK;
	private String transactionName 		= ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Handling InterBankTransfer Enquiry WebAPI request::From " + transactionDetails.getSourceMDN()+ " For Amount = " + transactionDetails.getAmount());
		XMLResult result = new TransferInquiryXMLResult();

		ChannelCode channelCode = transactionDetails.getCc();
 		BigDecimal amount = transactionDetails.getAmount();
		
 		SubscriberMDN sourceMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());


		Integer validationResult=transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+sourceMDN.getMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		Pocket srcPocket = pocketService.getDefaultPocket(sourceMDN, transactionDetails.getSourcePocketCode());
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		/*
		 * Commented the below code to allow interbank transfer from an e-money pocket
		 */
		//if(!srcPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)){
		//	result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidSourcePocketCode);
		//	return result;
		//}
		
		String destinationMDN = systemParametersService.getString(SystemParameterKeys.INTERBANK_PARTNER_MDN_KEY);
		transactionDetails.setDestMDN(destinationMDN);

		CMInterBankFundsTransferInquiry interBankTransferInquiry = new CMInterBankFundsTransferInquiry();
		interBankTransferInquiry.setDestMDN(transactionDetails.getDestMDN());
		interBankTransferInquiry.setAmount(amount);
		interBankTransferInquiry.setPin(transactionDetails.getSourcePIN());
		interBankTransferInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		interBankTransferInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		interBankTransferInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		interBankTransferInquiry.setSourceApplication(channelCode.getChannelSourceApplication());
		interBankTransferInquiry.setChannelCode(channelCode.getChannelCode());
		interBankTransferInquiry.setServiceName(transactionDetails.getServiceName());
		interBankTransferInquiry.setSourcePocketID(srcPocket.getID());
		interBankTransferInquiry.setSourceBankAccountNo(srcPocket.getCardPAN());
		interBankTransferInquiry.setDestAccountNumber(transactionDetails.getDestAccountNumber());
		interBankTransferInquiry.setDestinationBankAccountNo(transactionDetails.getDestAccountNumber());
		interBankTransferInquiry.setDestBankCode(transactionDetails.getDestBankCode());
		interBankTransferInquiry.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_INTERBANK_TRANSFER);
		interBankTransferInquiry.setUICategory(CmFinoFIX.TransactionUICategory_InterBank_Transfer);
		
 		SubscriberMDN destMDN = subscriberMdnService.getByMDN(transactionDetails.getDestMDN());
 		validationResult=transactionApiValidationService.validateSubscriberAsDestination(destMDN);
		addCompanyANDLanguageToResult(sourceMDN, result);

		Pocket destPocket = pocketService.getDefaultPocket(destMDN, "2");
		validationResult = transactionApiValidationService.validateSourcePocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(destPocket!=null? destPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		if(srcPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)
				||destPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)){
			

			if(!systemParametersService.getBankServiceStatus()){
				
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
				return result;
			}
		}
		interBankTransferInquiry.setDestPocketID(destPocket.getID());
		
		Transaction transaction = null;

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_InterBankFundsTransfer, interBankTransferInquiry.DumpFields());
		interBankTransferInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(interBankTransferInquiry);
		result.setTransactionID(transactionsLog.getID());
		//result.setDestinationMDN(interBankTransferInquiry.getDestMDN());

		addCompanyANDLanguageToResult(sourceMDN, result);


		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(interBankTransferInquiry.getSourceMDN());
		sc.setDestMDN(interBankTransferInquiry.getDestMDN());
		sc.setChannelCodeId(channelCode.getID());
		sc.setServiceName(interBankTransferInquiry.getServiceName());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER);
		sc.setTransactionAmount(interBankTransferInquiry.getAmount());
		sc.setTransactionLogId(transactionsLog.getID());
		sc.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

		if(transactionDetails.getDestinationBankAccountNo() != null){
			sc.setOnBeHalfOfMDN(transactionDetails.getDestinationBankAccountNo());
			interBankTransferInquiry.setDestinationBankAccountNo(transactionDetails.getDestinationBankAccountNo());
		}
		
		//Hierarchy validation
		validationResult = hierarchyService.validate((sourceMDN != null ? sourceMDN.getSubscriber() : null), null, sc.getServiceName(), sc.getTransactionTypeName());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			return result;
		}
		
		try{
			transaction =transactionChargingService.getCharge(sc);
			interBankTransferInquiry.setAmount(transaction.getAmountToCredit());
			interBankTransferInquiry.setCharges(transaction.getAmountTowardsCharges());

		} catch (InvalidServiceException ise) {
			log.error("Exception occured in getting charges",ise);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);//change to service not found
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		interBankTransferInquiry.setServiceChargeTransactionLogID(sctl.getID());

		CFIXMsg response = super.process(interBankTransferInquiry);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (transactionResponse.getTransactionId()!=null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			interBankTransferInquiry.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}
		if (!transactionResponse.isResult() && sctl!=null){
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}

		result.setDestinationName(transactionResponse.getDestinationUserName());
		result.setDestinationAccountNumber(transactionDetails.getDestAccountNumber());
		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setSctlID(sctl.getID());
		result.setBankName(transactionResponse.getBankName());
		result.setMfaMode("None");
		
		//For 2 factor authentication
		if(transactionResponse.isResult() == true){
			if(mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionName, channelCode.getID()) == true){
				result.setMfaMode("OTP");
				//mfaService.handleMFATransaction(sctl.getID(), sourceMDN.getMDN());
			}
		}
		return result;
	}
}
