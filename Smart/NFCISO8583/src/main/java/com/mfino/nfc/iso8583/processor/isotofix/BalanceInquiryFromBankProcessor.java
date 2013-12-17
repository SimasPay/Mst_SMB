package com.mfino.nfc.iso8583.processor.isotofix;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromNFC;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.nfc.iso8583.GetConstantCodes;
import com.mfino.nfc.iso8583.processor.NFCISOtoFixProcessor;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class BalanceInquiryFromBankProcessor implements NFCISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CMBalanceInquiryFromNFC fromBank = new CMBalanceInquiryFromNFC();
		CMBalanceInquiryToBank toBank = (CMBalanceInquiryToBank) request;

		fromBank.copy(toBank);
		
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		
		//if the response is failure there is any point trying to parse the result
		if(GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)) && StringUtils.isNotBlank(isoMsg.getString(54)))
		{	
			String balances = isoMsg.getString(54);	
			int entrySize=31;
			//int balances_length = balances.length()/entrySize;
			String nofLinkedCards = balances.substring(0,2);
			Integer balances_length = Integer.parseInt(nofLinkedCards);
			CMBalanceInquiryFromNFC.CGEntries[] entries = fromBank.allocateEntries(balances_length);
			int index=0;
			balances = balances.substring(2);
			while(balances.length()>0&&index < fromBank.getEntries().length) {
				String cmsBalance  = balances.substring(0	, entrySize);
				balances = balances.substring(entrySize);
				entries[index] = new CMBalanceInquiryFromNFC.CGEntries();
				entries[index].setBankAccountType(CmFinoFIX.BankAccountType_NFCCard);
				entries[index].setSourceCardPAN(cmsBalance.substring(0,16));
				entries[index].setCurrency(cmsBalance.substring(16,19));
				entries[index].setAmount(new BigDecimal(cmsBalance.substring(19, 31)));
				entries[index].setBankAmountType(CmFinoFIX.BankAmountType_AccountAvailableBalance);		
				index++;
			}
		}
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}
}
