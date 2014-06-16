package com.mfino.bayar.communicator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mfino.bayar.service.BayarWebServiceResponse;
import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillInquiry;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
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
		CMBillInquiry request = (CMBillInquiry) requestFix;
		
		requestParams.add(new BasicNameValuePair("product_code", request.getDenominationCode()));
		requestParams.add(new BasicNameValuePair("bill_number", request.getInvoiceNumber()));
		
		return requestParams;
	}

	@Override
	public CFIXMsg constructReplyMessage(Object response, CFIXMsg requestFixMessage) {
		
		BayarWebServiceResponse wsResponseElement = (BayarWebServiceResponse)response;
		BillPayResponse billPayResponse = new BillPayResponse();
		CMBase requestFix = (CMBase)requestFixMessage;
		
		log.info("BayarBillPayInquiryCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
			
		if(wsResponseElement != null && wsResponseElement.getStatus() != null && wsResponseElement.getStatus().intValue() == 0){
			
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			log.info("BayarBillPayInquiryCommunicator :: constructReplyMessage Status="+wsResponseElement.getStatus());
			
			if(wsResponseElement.getPaymentCode() != null)
				billPayResponse.setBillPaymentReferenceID(wsResponseElement.getPaymentCode().toString());
			if(wsResponseElement.getGrandTotal() != null){
				billPayResponse.setAmount(new BigDecimal(wsResponseElement.getGrandTotal()));
			}
			if(wsResponseElement.getFee() != null)
				billPayResponse.setCharges(new BigDecimal(wsResponseElement.getFee()));
			if(wsResponseElement.getBillName() != null)
				billPayResponse.setDestinationUserName(wsResponseElement.getBillName());
			if(wsResponseElement.getBillInfo() != null)
				billPayResponse.setAdditionalInfo(wsResponseElement.getBillInfo()); //Storing bill date information AS IS
			log.info("BayarBillPayInquiryCommunicator :: payment_code ="+wsResponseElement.getPaymentCode());
			
		}else{
			//billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
		}
		
//		if(wsResponseElement.getStatus() != null)
//			billPayResponse.setInResponseCode(wsResponseElement.getStatus().toString());
		billPayResponse.setInternalErrorCode(getInternalErrorCode(billPayResponse.getResult()));
		billPayResponse.setSourceMDN(requestFix.getSourceMDN());
		if(wsResponseElement.getMessage() != null)
			billPayResponse.setOperatorMessage(wsResponseElement.getMessage()); // Storing return message in OperatorMessage column of bill_payments
		
		billPayResponse.header().setSendingTime(DateTimeUtil.getLocalTime());
		billPayResponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return billPayResponse;
	}
	
	public MCEMessage constructResponseMessage(MCEMessage mceMceMessage) {
		
		log.info("BayarBillPayInquiryCommunicator :: constructResponseMessage() BEGIN mceMessage="+mceMceMessage);
		
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
		
		log.info("BayarBillPayInquiryCommunicator :: constructResponseMessage() END");
		return responseMceMessage;
	}
	
	@Override
	public String getMethodName(MCEMessage mceMessage) {
		return constantFieldsMap.get("billpay_inquiry");
	}
	
	private Integer getInternalErrorCode(Integer responseCode) {
		if(responseCode.equals(CmFinoFIX.ResponseCode_Success)){
			return NotificationCodes.BillDetails.getInternalErrorCode();
		}
		return NotificationCodes.GetBillDetailsFailed.getInternalErrorCode();
	}
}
