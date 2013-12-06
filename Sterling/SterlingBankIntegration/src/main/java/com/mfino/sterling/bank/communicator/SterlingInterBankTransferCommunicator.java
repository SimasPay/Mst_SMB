package com.mfino.sterling.bank.communicator;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.domain.IntegrationSummary;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.mce.backend.IntegrationSummaryService;
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
public class SterlingInterBankTransferCommunicator extends SterlingBankWebServiceCommunicator {
	private IntegrationSummaryService integrationSummaryService;
	
	public IntegrationSummaryService getIntegrationSummaryService() {
		return integrationSummaryService;
	}

	public void setIntegrationSummaryService(
			IntegrationSummaryService integrationSummaryService) {
		this.integrationSummaryService = integrationSummaryService;
	}

	@Override
	public SterlingBankWebServiceRequest createSterlingBankWebServiceRequest(MCEMessage mceMessage) {
		log.info("SterlingInterBankTransferCommunicator :: createSterlingBankWebServiceRequest mceMessage="+mceMessage);
		
		SterlingBankWebServiceRequest sterlingBankWebServiceRequest = new SterlingBankWebServiceRequest();
			
		CMInterBankMoneyTransferToBank requestFix = (CMInterBankMoneyTransferToBank) mceMessage.getResponse();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
	
		IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		
		IntegrationSummary integrationSummary = integrationSummaryService.getIntegrationSummary(sctlId, null);
				
		
		/*
		 * NEResponse
		 */
		sterlingBankWebServiceRequest.setNeResponse(integrationSummary.getReconcilationID1());
		/*
		 * BeneFiName
		 */
		sterlingBankWebServiceRequest.setBenefiName(integrationSummary.getReconcilationID2());
		/*
		 * SessionID
		 */
		sterlingBankWebServiceRequest.setSessionID(integrationSummary.getReconcilationID3());
		sterlingBankWebServiceRequest.setPaymentReference(requestFix.getSourceAccountName());
		sterlingBankWebServiceRequest.setDestinationBankCode(requestFix.getDestBankCode());
		
		sterlingBankWebServiceRequest.setFromAccount(requestFix.getSourceAccountNumber());
		sterlingBankWebServiceRequest.setToAccount(requestFix.getDestAccountNumber());
		sterlingBankWebServiceRequest.setAmount(requestFix.getAmount().toPlainString());
		sterlingBankWebServiceRequest.setReferenceID(normalize(sctlId));	
		return sterlingBankWebServiceRequest;
	}

	@Override
	public CFIXMsg constructReplyMessage(List<Object> response, CFIXMsg requestFixMessage) {
		Object wsResponseElement = response.get(0);
		
		log.info("SterlingInterBankTransferCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
				
		CMInterBankMoneyTransferToBank toBank = (CMInterBankMoneyTransferToBank)requestFixMessage;
		CMInterBankMoneyTransferFromBank fromBank = new CMInterBankMoneyTransferFromBank();	
		fromBank.copy(toBank);
		
		if(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE)){
			fromBank.setResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			fromBank.setErrorText("Sterling Bank Integration service error");			
		} 
		else
		{
			SterlingBankWebServiceResponse sterlingBankWebServiceResponse = SterlingBankWebServiceReponseParser.getSterlingBankWebServiceResponse((String)wsResponseElement);
			fromBank.setResponseCode(sterlingBankWebServiceResponse.getResponseCode());		
			
			fromBank.setSourceApplication(toBank.getSourceApplication());
			fromBank.setSourceMDN(toBank.getSourceMDN());
			fromBank.setDestBankCode(toBank.getDestBankCode());
			fromBank.setDestAccountNumber(toBank.getDestAccountNumber());
			fromBank.setServiceChargeTransactionLogID(toBank.getServiceChargeTransactionLogID());
			fromBank.setTerminalID(toBank.getTerminalID());
			fromBank.setSourceAccountNumber(toBank.getSourceAccountNumber());		
			
		}
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
				
		return fromBank;
	}

	@Override
	public String createRequestXml(SterlingBankWebServiceRequest sterlingBankWebServiceRequest) {
		String requestXml = getXmlWSCommunicator().createInterbankFundsTransferRequest(sterlingBankWebServiceRequest);
		return requestXml;
	}
}
