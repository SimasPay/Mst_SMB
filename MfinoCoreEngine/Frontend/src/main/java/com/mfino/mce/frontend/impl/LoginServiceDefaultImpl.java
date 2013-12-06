package com.mfino.mce.frontend.impl;

import java.util.UUID;

import org.apache.camel.Exchange;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.frontend.LoginService;

public class LoginServiceDefaultImpl implements LoginService
{
	@Override
	public void process(Exchange exchange) throws Exception 
	{
		//lets send a Login Message
		/*CFIXMsg fixMesg=null;
		Object obj = exchange.getIn().getBody();
		boolean delayed = (Boolean)exchange.getIn().getHeader(FrontendAggregationStragety.DELAYED_REPLY);
		if(obj==null||!(obj instanceof MCEMessage)  )
		{
			fixMesg = getLoginFixMessage();
		}
		else
		{
			
			
		}
		
		MCEMessage mesg = new MCEMessage();
		mesg.setResponse(signOnFixMesg);
		exchange.getIn().setBody(mesg);
		exchange.getOut().setBody(mesg);*/
	}
	
	private CFIXMsg getLoginFixMessage()
	{
		CmFinoFIX.CMSignOnToBank signOnFixMesg = new CmFinoFIX.CMSignOnToBank();
		long l =123456;
		signOnFixMesg.setTransactionID(l);
		return signOnFixMesg;
	}
	
}
