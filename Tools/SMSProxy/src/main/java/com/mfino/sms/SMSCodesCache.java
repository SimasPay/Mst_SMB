/**
 * 
 */
package com.mfino.sms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.SMSCodeDAO;
import com.mfino.domain.SMSCode;


/**
 * @author Deva
 *
 */
public class SMSCodesCache {
	
	private static Logger log = LoggerFactory.getLogger(SMSCodesCache.class);
	private static Map<String, SMSCode> codesMap = new HashMap<String, SMSCode>();
	
	public static void init() {
		log.debug("Loading SMS Cache");
		SMSCodeDAO smsCodeDAO = new SMSCodeDAO();
		List<SMSCode> smsCodes = smsCodeDAO.getAll();
		for (SMSCode smsCode : smsCodes) {
			codesMap.put(smsCode.getSMSCodeText(), smsCode);
		}
		log.debug("Loaded all the SMS code to cache");
	}

	/**
	 * @return the codesMap
	 */
	public static Map<String, SMSCode> getCodesMap() {
		if (codesMap == null || codesMap.size() == 0) {
			SMSCodesCache.init();
		}
		return codesMap;
	}

	/**
	 * @param codesMap the codesMap to set
	 */
	public static void setCodesMap(Map<String, SMSCode> codesMap) {
		SMSCodesCache.codesMap = codesMap;
	}
	
	public static String getServiceName(String smsCode) {
		SMSCode smsCode2 = getCodesMap().get(smsCode);
		if (smsCode2 == null) {
			return null;
		}
		return smsCode2.getServiceName();
	}
	
	public static void main(String args[]){
		// \b
        Pattern email = Pattern.compile("\\b[a-z0-9._%-]+@[a-z0-9.-]+\\.[a-z]{2,4}$\\b");
        String mail ="deva-rajan.g@smart-telecom.co.id";
        Matcher m = email.matcher(mail);
        boolean result = m.find();
        System.out.println(result);
    }
}
