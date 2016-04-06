package com.mfino.flashiz.iso8583;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBankForBsim;
import com.mfino.fix.CmFinoFIX.CMPaymentAuthorizationToBankForBsim;
import com.mfino.fix.CmFinoFIX.CMQRPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentInquiryToBank;
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
		CFIXMsg request = mceMsg.getRequest();
		CFIXMsg response = mceMsg.getResponse();
		if (response instanceof CMPaymentAcknowledgementToBankForBsim) {
			CMPaymentAcknowledgementToBankForBsim ackToBank = (CMPaymentAcknowledgementToBankForBsim) response;
			ackToBank.setIsAdvice(true);
			mceMsg.setResponse(ackToBank);
			return mceMsg;
		}
		else if (request instanceof CMPaymentAcknowledgementToBankForBsim) {
			CMPaymentAcknowledgementToBankForBsim ackToBank = (CMPaymentAcknowledgementToBankForBsim) request;
			ackToBank.setIsAdvice(true);
			mceMsg.setResponse(ackToBank);
			return mceMsg;
		}
		else if(request instanceof CMQRPaymentToBank) {
			CMPaymentAcknowledgementToBankForBsim ackToBank = new CMPaymentAcknowledgementToBankForBsim();
			CMQRPaymentToBank paymentRequest = (CMQRPaymentToBank)request;
			CMQRPaymentFromBank paymentResponse = (CMQRPaymentFromBank) mceMsg.getResponse();

			if("00".equals(paymentResponse.getResponseCode())){
				ackToBank.copy(paymentRequest);
				ackToBank.setUserAPIKey(paymentRequest.getUserAPIKey());
				ackToBank.setMerchantData(paymentRequest.getMerchantData());
				ackToBank.setInvoiceNo(paymentRequest.getInvoiceNo());
				ackToBank.setInfo3(paymentRequest.getInfo3());
				ackToBank.setBillerCode(paymentRequest.getBillerCode());
				ackToBank.setDiscountAmount(paymentRequest.getDiscountAmount());
				ackToBank.setDiscountType(paymentRequest.getDiscountType());
				ackToBank.setNumberOfCoupons(paymentRequest.getNumberOfCoupons());
				ackToBank.setLoyalityName(paymentRequest.getLoyalityName());
				ackToBank.setTippingAmount(paymentRequest.getTippingAmount());
				ackToBank.setPointsRedeemed(paymentRequest.getPointsRedeemed());
				ackToBank.setAmountRedeemed(paymentRequest.getAmountRedeemed());
				mceMsg.setResponse(ackToBank);
			}
			return mceMsg;
		}
		else if(request instanceof CMQRPaymentInquiryToBank) {
			CMPaymentAuthorizationToBankForBsim authToBank = new CMPaymentAuthorizationToBankForBsim();
			CMQRPaymentInquiryToBank paymentInquiryRequest = (CMQRPaymentInquiryToBank) request;
			CMQRPaymentInquiryFromBank paymentInquiryResponse = (CMQRPaymentInquiryFromBank) mceMsg.getResponse();
			
			if("00".equals(paymentInquiryResponse.getResponseCode())){
				authToBank.copy(paymentInquiryRequest);
				authToBank.setUserAPIKey(paymentInquiryRequest.getUserAPIKey());
				authToBank.setMerchantData(paymentInquiryRequest.getMerchantData());
				authToBank.setInvoiceNo(paymentInquiryRequest.getInvoiceNo());
				authToBank.setDiscountAmount(paymentInquiryRequest.getDiscountAmount());
				authToBank.setDiscountType(paymentInquiryRequest.getDiscountType());
				authToBank.setNumberOfCoupons(paymentInquiryRequest.getNumberOfCoupons());
				authToBank.setLoyalityName(paymentInquiryRequest.getLoyalityName());
				authToBank.setTippingAmount(paymentInquiryRequest.getTippingAmount());
				authToBank.setPointsRedeemed(paymentInquiryRequest.getPointsRedeemed());
				authToBank.setAmountRedeemed(paymentInquiryRequest.getAmountRedeemed());
				//authToBank.setInfo3(paymentInquiryRequest.getInfo3());
				authToBank.setBillerCode(paymentInquiryRequest.getBillerCode());
				mceMsg.setResponse(authToBank);
			}
			return mceMsg;
		}
		return null;
	}

}
