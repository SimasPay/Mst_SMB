package com.mfino.zenith.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOnResponseFromBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.zenith.iso8583.processor.IZenithBankISOtoFixProcessor;

public class SignOnResponse implements IZenithBankISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMSignOnResponseFromBank response = new CMSignOnResponseFromBank();
		response.setResponseCode(isoMsg.getString(39));
		return response;
	}
}
