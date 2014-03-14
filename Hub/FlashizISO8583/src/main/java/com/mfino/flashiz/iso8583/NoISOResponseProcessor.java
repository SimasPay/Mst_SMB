package com.mfino.flashiz.iso8583;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMOperatorRequest;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class NoISOResponseProcessor {

	Log	log	= LogFactory.getLog(NoISOResponseProcessor.class);

	public MCEMessage processMessage(MCEMessage msg) {

		log.info("processing in NoISOResponseMessage");

		log.info("MCERequest--> " + msg.getRequest().getClass());//to
		log.info("MCEResponse-->" + msg.getResponse().getClass());//from
		
		if (msg.getRequest() instanceof CMPaymentAcknowledgementToBank || msg.getRequest() instanceof CMGetUserAPIKeyToBank || msg.getResponse() instanceof CMGetUserAPIKeyToBank)
			return msg;
		CMPaymentAcknowledgementToBank isoRequest = (CMPaymentAcknowledgementToBank) msg.getResponse();
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