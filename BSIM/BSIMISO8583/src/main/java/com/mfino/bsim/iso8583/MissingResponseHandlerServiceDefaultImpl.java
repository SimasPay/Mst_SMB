package com.mfino.bsim.iso8583;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.InterBankTransfersDao;
import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.domain.InterbankTransfers;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class MissingResponseHandlerServiceDefaultImpl {
	Log	log	= LogFactory.getLog(MissingResponseHandlerServiceDefaultImpl.class);

	public MCEMessage processMessage(MCEMessage mesg) {
		// request that was sent to frontend for outside communication
		CFIXMsg requestFixMsg = mesg.getResponse();

		// request is a money transfer to bank then we need to send
		// a reversal request to bank
		/*if (requestFixMsg instanceof CMDSTVMoneyTransferToBank) {
			log.info("no response from bank for DSTV, constructing a reversal fix message");

			CMDSTVMoneyTransferToBank moneyTransferToBank = (CMDSTVMoneyTransferToBank) requestFixMsg;
			CMDSTVMoneyTransferReversalToBank reversalFixMsg = new CMDSTVMoneyTransferReversalToBank();
			reversalFixMsg.copy(moneyTransferToBank);

			*//**
			 * Set the mandatary parameters for the fix message.
			 *//*
			reversalFixMsg.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFixMsg.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFixMsg.setBankSystemTraceAuditNumber((moneyTransferToBank.getTransactionID() % 1000000) + "");
			reversalFixMsg.setTransactionID(reversalFixMsg.getTransactionID() + 1);
			reversalFixMsg.setBankRetrievalReferenceNumber(reversalFixMsg.getTransactionID() + "");
			mesg.setResponse(reversalFixMsg);
		}
		else if (requestFixMsg instanceof CMDSTVMoneyTransferReversalToBank) {
			log.info("no Response for reversal request also, " + "sending the message to backend for putting the transaction in pending");
			CMDSTVMoneyTransferReversalToBank reversalFixMsg = (CMDSTVMoneyTransferReversalToBank) requestFixMsg;
			CMDSTVMoneyTransferReversalFromBank reversalFromBank = new CMDSTVMoneyTransferReversalFromBank();
			// set the status to fail for Backend to take approriate action
			reversalFromBank.copy(reversalFixMsg);
			reversalFromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFromBank.setResponseCode(CmFinoFIX.ResponseCode_Failure.toString());
			mesg.setResponse(reversalFromBank);
		}
		// also write for reversal also not replied by bank
		else*/ if (requestFixMsg instanceof CMMoneyTransferToBank) {
			log.info("no response from bank, constructing reversal fix message ");
			CMMoneyTransferToBank moneyTransferToBank = (CMMoneyTransferToBank) requestFixMsg;
			CMMoneyTransferReversalToBank reversalFixMsg = new CMMoneyTransferReversalToBank();
			reversalFixMsg.copy(moneyTransferToBank);

			/**
			 * Set the mandatary parameters for the fix message.
			 */
			reversalFixMsg.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFixMsg.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFixMsg.setBankSystemTraceAuditNumber((moneyTransferToBank.getTransactionID() % 1000000) + "");
			reversalFixMsg.setTransactionID(reversalFixMsg.getTransactionID() + 1);
			reversalFixMsg.setBankRetrievalReferenceNumber(reversalFixMsg.getTransactionID() + "");
			
			// Setting DestBankCode separately as it is not part of MoneyTransferToBank - Hence would not copy from original message
			InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
			InterBankTransfersQuery query = new InterBankTransfersQuery();
			query.setSctlId(moneyTransferToBank.getServiceChargeTransactionLogID());
			List<InterbankTransfers> ibtList = interBankTransferDao.get(query);
			
			if(ibtList!=null && !ibtList.isEmpty())
			{
				//Only there should be one record for a given sctld
				reversalFixMsg.setDestBankCode(ibtList.get(0).getDestbankcode());
			}
						
			mesg.setResponse(reversalFixMsg);
		}
		// no response for reversal also
		// lets send a message to backend asking to put the transaction in
		// pending status
		else if (requestFixMsg instanceof CMMoneyTransferReversalToBank) {
			log.info("no Response for reversal request also, " + "sending the message to backend for putting the transaction in pending");
			CMMoneyTransferReversalToBank reversalFixMsg = (CMMoneyTransferReversalToBank) requestFixMsg;
			CMMoneyTransferReversalFromBank reversalFromBank = new CMMoneyTransferReversalFromBank();
			// set the status to fail for Backend to take approriate action
			reversalFromBank.copy(reversalFixMsg);
			reversalFromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFromBank.setResponseCode(CmFinoFIX.ResponseCode_Failure.toString());
			mesg.setResponse(reversalFromBank);
		}
		else {
			log.warn("Not sure why this message is here, some bug in routing logic");
		}
		return mesg;

	}
}
