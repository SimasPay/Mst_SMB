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
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.TransferToUangkuInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;


/**
 * @author Chaitanya
 *
 */
@Service("TransferToUangkuInquiryHandlerImpl")
public class TransferToUangkuInquiryHandlerImpl extends FIXMessageHandler implements TransferToUangkuInquiryHandler {

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

	private String transactionName 		= ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_UANGKU;

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
		
		log.info("Handling TransferToUangkuInquiryHandlerImpl Enquiry WebAPI request::From " + transactionDetails.getSourceMDN()+ " For Amount = " + transactionDetails.getAmount());
		XMLResult result = new TransferInquiryXMLResult();

		ChannelCode channelCode = transactionDetails.getCc();
 		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());

		Integer validationResult=transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+sourceMDN.getMdn()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		Pocket srcPocket = pocketService.getDefaultPocket(sourceMDN, transactionDetails.getSourcePocketCode());
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		String destinationMDN = systemParametersService.getString(SystemParameterKeys.INTERBANK_PARTNER_MDN_KEY);
		transactionDetails.setDestMDN(destinationMDN);
		
 		SubscriberMdn destMDN = subscriberMdnService.getByMDN(transactionDetails.getDestMDN());
 		validationResult=transactionApiValidationService.validateSubscriberAsDestination(destMDN);
		addCompanyANDLanguageToResult(sourceMDN, result);

		Pocket destPocket = pocketService.getDefaultPocket(destMDN, "2");
		validationResult = transactionApiValidationService.validateSourcePocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		if(srcPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_BankAccount)
				||destPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_BankAccount)){

			if(!systemParametersService.getBankServiceStatus()){
				
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
				return result;
			}
		}
		
		CMBankAccountToBankAccount transferToUangkuInquiry = new CMBankAccountToBankAccount();
		transferToUangkuInquiry.setDestMDN(transactionDetails.getDestMDN());
		transferToUangkuInquiry.setAmount(transactionDetails.getAmount());
		transferToUangkuInquiry.setPin(transactionDetails.getSourcePIN());
		transferToUangkuInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferToUangkuInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		transferToUangkuInquiry.setSourceApplication((int)channelCode.getChannelsourceapplication());
		transferToUangkuInquiry.setChannelCode(channelCode.getChannelcode());
		transferToUangkuInquiry.setServiceName(transactionDetails.getServiceName());
		transferToUangkuInquiry.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_TRANSFER_TO_UANGKU);
		transferToUangkuInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Transfer_To_Uangku);
		transferToUangkuInquiry.setSourcePocketID(srcPocket.getId().longValue());
		transferToUangkuInquiry.setDestPocketID(destPocket.getId().longValue());
		transferToUangkuInquiry.setRemarks(transactionDetails.getDescription());
		transferToUangkuInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());		
		
		Transaction transaction = null;

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_TransferToUangkuInquiry, transferToUangkuInquiry.DumpFields());
		transferToUangkuInquiry.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(transferToUangkuInquiry);
		result.setTransactionID(transactionsLog.getId().longValue());

		addCompanyANDLanguageToResult(sourceMDN, result);

		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(transferToUangkuInquiry.getSourceMDN());
		sc.setDestMDN(transferToUangkuInquiry.getDestMDN());
		sc.setChannelCodeId(channelCode.getId().longValue());
		sc.setServiceName(transferToUangkuInquiry.getServiceName());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_UANGKU);
		sc.setTransactionAmount(transferToUangkuInquiry.getAmount());
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		sc.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		String uangkuPrefix = ConfigurationUtil.getTransferToUangkuPrefixNumber();

		if(transactionDetails.getDestinationBankAccountNo() != null){
			sc.setOnBeHalfOfMDN(uangkuPrefix + transactionDetails.getDestinationBankAccountNo());
		}
		
		transferToUangkuInquiry.setDestinationBankAccountNo(uangkuPrefix + transactionDetails.getDestAccountNumber());
		
		//Hierarchy validation
		validationResult = hierarchyService.validate((sourceMDN != null ? sourceMDN.getSubscriber() : null), null, sc.getServiceName(), sc.getTransactionTypeName());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			return result;
		}
		
		try{
			transaction =transactionChargingService.getCharge(sc);
			transferToUangkuInquiry.setAmount(transaction.getAmountToCredit());
			transferToUangkuInquiry.setCharges(transaction.getAmountTowardsCharges());

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
		transferToUangkuInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());

		CFIXMsg response = super.process(transferToUangkuInquiry);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (transactionResponse.getTransactionId()!=null) {
			sctl.setTransactionid(transactionResponse.getTransactionId());
			transferToUangkuInquiry.setTransactionID(transactionResponse.getTransactionId());
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
		result.setSctlID(sctl.getId().longValue());
		result.setBankName(transactionResponse.getBankName());
		result.setMfaMode("None");
		
		//For 2 factor authentication
		if(transactionResponse.isResult() == true){
			if(mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionName, channelCode.getId().longValue()) == true){
				result.setMfaMode("OTP");
				//mfaService.handleMFATransaction(sctl.getID(), sourceMDN.getMDN());
			}
		}
		return result;
	}
}
