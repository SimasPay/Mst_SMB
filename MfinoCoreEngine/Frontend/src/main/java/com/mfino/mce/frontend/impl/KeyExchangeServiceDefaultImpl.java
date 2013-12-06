package com.mfino.mce.frontend.impl;

import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPinKeyExchangeResponseFromBank;
import com.mfino.fix.CmFinoFIX.CMPinKeyExchangeToBank;
import com.mfino.fix.CmFinoFIX.CMSignOnFromBank;
import com.mfino.fix.CmFinoFIX.CMSignOnResponseFromBank;
import com.mfino.fix.CmFinoFIX.CMSignOnToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.frontend.KeyExchangeService;


public class KeyExchangeServiceDefaultImpl implements KeyExchangeService 
{
	Log log = LogFactory.getLog(KeyExchangeServiceDefaultImpl.class);
	
	String messageRoute;
	String keyExchangeRoute;
	public String getKeyExchangeRoute() {
		return keyExchangeRoute;
	}

	public void setKeyExchangeRoute(String keyExchangeRoute) {
		this.keyExchangeRoute = keyExchangeRoute;
	}

	public String getMessageRoute() 
	{
		return messageRoute;
	}
	
	public void setMessageRoute(String routeToStop) 
	{
		this.messageRoute = routeToStop;
	}
	
	@Override
	public void process(Exchange exchange) throws Exception 
	{
		// its 24 hours or time for exchange of keys
		MCEMessage mceMessage = exchange.getIn().getBody(MCEMessage.class);
		
		// this is the login service 
		log.info("LoginService: got the object" +mceMessage);
		CFIXMsg requestFixMsg=mceMessage!=null?mceMessage.getResponse():null;
		CFIXMsg responseFixMesg=null;
		if(requestFixMsg==null)
		{
			//this is the 24 hrs timer that triggerred the request for key exchange
			responseFixMesg = new CmFinoFIX.CMPinKeyExchangeToBank();
			//suspend the route 
			exchange.getContext().suspendRoute(messageRoute);
			exchange.getContext().resumeRoute(keyExchangeRoute);
			
		}
		else if (requestFixMsg instanceof CMSignOnFromBank)
		{
			//we get the response back from ISO server for login request
			//check if the request was successful, if yes then send the PIN request
			// if the timeout happened then we need to send a new request
			responseFixMesg = (CMSignOnFromBank) requestFixMsg;
			//disable the route
			//TODO: check if the login was successful
			log.info("Trying to do key exchange");
			responseFixMesg = new CmFinoFIX.CMPinKeyExchangeToBank();
			((CMPinKeyExchangeToBank)responseFixMesg).setTransactionID(Long.parseLong(Long.toString(UUID.randomUUID().getMostSignificantBits()).substring(0, 6)));
		}
		
		else if(requestFixMsg instanceof CMSignOnToBank)
		{
			// we sent the sign on request but didnt get any response from the bank
			// resend the sign on request
			((CMSignOnToBank) requestFixMsg).setTransactionID(Long.parseLong(Long.toString(UUID.randomUUID().getMostSignificantBits()).substring(0, 6)));
			responseFixMesg = (CMSignOnToBank)requestFixMsg;
			
		}
		
		else if(requestFixMsg instanceof CMPinKeyExchangeResponseFromBank)
		{
			String key = ((CMPinKeyExchangeResponseFromBank)requestFixMsg).getKeyCheckValue();
			//TODO: store the PIN key for futher use
			
			//start the paused route again
			Route r = exchange.getContext().getRoute(messageRoute);
			exchange.getContext().startRoute(messageRoute);
			
			//need to send a message out to stop the processing of this route
			exchange.getContext().getInflightRepository().remove(exchange);
			exchange.getContext().suspendRoute(keyExchangeRoute);
			return;
		}
		else if(requestFixMsg instanceof CMPinKeyExchangeToBank)
		{
			// we did not get the response from the bank
			// lets resend it again
			responseFixMesg=(CMPinKeyExchangeToBank)requestFixMsg;
		}
		mceMessage.setResponse(responseFixMesg);
		exchange.getOut().setBody(mceMessage);
		exchange.getIn().setBody(mceMessage);
	}
}
