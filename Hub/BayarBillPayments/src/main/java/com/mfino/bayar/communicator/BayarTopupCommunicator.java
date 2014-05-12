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
public class BayarTopupCommunicator extends BayarHttpCommunicator {

	
	@Override
	public Object createBayarHttpRequest(MCEMessage mceMessage) {
		log.info("BayarTopupCommunicator :: createBayarHttpRequest mceMessage="+mceMessage);
		
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		
		CMBillPay request = (CMBillPay)mceMessage.getRequest();
		Long sctlId = request.getServiceChargeTransactionLogID();
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		requestParams.add(new BasicNameValuePair("product_code", request.getBillerCode()));
		requestParams.add(new BasicNameValuePair("bill_number", request.getDestMDN()));
		requestParams.add(new BasicNameValuePair("voucher_denomination", billPayments.getAmount().toString()));
		requestParams.add(new BasicNameValuePair("reference_id", request.getTransactionID().toString()));
		
		return requestParams;
	}

	@Override
	public CFIXMsg constructReplyMessage(Object response, CFIXMsg requestFixMessage) {
		
		HashMap<String,Object> wsResponseElement = (HashMap<String,Object>)response;
		BillPayResponse billPayResponse = new BillPayResponse();
		
		log.info("BayarTopupCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
		
		if(wsResponseElement != null && wsResponseElement.get("status") != null && wsResponseElement.get("status").equals("0")){
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setServiceChargeTransactionLogID(((CMBase) requestFixMessage).getServiceChargeTransactionLogID());
			billPayResponse.setInResponseCode(wsResponseElement.get("status").toString());
			if(wsResponseElement.get("transaction_id") != null){
				billPayResponse.setInTxnId(wsResponseElement.get("transaction_id").toString());
			}
			log.info("HubBillPayReversalCommunicator :: constructReplyMessage Status="+wsResponseElement.get("status"));
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
		return constantFieldsMap.get("topup");
	}
}
