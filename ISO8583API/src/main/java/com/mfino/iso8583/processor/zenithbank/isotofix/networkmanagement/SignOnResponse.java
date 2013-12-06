package com.mfino.iso8583.processor.zenithbank.isotofix.networkmanagement;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOnResponseFromBank;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;

public class SignOnResponse implements IZenithBankISOtoFixProcessor {

	@Override
	public CFIXMsg process(ZenithBankISOMessage isoMsg, CFIXMsg request) throws Exception {

		CMSignOnResponseFromBank response = new CMSignOnResponseFromBank();
		response.setResponseCode(isoMsg.getResponseCode());
		return response;
	}
}
