package com.mfino.zte.iso8583;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class MissingResponseHandlerServiceDefaultImpl {
	Log	log	= LogFactory.getLog(MissingResponseHandlerServiceDefaultImpl.class);

	public MCEMessage processMessage(MCEMessage mesg) {
		// request that was sent to frontend for outside communication
		CFIXMsg requestFixMsg = mesg.getResponse();
		// also write for reversal also not replied by Zte
		 if (requestFixMsg instanceof CMCommodityTransferToOperator) {
			log.info("no response from Zte, constructing reversal fix message ");
			CMCommodityTransferToOperator requestToOperator = (CMCommodityTransferToOperator) requestFixMsg;
			CMCommodityTransferReversalToOperator reversalFixMsg = new CMCommodityTransferReversalToOperator();
			reversalFixMsg.copy(requestToOperator);

			/**
			 * Set the mandatary parameters for the fix message.
			 */
			reversalFixMsg.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFixMsg.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFixMsg.setISO8583_SystemTraceAuditNumber((requestToOperator.getTransactionID() % 1000000) + "");
			reversalFixMsg.setTransactionID(reversalFixMsg.getTransactionID() + 1);
			reversalFixMsg.setISO8583_RetrievalReferenceNum(reversalFixMsg.getTransactionID() + "");
			mesg.setResponse(reversalFixMsg);
		}
		// no response for reversal also
		// lets send a message to backend asking to put the transaction in
		// pending status
		else if (requestFixMsg instanceof CMCommodityTransferReversalToOperator) {
			log.info("no Response for reversal request also, " + "sending the message to backend for putting the transaction in pending");
			CMCommodityTransferReversalToOperator reversalFixMsg = (CMCommodityTransferReversalToOperator) requestFixMsg;
			CMCommodityTransferReversalFromOperator reversalFromOperator = new CMCommodityTransferReversalFromOperator();
			// set the status to fail for Backend to take approriate action
			reversalFromOperator.copy(reversalFixMsg);
			reversalFromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFromOperator.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFromOperator.setResponseCode(CmFinoFIX.ResponseCode_Failure);
			mesg.setResponse(reversalFromOperator);
		}
		else {
			log.warn("Not sure why this message is here, some bug in routing logic");
		}
		return mesg;

	}
}
