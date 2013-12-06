package com.mfino.iso8583.processor.zenithbank.isotofix;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.mfino.util.DateTimeUtil;

public class GetLastTrxnsFromZenith implements IZenithBankISOtoFixProcessor {

	@Override
	public CFIXMsg process(ZenithBankISOMessage isoMsg,CFIXMsg request) throws Exception {
		
		CMGetLastTransactionsToBank toBank = (CMGetLastTransactionsToBank)request;
		if(!CmFinoFIX.ISO8583_ResponseCode_Success.equals(isoMsg.getResponseCode()))
			return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
		
		CMGetLastTransactionsFromBank fromBank =  new CMGetLastTransactionsFromBank();
		fromBank.copy(toBank);
		fromBank.setAIR(isoMsg.getAuthorizationIdentificationResponse());
		fromBank.setResponseCode(isoMsg.getResponseCode());
		CMGetLastTransactionsFromBank.CGEntries[] entries = fromBank.allocateEntries(10);
		String transactions = isoMsg.getAdditionalAmounts();
		int index=0;
		int entrySize=38;
		while(transactions.length()>0&&index < fromBank.getEntries().length) {
			String tran  = transactions.substring(0	, entrySize);
			transactions =transactions.substring(entrySize);
			
			entries[index] = new CMGetLastTransactionsFromBank.CGEntries();
			entries[index].setBankTransactionDate(tran.substring(0,13));
			entries[index].setBankTransactionReferenceNumber(tran.substring(14, 6));
			entries[index].setAmount(new BigDecimal(tran.substring(25, 37)));
			index++;
		}
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		
		return fromBank;
	}
}

