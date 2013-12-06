package com.mfino.vah.iso8583;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;

public class VAIsoToFixProcessorFactory implements IIsoToFixProcessorFactory {

	@Override
	public IIsoToFixProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		return null;
	}

}
