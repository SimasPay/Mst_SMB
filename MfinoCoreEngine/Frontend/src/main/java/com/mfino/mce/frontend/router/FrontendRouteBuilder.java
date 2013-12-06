package com.mfino.mce.frontend.router;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMSignOnToBank;
import com.mfino.mce.core.MCEMessage;

public class FrontendRouteBuilder extends RouteBuilder 
{

	Log log = LogFactory.getLog(FrontendRouteBuilder.class);
	
	private String accno;

	private String accType;
	
	public FrontendRouteBuilder(String accno,String accType)
	{
		this.accno= accno;
		this.accType = accType;
	}
	@Override
	public void configure() throws Exception 
	{
	
	}

}
