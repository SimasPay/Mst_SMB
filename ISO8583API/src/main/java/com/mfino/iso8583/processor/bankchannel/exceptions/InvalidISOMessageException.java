package com.mfino.iso8583.processor.bankchannel.exceptions;

import com.mfino.iso8583.WrapperISOMessage;

public class InvalidISOMessageException extends Exception {
	
	public InvalidISOMessageException(WrapperISOMessage isoMsg,String code,String message) {
		super(message);
		this.setIsoMsg(isoMsg);
		this.setResponseCode(code);
	}
	private WrapperISOMessage isoMsg;
	private String responseCode;

//	private Class<? extends WrapperISOMessage> isoMsgType;
//	public Class<? extends WrapperISOMessage> getIsoMsgType(){
//		return isoMsgType;
//	}
	public WrapperISOMessage getIsoMsg() {
	    return isoMsg;
    }
	private void setIsoMsg(WrapperISOMessage isoMsg) {
	    this.isoMsg = isoMsg;
    }
	public String getResponseCode() {
	    return responseCode;
    }
	private void setResponseCode(String responseCode) {
	    this.responseCode = responseCode;
    }
}
