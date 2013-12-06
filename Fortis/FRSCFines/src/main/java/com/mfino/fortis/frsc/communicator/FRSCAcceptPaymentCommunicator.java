package com.mfino.fortis.frsc.communicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fortis.frsc.util.FRSCConstants;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;


/**
 * @author Bala Sunku
 *
 */
public class FRSCAcceptPaymentCommunicator extends FRSCCommunicator {
	
	private String paymentDateFormat = "yyyy/MM/dd";
	private String paymentTimeFormat = "HH:mm";

	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) {
		log.info("FRSCAcceptPaymentCommunicator :: getParameterList mceMessage="+mceMessage);

		List<Object> parameterList = new ArrayList<Object>();
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getById(sctlId);
		
		String paymentDate = new SimpleDateFormat(paymentDateFormat).format(sctl.getCreateTime());
		String paymentTime = new SimpleDateFormat(paymentTimeFormat).format(sctl.getCreateTime());
		parameterList.add(getParams().get(FRSCConstants.SECURITY_TOKEN));
		parameterList.add(sctlId.toString());
		parameterList.add(billPayments.getInvoiceNumber());
		parameterList.add(billPayments.getInfo2());	// Offenders name
		parameterList.add(billPayments.getAmount().toString());
		parameterList.add(paymentDate);
		parameterList.add(paymentTime);


		return parameterList;
	}

	@Override
	public String getMessageName(MCEMessage mceMessage) {
		return "AcceptPayment";
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage) {
		Object wsResponseElement = response.get(0);

		log.info("FRSCAcceptPaymentCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestMceMessage="+requestMceMessage);
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
			Integer frscPaymentResponse = (Integer)wsResponseElement;
			billPayResponse.setInResponseCode(frscPaymentResponse.toString());
			
			if(FRSCConstants.FRSC_RESPONSE_SUCCESSFUL == frscPaymentResponse){
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
}
