package com.mfino.fep.processor;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fep.FEPConfiguration;
import com.mfino.fep.FEPConstants;
import com.mfino.fep.messaging.ChannelCommunicationException;
import com.mfino.fep.messaging.QueueChannel;
import com.mfino.fep.validators.ISORequestValidator;

public abstract class ISORequestProcessor {
	
	private static Logger	         log	= LoggerFactory.getLogger(ISORequestProcessor.class);
	 
	protected ISORequestValidator validator;
	protected QueueChannel	         channel;
		
	public void setValidator(ISORequestValidator validator) {
		this.validator = validator;
	}
	
	public abstract void process(ISOMsg msg) throws ChannelCommunicationException, ISOException;
	
	public String getResponse(Serializable request) throws ChannelCommunicationException, ConfigurationException{
		String element39 = FEPConfiguration.getISOResponseCode(FEPConstants.ISORESPONSE_TIMEDOUT);
		log.info("sending request=" + request + " to QueueChannel for processing ");
		String response = channel.requestAndReceive(request);
		log.info("received reponse=" + response + " from QueueChannel");
		if(StringUtils.isNotBlank(response))
			element39=FEPConfiguration.getISOResponseCode(response);
		return element39;
	}

	

}
