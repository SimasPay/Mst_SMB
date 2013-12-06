package com.mfino.nfc.iso8583;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMNFCCardLinkReversalToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardLinkToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkReversalToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkToCMS;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class NoISOResponseProcessor {

	Log	log	= LogFactory.getLog(NoISOResponseProcessor.class);

	public MCEMessage processMessage(MCEMessage msg) {

		log.info("processing in NoISOResponseMessage");

		log.info("MCERequest--> " + msg.getRequest().getClass());
		log.info("MCEResponse-->" + msg.getResponse().getClass());

		if(!( msg.getResponse().getClass().equals(CMNFCCardLinkToCMS.class)))
			return msg;
		if(msg.getResponse().getClass().equals(CMNFCCardLinkToCMS.class)) {
			CMNFCCardLinkToCMS isoRequest = (CMNFCCardLinkToCMS) msg.getResponse();
			CMNFCCardLinkReversalToCMS cardLinkReversal = new CMNFCCardLinkReversalToCMS();
			cardLinkReversal.copy(isoRequest);
			cardLinkReversal.header().setSendingTime(DateTimeUtil.getLocalTime());
			cardLinkReversal.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			msg.setRequest(isoRequest);
			msg.setResponse(cardLinkReversal);
			return msg;
		} else if(msg.getResponse().getClass().equals(CMNFCCardUnlinkToCMS.class)) {
			CMNFCCardUnlinkToCMS isoRequest = (CMNFCCardUnlinkToCMS) msg.getResponse();
			CMNFCCardUnlinkReversalToCMS cardUnlinkReversal = new CMNFCCardUnlinkReversalToCMS();
			cardUnlinkReversal.copy(isoRequest);
			cardUnlinkReversal.header().setSendingTime(DateTimeUtil.getLocalTime());
			cardUnlinkReversal.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			msg.setRequest(isoRequest);
			msg.setResponse(cardUnlinkReversal);
			return msg;
		}
		CMBankRequest isoRequest = (CMBankRequest)  msg.getResponse();
		NoISOResponseMsg noResponse = new NoISOResponseMsg();
		noResponse.copy(isoRequest);
		noResponse.header().setSendingTime(DateTimeUtil.getLocalTime());
		noResponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		msg.setRequest(isoRequest);
		msg.setResponse(noResponse);

		return msg;

	}

	public ISOMsg processMessage(ISOMsg msg) {

		log.info("Looks like we received an isoMsg after aggregationtimeout. What to do here");

		throw new TimedoutISOException("ISOMsg with rrn=" + msg.getString(37) + " received after timeout.Sending to dedadLetterQueue");

	}
}