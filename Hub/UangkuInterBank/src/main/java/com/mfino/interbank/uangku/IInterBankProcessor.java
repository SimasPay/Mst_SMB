package com.mfino.interbank.uangku;

import com.mfino.mce.core.MCEMessage;

/**
 * 
 * @author HemanthKumar
 *
 */
public interface IInterBankProcessor  {	
	
	public  MCEMessage constructRequestMessage(MCEMessage mceMessage);
	
	public  MCEMessage constructReplyMessage(MCEMessage mceMessage);
	
}
