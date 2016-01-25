package com.mfino.bsm.ppob.iso8583;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class ReversalFailureResponseConstructor {

	Log	log	= LogFactory.getLog(ReversalFailureResponseConstructor.class);

	public CFIXMsg construct(CMBankRequest request) {

		if (request instanceof CMMoneyTransferReversalToBank) {
			log.info("no Response for reversal request also, " + "sending the message to backend for putting the transaction in pending");
			CMMoneyTransferReversalToBank reversalFixMsg = (CMMoneyTransferReversalToBank) request;
			CMMoneyTransferReversalFromBank reversalFromBank = new CMMoneyTransferReversalFromBank();
			// set the status to fail for Backend to take approriate action
			reversalFromBank.copy(reversalFixMsg);
			reversalFromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFromBank.setResponseCode(CmFinoFIX.ResponseCode_Failure.toString());
			return reversalFromBank;
		}
		/*else if (request instanceof CMDSTVMoneyTransferReversalToBank) {
			log.info("no Response for reversal request also, " + "sending the message to backend for putting the transaction in pending");
			CMDSTVMoneyTransferReversalToBank reversalFixMsg = (CMDSTVMoneyTransferReversalToBank) request;
			CMDSTVMoneyTransferReversalFromBank reversalFromBank = new CMDSTVMoneyTransferReversalFromBank();
			// set the status to fail for Backend to take approriate action
			reversalFromBank.copy(reversalFixMsg);
			reversalFromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFromBank.setResponseCode(CmFinoFIX.ResponseCode_Failure.toString());
			return reversalFromBank;
		}*/

		return null;

	}

}
