package com.mfino.hub.xmlrpc.communicator;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.fix.CmFinoFIX.CMBillPaymentInquiryFromBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.hub.xmlrpc.CBillDataMsg;
import com.mfino.hub.xmlrpc.CXMLRPCMsg;
import com.mfino.hub.xmlrpc.utils.GenerateSignature;
import com.mfino.hub.xmlrpc.utils.StringUtilities;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

/**
 * @author Amar
 *
 */
public class HubBillPayInquiryCommunicator extends HubWebServiceCommunicator {

	public static final String BANK_NAME = "HubXMLRPC";
	private CBillDataMsg billdataMsg = new CBillDataMsg();
	
	@Override
	public CXMLRPCMsg createHubWebServiceRequest(MCEMessage mceMessage) {
		log.info("HubBillPayInquiryCommunicator :: createHubWebServiceRequest mceMessage="+mceMessage);
		
		CXMLRPCMsg hubWebServiceRequest = new CXMLRPCMsg();
		
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		
		CMBillPayInquiry request = (CMBillPayInquiry) requestFix;
		billPaymentsService.createBillPayments(request);
		Timestamp ts = request.getReceiveTime();
		Long amount = 0L;
		String trxnID = StringUtilities.getLastNChars(request.getTransactionID().toString(),6);
		
		hubWebServiceRequest.setRqtime(ts);
		hubWebServiceRequest.setRqid(trxnID);
		hubWebServiceRequest.setBillid(request.getInvoiceNumber());
		hubWebServiceRequest.setProduct(request.getBillerCode());
		hubWebServiceRequest.setAmount(request.getAmount().toString());
		hubWebServiceRequest.setTerminal(request.getSourceMDN());
		hubWebServiceRequest.setAgent(constantFieldsMap.get("agent"));
		hubWebServiceRequest.setCaid(constantFieldsMap.get("caid"));
	    
	    String sign = GenerateSignature.getRequestSign(constantFieldsMap.get("caid"), constantFieldsMap.get("passcode"), trxnID, ts, request.getInvoiceNumber(), amount);
	    log.info("HubBillPayInquiryCommunicator :: createHubWebServiceRequest Signature="+sign);
	    hubWebServiceRequest.setSign(sign);
		
		return hubWebServiceRequest;
	}

	@Override
	public CFIXMsg constructReplyMessage(Object response, CFIXMsg requestFixMessage) {
		
		HashMap<String,Object> wsResponseElement = (HashMap<String,Object>)response;
		BillPayResponse billPayResponse = new BillPayResponse();
		BillPayments billPayments = null;
		
		log.info("HubBillPayInquiryCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
			
		if(wsResponseElement != null && wsResponseElement.get("status") != null && wsResponseElement.get("status").equals("00")){
			
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			log.info("HubBillPayInquiryCommunicator :: constructReplyMessage Status="+wsResponseElement.get("status"));
			
			billPayResponse.setServiceChargeTransactionLogID(((CMBase) requestFixMessage).getServiceChargeTransactionLogID());
			billPayResponse.setInResponseCode(wsResponseElement.get("status").toString());
			billPayResponse.setAmount(BigDecimal.valueOf(Long.valueOf(wsResponseElement.get("amount").toString())));
			if(StringUtils.isNotBlank(wsResponseElement.get("reffno").toString()))
				billPayResponse.setOriginalReferenceID(Long.parseLong((String) wsResponseElement.get("reffno")));

			if(wsResponseElement.get("billdata") != null)
				billdataMsg.setXmlMsg((HashMap<String,Object>)wsResponseElement.get("billdata"));
			
			Long sctlId = ((CMBase) requestFixMessage).getServiceChargeTransactionLogID();
			billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
			log.info("HubBillPayInquiryCommunicator :: billdata ="+billdataMsg.toString());
			billPayments.setBillData(billdataMsg.toString());
			billPayments.setAmount(billPayResponse.getAmount());
			((CMBillPayInquiry) requestFixMessage).setAmount(billPayResponse.getAmount());
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
		return "Biller.BillInq";
	}
}
