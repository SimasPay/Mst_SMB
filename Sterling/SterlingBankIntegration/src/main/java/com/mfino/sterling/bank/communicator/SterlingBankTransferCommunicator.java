package com.mfino.sterling.bank.communicator;

import java.util.List;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.sterling.bank.util.SterlingBankWebServiceReponseParser;
import com.mfino.sterling.bank.util.SterlingBankWebServiceRequest;
import com.mfino.sterling.bank.util.SterlingBankWebServiceResponse;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

/**
 * @author Amar
 *
 */
public class SterlingBankTransferCommunicator extends SterlingBankWebServiceCommunicator {

	@Override
	public SterlingBankWebServiceRequest createSterlingBankWebServiceRequest(MCEMessage mceMessage) {
		log.info("SterlingBankTransferCommunicator :: createSterlingBankWebServiceRequest mceMessage="+mceMessage);
		
		SterlingBankWebServiceRequest sterlingBankWebServiceRequest = new SterlingBankWebServiceRequest();
			
		CMMoneyTransferToBank requestFix = (CMMoneyTransferToBank) mceMessage.getResponse();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
					
		sterlingBankWebServiceRequest.setFromAccount(requestFix.getSourceCardPAN());
		sterlingBankWebServiceRequest.setToAccount(requestFix.getDestCardPAN());
		sterlingBankWebServiceRequest.setAmount(requestFix.getAmount().toPlainString());
		sterlingBankWebServiceRequest.setReferenceID(normalize(sctlId));	
		return sterlingBankWebServiceRequest;
	}

	@Override
	public CFIXMsg constructReplyMessage(List<Object> response, CFIXMsg requestFixMessage) {
		Object wsResponseElement = response.get(0);
		
		log.info("SterlingBankTransferCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
				
		CMMoneyTransferFromBank fromBank;
		/*
		 * If requestFixMessage is of type CMBankTellerMoneyTransferToBank, then the response should be of type CMBankTellerMoneyTransferFromBank
		 * else request fix would be of type CMMoneyTransferToBank and response should be of type CMMoneyTransferFromBank;
		 */
		if (requestFixMessage instanceof CMBankTellerMoneyTransferToBank)
		{
			fromBank = new CMBankTellerMoneyTransferFromBank();
			((CMBankTellerMoneyTransferFromBank) fromBank).setIsInDirectCashIn(((CMBankTellerMoneyTransferToBank) requestFixMessage).getIsInDirectCashIn());
		}
		else 
		{
			fromBank = new CMMoneyTransferFromBank();
		}
		
		fromBank.copy((CMMoneyTransferToBank)requestFixMessage);
		
		if(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE)){
			fromBank.setResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			fromBank.setErrorText("Sterling Bank Integration service error");			
		} 
		else
		{
			SterlingBankWebServiceResponse sterlingBankWebServiceResponse = SterlingBankWebServiceReponseParser.getSterlingBankWebServiceResponse((String)wsResponseElement);
			fromBank.setResponseCode(sterlingBankWebServiceResponse.getResponseCode());			
		}
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
				
		return fromBank;
	}

	@Override
	public String createRequestXml(SterlingBankWebServiceRequest sterlingBankWebServiceRequest) {
		String requestXml = getXmlWSCommunicator().createIntrabankFundsTransferRequest(sterlingBankWebServiceRequest);
		return requestXml;
	}
}
