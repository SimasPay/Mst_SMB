package com.mfino.billpayments.bsim;

import com.mfino.mce.core.MCEMessage;

/**
 * 
 * @author Maruthi
 *
 */
public interface bsimProcessor  {	
	
	public  MCEMessage constructRequestMessage(MCEMessage mceMessage);
	
	public  MCEMessage constructReplyMessage(MCEMessage mceMessage);
	
	
}
