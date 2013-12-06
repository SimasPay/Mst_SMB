package com.mfino.zenith.interbank.impl;

import static com.mfino.zenith.interbank.impl.IBTConstants.TRASACTION_STATUS_WS_METHOD_NAME;

import java.util.ArrayList;
import java.util.List;

import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferStatus;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.core.ws.WSCommunicator;

/**
 * @author Sasi
 */
public class IBTWSGetTransactionStatus extends WSCommunicator {

	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) {
		log.debug("IBTWSGetTransactionStatus :: getParameterList() BEGIN");
		List<Object> paramList = new ArrayList<Object>();
		
		CMInterBankFundsTransferStatus ibFundsTransferStatus =  (CMInterBankFundsTransferStatus)mceMessage.getResponse();
		
		IBTWSData wsRequest = new IBTWSData();
		wsRequest.setDestinationBankCode(ibFundsTransferStatus.getDestBankCode());
		wsRequest.setChannelCode(ibFundsTransferStatus.getChannelCode());
		wsRequest.setPaymentReference(ibFundsTransferStatus.getPaymentReference());
		String requestXML = IBTParser.getXML(wsRequest, TRASACTION_STATUS_WS_METHOD_NAME);
		log.debug("IBTWSCommunicator :: getParameterList() : xmlData="+requestXML);
		
		paramList.add(requestXML);
		log.debug("IBTWSGetTransactionStatus :: getParameterList() END");
		return paramList;
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> wsResponse, MCEMessage requestMceMessage) {
		log.debug("IBTWSGetTransactionStatus :: getParameterList() BEGIN");
		Object wsResponseElement = wsResponse.get(0);
		log.debug("IBTWSGetTransactionStatus :: constructReplyMessage() response="+wsResponseElement);
		
		MCEMessage responseMessage = new MCEMessage();
		CMInterBankFundsTransferStatus txnStatusRequest = (CMInterBankFundsTransferStatus)requestMceMessage.getResponse();
		IBTBackendResponse response = new IBTBackendResponse();
		
		IBTWSData responseData = null;
		
		if(!(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE))){
			responseData = IBTParser.getIBTWSResponseData((String)wsResponseElement);
			
		}
		else{
			response.setWebServiceResponse(MCEUtil.SERVICE_UNAVAILABLE);
		}
		
		if(responseData != null){
			response.setDestinationBankCode(responseData.getDestinationBankCode());
			response.setChannelCode(responseData.getChannelCode());
			response.setPaymentReference(responseData.getPaymentReference());
			response.setSessionId(responseData.getSessionId());
			response.setResponseCode(responseData.getResponseCode());
		}
		else
		{
			response.setResponseCode(MCEUtil.SERVICE_UNAVAILABLE);

		}
		
		response.setServiceChargeTransactionLogID(txnStatusRequest.getServiceChargeTransactionLogID());
		response.setTransferID(txnStatusRequest.getParentTransactionID());
		
		responseMessage.setRequest(txnStatusRequest);
		responseMessage.setResponse(response);
		
		log.debug("IBTWSGetTransactionStatus :: getParameterList() END");
		return responseMessage;
	}

	@Override
	public String getMessageName(MCEMessage mceMessage) {
		return "getTransactionStatus";
	}
}
