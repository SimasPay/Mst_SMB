package com.mfino.iso8583.processor.bank.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions1;

import java.io.IOException;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;

public class GetLastTrxns extends BankRequest implements IFIXtoISOProcessor {

	public GetLastTrxns() throws IOException {
		isoMsg = (SinarmasISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200);
	}

	public int	TPM_UseBankNewCodes	= 0;

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMGetLastTransactionsToBank msg = (CMGetLastTransactionsToBank) fixmsg;
		isoMsg.setPAN(msg.getSourceCardPAN());
		if (TPM_UseBankNewCodes != 0)
			isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions1));
		else
			isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions));
		Timestamp ts = DateTimeUtil.getLocalTime();
//		isoMsg.setSettlementDate(ts);
		isoMsg.setEncryptedPin(CryptographyService.buildEncryptedPINBlock16(null, null, null));
		return isoMsg;
	}
}
