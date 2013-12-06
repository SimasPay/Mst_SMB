package com.mfino.zte.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOnFromBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.zte.iso8583.processor.ZTEISOtoFixProcessor;

public class SignOn implements ZTEISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		//FIXME change to Operator
		CMSignOnFromBank fromBank = new CMSignOnFromBank();
		return fromBank;
	}
}
