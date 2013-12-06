package com.mfino.nfc.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOffResponseFromBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.nfc.iso8583.processor.NFCISOtoFixProcessor;

public class SignOffResponse implements NFCISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMSignOffResponseFromBank response = new CMSignOffResponseFromBank();
		response.setResponseCode(isoMsg.getString(39));
		return response;
	}
}
