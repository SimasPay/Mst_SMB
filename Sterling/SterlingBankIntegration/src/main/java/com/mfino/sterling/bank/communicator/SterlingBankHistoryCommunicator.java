package com.mfino.sterling.bank.communicator;

import java.util.Iterator;
import java.util.List;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
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
public class SterlingBankHistoryCommunicator extends SterlingBankWebServiceCommunicator {

	public static final String NO_OF_RECORDS = "5";
	public static final String BANK_NAME = "Sterling";
	@Override
	public SterlingBankWebServiceRequest createSterlingBankWebServiceRequest(MCEMessage mceMessage) {
		log.info("SterlingBankHistoryCommunicator :: createSterlingBankWebServiceRequest mceMessage="+mceMessage);
		
		SterlingBankWebServiceRequest sterlingBankWebServiceRequest = new SterlingBankWebServiceRequest();
		
 		CMBase requestFix = (CMBase)mceMessage.getResponse();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();
		
		CMGetLastTransactionsToBank msg = (CMGetLastTransactionsToBank) requestFix;
		sterlingBankWebServiceRequest.setAccount(msg.getSourceCardPAN());
		sterlingBankWebServiceRequest.setReferenceID(normalize(sctlId));
		
		sterlingBankWebServiceRequest.setRecords(NO_OF_RECORDS);
		return sterlingBankWebServiceRequest;
	}

	@Override
	public CFIXMsg constructReplyMessage(List<Object> response, CFIXMsg requestFixMessage) {
		Object wsResponseElement = response.get(0);
		
		log.info("SterlingBankHistoryCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestFixMessage="+requestFixMessage);
		CMGetLastTransactionsFromBank fromBank = new CMGetLastTransactionsFromBank();
		CMGetLastTransactionsToBank toBank = (CMGetLastTransactionsToBank) requestFixMessage;
		fromBank.copy(toBank);
		
		if(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE)){
			fromBank.setResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			fromBank.setErrorText("Sterling Bank Integration service error");			
		} 
		else
		{
			fromBank.setResponseCode(SterlingBankWebServiceIntegrationConstants.STERLING_BANK_WEB_SERVICE_RESPONSE_SUCCESSFUL);
			SterlingBankWebServiceResponse sterlingBankWebServiceResponse = SterlingBankWebServiceReponseParser.getSterlingBankWebServiceResponse((String)wsResponseElement);
			StringBuilder txnHistory = new StringBuilder("BANK_NAME|TRAN_DATE|TRAN_TYPE|CURR_CODE|TRAN_AMOUNT~");
			
			List<SterlingBankWebServiceResponse.Record> records = sterlingBankWebServiceResponse.getRecords();
			Iterator<SterlingBankWebServiceResponse.Record> it = records.iterator();
			TransactionHistory history = null;
			while (it.hasNext()) 
			{
				history = new TransactionHistory();
				history.setBankName(BANK_NAME);
				history.setCurrencyCode(CmFinoFIX.Currency_NGN);
				SterlingBankWebServiceResponse.Record record = it.next();
				history.setDateTime(record.getDate());
				if(record.getIsCredit())
				{
					history.setTransactionType("Credit");
				}
				else
				{
					history.setTransactionType("Debit");
				}
				Double amount = Double.parseDouble(record.getAmount());
				/*
				 * Multiplying it by 100 to make it compatible to ISO message format.
				 */
				amount = amount * 100;
				history.setTransactionAmount(amount.toString());
				txnHistory.append(history.toString() + "~");
			}

			log.info("SterlingBankHistoryCommunicator :: constructReplyMessage txnHistory="+txnHistory);
			fromBank.setTransactionHistory(txnHistory.toString());
		}
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}


	@Override
	public String createRequestXml(SterlingBankWebServiceRequest sterlingBankWebServiceRequest) {
		String requestXml = getXmlWSCommunicator().createMiniStatementRequest(sterlingBankWebServiceRequest);
		return requestXml;
	}
}
