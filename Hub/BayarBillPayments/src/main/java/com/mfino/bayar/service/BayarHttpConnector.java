package com.mfino.bayar.service;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Vishal
 *
 */
public class BayarHttpConnector {

	private static final Logger log = LoggerFactory.getLogger(BayarHttpConnector.class);

	private String timeout;
	private Object result;
	
	protected Map<String, String>	constantFieldsMap;

	public void setConstantFieldsMap(Map<String, String> map) {
		this.constantFieldsMap = map;
	}

	public Object sendHttpRequest(String method, Object params) throws MalformedURLException, SocketTimeoutException {
			

			result = null;

			constantFieldsMap.get("partner_id");
			constantFieldsMap.get("api_key");
			constantFieldsMap.get("output");
			
			
			if (result == null)
			{
				log.info("The result is NULL ");
				throw new SocketTimeoutException();
			}
			else
				log.info("The result is: ");

		return result;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

}