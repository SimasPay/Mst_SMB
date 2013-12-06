package com.mfino.iso8583.processor.zenithbank.isotofix.networkmanagement;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOffFromBank;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;

public class SignOff implements IZenithBankISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ZenithBankISOMessage isoMsg, CFIXMsg request) throws Exception {
		
		CMSignOffFromBank fromBank = new CMSignOffFromBank();
		return fromBank;
	}
}
