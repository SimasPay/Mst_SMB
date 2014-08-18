package com.mfino.bsm.uangku.iso8583.processor.isotofix;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.uangku.iso8583.GetConstantCodes;
import com.mfino.bsm.uangku.iso8583.processor.BSMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class GetLastTrxnsFromBankProcessor implements BSMISOtoFixProcessor {

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
			CMGetLastTransactionsFromBank.CGEntries[] entries = fromBank.allocateEntries(10);
			String transactions = isoMsg.getString(61);
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
		}
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}

	/*public static void main(String[] args) throws Exception {
		CMGetLastTransactionsToBank request = new CMGetLastTransactionsToBank();
		ISOMsg iso = new ISOMsg();
		iso.set(39, "00");
		iso.set(48,
		        "TERM_ID|DATE_TIME|ACC_ID1|TRAN_TYPE|TRAN_AMOUNT|FROM_ACC|CURR_CODE~00000000|20110413000000|0000000000000000000000000072|91|4000000||566~00000000|20110413000000|0000000000000000000000000072|91|1355500||566~00000000|20110408000000|0000000000000000000000000072|91|350000||566~00000000|20110408000000|0000000000000000000000000072|91|950000||566~00000000|20110408000000|0000000000000000000000000072|91|1000000||566~00000000|20110408000000|0000000000000000000000000072|91|2500000||566~00000000|20110406000000|0000000000000000000000000072|91|1400000||566~00000000|20110405000000|0000000000000000000000000072|91|2000000||566~00000000|20110331000000|0000000000000000000000000072|91|3000000||566~00000000|20110330000000|0000000000000000000000000072|91|800000||566~");
		GetLastTrxnsFromBankProcessor processor = new GetLastTrxnsFromBankProcessor();
		CMGetLastTransactionsFromBank response = (CMGetLastTransactionsFromBank) processor.process(iso, request);
	}*/
}
