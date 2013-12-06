package com.mfino.bsim.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOnResponseFromBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;

public class SignOnResponse implements BSIMISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMSignOnResponseFromBank response = new CMSignOnResponseFromBank();
		response.setResponseCode(isoMsg.getString(39));
		return response;
	}
}
