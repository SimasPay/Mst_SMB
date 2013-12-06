package com.mfino.iso8583.definitions.fixtoiso;

import java.util.Map;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;

public interface IFixToIsoProcessorFactory {

	public void setConstantFieldsMap(Map<String,String> map);
	
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException ;
	
}
