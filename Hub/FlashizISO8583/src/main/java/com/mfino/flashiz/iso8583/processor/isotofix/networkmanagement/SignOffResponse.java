package com.mfino.flashiz.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOffResponseFromBank;
import com.mfino.flashiz.iso8583.processor.FlashizISOtoFixProcessor;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;

public class SignOffResponse implements FlashizISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMSignOffResponseFromBank response = new CMSignOffResponseFromBank();
		response.setResponseCode(isoMsg.getString(39));
		return response;
	}
}
