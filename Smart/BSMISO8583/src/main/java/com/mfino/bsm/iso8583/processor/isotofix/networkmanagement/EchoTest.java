package com.mfino.bsm.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.bsm.iso8583.processor.BSMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMEchoTestFromBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;

public class EchoTest implements BSMISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{
		
		CMEchoTestFromBank fromBank = new CMEchoTestFromBank();
		return fromBank;
	}
}
