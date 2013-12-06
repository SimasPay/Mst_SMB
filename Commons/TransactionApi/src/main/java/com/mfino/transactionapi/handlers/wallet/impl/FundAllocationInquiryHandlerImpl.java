package com.mfino.transactionapi.handlers.wallet.impl;

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
import com.mfino.fix.CmFinoFIX.CMFundAllocationInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.FundValidationService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.FundAllocationInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
/**
 * 
 * @author Sreenath
 *
 */
@Service("FundAllocationInquiryHandlerImpl")
public class FundAllocationInquiryHandlerImpl extends FIXMessageHandler implements FundAllocationInquiryHandler{
	private static Logger log = LoggerFactory.getLogger(FundAllocationInquiryHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("FundValidationServiceImpl")
	private FundValidationService fundValidationService;
	
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
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	public Result handle(TransactionDetails transactionDetails)
	{
		log.info("Extracting data from transactionDetails in FundAllocationInquiryHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN()
				+"to"+transactionDetails.getDestMDN());
		ChannelCode cc = transactionDetails.getCc();
		CMFundAllocationInquiry fundAllocationInquiry = new CMFundAllocationInquiry();
		fundAllocationInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		fundAllocationInquiry.setPin(transactionDetails.getSourcePIN());
		fundAllocationInquiry.setAmount(transactionDetails.getAmount());
		fundAllocationInquiry.setSourceApplication(cc.getChannelSourceApplication());
		fundAllocationInquiry.setChannelCode(cc.getChannelCode());
		fundAllocationInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		fundAllocationInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		if(StringUtils.isBlank(transactionDetails.getOnBehalfOfMDN())){
			//self transfer
			fundAllocationInquiry.setWithdrawalMDN(transactionDetails.getSourceMDN());
		}else{
			fundAllocationInquiry.setWithdrawalMDN(subscriberService.normalizeMDN(transactionDetails.getOnBehalfOfMDN()));
	    }
		if(StringUtils.isNotEmpty(transactionDetails.getPartnerCode())){
			fundAllocationInquiry.setPartnerCode(transactionDetails.getPartnerCode());
		}
		else{
			fundAllocationInquiry.setPartnerCode(ANY_PARTNER);
		}
		String srcpocketcode=transactionDetails.getSourcePocketCode();
		fundAllocationInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		log.info("Handling Fund Allocation Inquiry webapi request::From " + fundAllocationInquiry.getSourceMDN() + "to "+fundAllocationInquiry.getWithdrawalMDN()+
				 " For Amount = " + fundAllocationInquiry.getAmount()+" usable at partner: "+fundAllocationInquiry.getPartnerCode());
		XMLResult result = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_FundAllocationInquiry,fundAllocationInquiry.DumpFields());
		fundAllocationInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(fundAllocationInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());


		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(fundAllocationInquiry.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+fundAllocationInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		Pocket srcSubscriberPocket = pocketService.getDefaultPocket(srcSubscriberMDN, srcpocketcode);
		validationResult = transactionApiValidationService.validateSourcePocket(srcSubscriberPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcSubscriberPocket!=null? srcSubscriberPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(srcSubscriberPocket);
		result.setPocketList(pocketList);
		//change here
		log.info("getting third partner from system parameter 'thirdparty.partner.mdn' ");
		String thirdPartyPartnerMDN = systemParametersService.getString(SystemParameterKeys.THIRDPARTY_PARTNER_MDN);
		if (StringUtils.isBlank(thirdPartyPartnerMDN)) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			log.info("Third Party Partner MDN Value in System Parameters is Null");
			return result;
		}

		SubscriberMDN destPartnerMDN = subscriberMdnService.getByMDN(thirdPartyPartnerMDN);
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
		
		log.info("Checking for fundDefintions and purpose");
		boolean purposeAndFundDefinitionCheck = fundValidationService.validatePurposeAndFundDefinition(fundAllocationInquiry.getPartnerCode());
		if(!purposeAndFundDefinitionCheck){
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidFundDefinitionOrPurpose);
			log.error("Purpose or fundDefinition invalid");
			return result;
		}
		
		// add service charge to amount

		log.info("creating the serviceCharge object....");
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getID());
		sc.setDestMDN(destPartnerMDN.getMDN());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_FUND_ALLOCATION);
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		sc.setSourceMDN(srcSubscriberMDN.getMDN());
		sc.setTransactionAmount(fundAllocationInquiry.getAmount());
		sc.setTransactionLogId(fundAllocationInquiry.getTransactionID());
		sc.setOnBeHalfOfMDN(fundAllocationInquiry.getWithdrawalMDN());
		sc.setTransactionIdentifier(fundAllocationInquiry.getTransactionIdentifier());
		
		//For Hierarchy
		validationResult = hierarchyService.validate(srcSubscriberMDN.getSubscriber(), destPartnerMDN.getSubscriber(), 
				sc.getServiceName(), sc.getTransactionTypeName());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			log.info("Due to DCT Restrictions the Transaction " + ServiceAndTransactionConstants.TRANSACTION_FUND_ALLOCATION + " Is Failed");
			return result;
		}
		
		Transaction transaction=null;
		try{
			transaction =transactionChargingService.getCharge(sc);
			fundAllocationInquiry.setAmount(transaction.getAmountToCredit());
			fundAllocationInquiry.setCharges(transaction.getAmountTowardsCharges());

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

		fundAllocationInquiry.setDestMDN(destPartnerMDN.getMDN());
		fundAllocationInquiry.setSourcePocketID(srcSubscriberPocket.getID());
		fundAllocationInquiry.setDestPocketID(destPocket.getID());
		fundAllocationInquiry.setServiceChargeTransactionLogID(sctl.getID());

		log.info("sending fundAllocationInquiry request to backend for processing");
		CFIXMsg response = super.process(fundAllocationInquiry);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		log.info("Got the response from backend .The notification code is : "+transactionResponse.getCode()+" and the result: "+transactionResponse.isResult());
		
		if (!transactionResponse.isResult() && sctl!=null) 
		{
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}
		if (transactionResponse.getTransactionId() !=null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}

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
