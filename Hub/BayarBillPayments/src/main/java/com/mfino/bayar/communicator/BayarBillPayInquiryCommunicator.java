package com.mfino.bayar.communicator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mfino.bayar.service.BayarWebServiceResponse;
import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

/**
 * @author Vishal
 *
 */
public class BayarBillPayInquiryCommunicator extends BayarHttpCommunicator {
	
	@Override
	public Object createBayarHttpRequest(MCEMessage mceMessage) {
		log.info("BayarBillPayInquiryCommunicator :: createBayarHttpRequest mceMessage="+mceMessage);
		
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		CMBillPayInquiry request = (CMBillPayInquiry) requestFix;
		billPaymentsService.createBillPayments(request);
		
		requestParams.add(new BasicNameValuePair("product_code", request.getDenominationCode()));
		requestParams.add(new BasicNameValuePair("bill_number", request.getInvoiceNumber()));
		requestParams.add(new BasicNameValuePair("reference_id", request.getTransactionID().toString()));
		
		return requestParams;
	}

	@Override
	public CFIXMsg constructReplyMessage(Object response, CFIXMsg requestFixMessage) {
		
		BayarWebServiceResponse wsResponseElement = (BayarWebServiceResponse)response;
		BillPayResponse billPayResponse = new BillPayResponse();
		BillPayments billPayments = null;
		
		log.info("BayarBillPayInquiryCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
			
		if(wsResponseElement != null && wsResponseElement.getStatus() != null && wsResponseElement.getStatus().intValue() == 0){
			
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			log.info("BayarBillPayInquiryCommunicator :: constructReplyMessage Status="+wsResponseElement.getStatus());
			
			billPayResponse.setServiceChargeTransactionLogID(((CMBase) requestFixMessage).getServiceChargeTransactionLogID());
			billPayResponse.setInResponseCode(wsResponseElement.getStatus().toString());
						
			Long sctlId = ((CMBase) requestFixMessage).getServiceChargeTransactionLogID();
			billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
			
			if(wsResponseElement.getPaymentCode() != null)
				billPayments.setBillData(wsResponseElement.getPaymentCode().toString());
			if(wsResponseElement.getGrandTotal() != null){
				billPayResponse.setAmount(new BigDecimal(wsResponseElement.getGrandTotal()));
				billPayments.setAmount(new BigDecimal(wsResponseElement.getGrandTotal()));
				((CMBillPayInquiry) requestFixMessage).setAmount(billPayResponse.getAmount());
			}
			log.info("BayarBillPayInquiryCommunicator :: payment_code ="+wsResponseElement.getPaymentCode());
			
			billPaymentsService.saveBillPayment(billPayments);
			
		}else{
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
		}
		
		billPayResponse.header().setSendingTime(DateTimeUtil.getLocalTime());
		billPayResponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return billPayResponse;
	}
	
	@Override
	public String getMethodName(MCEMessage mceMessage) {
		return constantFieldsMap.get("billpay_inquiry");
	}
}
