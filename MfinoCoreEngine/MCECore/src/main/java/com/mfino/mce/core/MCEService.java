package com.mfino.mce.core;

import com.mfino.fix.CFIXMsg;
/**
 * The basic concept for the Mfino core engine is that every module is 
 * a service. Services do a particular action and generate a output message 
 * Routing engine would route the messages between the services
 * 
 * @author pochadri
 *
 */

public interface MCEService
{
	/**
	 * Process a message and reply with a message
	 * @param mesg
	 * @returnCFIXMsg
	 */
	public MCEMessage processMessage(MCEMessage mesg);
}
