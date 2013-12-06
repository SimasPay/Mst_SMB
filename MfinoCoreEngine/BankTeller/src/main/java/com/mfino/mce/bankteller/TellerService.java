package com.mfino.mce.bankteller;

import com.mfino.mce.core.MCEMessage;

/**
 * 
 * @author Maruthi
 *
 */
public interface TellerService {

	public MCEMessage processMessage(MCEMessage mesg);
}
