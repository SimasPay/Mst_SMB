package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;

import com.mfino.fix.CFIXMsg;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SharePartnersProcessor;

@Service("SharePartnersProcessorImpl")
public class SharePartnersProcessorImpl extends BaseFixProcessor implements SharePartnersProcessor{
	@Override
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		return null;
	}
}
