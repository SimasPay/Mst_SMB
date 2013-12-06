package com.mfino.bsm.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.bsm.iso8583.processor.BSMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOffFromBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;

public class SignOff implements BSMISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMSignOffFromBank fromBank = new CMSignOffFromBank();
		return fromBank;
	}
}
