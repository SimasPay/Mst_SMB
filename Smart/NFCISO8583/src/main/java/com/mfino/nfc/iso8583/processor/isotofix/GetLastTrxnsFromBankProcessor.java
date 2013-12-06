package com.mfino.nfc.iso8583.processor.isotofix;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		
		if (isoMsg.hasField(54) && StringUtils.isNotBlank(isoMsg.getString(61)))
		{
			String cmsBalance = isoMsg.getString(54);
			fromBank.setAmount(new BigDecimal(cmsBalance.substring(5, 17)));
		}
	
		if ((GetConstantCodes.SUCCESS.equals(isoMsg.getString(39))) && StringUtils.isNotBlank(isoMsg.getString(61)))
		{
			String transactions = isoMsg.getString(61);	
			int entrySize=69;
			int transaction_length = transactions.length()/entrySize;
			CMGetLastTransactionsFromBank.CGEntries[] entries = fromBank.allocateEntries(transaction_length);
			int index=0;
			while(transactions.length()>0&&index < fromBank.getEntries().length) {
				String transaction  = transactions.substring(0	, entrySize);
				transactions =transactions.substring(entrySize);
				entries[index] = new CMGetLastTransactionsFromBank.CGEntries();
				entries[index].setBankName("Uangku");	//Change the Bank Name appropriately 
				SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyhhmmss");
				Date transactionDate;
				try {
					transactionDate = dateFormat.parse(transaction.substring(0, 12));
				} catch (ParseException e) {
					throw new InvalidIsoElementException(e.getMessage());
				}
				entries[index].setBankTransactionDate(new SimpleDateFormat("dd/MM/yy hh:mm").format(transactionDate));
				entries[index].setBankTransactionFlag(transaction.charAt(12));
				entries[index].setCurrency(transaction.substring(13,16));
				entries[index].setAmount(new BigDecimal(transaction.substring(16, 30)));
				entries[index].setBankTransactionCode(transaction.substring(30, 34));
				entries[index].setBankTransactionReferenceNumber(transaction.substring(34, 44));
				entries[index].setMerchantName(transaction.substring(44, 69));
				entries[index].setSourceCardPAN(toBank.getSourceCardPAN());
				entries[index].setCardAlias(toBank.getCardAlias());
				
				index++;
			}
		}
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}

}
