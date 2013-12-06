package com.mfino.iso8583.processor.bank.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankAccountTopupReversalToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.ISinarmasISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;

public class BankTopupReversal implements ISinarmasISOtoFIXProcessor{

	@Override
    public CFIXMsg process(SinarmasISOMessage isoMsg, CFIXMsg request) throws Exception {

		CMBankAccountTopupReversalToBank toBank = (CMBankAccountTopupReversalToBank)request;
		return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
    }

}
