package com.mfino.monitor.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

/**
 * Loads external code,description values into codeMap from properties file
 * "externalcodedescriptions.xml". Bean for the class is defined in
 * mcecore_context.xml
 * 
 * @author Srikanth
 * 
 */
public class ExternalResponseCodeHolder {

	static Log log = LogFactory.getLog(ExternalResponseCodeHolder.class);

	private static HashMap<String, ExternalResponsecode> codeMap = new HashMap<String, ExternalResponsecode>();
	private static ExternalResponseCodeHolder externalResponseCodeHolder = null;

	private ExternalResponseCodeHolder() {}
	
	public static ExternalResponseCodeHolder getInstance() {
		
		if(externalResponseCodeHolder == null) {
			
			externalResponseCodeHolder = new ExternalResponseCodeHolder();
			externalResponseCodeHolder.loadExternalCodeMappings("");
		}
		
		return externalResponseCodeHolder;
	}
	
	private void loadExternalCodeMappings(String filePath) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    URL url = loader.getResource("RCCodes.json");
	    JSONObject RCCodes_JSON_OBJECT = null;
	    
		try {
			
			RCCodes_JSON_OBJECT = FileReaderUtil.readFileContAsJsonObj(url.getFile());
			
			if(null != RCCodes_JSON_OBJECT) {
				
				for (Iterator<String> iterator = RCCodes_JSON_OBJECT.keys(); iterator.hasNext();) {
					
					String key = (String) iterator.next();
					String value = (String)RCCodes_JSON_OBJECT.get(key);
					
					ExternalResponsecode responseCode = new ExternalResponsecode();
					responseCode.setCode(key);
					responseCode.setDescription(value);
					
					codeMap.put(key,responseCode);
				}
				
			}			
		} catch (Exception e) {
			e.fillInStackTrace();
		}
	}

	public String getNotificationText(String key) {
		ExternalResponsecode externalResponsecode = (ExternalResponsecode) codeMap.get(key);
		if (externalResponsecode == null) {
			return "No Description Available";
		} else {
			
			return externalResponsecode.getNotificationText();
		}
	}
	
	public String getDescription(String key) {
		ExternalResponsecode externalResponsecode = (ExternalResponsecode) codeMap.get(key);
		if (externalResponsecode == null) {
			return "No Description Available";
		} else {
			
			return externalResponsecode.getDescription();
		}
	}

}
