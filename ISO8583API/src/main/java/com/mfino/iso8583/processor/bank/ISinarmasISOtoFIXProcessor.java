package com.mfino.iso8583.processor.bank;

import com.mfino.fix.CFIXMsg;

public interface ISinarmasISOtoFIXProcessor {
	
	public CFIXMsg process(SinarmasISOMessage isoMsg,CFIXMsg request)throws Exception;

}
