package com.mfino.sterling.bank.communicator;

import java.math.BigDecimal;
import java.util.List;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.sterling.bank.util.SterlingBankWebServiceIntegrationConstants;
import com.mfino.sterling.bank.util.SterlingBankWebServiceReponseParser;
import com.mfino.sterling.bank.util.SterlingBankWebServiceRequest;
import com.mfino.sterling.bank.util.SterlingBankWebServiceResponse;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

/**
 * @author Amar
 *
 */
public class SterlingBankBalanceInquiryCommunicator extends SterlingBankWebServiceCommunicator {

	@Override
	public SterlingBankWebServiceRequest createSterlingBankWebServiceRequest(MCEMessage mceMessage) {
		log.info("SterlingBankBalanceInquiryCommunicator :: createSterlingBankWebServiceRequest mceMessage="+mceMessage);
		
		SterlingBankWebServiceRequest sterlingBankWebServiceRequest = new SterlingBankWebServiceRequest();
		CMBase requestFix = (CMBase)mceMessage.getResponse();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		CMBalanceInquiryToBank msg = (CMBalanceInquiryToBank) requestFix;
		sterlingBankWebServiceRequest.setAccount(msg.getSourceCardPAN());
		sterlingBankWebServiceRequest.setReferenceID(normalize(sctlId));
		
		return sterlingBankWebServiceRequest;
	}

	@Override
	public CFIXMsg constructReplyMessage(List<Object> response, CFIXMsg requestFixMessage) {
		Object wsResponseElement = response.get(0);
		
		log.info("SterlingBankBalanceInquiryCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
		CmFinoFIX.CMBalanceInquiryFromBank balanceInquiryFromBankresponse = new CmFinoFIX.CMBalanceInquiryFromBank();
		CMBalanceInquiryToBank toBank = (CMBalanceInquiryToBank) requestFixMessage;
		balanceInquiryFromBankresponse.copy(toBank);
		
		if(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE)){
			balanceInquiryFromBankresponse.setResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			balanceInquiryFromBankresponse.setErrorText("Sterling Bank Integration service error");			
		} 
		else
		{
			SterlingBankWebServiceResponse sterlingBankWebServiceResponse = SterlingBankWebServiceReponseParser.getSterlingBankWebServiceResponse((String)wsResponseElement);
			
			balanceInquiryFromBankresponse.setResponseCode(SterlingBankWebServiceIntegrationConstants.STERLING_BANK_WEB_SERVICE_RESPONSE_SUCCESSFUL);
			CGEntries[] entries = balanceInquiryFromBankresponse.allocateEntries(2);
			entries[0] = new CmFinoFIX.CMBalanceInquiryFromBank.CGEntries();
			
			Double bookBalance = Double.parseDouble(sterlingBankWebServiceResponse.getBook());
			entries[0].setAmount(BigDecimal.valueOf(bookBalance));
			entries[0].setBankAccountType(Integer.parseInt(toBank.getSourceBankAccountType()));
			entries[0].setCurrency(CmFinoFIX.Currency_NGN);
			entries[0].setBankAmountType(CmFinoFIX.BankAmountType_AccountLegderBalance);
			
			entries[1] = new CmFinoFIX.CMBalanceInquiryFromBank.CGEntries();
			Double availableBalance = Double.parseDouble(sterlingBankWebServiceResponse.getAvailableBalance());
			entries[1].setAmount(BigDecimal.valueOf(availableBalance));
			entries[1].setBankAccountType(Integer.parseInt(toBank.getSourceBankAccountType()));
			entries[1].setCurrency(CmFinoFIX.Currency_NGN);
			entries[1].setBankAmountType(CmFinoFIX.BankAmountType_AccountAvailableBalance);
			
		}
		
		balanceInquiryFromBankresponse.header().setSendingTime(DateTimeUtil.getLocalTime());
		balanceInquiryFromBankresponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return balanceInquiryFromBankresponse;
	}

	@Override
	public String createRequestXml(SterlingBankWebServiceRequest sterlingBankWebServiceRequest) {
		String requestXml = getXmlWSCommunicator().createBalanceEnquiryRequest(sterlingBankWebServiceRequest);
		return requestXml;
	}
}
