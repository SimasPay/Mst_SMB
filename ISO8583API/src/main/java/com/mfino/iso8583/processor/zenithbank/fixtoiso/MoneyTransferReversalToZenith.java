package com.mfino.iso8583.processor.zenithbank.fixtoiso;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.mfino.util.DateTimeUtil;

public class MoneyTransferReversalToZenith extends ZenithBankRequest implements IFIXtoISOProcessor {

	public MoneyTransferReversalToZenith() throws IOException {
		isoMsg = (ZenithBankISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x202,CmFinoFIX.ISO8583_Variant_Zenith_Bank_Interface);
	}
	
	public static String padOnLeft(String str,char paddingChar,int finalLength)throws Exception {
		if(finalLength==str.length())
			return str;
		if(finalLength<str.length())
			throw new Exception("String length is already greater than the final length");
		String s = "";
		for(int i=0;i<finalLength-str.length();i++) 
			s = s +String.valueOf(paddingChar);
		str = s+str;
		return str;
	}
	
	public static String padOnRight(String str,char paddingChar,int finalLength) throws Exception{
		if(finalLength==str.length())
			return str;
		if(finalLength<str.length())
			throw new Exception("String length is already greater than the final length");
		String s = "";
		for(int i=0;i<finalLength-str.length();i++) 
			s = s+String.valueOf(paddingChar);
		str = str+s;
		return str;
	}
	
	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMMoneyTransferReversalToBank  msg = (CMMoneyTransferReversalToBank) fixmsg;
		
		isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Transfer_SavingsToSavings));
		//isoMsg.setTransactionAmount(msg.getAmount().toPlainString());	//4
		isoMsg.setTransactionAmount(msg.getAmount().toString());		//4
		//isoMsg.setSettlementAmount(msg.getAmount().toPlainString());	//5
		
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setTransmissionTime(ts);					//7
		isoMsg.setSTAN(msg.getTransactionID());			//11
		isoMsg.setExpirationDate(ts);					//14 
		isoMsg.setSettlementDate(ts); 					//15
		isoMsg.setPOSEntryMode(011); 					//22
		isoMsg.setCardSequenceNumber(001);				//23
		isoMsg.setPOSConditionCode(00);					//25
		isoMsg.setConversionDate(ts);					//16 FIXME
		isoMsg.setPOSConditionCode(00); 				//25
		isoMsg.setTransactionAmountFee("C00000000",1);		//28		//FIXME set transaction fee
		isoMsg.setTransactionProcessingFee("C00000000",1);	//30		//FIXME set transaction processing fee
//		isoMsg.setResponseCode(msg.getTransferInquiryResponseCode());	//39
		isoMsg.setCardAcceptorTerminalIdentification("40570005");	//41
//		isoMsg.setCardAcceptorNameLocation("SMS ZENITH"); //43
		isoMsg.setTransactionCurrencyCode(CmFinoFIX.ISO8583_CurrencyCode_NAIRA);		//49
//		isoMsg.setEncryptedPin(CryptographyService.tripleDESEncrypt(CryptographyService.getKeyToEncryptPin(), msg.getPin()));//52
		isoMsg.setAdditionalAmounts("2053566D000000050000");			//54 FIXME set additional amounts based transaction fees/costs
//		if(!StringUtils.isBlank(msg.getMessageReasonCode()))
//			isoMsg.setMessageReasonCode(msg.getMessageReasonCode());	//56
		isoMsg.setTEchoData(msg.getTransactionID().toString());			//59
		String originalMsg;
		String AcquiringIdCode = CmFinoFIX.ISO8583_AcquiringInstIdCode_mFino_to_Bank.toString();
		padOnLeft(AcquiringIdCode, '0',11);
		String ForwardingIdCode = CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_mFino_to_Bank.toString();
		padOnLeft(ForwardingIdCode, '0',11);
		originalMsg = padOnLeft(msg.getBankSystemTraceAuditNumber().toString(),'0',6) +
				msg.getTransferTime().toString() + AcquiringIdCode + ForwardingIdCode;		
		isoMsg.setReversalInfo(originalMsg);						//90
		isoMsg.setReplacementAmounts("000000000000000000000000C00000000C00000000");	//95 FIXME set amount,transaction amounts, settlement ,transaction fees
		isoMsg.setAccountIdentification1(msg.getSourceCardPAN());	//102
		
		return isoMsg;
	}
}
