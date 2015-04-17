/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.transactionapi.handlers.payment.impl;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
import com.mfino.fix.CmFinoFIX.CMQRPaymentInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.BillPaymentsService;
import com.mfino.service.BillerService;
import com.mfino.service.MFAService;
import com.mfino.service.MFSBillerService;
import com.mfino.service.MFSDenominationsService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.payment.QRPaymentInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author HemanthKumar
 *
 */
@Service("QRPaymentInquiryHandlerImpl")
public class QRPaymentInquiryHandlerImpl extends FIXMessageHandler implements QRPaymentInquiryHandler{

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

	private static Logger log = LoggerFactory.getLogger(QRPaymentInquiryHandlerImpl.class);
	
	public Result handle(TransactionDetails transactionDetails){
		
		String srcpocketcode;
		CMQRPaymentInquiry qrPaymentInquiry= new CMQRPaymentInquiry();
		ChannelCode cc = transactionDetails.getCc();
		
		qrPaymentInquiry.setSourceMDN(transactionDetails.getSourceMDN());
		qrPaymentInquiry.setInvoiceNumber(transactionDetails.getBillNum());
		qrPaymentInquiry.setPin(transactionDetails.getSourcePIN());
		qrPaymentInquiry.setBillerCode(transactionDetails.getBillerCode());
		qrPaymentInquiry.setDiscountAmount(transactionDetails.getDiscountAmount());
		qrPaymentInquiry.setAmount(transactionDetails.getAmount());
		qrPaymentInquiry.setSourceApplication(cc.getChannelSourceApplication());
		qrPaymentInquiry.setChannelCode(cc.getChannelCode());
		qrPaymentInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		qrPaymentInquiry.setSourceMessage(transactionDetails.getSourceMessage());
		qrPaymentInquiry.setMessageType(CmFinoFIX.MessageType_BillPayInquiry);
		qrPaymentInquiry.setBenOpCode(transactionDetails.getBenOpCode());
		qrPaymentInquiry.setNarration(transactionDetails.getNarration());
		qrPaymentInquiry.setParentTransactionID(0L);
		qrPaymentInquiry.setMerchantData(transactionDetails.getMerchantData());
		srcpocketcode=transactionDetails.getSourcePocketCode();
        qrPaymentInquiry.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		qrPaymentInquiry.setPaymentMode(transactionDetails.getPaymentMode());
        qrPaymentInquiry.setDiscountType(transactionDetails.getDiscountType());
        qrPaymentInquiry.setLoyalityName(transactionDetails.getLoyalityName());
        qrPaymentInquiry.setNumberOfCoupons(transactionDetails.getNumberOfCoupons());
		log.info("Handling Subscriber qr payment Inquiry webapi request");
		XMLResult result = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_QRPaymentInquiry, qrPaymentInquiry.DumpFields());
		qrPaymentInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(qrPaymentInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());

		SubscriberMDN sourceMDN = subscriberMdnService.getByMDN(qrPaymentInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+qrPaymentInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		qrPaymentInquiry.setUserAPIKey(sourceMDN.getUserAPIKey());

		MFSBiller mfsBiller = mfsBillerService.getByBillerCode(qrPaymentInquiry.getBillerCode());
		if (mfsBiller == null) {
			result.setBillerCode(qrPaymentInquiry.getBillerCode());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidBillerCode);
			return result;
		}
		Pocket subPocket = pocketService.getDefaultPocket(sourceMDN, srcpocketcode);
		validationResult = transactionApiValidationService.validateSourcePocket(subPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(subPocket!=null? subPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(subPocket);
		result.setPocketList(pocketList);
		Partner partner = billerService.getPartner(qrPaymentInquiry.getBillerCode());

		SubscriberMDN partnerMDN = partner.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
		validationResult = transactionApiValidationService.validatePartnerMDN(partnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination partner has failed validations");
			validationResult = processValidationResultForPartner(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		qrPaymentInquiry.setEmail(sourceMDN.getSubscriber().getEmail());
		
		MFSBillerPartner results = mfsBiller.getMFSBillerPartnerFromMFSBillerId().iterator().next();
		if(results != null){
			qrPaymentInquiry.setIntegrationCode(results.getIntegrationCode());
			qrPaymentInquiry.setPartnerBillerCode(results.getPartnerBillerCode());
			qrPaymentInquiry.setChargesIncluded(results.getChargesIncluded());
			if(CmFinoFIX.BillerPartnerType_Topup_Denomination.equals(results.getBillerPartnerType())){
				MFSDenominationsQuery mdquery = new MFSDenominationsQuery();
				mdquery.setMfsID(results.getID());
				List<MFSDenominations> res  = mfsDenominationsService.get(mdquery);
				if(res.size() > 0){
					boolean isValid = false;
					StringBuffer validDenominations = new StringBuffer();
					for(int i=0; i < res.size(); i++){
						if(res.get(i).getDenominationAmount().compareTo(qrPaymentInquiry.getAmount()) == 0 ){
							qrPaymentInquiry.setPartnerBillerCode(res.get(i).getProductCode());
							isValid=true;
						}
						validDenominations.append((res.get(i).getDenominationAmount().setScale(2, RoundingMode.HALF_EVEN)).toString()+" ");
					}
					if(isValid == false){
						result.setBillAmount(qrPaymentInquiry.getAmount().setScale(2, RoundingMode.HALF_EVEN));
						result.setValidDenominations(validDenominations.toString());
						result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidTopupDenomination);
						return result;
					}
				}
			}
		}
		
		// add service charge to amount

		ServiceCharge serviceCharge=new ServiceCharge();
		serviceCharge.setChannelCodeId(cc.getID());
		serviceCharge.setDestMDN(partnerMDN.getMDN());
		serviceCharge.setServiceName(transactionDetails.getServiceName());//change to service
		serviceCharge.setTransactionTypeName(transactionDetails.getTransactionTypeName());
		serviceCharge.setSourceMDN(sourceMDN.getMDN());
		serviceCharge.setTransactionAmount(qrPaymentInquiry.getAmount());
		serviceCharge.setMfsBillerCode(qrPaymentInquiry.getBillerCode());
		serviceCharge.setTransactionLogId(qrPaymentInquiry.getTransactionID());
		serviceCharge.setInvoiceNo(qrPaymentInquiry.getInvoiceNumber());
		serviceCharge.setOnBeHalfOfMDN(StringUtils.isNotBlank(qrPaymentInquiry.getOnBeHalfOfMDN()) ? qrPaymentInquiry.getOnBeHalfOfMDN() : StringUtils.EMPTY);
		serviceCharge.setTransactionIdentifier(qrPaymentInquiry.getTransactionIdentifier());
		
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
			qrPaymentInquiry.setAmount(transaction.getAmountToCredit());
			qrPaymentInquiry.setCharges(transaction.getAmountTowardsCharges());

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
		sctl.setIntegrationCode(qrPaymentInquiry.getIntegrationCode());
		
		BillPaymentsQuery query = new BillPaymentsQuery();
		query.setSctlID(sctl.getID());
		List<BillPayments> billPayments = billPaymentsService.get(query);
		if(billPayments != null && billPayments.size() > 0){
			BillPayments billPayment = billPayments.get(0);
			billPayment.setInfo1(qrPaymentInquiry.getPaymentInquiryDetails());
			log.info("Check for ChargesInclusion"+results.getChargesIncluded());
		    billPayment.setChargesIncluded(results.getChargesIncluded());
		    billPaymentsService.save(billPayment);
		}
		
		qrPaymentInquiry.setServiceChargeTransactionLogID(sctl.getID());
		qrPaymentInquiry.setDestMDN(partnerMDN.getMDN());
		qrPaymentInquiry.setCharges(transaction.getAmountTowardsCharges());
		qrPaymentInquiry.setChannelCode(cc.getChannelCode());
		qrPaymentInquiry.setSourcePocketID(subPocket.getID());
		qrPaymentInquiry.setDestPocketID(agentPocket.getID());
		qrPaymentInquiry.setSourceApplication(cc.getChannelSourceApplication());
		
		if((StringUtils.isNotBlank(transactionDetails.getPaymentMode())) && (CmFinoFIX.PaymentMode_HubZeroAmount.equalsIgnoreCase(transactionDetails.getPaymentMode())))
		{
			qrPaymentInquiry.setNarration("online");
		}
		CFIXMsg response = super.process(qrPaymentInquiry);

		// Saves the Transaction Id returned from Back End		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (transactionResponse.getTransactionId()!=null) {
			
			sctl.setTransactionID(transactionResponse.getTransactionId());
			sctl.setCommodityTransferID(transactionResponse.getTransferId());
			qrPaymentInquiry.setTransactionID(transactionResponse.getTransactionId());
			result.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}
		if (!transactionResponse.isResult() && sctl!=null){
			
			String errorMsg = transactionResponse.getMessage();
			transactionChargingService.failTheTransaction(sctl, errorMsg);	
		}

		transactionChargingService.updateTransactionStatus(transactionResponse, sctl);
		
		result.setSctlID(sctl.getID());
		result.setMultixResponse(response);
		result.setDebitAmount(transaction.getAmountToDebit());
		result.setCreditAmount(transaction.getAmountToCredit());
		result.setServiceCharge(transaction.getAmountTowardsCharges());
		addCompanyANDLanguageToResult(sourceMDN,result);
		result.setParentTransactionID(transactionResponse.getTransactionId());
		result.setTransferID(transactionResponse.getTransferId());
		result.setCode(transactionResponse.getCode());
		result.setMessage(transactionResponse.getMessage());

		//For 2 factor authentication
		if(transactionResponse.isResult() == true){
			if(mfaService.isMFATransaction(transactionDetails.getServiceName(), transactionDetails.getTransactionTypeName(), cc.getID()) == true){
				result.setMfaMode("OTP");
				mfaService.handleMFATransaction(sctl.getID(), sourceMDN.getMDN());
			}
		}
		return result;
	}

}
