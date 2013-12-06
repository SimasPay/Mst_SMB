package com.mfino.iso8583.definitions.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;

public interface IIsoToFixProcessorFactory {

	public IIsoToFixProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException;
	
}
