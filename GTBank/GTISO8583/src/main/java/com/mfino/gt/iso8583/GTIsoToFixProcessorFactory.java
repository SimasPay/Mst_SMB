package com.mfino.gt.iso8583;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMEchoTestResponseToBank;
import com.mfino.fix.CmFinoFIX.CMEchoTestToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMSignOffResponseToBank;
import com.mfino.fix.CmFinoFIX.CMSignOffToBank;
import com.mfino.fix.CmFinoFIX.CMSignOnResponseToBank;
import com.mfino.fix.CmFinoFIX.CMSignOnToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.gt.iso8583.processor.isotofix.BalanceInquiryFromGTProcessor;
import com.mfino.gt.iso8583.processor.isotofix.BankTellerTransferFromGTProcessor;
import com.mfino.gt.iso8583.processor.isotofix.BankTellerTransferInquiryFromGTProcessor;
import com.mfino.gt.iso8583.processor.isotofix.GetLastTrxnsFromGTProcessor;
import com.mfino.gt.iso8583.processor.isotofix.MoneyTransferFromGTProcessor;
import com.mfino.gt.iso8583.processor.isotofix.MoneyTransferReversalFromGTProcessor;
import com.mfino.gt.iso8583.processor.isotofix.TransferInquiryFromGTProcessor;
import com.mfino.gt.iso8583.processor.isotofix.networkmanagement.EchoTest;
import com.mfino.gt.iso8583.processor.isotofix.networkmanagement.EchoTestResponse;
import com.mfino.gt.iso8583.processor.isotofix.networkmanagement.SignOff;
import com.mfino.gt.iso8583.processor.isotofix.networkmanagement.SignOffResponse;
import com.mfino.gt.iso8583.processor.isotofix.networkmanagement.SignOn;
import com.mfino.gt.iso8583.processor.isotofix.networkmanagement.SignOnResponse;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;

public class GTIsoToFixProcessorFactory implements IIsoToFixProcessorFactory {

	private static final GTIsoToFixProcessorFactory factory = new GTIsoToFixProcessorFactory();
	
	@Override
	public IIsoToFixProcessor getProcessor(CFIXMsg requestFixMsg) throws ProcessorNotAvailableException {

		IIsoToFixProcessor processor = null;
		
		if (requestFixMsg instanceof CMBalanceInquiryToBank)
			processor = new BalanceInquiryFromGTProcessor();
		else if(requestFixMsg instanceof CMMoneyTransferReversalToBank)
			processor = new MoneyTransferReversalFromGTProcessor();
		else if (requestFixMsg instanceof CMBankTellerTransferInquiryToBank)
			processor = new BankTellerTransferInquiryFromGTProcessor();
		else if (requestFixMsg instanceof CMBankTellerMoneyTransferToBank)
			processor = new BankTellerTransferFromGTProcessor();
		else if (requestFixMsg instanceof CMMoneyTransferToBank) 
			processor = new MoneyTransferFromGTProcessor();
		else if (requestFixMsg instanceof CMTransferInquiryToBank)
			processor = new TransferInquiryFromGTProcessor();
		else if (requestFixMsg instanceof CMGetLastTransactionsToBank)
			processor = new GetLastTrxnsFromGTProcessor();
		else if (requestFixMsg instanceof CMSignOnToBank)
			processor = new SignOn();
		else if (requestFixMsg instanceof CMSignOffToBank)
			processor = new SignOff();
		else if (requestFixMsg instanceof CMSignOnResponseToBank)
			processor = new SignOnResponse();
		else if (requestFixMsg instanceof CMSignOffResponseToBank)
			processor = new SignOffResponse();
		else if (requestFixMsg instanceof CMEchoTestToBank)
			processor = new EchoTest();
		else if (requestFixMsg instanceof CMEchoTestResponseToBank)
			processor = new EchoTestResponse();
		else throw new ProcessorNotAvailableException();

		return processor;
	}
	
	public static GTIsoToFixProcessorFactory getInstance(){
		return factory;
	}

}
