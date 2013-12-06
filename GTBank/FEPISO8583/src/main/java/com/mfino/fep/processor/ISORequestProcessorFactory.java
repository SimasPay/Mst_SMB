package com.mfino.fep.processor;

import javax.jms.JMSException;

import com.mfino.fep.FEPConstants;
import com.mfino.fep.ProcessorNotAvailableException;


public class ISORequestProcessorFactory {
	
	public static ISORequestProcessor getProcessor(String mti,String processingCode) throws ProcessorNotAvailableException, JMSException{
		
		ISORequestProcessor processor = null;
		if(FEPConstants.REQUEST_MSG_TYPE.equals(mti)){
			processor = new CashOutRequestProcessor();
		}
		else if(FEPConstants.REVERSAL_REQUEST_MSG_TYPE.equals(mti))
			processor = new CashOutRevesalRequestProcessor();
		else
			throw new ProcessorNotAvailableException();
		
		return processor;
		
	}

}
