package com.mfino.billpayments.communicator;

import static com.mfino.billpayments.BillPayConstants.TERMINAL_ID_KEY;
import static com.mfino.billpayments.QTBillPayConstants.QT_RESPONSE_SUCCESSFUL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mfino.billpayments.BillPayConstants;
import com.mfino.billpayments.QTBillPayConstants;
import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.beans.QTBillPayAdviceResponse;
import com.mfino.billpayments.beans.QTBillPaymentAdvice;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.billpayments.util.QTBillPayResponseParser;
import com.mfino.billpayments.util.QTBillPayUtil;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.BillPayments;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.service.impl.SubscriberServiceImpl;


/**
 * @author Sasi
 *
 */
public class QTBillPayAdviceCommunicator extends QuickTellerCommunicator{

	private Map<String, String> params;
	private BillPaymentsService billPaymentsService;
 
	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) {
		log.info("QTBillPayAdviceCommunicator :: getParameterList mceMessage="+mceMessage);

		List<Object> parameterList = new ArrayList<Object>();
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMdn subscriberMdn = subscriberMdnDao.getByMDN(sctl.getSourcemdn());
		
		String emailAddress = subscriberMdn.getSubscriber().getEmail();
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		String requestReference = QTBillPayUtil.getRequestReference(params.get(BillPayConstants.PREFIX), sctlId);
 	 	IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummary integrationSummary = new IntegrationSummary();
		integrationSummary.setSctlid(sctlId);
		integrationSummary.setReconcilationid1(requestReference);
		integrationSummaryDao.save(integrationSummary);
		
		QTBillPaymentAdvice billPayAdvice = new QTBillPaymentAdvice();
		billPayAdvice.setAmount( billPayments.getAmount().multiply(new BigDecimal(100.00)).setScale(0,BigDecimal.ROUND_DOWN));
		log.info("Amount:BillPayMoneyTransferServiceImpl"+billPayments.getAmount());
		billPayAdvice.setPaymentCode(billPayments.getPartnerbillercode());
//this change was made as per fortis requirement 22-02-2013 tarun.
		
		billPayAdvice.setCustomerMobile(sctl.getSourcemdn());
		billPayAdvice.setCustomerEmail(emailAddress);
		billPayAdvice.setCustomerId(billPayments.getInvoicenumber());
		
		TransactionType airtimePurchase = DAOFactory.getInstance().getTransactionTypeDAO().getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE);
		Long airtimePurchaseTxnId = airtimePurchase != null ? airtimePurchase.getId().longValue() : 0l;
		SubscriberServiceImpl subscriberServiceImpl = new SubscriberServiceImpl();
		if(sctl.getTransactiontypeid().intValue() != 0 && sctl.getTransactiontypeid().equals(airtimePurchaseTxnId)){
			billPayAdvice.setCustomerMobile("0"+subscriberServiceImpl.deNormalizeMDN(sctl.getSourcemdn()));
			billPayAdvice.setCustomerId("0"+subscriberServiceImpl.deNormalizeMDN(billPayments.getInvoicenumber()));
		}
		
		
		billPayAdvice.setTerminalId(params.get(TERMINAL_ID_KEY));
		billPayAdvice.setRequestReference(requestReference);
		
		log.info("QTBillPayAdviceCommunicator :: billPayAdvice="+billPayAdvice.toXML());
		parameterList.add(billPayAdvice.toXML());
		
		return parameterList;
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage) {
		Object wsResponseElement = response.get(0);

		log.info("QTBillPayAdviceCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestMceMessage="+requestMceMessage);
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
			QTBillPayAdviceResponse qtBillPayAdviceResponse = QTBillPayResponseParser.getBillPayAdviceResponse((String)wsResponseElement);
			
			billPayResponse.setInResponseCode(qtBillPayAdviceResponse.getResponseCode());
			billPayResponse.setInTxnId(qtBillPayAdviceResponse.getTransactionReference());
			billPayResponse.setRechargePin(qtBillPayAdviceResponse.getRechargePin());
			
			if(QT_RESPONSE_SUCCESSFUL.equals(billPayResponse.getInResponseCode())
					|| QTBillPayConstants.QT_RESPONSE_SUCCESSFUL_2.equals(billPayResponse.getInResponseCode())){
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
			}
			else{
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			}
		}
		
		responseMceMessage.setRequest(requestMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);

		return responseMceMessage;
	}
	
	@Override
	public String getMessageName(MCEMessage mceMessage) {
		return "SendBillPaymentAdvice";
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}
}
