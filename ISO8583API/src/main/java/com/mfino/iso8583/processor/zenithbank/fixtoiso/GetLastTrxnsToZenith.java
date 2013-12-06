package com.mfino.iso8583.processor.zenithbank.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions;

import java.io.IOException;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.mfino.util.DateTimeUtil;


public class GetLastTrxnsToZenith extends ZenithBankRequest implements IFIXtoISOProcessor {

	public GetLastTrxnsToZenith() throws IOException {
		isoMsg = (ZenithBankISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200);
	}
	
	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		
		CMGetLastTransactionsToBank msg = (CMGetLastTransactionsToBank) fixmsg;
		isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions));
		Timestamp ts = DateTimeUtil.getLocalTime();

		isoMsg.setTransactionAmount("000000000000");	//4
		isoMsg.setSettlementDate(ts);					//15
		isoMsg.setPOSEntryMode(012);					//22
		isoMsg.setPOSConditionCode(00);					//25
		isoMsg.setCardAcceptorTerminalIdentification("01234567");	//41
		isoMsg.setCardAcceptorNameLocation("SMS ZENITH");	//43
		isoMsg.setTransactionCurrencyCode(CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_USD);	//49
		isoMsg.setEncryptedPin(CryptographyService.buildEncryptedPINBlock16(null, null, null));
		isoMsg.setPOSDataCode("012345678912345");
		
		return isoMsg;
	}
}
