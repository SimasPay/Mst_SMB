package com.mfino.zenith.iso8583;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVTransferInquiryToBank;
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
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;
import com.mfino.zenith.iso8583.processor.isotofix.BalanceInquiryFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.BankTellerTransferFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.BankTellerTransferInquiryFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.DSTVPaymentFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.DSTVTransferInquiryFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.GetLastTrxnsFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.MoneyTransferFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.MoneyTransferReversalFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.TransferInquiryFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.VisafoneAirtimePurchaseFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.VisafoneAirtimeTransferInquiryFromZenithProcessor;
import com.mfino.zenith.iso8583.processor.isotofix.networkmanagement.EchoTest;
import com.mfino.zenith.iso8583.processor.isotofix.networkmanagement.EchoTestResponse;
import com.mfino.zenith.iso8583.processor.isotofix.networkmanagement.SignOff;
import com.mfino.zenith.iso8583.processor.isotofix.networkmanagement.SignOffResponse;
import com.mfino.zenith.iso8583.processor.isotofix.networkmanagement.SignOn;
import com.mfino.zenith.iso8583.processor.isotofix.networkmanagement.SignOnResponse;

public class ZenithIsoToFixProcessorFactory implements IIsoToFixProcessorFactory {

	private static final ZenithIsoToFixProcessorFactory factory = new ZenithIsoToFixProcessorFactory();
	
	@Override
	public IIsoToFixProcessor getProcessor(CFIXMsg requestFixMsg) throws ProcessorNotAvailableException {

		IIsoToFixProcessor processor = null;
		
		if (requestFixMsg instanceof CMBalanceInquiryToBank)
			processor = new BalanceInquiryFromZenithProcessor();
		else if (requestFixMsg instanceof CMDSTVTransferInquiryToBank)
			processor = new DSTVTransferInquiryFromZenithProcessor();
		else if (requestFixMsg instanceof CMDSTVMoneyTransferToBank)
			processor = new DSTVPaymentFromZenithProcessor();
		else if (requestFixMsg instanceof CMVisafoneAirtimeTransferInquiryToBank)
			processor = new VisafoneAirtimeTransferInquiryFromZenithProcessor();
		else if (requestFixMsg instanceof CMVisafoneAirtimeMoneyTransferToBank)
			processor = new VisafoneAirtimePurchaseFromZenithProcessor();
		else if (requestFixMsg instanceof CMBankTellerTransferInquiryToBank)
			processor = new BankTellerTransferInquiryFromZenithProcessor();
		else if (requestFixMsg instanceof CMBankTellerMoneyTransferToBank)
			processor = new BankTellerTransferFromZenithProcessor();
		else if(requestFixMsg instanceof CMMoneyTransferReversalToBank)
			processor = new MoneyTransferReversalFromZenithProcessor();
		else if (requestFixMsg instanceof CMMoneyTransferToBank) 
			processor = new MoneyTransferFromZenithProcessor();
		else if (requestFixMsg instanceof CMTransferInquiryToBank)
			processor = new TransferInquiryFromZenithProcessor();
		else if (requestFixMsg instanceof CMGetLastTransactionsToBank)
			processor = new GetLastTrxnsFromZenithProcessor();
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
	
	public static ZenithIsoToFixProcessorFactory getInstance(){
		return factory;
	}

}
