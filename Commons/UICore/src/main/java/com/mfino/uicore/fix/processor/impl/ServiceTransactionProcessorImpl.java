package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;

import com.mfino.fix.CFIXMsg;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ServiceTransactionProcessor;

@Service("ServiceTransactionProcessorImpl")
public class ServiceTransactionProcessorImpl extends BaseFixProcessor implements ServiceTransactionProcessor{
	@Override
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		return null;
	}
}
