package com.mfino.interemoney.nibss.communicator;

import static com.mfino.interemoney.nibss.util.NIBSSCashOutConstants.NIBSS_RESPONSE_SUCCESSFUL;

import java.util.ArrayList;
import java.util.List;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.interemoney.nibss.util.NIBSSCashOutResponse;
import com.mfino.interemoney.nibss.util.NIBSSCashOutResponseParser;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MCEUtil;

/**
 * @author Sasi
 *
 */
public class NIBSSNameEnqiuryCommunicator extends NIBSSCommunicator{

	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) {
		log.info("NIBSSNameEnquiryCommunicator :: getParameterList mceMessage="+mceMessage);
		
		List<Object> parameterList = new ArrayList<Object>();
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		parameterList.add(billPayments.getInfo1());//ben opCode or destCode
		parameterList.add(billPayments.getInvoiceNumber());//dest acc no
		
		
		return parameterList;
	}

	@Override
	public String getMessageName(MCEMessage mceMessage) {
		return "MM_BenNameEnquirySingleItem";
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage) {
		Object wsResponseElement = response.get(0);
		
		log.info("NIBSSNameEnquiryCommunicator :: constructReplyMessage ResponseElement="+wsResponseElement+" requestMceMessage="+requestMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMBase requestFix = (CMBase)requestMceMessage.getRequest();
		BackendResponse backendResponse = (BackendResponse) requestMceMessage.getResponse();
		
		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
		
		if(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE)){
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			billPayResponse.setResponse(-1);
			billPayResponse.setResult(-1);			
		} 
		else
		{
			NIBSSCashOutResponse nibssCashOutResponse = NIBSSCashOutResponseParser.getNIBSSCashOutResponse((String)wsResponseElement);
			
			billPayResponse.setInResponseCode(nibssCashOutResponse.getResponseCode());

			billPayResponse.setParentTransactionID(backendResponse.getParentTransactionID());
			billPayResponse.setTransferID(backendResponse.getTransferID());

			
			if(NIBSS_RESPONSE_SUCCESSFUL.equals(billPayResponse.getInResponseCode())){
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
				
				
				
				billPayResponse.setInfo3(nibssCashOutResponse.getBenName());
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
