package com.mfino.billpayments.flashiz;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBank;
import com.mfino.fix.CmFinoFIX.CMQRPayment;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.SubscriberService;
import com.mfino.service.impl.SubscriberServiceImpl;

public class BillPaymentProcessor implements FlashizProcessor {
	
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
		log.info("BillPaymentProcessor :: constructRequestMessage() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage(); 
		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecordWrapInTxn(sctlId);
		//CMBillPay requestFix = (CMBillPay)mceMessage.getRequest();
		BackendResponse backendResponse = (BackendResponse) mceMessage.getResponse();
		CMPaymentAcknowledgementToBank toOperator = new CMPaymentAcknowledgementToBank();
		toOperator.setSourceMDN(backendResponse.getSourceMDN());
		toOperator.setDestMDN(subscriberService.normalizeMDN(billPayments.getInvoiceNumber()));
		toOperator.setPaymentInquiryDetails(backendResponse.getPaymentInquiryDetails());
		Integer billerPartnerType = CmFinoFIX.BillerPartnerType_Payment_Full;
		//FIXME set this for postpaid and topup properly
		toOperator.setAmount(backendResponse.getAmount());
		toOperator.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		//toOperator.setTransferID(backendResponse.getTransferID());
		//toOperator.setTransferID(sctlId);
		toOperator.setTransactionID(sctlId);
		toOperator.setParentTransactionID(sctlId);
		toOperator.setSourceApplication(requestFix.getSourceApplication());
		toOperator.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_FLASHIZ);//routing code for flashiz
		toOperator.setBillPaymentReferenceID(billPayments.getInvoiceNumber());//FIXME use getBillPaymentReferenceID()
		toOperator.setProductIndicatorCode(billPayments.getPartnerBillerCode()); // Identify the product for which bill payment is being done. 
		toOperator.setUserAPIKey(((CMQRPayment) requestFix).getUserAPIKey());
		toOperator.setMerchantData(((CMQRPayment) requestFix).getMerchantData());
		toOperator.setInvoiceNo(((CMQRPayment) requestFix).getInvoiceNumber());
		replyMessage.setRequest(mceMessage.getRequest());
		replyMessage.setResponse(toOperator);
		replyMessage.setDestinationQueue("jms:flashizBillPaymentResponseQueue?disableReplyTo=true");
		log.info("BillPaymentProcessor :: constructRequestMessage() END");
		return replyMessage;
	}
	
	@Override
	public MCEMessage constructReplyMessage(MCEMessage mceMceMessage) {
		log.info("BillPaymentProcessor :: constructReplyMessage() BEGIN mceMessage="+mceMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMBase requestFix = (CMBase)mceMceMessage.getRequest();
		CMPaymentAcknowledgementFromBank response  = (CMPaymentAcknowledgementFromBank) mceMceMessage.getResponse();
		log.info("BillPaymentProcessor:: Response for BillPayment from flashiz = "+response.getResponseCode());
		
		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		billPayResponse.setInResponseCode(response.getResponseCode().toString());
		billPayResponse.setParentTransactionID(response.getParentTransactionID());
		billPayResponse.setTransferID(response.getTransferID());
		
		if(CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Success.equals(response.getResponseCode())){
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setInTxnId(response.getINTxnId());				
		}else{
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
		}	
		responseMceMessage.setRequest(mceMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);
		log.info("BillPaymentProcessor :: constructReplyMessage() END");
		
		return responseMceMessage;
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

}
