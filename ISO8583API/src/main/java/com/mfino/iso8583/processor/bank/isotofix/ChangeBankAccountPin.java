package com.mfino.iso8583.processor.bank.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMChangeBankAccountPinToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.ISinarmasISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;

public class ChangeBankAccountPin implements ISinarmasISOtoFIXProcessor {

	@Override
	public CFIXMsg process(SinarmasISOMessage isoMsg, CFIXMsg request) throws Exception {

		CMChangeBankAccountPinToBank toBank = (CMChangeBankAccountPinToBank) request;

		return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
	}

}
