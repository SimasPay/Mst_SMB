package com.mfino.iso8583.processor.zenithbank.isotofix;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.processor.zenithbank.AdditionalAmounts;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;

public class BalanceInquiryFromZenith implements IZenithBankISOtoFixProcessor {

	@Override
	public CFIXMsg process(ZenithBankISOMessage isoMsg, CFIXMsg request) throws Exception {

		CmFinoFIX.CMBalanceInquiryFromBank response = new CmFinoFIX.CMBalanceInquiryFromBank();
//		if (!(request instanceof CMBalanceInquiryToBank))
	//		throw new Exception("not an instance of CMBalanceInquiryFromBank");
		CMBalanceInquiryToBank toBank = (CMBalanceInquiryToBank) request;

		if(!CmFinoFIX.ResponseCode_Success.toString().equals(isoMsg.getResponseCode()))
			return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
		
		response.copy(toBank);
		response.setAIR(isoMsg.getAuthorizationIdentificationResponse());
		response.setResponseCode(isoMsg.getResponseCode());
		CGEntries[] entries = response.allocateEntries(10);
		String amounts = isoMsg.getAdditionalAmounts();
		int index = 0;
		int entrySize = 20;
		while (amounts.length() > index * entrySize && index < response.getEntries().length) {

			AdditionalAmounts aa = AdditionalAmounts.parseAdditionalAmounts(amounts);

			if (aa.getAmountSign() == 'D')
				aa.setAmount(new BigDecimal(-1).multiply(aa.getAmount()));
			entries[index] = new CmFinoFIX.CMBalanceInquiryFromBank.CGEntries();
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

		return response;

	}
	
}
