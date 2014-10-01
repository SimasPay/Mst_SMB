package com.mfino.flashiz.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOffFromBank;
import com.mfino.flashiz.iso8583.processor.FlashizISOtoFixProcessor;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;

public class SignOff implements FlashizISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMSignOffFromBank fromBank = new CMSignOffFromBank();
		return fromBank;
	}
}
