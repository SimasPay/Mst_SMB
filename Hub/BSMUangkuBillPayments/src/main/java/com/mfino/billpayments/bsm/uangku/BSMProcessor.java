package com.mfino.billpayments.bsm.uangku;

import com.mfino.mce.core.MCEMessage;

/**
 * 
 * @author Vishal
 *
 */
public interface BSMProcessor  {
	
	public  MCEMessage constructRequestMessage(MCEMessage mceMessage);
	
	public  MCEMessage constructReplyMessage(MCEMessage mceMessage);
	
	
}
