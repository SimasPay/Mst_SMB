package com.mfino.interbank.bsim;

import com.mfino.mce.core.MCEMessage;

public interface InterBankTransferService {
	
	public MCEMessage ibtInquirySourceToDestination(MCEMessage mceMessage);
	
	public MCEMessage ibtInquiryCompletionSourceToDestination(MCEMessage mceMessage);
	
	public MCEMessage ibtSourceToDestination(MCEMessage mceMessage);
	
	public MCEMessage ibtCompletionSourceToDestination(MCEMessage mceMessage);
	
}
