package com.mfino.iso8583.processor.bankchannel;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.processor.bankchannel.isomessages.Mobile8ISOMessage;

public interface IMobile8RequestProcessor {
	
	public CFIXMsg process(Mobile8ISOMessage isoMsg) throws Exception;

}
