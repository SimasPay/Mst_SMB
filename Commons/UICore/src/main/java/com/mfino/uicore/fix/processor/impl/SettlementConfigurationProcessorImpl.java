package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;

import com.mfino.fix.CFIXMsg;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SettlementConfigurationProcessor;

@Service("SettlementConfigurationProcessorImpl")
public class SettlementConfigurationProcessorImpl extends BaseFixProcessor implements SettlementConfigurationProcessor{

	@Override
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
