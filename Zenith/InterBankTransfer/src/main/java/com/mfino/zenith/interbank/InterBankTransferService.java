package com.mfino.zenith.interbank;

import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public interface InterBankTransferService {
	
	public MCEMessage processMessage(MCEMessage mesg);
	
	public IBTBankService getIbtBankService();
}
