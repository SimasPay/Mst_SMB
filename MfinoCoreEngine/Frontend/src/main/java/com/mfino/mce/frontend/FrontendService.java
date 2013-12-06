package com.mfino.mce.frontend;

import org.jpos.iso.ISOMsg;

import com.mfino.mce.core.MCEMessage;

public interface FrontendService  
{
	public ISOMsg processMessage(MCEMessage mesg);
}
