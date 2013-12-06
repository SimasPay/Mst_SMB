package com.mfino.iso8583.processor.bank.fixtoiso;

import java.io.IOException;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;

public class BalanceInquiry extends BankRequest implements IFIXtoISOProcessor {

	public BalanceInquiry() throws IOException {
		isoMsg = (SinarmasISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200);
	}

	public int	TPM_UseNewBankCodes	= 0;

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMBalanceInquiryToBank msg = (CMBalanceInquiryToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getLocalTime();
		if (TPM_UseNewBankCodes != 0)
			isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Balance_Inquiry1));//3
		else
			isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Balance_Inquiry));

		isoMsg.setTransactionAmount("000000000000");//4
		isoMsg.setSettlementDate(ts);//15
		isoMsg.setEncryptedPin(CryptographyService.buildEncryptedPINBlock16(msg.getPin(), msg.getSourceCardPAN(), null));//52

		return isoMsg;
	}

}
