package com.mfino.zenith.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOffResponseFromBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.zenith.iso8583.processor.IZenithBankISOtoFixProcessor;

public class SignOffResponse implements IZenithBankISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMSignOffResponseFromBank response = new CMSignOffResponseFromBank();
		response.setResponseCode(isoMsg.getString(39));
		return response;
	}
}
