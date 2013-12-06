package com.mfino.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;

/**
 * 
 * @author sasidhar
 */
public class SettlementFixCommunicationHandler extends FIXMessageHandler{
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public CFIXMsg process(CFIXMsg msg) {
		return super.process(msg);
	}

}
