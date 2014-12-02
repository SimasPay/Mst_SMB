/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.transactionapi.handlers.payment.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MFSBiller;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.MFSDenominations;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
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
import com.mfino.fix.CmFinoFIX.CMBillInquiry;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.BillPaymentsService;
import com.mfino.service.BillerService;
import com.mfino.service.MFAService;
import com.mfino.service.MFSBillerPartnerMapService;
import com.mfino.service.MFSBillerService;
import com.mfino.service.MFSDenominationsService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.payment.BillPayInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * Currently this handler handles bill pay for any bill pay
 * 
 * @author Chaitanya
 */
@Service("BillPayInquiryHandlerImpl")
public class BillPayInquiryHandlerImpl extends FIXMessageHandler implements BillPayInquiryHandler{

	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("BillerServiceImpl")
	private BillerService billerService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("MFSBillerServiceImpl")
	private MFSBillerService mfsBillerService;
	
	@Autowired
	@Qualifier("MFSBillerPartnerMapServiceImpl")
	private MFSBillerPartnerMapService mfsBillerPartnerMapService;
	
	@Autowired
	@Qualifier("BillPaymentsServiceImpl")
	private BillPaymentsService billPaymentsService;	

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("MFSDenominationsServiceImpl")
	private MFSDenominationsService mfsDenominationsService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	private static Logger log = LoggerFactory.getLogger(BillPayInquiryHandlerImpl.class);
	
	public Result handle(TransactionDetails transactionDetails){
		CFIXMsg response = null;
		TransactionResponse transactionResponse = null;
		String paymentRequestDetails = null, operatorMsg = null;
		String destUserName = null, additionalInfo = null, billPayRefID = null;
		BigDecimal operatorChgs = BigDecimal.ZERO;
		String srcpocketcode;
		CMBillPayInquiry billPaymentInquiry= new CMBillPayInquiry();
		ChannelCode cc = transactionDetails.getCc();
		
		billPaymentInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		//Change as part of migration to include old parameter names
		if (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY.equalsIgnoreCase(transactionDetails.getTransactionName())
				&& StringUtils.isNotBlank(transactionDetails.getDestMDN())) {
			billPaymentInquiry.setInvoiceNumber(transactionDetails.getDestMDN());
		}
		else {
			billPaymentInquiry.setInvoiceNumber(transactionDetails.getBillNum());
		}
		billPaymentInquiry.setPin(transactionDetails.getSourcePIN());
		//Change as part of migration to include old parameter names
		if (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY.equalsIgnoreCase(transactionDetails.getTransactionName())
				&& StringUtils.isNotBlank(transactionDetails.getCompanyID())) {
			billPaymentInquiry.setBillerCode(transactionDetails.getCompanyID());
		}
		else {
			billPaymentInquiry.setBillerCode(transactionDetails.getBillerCode());
		}
		billPaymentInquiry.setAmount(transactionDetails.getAmount());
		billPaymentInquiry.setSourceApplication(cc.getChannelSourceApplication());
		billPaymentInquiry.setChannelCode(cc.getChannelCode());
		billPaymentInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		billPaymentInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		billPaymentInquiry.setMessageType(CmFinoFIX.MessageType_BillPayInquiry);
		billPaymentInquiry.setBenOpCode(transactionDetails.getBenOpCode());
		billPaymentInquiry.setNarration(transactionDetails.getNarration());
		billPaymentInquiry.setParentTransactionID(0L);
		if (ServiceAndTransactionConstants.MESSAGE_BILL_PAY.equals(transactionDetails.getSourceMessage())) {
			billPaymentInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Bill_Payment);
		} 
		else {
			billPaymentInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Bill_Payment_Topup);
		}
		
		//For Bayar.Net BillPayments
		if(transactionDetails.getDenomCode()!=null)
			billPaymentInquiry.setDenominationCode(transactionDetails.getDenomCode());
		if(transactionDetails.getNominalAmount()!=null)
			billPaymentInquiry.setNominalAmount(transactionDetails.getNominalAmount());
		
		srcpocketcode=transactionDetails.getSourcePocketCode();
        billPaymentInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
        billPaymentInquiry.setPaymentMode(transactionDetails.getPaymentMode());
        if (ServiceAndTransactionConstants.TRANSACTION_INTER_EMONEY_TRANSFER_INQUIRY.equalsIgnoreCase(transactionDetails.getTransactionName())) {
            String nibssCode = systemParametersService.getString(SystemParameterKeys.NIBSS_INTER_EMONEY_TRANSFER_CODE);		
            billPaymentInquiry.setBillerCode(nibssCode);
		}
		log.info("Handling Subscriber bill pay Inquiry webapi request");
		XMLResult result = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BillPayInquiry, billPaymentInquiry.DumpFields());
		billPaymentInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(billPaymentInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());

		SubscriberMDN sourceMDN = subscriberMdnService.getByMDN(billPaymentInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+billPaymentInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		MFSBiller mfsBiller = mfsBillerService.getByBillerCode(billPaymentInquiry.getBillerCode());
		if (mfsBiller == null) {
			result.setBillerCode(billPaymentInquiry.getBillerCode());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidBillerCode);
			return result;
		}
		
		//For Integration Code
		MFSBillerPartner mfsBillerPartner = mfsBillerPartnerMapService.getByBillerCode(billPaymentInquiry.getBillerCode());
		if (mfsBillerPartner != null){
			billPaymentInquiry.setIntegrationCode(mfsBillerPartner.getIntegrationCode());
		}
		
		Pocket subPocket = pocketService.getDefaultPocket(sourceMDN, srcpocketcode);
		validationResult = transactionApiValidationService.validateSourcePocket(subPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(subPocket!=null? subPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		billPaymentInquiry.setSourceBankAccountNo(subPocket.getCardPAN()); // Storing SourceCardPAN in SourceBankAccNo to be used later while sending request to BSM
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(subPocket);
		result.setPocketList(pocketList);
		Partner partner = billerService.getPartner(billPaymentInquiry.getBillerCode());

		SubscriberMDN partnerMDN = partner.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
		validationResult = transactionApiValidationService.validatePartnerMDN(partnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination partner has failed validations");
			validationResult = processValidationResultForPartner(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		billPaymentInquiry.setEmail(sourceMDN.getSubscriber().getEmail());
		
		MFSBillerPartner results = mfsBiller.getMFSBillerPartnerFromMFSBillerId().iterator().next();
		if(results != null){
			billPaymentInquiry.setIntegrationCode(results.getIntegrationCode());
			billPaymentInquiry.setPartnerBillerCode(results.getPartnerBillerCode());
			billPaymentInquiry.setChargesIncluded(results.getChargesIncluded());
			if(CmFinoFIX.BillerPartnerType_Topup_Denomination.equals(results.getBillerPartnerType())){
				MFSDenominationsQuery mdquery = new MFSDenominationsQuery();
				mdquery.setMfsID(results.getID());
				List<MFSDenominations> res  = mfsDenominationsService.get(mdquery);
				if(res.size() > 0){
					boolean isValid = false;
					StringBuffer validDenominations = new StringBuffer();
					for(int i=0; i < res.size(); i++){
						if(res.get(i).getDenominationAmount().compareTo(billPaymentInquiry.getAmount()) == 0 ){
							billPaymentInquiry.setPartnerBillerCode(res.get(i).getProductCode());
							isValid=true;
						}
						validDenominations.append((res.get(i).getDenominationAmount().setScale(2, RoundingMode.HALF_EVEN)).toString()+" ");
					}
					if(isValid == false){
						result.setBillAmount(billPaymentInquiry.getAmount().setScale(2, RoundingMode.HALF_EVEN));
						result.setValidDenominations(validDenominations.toString());
						result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidTopupDenomination);
						return result;
					}
				}
			}
		}
		addCompanyANDLanguageToResult(sourceMDN, result);
		if((StringUtils.isNotBlank(transactionDetails.getPaymentMode())) && 
				(CmFinoFIX.PaymentMode_HubZeroAmount.equalsIgnoreCase(transactionDetails.getPaymentMode()) || CmFinoFIX.PaymentMode_HubFreeAmount.equalsIgnoreCase(transactionDetails.getPaymentMode()) 
						|| CmFinoFIX.PaymentMode_ZeroAmount.equalsIgnoreCase(transactionDetails.getPaymentMode()) )) {
			billPaymentInquiry.setNarration("online");
			// Getting the Bill amount for the given online transaction.
			response = doBillInquiry(billPaymentInquiry); 
			transactionResponse = checkBackEndResponse(response);
			
			if(transactionResponse != null && transactionResponse.isResult()) {
				billPaymentInquiry.setAmount(transactionResponse.getAmount());
				paymentRequestDetails = transactionResponse.getPaymentInquiryDetails();
				if(transactionResponse.getCharges() != null)
					operatorChgs = transactionResponse.getCharges();
				if(transactionResponse.getDestinationUserName() != null)
					destUserName = transactionResponse.getDestinationUserName();
				if(transactionResponse.getAdditionalInfo() != null)
					additionalInfo = transactionResponse.getAdditionalInfo();
				if(transactionResponse.getBillPaymentReferenceID() != null)
					billPayRefID = transactionResponse.getBillPaymentReferenceID();
				if(transactionResponse.getOperatorMsg() != null)
					operatorMsg = transactionResponse.getOperatorMsg();
			}
			else {
				result.setCode(transactionResponse.getCode());
				result.setMessage(transactionResponse.getMessage());
				result.setMultixResponse(response);
				if(transactionResponse.getOperatorMsg() != null) {
					result.setResponseMessage(transactionResponse.getOperatorMsg());
					result.setMessage(transactionResponse.getOperatorMsg());
				}
				return result;
			}
		}
		if(CmFinoFIX.PaymentMode_HubFullAmount.equalsIgnoreCase(transactionDetails.getPaymentMode()) &&
				billPaymentInquiry.getNominalAmount() != null)  {
			operatorChgs = billPaymentInquiry.getAmount().subtract(billPaymentInquiry.getNominalAmount());
		}
		
		if( (transactionDetails.getNominalAmount()==null) || (transactionDetails.getNominalAmount()!=null && transactionDetails.getNominalAmount().compareTo(BigDecimal.ZERO) <1) )
			billPaymentInquiry.setNominalAmount(billPaymentInquiry.getAmount().subtract(operatorChgs));
		// add service charge to amount

		ServiceCharge serviceCharge=new ServiceCharge();
		serviceCharge.setChannelCodeId(cc.getID());
		serviceCharge.setDestMDN(partnerMDN.getMDN());
		serviceCharge.setServiceName(transactionDetails.getServiceName());//change to service
		serviceCharge.setTransactionTypeName(transactionDetails.getTransactionTypeName());
		serviceCharge.setSourceMDN(sourceMDN.getMDN());
		serviceCharge.setTransactionAmount(billPaymentInquiry.getAmount()!=null ? billPaymentInquiry.getAmount() : BigDecimal.ZERO);
		serviceCharge.setMfsBillerCode(billPaymentInquiry.getBillerCode());
		serviceCharge.setTransactionLogId(billPaymentInquiry.getTransactionID());
		serviceCharge.setInvoiceNo(billPaymentInquiry.getInvoiceNumber());
		serviceCharge.setOnBeHalfOfMDN(StringUtils.isNotBlank(billPaymentInquiry.getOnBeHalfOfMDN()) ? billPaymentInquiry.getOnBeHalfOfMDN() : StringUtils.EMPTY);
		serviceCharge.setTransactionIdentifier(billPaymentInquiry.getTransactionIdentifier());
		
		Pocket agentPocket;
		try {
			long servicePartnerId = transactionChargingService.getServiceProviderId(null);
			long serviceId = transactionChargingService.getServiceId(serviceCharge.getServiceName());
			PartnerServices partnerService = transactionChargingService.getPartnerService(partner.getID(), servicePartnerId, serviceId);
			if (partnerService == null) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
				return result;
			}
			agentPocket = partnerService.getPocketByDestPocketID();
			if(agentPocket==null){
				result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
				return result;
			}
			if (!agentPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
				return result;
			}
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting Source Pocket",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}
		if(subPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)||agentPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)){

			if(!systemParametersService.getBankServiceStatus())	{
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
				return result;
			}
		}
		Transaction transaction=null;
		try{
			transaction =transactionChargingService.getCharge(serviceCharge);
			billPaymentInquiry.setAmount(transaction.getAmountToCredit());
			billPaymentInquiry.setCharges(transaction.getAmountTowardsCharges());

		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);//change to service not found
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);			
			return result;
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		sctl.setIntegrationCode(billPaymentInquiry.getIntegrationCode());
		
		//This part of code is never called for existing bill payments in dimo/hub
/*		BillPaymentsQuery query = new BillPaymentsQuery();
		query.setSctlID(sctl.getID());
		List<BillPayments> billPayments = billPaymentsService.get(query);
		if(billPayments != null && billPayments.size() > 0){
			BillPayments billPayment = billPayments.get(0);
			billPayment.setInfo1(billPaymentInquiry.getPaymentInquiryDetails());
			log.info("Check for ChargesInclusion"+results.getChargesIncluded());
		    billPayment.setChargesIncluded(results.getChargesIncluded());
		    billPaymentsService.save(billPayment);
		}*/
		
		billPaymentInquiry.setServiceChargeTransactionLogID(sctl.getID());
		billPaymentInquiry.setDestMDN(partnerMDN.getMDN());
		billPaymentInquiry.setCharges(transaction.getAmountTowardsCharges());
		billPaymentInquiry.setChannelCode(cc.getChannelCode());
		billPaymentInquiry.setSourcePocketID(subPocket.getID());
		billPaymentInquiry.setDestPocketID(agentPocket.getID());
		billPaymentInquiry.setSourceApplication(cc.getChannelSourceApplication());
		// To handle - DE-62 from inquiry response should be DE-61 of confirmation request
		if(billPayRefID != null)
			billPaymentInquiry.setBillData(billPayRefID);
		
		if(transactionResponse != null && !transactionResponse.isResult()){
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}
		else {
			response = super.process(billPaymentInquiry);

			// Saves the Transaction Id returned from Back End		
			transactionResponse = checkBackEndResponse(response);
			if (transactionResponse.getTransactionId()!=null) {
				
				sctl.setTransactionID(transactionResponse.getTransactionId());
				sctl.setCommodityTransferID(transactionResponse.getTransferId());
				billPaymentInquiry.setTransactionID(transactionResponse.getTransactionId());
				result.setTransactionID(transactionResponse.getTransactionId());
				result.setAdditionalInfo(additionalInfo);
				transactionChargingService.saveServiceTransactionLog(sctl);
			}
			if (!transactionResponse.isResult() && sctl!=null){
				
				String errorMsg = transactionResponse.getMessage();
				transactionChargingService.failTheTransaction(sctl, errorMsg);	
			}

			transactionChargingService.updateTransactionStatus(transactionResponse, sctl);
		}
		if ("ZTE".equalsIgnoreCase(billPaymentInquiry.getIntegrationCode())) {
			if (StringUtils.isNotBlank(paymentRequestDetails) && paymentRequestDetails.length()>=183) {
				result.setBillDate(formatBillDate(paymentRequestDetails.substring(151, 159), "yyyyMMdd"));
				result.setDestinationName(paymentRequestDetails.substring(109, 139));
			}
		}else{
			BillPaymentsQuery query = new BillPaymentsQuery();
			query.setSctlID(sctl.getID());
			List<BillPayments> billPayments = billPaymentsService.get(query);
			if(billPayments != null && billPayments.size() > 0){
				BillPayments billPayment = billPayments.get(0);
	
				//Saving bayar.net paramters
				if(operatorChgs != null){
					billPayment.setOperatorCharges(operatorChgs);
					result.setOperatorCharges(operatorChgs);
				}
				if(destUserName != null){
					billPayment.setInfo2(destUserName);
					result.setDestinationName(billPayment.getInfo2());
				}
				if(additionalInfo != null){
					billPayment.setInfo4(additionalInfo);
					if(billPayment.getInfo4() != null && "Bayar".equalsIgnoreCase(billPaymentInquiry.getIntegrationCode()) && billPayment.getInfo4().trim().length() >=6)
						result.setBillDate(formatBillDate(billPayment.getInfo4().trim().substring(0, 6), "yyyyMM"));
					else
						result.setAdditionalInfo(additionalInfo);
				}
				if(billPayRefID != null)
					billPayment.setBillData(billPayRefID);
				if(operatorMsg != null){
					billPayment.setOperatorMessage(operatorMsg);
					result.setResponseMessage(operatorMsg);
				}
				
				billPaymentsService.save(billPayment);
			}
				
		}
		result.setInvoiceNo(billPaymentInquiry.getInvoiceNumber());
		result.setSctlID(sctl.getID());
		result.setMultixResponse(response);
		result.setDebitAmount(sctl.getTransactionAmount());
		result.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
		result.setServiceCharge(sctl.getCalculatedCharge().add(operatorChgs));
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());
		result.setNominalAmount(billPaymentInquiry.getNominalAmount());

		//For 2 factor authentication
		if(transactionResponse.isResult() == true){
			if(mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionDetails.getTransactionTypeName(), cc.getID()) == true){
				result.setMfaMode("OTP");
				mfaService.handleMFATransaction(sctl.getID(), sourceMDN.getMDN());
			}
		}
		return result;
	}

	private CFIXMsg doBillInquiry(CMBillPayInquiry billPayInquiry) {
		log.info("BillpayInquiryHandler:: doBillInquiry :: Begin");
		CMBillInquiry billInquiry = new CMBillInquiry();
		billInquiry.setSourceMDN(billPayInquiry.getSourceMDN());
		billInquiry.setSourceApplication(billPayInquiry.getSourceApplication());
		billInquiry.setChannelCode(billPayInquiry.getChannelCode());
		billInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		billInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Bill_Inquiry);
		billInquiry.setInvoiceNumber(billPayInquiry.getInvoiceNumber());
		billInquiry.setBillerCode(billPayInquiry.getBillerCode());
		billInquiry.setTransactionIdentifier(billPayInquiry.getTransactionIdentifier());
		billInquiry.setTransactionID(billPayInquiry.getTransactionID());
		billInquiry.setIntegrationCode(billPayInquiry.getIntegrationCode());
		billInquiry.setSourceCardPAN(billPayInquiry.getSourceBankAccountNo());
		billInquiry.setAmount(billPayInquiry.getAmount());
		//For Bayar.Net BillPayments
		if(billPayInquiry.getDenominationCode()!=null)
			billInquiry.setDenominationCode(billPayInquiry.getDenominationCode());
		if(billPayInquiry.getNominalAmount()!=null)
			billInquiry.setNominalAmount(billPayInquiry.getNominalAmount());
		
		CFIXMsg response = super.process(billInquiry);
		log.info("BillpayInquiryHandler:: doBillInquiry :: End");
		return response;
	}
	
	private String formatBillDate(String billDate, String inputDateFormat) {
		String result = billDate;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(inputDateFormat);
			sdf.setLenient(false);
			Date day  = sdf.parse(billDate);
			SimpleDateFormat newsdf = new SimpleDateFormat("MMMM yyyy");
			result = newsdf.format(day);
		} catch (ParseException e) {
			log.error("Error while decrypting the Bill Date: "+ billDate);
		}
		return result;
	}
}
