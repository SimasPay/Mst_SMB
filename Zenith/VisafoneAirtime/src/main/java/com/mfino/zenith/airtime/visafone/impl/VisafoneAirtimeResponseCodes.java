package com.mfino.zenith.airtime.visafone.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sasi
 *
 */
public enum VisafoneAirtimeResponseCodes {
	
/*	OPERATION_SUCCESSFUL(1000, "OPERATION_SUCCESSFUL"),
	TRANSACTION_ID_DNE(1001, "TRANSACTION_ID_DNE"),
	MOBILE_NO_DNE(1002, "MOBILE_NO_DNE"),
	INVALID_ACCOUNT(1003, "INVALID_ACCOUNT"),
	UNKNOWN_NUMBER(1004, "UNKNOWN_NUMBER"),
	INVALID_AMOUNT(1005, "INVALID_AMOUNT"),
	UNABLE_TO_COMMUNICATE_WITH_WEBSERVICE(9999, "UNABLE_TO_COMMUNICATE_WITH_WEBSERVICE");*/

	OPERATION_SUCCESSFUL(1000, "OPERATION_SUCCESSFUL"),
	TRANSACTION_ID_ALREADY_EXISTS(1001, "TRANSACTION_ID_ALREADY_EXISTS"),
	MOBILE_NO_DNE(1002, "MOBILE_NO_DNE"),
	ACCESS_EXPIRED(1003, "ACCESS_EXPIRED"),
	INSUFFICIENT_BALANCE(1004, "INSUFFICIENT_BALANCE"),
	OTHER_ERROR(1005, "OTHER_ERROR"),
	ACCESS_DENIED(1006, "ACCESS_DENIED"),
	INVALID_INPUT_PARAMETER_VALUE(1007, "INVALID_INPUT_PARAMETER_VALUE"),
	INTERNAL_SYSTEM_ERROR(1008, "INTERNAL_SYSTEM_ERROR"),
	UNABLE_TO_COMMUNICATE_WITH_WEBSERVICE(9999, "UNABLE_TO_COMMUNICATE_WITH_WEBSERVICE");

	
	VisafoneAirtimeResponseCodes(Integer responseCode, String displayString){
		this.responseCode = responseCode;
		this.displayString = displayString;
	}
	
	private Integer responseCode;
	private String displayString;
	private static Map<Integer,VisafoneAirtimeResponseCodes> responseCodesMap = null;

	static
	{
		responseCodesMap = new HashMap<Integer, VisafoneAirtimeResponseCodes>();
		
		VisafoneAirtimeResponseCodes[] responseCodes = VisafoneAirtimeResponseCodes.values();
		
		for (int i = 0; i < responseCodes.length; i++) {
			responseCodesMap.put(responseCodes[i].getInternalErrorCode(), responseCodes[i]);
		}
	}
	
	public Integer getInternalErrorCode() {
		return responseCode;
	}
	
	public void setInternalErrorCode(Integer internalErrorCode) {
		this.responseCode = internalErrorCode;
	}
	
	public String getDisplayString() {
		return displayString;
	}
	
	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}
	
	public static VisafoneAirtimeResponseCodes getResponseCode(Integer responseCode){
		return responseCodesMap.get(responseCode);
	}
	
	@Override
	public String toString() {
		return displayString;
	}
}
