package com.mfino.zenith.dstv.impl;

import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.ws.WSCommunicator;
import com.mfino.zenith.dstv.DSTVBackendResponse;
import com.mfino.zenith.dstv.DSTVParameters;
import com.mfino.zenith.dstv.DSTVResponseUtil;

/**
 * 
 * @author POCHADRI
 *
 */

public class DSTVWSCommunicatorDefaultImpl extends WSCommunicator 
{
	private DSTVParameters dstvParameters;
	Log log = LogFactory.getLog(this.getClass());
	
	public DSTVParameters getDstvParameters() {
		return dstvParameters;
	}

	public void setDstvParameters(DSTVParameters dstvParameters) {
		this.dstvParameters = dstvParameters;
	}
  
	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) 
	{
		List<Object> params = new ArrayList<Object>();
		
		//request for this task is in response of mcemessage
		// IMP:: order of the parameters is very important
		//if order changes in wsdl it needs to be changed here as well.
		DSTVBackendResponse dstvRequest ;
		dstvRequest = (DSTVBackendResponse)mceMessage.getResponse();
		
		params.add(dstvRequest.getTransactionID()+"");
		
		String dateString = format(new Date(),dstvParameters.getDateFormat());
		params.add(dateString);
		
		params.add(dstvParameters.getMerchantID());
		params.add(dstvParameters.getTerminalID());
		
		BigDecimal amount = dstvRequest.getAmount().multiply(new BigDecimal(100));
		params.add(amount.longValue()+"");
		
		params.add("DSTV Subscription");
		params.add(dstvRequest.getDecoderCode());
		params.add(dstvParameters.getResponseCode());
		params.add(dstvParameters.getUID());
		params.add(dstvParameters.getPWD());
		
		log.debug("DSTVWSCommunicatorDefaultImpl :: merchantId="+dstvParameters.getMerchantID()+", terminalId="+dstvParameters.getTerminalID()+", amount="+amount+", decoderCode="+dstvRequest.getDecoderCode());
		return params;
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> wsResponse,
			MCEMessage requestMceMessage) 
	{
		Object wsResponseElement = wsResponse.get(0);
		log.info("Got response from web service:" +wsResponseElement);
		MCEMessage  mceResponse =  new MCEMessage();
		mceResponse.setRequest(requestMceMessage.getRequest());
		DSTVBackendResponse inResponse = (DSTVBackendResponse)requestMceMessage.getResponse();
		inResponse.setProcessed(true);
		inResponse.setWebServiceResponse(DSTVResponseUtil.getResponseCode(wsResponseElement));
		mceResponse.setResponse(inResponse);
		return mceResponse;
	}

	@Override
	public String getMessageName(MCEMessage mceMessage) 
	{
		return "SavePOS_TXNS";
	}
	
	String format(Date visited,String format) 
	{
	    SimpleDateFormat sdf = new SimpleDateFormat(format); 
	    FieldPosition pos = new FieldPosition(0);
	    StringBuffer empty = new StringBuffer();
	    StringBuffer date = sdf.format(visited, empty, pos);
	    return date.toString();
	  }
	
}
