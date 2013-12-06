package com.mfino.billpayments.clickatell;


import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsFromOperator;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.SubscriberService;
import com.mfino.service.impl.SubscriberServiceImpl;

/**
 * 
 * @author Maruthi
 *
 */
public class BillPayInquiryProcessor extends BillPaymentsBaseServiceImpl implements ClickatellProcessor {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private BillPaymentsService billPaymentsService;
	private SubscriberService subscriberService ;
	
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}
	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}
	@Override
	public MCEMessage constructRequestMessage(MCEMessage mceMessage){
		log.info("BillPayInquiryProcessor :: constructRequestMessage() BEGIN mceMessage="+mceMessage);

		MCEMessage replyMessage = new MCEMessage(); 
		CMBase requestFix = (CMBase)mceMessage.getRequest();
		BackendResponse backendResponse = (BackendResponse) mceMessage.getResponse();
		CMGetMDNBillDebtsToOperator toOperator = new CMGetMDNBillDebtsToOperator();
		toOperator.setSourceMDN(backendResponse.getSourceMDN());
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(requestFix.getServiceChargeTransactionLogID());


		toOperator.setDestMDN(subscriberService.normalizeMDN(billPayments.getInvoiceNumber()));
		
		toOperator.setTransactionID(backendResponse.getTransferID());//FIXME is it transferid or transactionid
		toOperator.setSourceApplication(requestFix.getSourceApplication());
		toOperator.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		toOperator.setParentTransactionID(requestFix.getTransactionID());
		toOperator.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);//routing code for clickatell
		
		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toOperator);
		replyMessage.setDestinationQueue("jms:clickatellBillPayInquiryResponseQueue?disableReplyTo=true");
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
		log.info("clickatellBillPayInquiry Response for BillInquiry = "+response.getResponseCode());
		
		
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		billPayResponse.setInResponseCode(response.getResponseCode().toString());
		billPayResponse.setInTxnId(response.getBillPaymentReferenceID());
		billPayResponse.setParentTransactionID(response.getParentTransactionID());
		billPayResponse.setTransferID(response.getTransactionID());
		
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

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}
}
