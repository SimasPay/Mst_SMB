package com.mfino.iso8583.processor.bank.fixtoiso;

import java.io.IOException;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMChangeBankAccountPinToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;

public class ChangeBankAccountPin extends BankRequest implements IFIXtoISOProcessor {

	public ChangeBankAccountPin() throws IOException {
		isoMsg = (SinarmasISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200);
	}

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMChangeBankAccountPinToBank msg = (CMChangeBankAccountPinToBank) fixmsg;
		isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Pin_Setup));
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setSettlementDate(ts);
		isoMsg.setNetworkInternationalIdentifier(CmFinoFIX.ISO8583_NetworkInternationalIdentifier_Sinarmas_Pin_Change);
		isoMsg.setEncryptedPinBlocks(CryptographyService.buildEncryptedPINBlock32(null, null, null));
		return isoMsg;
	}

}
