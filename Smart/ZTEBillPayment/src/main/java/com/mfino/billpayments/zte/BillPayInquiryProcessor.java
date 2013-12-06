package com.mfino.billpayments.zte;


import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.IntegrationSummary;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsFromOperator;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.impl.SubscriberServiceImpl;

/**
 * 
 * @author Maruthi
 *
 */
public class BillPayInquiryProcessor extends BillPaymentsBaseServiceImpl implements ZteProcessor {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private BillPaymentsService billPaymentsService;
	
	@Override
	public MCEMessage constructRequestMessage(MCEMessage mceMessage){
		log.info("BillPayInquiryProcessor :: constructRequestMessage() BEGIN mceMessage="+mceMessage);

		MCEMessage replyMessage = new MCEMessage(); 
		CMBase requestFix = (CMBase)mceMessage.getRequest();
		BackendResponse backendResponse = (BackendResponse) mceMessage.getResponse();
		CMGetMDNBillDebtsToOperator toOperator = new CMGetMDNBillDebtsToOperator();
		toOperator.setSourceMDN(backendResponse.getSourceMDN());
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(requestFix.getServiceChargeTransactionLogID());
		SubscriberServiceImpl subscriberServiceImpl = new SubscriberServiceImpl();
		toOperator.setDestMDN(subscriberServiceImpl.normalizeMDN(billPayments.getInvoiceNumber()));
		
		toOperator.setTransactionID(backendResponse.getTransferID());//FIXME is it transferid or transactionid
		toOperator.setSourceApplication(requestFix.getSourceApplication());
		toOperator.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		toOperator.setParentTransactionID(requestFix.getTransactionID());
		toOperator.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);//routing code for zte
		
		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toOperator);
		replyMessage.setDestinationQueue("jms:zteBillPayInquiryResponseQueue?disableReplyTo=true");
		log.info("BillPayInquiryProcessor :: constructRequestMessage() END");

		return replyMessage;
	}
	
	@Override
	public MCEMessage constructReplyMessage(MCEMessage mceMceMessage) {
		
		log.info("BillPayInquiryProcessor :: constructReplyMessage() BEGIN mceMessage="+mceMceMessage);
		
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMBase requestFix = (CMBase)mceMceMessage.getRequest();
		BillPayResponse billPayResponse = new BillPayResponse();
		CMGetMDNBillDebtsFromOperator response  = (CMGetMDNBillDebtsFromOperator) mceMceMessage.getResponse();
		log.info("zteBillPayInquiry Response for BillInquiry = "+response.getResponseCode());
		
		
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		billPayResponse.setInResponseCode(response.getResponseCode().toString());
		billPayResponse.setInTxnId(response.getBillPaymentReferenceID());
		billPayResponse.setParentTransactionID(response.getParentTransactionID());
		billPayResponse.setTransferID(response.getTransactionID());
		
		recordOperatorResponseCode(requestFix.getServiceChargeTransactionLogID(), response.getResponseCode().toString());
		
		if(response.getTotalBillDebts()!=null&&(!response.getTotalBillDebts().equals(BigDecimal.ZERO)))
		billPayResponse.setAmount(response.getTotalBillDebts());
		
		if(CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Success.equals(response.getResponseCode())){
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
		}else{
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
		}	
		responseMceMessage.setRequest(mceMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);
		
		log.info("BillPayInquiryProcessor :: constructReplyMessage() END");
		return responseMceMessage;
	}
	
	
	public MCEMessage constructResponseMessage(MCEMessage mceMceMessage) {
		
		log.info("BillPayInquiryProcessor :: constructResponseMessage() BEGIN mceMessage="+mceMceMessage);
		
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMBase requestFix = (CMBase)mceMceMessage.getRequest();
		BillPayResponse billPayResponse = new BillPayResponse();
		BackendResponse backendResponse = (BackendResponse) mceMceMessage.getResponse();
		billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
		billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		billPayResponse.setInResponseCode("0");
		billPayResponse.setParentTransactionID(backendResponse.getParentTransactionID());
		billPayResponse.setTransferID(backendResponse.getTransferID());
		responseMceMessage.setRequest(mceMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);
		
		log.info("BillPayInquiryProcessor :: constructResponseMessage() END");
		return responseMceMessage;
	}

	private void recordOperatorResponseCode(Long sctlId, String de39){
		/*
		 * ZTE specific req (smart ticket #1024) to display the DE39 value in both Admin Application and offline reports.
		 * Save this value to ReconcillationId2 in integration_summary table, and the same is synched across. 
		 */
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
	
	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}
}
