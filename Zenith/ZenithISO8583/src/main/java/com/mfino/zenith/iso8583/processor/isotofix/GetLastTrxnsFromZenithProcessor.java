package com.mfino.zenith.iso8583.processor.isotofix;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;
import com.mfino.zenith.iso8583.GetConstantCodes;
import com.mfino.zenith.iso8583.processor.IZenithBankISOtoFixProcessor;

public class GetLastTrxnsFromZenithProcessor implements IZenithBankISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		final String LINE_SEPERATOR = "~";
		final String FIELD_SEPERATOR = "|";
		final String BANK_NAME = "Zenith";
		CMGetLastTransactionsToBank toBank = (CMGetLastTransactionsToBank) request;
		//if (!CmFinoFIX.ISO8583_ResponseCode_Success.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);

		CMGetLastTransactionsFromBank fromBank = new CMGetLastTransactionsFromBank();
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		fromBank.copy(toBank);
		fromBank.setTransactionHistory(StringUtils.EMPTY);
		if (isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		
		if ((GetConstantCodes.SUCCESS.equals(isoMsg.getString(39))) && StringUtils.isNotBlank(isoMsg.getString(48)))
		{
			String transactions = isoMsg.getString(48);
			transactions = transactions.replace(FIELD_SEPERATOR, " "+FIELD_SEPERATOR+" ");
			Map<Integer, String> lineMap = new HashMap<Integer, String>();
			if (StringUtils.isBlank(transactions)) {
				return fromBank;
			}
	
			StringTokenizer lines = new StringTokenizer(transactions, LINE_SEPERATOR);
			if (lines.hasMoreTokens()) {
				String line = lines.nextToken();
				StringTokenizer fields = new StringTokenizer(line, FIELD_SEPERATOR);
				for (int i = 0; fields.hasMoreTokens(); i++) {
					lineMap.put(i, fields.nextToken().trim());
				}
			}
			int j = 0;
			StringBuilder txnHistory = new StringBuilder("BANK_NAME|TRAN_DATE|TRAN_TYPE|CURR_CODE|TRAN_AMOUNT~");
			TransactionHistory history = null;
			while (lines.hasMoreTokens()) {
				history = new TransactionHistory();
				history.setBankName(BANK_NAME);
				String line = lines.nextToken();
				StringTokenizer fields = new StringTokenizer(line, FIELD_SEPERATOR);
				for (int i = 0; fields.hasMoreTokens(); i++) {
					String field = fields.nextToken();
					field = field.trim();
					if (lineMap.get(i).equals("DATE_TIME"))
						history.setDateTime(field);
					else if (lineMap.get(i).equals("TRAN_TYPE"))
						history.setTransactionType(field);
					else if (lineMap.get(i).equals("ACC_ID1"))
						history.setFromAccount(field);
					else if (lineMap.get(i).equals("ACC_ID2"))
						history.setToAccount(field);
					else if (lineMap.get(i).equals("TRAN_AMOUNT"))
						history.setTransactionAmount(field);
					else if (lineMap.get(i).equals("CURR_CODE"))
						history.setCurrencyCode(field);
				}
				txnHistory.append(history.toString() + "~");
				j++;
			}
			fromBank.setTransactionHistory(txnHistory.toString());
		}
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}

	public static void main(String[] args) throws Exception {
		CMGetLastTransactionsToBank request = new CMGetLastTransactionsToBank();
		ISOMsg iso = new ISOMsg();
		iso.set(39, "00");
		iso.set(48,
		        "TERM_ID|DATE_TIME|ACC_ID1|TRAN_TYPE|TRAN_AMOUNT|FROM_ACC|CURR_CODE~00000000|20110413000000|0000000000000000000000000072|91|4000000||566~00000000|20110413000000|0000000000000000000000000072|91|1355500||566~00000000|20110408000000|0000000000000000000000000072|91|350000||566~00000000|20110408000000|0000000000000000000000000072|91|950000||566~00000000|20110408000000|0000000000000000000000000072|91|1000000||566~00000000|20110408000000|0000000000000000000000000072|91|2500000||566~00000000|20110406000000|0000000000000000000000000072|91|1400000||566~00000000|20110405000000|0000000000000000000000000072|91|2000000||566~00000000|20110331000000|0000000000000000000000000072|91|3000000||566~00000000|20110330000000|0000000000000000000000000072|91|800000||566~");
		GetLastTrxnsFromZenithProcessor processor = new GetLastTrxnsFromZenithProcessor();
		CMGetLastTransactionsFromBank response = (CMGetLastTransactionsFromBank) processor.process(iso, request);
	}
}
