package com.mfino.mce.iso.jpos.comm;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.mce.iso.jpos.camel.util.ISOUtil;
import com.mfino.mce.iso.jpos.exception.ISOException;

/**
 * 
 * @author POCHADRI
 * 
 * this code is currently not used
 *
 */
public class Sender implements Processor 
{
	Log log = LogFactory.getLog(Receiver.class);
	@Override
	public void process(Exchange exchange)
	{
		int count = 0;
		boolean send = false;
		while(true)
		{
			if(count++>10)
				break;
			try 
			{
				send = ISOUtil.sendMessage(exchange.getIn().getBody(ISOMsg.class));
			} 
			catch (org.jpos.iso.ISOException e1) 
			{
				log.warn("error sending message, will retry",e1);
				send=false;
			}
			if(send)
				break;
			else
			{
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					//sleep is interrupted
					log.warn("exception during waiting for sending messsage ",e);
				}
			}
		}
	}

}
