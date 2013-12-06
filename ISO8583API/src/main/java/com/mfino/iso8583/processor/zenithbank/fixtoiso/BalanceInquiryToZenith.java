package com.mfino.iso8583.processor.zenithbank.fixtoiso;

import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.mfino.util.DateTimeUtil;

public class BalanceInquiryToZenith extends ZenithBankRequest implements IFIXtoISOProcessor {

	public BalanceInquiryToZenith() throws IOException {
		isoMsg = (ZenithBankISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200);
		element127Msg = (ZenithBankISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0);
	}
	
	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMBalanceInquiryToBank msg = (CMBalanceInquiryToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getLocalTime();
		
		
		/**
		 * ProcessingCode can have many other possible values.
		 * Will have to think something about how to construct the processingcodes.
		 */
		if(msg.getSourceBankAccountType().equals(""+CmFinoFIX.BankAccountType_Saving))
			isoMsg.setProcessingCode(Integer.parseInt(""+CmFinoFIX.ISO8583_ProcessingCode_BalanceInquiry_Savings));	//3
		else if(msg.getSourceBankAccountType().equals(""+CmFinoFIX.BankAccountType_Checking))
			isoMsg.setProcessingCode(Integer.parseInt(""+CmFinoFIX.ISO8583_ProcessingCode_BalanceInquiry_Check)); //3
		
		isoMsg.setTransactionAmount("000000000000");	//4
		isoMsg.setExpirationDate(ts);					//14 
		isoMsg.setSettlementDate(ts);					//15
		isoMsg.setPOSEntryMode(011);					//22
		isoMsg.setCardSequenceNumber(001);				//23
		isoMsg.setPOSConditionCode(00);					//25
		isoMsg.setPOSPINCaptureCode(04);				//26
		isoMsg.setTransactionAmountFee("C00000000",1);		//28	//FIXME set transaction fee
		isoMsg.setTransactionProcessingFee("C00000000",1);	//30	//FIXME set transaction processing fee
		isoMsg.setCardAcceptorTerminalIdentification("40570005");	//41
		isoMsg.setCardAcceptorNameLocation("ZMOBILE                             LANG");	//43
		isoMsg.setTransactionCurrencyCode(CmFinoFIX.ISO8583_CurrencyCode_NAIRA);	//49
		//FIXME visit this once again
//		isoMsg.setEncryptedPin(CryptographyService.tripleDESEncrypt( CryptographyService.getKeyToEncryptPin(),msg.getPin()));//52
		isoMsg.setEncryptedPin("1029384756102938");	//52
		isoMsg.setMessageReasonCode("1510");		//56
		isoMsg.setTEchoData(msg.getTransactionID().toString());		//59
		isoMsg.setAccountIdentification1(msg.getSourceCardPAN());	//102
		//isoMsg.setPOSDataCode("012345678912345");//123
		
		/*
		 * Setting 127 element with message type 0, Un comment this to set 127
		 */
		/*
		String authorizerDate = ts.toString();
		element127Msg.set127Element_3("ZMobileSrc  ZMobileMeSnk470310000820VisaTG      ");
		element127Msg.set127Element_20(authorizerDate);
		isoMsg.set127ElementData(element127Msg.toString());
		*/
		return isoMsg;
	}

	
}