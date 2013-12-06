package com.mfino.iso8583.processor.zenithbank.isotofix.networkmanagement;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMEchoTestFromBank;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;

public class EchoTest implements IZenithBankISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ZenithBankISOMessage isoMsg, CFIXMsg request) throws Exception {
		
		CMEchoTestFromBank fromBank = new CMEchoTestFromBank();
		return fromBank;
	}
}
