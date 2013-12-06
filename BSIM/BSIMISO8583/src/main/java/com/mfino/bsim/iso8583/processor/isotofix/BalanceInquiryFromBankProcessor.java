package com.mfino.bsim.iso8583.processor.isotofix;

import java.math.BigDecimal;

import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.AdditionalAmounts;
import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class BalanceInquiryFromBankProcessor implements BSIMISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CmFinoFIX.CMBalanceInquiryFromBank response = new CmFinoFIX.CMBalanceInquiryFromBank();
		CMBalanceInquiryToBank toBank = (CMBalanceInquiryToBank) request;

		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
		
		response.copy(toBank);
		if(isoMsg.hasField(38))
			response.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			response.setResponseCode(isoMsg.getString(39));
		
		//if the response is failure there is any point trying to parse the result
		if(GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		{
			String amounts = isoMsg.getString(54);
			// entry size is always 20 with feild being 
			int entrySize = 26; 
			CGEntries[] entries = response.allocateEntries(amounts.length()/entrySize);
		
			int index = 0;
			while (amounts.length() > index * entrySize && index < response.getEntries().length) {
	
				String checkbalanceContent = amounts.substring(index*entrySize,(index+1)*entrySize);
				AdditionalAmounts aa=null;
                try {
	                aa = AdditionalAmounts.parseAdditionalAmounts(checkbalanceContent);
                }
                catch (Exception ex) {
	                ex.printStackTrace();
	                InvalidIsoElementException exception = new InvalidIsoElementException("54");
	                exception.fillInStackTrace();
	                throw exception;
                }
	
				if (aa.getAmountSign() == 'D')
					aa.setAmount(new BigDecimal(-1).multiply(aa.getAmount()));
				entries[index] = new CmFinoFIX.CMBalanceInquiryFromBank.CGEntries();
                //long amount = aa.getAmount().longValue()/100;
                //BigDecimal amt = new BigDecimal(amount);
				entries[index].setAmount(aa.getAmount());
				entries[index].setBankAccountType(aa.getAccountType());
				entries[index].setBankAmountType(aa.getAmountType());
				
				if (aa.getCurrencyCode() == CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_IDR)
					entries[index].setCurrency(String.valueOf(aa.getCurrencyCode()));
				else if (aa.getCurrencyCode() == CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_USD)
					entries[index].setCurrency(String.valueOf(aa.getCurrencyCode()));
				else
					entries[index].setCurrency(String.valueOf(CmFinoFIX.Currency_UnKnown));
				
				index++;
			}
		}
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
