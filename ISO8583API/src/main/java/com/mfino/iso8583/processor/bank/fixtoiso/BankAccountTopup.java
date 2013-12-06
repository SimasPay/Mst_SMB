package com.mfino.iso8583.processor.bank.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Topup_Other;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Topup_Other1;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Topup_Self;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Topup_Self1;

import java.io.IOException;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankAccountTopupToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;

public class BankAccountTopup extends BankRequest implements IFIXtoISOProcessor {

	public BankAccountTopup() throws IOException {
		isoMsg = (SinarmasISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200);
	}

	public int	TPM_UseNewBankCodes	= 0;

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMBankAccountTopupToBank msg = (CMBankAccountTopupToBank) fixmsg;
		if (msg.getSourceMDN().equals(msg.getDestMDN())) {
			if (TPM_UseNewBankCodes != 0)
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Topup_Self1));
			else
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Topup_Self));
		}
		else {
			if (TPM_UseNewBankCodes != 0)
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Topup_Other1));
			else
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Topup_Other));
		}
		isoMsg.setTransactionAmount(msg.getAmount().toString());
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setLocalTransactionTime(msg.getTransferTime());
		isoMsg.setTransmissionTime(msg.getTransferTime());
		isoMsg.setLocalTransactionDate(msg.getTransferTime());
		isoMsg.setSettlementDate(ts);
		isoMsg.setEncryptedPin(CryptographyService.buildEncryptedPINBlock16(null, null, null));//52
		return isoMsg;
	}

}
