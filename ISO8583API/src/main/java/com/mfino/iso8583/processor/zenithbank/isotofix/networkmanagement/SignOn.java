package com.mfino.iso8583.processor.zenithbank.isotofix.networkmanagement;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOnFromBank;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;

public class SignOn implements IZenithBankISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ZenithBankISOMessage isoMsg, CFIXMsg request) throws Exception {

		CMSignOnFromBank fromBank = new CMSignOnFromBank();
		return fromBank;
	}
}
