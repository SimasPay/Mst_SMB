package com.mfino.mce.iso.jpos.comm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.apache.commons.logging.Log;

import com.mfino.mce.iso.jpos.camel.util.ISOUtil;

/**
 * 
 * @author POCHADRI
 * 
 * This code is currently not used
 *
 */
public class Receiver 
{
	Log log = LogFactory.getLog(Receiver.class);
	Endpoint sendQueue;
	CamelContext context;

	ProducerTemplate template;
	private ExecutorService pool;

	
	public Receiver(Endpoint sendQueue, CamelContext context, int poolSize)
	{
		this.context=context;
		template  = context.createProducerTemplate();
		this.sendQueue = sendQueue;
		pool = Executors.newFixedThreadPool(poolSize);
	}
	
	public void start()
	{
		
		pool.execute(new Runnable()
		{
			@Override
			public void run() 
			{
				boolean exceptionCaught = false;
				while(true)
				{
					try 
					{
						log.info("receiving messages");
						ISOMsg isoMsg = ISOUtil.receiveMessage();
						log.info("got iso message\n "+isoMsg +" "+sendQueue);
						template.sendBody(sendQueue,isoMsg);
					} 
					catch (ISOException e) 
					{
						log.warn("Exception during reading iso channel",e);
					}
					catch(Throwable t)
					{
						log.warn("Exception listening to iso channel ",t);
						
					}
					if(exceptionCaught)
					{
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							log.warn("Exception during sleep waiting for channel to be available",e);
						}
					}
				}
			}
			});
		
		log.info("Starting to Listen for Messages, Hope channel is live");
	}
	
	public void stop()
	{
		log.info("Stopping Listening to iso channel");
		pool.shutdown();
	}
}
