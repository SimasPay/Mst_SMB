package com.mfino.tools.adjustments;

import org.apache.camel.Processor;

import com.mfino.mce.core.MCEMessage;

public interface InquiryCreator extends Processor {
	
	public MCEMessage processMessage(MCEMessage mce)
			throws Exception; 

}
