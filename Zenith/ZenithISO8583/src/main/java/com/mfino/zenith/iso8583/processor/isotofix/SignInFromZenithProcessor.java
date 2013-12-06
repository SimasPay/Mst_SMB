package com.mfino.zenith.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.zenith.iso8583.processor.IZenithBankISOtoFixProcessor;

public class SignInFromZenithProcessor implements IZenithBankISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		//response.setResponseCode(isoMsg.getResponseCode());
		
		return request;
	}
}
