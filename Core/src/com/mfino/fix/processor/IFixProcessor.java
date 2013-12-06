/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;

/**
 *
 * @author xchen
 */
public interface IFixProcessor {

    public CFIXMsg process(CFIXMsg msg) throws Exception;
    
	public static BigDecimal ZERO = new BigDecimal(0);
	public static BigDecimal HUNDREAD = new BigDecimal(100);
	
	/**
	 * @param loggedUserName the loggedUserName to set
	 */
	public void setLoggedUserName(String loggedUserName);

	/**
	 * @return the loggedUserName
	 */
	public String getLoggedUserName();
	
	/**
	 * @return the loggedUserName with IP
	 */
	public String getLoggedUserNameWithIP();
	
	/**
	 * @param ipAddress  to set
	 */
	public void setIpAddress(String ipAddress);

	/**
	 * @return ipAddress
	 */
	public String getIpAddress();
	
}
