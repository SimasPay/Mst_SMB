package com.mfino.iso8583.processor.zenithbank.fixtoiso;

import java.io.IOException;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.mfino.util.DateTimeUtil;

public class TransferInquiryToZenith extends ZenithBankRequest implements IFIXtoISOProcessor{

	public TransferInquiryToZenith() throws IOException {
		isoMsg = (ZenithBankISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200,CmFinoFIX.ISO8583_Variant_Zenith_Bank_Interface);
	}

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMTransferInquiryToBank msg = (CMTransferInquiryToBank) fixmsg;
		//3
		if (msg.getSourceBankAccountType().equals(CmFinoFIX.BankAccountType_Saving) && 
				msg.getDestinationBankAccountType().equals(CmFinoFIX.BankAccountType_Saving))
			isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_TransferInquiry_SavingsToSavings));
		if (msg.getSourceBankAccountType().equals(CmFinoFIX.BankAccountType_Checking) && 
				msg.getDestinationBankAccountType().equals(CmFinoFIX.BankAccountType_Checking))
			isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_TransferInquiry_CheckToCheck));
		
		isoMsg.setTransactionAmount(msg.getAmount().toString());	//4
		Timestamp ts = DateTimeUtil.getLocalTime();					
		isoMsg.setSettlementDate(ts);					//15
		isoMsg.setPOSEntryMode(012);					//22
		isoMsg.setPOSConditionCode(00);					//25
		isoMsg.setCardAcceptorTerminalIdentification("01234567");	//41
		isoMsg.setCardAcceptorNameLocation("SMS ZENITH");	//43
		isoMsg.setTransactionCurrencyCode(CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_USD);	//49
		
		isoMsg.setEncryptedPin(CryptographyService.tripleDESEncrypt( null,msg.getPin()));//52
		isoMsg.setAccountIdentification1(msg.getSourceCardPAN());
		isoMsg.setAccountIdentification2(msg.getDestCardPAN());
		isoMsg.setPOSDataCode("012345678912345");
		return isoMsg;
	}
}
