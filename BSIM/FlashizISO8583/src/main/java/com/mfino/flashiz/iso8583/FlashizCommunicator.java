package com.mfino.flashiz.iso8583;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBankForBsim;
import com.mfino.fix.CmFinoFIX.CMQRPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
/**
 * 
 * @author Hemanth
 *
 */
public class FlashizCommunicator {
	
	public MCEMessage createBackendResponse(MCEMessage mceMsg) {
		BackendResponse backendResponse= new BackendResponse();
		if(mceMsg.getResponse() instanceof CMGetUserAPIKeyFromBank) {
			CMGetUserAPIKeyFromBank cfixMsg= (CMGetUserAPIKeyFromBank) mceMsg.getResponse();
			backendResponse.copy(cfixMsg);
			if(cfixMsg.getResponseCode().equals("00")) {
				backendResponse.setInternalErrorCode(NotificationCodes.GetUserAPIKeySuccess.getInternalErrorCode());
				backendResponse.setResult(CmFinoFIX.ResponseCode_Success);
			} else {
				backendResponse.setInternalErrorCode(NotificationCodes.GetUserAPIKeyFailed.getInternalErrorCode());
				backendResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			}
			backendResponse.setUserAPIKey(cfixMsg.getUserAPIKey());
			mceMsg.setResponse(backendResponse);
		} 
		return mceMsg;	
	}
	
	public MCEMessage processMessage(MCEMessage mceMsg) {
		CMPaymentAcknowledgementToBankForBsim ackToBank = new CMPaymentAcknowledgementToBankForBsim();
		CMQRPaymentToBank paymentRequest = (CMQRPaymentToBank) mceMsg.getRequest();
		CMQRPaymentFromBank paymentResponse = (CMQRPaymentFromBank) mceMsg.getResponse();
		
		if("00".equals(paymentResponse.getResponseCode())){
			ackToBank.copy(paymentRequest);
			ackToBank.setUserAPIKey(paymentRequest.getUserAPIKey());
			ackToBank.setMerchantData(paymentRequest.getMerchantData());
			ackToBank.setInvoiceNo(paymentRequest.getInvoiceNo());
			ackToBank.setInfo3(paymentRequest.getInfo3());
			ackToBank.setBillerCode(paymentRequest.getBillerCode());
			mceMsg.setResponse(ackToBank);
		}
		return mceMsg;
		}
	
}
