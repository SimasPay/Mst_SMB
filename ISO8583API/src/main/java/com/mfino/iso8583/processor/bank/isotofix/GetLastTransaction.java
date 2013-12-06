package com.mfino.iso8583.processor.bank.isotofix;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.ISinarmasISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;

public class GetLastTransaction implements ISinarmasISOtoFIXProcessor{

	@Override
    public CFIXMsg process(SinarmasISOMessage isoMsg, CFIXMsg request) throws Exception {
		
		
		CMGetLastTransactionsToBank toBank = (CMGetLastTransactionsToBank)request;
		if(!CmFinoFIX.ISO8583_ResponseCode_Success.equals(isoMsg.getResponseCode()))
			return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
		
		CMGetLastTransactionsFromBank fromBank =  new CMGetLastTransactionsFromBank();
		fromBank.copy(toBank);
		fromBank.setAIR(isoMsg.getAuthorizationIdentificationResponse());
		fromBank.setResponseCode(isoMsg.getResponseCode());
		CMGetLastTransactionsFromBank.CGEntries[] entries = fromBank.allocateEntries(10);
		String transactions = isoMsg.getLastTransactions();
		int index=0;
		int entrySize=48;
		while(transactions.length()>0&&index < fromBank.getEntries().length) {
			String tran  = transactions.substring(0	, entrySize);
			transactions =transactions.substring(entrySize);
			entries[index] = new CMGetLastTransactionsFromBank.CGEntries();
			entries[index].setBankTransactionDate("20"+tran.substring(4	, 6)+tran.substring(2,4)+tran.substring(0,2));
			entries[index].setBankTransactionFlag(tran.charAt(6));
			entries[index].setBankTransactionCodeDescription(tran.substring(7,17));
			entries[index].setCurrency(tran.substring(17,20));
			entries[index].setAmount(new BigDecimal(tran.substring(20, 34)));
			entries[index].setBankTransactionCode(tran.substring(34, 38));
			//NOTE: this is different from the spec. on the spec, the length is 16
			entries[index].setBankTransactionReferenceNumber(tran.substring(38, 48));
			index++;
		}
//		fromBank.header().setMsgSeqNum(null);
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		
		return fromBank;
    }

}
