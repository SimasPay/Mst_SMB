package com.mfino.nfc.iso8583.processor.isotofix;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.nfc.iso8583.GetConstantCodes;
import com.mfino.nfc.iso8583.processor.NFCISOtoFixProcessor;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

/**
 * 
 * @author Amar
 *
 */
public class GetLastTrxnsFromBankProcessor implements NFCISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMGetLastTransactionsToBank toBank = (CMGetLastTransactionsToBank) request;
		CMGetLastTransactionsFromBank fromBank = new CMGetLastTransactionsFromBank();
		fromBank.copy(toBank);
		fromBank.setTransactionHistory(StringUtils.EMPTY);
		fromBank.setPocketID(toBank.getPocketID());
		
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if (isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		
		if (isoMsg.hasField(54) && StringUtils.isNotBlank(isoMsg.getString(120)))
		{
			String cmsBalance = isoMsg.getString(54);
			fromBank.setAmount(new BigDecimal(cmsBalance.substring(5, 17)));
		}
	
		int RecordsFieldNo = 120;
		
		if (GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		{boolean moreRecordsAvailable = true;
			List<CMGetLastTransactionsFromBank.CGEntries> entries = new ArrayList<CMGetLastTransactionsFromBank.CGEntries>();
			
			while(moreRecordsAvailable && StringUtils.isNotBlank(isoMsg.getString(RecordsFieldNo)))
			{
				String transactions = isoMsg.getString(RecordsFieldNo);	
				int entrySize=69;
				int transactionCount = Integer.parseInt(transactions.substring(0, 2));
				transactions = transactions.substring(2);
				while(transactionCount > 0) {
					String transaction  = transactions.substring(0	, entrySize);
					transactions = transactions.substring(entrySize);
					CMGetLastTransactionsFromBank.CGEntries entry = new CMGetLastTransactionsFromBank.CGEntries();
					entry.setBankName("");	//Change the Bank Name appropriately 
					SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyhhmmss");
					Date transactionDate;
					try {
						transactionDate = dateFormat.parse(transaction.substring(0, 12));
					} catch (ParseException e) {
						throw new InvalidIsoElementException(e.getMessage());
					}
					entry.setBankTransactionDate(new SimpleDateFormat("dd/MM/yy hh:mm").format(transactionDate));
					entry.setBankTransactionFlag(transaction.charAt(12));
					entry.setCurrency(transaction.substring(13,16));
					entry.setAmount(new BigDecimal(transaction.substring(16, 30)));
					entry.setBankTransactionCode(transaction.substring(30, 34));
					entry.setBankTransactionReferenceNumber(transaction.substring(34, 44));
					entry.setMerchantName(transaction.substring(44, 69));
					entry.setSourceCardPAN(toBank.getSourceCardPAN());
					entry.setCardAlias(toBank.getCardAlias());
					entries.add(entry);
					transactionCount--;
				}
				RecordsFieldNo++;
				if(transactions.equals("1"))
				{
					moreRecordsAvailable = true;
				}	
				else
				{
					moreRecordsAvailable = false;
				}				
			}
			
			fromBank.setMoreRecordsAvailable(moreRecordsAvailable);
			fromBank.allocateEntries(entries.size());
			fromBank.setEntries(entries.toArray(new CMGetLastTransactionsFromBank.CGEntries[0]));
		
		}
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}

}
