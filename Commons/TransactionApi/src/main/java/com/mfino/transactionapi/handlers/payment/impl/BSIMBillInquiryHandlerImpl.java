/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.transactionapi.handlers.payment.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MfsbillerPartnerMap;
import com.mfino.domain.MfsBiller;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
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
import com.mfino.fix.CmFinoFIX.CMBSIMBillPayInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.BillerService;
import com.mfino.service.MFSBillerService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.payment.BSIMBillInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * 
 * @author Maruthi
 */
@Service("BSIMBillInquiryHandlerImpl")
public class BSIMBillInquiryHandlerImpl extends FIXMessageHandler implements BSIMBillInquiryHandler{

	@Autowired
	@Qualifier("MFSBillerServiceImpl")
	private MFSBillerService mfsBillerService;
	
	private static Logger log = LoggerFactory.getLogger(BSIMBillInquiryHandlerImpl.class);
	
	@Autowired
	@Qualifier("BillerServiceImpl")
	private BillerService billerService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
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
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	private String serviceName = ServiceAndTransactionConstants.SERVICE_PAYMENT;
	private String transactionName = ServiceAndTransactionConstants.TRANSACTION_BILL_PAY;
	
	public Result handle(TransactionDetails transDetails)
	{
		CMBSIMBillPayInquiry billInquiry = new CMBSIMBillPayInquiry();
		ChannelCode cc = transDetails.getCc();
		
		billInquiry.setSourceMDN(transDetails.getSourceMDN());
		billInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		billInquiry.setChannelCode(cc.getChannelcode());
		billInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		billInquiry.setPin(transDetails.getSourcePIN());
		billInquiry.setSourceMessage(transDetails.getSourceMessage());
		billInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Bill_Inquiry);
		billInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		billInquiry.setInvoiceNumber(transDetails.getBillNum());
		billInquiry.setBillerCode(transDetails.getBillerCode());
		billInquiry.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		billInquiry.setPaymentMode(transDetails.getPaymentMode());
		billInquiry.setAmount(transDetails.getAmount());
		log.info("Handling Subscriber bill Inquiry webapi request");
		XMLResult result = new TransferInquiryXMLResult();

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BillInquiry, billInquiry.DumpFields());
		billInquiry.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(billInquiry);
		result.setTransactionTime(transactionsLog.getTransactiontime());
		

		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(billInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+billInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		if(StringUtils.isBlank(billInquiry.getInvoiceNumber())){//billInquiry.getInvoiceNumber().length() < 10) {
			result.setBillerCode(billInquiry.getBillerCode());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidDecoderNumber);
			return result;
		}
		
		MfsBiller mfsBiller = mfsBillerService.getByBillerCode(billInquiry.getBillerCode());
		if (mfsBiller == null) {
			result.setBillerCode(billInquiry.getBillerCode());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidBillerCode);
			return result;
		}
		
        Pocket subPocket = subscriberService.getDefaultPocket(srcSubscriberMDN.getId().longValue(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		if(subPocket==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
			return result;
		}
		if (!(subPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
			return result;
		}
		
		Partner agent = billerService.getPartner(billInquiry.getBillerCode());

		if(agent==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			result.setBillerCode(billInquiry.getBillerCode());
			result.setPartnerCode(billInquiry.getBillerCode());
			return result;
		}
		
		Subscriber agentsub = agent.getSubscriber();
		SubscriberMdn agentmdn = agentsub.getSubscriberMdns().iterator().next();	
		
		MfsbillerPartnerMap results = mfsBiller.getMfsbillerPartnerMaps().iterator().next();
		if(results != null){
			billInquiry.setIntegrationCode(results.getIntegrationcode());
		}
		
		billInquiry.setSourcePocketID(subPocket.getId().longValue());
		Pocket srcPocket = pocketService.getById(new Long(billInquiry.getSourcePocketID()));

		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setDestMDN(agentmdn.getMdn());
		sc.setServiceName(serviceName);
		sc.setTransactionTypeName(transactionName);
		sc.setSourceMDN(srcSubscriberMDN.getMdn());
		sc.setTransactionAmount(BigDecimal.ZERO);//ONLINE BILLPAY
		if(StringUtils.isNotBlank(billInquiry.getAmount().toString()))
			sc.setTransactionAmount(billInquiry.getAmount());//Case where inquiry with amount sent to bank
		sc.setMfsBillerCode(billInquiry.getBillerCode());
		sc.setTransactionLogId(billInquiry.getTransactionID());
		sc.setInvoiceNo(billInquiry.getInvoiceNumber());
		sc.setTransactionIdentifier(billInquiry.getTransactionIdentifier());
		Pocket agentPocket;
		try {
			long servicePartnerId = transactionChargingService.getServiceProviderId(null);
			long serviceId = transactionChargingService.getServiceId("Payment");
			PartnerServices partnerService = transactionChargingService.getPartnerService(agent.getId().longValue(), servicePartnerId, serviceId);
			if (partnerService == null) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
				return result;
			}
			agentPocket = partnerService.getPocketByDestpocketid();
			if(agentPocket==null){
				result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
				return result;
			}
			if (!(agentPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
				return result;
			}
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting Source Pocket",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}
		if(subPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_BankAccount)||agentPocket.getPocketTemplateByPockettemplateid().getType()==(CmFinoFIX.PocketType_BankAccount))
		{

			if(!systemParametersService.getBankServiceStatus())
			{
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
				return result;
			}
		}
		billInquiry.setNarration("online");
		billInquiry.setSourceBankAccountNo(srcPocket.getCardpan());
		billInquiry.setSourcePocketID(srcPocket.getId().longValue());
		if(CmFinoFIX.BankAccountCardType_SavingsAccount.equals(srcPocket.getPocketTemplateByPockettemplateid().getBankaccountcardtype()))
	    billInquiry.setSourceBankAccountType(""+ CmFinoFIX.BankAccountType_Saving);
		billInquiry.setDestPocketID(agentPocket.getId().longValue());
		billInquiry.setDestMDN(agentmdn.getMdn());
		Transaction transaction=null;
		try{
			transaction =transactionChargingService.getCharge(sc);
			if(!(CmFinoFIX.PaymentMode_PackageType.equalsIgnoreCase(billInquiry.getPaymentMode()))){ 
			 	billInquiry.setAmount(transaction.getAmountToCredit()); 
			}
			billInquiry.setCharges(transaction.getAmountTowardsCharges());
			
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);//change to service not found
			//saveActivitiesLog(result, subscriberMDN);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);			
			return result;
		}
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		
		billInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());
		CFIXMsg response = super.process(billInquiry);
        
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		Long sctlId = Long.parseLong(transactionResponse.getPaymentInquiryDetails());
        
		  sctl = sctlService.getBySCTLID(sctlId);
		if (transactionResponse.getTransactionId()!=null) {
			sctl.setTransactionid(BigDecimal.valueOf(transactionResponse.getTransactionId()));
			sctl.setCommoditytransferid(BigDecimal.valueOf(transactionResponse.getTransferId()));
			billInquiry.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}
		if (!transactionResponse.isResult() && sctl!=null) 
		{
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}

		transactionChargingService.updateTransactionStatus(transactionResponse, sctl);
		
		result.setAdditionalInfo(transactionResponse.getAdditionalInfo());
		result.setSctlID(sctl.getId().longValue());
		result.setMultixResponse(response);
		result.setDebitAmount(sctl.getTransactionamount());
		result.setCreditAmount(sctl.getTransactionamount());
		result.setServiceCharge(sctl.getCalculatedcharge());
		result.setMultixResponse(response);
	//	addCompanyANDLanguageToResult(srcSubscriberMDN,result);
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		return result;
		
		
	}

}
