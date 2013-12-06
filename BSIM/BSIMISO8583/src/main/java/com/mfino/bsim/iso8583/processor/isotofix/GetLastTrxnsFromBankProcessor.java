package com.mfino.bsim.iso8583.processor.isotofix;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class GetLastTrxnsFromBankProcessor implements BSIMISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMGetLastTransactionsToBank toBank = (CMGetLastTransactionsToBank) request;
		CMGetLastTransactionsFromBank fromBank = new CMGetLastTransactionsFromBank();
		fromBank.copy(toBank);
		fromBank.setTransactionHistory(StringUtils.EMPTY);
		
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if (isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
	
		if ((GetConstantCodes.SUCCESS.equals(isoMsg.getString(39))) && StringUtils.isNotBlank(isoMsg.getString(61)))
		{
			String transactions = isoMsg.getString(61);			
			int transaction_length = transactions.length()/48;
			CMGetLastTransactionsFromBank.CGEntries[] entries = fromBank.allocateEntries(transaction_length);
			int index=0;
			int entrySize=48;
			while(transactions.length()>0&&index < fromBank.getEntries().length) {
				String tran  = transactions.substring(0	, entrySize);
				transactions =transactions.substring(entrySize);
				entries[index] = new CMGetLastTransactionsFromBank.CGEntries();
				entries[index].setBankName("BSIM");	//Change the Bank Name appropriately 
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
		}
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}

	/*public static void main(String[] args) throws Exception {
		CMGetLastTransactionsToBank request = new CMGetLastTransactionsToBank();
		ISOMsg iso = new ISOMsg();
		iso.set(39, "00");
		031111CE-TransferIDR000000000055000268FT11307JRQ0
		iso.set(48,
		        "031111CE-TransferIDR000000000055000268FT11307JRQ031111DE-TransferIDR000000000055000268FT11307JRQ031111CE-TransferIDR000000000030000268FT11307D44");
		GetLastTrxnsFromBankProcessor processor = new GetLastTrxnsFromBankProcessor();
		CMGetLastTransactionsFromBank response = (CMGetLastTransactionsFromBank) processor.process(iso, request);
	}*/
}
