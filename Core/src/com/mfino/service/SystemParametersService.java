/**
 * 
 */
package com.mfino.service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Sreenath
 *
 */
public interface SystemParametersService {

	/**
	 * 
	 * @return
	 */
	public boolean getBankServiceStatus();
	/**
	 * Parse the value of the system parameter to integer and return it
	 * @param property
	 * @return
	 */
	public int getInteger(String property);

	/**Parse the value of the system parameter to Long and return it
	 * 
	 * @param property
	 * @return
	 */
	public long getLong(String property);
	
	/**
	 * Parse the value of System Parameter 'default.language' and returns it.
	 * 
	 * @param property
	 * @return
	 */
	public int getSubscribersDefaultLanguage();

	/**
	 * Parse the value of the system parameter to BigDecimal and return it
	 * @param property
	 * @return
	 */
	public BigDecimal getBigDecimal(String property);

	/**
	 * Returns the value of the given property
	 * @param property
	 * @return
	 */
	public String getString(String property);
	
	/**
	 * 
	 * @return
	 */
	public int getPinLength();
	
	/**
	 * 
	 * @return
	 */
	public int getOTPLength();
	
	//use these methods for other than adminapplication
	/**
	 * Fetches the value of the given property from the systemparameters table
	 * @param property
	 * @return
	 */
	public String getUpdatedValue(String property);
	
	/**
	 * 
	 * @return
	 */
	public String generatePIN();
	
	/**
	 * 
	 * @return
	 */
	public void setBookDatedValue(Date bookDatedValue);

	public boolean getIsEmailVerificationNeeded();

}
