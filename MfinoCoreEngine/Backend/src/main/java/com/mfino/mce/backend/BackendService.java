package com.mfino.mce.backend;

import com.mfino.mce.core.MCEMessage;

public interface BackendService 
{
	/**
	 * Process a message and reply with a message
	 * @param mesg
	 * @returnCFIXMsg
	 */
	public MCEMessage processMessage(MCEMessage mesg);
	public void setSessionFactory(org.hibernate.SessionFactory sessionFactory);
}
