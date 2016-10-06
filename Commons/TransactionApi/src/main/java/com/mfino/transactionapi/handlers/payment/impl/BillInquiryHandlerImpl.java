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

import com.mfino.domain.ChannelCode;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.MfsBiller;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.BillerService;
import com.mfino.service.MFSBillerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
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
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	public Result handle(TransactionDetails transDetails)
	{
		CMBillInquiry billInquiry = new CMBillInquiry();
		ChannelCode cc = transDetails.getCc();
		
		billInquiry.setSourceMDN(transDetails.getSourceMDN());
		billInquiry.setSourceApplication((int)cc.getChannelsourceapplication());
		billInquiry.setChannelCode(cc.getChannelcode());
		billInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		billInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Bill_Inquiry);
		billInquiry.setInvoiceNumber(transDetails.getBillNum());
		billInquiry.setBillerCode(transDetails.getBillerCode());
		billInquiry.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		
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
		
		/* pocket required for charge deduction*/
		Pocket subPocket = pocketService.getDefaultPocket(srcSubscriberMDN, transDetails.getSourcePocketCode());
		if(subPocket==null){
			log.error("Subscriber with mdn : "+billInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound);
			return result;
		}
		if (!(subPocket.getStatus()==(CmFinoFIX.PocketStatus_Active))) {
			log.error("Subscriber with mdn : "+billInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(CmFinoFIX.NotificationCode_MoneyPocketNotActive);
			return result;
		}

		if(StringUtils.isBlank(billInquiry.getInvoiceNumber())){
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

		Partner partner = billerService.getPartner(billInquiry.getBillerCode());

		if(partner==null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			return result;
		}
		
		SubscriberMdn partnerMDN = partner.getSubscriber().getSubscriberMdns().iterator().next();
		validationResult = transactionApiValidationService.validatePartnerMDN(partnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+billInquiry.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;

		}
		
		MFSBillerPartner results = mfsBiller.getMfsbillerPartnerMaps().iterator().next();
		if(results != null){
			billInquiry.setIntegrationCode(results.getIntegrationcode());
		}
		
		billInquiry.setNarration("ccpayment");
		
		billInquiry.setSourceBankAccountNo(subPocket.getCardpan());
		billInquiry.setSourcePocketID(subPocket.getId().longValue());
		if(CmFinoFIX.BankAccountCardType_SavingsAccount.equals(subPocket.getPocketTemplate().getBankaccountcardtype()))
			billInquiry.setSourceBankAccountType(""+ CmFinoFIX.BankAccountType_Saving);
		
		CFIXMsg response = super.process(billInquiry);

		TransactionResponse transactionResponse = checkBackEndResponse(response);
		
		if(transactionResponse != null && transactionResponse.isResult()){
			if(StringUtils.isNotBlank(transactionResponse.getPaymentInquiryDetails())){
				BigDecimal amount = BigDecimal.ZERO;
				try{
					amount = new BigDecimal(Long.parseLong(transactionResponse.getPaymentInquiryDetails()));
				}catch(Exception e){
					log.error("Exception occured in getting amount",e);
					result.setNotificationCode(CmFinoFIX.NotificationCode_GetBillDetailsFailed);
					return result;
				}
				result.setMultixResponse(response);
				result.setAmount(amount);
				addCompanyANDLanguageToResult(srcSubscriberMDN, result);
				result.setParentTransactionID(transactionResponse.getTransactionId());
				result.setCode(transactionResponse.getCode());
				result.setMessage(transactionResponse.getMessage());
				result.setAdditionalInfo(transactionResponse.getAdditionalInfo());
				
				return result;
			}
		}

		result.setNotificationCode(CmFinoFIX.NotificationCode_GetBillDetailsFailed);
		return result;
		
	}

}
