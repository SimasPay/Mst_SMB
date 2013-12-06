package com.mfino.mce.frontend;

import org.jpos.iso.ISOMsg;

public interface FrontendListenerService 
{
	public ISOMsg processMessage( ISOMsg isoMsg);
}
