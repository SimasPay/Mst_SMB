/**
 * 
 */
package com.mfino.bsim.iso8583.handlers;

import org.hibernate.Session;
import org.jpos.iso.ISOMsg;

/**
 * @author Hemanth
 *
 */
public interface IATMChangePinHandler {

	public Integer handle(ISOMsg msg,Session session) throws Exception;
	
}
