package com.mfino.sterling.bank.communicator;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.domain.IntegrationSummary;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
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
public class SterlingInterBankTransferInquiryCommunicator extends SterlingBankWebServiceCommunicator {

	@Override
	public SterlingBankWebServiceRequest createSterlingBankWebServiceRequest(MCEMessage mceMessage) {
		log.info("SterlingInterBankTransferInquiryCommunicator :: createSterlingBankWebServiceRequest mceMessage="+mceMessage);
		
		SterlingBankWebServiceRequest sterlingBankWebServiceRequest = new SterlingBankWebServiceRequest();
		CMBase requestFix = (CMBase)mceMessage.getResponse();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		CMInterBankTransferInquiryToBank msg = (CMInterBankTransferInquiryToBank) requestFix;
		sterlingBankWebServiceRequest.setToAccount(msg.getDestCardPAN());
		sterlingBankWebServiceRequest.setReferenceID(normalize(sctlId));
		sterlingBankWebServiceRequest.setDestinationBankCode(msg.getDestBankCode());
		
		return sterlingBankWebServiceRequest;
	}

	@Override
	public CFIXMsg constructReplyMessage(List<Object> response, CFIXMsg requestFixMessage) {
		Object wsResponseElement = response.get(0);
		
		log.info("SterlingInterBankTransferInquiryCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
		CMInterBankTransferInquiryFromBank fromBank = new CMInterBankTransferInquiryFromBank();
		CMInterBankTransferInquiryToBank toBank = (CMInterBankTransferInquiryToBank) requestFixMessage;
		fromBank.copy(toBank);
		
		if(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE)){
			fromBank.setResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			fromBank.setErrorText("Sterling Bank Integration service error");			
		} 
		else
		{
			SterlingBankWebServiceResponse sterlingBankWebServiceResponse = SterlingBankWebServiceReponseParser.getSterlingBankWebServiceResponse((String)wsResponseElement);
			
			fromBank.setResponseCode(sterlingBankWebServiceResponse.getResponseCode());
			fromBank.setDestBankCode(toBank.getDestBankCode());
			if(SterlingBankWebServiceIntegrationConstants.STERLING_BANK_WEB_SERVICE_RESPONSE_SUCCESSFUL.equals(sterlingBankWebServiceResponse.getResponseCode()))
			{
				/*
				 * Saves some of the attributes returned by the web service that are used later during confirmation call.
				 */
				updateIntegrationSummary(toBank.getServiceChargeTransactionLogID(), sterlingBankWebServiceResponse);
			}
			
		}
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}
	
	private void updateIntegrationSummary(Long sctlId, SterlingBankWebServiceResponse sterlingBankWebServiceResponse)
	{
		IntegrationSummary integrationSummary = new IntegrationSummary();
		IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		
		integrationSummary.setSctlId(sctlId);
		/*
		 * NEResponse
		 */
		integrationSummary.setReconcilationID1(sterlingBankWebServiceResponse.getResponseCode());
		/*
		 * BeneFiName
		 */
		integrationSummary.setReconcilationID2(sterlingBankWebServiceResponse.getResponseText());
		/*
		 * SessionID 
		 */
		integrationSummary.setReconcilationID3(sterlingBankWebServiceResponse.getSessionID());
		integrationSummaryDao.save(integrationSummary);
	}

	@Override
	public String createRequestXml(SterlingBankWebServiceRequest sterlingBankWebServiceRequest) {
		String requestXml = getXmlWSCommunicator().createInterbankNameQueryRequest(sterlingBankWebServiceRequest);
		return requestXml;
	}
}
