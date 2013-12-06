package com.mfino.iso8583.processor.bank.isotofix;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankAccountPinSetupToBank;
import com.mfino.iso8583.ISOtoFIXProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.ISinarmasISOtoFIXProcessor;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;

public class BankPinSetup implements ISinarmasISOtoFIXProcessor{

	@Override
    public CFIXMsg process(SinarmasISOMessage isoMsg, CFIXMsg request) throws Exception {

//		if(!(request instanceof CMBankAccountPinSetupToBank))
	//		throw new Exception("not an instance of CMBankAccountPinSetupToBank");
		
		CMBankAccountPinSetupToBank toBank = (CMBankAccountPinSetupToBank) request;
		
		return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);

	}

}
