package com.mfino.nfc.iso8583;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMNFCCardLinkFromCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardStatusFromCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkFromCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkReversalFromCMS;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
/**
 * 
 * @author Hemanth
 *
 */
public class NFCCardCommunicator {
	
	public MCEMessage createBackendResponse(MCEMessage mceMsg) {
		BackendResponse backendResponse= new BackendResponse();
		if(mceMsg.getResponse() instanceof CMNFCCardLinkFromCMS) {
			CMNFCCardLinkFromCMS cfixMsg= (CMNFCCardLinkFromCMS) mceMsg.getResponse();
			backendResponse.copy(cfixMsg);
			if(cfixMsg.getResponseCodeString().equals("00")) {
				backendResponse.setInternalErrorCode(NotificationCodes.NFCCardLinkSuccess.getInternalErrorCode());
				backendResponse.setResult(CmFinoFIX.ResponseCode_Success);
			} else {
				backendResponse.setInternalErrorCode(NotificationCodes.NFCCardLinkFailed.getInternalErrorCode());
				backendResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			}
			mceMsg.setResponse(backendResponse);
		} else if(mceMsg.getResponse() instanceof CMNFCCardUnlinkFromCMS) {
			CMNFCCardUnlinkFromCMS cfixMsg= (CMNFCCardUnlinkFromCMS) mceMsg.getResponse();
			backendResponse.copy(cfixMsg);
			if(cfixMsg.getResponseCodeString().equals("00")) {
				backendResponse.setInternalErrorCode(NotificationCodes.NFCCardUnlinkSuccess.getInternalErrorCode());
				backendResponse.setResult(CmFinoFIX.ResponseCode_Success);
			} else {
				backendResponse.setInternalErrorCode(NotificationCodes.NFCCardUnlinkFailed.getInternalErrorCode());
				backendResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			}
			mceMsg.setResponse(backendResponse);
		} else if(mceMsg.getResponse() instanceof CMNFCCardUnlinkReversalFromCMS) {
			CMNFCCardUnlinkReversalFromCMS cfixMsg= (CMNFCCardUnlinkReversalFromCMS) mceMsg.getResponse();
			backendResponse.copy(cfixMsg);
			if(cfixMsg.getResponseCodeString().equals("00")) {
				backendResponse.setResult(CmFinoFIX.ResponseCode_Success);
			} else {
				backendResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			}
			mceMsg.setResponse(backendResponse);
		}else if(mceMsg.getResponse() instanceof CMNFCCardStatusFromCMS) {
			CMNFCCardStatusFromCMS cfixMsg= (CMNFCCardStatusFromCMS) mceMsg.getResponse();
			backendResponse.copy(cfixMsg);
			if(cfixMsg.getResponseCodeString().equals("00")) {
				backendResponse.setInternalErrorCode(NotificationCodes.NFCCardActive.getInternalErrorCode());
				backendResponse.setResult(CmFinoFIX.ResponseCode_Success);
			} else {
				backendResponse.setInternalErrorCode(NotificationCodes.NFCCardNotActive.getInternalErrorCode());
				backendResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			}
			mceMsg.setResponse(backendResponse);
		}
		return mceMsg;	
	}
}
