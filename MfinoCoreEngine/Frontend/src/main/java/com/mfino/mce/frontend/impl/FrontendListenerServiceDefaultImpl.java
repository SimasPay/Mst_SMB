package com.mfino.mce.frontend.impl;

import java.io.IOException;

import javax.xml.soap.MessageFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.mce.frontend.FrontendListenerService;

public class FrontendListenerServiceDefaultImpl implements FrontendListenerService {
	MessageFactory	messageFactory;

	public FrontendListenerServiceDefaultImpl() throws IOException {
	}

	Log	log	= LogFactory.getLog(FrontendListenerServiceDefaultImpl.class);

	@Override
	public ISOMsg processMessage(ISOMsg msg) {
		
		return msg;
		
	}

}
