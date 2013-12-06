package com.mfino.iso8583.processor.zenithbank.fixtoiso;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.mfino.util.DateTimeUtil;

public class MoneyTransferToZenith extends ZenithBankRequest implements IFIXtoISOProcessor {

	public MoneyTransferToZenith() throws IOException {
		isoMsg = (ZenithBankISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x202,CmFinoFIX.ISO8583_Variant_Zenith_Bank_Interface);
	}

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMMoneyTransferToBank msg = (CMMoneyTransferToBank) fixmsg;
//		
//		if (msg.getSourceBankAccountType().equals(CmFinoFIX.BankAccountType_Saving) && 
//				msg.getDestinationBankAccountType().equals(CmFinoFIX.BankAccountType_Saving))
//			isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Zenith_Transfer_SavingToSaving));
//		if (msg.getSourceBankAccountType().equals(CmFinoFIX.BankAccountType_Checking) && 
//				msg.getDestinationBankAccountType().equals(CmFinoFIX.BankAccountType_Checking))
//			isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Zenith_Transfer_CheckingToChecking));
		
		isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Transfer_SavingsToSavings));
		isoMsg.setTransactionAmount(msg.getAmount().toPlainString());//4
		isoMsg.setSettlementAmount(msg.getAmount().toPlainString());//5
		//FIXME 5,9
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setTransmissionTime(ts);//7
		isoMsg.setSTAN(msg.getTransactionID());//11
		isoMsg.setTransactionAmount(msg.getAmount().toString());
		isoMsg.setSettlementDate(ts); //15
		isoMsg.setPOSEntryMode(011); //22
		isoMsg.setConversionDate(ts);//16 FIXME
		isoMsg.setPOSConditionCode(00); //25
//		isoMsg.setResponseCode(msg.getTransferInquiryResponseCode());//39
//		isoMsg.setCardAcceptorTerminalIdentification("01234567"); //41
//		isoMsg.setCardAcceptorNameLocation("SMS ZENITH"); //43
//		isoMsg.setTransactionCurrencyCode(CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_USD); //49
//		isoMsg.setEncryptedPin(CryptographyService.tripleDESEncrypt(CryptographyService.getKeyToEncryptPin(), msg.getPin()));//52
//		if(!StringUtils.isBlank(msg.getMessageReasonCode()))
//			isoMsg.setMessageReasonCode(msg.getMessageReasonCode());//56
//		isoMsg.setAccountIdentification1(msg.getSourceCardPAN());
//		isoMsg.setAccountIdentification2(msg.getDestCardPAN());
//		isoMsg.setPOSDataCode("012345678912345");
		return isoMsg;
	}
}
