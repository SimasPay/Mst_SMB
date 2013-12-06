package com.mfino.fidelity.iso8583.processor.isotofix;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.fidelity.iso8583.GetConstantCodes;
import com.mfino.fidelity.iso8583.processor.FidelityISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class GetLastTrxnsFromFidelityProcessor implements FidelityISOtoFixProcessor {

	public Log log = LogFactory.getLog(this.getClass());
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		final String LINE_SEPERATOR = "~";
		final String FIELD_SEPERATOR = "|";
		final String BANK_NAME = "Fidelity";
		CMGetLastTransactionsToBank toBank = (CMGetLastTransactionsToBank) request;
		CMGetLastTransactionsFromBank fromBank = new CMGetLastTransactionsFromBank();
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		fromBank.copy(toBank);
		fromBank.setTransactionHistory(StringUtils.EMPTY);
		if (isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		
		if ((GetConstantCodes.SUCCESS.equals(isoMsg.getString(39))) && StringUtils.isNotBlank(isoMsg.getString(125)))
		{
			fromBank.setResponseCode(CmFinoFIX.ISO8583_ResponseCode_Success);
			String transactions = isoMsg.getString(125);
			StringBuilder txnHistory = new StringBuilder("BANK_NAME|TRAN_DATE|TRAN_TYPE|CURR_CODE|TRAN_AMOUNT|TRAN_PARTICULARS~");
			int size = transactions.length()/79;
			int start = 0 ;
			TransactionHistory history = null;
			for(int i=0;i<size;i++){				
				String entry = transactions.substring(start, (i+1)*79);
				history = new TransactionHistory();
				history.setBankName(BANK_NAME);
				history.setDateTime(entry.substring(0, 8));
				
				String instrumentID = entry.substring(8, 16).trim();
				String trreportCode = entry.substring(16, 21).trim();
				String particulars = entry.substring(21, 61).trim();
				String bankFlag = entry.substring(61, 62);
				String amount = entry.substring(62, 79).trim();
				
				history.setTransactionAmount(amount);
				history.setTransactionType(bankFlag);
				history.setParticulars(particulars);
				history.setCurrencyCode("566");
				txnHistory.append(history.toString() + "~");
				start =(i+1)*79;
			}
			fromBank.setTransactionHistory(txnHistory.toString());
		}
		return fromBank;
	}
			

	public static void main(String[] args) throws Exception {
		CMGetLastTransactionsToBank request = new CMGetLastTransactionsToBank();
		ISOMsg iso = new ISOMsg();
		iso.set(39, "000");
		iso.set(125,
		        "20130628             6070000208:Int.Pd:01-04-2013 to 30-06-20C           384.8020130628             6070000208:WTax.Pd:01-04-2013to 30-06-20D            38.4820130627             ZENITH-37958-DW-AMADI THEO              C          5400.0020130627             SKYE-96201548-DW-AMADI THEO             C         31500.0020130620             IQTL/10161371734428903/0005932242/TAMADID         45100.0020130619             ATM WD @ 10700167-FBP2,  KOFO ABAYOM    D         15000.0020130618             ATM WD @ 10700166-FBP2,  KOFO ABAYOM    D          5000.0020130617             ATM WD @ 10701731-FB,AIRPORT ROAD BR    D         20000.0020130617             POS Purchase @ 20708F53-EZEKIEL FLORENCED          2910.0020130614             VISA WD @ 10700168-107001660000000&gt;LAD         10000.00");
		GetLastTrxnsFromFidelityProcessor processor = new GetLastTrxnsFromFidelityProcessor();
		CMGetLastTransactionsFromBank response = (CMGetLastTransactionsFromBank) processor.process(iso, request);
		String[] sp = response.getTransactionHistory().split("~");
		for(String e:sp){
			System.out.println(e);
		}
	}
}
