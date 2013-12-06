package com.mfino.mce.iso.jpos.dispatcher;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;
import com.mfino.mce.iso.jpos.camel.util.Constants;
import com.mfino.mce.iso.jpos.camel.util.CamelISOSource;

/**
 * 
 * @author POCHADRI
 * 
 * Code currently not used
 *
 */
public class DispatcherProcessor implements Processor, Constants
{
	private String destination;
	
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public void process(Exchange exchange) throws Exception 
	{
		  //this block should be very familiar...
        //We get the ISOMsg from the exchange input
        ISOMsg m= exchange.getIn().getBody(ISOMsg.class);

        //We create an ISOSource so our code knows where to send replies!
        CamelISOSource source=new CamelISOSource(exchange.getContext(),destination);
        Context ctx=new Context();
        ctx.put(SOURCE,source);
        ctx.put(REQUEST,m);

        //We set the exchange output body the jPOS Context
        exchange.getOut().setBody(ctx);

	}

}
