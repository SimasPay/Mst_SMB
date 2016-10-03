package com.mfino.billpayments.communicator;

import static com.mfino.billpayments.BillPayConstants.TERMINAL_ID_KEY;
import static com.mfino.billpayments.QTBillPayConstants.QT_RESPONSE_SUCCESSFUL;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.beans.QTBillPaymentInquiry;
import com.mfino.billpayments.beans.QTBillPaymentInquiryResponse;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.billpayments.util.QTBillPayResponseParser;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MCEUtil;


/**
 * @author Sasi
 *
 */
public class QTBillPayInquiryCommunicator extends QuickTellerCommunicator{

	private Map<String, String> params;
	private BillPaymentsService billPaymentsService;
	
	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) {
		log.info("QTBillPayInquiryCommunicator :: getParameterList mceMessage="+mceMessage);
		
		Long sctlId = ((CMBase)mceMessage.getRequest()).getServiceChargeTransactionLogID();
		BillPayments billPayment = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMdn subscriberMdn = subscriberMdnDao.getByMDN(sctl.getSourcemdn());
		
		String emailAddress = subscriberMdn.getSubscriber().getEmail();
		
		QTBillPaymentInquiry qtBillPayInquiry = new QTBillPaymentInquiry();
		qtBillPayInquiry.setPaymentCode(billPayment.getPartnerbillercode());
		qtBillPayInquiry.setCustomerMobile(billPayment.getInvoicenumber());
		qtBillPayInquiry.setCustomerEmail(emailAddress);
		qtBillPayInquiry.setCustomerId(sctl.getSourcemdn());
		qtBillPayInquiry.setTerminalId(params.get(TERMINAL_ID_KEY));
		
		log.info("QTBillPayInquiryCommunicator : getParameterList "+qtBillPayInquiry.toXML());
		
		List<Object> parameterList = new ArrayList<Object>();
		parameterList.add(qtBillPayInquiry.toXML());
		
		return parameterList;
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage) {
		Object wsResponseElement = response.get(0);
		log.info("QTBillPayInquiryCommuicator Response for DoBillPayInquiry = " +wsResponseElement);
		
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMBase requestFix = (CMBase)requestMceMessage.getRequest();
		BackendResponse backendResponse = (BackendResponse) requestMceMessage.getResponse();
		
		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		
		if(wsResponseElement.equals(MCEUtil.SERVICE_TIME_OUT)){
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_TIME_OUT);
		}
		else if(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE)){
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
		}
		else
		{
			QTBillPaymentInquiryResponse qtBillPaymentInquiryResponse = QTBillPayResponseParser.getBillPaymentInquiryResponse((String)wsResponseElement);
			
			billPayResponse.setInResponseCode(qtBillPaymentInquiryResponse.getResponseCode());
			billPayResponse.setInTxnId(qtBillPaymentInquiryResponse.getTransactionReference());
			billPayResponse.setBiller(qtBillPaymentInquiryResponse.getBiller());
			billPayResponse.setParentTransactionID(backendResponse.getParentTransactionID());
			billPayResponse.setTransferID(backendResponse.getTransferID());
			
			if(QT_RESPONSE_SUCCESSFUL.equals(billPayResponse.getInResponseCode())){
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
		return "DoBillPaymentInquiry";
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
	
	public List<Object> handleWSCommunicationException(Exception e){
		List<Object> responseFromWS = new ArrayList<Object>();
		
  		if(e.getCause() instanceof SocketTimeoutException){
			responseFromWS.add(MCEUtil.SERVICE_TIME_OUT);
		}
		else{
			responseFromWS.add(MCEUtil.SERVICE_UNAVAILABLE);
		}
		
		return responseFromWS;
	}
}
