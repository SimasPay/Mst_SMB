/**
 * 
 */
package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SystemParametersDao;
import com.mfino.domain.SystemParameters;
import com.mfino.handlers.hsm.HSMHandler;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.SystemParametersUtil;

/**
 * This a service class for the system parameters
 * @author Sreenath
 *
 */
@Service("SystemParametersServiceImpl")
public class SystemParametersServiceImpl implements SystemParametersService{
	private static Logger log = LoggerFactory.getLogger(SystemParametersServiceImpl.class);	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean getBankServiceStatus(){
		String systemParameter =  getString(SystemParameterKeys.BANK_SERVICE_STATUS);
		if("true".equalsIgnoreCase(systemParameter)){
			return true;
		}
		else{
			return false;	
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean getIsEmailVerificationNeeded(){
		String systemParameter =  getString(SystemParameterKeys.EMAIL_VERIFICATION_NEEDED);
		if("true".equalsIgnoreCase(systemParameter)){
			return true;
		}
		else{
			return false;	
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int getInteger(String property) {
		try {
			return Integer.parseInt(getUpdatedValue(property));
		} catch (NumberFormatException ex) {
			log.error("failed get property :"+property, ex);
			return -1;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public long getLong(String property) {
		try {
			return Long.parseLong(getUpdatedValue(property));
		} catch (NumberFormatException ex) {
			log.error("failed get property :"+property, ex);
			return -1;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int getSubscribersDefaultLanguage() {
		try {
			return Integer.parseInt(getUpdatedValue(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER));
		} catch (NumberFormatException ex) {
			log.error("failed get property :"+SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER, ex);
			// 0 for English
			return 0;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public BigDecimal getBigDecimal(String property) {
		try {
			return new BigDecimal(getUpdatedValue(property));
		} catch (NumberFormatException ex) {
			log.error("failed get property :"+property, ex);
			return new BigDecimal("-1");
		}
		catch(NullPointerException npe){
			log.error("failed get property :"+property, npe);
			return new BigDecimal("-1");
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String getString(String property){
		return getUpdatedValue(property);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int getPinLength(){
		int pinlength=getInteger(SystemParameterKeys.PIN_LENGTH);
		if(pinlength==-1){
			pinlength=getOTPLength();
		}
		return pinlength;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int getOTPLength(){
		int pinlength=getInteger(SystemParameterKeys.OTP_LENGTH);
		if(pinlength==-1){
			pinlength=6;
		}
		return pinlength;
	}
	
	//use these methods for other than adminapplication
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String getUpdatedValue(String property){
		SystemParametersDao systemParameterDao = DAOFactory.getInstance().getSystemParameterDao();
		SystemParameters parameter=systemParameterDao.getSystemParameterByName(property) ;
		if(parameter!=null){
			return parameter.getParameterValue();
		}
		return null;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String generatePIN() {
		int length = getPinLength();
		return MfinoUtil.generateRandomNumber(length);
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void setBookDatedValue(Date value) {
		if(value!=null){
		SystemParametersDao systemParameterDao = DAOFactory.getInstance().getSystemParameterDao();
		SystemParameters parameter=systemParameterDao.getSystemParameterByName(SystemParameterKeys.LAST_BDV_DATE) ;
		parameter.setParameterValue(DateUtil.getFormattedDate(value));
		systemParameterDao.save(parameter);
		}
	}


}
