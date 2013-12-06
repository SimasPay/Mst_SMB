package com.mfino.mce.bankteller;

import com.mfino.fix.CFIXMsg;
import com.mfino.mce.core.MCEMessage;

public interface TellerEmoneyTransferService {
	
	public CFIXMsg generateCashInInquiry(TellerBackendResponse response);
	
	public CFIXMsg generateCashInConfirm(TellerBackendResponse response);
	
	public CFIXMsg processMessage(MCEMessage mceMessage);

}
