package com.mfino.fep;


public class FEPConstants {

	public static final String REQUEST_MSG_TYPE = "0200";
	public static final String RESPONSE_MSG_TYPE = "0210";
	public static final String REVERSAL_REQUEST_MSG_TYPE = "0420";
	public static final String REVERSAL_RESPONSE_MSG_TYPE = "0430";
	public static final String REVERSAL_ADDLN_REQUEST_MSG_TYPE = "0421";
	public static final String NETWORK_REQUEST_MSG_TYPE = "0800";
	public static final String WITHDRAW_REQUEST = "010000";

	public static final String MDNTAG = "<BufferB>";
	public static final String FACTAG = "<BufferC>";
	
	public static final String ISORESPONSE_SYSTEM_MALFUNCTION = "sysmalfunction";
	public static final String ISORESPONSE_INVALID_MSG = "invalidmsg";
	public static final String ISORESPONSE_TIMEDOUT = "timedout";
	public static final String ISORESPONSE_NOTSUPPORTED = "notsupported";
	
}
