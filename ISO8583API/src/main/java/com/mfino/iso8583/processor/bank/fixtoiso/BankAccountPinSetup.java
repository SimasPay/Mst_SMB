package com.mfino.iso8583.processor.bank.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_NetworkInternationalIdentifier_Sinarmas_Pin_Setup;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Pin_Setup;

import java.io.IOException;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankAccountPinSetupToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;

public class BankAccountPinSetup extends BankRequest implements IFIXtoISOProcessor {

	public BankAccountPinSetup() throws IOException {
		isoMsg = (SinarmasISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200);
	}

	@Override
	public WrapperISOMessage process(CFIXMsg fmsg) throws Exception {
		super.process(fmsg);
		CMBankAccountPinSetupToBank msg = (CMBankAccountPinSetupToBank) fmsg;
		isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Pin_Setup));
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setSettlementDate(ts);
		isoMsg.setNetworkInternationalIdentifier(ISO8583_NetworkInternationalIdentifier_Sinarmas_Pin_Setup);
		isoMsg.setEncryptedPin(CryptographyService.buildEncryptedPINBlock16(null, null, null));
		return isoMsg;
	}

}
