/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
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
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.MFAService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.BankTransferInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Deva
 * 
 */
@org.springframework.stereotype.Service("BankTransferInquiryHandlerImpl")
public class BankTransferInquiryHandlerImpl extends FIXMessageHandler implements BankTransferInquiryHandler{
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	private static Logger log = LoggerFactory.getLogger(BankTransferInquiryHandlerImpl.class);

	private String transactionName = ServiceAndTransactionConstants.TRANSACTION_TRANSFER;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	public Result handle(TransactionDetails transactionDetails) {

		log.info("Handling Bank Transfer Enquiry WebAPI request::From " + transactionDetails.getSourceMDN() + " To " + 
				transactionDetails.getDestMDN() + " For Amount = " + transactionDetails.getAmount());
		
		ChannelCode channelCode = transactionDetails.getCc();

		CMBankAccountToBankAccount bankAccountToBankAccount = new CMBankAccountToBankAccount();
		bankAccountToBankAccount.setSourcePocketID(transactionDetails.getSrcPocketId());
		bankAccountToBankAccount.setDestPocketID(transactionDetails.getDestinationPocketId());
		bankAccountToBankAccount.setSourceMDN(transactionDetails.getSourceMDN());
		bankAccountToBankAccount.setDestMDN(transactionDetails.getDestMDN());
		bankAccountToBankAccount.setIsSystemIntiatedTransaction(transactionDetails.isSystemIntiatedTransaction());
		bankAccountToBankAccount.setAmount(transactionDetails.getAmount());
		bankAccountToBankAccount.setServletPath(transactionDetails.getServletPath());
		bankAccountToBankAccount.setSourceApplication((int)channelCode.getChannelsourceapplication());
		bankAccountToBankAccount.setChannelCode(channelCode.getChannelcode());
		bankAccountToBankAccount.setSourceMessage(transactionDetails.getSourceMessage());
		bankAccountToBankAccount.setServiceName(transactionDetails.getServiceName());
		bankAccountToBankAccount.setPin(transactionDetails.getSourcePIN());
		bankAccountToBankAccount.setRemarks(transactionDetails.getDescription());
		bankAccountToBankAccount.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());

		XMLResult result = new TransferInquiryXMLResult();
		ChannelCode cc = transactionDetails.getCc();

		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());
		Transaction transaction = null;

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccount, bankAccountToBankAccount.DumpFields());
		
		bankAccountToBankAccount.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(bankAccountToBankAccount);
		result.setTransactionID(transactionsLog.getId().longValue());
		
		addCompanyANDLanguageToResult(sourceMDN, result);
		
		Pocket destPocket = null;
		
		if (StringUtils.isNotBlank(String.valueOf(bankAccountToBankAccount.getDestPocketID()))) {
			
			destPocket = pocketService.getById(bankAccountToBankAccount.getDestPocketID());
		}
		
		Pocket srcPocket = null;
		
		if (StringUtils.isNotBlank(String.valueOf(bankAccountToBankAccount.getSourcePocketID()))) {
			
			srcPocket = pocketService.getById(bankAccountToBankAccount.getSourcePocketID());
		}
		
		ServiceCharge sc = new ServiceCharge();
		
		sc.setSourceMDN(bankAccountToBankAccount.getSourceMDN());
		sc.setDestMDN(bankAccountToBankAccount.getDestMDN());
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(bankAccountToBankAccount.getServiceName());
		
		if(ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER_INQUIRY.equals(transactionDetails.getTransactionName())) {
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER);
		} 
		else {
			
			/*
			 * If the Destination Pocket Type is of type SVA or LakuPandai then it is E2ETransfer type; else it is E2BTransfer type.
			 */
			if(srcPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_SVA)) {
				if(destPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_BankAccount)){
					sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_E2BTRANSFER);
					transactionName = ServiceAndTransactionConstants.TRANSACTION_E2BTRANSFER;
					sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
					bankAccountToBankAccount.setUICategory(CmFinoFIX.TransactionUICategory_Emoney_To_Bank);
				}
			}			
			
			if(srcPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_LakuPandai)){
				
				if(destPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_BankAccount)){
					sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_L2BTRANSFER);
					transactionName = ServiceAndTransactionConstants.TRANSACTION_L2BTRANSFER;
					if (ServiceAndTransactionConstants.SERVICE_AGENT.equals(transactionDetails.getServiceName())) {
						sc.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
					}else{
						sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
					}
				}
				
				if(destPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_LakuPandai) || destPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_SVA)){
					sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_L2LTRANSFER);
					sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
					
					transactionName = ServiceAndTransactionConstants.TRANSACTION_L2LTRANSFER;
				}
			}
			
			if(srcPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_BankAccount)){

				if(destPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_BankAccount)){
					sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
					sc.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
					
					transactionName = ServiceAndTransactionConstants.TRANSACTION_TRANSFER;
				}
				
				if(destPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_LakuPandai) || destPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_SVA)){
					sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_B2LTRANSFER);
					//sc.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
					if (ServiceAndTransactionConstants.SERVICE_AGENT.equals(transactionDetails.getServiceName())) {
						sc.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
					}else{
						sc.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
					}
					
					transactionName = ServiceAndTransactionConstants.TRANSACTION_B2LTRANSFER;
				}
			}
						
			//sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
		}
		
		sc.setTransactionAmount(bankAccountToBankAccount.getAmount());
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		sc.setTransactionIdentifier(bankAccountToBankAccount.getTransactionIdentifier());
		sc.setDescription(transactionDetails.getDescription());
		
		if(transactionDetails.getDestinationBankAccountNo() != null){
			sc.setOnBeHalfOfMDN(transactionDetails.getDestinationBankAccountNo());
			bankAccountToBankAccount.setDestinationBankAccountNo(transactionDetails.getDestinationBankAccountNo());
		}

		SubscriberMdn sourceSubscriberMdn = sourceMDN;
		SubscriberMdn destSubscriberMdn =  subscriberMdnService.getByMDN(bankAccountToBankAccount.getDestMDN());

		Subscriber sourceSubscriber = (sourceSubscriberMdn != null) ? sourceSubscriberMdn.getSubscriber() : null;
		Subscriber destSubscriber = (destSubscriberMdn != null) ? destSubscriberMdn.getSubscriber() : null;
	
//		Integer validationResult = hierarchyService.validate(sourceSubscriber, destSubscriber, sc.getServiceName(), sc.getTransactionTypeName());
//		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
//			result.setNotificationCode(validationResult);
//			return result;
//		}
		
		try{
			transaction =transactionChargingService.getCharge(sc);
			bankAccountToBankAccount.setAmount(transaction.getAmountToCredit());
			bankAccountToBankAccount.setCharges(transaction.getAmountTowardsCharges());
		} catch (InvalidServiceException ise) {
			log.error("Exception occured in getting charges",ise);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		bankAccountToBankAccount.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		CFIXMsg response = super.process(bankAccountToBankAccount);
        
		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (transactionResponse.getTransactionId() !=null){
			sctl.setTransactionid(transactionResponse.getTransactionId());
			bankAccountToBankAccount.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}
		transactionChargingService.updateTransactionStatus(transactionResponse, sctl);
       if(transactionResponse.getDestinationType() !=null){
			if(transactionResponse.getDestinationType().equals("Account")){
				result.setDestinationAccountNumber(bankAccountToBankAccount.getDestinationBankAccountNo());
			}
		}else{
			result.setDestinationMDN(bankAccountToBankAccount.getDestMDN());
			result.setDestinationAccountNumber(bankAccountToBankAccount.getDestinationBankAccountNo());
		}
       	String receiverAccountName = (StringUtils.isNotBlank(destSubscriber.getFirstname()) ? destSubscriber.getFirstname() : "")
					+ " " + (StringUtils.isNotBlank(destSubscriber.getLastname()) ? destSubscriber.getLastname() : "");
       	result.setReceiverAccountName(receiverAccountName);
		result.setDestinationName(transactionResponse.getDestinationUserName());
		result.setBankName(transactionResponse.getBankName());
		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setSctlID(sctl.getId().longValue());
		
		result.setMfaMode("None");
		
		//For 2 factor authentication
		if(transactionResponse.isResult() == true){
			if(mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionName, cc.getId().longValue()) == true){
				result.setMfaMode("OTP");
				//mfaService.handleMFATransaction(sctl.getID(), sourceMDN.getMDN());
			}
		}
		return result;
	}
}
