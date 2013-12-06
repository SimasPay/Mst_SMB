package com.mfino.iso8583.processor.bankchannel;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.processor.bankchannel.isomessages.ArtajasaISOMessage;

public interface IArtajasaRequestProcessor {
	
	public CFIXMsg process(ArtajasaISOMessage isoMsg) throws Exception;

}
