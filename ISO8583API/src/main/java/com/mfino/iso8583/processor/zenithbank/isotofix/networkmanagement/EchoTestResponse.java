package com.mfino.iso8583.processor.zenithbank.isotofix.networkmanagement;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMEchoTestResponseFromBank;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;

public class EchoTestResponse implements IZenithBankISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ZenithBankISOMessage isoMsg, CFIXMsg request) throws Exception {
		
		CMEchoTestResponseFromBank fromBank = new CMEchoTestResponseFromBank();
		fromBank.setResponseCode(isoMsg.getResponseCode());
		return fromBank;
	}
}
