package com.mfino.mce.fix;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.mfino.mce.core.MCEMessage;

public interface FIXMessageListenerService extends Processor
{
	public MCEMessage processMessage(byte[] in);
	
	public void process(Exchange httpExchange) throws Exception;
	
	public void setTimeout(Integer timeout);
}
