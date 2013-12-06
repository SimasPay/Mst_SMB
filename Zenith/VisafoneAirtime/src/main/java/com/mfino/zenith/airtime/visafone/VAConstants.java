package com.mfino.zenith.airtime.visafone;

import org.apache.commons.lang.StringUtils;

import com.mfino.billpayments.BillPayConstants;



public class VAConstants extends BillPayConstants{
	
	public static final String APPID_KEY = "appid";

	public static final String MERCHANTID_KEY = "merchantId";
	
	public static final String OPERATION_SUCCESS  = "1000";
	
	public static final String TRANSACTIONID_EXISTS = "1001";
	public static final String INVALID_CUSTOMER = "1002";
	public static final String ACCESS_EXPIRED = "1003";
	public static final String INSUFFUCIENT_FUNDS = "1004";
	public static final String OTHER_ERROR = "1005";
	public static final String ACCESS_DENIED = "1006";
	public static final String INVALID_AMOUNT = "1007";
	public static final String INTERNAL_ERROR = "1008";
	public static final String TRANSACTION_NOTFOUND = "1009";
	public static final String INVALID_ACCCOUNT_INFO = "1011";
	
	
	public static void main(String... args){
		
		System.out.println(deNormalizeMDN("098765467898765"));
		
	}
	
	public static String deNormalizeMDN(String MDN) {

		int start = 0;
		if (StringUtils.isBlank(MDN))
			return StringUtils.EMPTY;

		MDN = MDN.trim();

		while (start < MDN.length()) {
			if ('0' == MDN.charAt(start))
				start++;
			else
				break;
		}

		if (MDN.startsWith("234", start)) {
			start += "234".length();
		}

		return "0"+MDN.substring(start);
	}

	
}
