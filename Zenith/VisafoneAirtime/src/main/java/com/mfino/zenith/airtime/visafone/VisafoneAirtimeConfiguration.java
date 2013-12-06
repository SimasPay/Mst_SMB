package com.mfino.zenith.airtime.visafone;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sasi
 * Configuration to invoke Visafone Airtime Webservice.
 */
public class VisafoneAirtimeConfiguration {
	
//	public static final String KEY_ACCOUNT_TYPE = "account";
	public static final String KEY_MSISDN = "msisdn";
	public static final String KEY_AMOUNT = "value";
	public static final String KEY_TRANSACTION_ID = "transid";
	public static final String KEY_VENDOR_ID = "vendorid";
	public static final String KEY_PASSWORD = "password";
	
	private String url;
	private Map<String, String> parameters;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	public void setParameter(String key, String value){
		if(parameters == null){
			parameters = new HashMap<String, String>();
		}
		
		parameters.put(key, value);
	}
	
	public String getQueryString(){
		String queryString = "";
		
		queryString = queryString + KEY_MSISDN + "=" + getParameters().get(KEY_MSISDN);
		queryString = queryString + "&" +  KEY_TRANSACTION_ID + "=" + getParameters().get(KEY_TRANSACTION_ID);
		queryString = queryString + "&" +  KEY_VENDOR_ID + "=" + getParameters().get(KEY_VENDOR_ID);
		queryString = queryString + "&" +  KEY_PASSWORD + "=" + getParameters().get(KEY_PASSWORD);
		queryString = queryString + "&" +  KEY_AMOUNT + "=" + getParameters().get(KEY_AMOUNT);
		
		return queryString;
	}
}
