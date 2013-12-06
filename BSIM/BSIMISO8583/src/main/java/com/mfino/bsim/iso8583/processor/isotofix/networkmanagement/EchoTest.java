package com.mfino.bsim.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMEchoTestFromBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;

public class EchoTest implements BSIMISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{
		
		CMEchoTestFromBank fromBank = new CMEchoTestFromBank();
		return fromBank;
	}
}
