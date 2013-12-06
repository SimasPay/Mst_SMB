package com.mfino.zenith.billpay.mfinows;

import com.mfino.billpayments.BillPayConstants;
/**
 * @author Satya
 * 
 */
public class MfinoWSConstants extends BillPayConstants{
	public static final String APPID_KEY = "appid";
	public static final String MERCHANTID_KEY = "merchant_code";
	public static final String OPERATION_SUCCESS  = "00";
	public static final String OPERATION_SUCCESS_DESCRIPTION  = "Transaction successful";
	public static final String OPERATION_FAILURE  = "9999";
	public static final String OPERATION_FAILURE_DESCRIPTION  = "Webservice communication failed";
}
