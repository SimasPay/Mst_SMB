package com.mfino.billpayments.bsim;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.domain.IntegrationSummary;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.SubscriberService;

/**
 * 
 * @author Maruthi
 * 
 */
public class BillPaymentProcessor implements BSIMProcessorNew {

	private Logger	log	= LoggerFactory.getLogger(this.getClass());
	private SubscriberService subscriberService;

 	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage constructRequestMessage(MCEMessage mceMessage) {
		log.info("BillPaymentProcessor :: constructRequestMessage() BEGIN mceMessage=" + mceMessage);
		MCEMessage replyMessage = new MCEMessage();

		CMBillPay requestFix = (CMBillPay) mceMessage.getRequest();

		BackendResponse backendResponse = (BackendResponse) mceMessage.getResponse();
		CMCommodityTransferToOperator toOperator = new CMCommodityTransferToOperator();
		toOperator.setSourceMDN(backendResponse.getSourceMDN());
		toOperator.setDestMDN(subscriberService.normalizeMDN(requestFix.getInvoiceNumber()));
		toOperator.setPaymentInquiryDetails(backendResponse.getPaymentInquiryDetails());
		// FIXME set this for postpaid and topup properly
		Integer billerPartnerType = requestFix.getBillerPartnerType();
		if (CmFinoFIX.BillerPartnerType_Payment_Full.equals(billerPartnerType)
		        || CmFinoFIX.BillerPartnerType_Payment_Partial.equals(billerPartnerType))
			toOperator.setSourceTransactionUICategory(CmFinoFIX.TransactionUICategory_Bank_Channel_Payment);// for
																											// PostPaid
		else
			toOperator.setSourceTransactionUICategory(CmFinoFIX.TransactionUICategory_Bank_Channel_Topup);// for
																										  // Prepaid

		toOperator.setAmount(backendResponse.getAmount());
		toOperator.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		toOperator.setTransferID(backendResponse.getTransferID());
		toOperator.setTransactionID(requestFix.getTransactionID());
		toOperator.setParentTransactionID(requestFix.getTransactionID());
		toOperator.setSourceApplication(requestFix.getSourceApplication());
		toOperator.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);// routing
																		   // code
																		   // for
																		   // zte
		toOperator.setBillPaymentReferenceID(requestFix.getInvoiceNumber());// FIXME
																			// use
																			// getBillPaymentReferenceID()
		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toOperator);
		replyMessage.setDestinationQueue("jms:zteBillPaymentResponseQueue?disableReplyTo=true");
		log.info("BillPaymentProcessor :: constructRequestMessage() END");

		saveIntegrationSummary(requestFix.getTransactionID().toString(),requestFix.getServiceChargeTransactionLogID() );
		
		return replyMessage;
	}

 	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage constructReplyMessage(MCEMessage mceMceMessage) {
		log.info("BillPaymentProcessor :: constructReplyMessage() BEGIN mceMessage=" + mceMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();

		CMBase requestFix = (CMBase) mceMceMessage.getRequest();
		CMCommodityTransferFromOperator response = (CMCommodityTransferFromOperator) mceMceMessage.getResponse();
		log.info("BillPaymentProcessor:: Response for BillPayment from ZTE = " + response.getResponseCode());

		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		billPayResponse.setInResponseCode(response.getResponseCode().toString());
		billPayResponse.setParentTransactionID(response.getParentTransactionID());
		billPayResponse.setTransferID(response.getTransferID());

		recordOperatorResponseCode(requestFix.getServiceChargeTransactionLogID(), response.getResponseCode().toString());
		
		if (CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Success.equals(response.getResponseCode())) {
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

 		IntegrationSummaryDao isdao = DAOFactory.getInstance().getIntegrationSummaryDao();

 		IntegrationSummary isummary = new IntegrationSummary();
 		isummary.setSctlId(sctlID);
 		isummary.setReconcilationID1(transactionID);
 		isdao.save(isummary);
 	}


	private void recordOperatorResponseCode(Long sctlId, String de39){

		IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummaryQuery query = new IntegrationSummaryQuery();
		query.setSctlID(sctlId);
		List<IntegrationSummary> iSummaryList = integrationSummaryDao.get(query);

		IntegrationSummary iSummary = null;
		if((null != iSummaryList)&&(iSummaryList.size() > 0)){
			iSummary = iSummaryList.get(0);
			iSummary.setReconcilationID2(de39);
		}
		else{
			iSummary = new IntegrationSummary();
			iSummary.setSctlId(sctlId);
			iSummary.setReconcilationID2(de39);
		}

		integrationSummaryDao.save(iSummary);
	}
	
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

}
