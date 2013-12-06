package com.mfino.mce.backend;

import com.mfino.fix.CFIXMsg;
import com.mfino.mce.core.MCEMessage;

public interface SubscriberService {

	public CFIXMsg processMessage(MCEMessage mesg);
}
