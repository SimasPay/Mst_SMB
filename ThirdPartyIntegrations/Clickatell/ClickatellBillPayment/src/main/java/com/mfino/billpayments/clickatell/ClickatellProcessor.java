package com.mfino.billpayments.clickatell;

import com.mfino.mce.core.MCEMessage;

/**
 * 
 * @author Maruthi
 *
 */
public interface ClickatellProcessor  {	
	
	public  MCEMessage constructRequestMessage(MCEMessage mceMessage);
	
	public  MCEMessage constructReplyMessage(MCEMessage mceMessage);
	
	
}
