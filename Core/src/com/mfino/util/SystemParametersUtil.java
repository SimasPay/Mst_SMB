/**
 * 
 */
package com.mfino.util;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.service.SystemParametersService;

/**
 * @author Chaitanya
 *
 */
public class SystemParametersUtil {

	private static Logger log = LoggerFactory.getLogger(SystemParametersUtil.class);
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	public int getInteger(String property) {
		try {
			return Integer.parseInt(systemParametersService.getUpdatedValue(property));
		} catch (NumberFormatException ex) {
			log.error("failed get property :"+property, ex);
			return -1;
		}
	}

	public long getLong(String property) {
		try {
			return Long.parseLong(systemParametersService.getUpdatedValue(property));
		} catch (NumberFormatException ex) {
			log.error("failed get property :"+property, ex);
			return -1;
		}
	}

	public  BigDecimal getBigDecimal(String property) {
		try {
			return new BigDecimal(systemParametersService.getUpdatedValue(property));
		} catch (NumberFormatException ex) {
			log.error("failed get property :"+property, ex);
			return new BigDecimal("-1");
		}
		catch(NullPointerException npe){
			log.error("failed get property :"+property, npe);
			return new BigDecimal("-1");
		}
	}

	public  String getString(String property){
		return systemParametersService.getUpdatedValue(property);
	}

	public int getPinLength(){
		int pinlength=getInteger(SystemParameterKeys.PIN_LENGTH);
		if(pinlength==-1){
			pinlength=getOTPLength();
		}
		return pinlength;
	}
	
	public int getOTPLength(){
		int pinlength=getInteger(SystemParameterKeys.OTP_LENGTH);
		if(pinlength==-1){
			pinlength=6;
		}
		return pinlength;
	}
}
