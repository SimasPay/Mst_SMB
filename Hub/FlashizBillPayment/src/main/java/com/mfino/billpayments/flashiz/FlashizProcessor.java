package com.mfino.billpayments.flashiz;

import com.mfino.mce.core.MCEMessage;

public interface FlashizProcessor  {	
	
	public  MCEMessage constructRequestMessage(MCEMessage mceMessage);
	
	public  MCEMessage constructReplyMessage(MCEMessage mceMessage);
	
	
}
