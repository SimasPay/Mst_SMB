package com.mfino.flashiz.iso8583;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBankForBsim;
import com.mfino.fix.CmFinoFIX.CMQRPaymentReversalToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class ReversalFailureResponseConstructor {

	Log	log	= LogFactory.getLog(ReversalFailureResponseConstructor.class);

//	public CFIXMsg construct(CMBankRequest request) {
//
//		if (request instanceof CMMoneyTransferReversalToBank) {
//			log.info("no Response for reversal request also, " + "sending the message to backend for putting the transaction in pending");
//			CMMoneyTransferReversalToBank reversalFixMsg = (CMMoneyTransferReversalToBank) request;
//			CMMoneyTransferReversalFromBank reversalFromBank = new CMMoneyTransferReversalFromBank();
//			// set the status to fail for Backend to take approriate action
//			reversalFromBank.copy(reversalFixMsg);
//			reversalFromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
//			reversalFromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
//			reversalFromBank.setResponseCode(CmFinoFIX.ResponseCode_Failure.toString());
//			return reversalFromBank;
//		}
//		/*else if (request instanceof CMDSTVMoneyTransferReversalToBank) {
//			log.info("no Response for reversal request also, " + "sending the message to backend for putting the transaction in pending");
//			CMDSTVMoneyTransferReversalToBank reversalFixMsg = (CMDSTVMoneyTransferReversalToBank) request;
//			CMDSTVMoneyTransferReversalFromBank reversalFromBank = new CMDSTVMoneyTransferReversalFromBank();
//			// set the status to fail for Backend to take approriate action
//			reversalFromBank.copy(reversalFixMsg);
//			reversalFromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
//			reversalFromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
//			reversalFromBank.setResponseCode(CmFinoFIX.ResponseCode_Failure.toString());
//			return reversalFromBank;
//		}*/
//
//		return null;
//
//	}
	
	public CFIXMsg construct(MCEMessage msg) {
		
		log.info("Constructing the Reversal Request..............");

		CMPaymentAcknowledgementToBankForBsim paymentAcknowledgementToBank;

		if (msg.getRequest() instanceof CMPaymentAcknowledgementToBankForBsim) {
			paymentAcknowledgementToBank = (CMPaymentAcknowledgementToBankForBsim) msg.getRequest();			
		}
		// if the paymentAcknowledgement from bank is not recieved, then the CMPaymentAcknowledgementToBank will be part of the response.
		else
		{
			paymentAcknowledgementToBank = (CMPaymentAcknowledgementToBankForBsim) msg.getResponse();
		}

		CMQRPaymentReversalToBank billPayrevtobank = new CMQRPaymentReversalToBank();
		billPayrevtobank.copy(paymentAcknowledgementToBank);
		billPayrevtobank.header().setSendingTime(DateTimeUtil.getLocalTime());
		billPayrevtobank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		billPayrevtobank.setBankSystemTraceAuditNumber(billPayrevtobank.getTransactionID() % 1000000 + "");
		billPayrevtobank.setTransactionID(billPayrevtobank.getTransactionID() + 1);
		billPayrevtobank.setBankRetrievalReferenceNumber(billPayrevtobank.getTransactionID() + "");
		billPayrevtobank.setBillerCode(paymentAcknowledgementToBank.getBillerCode());
		return billPayrevtobank;
	}

}
