package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;

import com.mfino.fix.CFIXMsg;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.TaxPricingProcessor;

@Service("TaxPricingProcessorImpl")
public class TaxPricingProcessorImpl extends BaseFixProcessor implements TaxPricingProcessor{

	@Override
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		return null;
	}
}
