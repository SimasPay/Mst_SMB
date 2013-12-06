package com.mfino.zenith.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMEchoTestResponseFromBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.zenith.iso8583.processor.IZenithBankISOtoFixProcessor;

public class EchoTestResponse implements IZenithBankISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMEchoTestResponseFromBank fromBank = new CMEchoTestResponseFromBank();
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		return fromBank;
	}
}
