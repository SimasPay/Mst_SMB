package com.mfino.zenith.interbank.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sasi
 *
 */
public enum NIBSSResponseCode {
	
	APPROVED("00", "Approved", "APPROVED"),
	INVALID_SENDER("03", "Invalid Sender", "INVALID_SENDER"),
	DO_NOT_HONOR("05", "Do not Honor", "DO_NOT_HONOR"),
	ACCOUNT_DORMANT("06", "Account is Dormant", "ACCOUNT_DORMANT"),
	INVALID_ACCOUNT("07", "Invalid Account", "INVALID_ACCOUNT"),
	ACCOUNT_NAME_MISMATCH("08", "Account Name Mismatch", "ACCOUNT_NAME_MISMATCH"),
	REQUEST_IN_PROGRESS("09", "Request Processing in Progress", "REQUEST_IN_PROGRESS"),
	INVALID_TRANSACTION("12", "Invalid Transaction", "INVALID_TRANSACTION"),
	INVALID_AMOUNT("13", "Invalid Amount", "INVALID_AMOUNT"),
	INVALID_BATCH_NUMBER("14", "Invalid Batch Number", "INVALID_BATCH_NUMBER"),
	INVALID_SESSION_OR_RECORD("15", "Invalid session or Record ID", "INVALID_SESSION_OR_RECORD"),
	UNKNOWN_BANK_CODE("16", "Unknown Bank Code", "UNKNOWN_BANK_CODE"),
	CHANNEL_NOT_ENABLED("17", "This Channel is not enabled for transactions", "CHANNEL_NOT_ENABLED"),
	WRONG_METHOD_CALL("18", "Wrong Method Call", "WRONG_METHOD_CALL"),
	NO_ACTION_TAKEN("21", "No action taken", "NO_ACTION_TAKEN"),
	UNABLE_TO_LOCATE_RECORD("25", "Unable to locate record", "UNABLE_TO_LOCATE_RECORD"),
	DUPLICATE_RECORD("26", "Duplicate record", "DUPLICATE_RECORD"),
	FORMAT_ERROR("30", "Transaction failed at the other bank due to Format Error", "FORMAT_ERROR"),
	SUSPECTED_FRAUD("34", "Suspected Fraud", "SUSPECTED_FRAUD"),
	CONTACT_SENDING_BANK("35", "Contact sending bank", "CONTACT_SENDING_BANK"),
	INSUFFICIENT_FUNDS("51", "No sufficient funds", "INSUFFICIENT_FUNDS"),
	NOT_PERMITTED_TO_SENDER("57", "Transaction not permitted to sender", "NOT_PERMITTED_TO_SENDER"),
	NOT_PERMITTED_ON_CHANNEL("58", "Transaction not permitted on channel", "NOT_PERMITTED_ON_CHANNEL"),
	TRANSFER_LIMIT_EXCEEDED("61", "Transfer Limit Exceeded", "TRANSFER_LIMIT_EXCEEDED"),
	SECURITY_VIOLATION("63", "Security Violation", "SECURITY_VIOLATION"),
	EXCEEDS_WITHDRAWAL_FREQUENCY("65", "Exceeds withdrawal frequency", "EXCEEDS_WITHDRAWAL_FREQUENCY"),
	RESPONSE_RECEIVED_TOO_LATE("68", "Response received too late", "RESPONSE_RECEIVED_TOO_LATE"),
	BENEFICIARY_BANK_NOT_AVAILABLE("91", "Beneficiary Bank not available", "BENEFICIARY_BANK_NOT_AVAILABLE"),
	ROUTING_ERROR("92", "Transaction failed at the other bank due to Routing Error", "ROUTING_ERROR"),
	DUPLICATE_TRANSACTION("94", "Duplicate Transaction", "DUPLICATE_TRANSACTION"),
	SYSTEM_MALFUNCTION("96", "Transaction failed at the other bank due to System Malfunction", "SYSTEM_MALFUNCTION");
	
	NIBSSResponseCode(String responseCode, String description, String displayText){
		this.responseCode = responseCode;
		this.description = description;
		this.displayText = displayText;
	}
	
	private String responseCode;
	private String description;
	private String displayText;
	
	private static Map<String,NIBSSResponseCode> responseCodesMap = null;

	static
	{
		responseCodesMap = new HashMap<String, NIBSSResponseCode>();
		NIBSSResponseCode[] responseCodes = NIBSSResponseCode.values();
		
		for (int i = 0; i < responseCodes.length; i++) {
			responseCodesMap.put(responseCodes[i].getResponseCode(), responseCodes[i]);
		}
	}
	
	public String getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDisplayText() {
		return displayText;
	}
	
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
	
	public NIBSSResponseCode getNIBSSResponse(String responseCode){
		NIBSSResponseCode response = responseCodesMap.get(responseCode);
		
		return response;
	}
}
