package com.mfino.iso8583;

import com.mfino.fix.CFIXMsg;

public interface IFIXtoISOProcessor {
	
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception;

}
