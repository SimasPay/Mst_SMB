package com.mfino.clickatell.iso8583;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.fix.CmFinoFIX.CMOperatorRequest;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class NoISOResponseProcessor {

	Log	log	= LogFactory.getLog(NoISOResponseProcessor.class);

	public MCEMessage processMessage(MCEMessage msg) {

		log.info("processing in NoISOResponseMessage");

		log.info("MCERequest--> " + msg.getRequest().getClass());
		log.info("MCEResponse-->" + msg.getResponse().getClass());

		if (msg.getRequest() instanceof CMOperatorRequest)
			return msg;

		if(!( msg.getResponse().getClass().equals(CMCommodityTransferToOperator.class) || 
				msg.getResponse().getClass().equals(CMGetMDNBillDebtsToOperator.class)))
			return msg;
		
		CMOperatorRequest isoRequest = (CMOperatorRequest) msg.getResponse();
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