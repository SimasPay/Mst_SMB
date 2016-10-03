package com.mfino.billpayments.communicator;

import static com.mfino.billpayments.QTBillPayConstants.QT_RESPONSE_SUCCESSFUL;

import java.util.ArrayList;
import java.util.List;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.beans.QTQueryTransaction;
import com.mfino.billpayments.beans.QTQueryTransactionResponse;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.billpayments.util.QTBillPayResponseParser;
import com.mfino.billpayments.util.QTBillPayUtil;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.IntegrationSummary;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;

/**
 * @author Sasi
 *
 */
public class QTQueryTransactionCommunicator extends QuickTellerCommunicator {

	private BillPaymentsService billPaymentsService;
	
	private String quickTellerPrefix;
	
	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) {
		log.info("QTQueryTransactionCommunicator :: getParameterList()");

		List<Object> parameterList = new ArrayList<Object>();
		
		CMBase cmBase = (CMBase)mceMessage.getRequest();
		Long sctlId = cmBase.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummaryQuery integrationSummaryQuery = new IntegrationSummaryQuery();
		integrationSummaryQuery.setSctlID(sctlId);
		
		String requestReference = "";
		List<IntegrationSummary> integrationSummaryList = integrationSummaryDao.get(integrationSummaryQuery);

		if((null != integrationSummaryList) && (integrationSummaryList.size() > 0)){
			IntegrationSummary iSummary = integrationSummaryList.iterator().next();
			if(null != iSummary && null != iSummary.getReconcilationid1()){
				requestReference = iSummary.getReconcilationid1();
			}
			else{
				requestReference = QTBillPayUtil.getRequestReference(getQuickTellerPrefix(), sctlId);
			}
		}
		
		QTQueryTransaction queryTransaction = new QTQueryTransaction();
		queryTransaction.setRequestReference(requestReference);
		
		String qtXml = queryTransaction.toXML();
		
		parameterList.add(qtXml);

		log.info("QTQueryTransactionCommunicator :: getParameterList() xml="+qtXml);
		return parameterList;
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage) {

		Object wsResponseElement = response.get(0);
		log.info("QTQueryTransactionCommunicator :: constructReplyMessage response="+wsResponseElement);
		
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMBase requestFix = (CMBase)requestMceMessage.getRequest();
		
		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());

		if(wsResponseElement.equals(MCEUtil.SERVICE_TIME_OUT)){
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_TIME_OUT);
		}
		else if(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE)){
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			billPayResponse.setResponse(-1);
			billPayResponse.setResult(-1);			
		} 
		else
		{
			QTQueryTransactionResponse qtResponse =  QTBillPayResponseParser.getQueryTransactionResponse((String)wsResponseElement);
			
			
			if(QT_RESPONSE_SUCCESSFUL.equals(qtResponse.getResponseCode())
					&&QT_RESPONSE_SUCCESSFUL.equals(qtResponse.getTransactionResponseCode())){
				billPayResponse.setInResponseCode(qtResponse.getTransactionResponseCode());				
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			}
			else{
				billPayResponse.setInResponseCode(MCEUtil.SERVICE_TIME_OUT);
			}
		}
		
		responseMceMessage.setRequest(requestMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);

		return responseMceMessage;
	}

	@Override
	public String getMessageName(MCEMessage mceMessage) {
		return "QueryTransaction";
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

	public String getQuickTellerPrefix() {
		return quickTellerPrefix;
	}

	public void setQuickTellerPrefix(String quickTellerPrefix) {
		this.quickTellerPrefix = quickTellerPrefix;
	}
}
