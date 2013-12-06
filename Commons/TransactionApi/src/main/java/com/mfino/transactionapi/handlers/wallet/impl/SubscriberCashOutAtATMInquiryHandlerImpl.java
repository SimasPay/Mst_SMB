package com.mfino.transactionapi.handlers.wallet.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.mfino.fix.CmFinoFIX.CMCashOutAtATMInquiry;
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

/**
 *
 * @author Bala sunku
 */
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
	private TransactionChargingService transactionChargingService ;
	
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
	
	public Result handle(TransactionDetails transactionDetails)
	{
		log.info("Extracting data from transactionDetails in SubscriberCashOutAtATMInquiryHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		CMCashOutAtATMInquiry cashOutInquiry = new CMCashOutAtATMInquiry();
		ChannelCode cc = transactionDetails.getCc();
		cashOutInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		cashOutInquiry.setPin(transactionDetails.getSourcePIN());
		cashOutInquiry.setAmount(transactionDetails.getAmount());
		cashOutInquiry.setSourceApplication(cc.getChannelSourceApplication());
		cashOutInquiry.setChannelCode(cc.getChannelCode());
		cashOutInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		cashOutInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		cashOutInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		log.info("Handling Subscriber CashOut At ATM Inquiry webapi request::From " + cashOutInquiry.getSourceMDN() + 
				 " For Amount = " + cashOutInquiry.getAmount());
		XMLResult result = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_CashOutInquiry,cashOutInquiry.DumpFields());
		cashOutInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(cashOutInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		
		BigDecimal maxAmount = systemParametersService.getBigDecimal(SystemParameterKeys.MAX_VALUE_OF_CASHOUT_AT_ATM);
		if (cashOutInquiry.getAmount().compareTo(maxAmount) > 0) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_MaxValueOfCashOutAtATM);
			log.info("Exceded the Max amount allowed for Cash out at ATM");
			result.setMaxAmount(maxAmount);
			return result;
		}

		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(cashOutInquiry.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+cashOutInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Pocket srcSubscriberPocket = pocketService.getDefaultPocket(srcSubscriberMDN, transactionDetails.getSourcePocketCode());
		validationResult = transactionApiValidationService.validateSourcePocket(srcSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(srcSubscriberPocket);
		result.setPocketList(pocketList);
		
		log.info("getting third partner from system parameter 'thirdparty.partner.mdn' ");
		String ATMPartnerMDN = systemParametersService.getString(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
		if (StringUtils.isBlank(ATMPartnerMDN)) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			log.info("Third Party Partner MDN Value in System Parameters is Null");
			return result;
		}

		SubscriberMDN destPartnerMDN = subscriberMdnService.getByMDN(ATMPartnerMDN);
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
			log.error("Destination pocket with id "+(destPocket!=null? destPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		// add service charge to amount

		log.info("creating the serviceCharge object....");
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getID());
		sc.setDestMDN(destPartnerMDN.getMDN());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM);
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		sc.setSourceMDN(srcSubscriberMDN.getMDN());
		sc.setTransactionAmount(cashOutInquiry.getAmount());
		sc.setTransactionLogId(cashOutInquiry.getTransactionID());
		sc.setTransactionIdentifier(cashOutInquiry.getTransactionIdentifier());
		
		//For Hierarchy
		validationResult = hierarchyService.validate(srcSubscriberMDN.getSubscriber(), destPartnerMDN.getSubscriber(), 
				sc.getServiceName(), sc.getTransactionTypeName());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			log.info("Due to DCT Restrictions the Transaction " + ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM + " Is Failed");
			return result;
		}
		
		Transaction transaction=null;
		try{
			transaction =transactionChargingService.getCharge(sc);
			cashOutInquiry.setAmount(transaction.getAmountToCredit());
			cashOutInquiry.setCharges(transaction.getAmountTowardsCharges());

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

		cashOutInquiry.setDestMDN(destPartnerMDN.getMDN());
		cashOutInquiry.setSourcePocketID(srcSubscriberPocket.getID());
		cashOutInquiry.setDestPocketID(destPocket.getID());
		cashOutInquiry.setServiceChargeTransactionLogID(sctl.getID());

		log.info("sending the cashOutInquiry request to backend for processing");
		CFIXMsg response = super.process(cashOutInquiry);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());

		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}
		if (!transactionResponse.isResult() && sctl!=null) 
		{
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}

		result.setSctlID(sctl.getID());
		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		addCompanyANDLanguageToResult(srcSubscriberMDN,result);
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		return result;
	}
}
