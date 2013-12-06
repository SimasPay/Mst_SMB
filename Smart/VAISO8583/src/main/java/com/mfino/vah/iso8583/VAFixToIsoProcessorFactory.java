package com.mfino.vah.iso8583;

import java.util.Map;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;

public class VAFixToIsoProcessorFactory implements IFixToIsoProcessorFactory {

	@Override
	public void setConstantFieldsMap(Map<String, String> map) {

	}

	@Override
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		return null;
	}

}
