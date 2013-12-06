package com.mfino.iso8583.processor.bank.isotofix;


import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.AdditionalAmounts;
import com.mfino.iso8583.processor.bank.ISinarmasISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;

public class BalanceInquiry implements ISinarmasISOtoFIXProcessor {

	@Override
	public CFIXMsg process(SinarmasISOMessage isoMsg, CFIXMsg request) throws Exception {

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
/*
 * String rc = isoMsg.getResponseCode(); if
 * (!rc.equals(CmFinoFIX.ISO8583_ResponseCode_Success)) { return null; } String
 * str1 = null; String.format(str1, (Object[]) null); str1 = isoMsg.getPAN();
 * str1 = isoMsg.getProcessingCode().toString(); str1 =
 * isoMsg.getTransactionAmount();// 4 str1 =
 * isoMsg.getTransmissionTime().toString();// 7 str1 =
 * isoMsg.getSTAN().toString();// 11 str1 =
 * isoMsg.getDateTimeLocal().toString();// 12 str1 =
 * isoMsg.getTransactionDate().toString();// 13 str1 =
 * isoMsg.getSettlementDate().toString();// 15 str1 =
 * isoMsg.getMerchantType().toString(); str1 =
 * isoMsg.getAuthorizingIdentificationResponseLength().toString();// 27 str1 =
 * isoMsg.getAcquiringInstitutionIdentificationCode();// 32 str1 =
 * isoMsg.getForwardInstitutionIdentificationCode();// 33 str1 =
 * isoMsg.getTrack2Data();// 35 str1 = isoMsg.getRRN(); str1 =
 * isoMsg.getCardAcceptorIdentificationCode(); str1 =
 * isoMsg.getCardAcceptorNameLocation();// 43 str1 =
 * isoMsg.getPrivateTransactionID(); str1 = isoMsg.getEncryptedPin();// 52
 */