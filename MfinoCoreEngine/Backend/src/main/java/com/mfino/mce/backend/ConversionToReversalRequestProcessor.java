package com.mfino.mce.backend;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.InterBankTransfersDao;
import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.domain.InterbankTransfer;
import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class ConversionToReversalRequestProcessor {
	Log	log	= LogFactory.getLog(ConversionToReversalRequestProcessor.class);

	public CFIXMsg processMessage(CFIXMsg requestFixMsg, NoISOResponseMsg responseFix ) {
		// request that was sent to frontend for outside communication
//		 = mesg.getResponse();

		// request is a money transfer to bank then we need to send
		// a reversal request to bank
		if (requestFixMsg instanceof CMDSTVMoneyTransferToBank) {
			log.info("no response from bank for DSTV, constructing a reversal fix message");

			String str = "";

			CMDSTVMoneyTransferToBank moneyTransferToBank = (CMDSTVMoneyTransferToBank) requestFixMsg;
			CMDSTVMoneyTransferReversalToBank reversalFixMsg = new CMDSTVMoneyTransferReversalToBank();
			reversalFixMsg.copy(moneyTransferToBank);

			/**
			 * Set the mandatary parameters for the fix message.
			 */
			reversalFixMsg.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFixMsg.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFixMsg.setBankSystemTraceAuditNumber((moneyTransferToBank.getTransactionID() % 1000000) + "");
			reversalFixMsg.setTransactionID(reversalFixMsg.getTransactionID() + 1);
			reversalFixMsg.setBankRetrievalReferenceNumber(reversalFixMsg.getTransactionID() + "");

			return reversalFixMsg;
			
		}
		// also write for reversal also not replied by bank
		else if (requestFixMsg instanceof CMMoneyTransferToBank) {
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
			// get the transaction id from the NOISOResponse
			reversalFixMsg.setTransactionID(responseFix.getTransactionID());
			reversalFixMsg.setBankRetrievalReferenceNumber(reversalFixMsg.getTransactionID() + "");
			
			// Setting DestBankCode separately as it is not part of MoneyTransferToBank - Hence would not copy from original message
			InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
			InterBankTransfersQuery query = new InterBankTransfersQuery();
			query.setSctlId(moneyTransferToBank.getServiceChargeTransactionLogID());
			List<InterbankTransfer> ibtList = interBankTransferDao.get(query);
			
			if(ibtList!=null && !ibtList.isEmpty())
			{
				//Only there should be one record for a given sctld
				reversalFixMsg.setDestBankCode(ibtList.get(0).getDestBankCode());
			}
			
			return reversalFixMsg;
		}
		else {
			log.warn("Not sure why this message is here, some bug in routing logic");
		}
		return null;
	}
}
