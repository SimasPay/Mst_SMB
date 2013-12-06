package com.mfino.sterling.bank.communicator;

import java.util.List;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.sterling.bank.util.SterlingBankWebServiceIntegrationConstants;
import com.mfino.sterling.bank.util.SterlingBankWebServiceRequest;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

/**
 * @author Amar
 *
 */
public class SterlingBankTransferInquiryCommunicator extends SterlingBankWebServiceCommunicator {

	@Override
	public SterlingBankWebServiceRequest createSterlingBankWebServiceRequest(MCEMessage mceMessage) {
		log.info("SterlingBankTransferInquiryCommunicator :: createSterlingBankWebServiceRequest mceMessage="+mceMessage);
		
		SterlingBankWebServiceRequest sterlingBankWebServiceRequest = new SterlingBankWebServiceRequest();
		return sterlingBankWebServiceRequest;
	}

	@Override
	public CFIXMsg constructReplyMessage(List<Object> response, CFIXMsg requestFixMessage) 
	{
		log.info("SterlingTransferInquiryCommunicator :: constructReplyMessage requestFixMessage="+requestFixMessage);
		
		CmFinoFIX.CMTransferInquiryFromBank transferInquiryFromBankresponse = new CmFinoFIX.CMTransferInquiryFromBank();
		CMTransferInquiryToBank toBank = (CMTransferInquiryToBank) requestFixMessage;
		
		transferInquiryFromBankresponse.copy(toBank);
		transferInquiryFromBankresponse.setResponseCode(SterlingBankWebServiceIntegrationConstants.STERLING_BANK_WEB_SERVICE_RESPONSE_SUCCESSFUL);
		transferInquiryFromBankresponse.header().setSendingTime(DateTimeUtil.getLocalTime());
		transferInquiryFromBankresponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		
		return transferInquiryFromBankresponse;
	}

	@Override
	public String createRequestXml(SterlingBankWebServiceRequest sterlingBankWebServiceRequest) {
		return null;
	}
}
