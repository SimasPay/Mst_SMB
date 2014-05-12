package com.mfino.bayar.communicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
		if( billPayments != null && billPayments.getBillData() != null)
			billdataMsg = billPayments.getBillData();

		billPayments.setNoOfRetries(0);
		billPaymentsService.saveBillPayment(billPayments);
		
		requestParams.add(new BasicNameValuePair("payment_code", billdataMsg));
		requestParams.add(new BasicNameValuePair("reference_id", request.getTransactionID().toString()));
		
		return requestParams;
	}

	@Override
	public CFIXMsg constructReplyMessage(Object response, CFIXMsg requestFixMessage) {
		
		HashMap<String,Object> wsResponseElement = (HashMap<String,Object>)response;
		BillPayResponse billPayResponse = new BillPayResponse();
		
		log.info("BayarBillPayCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
		
		Long sctlId = ((CMBase) requestFixMessage).getServiceChargeTransactionLogID();
		
		if(wsResponseElement.get("status") != null && wsResponseElement.get("status").equals("0")){

			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setInResponseCode(wsResponseElement.get("status").toString());
			billPayResponse.setInTxnId(wsResponseElement.get("transaction_id").toString());
			billPayResponse.setServiceChargeTransactionLogID(sctlId);

			log.info("BayarBillPayCommunicator :: constructReplyMessage Status="+wsResponseElement.get("status"));
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
		
		return constantFieldsMap.get("billpay");
	}
}
