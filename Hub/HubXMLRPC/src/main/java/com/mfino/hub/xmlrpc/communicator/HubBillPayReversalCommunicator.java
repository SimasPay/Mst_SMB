package com.mfino.hub.xmlrpc.communicator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.util.QTBillPayUtil;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAutoReversal;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.fix.CmFinoFIX.CMBillPaymentInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBillPaymentReversal;
import com.mfino.hibernate.Timestamp;
import com.mfino.hub.xmlrpc.CBillDataMsg;
import com.mfino.hub.xmlrpc.CXMLRPCMsg;
import com.mfino.hub.xmlrpc.utils.GenerateSignature;
import com.mfino.hub.xmlrpc.utils.StringUtilities;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.service.TransactionLogService;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

/**
 * @author Amar
 *
 */
public class HubBillPayReversalCommunicator extends HubWebServiceCommunicator {

	public static final String BANK_NAME = "HubXMLRPC";
	private String revMsg = "";
	
	@Override
	public CXMLRPCMsg createHubWebServiceRequest(MCEMessage mceMessage) {
		log.info("HubBillPayReversalCommunicator :: createHubWebServiceRequest mceMessage="+mceMessage);
		
		CXMLRPCMsg hubWebServiceRequest = new CXMLRPCMsg();
		
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		TransactionsLog originalTxnLog = null ;
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		if(billPayments != null)
			originalTxnLog = transactionLogService.getById(Long.parseLong(billPayments.getInfo3()));

		//Creating new transaction_log to get new rqid, that we send as part of reversal along with original rqid
		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BillPaymentReversal,requestFix.DumpFields(),requestFix.getParentTransactionID());
		
		Timestamp ts = transactionsLog.getCreateTime();
		String trxnID = StringUtilities.getLastNChars(transactionsLog.getID().toString(),6);
		String billID = billPayments.getInvoiceNumber();
		Long amount = billPayments.getAmount().longValue();
		
		hubWebServiceRequest.setRqtime(transactionsLog.getCreateTime());
		hubWebServiceRequest.setRqid(trxnID);
		if(billPayments != null){
			hubWebServiceRequest.setBillid(billID);
			hubWebServiceRequest.setProduct(billPayments.getBillerCode());
			hubWebServiceRequest.setAmount(amount.toString());
			hubWebServiceRequest.setTerminal(billPayments.getSourceMDN());
		}
		hubWebServiceRequest.setAgent(constantFieldsMap.get("agent"));
		hubWebServiceRequest.setCaid(constantFieldsMap.get("caid"));
		//Original request parameters
		if(originalTxnLog != null){
			hubWebServiceRequest.setOrgRqId(originalTxnLog.getID().toString());
			hubWebServiceRequest.setOrgTime(originalTxnLog.getCreateTime());
			hubWebServiceRequest.setOrgMethod((billPayments.getInfo2() != null) ? "Biller.BillPay" : "Biller.TopUp"); // Info2 = MethodName
			hubWebServiceRequest.setRevCount( (billPayments.getNoOfRetries() != null) ? billPayments.getNoOfRetries().toString() : "0");
		}
	    
	    String sign = GenerateSignature.getRequestSign(constantFieldsMap.get("caid"), constantFieldsMap.get("passcode"), trxnID, ts, billID, amount);
	    log.info("HubBillPayReversalCommunicator :: createHubWebServiceRequest Signature="+sign);
	    hubWebServiceRequest.setSign(sign);
	    
	    //Updating the reversal count
	    Integer retries = billPayments.getNoOfRetries();
	    if(retries != null)
	    	billPayments.setNoOfRetries(retries + 1);
	    else
	    	billPayments.setNoOfRetries(0);
	    billPaymentsService.saveBillPayment(billPayments);
	    
	    //Updating Integration Summary and also getting Original Request data saved as part of ReconcilationID2
	    /*IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummaryQuery integrationSummaryQuery = new IntegrationSummaryQuery();
		integrationSummaryQuery.setSctlID(sctlId);
		
		List<IntegrationSummary> integrationSummaryList = integrationSummaryDao.get(integrationSummaryQuery);
		String originalRequestID = "";
		
		if((null != integrationSummaryList) && (integrationSummaryList.size() > 0)){
			IntegrationSummary iSummary = integrationSummaryList.iterator().next();

			if(null != iSummary && null != iSummary.getReconcilationID1()){
				originalRequestID = iSummary.getReconcilationID2();
			}
		}*/
		
		return hubWebServiceRequest;
	}

	@Override
	public CFIXMsg constructReplyMessage(Object response, CFIXMsg requestFixMessage) {
		
		HashMap<String,Object> wsResponseElement = (HashMap<String,Object>)response;
		BillPayResponse billPayResponse = new BillPayResponse();
		
		log.info("HubBillPayReversalCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
		
		if(wsResponseElement != null && wsResponseElement.get("status") != null && wsResponseElement.get("status").equals("00")){
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			billPayResponse.setServiceChargeTransactionLogID(((CMBase) requestFixMessage).getServiceChargeTransactionLogID());
			billPayResponse.setInResponseCode(wsResponseElement.get("status").toString());
			if(wsResponseElement.get("rqid") != null){
				billPayResponse.setInTxnId(wsResponseElement.get("rqid").toString());
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
		return "Biller.Reverse";
	}
}
