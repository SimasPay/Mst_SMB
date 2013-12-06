package com.mfino.mce.frontend;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;
import com.mfino.mce.core.MCEMessage;


public class FrontendAggregationStragety implements AggregationStrategy
{
	private IIsoToFixProcessorFactory isotofixFactoryInstance;
	
	public void setIsotofixFactoryInstance(IIsoToFixProcessorFactory isotofixFactoryInstance) {
    	this.isotofixFactoryInstance = isotofixFactoryInstance;
    }

	public static String DELAYED_REPLY="delayedReply";
	Log log = LogFactory.getLog(FrontendAggregationStragety.class);
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange)	 
	{
        if (oldExchange == null) 
        {
            // first time through, the oldExchange is null,
            // so just return newExchange as there is nothing to merge
        	log.info("Got the message, waiting for the matching response to come\n");
        	return newExchange;
        }
        
        	/*MCEMessage mceMessage;
        	if(newExchange.getIn().getBody() instanceof MCEMessage)
        	{
        		mceMessage = newExchange.getIn().getBody(MCEMessage.class);
        		//mceMessage.setTimeout(true);
        		newExchange.getOut().setBody(mceMessage);
        		newExchange.getOut().setHeader(DELAYED_REPLY,true);
        	}
        	else
        	{
        		newExchange.getOut().setHeader(DELAYED_REPLY,true);
        	}
            return newExchange;*/
        
        
       /* if(newExchange.getIn().getHeader("MAIN_EXCHANGE")!=null)
        {
        	Exchange temp = oldExchange;
        	oldExchange = newExchange;
        	newExchange=oldExchange;
        }*/
        
        log.info("Got the request and response, would process to send a single response\n");
        ISOMsg isoMesg;
        MCEMessage mceMessage;
//        if(oldExchange.getIn().getBody() instanceof ZenithBankISOMessage)
//        {
//        	isoMesg = oldExchange.getIn().getBody(ISOMsg.class);
//        	mceMessage = newExchange.getIn().getBody(MCEMessage.class);
//        }
//        else
        {
        	isoMesg = newExchange.getIn().getBody(ISOMsg.class);
        	mceMessage = oldExchange.getIn().getBody(MCEMessage.class);
        }
        
        CFIXMsg requestFixMsg = mceMessage.getResponse();
        /**
         * Now i got the fix request that we started with and the response from the bank.
         */
        log.info("Messages "+isoMesg+"--"+requestFixMsg);
        CFIXMsg responseFixMsg = null;
        IIsoToFixProcessor processor = null;
        
		try {
			log.info("Creating the response fix message\n");
			processor = isotofixFactoryInstance.getProcessor(requestFixMsg);
			responseFixMsg = processor.process(isoMesg, requestFixMsg);
			//responseFixMesg = new BalanceInquiry().process(isoMesg, fixMesg);
		} 
		catch(ProcessorNotAvailableException ex){
			log.error("FrontendAggregationStrategy processorNotAvailable ", ex);
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			log.error("error", e);
		}
		MCEMessage mceMessageResponse = new MCEMessage();
		mceMessageResponse.setRequest(mceMessage.getResponse());
		mceMessageResponse.setResponse(responseFixMsg);
		mceMessageResponse.setDestinationQueues(mceMessage.getDestinationQueues());
		
		//mceMessage.setTimeout(false);
		
		//copy the in to out for retaining all other info like headers etc
		/*if(oldExchange.hasOut())
			oldExchange.getOut().copyFrom(oldExchange.getIn());
		else
			oldExchange.setOut(oldExchange.getIn());
		*/
		Map<String, Object> headers = oldExchange.getOut().getHeaders();
		log.info("Headers\n");
		for(String key:headers.keySet())
		{
			log.info("Key--"+key+"--value:"+headers.get(key));
		}
		log.info("\n\n");
		
		//oldExchange.getOut(JmsMessage.class);
		oldExchange.getIn().getHeaders();
		
		newExchange.getIn().setBody(mceMessageResponse);
		
		Map<String,Object> newHeaderMap = new HashMap<String,Object>();
		
		newHeaderMap.putAll(oldExchange.getIn().getHeaders());
		log.info("Header map size in frontendListener :" +newHeaderMap.size());
		newExchange.getIn().setHeaders(newHeaderMap);
		Map<String, Object> headersIn = newExchange.getIn().getHeaders();
		log.info("Headers in frontend In \n");
		for(String key:headersIn.keySet())
		{
			log.info("Key--"+key+"--value:"+headersIn.get(key));
		}
		newExchange.setOut( newExchange.getIn());
        oldExchange.setIn(oldExchange.getOut());
        //newExchange.getOut().setHeader(DELAYED_REPLY,false);
        return newExchange;
    }
	
}
