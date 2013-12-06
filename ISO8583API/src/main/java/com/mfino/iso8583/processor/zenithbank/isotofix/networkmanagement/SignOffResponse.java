package com.mfino.iso8583.processor.zenithbank.isotofix.networkmanagement;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOffResponseFromBank;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;

public class SignOffResponse implements IZenithBankISOtoFixProcessor {

	@Override
	public CFIXMsg process(ZenithBankISOMessage isoMsg, CFIXMsg request) throws Exception {

		CMSignOffResponseFromBank response = new CMSignOffResponseFromBank();
		response.setResponseCode(isoMsg.getResponseCode());
		return response;
	}
}
