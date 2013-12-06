/**
 * 
 */
package com.mfino.webapi.services;

import javax.servlet.ServletOutputStream;

import com.mfino.result.XMLResult;

/**
 * @author Sreenath
 *
 */
public interface WebAPIUtilsService {
	
	/**
	 * 
	 * @param notificationCode
	 * @param writer
	 * @param SourceMDN
	 * @param MissingParam
	 */
	public void sendError(Integer notificationCode, ServletOutputStream writer, String SourceMDN, String MissingParam);
	/**
	 * 
	 * @param xmlResult
	 * @param writer
	 */
	public void sendIntegrationValidationError(XMLResult xmlResult, ServletOutputStream writer);
	/**
	 * 
	 * @param writer
	 * @param SourceMDN
	 */
	public void sendSessionTimeoutError(ServletOutputStream writer, String SourceMDN);


}
