package com.mfino.sterling.interbank;

import com.mfino.mce.core.MCEMessage;

/**
 * @author Amar
 *
 */
public interface InterBankTransferService {
	
	public MCEMessage processMessage(MCEMessage mesg);
	
	public IBTBankService getIbtBankService();
}
