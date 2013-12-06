/**
 * 
 */
package com.mfino.bsim.iso8583.handlers;

import org.jpos.iso.ISOMsg;

/**
 * @author Hemanth
 *
 */
public interface IATMChangePinHandler {

	public Integer handle(ISOMsg msg) throws Exception;
	
}
