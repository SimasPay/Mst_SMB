package com.mfino.offlinefrontend;

import com.mfino.mce.core.MCEMessage;

public interface OfflineFrontendService 
{
	/**
	 * Process the in coming message 
	 * @param mesg
	 * @return
	 */
	public MCEMessage processMessage(MCEMessage msg);
}
