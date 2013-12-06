package com.mfino.hub.xmlrpc.communicator;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.domain.BillPayments;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.fix.CmFinoFIX.CMBillPaymentInquiryFromBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.hub.xmlrpc.CBillDataMsg;
import com.mfino.hub.xmlrpc.CXMLRPCMsg;
import com.mfino.hub.xmlrpc.utils.GenerateSignature;
import com.mfino.hub.xmlrpc.utils.StringUtilities;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

/**
 * @author Amar
 *
 */
public class HubBillPayCommunicator extends HubWebServiceCommunicator {

	public static final String BANK_NAME = "HubXMLRPC";
	private CBillDataMsg billdataMsg;
	
	@Override
	public CXMLRPCMsg createHubWebServiceRequest(MCEMessage mceMessage) {
		log.info("HubBillPayCommunicator :: createHubWebServiceRequest mceMessage="+mceMessage);
		
		CXMLRPCMsg hubWebServiceRequest = new CXMLRPCMsg();
		
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();		
		
		CMBillPay request = (CMBillPay) requestFix;
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		if( billPayments != null && billPayments.getBillData() != null)
			billdataMsg = new CBillDataMsg(billPayments.getBillData());
		
		Timestamp ts = request.getReceiveTime();
		String trxnID = StringUtilities.getLastNChars(request.getTransactionID().toString(),6);
		String billID = billPayments.getInvoiceNumber();
		Long amount = billPayments.getAmount().longValue();
		
		if(billPayments != null)
			billPayments.setInfo3(request.getTransactionID().toString()); // We use this rqid to send in reversal as part of original request ID
		billPayments.setNoOfRetries(0);
		billPaymentsService.saveBillPayment(billPayments);
		
		hubWebServiceRequest.setRqtime(ts);
		hubWebServiceRequest.setRqid(trxnID);
		if(billPayments != null){
			hubWebServiceRequest.setBillid(billID);
			hubWebServiceRequest.setProduct(billPayments.getBillerCode());
			hubWebServiceRequest.setAmount(amount.toString());
			hubWebServiceRequest.setTerminal(billPayments.getSourceMDN());
		}
		hubWebServiceRequest.setAgent(constantFieldsMap.get("agent"));
		hubWebServiceRequest.setCaid(constantFieldsMap.get("caid"));
	    
	    String sign = GenerateSignature.getRequestSign(constantFieldsMap.get("caid"), constantFieldsMap.get("passcode"), trxnID, ts, billID, amount);
	    log.info("HubBillPayCommunicator :: createHubWebServiceRequest Signature="+sign);
	    hubWebServiceRequest.setSign(sign);
	    
	    if(billdataMsg != null)
	    	hubWebServiceRequest.setBillData(billdataMsg.getXmlMsg());
	    
	    //Save data in Integration Summary; 
 	 	/*IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummary integrationSummary = new IntegrationSummary();
		integrationSummary.setSctlId(sctlId);
		integrationSummary.setReconcilationID2(trxnID.toString()); // ReconcilationID2 for BillPay TransactionID
		integrationSummaryDao.save(integrationSummary);*/
		
		return hubWebServiceRequest;
	}

	@Override
	public CFIXMsg constructReplyMessage(Object response, CFIXMsg requestFixMessage) {
		
		HashMap<String,Object> wsResponseElement = (HashMap<String,Object>)response;
		BillPayResponse billPayResponse = new BillPayResponse();
		BillPayments billPayments = null;
		
		log.info("HubBillPayCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
		
		Long sctlId = ((CMBase) requestFixMessage).getServiceChargeTransactionLogID();
		
		if(wsResponseElement.get("status") != null && wsResponseElement.get("status").equals("00")){

			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setInResponseCode(wsResponseElement.get("status").toString());
			billPayResponse.setInTxnId(wsResponseElement.get("reffno").toString());
			billPayResponse.setServiceChargeTransactionLogID(sctlId);

			log.info("HubBillPayCommunicator :: constructReplyMessage Status="+wsResponseElement.get("status"));
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
		
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
 		if(billPayments != null){
 			if(billPayments.getInfo2() != null && billPayments.getInfo2().equalsIgnoreCase("online")){
 				return "Biller.BillPay";
 			}
 		}
		return "Biller.TopUp";
	}
}
