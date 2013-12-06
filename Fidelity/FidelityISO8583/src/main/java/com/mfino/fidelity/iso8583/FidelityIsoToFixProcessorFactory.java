package com.mfino.fidelity.iso8583;

import com.mfino.fidelity.iso8583.processor.isotofix.BalanceInquiryFromFidelityProcessor;
import com.mfino.fidelity.iso8583.processor.isotofix.BankTellerTransferFromFidelityProcessor;
import com.mfino.fidelity.iso8583.processor.isotofix.BankTellerTransferInquiryFromFidelityProcessor;
import com.mfino.fidelity.iso8583.processor.isotofix.GetLastTrxnsFromFidelityProcessor;
import com.mfino.fidelity.iso8583.processor.isotofix.MoneyTransferFromFidelityProcessor;
import com.mfino.fidelity.iso8583.processor.isotofix.TransferInquiryFromFidelityProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;

public class FidelityIsoToFixProcessorFactory implements IIsoToFixProcessorFactory {

	private static final FidelityIsoToFixProcessorFactory factory = new FidelityIsoToFixProcessorFactory();
	
	@Override
	public IIsoToFixProcessor getProcessor(CFIXMsg requestFixMsg) throws ProcessorNotAvailableException {

		IIsoToFixProcessor processor = null;
		
		if (requestFixMsg instanceof CMBalanceInquiryToBank)
			processor = new BalanceInquiryFromFidelityProcessor();
		else if (requestFixMsg instanceof CMBankTellerTransferInquiryToBank)
			processor = new BankTellerTransferInquiryFromFidelityProcessor();
		else if (requestFixMsg instanceof CMBankTellerMoneyTransferToBank)
			processor = new BankTellerTransferFromFidelityProcessor();
		else if (requestFixMsg instanceof CMMoneyTransferToBank) 
			processor = new MoneyTransferFromFidelityProcessor();
		else if (requestFixMsg instanceof CMTransferInquiryToBank)
			processor = new TransferInquiryFromFidelityProcessor();
		else if (requestFixMsg instanceof CMGetLastTransactionsToBank)
			processor = new GetLastTrxnsFromFidelityProcessor();
		else if (requestFixMsg instanceof CMMoneyTransferReversalToBank)
			processor = new MoneyTransferFromFidelityProcessor();
		else throw new ProcessorNotAvailableException();

		return processor;
	}
	
	public static FidelityIsoToFixProcessorFactory getInstance(){
		return factory;
	}

}
