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

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MFSBiller;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.Partner;
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
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.BillerService;
import com.mfino.service.MFSBillerService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.impl.TransactionLogServiceImpl;
import com.mfino.transactionapi.handlers.payment.BillInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * 
 * @author Maruthi
 */
@Service("BillInquiryHandlerImpl")
public class BillInquiryHandlerImpl extends FIXMessageHandler implements BillInquiryHandler{

	@Autowired
	@Qualifier("MFSBillerServiceImpl")
	private MFSBillerService mfsBillerService;
	
	private static Logger log = LoggerFactory.getLogger(BillInquiryHandlerImpl.class);
	
	@Autowired
	@Qualifier("BillerServiceImpl")
	private BillerService billerService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	public Result handle(TransactionDetails transDetails)
	{
		CMBillInquiry billInquiry = new CMBillInquiry();
		ChannelCode cc = transDetails.getCc();
		
		billInquiry.setSourceMDN(transDetails.getSourceMDN());
		billInquiry.setSourceApplication(cc.getChannelSourceApplication());
		billInquiry.setChannelCode(cc.getChannelCode());
		billInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		billInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Bill_Inquiry);
		billInquiry.setInvoiceNumber(transDetails.getBillNum());
		billInquiry.setBillerCode(transDetails.getBillerCode());
		billInquiry.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		
		log.info("Handling Subscriber bill Inquiry webapi request");
		XMLResult result = new TransferInquiryXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BillInquiry, billInquiry.DumpFields());
		billInquiry.setTransactionID(transactionsLog.getID());
		result.setTransactionID(transactionsLog.getID());
		result.setSourceMessage(billInquiry);
		result.setTransactionTime(transactionsLog.getTransactionTime());


		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(billInquiry.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+billInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;

		}

		if(StringUtils.isBlank(billInquiry.getInvoiceNumber())){
			result.setBillerCode(billInquiry.getBillerCode());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidDecoderNumber);
			return result;
		}
		
		MFSBiller mfsBiller = mfsBillerService.getByBillerCode(billInquiry.getBillerCode());
		if (mfsBiller == null) {
			result.setBillerCode(billInquiry.getBillerCode());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidBillerCode);
			return result;
		}

		Partner partner = billerService.getPartner(billInquiry.getBillerCode());

		if(partner==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			return result;
		}
		
		SubscriberMDN partnerMDN = partner.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
		validationResult = transactionApiValidationService.validatePartnerMDN(partnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+billInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;

		}
		
		MFSBillerPartner results = mfsBiller.getMFSBillerPartnerFromMFSBillerId().iterator().next();
		if(results != null){
			billInquiry.setIntegrationCode(results.getIntegrationCode());
		}
		
		log.info("creating the serviceCharge object....");
		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(billInquiry.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(cc.getID());
		sc.setServiceName(transDetails.getServiceName());
		sc.setTransactionTypeName(transDetails.getTransactionName());
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionsLog.getID());
		sc.setTransactionIdentifier(billInquiry.getTransactionIdentifier());

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
		billInquiry.setServiceChargeTransactionLogID(sctl.getID());
		
		CFIXMsg response = super.process(billInquiry);

		TransactionResponse transactionResponse = checkBackEndResponse(response);
		
		if(transactionResponse != null && transactionResponse.isResult())
		{
			if(StringUtils.isNotBlank(transactionResponse.getPaymentInquiryDetails()))
			{
				BigDecimal amount = BigDecimal.ZERO;
				try
				{
					if(transDetails.getBillerCode().equalsIgnoreCase(systemParametersService.getString(SystemParameterKeys.STARTIMES_BILLER_CODE))){
						amount = transactionResponse.getAmount();
						sctl.setStatus(CmFinoFIX.SCTLStatus_Confirmed);
						transactionChargingService.completeTheTransaction(sctl);
					}else{
						amount = new BigDecimal(Long.parseLong(transactionResponse.getPaymentInquiryDetails().substring(172, 183)));
					}
				}
				catch(Exception e)
				{
					log.error("Exception occured in getting amount",e);
					result.setNotificationCode(CmFinoFIX.NotificationCode_GetBillDetailsFailed);
					return result;
				}
				result.setMultixResponse(response);
				result.setAmount(amount);
				result.setAdditionalInfo(transactionResponse.getPaymentInquiryDetails());
				//addCompanyANDLanguageToResult(srcSubscriberMDN,result);
				result.setParentTransactionID(transactionResponse.getTransactionId());
				result.setTransferID(transactionResponse.getTransferId());
				result.setCode(transactionResponse.getCode());
				result.setMessage(transactionResponse.getMessage());
				
				return result;
			}
		}

		result.setNotificationCode(CmFinoFIX.NotificationCode_GetBillDetailsFailed);
		return result;
		
	}

}
