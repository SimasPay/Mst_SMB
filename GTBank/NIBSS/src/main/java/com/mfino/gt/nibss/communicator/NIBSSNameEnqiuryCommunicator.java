package com.mfino.gt.nibss.communicator;

import static com.mfino.gt.nibss.util.NIBSSCashOutConstants.NIBSS_RESPONSE_SUCCESSFUL;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.gt.nibss.util.NIBSSCashOutResponse;
import com.mfino.gt.nibss.util.NIBSSCashOutResponseParser;
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
		log.info("NIBSSNameEnqiuryCommunicator :: getParameterList mceMessage="+mceMessage);
		List<Object> parameterList = new ArrayList<Object>();
 		CMBase requestFix = (CMBase)mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		parameterList.add(billPayments.getInfo1());
		parameterList.add(billPayments.getInvoiceNumber());
		
		return parameterList;
	}

	@Override
	public String getMessageName(MCEMessage mceMessage) {
		return "MM_BenNameEnquirySingleItem";
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage) {
		Object wsResponseElement = response.get(0);
		log.info("NIBSSNameEnqiuryCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestMceMessage="+requestMceMessage);
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
			billPayResponse.setResponse(-1);
			billPayResponse.setResult(-1);			
		} 
		else
		{
			NIBSSCashOutResponse nibssCashOutResponse = NIBSSCashOutResponseParser.getNIBSSCashOutResponse((String)wsResponseElement);
			String inResponse = nibssCashOutResponse.getResponseCode() + 
					(StringUtils.isNotBlank(nibssCashOutResponse.getError()) ? ":" + nibssCashOutResponse.getError() : StringUtils.EMPTY);
			if (StringUtils.isNotBlank(inResponse) && inResponse.length() > 255) {
				inResponse = inResponse.substring(0,255);
			}
			
			billPayResponse.setInResponseCode(inResponse);

			billPayResponse.setParentTransactionID(backendResponse.getParentTransactionID());
			billPayResponse.setTransferID(backendResponse.getTransferID());

			
			if(NIBSS_RESPONSE_SUCCESSFUL.equals(nibssCashOutResponse.getResponseCode())){
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
				billPayResponse.setInfo3(nibssCashOutResponse.getBenName());
				billPayResponse.setBeneficiaryName(nibssCashOutResponse.getBenName());
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
