package com.mfino.mce.frontend;

import com.mfino.mce.core.MCEMessage;

public interface MissingResponseHandlerService 
{
	MCEMessage processMessage(MCEMessage mesg);
}
