package com.mfino.bayar.communicator;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mfino.bayar.service.BayarWebServiceResponse;
import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

/**
 * @author Vishal
 *
 */
public class BayarBillPayCommunicator extends BayarHttpCommunicator {

	private String billdataMsg;
	
	@Override
	public Object createBayarHttpRequest(MCEMessage mceMessage) {
		log.info("BayarBillPayCommunicator :: createBayarHttpRequest mceMessage="+mceMessage);
		
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();		
		
		CMBillPay request = (CMBillPay) requestFix;
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		if( billPayments != null && billPayments.getBilldata() != null)
			billdataMsg = billPayments.getBilldata();
		
		requestParams.add(new BasicNameValuePair("payment_code", billdataMsg));
		requestParams.add(new BasicNameValuePair("reference_id", request.getServiceChargeTransactionLogID().toString()));
		
		return requestParams;
	}

	@Override
	public CFIXMsg constructReplyMessage(Object response, CFIXMsg requestFixMessage) {
		
		BayarWebServiceResponse wsResponseElement = (BayarWebServiceResponse)response;
		BillPayResponse billPayResponse = new BillPayResponse();
		BillPayments billPayments = null;
		
		log.info("BayarBillPayCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
		
		Long sctlId = ((CMBase) requestFixMessage).getServiceChargeTransactionLogID();
		billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		billPayResponse.setServiceChargeTransactionLogID(sctlId);
		if(wsResponseElement.getTransactionId() != null){
			billPayResponse.setInTxnId(wsResponseElement.getTransactionId().toString());
		}
		
		if(wsResponseElement != null && wsResponseElement.getStatus() != null && wsResponseElement.getStatus().intValue() == 0){

			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);			
			
			if(wsResponseElement.getVoucherToken() != null)	{			
				billPayments.setInfo3(wsResponseElement.getVoucherToken());//In case of PLN prepaid Token
				billPayResponse.setRechargePin(wsResponseElement.getVoucherToken());
			}
			log.info("BayarBillPayCommunicator :: constructReplyMessage Status="+wsResponseElement.getStatus());
		}else{	
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
		}

		if(wsResponseElement.getStatus() != null && wsResponseElement.getStatus().equals(SERVICE_TIME_OUT_FROM_BAYAR)){
			billPayResponse.setResponse(SERVICE_TIME_OUT_FROM_BAYAR);
			billPayResponse.setResult(SERVICE_TIME_OUT_FROM_BAYAR);
			billPayResponse.setInResponseCode(SERVICE_TIME_OUT);
		}
		if(wsResponseElement.getStatus() != null)
			billPayResponse.setInResponseCode(wsResponseElement.getStatus().toString());
		if(wsResponseElement.getMessage() != null)
			billPayments.setOperatormessage(wsResponseElement.getMessage()); // Storing return message in OperatorMessage column of bill_payments
		billPaymentsService.saveBillPayment(billPayments);
		
		billPayResponse.header().setSendingTime(DateTimeUtil.getLocalTime());
		billPayResponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return billPayResponse;
	}
	
	@Override
	public String getMethodName(MCEMessage mceMessage) {
		
		return constantFieldsMap.get("billpay");
	}

	@Override
	public MCEMessage constructResponseMessage(MCEMessage mceMceMessage) {
		
		return null;
	}
}
