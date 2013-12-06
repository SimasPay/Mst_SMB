package com.mfino.iso8583.processor.bankchannel;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.processor.bankchannel.isomessages.UMGVHISOMessage;

public interface IVirtualHostRequestProcessor {

	public CFIXMsg process(UMGVHISOMessage isoMsg) throws Exception;
}
