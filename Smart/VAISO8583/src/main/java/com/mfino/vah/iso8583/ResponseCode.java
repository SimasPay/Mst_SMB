package com.mfino.vah.iso8583;

public class ResponseCode {

	public static final String APPROVED = "00";
//	public static final String ERROR = "06";
	public static final String INVALID_TRANSACTION="12";
	public static final String INVALID_RECORD="25";
	public static final String FORMAT_ERROR = "30";
	public static final String INVALID_BANKID = "31";
	public static final String REQUEST_NOT_SUPPORTED= "40";
	public static final String SECURITY_VIOLATION = "63";
	public static final String INVALID_DATES = "89";
	public static final String CUTOFF_STARTED = "90";
	public static final String ISSUER_INOPERATIVE = "91";
	public static final String DUPLICATE_TRANSMISSION = "94";
	public static final String SYSTEM_MALFUNCTION = "96";
	//specific for vah only. we use 76 in place of 06
	public static final String VAH_ERROR = "76";
	
	
}
