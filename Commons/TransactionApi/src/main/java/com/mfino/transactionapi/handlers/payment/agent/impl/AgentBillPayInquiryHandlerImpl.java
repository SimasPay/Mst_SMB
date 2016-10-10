/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.transactionapi.handlers.payment.agent.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.commons.hierarchyservice.HierarchyService;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MfsbillerPartnerMap;
import com.mfino.domain.MfsDenominations;
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
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.BillPaymentsService;
import com.mfino.service.BillerService;
import com.mfino.service.MFSBillerService;
import com.mfino.service.MFSDenominationsService;
import com.mfino.service.PartnerServicesService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.payment.agent.AgentBillPayInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * Currently this handler handles bill pay for DSTV alone, need to refactor for
 * any bill pay
 * 
 * @author mfino
 */
@Service("AgentBillPayInquiryHandlerImpl")
public class AgentBillPayInquiryHandlerImpl extends FIXMessageHandler implements AgentBillPayInquiryHandler{

	@Autowired
	@Qualifier("MFSBillerServiceImpl")
	private MFSBillerService mfsBillerService;
	
	@Autowired
	@Qualifier("MFSDenominationsServiceImpl")
	private MFSDenominationsService mfsDenominationsService;
	
	@Autowired
	@Qualifier("HierarchyServiceImpl")
	private HierarchyService hierarchyService;
	
	private static Logger	         log	           = LoggerFactory.getLogger(AgentBillPayInquiryHandlerImpl.class);
	private String serviceName = ServiceAndTransactionConstants.SERVICE_AGENT;
	private String transactionName = ServiceAndTransactionConstants.TRANSACTION_BILL_PAY;        
	
	@Autowired
	@Qualifier("BillerServiceImpl")
	private BillerService billerService;
	
	@Autowired
	@Qualifier("BillPaymentsServiceImpl")
	private BillPaymentsService billPaymentsService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("PartnerServicesServiceImpl")
	private PartnerServicesService partnerServicesService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	public Result handle(TransactionDetails transDetails) {
		CMBillPayInquiry	paymentInquiry	= new CMBillPayInquiry();
		ChannelCode cc = transDetails.getCc();
		
		paymentInquiry.setSourceMDN(transDetails.getSourceMDN());
		paymentInquiry.setBillerCode(transDetails.getBillerCode());
		paymentInquiry.setInvoiceNumber(transDetails.getBillNum());
		paymentInquiry.setPin(transDetails.getSourcePIN());
		paymentInquiry.setAmount(transDetails.getAmount());
		paymentInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		paymentInquiry.setChannelCode(cc.getChannelcode());
		paymentInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		paymentInquiry.setSourceMessage(transDetails.getSourceMessage());
		paymentInquiry.setMessageType(CmFinoFIX.MessageType_BillPayInquiry);
		paymentInquiry.setOnBeHalfOfMDN(transDetails.getDestMDN());
		paymentInquiry.setParentTransactionID(0L);
		paymentInquiry.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		
		
		log.info("Handling agent bill pay Inquiry webapi request for mdn" + paymentInquiry.getSourceMDN());
		XMLResult result = new TransferInquiryXMLResult();

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AgenDSTVPaymentInquiry, paymentInquiry.DumpFields());
		log.info("creating and saving transactionslog with id " + transactionsLog.getId());
		paymentInquiry.setTransactionID(transactionsLog.getId().longValue());
		result.setTransactionID(transactionsLog.getId().longValue());
		result.setSourceMessage(paymentInquiry);
		result.setTransactionTime(transactionsLog.getTransactiontime());

		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(paymentInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateAgentMDN(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+paymentInquiry.getSourceMDN()+" has failed agent validations");
			result.setNotificationCode(validationResult);
			return result;

		}

		Partner billPartner = billerService.getPartner(paymentInquiry.getBillerCode());

		if(billPartner==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			return result;
		}
		
		Subscriber billPartnerSub = billPartner.getSubscriber();
		SubscriberMdn billPartnerMdn = billPartnerSub.getSubscriberMdns().iterator().next();

		paymentInquiry.setEmail(srcSubscriberMDN.getSubscriber().getEmail());
		//paymentInquiry.setPartnerBillerCode(billPartner.getMFSBillerPartnerFromPartnerID().iterator().next().getPartnerBillerCode());
		
		MfsBiller mfsBiller = mfsBillerService.getByBillerCode(paymentInquiry.getBillerCode());
		MfsbillerPartnerMap results = mfsBiller.getMfsbillerPartnerMaps().iterator().next();
		if(results != null){
			paymentInquiry.setIntegrationCode(results.getIntegrationcode());
			paymentInquiry.setPartnerBillerCode(results.getPartnerbillercode());
			if (CmFinoFIX.BillerPartnerType_Topup_Denomination.equals(results.getBillerpartnertype())) {

				//			if(results.getBillerPartnerType().intValue() == CmFinoFIX.BillerPartnerType_Topup_Denomination.intValue()){
				
				MFSDenominationsQuery mdquery = new MFSDenominationsQuery();
				mdquery.setMfsID(results.getId().longValue());
				List<MfsDenominations> res  = mfsDenominationsService.get(mdquery);
				if(res.size() > 0){
					boolean isValid = false;
					StringBuffer validDenominations = new StringBuffer();
					for(int i=0; i < res.size(); i++){
						if(res.get(i).getDenominationamount().compareTo(paymentInquiry.getAmount()) == 0 ){
							paymentInquiry.setPartnerBillerCode(res.get(i).getProductcode());
							isValid=true;
						}
						validDenominations.append((res.get(i).getDenominationamount().setScale(2, RoundingMode.HALF_EVEN)).toString()+" ");
					}
					if(isValid == false){
						result.setBillAmount(paymentInquiry.getAmount().setScale(2, RoundingMode.HALF_EVEN));
						result.setValidDenominations(validDenominations.toString());
						result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidTopupDenomination);
						return result;
					}
				}
			}
		}
		
		
		
		
		log.info("calculating and adding service charges to the trnasactions " + transactionsLog.getId());
		// add service charge to amount
		ServiceCharge sc = new ServiceCharge();
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setDestMDN(billPartnerMdn.getMdn());
		sc.setServiceName(getServiceName());
		sc.setOnBeHalfOfMDN(paymentInquiry.getOnBeHalfOfMDN());
		if(transDetails.getTransactionTypeName() != null && !transDetails.getTransactionTypeName().isEmpty()){
			sc.setTransactionTypeName(transDetails.getTransactionTypeName());
		}else{
			sc.setTransactionTypeName(getTransactionName());
		}
		sc.setSourceMDN(srcSubscriberMDN.getMdn());        
		sc.setTransactionAmount(paymentInquiry.getAmount());
		sc.setMfsBillerCode(paymentInquiry.getBillerCode());
		sc.setTransactionLogId(paymentInquiry.getTransactionID());
		sc.setInvoiceNo(paymentInquiry.getInvoiceNumber());
		sc.setTransactionIdentifier(paymentInquiry.getTransactionIdentifier());


		PartnerServices partnerService = null;
		long servicePartnerId = -1;
		long serviceId = -1;
		try {
			servicePartnerId = transactionChargingService.getServiceProviderId(null);
			serviceId = transactionChargingService.getServiceId(sc.getServiceName());
		}
		catch (InvalidServiceException e) {
			log.error("Service is not avaiable", e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}
		partnerService = transactionChargingService.getPartnerService(billPartner.getId().longValue(), servicePartnerId, serviceId);
		log.info("checking whether the partner has the required service enabled for him ");
		if (partnerService == null) {
			log.info("service=" + sc.getServiceName() + " is not available for the partner");
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
			return result;
		}
		//Pocket srcAgentPocket = partnerService.getPocketByDestPocketID();
 		Pocket srcAgentPocket = pocketService.getDefaultPocket(srcSubscriberMDN, transDetails.getSourcePocketCode());
		if (!isSourcePocketValid(srcAgentPocket, result, srcSubscriberMDN))
			return result;
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(srcAgentPocket);
		result.setPocketList(pocketList);
		log.info("source pocket ID of the agent="+srcAgentPocket.getId());

		long billerServiceId = -1;
		try {
			billerServiceId = transactionChargingService.getServiceId(ServiceAndTransactionConstants.SERVICE_PAYMENT);
		}
		catch (InvalidServiceException e) {
			log.error("Payment Service is not avaiable for Biller", e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}
		
		PartnerServices billerPartnerService = partnerServicesService.getPartnerServices(billPartner.getId().longValue(), servicePartnerId, billerServiceId);
		if (billerPartnerService == null) {
			log.warn("service=" + ServiceAndTransactionConstants.SERVICE_PAYMENT + " is not available for the biller partner");
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner);
			return result;
		}
		Pocket destBillerPartnerPocket = billerPartnerService.getPocketByDestpocketid();
		if(!isDestPocketValid(destBillerPartnerPocket, result, destBillerPartnerPocket.getSubscriberMdn()))
			return result;
		log.info("ID of the incoming funds pocket for agent services="+destBillerPartnerPocket.getId());
		
		if(srcAgentPocket.getPocketTemplate().getType()==(CmFinoFIX.PocketType_BankAccount)||destBillerPartnerPocket.getPocketTemplate().getType()==(CmFinoFIX.PocketType_BankAccount))
		{
			if(!systemParametersService.getBankServiceStatus())
			{
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
				return result;
			}
		}

		Transaction transaction = null;
		try {
			transaction = transactionChargingService.getCharge(sc);
			paymentInquiry.setAmount(transaction.getAmountToCredit());
			paymentInquiry.setCharges(transaction.getAmountTowardsCharges());
		}
		catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges", e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		
		SubscriberMdn destinationMDN = subscriberMdnService.getByMDN(paymentInquiry.getOnBeHalfOfMDN());
		validationResult = hierarchyService.validate(srcSubscriberMDN.getSubscriber(), ((destinationMDN != null) ? destinationMDN.getSubscriber() : null), sc.getServiceName(), sc.getTransactionTypeName());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			result.setNotificationCode(validationResult);
			return result;
		}
		
		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		sctl.setIntegrationcode(paymentInquiry.getIntegrationCode());
		
		BillPaymentsQuery query = new BillPaymentsQuery();
		query.setSctlID(sctl.getId().longValue());
		List<BillPayments> billPayments = billPaymentsService.get(query);
		if(billPayments != null && billPayments.size() > 0)
		{
			BillPayments billPayment = billPayments.get(0);
			billPayment.setInfo1(paymentInquiry.getPaymentInquiryDetails());
			billPaymentsService.save(billPayment);
		}
		
		paymentInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());
		paymentInquiry.setDestMDN(billPartnerMdn.getMdn());
		paymentInquiry.setCharges(transaction.getAmountTowardsCharges());
		paymentInquiry.setChannelCode(cc.getChannelcode());
		paymentInquiry.setSourcePocketID(srcAgentPocket.getId().longValue());
		paymentInquiry.setDestPocketID(destBillerPartnerPocket.getId().longValue());
		paymentInquiry.setSourceApplication((int)cc.getChannelsourceapplication());

		log.info("sending request to backend");
		CFIXMsg response = super.process(paymentInquiry);

		// Saves the Transaction Id returned from Back End
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (transactionResponse.getTransactionId() != null) {
			sctl.setTransactionid(BigDecimal.valueOf(transactionResponse.getTransactionId().longValue()));
			sctl.setCommoditytransferid(BigDecimal.valueOf(transactionResponse.getTransferId()));
			paymentInquiry.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}

		result.setSctlID(sctl.getId().longValue());
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

	public boolean isSourcePocketValid(Pocket srcPocket, XMLResult result, SubscriberMdn agentMDN) {

		if (srcPocket == null) {
			log.info("sourcepocket is null");
			result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
			return false;
		}
		if (!(srcPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
			log.info("sourcepocket with id= " + srcPocket.getId() + " is not active");
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
			return false;
		}
		return true;
	}

	private boolean isDestPocketValid(Pocket destPocket, XMLResult result, SubscriberMdn subMDN) {
		if (destPocket == null) {
			log.info("the service of the partner doesn't have a source pocket");
			result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
			return false;
		}
		if (!(destPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
			log.info("the source pocket with ID=" + destPocket.getId() + " of the service is not active");
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
			return false;
		}
		return true;
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
}
