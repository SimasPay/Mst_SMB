package com.mfino.iso8583.processor.bankchannel;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public interface IXLinkReqeustProcessor{
	
	public CFIXMsg process(XLinkISOMessage isoMsg) throws Exception;

}
