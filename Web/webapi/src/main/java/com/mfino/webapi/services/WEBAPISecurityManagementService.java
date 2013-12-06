/**
 * 
 */
package com.mfino.webapi.services;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import com.mfino.exceptions.InvalidMDNException;
import com.mfino.webapi.utilities.IUserDataContainer;
import com.mfino.webapi.utilities.InvalidWeabpiSessionException;

/**
 * @author Shashank
 *
 */
public interface WEBAPISecurityManagementService {

	IUserDataContainer getRequestData(HttpServletRequest request,ServletOutputStream writer, boolean isLoginEnabled) throws InvalidWeabpiSessionException, InvalidMDNException;

	IUserDataContainer getHttpsRequestData(HttpServletRequest request,ServletOutputStream writer, boolean isLoginEnabled) throws InvalidWeabpiSessionException, InvalidMDNException;

}
