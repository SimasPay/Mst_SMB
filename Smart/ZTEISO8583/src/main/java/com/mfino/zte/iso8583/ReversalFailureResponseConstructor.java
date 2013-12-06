package com.mfino.zte.iso8583;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalFromOperator;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.fix.CmFinoFIX.CMOperatorRequest;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class ReversalFailureResponseConstructor {

	Log	log	= LogFactory.getLog(ReversalFailureResponseConstructor.class);
	
	public CFIXMsg construct(CMOperatorRequest isoRequest) {

		if (isoRequest instanceof CMCommodityTransferReversalToOperator) {
			log.info("no Response for reversal request also, " + "sending the message to backend for putting the transaction in pending");
			CMCommodityTransferReversalToOperator reversalFixMsg = (CMCommodityTransferReversalToOperator) isoRequest;
			CMCommodityTransferReversalFromOperator reversalFromBank = new CMCommodityTransferReversalFromOperator();
			// set the status to fail for Backend to take approriate action
			reversalFromBank.copy(reversalFixMsg);
			reversalFromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFromBank.setResponseCode(CmFinoFIX.ResponseCode_Failure);
			return reversalFromBank;
		}
		return null;

	}

}
