package com.mfino.billpayments.bsm.uangku;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.CoreDataWrapper;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.util.DateTimeUtil;

/**
 * 
 * @author Vishal
 * 
 */
public class BillPaymentProcessor implements BSMProcessor {

	private Logger	log	= LoggerFactory.getLogger(this.getClass());
	
	private SubscriberService subscriberService;
	private SubscriberMdnService subscriberMdnService;
	private CoreDataWrapper coreDataWrapper;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage constructRequestMessage(MCEMessage mceMessage) {
		log.info("BillPaymentProcessor :: constructRequestMessage() BEGIN mceMessage=" + mceMessage);
		MCEMessage replyMessage = new MCEMessage();

		CMBillPay requestFix = (CMBillPay) mceMessage.getRequest();

		BackendResponse backendResponse = (BackendResponse) mceMessage.getResponse();
		CMBSIMBillPaymentToBank toOperator = new CMBSIMBillPaymentToBank();
		toOperator.setSourceCardPAN(requestFix.getSourceBankAccountNo());
		toOperator.setUICategory(requestFix.getUICategory());
		toOperator.setSourceMDN(backendResponse.getSourceMDN());
		toOperator.setDestMDN(subscriberService.normalizeMDN(requestFix.getInvoiceNumber()));
		toOperator.setPaymentInquiryDetails(backendResponse.getPaymentInquiryDetails());
		// set this for postpaid and topup properly
//		Integer billerPartnerType = requestFix.getBillerPartnerType();
//		if (CmFinoFIX.BillerPartnerType_Payment_Full.equals(billerPartnerType)
//		        || CmFinoFIX.BillerPartnerType_Payment_Partial.equals(billerPartnerType))
//			toOperator.setSourceTransactionUICategory(CmFinoFIX.TransactionUICategory_Bank_Channel_Payment);// for postpaid																											
//		else
//			toOperator.setSourceTransactionUICategory(CmFinoFIX.TransactionUICategory_Bank_Channel_Topup);// for pre-paid

		toOperator.setAmount(backendResponse.getAmount());
		toOperator.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		toOperator.setTransferID(backendResponse.getTransferID());
		toOperator.setTransactionID(requestFix.getTransactionID());
		toOperator.setParentTransactionID(requestFix.getTransactionID());
		toOperator.setSourceApplication(requestFix.getSourceApplication());
		toOperator.setBankCode(CmFinoFIX.OperatorCodeForRouting_BSM);//routing code for BSM
		toOperator.setInvoiceNo(requestFix.getInvoiceNumber());
		toOperator.setBillerCode(requestFix.getBillerCode());
		Timestamp ts = DateTimeUtil.getGMTTime();
		toOperator.setTransferTime(ts);
		
		
		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toOperator);
		replyMessage.setDestinationQueue("jms:bsmBillPaymentResponseQueue?disableReplyTo=true");
		log.info("BillPaymentProcessor :: constructRequestMessage() END");

		saveIntegrationSummary(requestFix.getTransactionID().toString(),requestFix.getServiceChargeTransactionLogID() );
		
		return replyMessage;
	}

 	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage constructReplyMessage(MCEMessage mceMceMessage) {
		log.info("BillPaymentProcessor :: constructReplyMessage() BEGIN mceMessage=" + mceMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();

		CMBase requestFix = (CMBase) mceMceMessage.getRequest();
		CMBSIMBillPaymentFromBank response = (CMBSIMBillPaymentFromBank) mceMceMessage.getResponse();
		log.info("BillPaymentProcessor:: Response for BillPayment from BSM = " + response.getResponseCode());

		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		billPayResponse.setInResponseCode(response.getResponseCode().toString());
		billPayResponse.setParentTransactionID(response.getParentTransactionID());
		Integer responseCode = new Integer(response.getResponseCode());
		billPayResponse.setOperatorMessage(getOperatorDescription(responseCode, requestFix.getSourceMDN()));
		billPayResponse.setAdditionalInfo(response.getInfo1());
		recordOperatorResponseCode(requestFix.getServiceChargeTransactionLogID(), response.getResponseCode().toString());
		
		if (CmFinoFIX.ISO8583_ResponseCode_Success.equals(response.getResponseCode())) {
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setInTxnId(response.getBillPaymentReferenceID());
		}
		else {
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
		}
		responseMceMessage.setRequest(mceMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);
		log.info("BillPaymentProcessor :: constructReplyMessage() END");

		return responseMceMessage;
	}


 	private void saveIntegrationSummary(String transactionID, Long sctlID) {
 		IntegrationSummaryDao isdao = getCoreDataWrapper().getIntegrationSummaryDao();
 		IntegrationSummary isummary = new IntegrationSummary();
 		isummary.setSctlid(new BigDecimal(sctlID));
 		isummary.setReconcilationid1(transactionID);
 		isdao.save(isummary);
 	}

 	private void recordOperatorResponseCode(Long sctlId, String de39){
 		/*
 		 * BSM specific req (smart ticket #1024) to display the DE39 value in both Admin Application and offline reports.
 		 * Save this value to ReconcillationId2 in integration_summary table, and the same is synched across. 
 		 */
 		IntegrationSummaryDao integrationSummaryDao = getCoreDataWrapper().getIntegrationSummaryDao();
 		IntegrationSummaryQuery query = new IntegrationSummaryQuery();
 		query.setSctlID(sctlId);
 		List<IntegrationSummary> iSummaryList = integrationSummaryDao.get(query);

 		IntegrationSummary iSummary = null;
 		if((null != iSummaryList)&&(iSummaryList.size() > 0)){
 			iSummary = iSummaryList.get(0);
 			iSummary.setReconcilationid2(de39);
 		}
 		else{
 			iSummary = new IntegrationSummary();
 			iSummary.setSctlid(new BigDecimal(sctlId));
 			iSummary.setReconcilationid2(de39);
 		}

 		integrationSummaryDao.save(iSummary);
 	}

 	private String getOperatorDescription(Integer responseCode, String mdn) {
 		Subscriber subscriber = subscriberMdnService.getSubscriberFromMDN(mdn);
 		String result = null;
 		if (subscriber != null) {
 			result = JSONUtil.getOperatorDescription(responseCode.toString(), (int)subscriber.getLanguage());
 		} else {
 			result = JSONUtil.getOperatorDescription(responseCode.toString(), 0);
 		}
 		return result;
 	}

 	public SubscriberService getSubscriberService() {
 		return subscriberService;
 	}

 	public void setSubscriberService(SubscriberService subscriberService) {
 		this.subscriberService = subscriberService;
 	}

 	public CoreDataWrapper getCoreDataWrapper() {
 		return coreDataWrapper;
 	}

 	public void setCoreDataWrapper(CoreDataWrapper coreDataWrapper) {
 		this.coreDataWrapper = coreDataWrapper;
 	}

 	public SubscriberMdnService getSubscriberMdnService() {
 		return subscriberMdnService;
 	}

 	public void setSubscriberMdnService(SubscriberMdnService subscriberMdnService) {
 		this.subscriberMdnService = subscriberMdnService;
 	}	

}
