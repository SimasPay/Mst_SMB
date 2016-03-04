/**
 * 
 */
package com.mfino.transactionapi.handlers.agent.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.handlers.agent.AgentTransferInquiryHandler;
import com.mfino.transactionapi.handlers.money.BankTransferInquiryHandler;
import com.mfino.transactionapi.handlers.wallet.NonRegisteredTransferInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author srinivaas
 *
 */
@Service("AgentTransferInquiryHandlerImpl")
public class AgentTransferInquiryHandlerImpl extends FIXMessageHandler implements AgentTransferInquiryHandler{

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("BankTransferInquiryHandlerImpl")
	private BankTransferInquiryHandler bankTransferInquiryHandler;
	
	@Autowired
	@Qualifier("NonRegisteredTransferInquiryHandlerImpl")
	private NonRegisteredTransferInquiryHandler nonRegisteredTransferInquiryHandler;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("BEGIN handle :: AgentTransferInquiryHandlerImpl");
		ChannelCode channelCode = transactionDetails.getCc();

		BigDecimal amount = transactionDetails.getAmount();
		log.info("Handling Transfer Enquiry WebAPI request::From " + transactionDetails.getSourceMDN() + " To " + 
				transactionDetails.getDestMDN() + " For Amount = " + transactionDetails.getAmount());
		Result result = new TransferInquiryXMLResult();
		String sourceMessage = transactionDetails.getSourceMessage();
		
		CMBankAccountToBankAccount bankAccountToBankAccount = new CMBankAccountToBankAccount();
		bankAccountToBankAccount.setDestMDN(transactionDetails.getDestMDN());
		bankAccountToBankAccount.setAmount(amount);
		bankAccountToBankAccount.setPin(transactionDetails.getSourcePIN());
		bankAccountToBankAccount.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		bankAccountToBankAccount.setSourceMDN(transactionDetails.getSourceMDN());
		bankAccountToBankAccount.setSourceMessage(StringUtils.isNotBlank(sourceMessage) ? sourceMessage : ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
		bankAccountToBankAccount.setSourceApplication(channelCode.getChannelSourceApplication());
		bankAccountToBankAccount.setChannelCode(channelCode.getChannelCode());
		bankAccountToBankAccount.setServiceName(transactionDetails.getServiceName());
	
		if(ServiceAndTransactionConstants.TRANSACTION_CASH_IN_TO_AGENT_INQUIRY.equals(transactionDetails.getTransactionName())){
			bankAccountToBankAccount.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
			transactionDetails.setSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
			bankAccountToBankAccount.setUICategory(CmFinoFIX.TransactionUICategory_Cash_In_To_Agent);
		}
		result.setDestinationMDN(transactionDetails.getDestMDN());
		result.setSourceMessage(bankAccountToBankAccount);
		
		SubscriberMDN sourceMDN = subscriberMdnService.getByMDN(transactionDetails.getSourceMDN());

		Integer validationResult=transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+sourceMDN.getMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Subscriber srcSub = sourceMDN.getSubscriber();
		KYCLevel srcKyc = srcSub.getKYCLevelByKYCLevel();
		if(srcKyc.getKYCLevel().equals(new Long(CmFinoFIX.SubscriberKYCLevel_NoKyc))){
			log.info(String.format("MoneyTransfer is Failed as the the Source Subscriber(%s) KycLevel is NoKyc",transactionDetails.getSourceMDN()));
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyTransferFromNoKycSubscriberNotAllowed);
			return result;
		}
		
		Pocket srcPocket = null;
		if (StringUtils.isNotBlank(transactionDetails.getSourcePocketId())) {
			srcPocket = pocketService.getById(Long.parseLong(transactionDetails.getSourcePocketId()));
		}
		else {
			srcPocket = pocketService.getDefaultPocket(sourceMDN, transactionDetails.getSourcePocketCode());
		}
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		
		bankAccountToBankAccount.setSourcePocketID(srcPocket.getID());
		transactionDetails.setSrcPocketId(srcPocket.getID());

		SubscriberMDN destinationMDN = subscriberMdnService.getByMDN(transactionDetails.getDestMDN());
		validationResult= transactionApiValidationService.validateSubscriberAsDestination(destinationMDN); 

		if((CmFinoFIX.NotificationCode_DestinationMDNNotFound.equals(validationResult) ||
				CmFinoFIX.NotificationCode_SubscriberNotRegistered.equals(validationResult)))
		{
			/*
			 * Commented as for Simaspay we do not need to transfer to Unregistered subscriber. 
			 */
			
			/*if(ApiConstants.POCKET_CODE_SVA.equals(transactionDetails.getSourcePocketCode()) &&
					transactionDetails.getSourcePocketCode().equals(transactionDetails.getDestPocketCode())){
				log.info("Handling transferInquiry for unregistered subscriber");
				//Handle UnRegistered MDN 
				//Here need to check if the Subscriber has debited the existing amount and OTP is set as null
				//if not reject the transaction.
				transactionDetails.setSourceMessage(bankAccountToBankAccount.getSourceMessage());
				transactionDetails.setAmount(amount);
				transactionDetails.setCc(channelCode);
				return nonRegisteredTransferInquiryHandler.handle(transactionDetails);
			}
			else{
				result.setNotificationCode(validationResult);
				return result;
			}*/
			
			result.setNotificationCode(CmFinoFIX.NotificationCode_DestinationMDNNotFound);
			return result;
		} 
		else if(!CmFinoFIX.ResponseCode_Success.equals(validationResult))
		{
			result.setNotificationCode(validationResult);
			SubscriberMDN rmdn = destinationMDN;
			if(rmdn != null){
				String receiverAccountName = rmdn.getSubscriber().getFirstName() + " " + rmdn.getSubscriber().getLastName();
				result.setReceiverAccountName(receiverAccountName);
			}
			return result;
		}

	//	addCompanyANDLanguageToResult(sourceMDN, result);

		log.info("AgentTransferInquiryHandlerImpl destMdn="+destinationMDN+", destPocketCode="+transactionDetails.getDestPocketCode());
		
		Pocket destPocket = null;
		if (StringUtils.isNotBlank(transactionDetails.getDestPocketId())) {
			destPocket = pocketService.getById(Long.parseLong(transactionDetails.getDestPocketId()));
		}
		else {
			destPocket = pocketService.getDefaultPocket(destinationMDN, transactionDetails.getDestPocketCode());
		}
		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination pocket with id "+(destPocket!=null? destPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		if(srcPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)||destPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)){

			if(!systemParametersService.getBankServiceStatus())	{
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
				return result;
			}
		}

 		bankAccountToBankAccount.setDestPocketID(destPocket.getID());
 		transactionDetails.setDestinationPocketId(destPocket.getID());
 		result = bankTransferInquiryHandler.handle(transactionDetails);
 		log.info("END handle :: AgentTransferInquiryHandlerImpl");
		return result;

	}
}
