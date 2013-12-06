package com.mfino.zenith.iso8583;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class NoISOResponseProcessor {

	Log	log	= LogFactory.getLog(NoISOResponseProcessor.class);

	public MCEMessage processMessage(MCEMessage msg) {

		log.info("processing in NoISOResponseMessage");

		log.info("MCERequest--> " + msg.getRequest().getClass());
		log.info("MCEResponse-->" + msg.getResponse().getClass());

		if (msg.getRequest() instanceof CMBankRequest)
			return msg;

		if(!( msg.getResponse().getClass().equals(CMMoneyTransferToBank.class) || 
				msg.getResponse().getClass().equals(CMMoneyTransferReversalToBank.class)))
			return msg;
		
		CMBankRequest isoRequest = (CMBankRequest) msg.getResponse();

		// No response for reversal
		if (isoRequest instanceof CMMoneyTransferReversalToBank) {
			ReversalFailureResponseConstructor proc = new ReversalFailureResponseConstructor();
			msg.setRequest(isoRequest);
			msg.setResponse(proc.construct(isoRequest));
			return msg;
		}

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