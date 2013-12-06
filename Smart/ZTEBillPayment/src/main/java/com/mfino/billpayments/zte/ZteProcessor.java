package com.mfino.billpayments.zte;

import com.mfino.mce.core.MCEMessage;

/**
 * 
 * @author Maruthi
 *
 */
public interface ZteProcessor  {	
	
	public  MCEMessage constructRequestMessage(MCEMessage mceMessage);
	
	public  MCEMessage constructReplyMessage(MCEMessage mceMessage);
	
	
}
