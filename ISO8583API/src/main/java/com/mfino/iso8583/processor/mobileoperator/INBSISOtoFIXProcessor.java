package com.mfino.iso8583.processor.mobileoperator;

import com.mfino.fix.CFIXMsg;

public interface INBSISOtoFIXProcessor {
	public CFIXMsg process(MobileOperatorISOMessage isoMsg,CFIXMsg request)throws Exception;
}
