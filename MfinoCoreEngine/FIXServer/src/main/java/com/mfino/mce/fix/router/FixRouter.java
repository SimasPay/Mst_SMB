package com.mfino.mce.fix.router;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.interceptor.Tracer;

import com.mfino.mce.core.conf.MCELinkConf;
import com.mfino.mce.core.conf.MCELinkType;
import com.mfino.mce.core.util.LinkUtil;
import com.mfino.mce.fix.FIXMessageListenerService;
import com.mfino.mce.fix.conf.FixConf;


public class FixRouter extends RouteBuilder
{
	private FixConf fixConf;
	private FIXMessageListenerService fixMessageListenerService;

	public FixRouter(FixConf fixConf )
	{
		setFixConf(fixConf);
		//this.messageConf = messageConf;
	}
	
	@Override
	public void configure() throws Exception 
	{
		/**
		 * Add the tracer for logging the information passing through these routes
		 */
		getContext().addInterceptStrategy(new Tracer());
//		FIXMessageListenerServiceDefaultImpl fixMessageListener = new FIXMessageListenerServiceDefaultImpl(fixConf.getTimeout());
		fixMessageListenerService.setTimeout(fixConf.getTimeout());
		
		for(MCELinkConf conf:fixConf.getLinkList())
		{
			 Endpoint endPoint = LinkUtil.getEndPoint(conf,getContext(),fixConf.getMinThreads(),fixConf.getMaxThreads());
			 if(conf.getLinkType()==MCELinkType.SERVER)
			 {
				 log.info("Destination "+"bean:"+fixConf.getListenerServiceName()+"?method="+fixConf.getListenerServiceMethodName());
				 //from the endpoints to the listener service to fix queue
				 from(endPoint)
				 //.threads(30)
				 .inOut()
				 //.to(MCEUtil.getToBeanURI(fixConf.getListenerServiceName(),fixConf.getListenerServiceMethodName()))
				 .process(fixMessageListenerService);
				 
				 //  .to(MCEUtil.getToBeanURI(fixConf.getListenerServiceName(),fixConf.getListenerServiceMethodName()));
				   //.to(fixConf.getFixServiceQueueName()+"?replyTo=jms:FixReplyQueue");
			 }
		}
		
		// from fix queue to the the fix service and then destination
		/*from(fixConf.getFixServiceQueueName())
		  .to(MCEUtil.getToBeanURI(fixConf.getFixServiceName(),fixConf.getFixServiceMethodName()))
		  .dynamicRouter(bean(messageConf, "route"));*/
		Processor processor = new Processor()
		{
			@Override
			public void process(Exchange exchange) throws Exception 
			{
				String sync_id = (String)exchange.getIn().getHeader("synchronous_request_id");
				CamelContext camelContext = exchange.getContext();
				ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
				producerTemplate.start();
				producerTemplate.send("seda:"+sync_id,exchange);
				producerTemplate.stop();
			}
			
		};
		
		from("jms:FixReplyQueue?disableReplyTo=true")
		//.threads(1)
		.inOnly()
		.process(processor);
	}
	
	public void setFixConf(FixConf fixConf) {
		this.fixConf = fixConf;
	}

	public FixConf getFixConf() {
		return fixConf;
	}

	public FIXMessageListenerService getFixMessageListenerService() {
		return fixMessageListenerService;
	}

	public void setFixMessageListenerService(FIXMessageListenerService fixMessageListenerService) {
		this.fixMessageListenerService = fixMessageListenerService;
	}
}
