package com.mfino.billpay.startimes;

import com.mfino.mce.core.MCEMessage;

public interface StarTimesProcessor  {	
	
	public  MCEMessage constructRequestMessage(MCEMessage mceMessage);
	
	public  MCEMessage constructReplyMessage(MCEMessage mceMessage);
	
	
}
