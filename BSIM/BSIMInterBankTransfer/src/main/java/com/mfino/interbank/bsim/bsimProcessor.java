package com.mfino.interbank.bsim;

import com.mfino.mce.core.MCEMessage;

/**
 * 
 * @author Satya
 *
 */
public interface bsimProcessor  {	
	
	public  MCEMessage constructRequestMessage(MCEMessage mceMessage);
	
	public  MCEMessage constructReplyMessage(MCEMessage mceMessage);
		
}
