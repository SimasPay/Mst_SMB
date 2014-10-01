package com.mfino.flashiz.iso8583;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementFromBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementFromBankForBsim;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBank;
import com.mfino.fix.CmFinoFIX.CMPaymentAcknowledgementToBankForBsim;
import com.mfino.fix.CmFinoFIX.CMQRPaymentToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class NoISOResponseProcessor {

	Log	log	= LogFactory.getLog(NoISOResponseProcessor.class);
	protected String reversalQueue = "jms:bsimISOQueue";

	public MCEMessage processMessage(MCEMessage msg) {

		log.info("processing in NoISOResponseMessage");

		log.info("MCERequest--> " + msg.getRequest().getClass());//to
		log.info("MCEResponse-->" + msg.getResponse().getClass());//from

		//if (msg.getRequest() instanceof CMBankRequest)
		//	return msg;

		//		if(!( msg.getResponse().getClass().equals(CMMoneyTransferToBank.class) ||  msg.getResponse().getClass().equals(CMQRPaymentToBank.class) 
		//				|| msg.getResponse().getClass().equals(CMPaymentAcknowledgementToBank.class) ||
		//			msg.getResponse().getClass().equals(CMMoneyTransferReversalToBank.class)))
		//			return msg;

		//		CMBankRequest isoRequest = (CMBankRequest) msg.getResponse();
		//
		//		// No response for reversal
		//		if (isoRequest instanceof CMMoneyTransferReversalToBank) {
		//			ReversalFailureResponseConstructor proc = new ReversalFailureResponseConstructor();
		//			msg.setRequest(isoRequest);
		//			msg.setResponse(proc.construct(isoRequest));
		//			return msg;
		//		}
		//
		//		NoISOResponseMsg noResponse = new NoISOResponseMsg();
		//		noResponse.copy(isoRequest);
		//		noResponse.header().setSendingTime(DateTimeUtil.getLocalTime());
		//		noResponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		//		msg.setRequest(isoRequest);
		//		msg.setResponse(noResponse);
		//
		//		return msg;


		//no response for payment acknowledge write logic what to do


		if( msg.getResponse().getClass().equals(CMPaymentAcknowledgementFromBankForBsim.class))
		{
			CMPaymentAcknowledgementFromBankForBsim paymentAcknowledgementFromBank  = (CMPaymentAcknowledgementFromBankForBsim) msg.getResponse();
			if( paymentAcknowledgementFromBank.getResponseCode().equals(CmFinoFIX.ISO8583_ResponseCode_Success) )
			{
				return msg;
			}

			//if response code is not success then initiate the reversal.
			else
			{
				ReversalFailureResponseConstructor proc = new ReversalFailureResponseConstructor();
				//msg.setResponse(proc.construct(paymentAcknowledgementFromBank));
				msg.setResponse(proc.construct(msg));
				msg.setRequest(msg.getResponse());
				msg.setDestinationQueue(reversalQueue);
				return msg;
			}
		}
		//if the request is of type PaymentAckToBank and the NMStatus failed we need to trigger reversal as the money is deducted from Bank AC
		else if((msg.getResponse().getClass().equals(CMPaymentAcknowledgementToBankForBsim.class)) && (!(StatusRegistrar.getSignonStatus("flashizmux").equals(NMStatus.Successful)) || !(StatusRegistrar.getEchoStatus("flashizmux").equals(NMStatus.Successful))))
		{
			ReversalFailureResponseConstructor proc = new ReversalFailureResponseConstructor();
			//msg.setResponse(proc.construct(paymentAcknowledgementFromBank));
			msg.setResponse(proc.construct(msg));
			msg.setRequest(msg.getResponse());
			msg.setDestinationQueue(reversalQueue);
			return msg;
		}else if((msg.getResponse().getClass().equals(CMPaymentAcknowledgementToBankForBsim.class)))
		{
			CMPaymentAcknowledgementToBankForBsim isoRequest = (CMPaymentAcknowledgementToBankForBsim) msg.getResponse();
	        NoISOResponseMsg noResponse = new NoISOResponseMsg();
			noResponse.copy(isoRequest);
			noResponse.header().setSendingTime(DateTimeUtil.getLocalTime());
			noResponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			msg.setRequest(isoRequest);
			msg.setResponse(noResponse);
			msg.setDestinationQueue("jms:flashizPendingQueue");
			return msg;
		}
		else{
			return msg;
		}

	}

	public ISOMsg processMessage(ISOMsg msg) {

		log.info("Looks like we received an isoMsg after aggregationtimeout. What to do here");

		throw new TimedoutISOException("ISOMsg with rrn=" + msg.getString(37) + " received after timeout.Sending to dedadLetterQueue");

	}
}