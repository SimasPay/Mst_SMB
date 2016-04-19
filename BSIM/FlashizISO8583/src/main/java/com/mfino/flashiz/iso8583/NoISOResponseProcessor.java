package com.mfino.flashiz.iso8583;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CFIXMsg;
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

		log.info("processing in Flashiz NoISOResponseMessage");

		log.info("MCERequest--> " + msg.getRequest().getClass());//to
		log.info("MCEResponse-->" + msg.getResponse().getClass());//from

		if( msg.getResponse().getClass().equals(CMPaymentAcknowledgementFromBankForBsim.class))
		{
			if (msg.getRequest() instanceof CMPaymentAcknowledgementToBankForBsim && 
					msg.getResponse() instanceof CMPaymentAcknowledgementFromBankForBsim) {
				CMPaymentAcknowledgementToBankForBsim isoRequest = (CMPaymentAcknowledgementToBankForBsim) msg.getRequest();
				CMPaymentAcknowledgementFromBankForBsim isoResponse = (CMPaymentAcknowledgementFromBankForBsim) msg.getResponse();
				if ((isoRequest.getCount() == 3) && (isoResponse.getResponseCode().equals("68"))) {
					log.info("As the Advice tried more than 3 times so treating the response as no response from flashiz...");
			        NoISOResponseMsg noResponse = new NoISOResponseMsg();
					noResponse.copy(isoRequest);
					noResponse.header().setSendingTime(DateTimeUtil.getLocalTime());
					noResponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
					msg.setRequest(isoRequest);
					msg.setResponse(noResponse);
					msg.setDestinationQueue("jms:bsimsourceToDestQueue");
					return msg;					
				}
			}			
			return msg;
		}
		//if the request is of type PaymentAckToBank and the NMStatus failed we need to trigger reversal as the money is deducted from Bank AC
		else if((msg.getResponse().getClass().equals(CMPaymentAcknowledgementToBankForBsim.class)) && 
				(!(StatusRegistrar.getSignonStatus("flashizmux").equals(NMStatus.Successful)) || 
				 !(StatusRegistrar.getEchoStatus("flashizmux").equals(NMStatus.Successful))))
		{
			ReversalFailureResponseConstructor proc = new ReversalFailureResponseConstructor();
			CFIXMsg rfresponse = proc.construct(msg);
			//msg.setResponse(proc.construct(paymentAcknowledgementFromBank));
			msg.setRequest(msg.getResponse());
			msg.setResponse(rfresponse);
			msg.setDestinationQueue(reversalQueue);
			return msg;
		}
		else if((msg.getResponse().getClass().equals(CMPaymentAcknowledgementToBankForBsim.class)))
		{
			CMPaymentAcknowledgementToBankForBsim isoRequest = (CMPaymentAcknowledgementToBankForBsim) msg.getResponse();
			if ((isoRequest.getCount() == 3) && (isoRequest.getIsAdvice()!= null && isoRequest.getIsAdvice())){
				log.info("No response from flashiz for Advice call also, sending the NoIsoResponse msg...");
		        NoISOResponseMsg noResponse = new NoISOResponseMsg();
				noResponse.copy(isoRequest);
				noResponse.header().setSendingTime(DateTimeUtil.getLocalTime());
				noResponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
				msg.setRequest(isoRequest);
				msg.setResponse(noResponse);
				msg.setDestinationQueue("jms:bsimsourceToDestQueue");
				return msg;
			}			
			else {
				log.info("Request for the Flashiz advice call as there is no response from flashiz...");
				msg.setDestinationQueue("jms:postBankResponseQueue");
				return msg;				
			}
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