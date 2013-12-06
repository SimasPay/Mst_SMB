package com.mfino.iso8583.processor.zenithbank;

import com.mfino.fix.CFIXMsg;

public interface IZenithBankISOtoFixProcessor {

	public CFIXMsg process(ZenithBankISOMessage isoMsg,CFIXMsg request)throws Exception;
}
