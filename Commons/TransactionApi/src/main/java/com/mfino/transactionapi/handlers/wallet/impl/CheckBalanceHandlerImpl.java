/**
 * 
 */
package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountBalanceInquiry;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.CheckBalanceHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.BankAccountCheckBalanceXMLResult;
import com.mfino.transactionapi.result.xmlresulttypes.nfc.NFCCardDetailsXMLResult;
import com.mfino.transactionapi.result.xmlresulttypes.nfc.NFCXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Deva
 * 
 */
@Service("CheckBalanceHandlerImpl")
public class CheckBalanceHandlerImpl extends FIXMessageHandler implements CheckBalanceHandler{

	private static Logger log = LoggerFactory.getLogger(CheckBalanceHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

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
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	//FIXME EMoney check balance can be done here itself instead of sending the request to Multix
	//FIXME provision is added in dispatcherservlet as bank checkbalance and emoney checkbalance
	//FIXME EMoney check balance needs to go to emoney partners like for bank
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in CheckBalanceHandlerImpl for sourceMDN: "+transactionDetails.getSourceMDN());
		ChannelCode cc = transactionDetails.getCc();

		CMBankAccountBalanceInquiry bankAccountBalanceInquiry = new CMBankAccountBalanceInquiry();
		bankAccountBalanceInquiry.setPin(transactionDetails.getSourcePIN());
		bankAccountBalanceInquiry.setServletPath(CmFinoFIX.ServletPath_BankAccount);
		bankAccountBalanceInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		bankAccountBalanceInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		bankAccountBalanceInquiry.setChannelCode(cc.getChannelcode());
		bankAccountBalanceInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		bankAccountBalanceInquiry.setCardPAN(transactionDetails.getCardPAN());
		bankAccountBalanceInquiry.setCardAlias(transactionDetails.getCardAlias());
		bankAccountBalanceInquiry.setTransactionTypeName(transactionDetails.getTransactionName());
		log.info("Handling Bank Account Balance Enquiry WebAPI request");

		XMLResult result = new BankAccountCheckBalanceXMLResult();
		if(transactionDetails.getTransactionName().equals(ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_BALANCE))
		{
			result = new NFCCardDetailsXMLResult();
		}
		else if(transactionDetails.getServiceName().equals(ServiceAndTransactionConstants.SERVICE_NFC))
		{
			result = new NFCXMLResult();
		}

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BankAccountBalanceInquiry, bankAccountBalanceInquiry.DumpFields());
		bankAccountBalanceInquiry.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(bankAccountBalanceInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
		result.setTransID(transactionDetails.getTransID());
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(bankAccountBalanceInquiry.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+bankAccountBalanceInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		addCompanyANDLanguageToResult(srcSubscriberMDN, result);
		Pocket srcSubscriberPocket = null;

		/* 
		 * For NFC Card Balance, CardPAN can be null, in which case we don't need to create a pocket and validate it.
		 */
		if( !(transactionDetails.getTransactionName().equals(ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_BALANCE) 
				&& StringUtils.isBlank(transactionDetails.getCardPAN()) && StringUtils.isBlank(transactionDetails.getCardAlias())) )
		{
			if (transactionDetails != null 
					&& ( StringUtils.isNotBlank(transactionDetails.getCardPAN()) || StringUtils.isNotBlank(transactionDetails.getCardAlias()) ) 
					&& CmFinoFIX.PocketType_NFC.toString().equals(transactionDetails.getSourcePocketCode())) {
				PocketQuery pquery = new PocketQuery();
				pquery.setCardPan(transactionDetails.getCardPAN());
				pquery.setCardAlias(transactionDetails.getCardAlias());
				pquery.setPocketType(CmFinoFIX.PocketType_NFC);
				pquery.setMdnIDSearch(srcSubscriberMDN.getId().longValue());
				List<Pocket> lstPockets = pocketService.get(pquery);
				if (CollectionUtils.isNotEmpty(lstPockets)) {
					srcSubscriberPocket = lstPockets.get(0);
				}
				bankAccountBalanceInquiry.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
			}
			else {
				srcSubscriberPocket = pocketService.getDefaultPocket(srcSubscriberMDN, transactionDetails.getSourcePocketCode());
			}
			
			if(srcSubscriberPocket != null)
			{
				bankAccountBalanceInquiry.setPocketID(srcSubscriberPocket.getId().longValue());
				result.setCardPan(srcSubscriberPocket.getCardpan());
				result.setCardAlias(srcSubscriberPocket.getCardalias());
			}

			validationResult = transactionApiValidationService.validateSourcePocket(srcSubscriberPocket);
			if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
				log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getId():null)+" has failed validations");
				result.setNotificationCode(validationResult);
				return result;
			}

			if(srcSubscriberPocket.getPocketTemplate().getType()==(CmFinoFIX.PocketType_BankAccount))
			{
				if(!systemParametersService.getBankServiceStatus())
				{
					log.info("The bank service is down as set in the system parameter");
					result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
					return result;
				}
			}
		}
		log.info("creating the serviceCharge object....");
		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(bankAccountBalanceInquiry.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(transactionDetails.getServiceName());
		sc.setTransactionTypeName(transactionDetails.getTransactionName());
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionsLog.getID());
		sc.setTransactionIdentifier(bankAccountBalanceInquiry.getTransactionIdentifier());

		try{
			transaction =transactionChargingService.getCharge(sc);
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		bankAccountBalanceInquiry.setServiceChargeTransactionLogID(sctl.getID());
		if (StringUtils.isNotBlank(transactionDetails.getTransID())) {
			sctl.setIntegrationTransactionID(new Long(transactionDetails.getTransID()));
		}

		log.info("sending the request to backend for processing");
		CFIXMsg response = super.process(bankAccountBalanceInquiry);

		if(transactionDetails.getTransactionName().equals(ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_BALANCE))
		{

			if (response instanceof CmFinoFIX.CMBalanceInquiryFromBank) {
				log.info("Got the Balancy Inquiry From NFC");
				if (sctl != null) {
					sctl.setCalculatedCharge(BigDecimal.ZERO);
					transactionChargingService.completeTheTransaction(sctl);
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
				}
				CmFinoFIX.CMBalanceInquiryFromBank balanceInquiryResponseFromBank = (CmFinoFIX.CMBalanceInquiryFromBank) response;
				result.setNotificationCode(CmFinoFIX.NotificationCode_NFCCardBalanceDetails);
				if(balanceInquiryResponseFromBank.getEntries() != null)
				{
					List<CmFinoFIX.CMBalanceInquiryFromBank.CGEntries> nfcCardBalances = Arrays.asList(balanceInquiryResponseFromBank.getEntries());
					for(CmFinoFIX.CMBalanceInquiryFromBank.CGEntries nfcCardBalance : nfcCardBalances)
					{
						Pocket pocket = pocketService.getByCardPan(nfcCardBalance.getSourceCardPAN());
						if(pocket != null)
						{
							nfcCardBalance.setCardAlias(pocket.getCardalias());
						}
					}

					if (CollectionUtils.isNotEmpty(nfcCardBalances)) {
						result.setNfcCardBalances(nfcCardBalances);
					} else {
						result.setNotificationCode(CmFinoFIX.NotificationCode_NoAssociatedNFCCardsForGivenMDN);
					}
				}
				else
				{
					result.setNotificationCode(CmFinoFIX.NotificationCode_NoAssociatedNFCCardsForGivenMDN);
				}
			}
			else {
				log.info("Error: While fetching NFC Card Balance");
				result.setNotificationCode(CmFinoFIX.NotificationCode_BankAccountGetTransactionsFailed);
				result.setMultixResponse(response);
				if(sctl!=null){
					transactionChargingService.failTheTransaction(sctl, MessageText._("Error: While fetching NFC Card balance"));
				}
			}
		}
		else
		{
			// Changing the Service_charge_transaction_log status based on the response from Core engine. 
			TransactionResponse transactionResponse = checkBackEndResponse(response);

			log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

			if (transactionResponse.isResult() && sctl!=null) {
				sctl.setCalculatedCharge(BigDecimal.ZERO);
				transactionChargingService.completeTheTransaction(sctl);
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
			} else {
				String errorMsg = ((CMJSError) response).getErrorDescription();
				// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				if(sctl!=null){
					transactionChargingService.failTheTransaction(sctl, errorMsg);
				}
			}

			result.setAmount(transactionResponse.getAmount());
			result.setMessage(transactionResponse.getMessage());
			result.setMultixResponse(response);
		}
		result.setSctlID(sctl.getID());
		return result;
	}

}
